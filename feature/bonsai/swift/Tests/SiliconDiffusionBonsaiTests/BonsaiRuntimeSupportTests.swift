import Foundation
@testable import SiliconDiffusionBonsai
import XCTest
import ZIPFoundation

final class BonsaiModelLayoutTests: XCTestCase {
    func testResolveFindsDirectHuggingFaceLayout() throws {
        let tempURL = try BonsaiTestFiles.makeTempDirectory()
        defer { try? FileManager.default.removeItem(at: tempURL) }
        let modelURL = try BonsaiTestFiles.makeBonsaiRoot(in: tempURL, named: "bonsai")

        let layout = try BonsaiModelLayout.resolve(modelPath: modelURL.path)

        XCTAssertEqual(layout.rootURL.standardizedFileURL.path, modelURL.standardizedFileURL.path)
        XCTAssertEqual(layout.packedTransformerURL.lastPathComponent, "transformer-packed-mflux")
        XCTAssertEqual(layout.textEncoderURL.lastPathComponent, "text_encoder-mlx-4bit")
        XCTAssertEqual(layout.tokenizerURL.lastPathComponent, "tokenizer")
        XCTAssertEqual(layout.vaeURL.lastPathComponent, "vae")
        XCTAssertEqual(layout.schedulerURL.lastPathComponent, "scheduler")
    }

    func testResolveFindsNestedResourcesLayout() throws {
        let tempURL = try BonsaiTestFiles.makeTempDirectory()
        defer { try? FileManager.default.removeItem(at: tempURL) }
        let wrapperURL = tempURL.appendingPathComponent("wrapper", isDirectory: true)
        let resourcesURL = try BonsaiTestFiles.makeBonsaiRoot(in: wrapperURL, named: "Resources")

        let layout = try BonsaiModelLayout.resolve(modelPath: wrapperURL.path)

        XCTAssertEqual(layout.rootURL.standardizedFileURL.path, resourcesURL.standardizedFileURL.path)
    }

    func testResolveAcceptsLegacyTextEncoderDirectory() throws {
        let tempURL = try BonsaiTestFiles.makeTempDirectory()
        defer { try? FileManager.default.removeItem(at: tempURL) }
        let modelURL = try BonsaiTestFiles.makeBonsaiRoot(
            in: tempURL,
            named: "bonsai",
            textEncoderDirectoryName: "text_encoder"
        )

        let layout = try BonsaiModelLayout.resolve(modelPath: modelURL.path)

        XCTAssertEqual(layout.textEncoderURL.lastPathComponent, "text_encoder")
    }

    func testResolvePrefersMlxTextEncoderWhenBothDirectoriesExist() throws {
        let tempURL = try BonsaiTestFiles.makeTempDirectory()
        defer { try? FileManager.default.removeItem(at: tempURL) }
        let modelURL = try BonsaiTestFiles.makeBonsaiRoot(in: tempURL, named: "bonsai")
        try FileManager.default.createDirectory(
            at: modelURL.appendingPathComponent("text_encoder", isDirectory: true),
            withIntermediateDirectories: true
        )

        let layout = try BonsaiModelLayout.resolve(modelPath: modelURL.path)

        XCTAssertEqual(layout.textEncoderURL.lastPathComponent, "text_encoder-mlx-4bit")
    }

    func testResolveExtractsModelZip() throws {
        let tempURL = try BonsaiTestFiles.makeTempDirectory()
        defer { try? FileManager.default.removeItem(at: tempURL) }
        let sourceURL = try BonsaiTestFiles.makeBonsaiRoot(in: tempURL, named: "zipped-bonsai")
        let containerURL = tempURL.appendingPathComponent("container", isDirectory: true)
        try FileManager.default.createDirectory(at: containerURL, withIntermediateDirectories: true)
        try FileManager.default.zipItem(
            at: sourceURL,
            to: containerURL.appendingPathComponent("model.zip")
        )
        try FileManager.default.removeItem(at: sourceURL)

        let layout = try BonsaiModelLayout.resolve(modelPath: containerURL.path)

        XCTAssertEqual(layout.rootURL.lastPathComponent, "zipped-bonsai")
        XCTAssertTrue(
            FileManager.default.fileExists(
                atPath: containerURL.appendingPathComponent("extracted", isDirectory: true).path
            )
        )
    }

    func testResolveReportsMissingResourcesWhenNoLayoutOrArchiveExists() throws {
        let tempURL = try BonsaiTestFiles.makeTempDirectory()
        defer { try? FileManager.default.removeItem(at: tempURL) }
        let emptyURL = tempURL.appendingPathComponent("empty", isDirectory: true)
        try FileManager.default.createDirectory(at: emptyURL, withIntermediateDirectories: true)

        XCTAssertThrowsError(try BonsaiModelLayout.resolve(modelPath: emptyURL.path)) { error in
            guard case BonsaiRuntimeError.modelResourcesNotFound(let path) = error else {
                return XCTFail("Expected modelResourcesNotFound, got \(error)")
            }
            XCTAssertEqual(path, emptyURL.path)
        }
    }

    func testResolveReportsInvalidLayoutAfterArchiveExtraction() throws {
        let tempURL = try BonsaiTestFiles.makeTempDirectory()
        defer { try? FileManager.default.removeItem(at: tempURL) }
        let invalidSourceURL = tempURL.appendingPathComponent("not-bonsai", isDirectory: true)
        try FileManager.default.createDirectory(at: invalidSourceURL, withIntermediateDirectories: true)
        try Data("{}".utf8).write(to: invalidSourceURL.appendingPathComponent("config.json"))
        let containerURL = tempURL.appendingPathComponent("container", isDirectory: true)
        try FileManager.default.createDirectory(at: containerURL, withIntermediateDirectories: true)
        try FileManager.default.zipItem(
            at: invalidSourceURL,
            to: containerURL.appendingPathComponent("model.zip")
        )
        try FileManager.default.removeItem(at: invalidSourceURL)

        XCTAssertThrowsError(try BonsaiModelLayout.resolve(modelPath: containerURL.path)) { error in
            guard case BonsaiRuntimeError.invalidModelLayout(let reason) = error else {
                return XCTFail("Expected invalidModelLayout, got \(error)")
            }
            XCTAssertTrue(reason.contains("transformer-packed-mflux"))
        }
    }
}

final class BonsaiTextEncoderTests: XCTestCase {
    func testChatFormattedPromptMatchesQwenTemplate() {
        XCTAssertEqual(
            BonsaiTextEncoder.chatFormattedPrompt("Cat"),
            "<|im_start|>user\nCat<|im_end|>\n<|im_start|>assistant\n<think>\n\n</think>\n\n"
        )
    }
}

final class BonsaiSeedTests: XCTestCase {
    func testParseUsesTrimmedNumericSeed() throws {
        XCTAssertEqual(try BonsaiSeed.parse(" 12345\n"), 12345)
    }

    func testParseGeneratesBoundedRandomSeedWhenInputIsBlank() throws {
        let seed = try BonsaiSeed.parse(" \n\t ")

        XCTAssertGreaterThanOrEqual(seed, 0)
        XCTAssertLessThanOrEqual(seed, Int(Int32.max))
    }

    func testParseReportsInvalidSeed() {
        XCTAssertThrowsError(try BonsaiSeed.parse("abc")) { error in
            guard case BonsaiRuntimeError.invalidSeed(let seed) = error else {
                return XCTFail("Expected invalidSeed, got \(error)")
            }
            XCTAssertEqual(seed, "abc")
        }
    }
}

@available(iOS 17.0, macOS 14.0, *)
final class BonsaiRequestValidatorTests: XCTestCase {
    func testValidateAcceptsMinimalValidRequest() throws {
        let tempURL = try BonsaiTestFiles.makeTempDirectory()
        defer { try? FileManager.default.removeItem(at: tempURL) }

        XCTAssertNoThrow(
            try BonsaiRequestValidator.validate(
                request: Self.makeRequest(modelPath: tempURL.path)
            )
        )
    }

    func testValidateRejectsBlankPrompt() throws {
        let tempURL = try BonsaiTestFiles.makeTempDirectory()
        defer { try? FileManager.default.removeItem(at: tempURL) }

        XCTAssertThrowsError(
            try BonsaiRequestValidator.validate(
                request: Self.makeRequest(modelPath: tempURL.path, prompt: " \n ")
            )
        ) { error in
            guard case BonsaiRuntimeError.emptyPrompt = error else {
                return XCTFail("Expected emptyPrompt, got \(error)")
            }
        }
    }

    func testValidateRejectsSizeNotDivisibleByThirtyTwo() throws {
        let tempURL = try BonsaiTestFiles.makeTempDirectory()
        defer { try? FileManager.default.removeItem(at: tempURL) }

        XCTAssertThrowsError(
            try BonsaiRequestValidator.validate(
                request: Self.makeRequest(modelPath: tempURL.path, width: 130)
            )
        ) { error in
            guard case BonsaiRuntimeError.invalidSize = error else {
                return XCTFail("Expected invalidSize, got \(error)")
            }
        }
    }

    func testValidateRejectsMissingModelPath() {
        let missingPath = FileManager.default.temporaryDirectory
            .appendingPathComponent("missing-bonsai-\(UUID().uuidString)", isDirectory: true)
            .path

        XCTAssertThrowsError(
            try BonsaiRequestValidator.validate(
                request: Self.makeRequest(modelPath: missingPath)
            )
        ) { error in
            guard case BonsaiRuntimeError.modelResourcesNotFound(let path) = error else {
                return XCTFail("Expected modelResourcesNotFound, got \(error)")
            }
            XCTAssertEqual(path, missingPath)
        }
    }

    private static func makeRequest(
        modelPath: String,
        prompt: String = "a bonsai tree",
        width: Int32 = 128,
        height: Int32 = 128
    ) -> SiliconDiffusionBonsaiGenerator.Request {
        SiliconDiffusionBonsaiGenerator.Request(
            modelPath: modelPath,
            prompt: prompt,
            negativePrompt: "",
            samplingSteps: 4,
            cfgScale: 1.0,
            width: width,
            height: height,
            seed: "1",
            allowNsfw: false
        )
    }
}

private enum BonsaiTestFiles {
    static func makeTempDirectory() throws -> URL {
        let url = FileManager.default.temporaryDirectory
            .appendingPathComponent("bonsai-runtime-tests-\(UUID().uuidString)", isDirectory: true)
        try FileManager.default.createDirectory(at: url, withIntermediateDirectories: true)
        return url
    }

    static func makeBonsaiRoot(
        in parentURL: URL,
        named name: String,
        textEncoderDirectoryName: String = "text_encoder-mlx-4bit"
    ) throws -> URL {
        let rootURL = parentURL.appendingPathComponent(name, isDirectory: true)
        try FileManager.default.createDirectory(at: rootURL, withIntermediateDirectories: true)
        let transformerURL = rootURL.appendingPathComponent("transformer-packed-mflux", isDirectory: true)
        try FileManager.default.createDirectory(at: transformerURL, withIntermediateDirectories: true)
        try Data(#"{"bits":2,"group_size":128}"#.utf8)
            .write(to: transformerURL.appendingPathComponent("quantization_config.json"))
        for directory in [textEncoderDirectoryName, "tokenizer", "vae", "scheduler"] {
            try FileManager.default.createDirectory(
                at: rootURL.appendingPathComponent(directory, isDirectory: true),
                withIntermediateDirectories: true
            )
        }
        return rootURL
    }
}
