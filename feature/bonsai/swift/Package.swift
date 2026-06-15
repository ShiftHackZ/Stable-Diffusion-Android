// swift-tools-version: 6.1

import PackageDescription

let package = Package(
    name: "SiliconDiffusionBonsai",
    platforms: [
        .macOS(.v14),
        .iOS(.v17),
    ],
    products: [
        .library(
            name: "SiliconDiffusionBonsai",
            targets: ["SiliconDiffusionBonsai"]
        ),
    ],
    dependencies: [
        .package(
            url: "https://github.com/PrismML-Eng/mlx-swift.git",
            revision: "e40e0a57a6f7ad08dc3fd87ad598a7aa6407d230"
        ),
        .package(
            url: "https://github.com/huggingface/swift-transformers.git",
            exact: "0.1.8"
        ),
        .package(
            url: "https://github.com/weichsel/ZIPFoundation.git",
            exact: "0.9.20"
        ),
    ],
    targets: [
        .target(
            name: "SiliconDiffusionBonsai",
            dependencies: [
                .product(name: "MLX", package: "mlx-swift"),
                .product(name: "MLXNN", package: "mlx-swift"),
                .product(name: "MLXRandom", package: "mlx-swift"),
                .product(name: "Transformers", package: "swift-transformers"),
                .product(name: "ZIPFoundation", package: "ZIPFoundation"),
            ]
        ),
        .executableTarget(
            name: "BonsaiRuntimeProbe",
            dependencies: [
                "SiliconDiffusionBonsai",
            ]
        ),
        .testTarget(
            name: "SiliconDiffusionBonsaiTests",
            dependencies: [
                "SiliconDiffusionBonsai",
                .product(name: "ZIPFoundation", package: "ZIPFoundation"),
            ]
        ),
    ]
)
