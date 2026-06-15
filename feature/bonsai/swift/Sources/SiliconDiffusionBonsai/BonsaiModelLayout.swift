import Foundation
import ZIPFoundation

struct BonsaiModelLayout {
    private static let textEncoderDirectoryName = "text_encoder-mlx-4bit"

    let rootURL: URL
    let packedTransformerURL: URL
    let textEncoderURL: URL
    let tokenizerURL: URL
    let vaeURL: URL
    let schedulerURL: URL

    static func resolve(modelPath: String) throws -> BonsaiModelLayout {
        let modelURL = URL(fileURLWithPath: modelPath, isDirectory: true)
        if let layout = find(in: modelURL) {
            return layout
        }

        let archiveURL = modelURL.appendingPathComponent("model.zip", isDirectory: false)
        guard FileManager.default.fileExists(atPath: archiveURL.path) else {
            throw BonsaiRuntimeError.modelResourcesNotFound(modelPath)
        }

        let extractedURL = modelURL.appendingPathComponent("extracted", isDirectory: true)
        if find(in: extractedURL) == nil {
            if FileManager.default.fileExists(atPath: extractedURL.path) {
                try FileManager.default.removeItem(at: extractedURL)
            }
            try FileManager.default.createDirectory(
                at: extractedURL,
                withIntermediateDirectories: true
            )
            do {
                try FileManager.default.unzipItem(at: archiveURL, to: extractedURL)
            } catch {
                throw BonsaiRuntimeError.invalidModelArchive
            }
        }

        if let layout = find(in: extractedURL) {
            return layout
        }

        throw BonsaiRuntimeError.invalidModelLayout(
            "expected transformer-packed-mflux, text_encoder or text_encoder-mlx-4bit, tokenizer, vae, and scheduler directories"
        )
    }

    private static func find(in rootURL: URL) -> BonsaiModelLayout? {
        let candidates = [
            rootURL,
            rootURL.appendingPathComponent("Resources", isDirectory: true),
            rootURL.appendingPathComponent("extracted", isDirectory: true),
            rootURL
                .appendingPathComponent("extracted", isDirectory: true)
                .appendingPathComponent("Resources", isDirectory: true),
        ]

        if let direct = candidates.first(where: isBonsaiRoot) {
            return layout(rootURL: direct)
        }

        guard let enumerator = FileManager.default.enumerator(
            at: rootURL,
            includingPropertiesForKeys: [.isDirectoryKey],
            options: [.skipsHiddenFiles]
        ) else {
            return nil
        }

        let rootDepth = rootURL.pathComponents.count
        for case let url as URL in enumerator {
            if url.pathComponents.count - rootDepth > 4 {
                enumerator.skipDescendants()
                continue
            }
            if isBonsaiRoot(url), let layout = layout(rootURL: url) {
                return layout
            }
        }
        return nil
    }

    private static func layout(rootURL: URL) -> BonsaiModelLayout? {
        guard isBonsaiRoot(rootURL),
              let textEncoderURL = firstDirectory(
                names: [textEncoderDirectoryName, "text_encoder"],
                in: rootURL
              )
        else {
            return nil
        }
        return BonsaiModelLayout(
            rootURL: rootURL,
            packedTransformerURL: rootURL.appendingPathComponent("transformer-packed-mflux", isDirectory: true),
            textEncoderURL: textEncoderURL,
            tokenizerURL: rootURL.appendingPathComponent("tokenizer", isDirectory: true),
            vaeURL: rootURL.appendingPathComponent("vae", isDirectory: true),
            schedulerURL: rootURL.appendingPathComponent("scheduler", isDirectory: true)
        )
    }

    private static func isBonsaiRoot(_ url: URL) -> Bool {
        let transformer = url
            .appendingPathComponent("transformer-packed-mflux", isDirectory: true)
            .appendingPathComponent("quantization_config.json", isDirectory: false)
        let requiredDirectories = [
            url.appendingPathComponent("tokenizer", isDirectory: true),
            url.appendingPathComponent("vae", isDirectory: true),
            url.appendingPathComponent("scheduler", isDirectory: true),
        ]
        return FileManager.default.fileExists(atPath: transformer.path)
            && firstDirectory(names: [textEncoderDirectoryName, "text_encoder"], in: url) != nil
            && requiredDirectories.allSatisfy { candidate in
                var isDirectory: ObjCBool = false
                return FileManager.default.fileExists(atPath: candidate.path, isDirectory: &isDirectory)
                    && isDirectory.boolValue
            }
    }

    private static func firstDirectory(names: [String], in rootURL: URL) -> URL? {
        names
            .map { rootURL.appendingPathComponent($0, isDirectory: true) }
            .first { candidate in
                var isDirectory: ObjCBool = false
                return FileManager.default.fileExists(atPath: candidate.path, isDirectory: &isDirectory)
                    && isDirectory.boolValue
            }
    }
}
