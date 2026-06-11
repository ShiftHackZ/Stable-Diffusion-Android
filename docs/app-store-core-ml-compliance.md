# App Store Core ML Compliance Research

Date: 2026-06-11

## Scope

This note evaluates whether SDAI can add an iOS-only local image generation provider built on Core ML, with downloadable or imported Stable Diffusion model assets.

Working feature name: `Silicon Diffusion Core ML`.

It is a product/engineering compliance review, not legal advice.

## Result

No App Store compliance blocker was found for an iOS Core ML local provider, if the implementation stays within Apple's public APIs and treats models as user-visible data assets, not executable code.

The recommended direction is to proceed with an iOS-first local provider. The safest first implementation is a free, explicit, opt-in "Local Core ML" provider that:

- uses Core ML / Swift / Kotlin Multiplatform integration through public APIs only;
- runs prompts, source images, and generated images on device by default;
- downloads only model data/resources into the app container after an explicit user action;
- clearly shows model source, license, file size, device requirements, and delete/offline status;
- avoids external paid unlocking for models or features unless routed through Apple's in-app purchase rules;
- includes App Review notes explaining the local generation flow and how reviewers can exercise it.

For public UI/metadata, prefer Apple's official spelling `Core ML` over `CoreML`.

## Evidence

Apple Review Guideline 2.5.1 requires public APIs and current OS support. Core ML is an Apple public ML framework, and Apple documents Core ML model packages as an intended way to integrate ML models into Xcode projects.

Apple Review Guideline 2.5.2 is the main boundary: the app must not download, install, or execute code that changes app functionality. Downloaded Core ML model files should therefore be handled strictly as data/resources. The implementation must not download dynamic frameworks, native binaries, Python code, custom kernels, JIT code, scripts, or plugin code.

Apple Review Guideline 4.2.3 explicitly allows apps that need additional resources on first launch, as long as the app discloses the download size and prompts the user. Apple also recommends this pattern in the `apple/ml-stable-diffusion` FAQ for large Core ML model files: prompt the user to download model assets on first launch and disclose the size because of data/storage impact.

Apple's `apple/ml-stable-diffusion` project is MIT-licensed and provides a Swift `StableDiffusion` package for apps, backed by Core ML model files generated from PyTorch/diffusers. Its README lists iOS/iPadOS runtime support and device benchmarks, including Stable Diffusion 2.1 at 512x512 and SDXL iOS at 768x768.

Apple App Privacy guidance says data processed only on device and not sent to a server is not "collected" for App Store privacy answers. That is favorable for a genuinely offline Core ML provider. If any model downloads, telemetry, crash analytics, remote provider calls, or cloud moderation are added, those data flows still need to be reflected in privacy labels and the privacy policy.

Apple Review Guideline 5.1.2 requires clear disclosure and explicit permission before personal data is shared with third-party AI. Local Core ML generation avoids this for prompt/source-image inference, but the app must keep cloud providers clearly separated from the local provider.

## Compliance Requirements

Use only public Apple APIs. Core ML, Swift, Foundation, UIKit/SwiftUI, and normal file APIs are acceptable. Do not use private ANE APIs, private compiler/runtime entry points, downloaded dynamic libraries, or JIT/executable payloads.

Keep downloaded models as data. Store model packages in the app container, validate them before use, and load them through Core ML-supported model loading paths. Treat model catalogs as metadata, not as code/config that silently unlocks unrelated app features.

Make downloads explicit. Before downloading a model, show at least source, license, approximate size, required iOS/device class, and storage impact. Provide cancel/retry/delete, and make failure states clean.

Keep privacy labels accurate. Pure local inference should not add prompt/image collection by itself, but model download requests, analytics, crash reporting, remote providers, or generated-image reporting can still collect data and must be disclosed.

Separate local and cloud flows in the UI. The local provider should say that generation runs on device. Remote providers should continue to disclose their network/API-key behavior.

Handle content risk. Image generation can produce objectionable or age-sensitive outputs depending on model and prompt. Metadata, age rating, safety copy, and sharing/reporting surfaces should be reviewed before release. If the app ever hosts or shares generated content between users, apply App Review user-generated-content controls.

Respect model licenses. Core ML compatibility does not grant model distribution rights. Bundled or first-party-downloadable models must have licenses compatible with App Store distribution and the app's use case. User-imported models should be presented as user-provided content, but the app should still avoid endorsing unknown licenses.

Use IAP if monetizing. Free model downloads from the app or user imports are lower risk. Any paid unlock of models, model packs, credits, or generation features inside the app should use Apple's in-app purchase system unless a specific App Review exception applies.

Document review access. App Review notes should explain that this is an offline Core ML provider, what device/iOS version is needed, how to download/import a test model, and whether a demo model is bundled.

## Risk Register

| Area | Risk | Level | Mitigation |
| --- | --- | --- | --- |
| Core ML runtime | Rejection for local ML execution | Low | Use public Core ML APIs only and document the feature in review notes. |
| Downloaded model assets | Reviewer interprets downloaded model package as downloaded code | Medium | Keep assets non-executable, load through Core ML, show them as model data, and avoid plugins/scripts/native binaries. |
| Model size | Large first-run download hurts review/user experience | Medium | Explicit size prompt, resumable download, Wi-Fi-friendly UX, delete model action. |
| Model license | Incompatible model license or unclear redistribution rights | Medium | Start with clearly licensed model sources; store source/license metadata per model. |
| Generated content | Objectionable generated output or age-rating mismatch | Medium | Review age rating, metadata, safety controls, and sharing/reporting behavior. |
| Privacy labels | New network or analytics path not reflected in App Store Connect | Medium | Keep local inference offline; update privacy labels for downloads/analytics/reporting. |
| Performance/thermal | Excess heat, memory pressure, or crashes on older iPhones | Medium | Gate by iOS/device capability, expose reduced-memory mode, test on device matrix. |
| Monetization | External model purchase/unlock path violates IAP | Medium | Use IAP for paid digital unlocks or keep model catalog free/user-imported. |

## Recommended Implementation Path

1. Add an iOS-only `Local Core ML` provider shell behind provider availability checks.
2. Start with text-to-image using the Apple/Hugging Face documented Stable Diffusion 2.1 Core ML path before SDXL.
3. Add model manager UX: download/import, verify, delete, show license/source/size/device requirements.
4. Keep prompts/images on device and avoid telemetry in the first version.
5. Add img2img/inpaint only after text-to-image is stable, because image preprocessing and memory pressure add review and QA surface.
6. Keep SDXL as a later option: Apple benchmarks show it works on iOS, but latency/model size make it a heavier first release.

## Sources

- Apple App Review Guidelines: https://developer.apple.com/app-store/review/guidelines/
- Apple Core ML Models page: https://developer.apple.com/machine-learning/models/
- Apple App Privacy Details: https://developer.apple.com/app-store/app-privacy-details/
- Apple Core ML Stable Diffusion repository: https://github.com/apple/ml-stable-diffusion
- Stability AI SDXL model card and license metadata: https://huggingface.co/stabilityai/stable-diffusion-xl-base-1.0
