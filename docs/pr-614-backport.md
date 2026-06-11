# PR 614 Backport Audit

Source: https://github.com/ShiftHackZ/Stable-Diffusion-Android/pull/614

This document tracks useful changes from PR #614 that are worth preserving in the current KMP master. The PR was created against an older Android-only master and later mixed product work with PDAI rebranding, website edits, release automation, dependency churn, and marketing/site tracking. Those categories are intentionally excluded from the backport.

## Useful Improvements

| Area | PR commits | What to preserve | Backport status |
| --- | --- | --- | --- |
| A1111 and Forge generation controls | `1696cacf`, `89cf7cb9` | Hires.Fix, ADetailer, Forge module lookup, scheduler/model metadata, and request mapping for A1111-compatible backends. | Backported. Domain configs, A1111 request mapping, KMP txt2img/img2img UI controls for Hires.Fix/ADetailer/Scheduler, Forge module discovery, txt2img Forge module selection/override settings, ADetailer availability checks, and Hires OOM error copy are ported. |
| Inpaint UX | `9ebbe972`, `e43b4642` | Better mask drawing ergonomics, pan/zoom handling, and visible mask overlay on the source image. | Backported in KMP form. The standalone inpaint screen now has draw mode, gesture zoom when draw is off, zoom reset, and a synchronized zoom slider; the img2img preview keeps an opaque mask overlay. |
| Gallery UX | `9ebbe972`, `96289bd6`, `3cca660e`, `9a7c3468`, `55826b37`, `27cd2b43`, `e43b4642`, `91a62d29` | Selection mode, batch like/hide/unlike/unhide, detail navigation improvements, thumbnail/blurhash placeholders, and save selected images. | Mostly backported. Batch hide/unhide, batch like/unlike, single-image like toggle, liked badges, full-width report action, and swipe navigation in gallery detail are ported. Save-selected export already existed in current master. Thumbnails/blurhash were reviewed and deferred because they require a KMP storage/cache migration. |
| Media storage performance | `30dd4d3e`, `96289bd6`, `9a7c3468`, `55826b37` | Move generated image payloads out of Room rows where feasible, add thumbnails/cache metadata, and keep migration compatibility. | Deferred. High-risk because current master is KMP Room with Android/iOS targets and the PR implementation was Android/file-store oriented. |
| Fal.AI backend | `509fae69`, `2c358643`, later Fal.AI fixes | Fal.AI source, endpoint catalog parsing, FLUX request/response mapping, dynamic generation form, and tests. | Backported in KMP architecture for compatible FLUX txt2img/img2img endpoints. Current master now has setup/API-key validation, endpoint selection in the universal generation form, predefined Fal image sizes, acceleration/sync/safety options, native `num_images` batch handling, queue polling, txt2img/img2img repository routes, and tests. Dynamic platform endpoint discovery, redux/variation endpoints, Flux 2 endpoints with divergent schemas, and Fal inpainting remain deferred. |
| Inactive-source network guard | `c027b8f7` | Avoid fetching remote model lists/engine lists for providers that are not the active generation source. | Backported. |
| Gallery model name | `127580e5` | Persist and display the model/engine name used for a generation result. | Backported. |
| Local Qualcomm QNN and MNN | `373edf15`, `1f3dc388`, `5dc3f84f` | QNN/MNN local backend ideas, model scanning, runtime selection, progress reporting, and square-resolution Hires.Fix presets. | Deferred. QNN is Android-only and expects proprietary native artifacts; MNN in this PR only adds model configuration JSON without integrating a runtime in current master. |
| Progress notifications | `1f3dc388` | Low-importance progress notification channel to avoid disruptive heads-up alerts. | Backported. |
| Logger export | `9ebbe972` | Export/copy log file from Logger screen. | Backported. |

## Excluded From Backport

- SDAI to PDAI package/name/path/log rebranding and icon/logo replacements.
- Website redesign, CNAME changes, Telegram QR/download page changes, Yandex.Metrika, policy/index updates, and docs site marketing changes.
- Release notes for PDAI versions and GitHub release workflow changes.
- Broad dependency updates and version-management plugins from the fork.
- Donate-button removal and unrelated product policy changes.
- Any generated documentation or large site artifact churn.

## Current Master Notes

- Current `master` is newer than the PR branch and has already moved to KMP/iOS-ready source sets.
- Room schemas are under `storage/src/commonMain` with Android and iOS platform builders.
- Gallery already has paging/export and a `hidden` field, so gallery changes must be ported as focused deltas rather than wholesale copies.
- The PR branch contains mass package moves after the PDAI rebrand; use pre-rebrand commits as references where possible.

## Backport Log

### 2026-06-11

- Created branch `codex/backport-pr-614-improvements` from current `master`.
- Added this audit document and initial status table.
- Backported inactive-source network guards into KMP domain use cases:
  `GetStableDiffusionModelsUseCaseImpl`, `FetchAndGetSwarmUiModelsUseCaseImpl`,
  `FetchHuggingFaceModelsUseCaseImpl`, and `FetchAndGetStabilityAiEnginesUseCaseImpl`.
- Kept server setup usable by falling back to built-in HuggingFace models when inactive-source guarding returns an empty list.
- Verification: `./gradlew :domain:testDebugUnitTest --tests ...GetStableDiffusionModelsUseCaseImplTest --tests ...FetchAndGetSwarmUiModelsUseCaseImplTest --tests ...FetchHuggingFaceModelsUseCaseImplTest --tests ...FetchAndGetStabilityAiEnginesUseCaseImplTest` passed.
- Backported the visible inpaint mask preview from PR `e43b4642` into the KMP img2img flow by rendering selected-image preview strokes at 50% alpha.
- Left the old standalone inpaint zoom/draw controls out because current master already merged inpaint into img2img and has a newer `ZoomableImage` implementation.
- Verification: `./gradlew :presentation:compileDebugKotlinAndroid` passed.
- Backported the gallery selection visibility toggle from PR `e43b4642` against the current `hidden` model:
  added bulk hidden updates in Room DAO, data source, repository, domain use case, GalleryViewModel, and selection toolbar UI.
- Toggle behavior: if any selected item is visible, the action hides the whole selection; if all selected items are hidden, the action unhides them.
- Verification: `./gradlew :data:testDebugUnitTest --tests ...GenerationResultLocalDataSourceTest --tests ...GenerationResultRepositoryImplTest :presentation:testDebugUnitTest --tests ...GalleryViewModelTest` passed.
- Backported gallery model name persistence/display from PR `127580e5`:
  added `modelName` to `AiGenerationResult`, Room `GenerationResultEntity`, schema v9, entity/domain mappers, and gallery details info/params output.
- Generation repositories now attach a model/engine name where current master already has reliable local selection data:
  A1111 checkpoint, HuggingFace alias, Stability AI engine, SwarmUI model, OpenAI image model, local ONNX model name, and MediaPipe selected/custom model id.
- Verification: `./gradlew :storage:compileDebugKotlinAndroid :data:testDebugUnitTest --tests ...StableDiffusionGenerationRepositoryImplTest --tests ...HuggingFaceGenerationRepositoryImplTest --tests ...StabilityAiGenerationRepositoryImplTest --tests ...OpenAiGenerationRepositoryImplTest --tests ...SwarmUiGenerationRepositoryImplTest :presentation:compileDebugKotlinAndroid` passed.
- Backported logger copy/share actions from PR `9ebbe972`:
  added `LoggerPlatformActions`, Android clipboard/share implementation, common no-op binding, logger toolbar copy/share icons, and ViewModel tests.
- Verification: `./gradlew :presentation:testDebugUnitTest --tests ...LoggerViewModelTest :presentation:compileDebugKotlinAndroid` passed.
- Backported low-priority progress notifications from PR `1f3dc388`:
  added a separate SDAI progress notification channel with low importance/no sound/no vibration and routed foreground generation progress through `createProgressNotification`.
- Verification: `./gradlew :core:notification:compileDebugKotlinAndroid :feature:work:compileDebugKotlinAndroid` passed.
- Partially backported A1111/Forge generation controls from PRs `1696cacf` and `89cf7cb9`:
  added KMP domain configs for Hires.Fix and ADetailer, optional A1111 request fields, typed kotlinx.serialization `alwayson_scripts` mapping, and mapper tests.
- Verification: `./gradlew :domain:compileDebugKotlinAndroid :network:compileDebugKotlinAndroid :data:testDebugUnitTest --tests ...KtorStableDiffusionGenerationMappersTest` passed.
- Added KMP advanced UI controls for A1111 Hires.Fix and ADetailer:
  txt2img can configure Hires.Fix and ADetailer, img2img can configure ADetailer, and both flows now carry enabled configs into their generation payloads only for `AUTOMATIC1111`.
- At this checkpoint Forge module discovery and scheduler selection were still pending; both were completed later in this backport.
- Verification: `./gradlew :presentation:compileDebugKotlinAndroid :presentation:testDebugUnitTest --tests ...TextToImageStateTest --tests ...ImageToImageStateTest` passed.
- Backported gallery liked/favorite support from PR gallery UX work:
  added `liked` to domain/storage schema v10, Room DAO bulk updates, repository/use cases, gallery selection like/unlike toggle, liked grid badge, and gallery detail like/unlike action.
- Verification: `./gradlew :storage:compileDebugKotlinAndroid :domain:compileDebugKotlinAndroid :data:testDebugUnitTest --tests ...GenerationResultLocalDataSourceTest --tests ...GenerationResultRepositoryImplTest :presentation:testDebugUnitTest --tests ...GalleryViewModelTest --tests ...GalleryDetailViewModelTest :presentation:compileDebugKotlinAndroid` passed.
- Backported Forge module discovery from PR `89cf7cb9` into the KMP metadata stack:
  added `ForgeModule`, `sdapi/v1/sd-modules` Ktor metadata endpoint, data mapper/remote data source/repository, `GetForgeModulesUseCase`, DI wiring, and unit tests.
- Kept the old PR behavior that missing/non-Forge A1111 endpoints resolve to an empty module list at the use-case boundary instead of surfacing as a UI error.
- Verification: `./gradlew :network:compileDebugKotlinAndroid :domain:testDebugUnitTest --tests ...GetForgeModulesUseCaseImplTest :data:testDebugUnitTest --tests ...KtorForgeModulesMappersTest --tests ...ForgeModulesRemoteDataSourceTest --tests ...ForgeModulesRepositoryImplTest` passed.
- Backported A1111 scheduler selection from PRs `1696cacf`/`89cf7cb9` without the old Android-only `ModelType` refactor:
  added the KMP `Scheduler` enum, optional `scheduler` request field for txt2img/img2img, A1111-only dropdown in advanced options, and state/request mapper tests.
- Default behavior stays compatible with existing A1111 servers: `Scheduler.AUTOMATIC` maps to `null`, so no scheduler field is sent unless the user chooses a concrete scheduler.
- Verification: `./gradlew :domain:compileDebugKotlinAndroid :network:compileDebugKotlinAndroid :data:testDebugUnitTest --tests ...KtorStableDiffusionGenerationMappersTest :presentation:compileDebugKotlinAndroid :presentation:testDebugUnitTest --tests ...TextToImageStateTest --tests ...ImageToImageStateTest` passed.
- Completed Forge module selection for txt2img:
  added a KMP multi-select dropdown, loaded Forge modules alongside A1111 samplers in txt2img/img2img ViewModels, carried selected modules into `TextToImagePayload`, and mapped them to `override_settings.forge_additional_modules`.
- Updated background-generation payload DTOs so Hires.Fix, ADetailer, Scheduler, and txt2img Forge modules survive scheduled/background work serialization.
- Verification: `./gradlew :domain:compileDebugKotlinAndroid :network:compileDebugKotlinAndroid :data:testDebugUnitTest --tests ...KtorStableDiffusionGenerationMappersTest :presentation:compileDebugKotlinAndroid :presentation:testDebugUnitTest --tests ...TextToImageStateTest --tests ...ImageToImageStateTest :feature:work:compileDebugKotlinAndroid` passed.
- Added domain unit tests for the backported gallery bulk visibility, bulk liked, and detail like-toggle use cases.
- Verification: `./gradlew :domain:testDebugUnitTest --tests ...SetGalleryItemsLikedUseCaseImplTest --tests ...SetGalleryItemsVisibilityUseCaseImplTest --tests ...ToggleImageLikeUseCaseImplTest` passed.
- Reviewed the remaining large PR areas:
  `d777d341` is already present in current master's Android themes;
  Fal.AI requires a dedicated KMP provider port instead of a direct old Android/Retrofit/Rx copy;
  thumbnail/blurhash and file-backed media storage require a KMP Room/cache migration;
  QNN requires proprietary Android native artifacts and does not fit the current shared runtime surface;
  `5dc3f84f` adds only MNN model configuration JSON with no runtime integration.
- Final verification:
  `git diff --check` passed;
  `./gradlew :storage:compileDebugKotlinAndroid :domain:testDebugUnitTest :network:compileDebugKotlinAndroid :data:testDebugUnitTest :presentation:testDebugUnitTest :presentation:compileDebugKotlinAndroid :core:notification:compileDebugKotlinAndroid :feature:work:compileDebugKotlinAndroid` passed;
  `./gradlew :app:assembleFossDebug` passed;
  `./gradlew :storage:compileKotlinIosSimulatorArm64 :domain:compileKotlinIosSimulatorArm64 :network:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64` passed.
- Simulator smoke:
  installed and launched `app/android/build/outputs/apk/foss/debug/app-foss-debug.apk` on Android `Pixel 9 Pro` emulator as `com.shifthackz.aisdv1.app.foss`;
  launched installed iOS simulator app `com.shifthackz.aisdv1.app` and verified the `Text to Image` screen;
  no new iOS crash reports were present for 2026-06-11. Android `mobile_list_crashes` returned a device lookup error for `emulator-5554`, so Android crash listing was not available through that tool.
- Follow-up ADetailer handling after simulator regression:
  added A1111 metadata lookup for ADetailer, KMP domain/data repository plumbing, and `IsADetailerAvailableUseCase`;
  txt2img/img2img now refresh ADetailer availability for the active A1111 server and show `ADetailer is not installed` with install/refresh actions when the script is absent;
  generation payload mapping now suppresses ADetailer when unavailable, preventing the A1111 `Script 'ADetailer' not found` 422 response.
- Corrected ADetailer availability detection after reviewing the official upstream docs and the live A1111 host:
  `Bing-su/adetailer` documents ADetailer as an A1111 extension using `alwayson_scripts["ADetailer"]` in its REST API examples, and the current server reports enabled `adetailer` through `sdapi/v1/extensions` while omitting it from `script-info/scripts`;
  availability now merges `script-info` or legacy `scripts` with enabled extensions before deciding whether to show the advanced controls.
  ADetailer refresh failures no longer write the form-level error state, so an absent extension cannot disable the main Imagine action; when unavailable, generation mapping still suppresses all ADetailer payload fields.
- Added targeted Hires.Fix error copy for server GPU OOM responses (`OutOfMemoryError` / `out of memory`) so Hires 500s explain that image size, Hires scale/steps, or Hires.Fix should be reduced.
- Completed the inpaint UX port in current KMP structure:
  restored opaque img2img mask preview, added full-screen inpaint draw-mode toggle as the third bottom control after Undo/Clear, added gesture zoom when draw mode is off, added magnifier reset, and added a synchronized zoom slider.
- Polished inpaint zoom controls after simulator testing:
  brush-size and zoom sliders now use the same leading-control width and slider width, the brush preview dot and magnifier icon align on one vertical axis, and gesture pan/zoom no longer restarts pointer handling during movement.
- Restored generation-form width/height helpers:
  added swap width/height and aspect-ratio adjustment controls for editable size fields, then aligned their placement with PDAI: both action icons sit to the right of the Width/Height fields in the same row.
- Finished gallery follow-ups:
  fixed like icons to outline when unliked and filled when liked, moved report to a full-width first-row action when the flavor allows reporting, and replaced the temporary detail-swipe overlay with a `HorizontalPager` that shows adjacent images during the swipe while preserving zoom gestures on the active image.
- Moved gallery selection actions out of the top app bar:
  like/unlike, hide/unhide, delete, and export selected images now render in a dedicated bottom action strip above the `Selected images` strip, leaving the top bar focused on close + title while selection mode is active.
- Stabilized gallery detail swiping after fast-swipe regression testing:
  pager state now uses absolute gallery indices, and `GalleryDetailViewModel` exposes a constructor-configurable side buffer with `GALLERY_DETAIL_PAGER_BUFFER = 3` to preload up to three images on either side of the selected image.
  This removes the post-swipe one-frame image mismatch and keeps a second quick swipe available while the buffered window catches up.
- Verification:
  `./gradlew :network:compileKotlinMetadata :domain:compileKotlinMetadata :data:compileKotlinMetadata :presentation:compileDebugKotlinAndroid --no-daemon` passed;
  `./gradlew :presentation:testDebugUnitTest :domain:testDebugUnitTest :data:testDebugUnitTest --no-daemon` passed.
- Follow-up verification:
  `./gradlew :network:compileKotlinMetadata :data:compileKotlinMetadata :presentation:compileDebugKotlinAndroid --no-daemon` passed;
  `./gradlew :data:testDebugUnitTest :domain:testDebugUnitTest :presentation:testDebugUnitTest --no-daemon` passed.
- Targeted verification after ADetailer extension lookup and gallery pager buffer:
  live A1111 `sdapi/v1/extensions` returned enabled `adetailer`, while `sdapi/v1/scripts` and `sdapi/v1/script-info` did not expose ADetailer;
  `./gradlew :data:testDebugUnitTest --tests '*StableDiffusionScriptsRemoteDataSourceTest' --tests '*KtorStableDiffusionScriptsMappersTest' :presentation:testDebugUnitTest --tests '*GalleryDetailViewModelTest' :presentation:compileDebugKotlinAndroid --no-daemon` passed.
- Final verification for this follow-up:
  `git diff --check` passed;
  `./gradlew :app:assembleFossDebug --no-daemon` passed;
  installed `app/android/build/outputs/apk/foss/debug/app-foss-debug.apk` on Android `Pixel 9 Pro` emulator and confirmed the app launches to `Text to Image`.
- Final follow-up verification after PDAI comparison and gallery gesture fix:
  `./gradlew :presentation:compileKotlinMetadata :presentation:compileDebugKotlinAndroid --no-daemon` passed;
  `./gradlew :data:testDebugUnitTest :domain:testDebugUnitTest :presentation:testDebugUnitTest :app:assembleFossDebug --no-daemon` passed;
  `xcodebuild -project app/ios/iosApp.xcodeproj -scheme Debug -configuration Debug -destination id=4796195F-A17E-4AC9-942C-789E81E93E6A -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build` passed.
- Android Mobile MCP smoke on `Pixel 9 Pro` emulator:
  confirmed Width/Height fields keep the PDAI-style two-field row with `Swap width and height` and `Aspect ratio` icons to the right of Height;
  confirmed ADetailer toggle on the active A1111 infra shows `ADetailer is not installed` with `Install` and `Refresh` instead of advanced ADetailer fields;
  confirmed gallery detail swipes horizontally from one image to the next through the new pager, repeated fast left swipes advance across the buffered pages, and liked/unliked icons render filled/outlined respectively.
- Added Fal.ai as a current-master KMP provider using the fork only as a contract reference:
  added `ServerSource.FAL_AI`, Fal.ai API-key storage/configuration, setup form/link/localization, connectivity use cases, Koin bindings, txt2img routing, data repository, Ktor network API, request/response DTOs, and mapper/remote/repository/domain tests.
- Verified against current Fal.ai documentation before porting:
  authentication uses `Authorization: Key <api key>`;
  long-running inference uses `https://queue.fal.run/{model}` with `status_url`/`response_url` polling and `IN_QUEUE`/`IN_PROGRESS`/`COMPLETED` states;
  the initial model is `fal-ai/flux/schnell`, using `prompt`, custom `image_size`, `num_inference_steps` clamped to `1..12`, `guidance_scale`, optional non-negative `seed`, `num_images = 1`, `sync_mode = false`, safety checker, and PNG output.
- Kept this as a KMP-native provider instead of copying the old Android/Retrofit/Rx dynamic endpoint screen from PR #614.
  Inpaint remains deferred because it needs endpoint-specific form semantics and a clearer upload/input pipeline decision in the shared architecture.
- Verification for Fal.ai provider:
  `./gradlew :domain:testDebugUnitTest --tests '*TextToImageUseCaseImplTest' --tests '*TestFalAiApiKeyUseCaseImplTest' --tests '*ConnectToFalAiUseCaseImplTest' :data:testDebugUnitTest --tests '*KtorFalAiGenerationMappersTest' --tests '*KtorFalAiGenerationRemoteDataSourceTest' --tests '*FalAiGenerationRepositoryImplTest' :presentation:compileDebugKotlinAndroid --no-daemon` passed;
  `./gradlew :app:assembleFossDebug --no-daemon` passed.
  Mobile MCP simulators were not touched during this Fal.ai pass.
- Expanded Fal.ai from an initial txt2img provider into the universal generation form:
  added a first-field endpoint selector for Fal, predefined `image_size` presets, model-specific sampling step/guidance ranges, acceleration, sync mode, and Fal safety-checker mapping through the existing Allow NSFW toggle.
- Added docs-verified compatible endpoints instead of copying the fork's standalone form wholesale:
  `fal-ai/flux/schnell`, `fal-ai/flux/dev`, `fal-ai/flux-lora`, `fal-ai/flux/dev/image-to-image`, and `fal-ai/flux-lora/image-to-image`.
  The fork-visible redux/variation endpoints and Flux 2 endpoints are left out until their schemas can be represented cleanly by the shared form.
- Reworked Fal batch handling to use Fal's native `num_images` contract (`1..4`) and return a list of generated results from one queue request instead of repeating single-image calls in the use case.
- Added Fal img2img routing through `ImageToImageUseCase`, `FalAiGenerationRepository`, Ktor queue API, and data URI image inputs so regular img2img works without introducing a Fal CDN upload pipeline in this patch.
- Updated background-work payload DTOs so Fal endpoint, image size, acceleration, and sync mode survive scheduled generation.
- Verification for Fal.ai form/img2img follow-up:
  `./gradlew :domain:testDebugUnitTest :data:testDebugUnitTest --no-daemon` passed;
  `./gradlew :presentation:compileDebugKotlinAndroid :feature:work:compileDebugKotlinAndroid --no-daemon` passed;
  `./gradlew :presentation:testDebugUnitTest --no-daemon` passed;
  `git diff --check` passed;
  `./gradlew :app:assembleFossDebug --no-daemon` passed;
  `./gradlew :domain:compileKotlinIosSimulatorArm64 :network:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 --no-daemon` passed.
- Live Fal.ai E2E was intentionally not pursued further:
  the configured iOS simulator API key reached Fal.ai's exhausted-balance lock, and the current official Fal.ai documentation does not expose a generic sandbox API key for API requests.
  Their Sandbox/Playground free credits are documented separately from API usage, so the remaining validation for this patch is covered by contract tests, queue-response fixtures, Android/iOS compilation, and app assembly.
- Final documentation refresh:
  README now lists Fal.ai in the provider/platform and feature matrices alongside Silicon Diffusion Core ML, and Dokka HTML is regenerated from the rebased PR #614 backport API surface.
