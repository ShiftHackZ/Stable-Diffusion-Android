import Foundation
import MLX
import MLXRandom
#if canImport(UIKit)
import UIKit
#elseif canImport(AppKit)
import AppKit
#endif

struct BonsaiGenerationInput {
    let layout: BonsaiModelLayout
    let prompt: String
    let negativePrompt: String
    let steps: Int
    let guidance: Float
    let width: Int
    let height: Int
    let seed: Int
}

struct BonsaiLatents {
    let values: MLXArray
    let ids: MLXArray
    let latentHeight: Int
    let latentWidth: Int
}

enum BonsaiSeed {
    static func parse(_ seed: String) throws -> Int {
        let trimmed = seed.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmed.isEmpty {
            return Int.random(in: 0...Int(Int32.max))
        }
        guard let value = Int(trimmed) else {
            throw BonsaiRuntimeError.invalidSeed(seed)
        }
        return value
    }
}

enum BonsaiLatentCreator {
    static func preparePackedLatents(
        seed: Int,
        height: Int,
        width: Int,
        batchSize: Int = 1,
        latentChannels: Int = 32,
        vaeScaleFactor: Int = 8
    ) -> BonsaiLatents {
        let normalizedHeight = 2 * (height / (vaeScaleFactor * 2))
        let normalizedWidth = 2 * (width / (vaeScaleFactor * 2))
        let latentHeight = normalizedHeight / 2
        let latentWidth = normalizedWidth / 2
        let key = MLXRandom.key(UInt64(bitPattern: Int64(seed)))
        let raw = MLXRandom
            .normal(
                [batchSize, latentChannels * 4, latentHeight, latentWidth],
                dtype: .bfloat16,
                key: key
            )
        let packed = packLatents(raw)
        return BonsaiLatents(
            values: packed,
            ids: gridIds(batchSize: batchSize, height: latentHeight, width: latentWidth),
            latentHeight: latentHeight,
            latentWidth: latentWidth
        )
    }

    static func packLatents(_ latents: MLXArray) -> MLXArray {
        let shape = latents.shape
        return latents
            .reshaped(shape[0], shape[1], shape[2] * shape[3])
            .transposed(0, 2, 1)
    }

    static func unpackPackedLatents(
        _ latents: MLXArray,
        imageHeight: Int,
        imageWidth: Int,
        vaeScaleFactor: Int = 8
    ) throws -> MLXArray {
        guard latents.ndim == 3 else {
            return latents
        }
        let batchSize = latents.shape[0]
        let seqLen = latents.shape[1]
        let channels = latents.shape[2]
        let latentHeight = imageHeight / (vaeScaleFactor * 2)
        let latentWidth = imageWidth / (vaeScaleFactor * 2)
        guard seqLen == latentHeight * latentWidth else {
            throw BonsaiRuntimeError.invalidModelLayout(
                "packed latent seq_len \(seqLen) does not match \(latentHeight)x\(latentWidth)"
            )
        }
        return latents
            .reshaped(batchSize, latentHeight, latentWidth, channels)
            .transposed(0, 3, 1, 2)
    }

    private static func gridIds(batchSize: Int, height: Int, width: Int) -> MLXArray {
        var values = [Int32]()
        values.reserveCapacity(batchSize * height * width * 4)
        for _ in 0..<batchSize {
            for h in 0..<height {
                for w in 0..<width {
                    values.append(0)
                    values.append(Int32(h))
                    values.append(Int32(w))
                    values.append(0)
                }
            }
        }
        return MLXArray(values, [batchSize, height * width, 4])
    }
}

struct BonsaiFlowMatchEulerScheduler {
    let timesteps: [Float]
    let sigmas: [Float]

    init(imageSeqLen: Int, steps: Int) {
        let stepCount = max(1, steps)
        let mu = Self.empiricalMu(imageSeqLen: imageSeqLen, steps: stepCount)
        var sigmaValues = [Float]()
        sigmaValues.reserveCapacity(stepCount + 1)
        for index in 0..<stepCount {
            let linear = 1.0 - Float(index) * (1.0 - 1.0 / Float(stepCount)) / Float(max(1, stepCount - 1))
            sigmaValues.append(Self.timeShift(mu: mu, sigmaPower: 1.0, t: linear))
        }
        timesteps = sigmaValues.map { $0 * 1000.0 }
        sigmas = sigmaValues + [0.0]
    }

    func step(noise: MLXArray, timestep: Int, latents: MLXArray) -> MLXArray {
        let dt = MLXArray(sigmas[timestep + 1] - sigmas[timestep], dtype: latents.dtype)
        return latents + dt * noise.asType(latents.dtype)
    }

    private static func empiricalMu(imageSeqLen: Int, steps: Int) -> Float {
        let a1: Float = 8.73809524e-05
        let b1: Float = 1.89833333
        let a2: Float = 0.00016927
        let b2: Float = 0.45666666
        let seqLen = Float(imageSeqLen)
        if imageSeqLen > 4300 {
            return a2 * seqLen + b2
        }
        let m200 = a2 * seqLen + b2
        let m10 = a1 * seqLen + b1
        let a = (m200 - m10) / 190.0
        let b = m200 - 200.0 * a
        return a * Float(steps) + b
    }

    private static func timeShift(mu: Float, sigmaPower: Float, t: Float) -> Float {
        let numerator = exp(mu)
        return numerator / (numerator + pow(1.0 / t - 1.0, sigmaPower))
    }
}

enum BonsaiImageEncoder {
    static func base64Jpeg(from decoded: MLXArray, compressionQuality: CGFloat = 0.92) throws -> String {
        let normalized = clip(decoded / 2.0 + 0.5, min: 0.0, max: 1.0)
        let nhwc: MLXArray
        if normalized.ndim == 4, normalized.shape[1] == 3 {
            nhwc = normalized.transposed(0, 2, 3, 1).asType(.float32)
        } else {
            nhwc = normalized.asType(.float32)
        }

        let shape = nhwc.shape
        guard shape.count == 4, shape[0] > 0, shape[3] >= 3 else {
            throw BonsaiRuntimeError.imageEncodingFailed
        }

        let height = shape[1]
        let width = shape[2]
        let channels = shape[3]
        let floats = nhwc.asArray(Float.self)
        var pixels = [UInt8](repeating: 0, count: width * height * 4)
        var stats = BonsaiOutputImageStats()
        for y in 0..<height {
            for x in 0..<width {
                let src = ((y * width) + x) * channels
                let dst = ((y * width) + x) * 4
                let red = floats[src]
                let green = floats[src + 1]
                let blue = floats[src + 2]
                stats.record(red)
                stats.record(green)
                stats.record(blue)
                pixels[dst] = Self.toByte(red)
                pixels[dst + 1] = Self.toByte(green)
                pixels[dst + 2] = Self.toByte(blue)
                pixels[dst + 3] = 255
            }
        }
        print("[Bonsai] output \(width)x\(height) \(stats.summary)")
        try stats.validate()

        guard let provider = CGDataProvider(data: Data(pixels) as CFData),
              let cgImage = CGImage(
                width: width,
                height: height,
                bitsPerComponent: 8,
                bitsPerPixel: 32,
                bytesPerRow: width * 4,
                space: CGColorSpaceCreateDeviceRGB(),
                bitmapInfo: [
                    CGBitmapInfo.byteOrder32Big,
                    CGBitmapInfo(rawValue: CGImageAlphaInfo.noneSkipLast.rawValue),
                ],
                provider: provider,
                decode: nil,
                shouldInterpolate: true,
                intent: .defaultIntent
              )
        else {
            throw BonsaiRuntimeError.imageEncodingFailed
        }

        let data: Data?
#if canImport(UIKit)
        data = UIImage(cgImage: cgImage).jpegData(compressionQuality: compressionQuality)
#elseif canImport(AppKit)
        let representation = NSBitmapImageRep(cgImage: cgImage)
        data = representation.representation(
            using: .jpeg,
            properties: [.compressionFactor: compressionQuality]
        )
#else
        data = nil
#endif

        guard let data else {
            throw BonsaiRuntimeError.imageEncodingFailed
        }
        return data.base64EncodedString()
    }

    private static func toByte(_ value: Float) -> UInt8 {
        guard value.isFinite else {
            return 0
        }
        return UInt8(clamping: Int((value * 255.0).rounded()))
    }
}

private struct BonsaiOutputImageStats {
    private(set) var minValue = Float.greatestFiniteMagnitude
    private(set) var maxValue = -Float.greatestFiniteMagnitude
    private(set) var sum: Double = 0
    private(set) var finiteCount = 0
    private(set) var nonFiniteCount = 0

    mutating func record(_ value: Float) {
        guard value.isFinite else {
            nonFiniteCount += 1
            return
        }
        minValue = min(minValue, value)
        maxValue = max(maxValue, value)
        sum += Double(value)
        finiteCount += 1
    }

    var mean: Double {
        guard finiteCount > 0 else {
            return 0
        }
        return sum / Double(finiteCount)
    }

    var summary: String {
        String(
            format: "min=%.5f max=%.5f mean=%.5f finite=%d nonFinite=%d",
            minValue,
            maxValue,
            mean,
            finiteCount,
            nonFiniteCount
        )
    }

    func validate() throws {
        guard finiteCount > 0 else {
            throw BonsaiRuntimeError.invalidOutputImage("all RGB values are non-finite")
        }
        guard nonFiniteCount == 0 else {
            throw BonsaiRuntimeError.invalidOutputImage(summary)
        }
        if mean <= 0.02 || (maxValue <= 0.08 && mean <= 0.03) {
            throw BonsaiRuntimeError.invalidOutputImage("nearly black output, \(summary)")
        }
    }
}
