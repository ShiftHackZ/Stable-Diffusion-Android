// swift-tools-version: 5.9

import PackageDescription

let package = Package(
    name: "SiliconDiffusionCoreML",
    platforms: [
        .iOS(.v16),
    ],
    products: [
        .library(
            name: "SiliconDiffusionCoreML",
            targets: ["SiliconDiffusionCoreML"]
        ),
    ],
    dependencies: [
        .package(
            url: "https://github.com/apple/ml-stable-diffusion.git",
            revision: "e12202c1f6405b83918b58a5d097cd61e3e1f702"
        ),
        .package(
            url: "https://github.com/weichsel/ZIPFoundation.git",
            from: "0.9.20"
        ),
    ],
    targets: [
        .target(
            name: "SiliconDiffusionCoreML",
            dependencies: [
                .product(name: "StableDiffusion", package: "ml-stable-diffusion"),
                .product(name: "ZIPFoundation", package: "ZIPFoundation"),
            ]
        ),
    ]
)
