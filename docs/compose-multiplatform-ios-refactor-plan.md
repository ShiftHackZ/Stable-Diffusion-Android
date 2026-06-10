# Compose Multiplatform + iOS Refactor Plan

## Цель

Помодульно перевести кодовую базу на Kotlin Multiplatform и Compose Multiplatform так, чтобы:

- Android-функционал сохранялся полностью на каждом этапе миграции.
- iOS получал тот же основной пользовательский сценарий через общий Compose UI.
- Local Diffusion на iOS был явно out-of-scope для первой версии.
- Local Diffusion на Android продолжал работать через существующие ONNX/MediaPipe реализации без регрессий.
- Миграция шла инкрементально: каждый этап должен оставлять проект в собираемом и тестируемом состоянии.

## Зафиксированные решения

- Common code uses coroutines and `Flow`; production Rx chains are removed instead of being wrapped with presentation adapters.
- When a workflow slice is compact enough, migrate the whole call-chain to `suspend`/`Flow` and common/iOS contracts in one pass.
- Networking target is Ktor:
  - common HTTP client/configuration in `commonMain`;
  - OkHttp engine on Android;
  - Darwin engine on iOS;
  - legacy Retrofit/Gson code is not used for migrated production API paths.
- Storage target is Room KMP with bundled SQLite for Android and iOS.
- Room DAO contracts should use `suspend`/`Flow`; iOS opens the same common schema through the platform driver.
- DI target is Koin KMP:
  - common graphs use `koin-core` in `commonMain`;
  - Android keeps `koin-android`, AndroidX ViewModel and Compose integration in `androidMain`;
  - iOS starts the shared Koin graph from the Compose/iOS bootstrap.
- Prefer maximum code sharing in `commonMain`; platform source sets should contain only platform APIs, adapters, and explicitly unsupported features.
- Local Diffusion and MediaPipe remain Android-only for the first iOS milestone. iOS must hide or explicitly disable those paths.
- Common source-set file/class names should not use `Shared` as a prefix/suffix. The shared nature is expressed by `commonMain` itself.
- Presentation migration target is full UI parity, not a simplified iOS-only substitute: existing Android workflows must move to common Koin ViewModels/content and keep router-based navigation.
- Localization must keep using the existing Android language resources as the source catalog and expose the same keys/languages to Android and iOS.

## Non-Goals первой итерации

- Не переносить Android Local Diffusion на iOS.
- Не переписывать ONNX/MediaPipe под CoreML/Metal в рамках базовой CMP-миграции.
- Не заменять дизайн-систему и UX без отдельной продуктовой причины.
- Не делать большой одномоментный rewrite всего проекта.

## Implementation checkpoint: `master-v1`

Current migration checkpoint:

### Актуальный checkpoint после очередного прохода

- Persistent Room database is now a KMP Room database:
  - `PersistentDatabase`, persistent entities, contracts, DAOs, and converters live in `storage/commonMain`;
  - Android opens the existing `ai_sd_v1_storage_db` file path to preserve the installed database;
  - iOS opens the same schema under Application Support;
  - DAOs moved to `suspend`/`Flow` APIs for migrated persistent entities.
- Generation-result persistence/history is now shared:
  - generation result data source/repository/use cases live on coroutine contracts;
  - shared txt2img saves generated results through the shared persistence path;
  - shared history route, router contract, ViewModel, and Compose content live in `presentation/commonMain`.
- Android-only generation internals are being converted to coroutines instead of being left as Rx islands:
  - Local Diffusion and MediaPipe contracts now expose `suspend` processing APIs;
  - Local Diffusion status observation is `Flow`;
  - Android Local Diffusion remains Android-only and out-of-scope for iOS, but its call-chain is coroutine-based.
- Android download/report/demo/random-image/background-status/preferences/work/media-store workflows were moved off Rx and onto `suspend`/`Flow`.
- Retrofit/Rx is no longer a production dependency target:
  - provider APIs that are in scope for iOS use common Ktor clients/data sources;
  - Android metadata/provider paths that had common Ktor replacements were removed;
  - Gradle version-catalog aliases for RxJava/RxAndroid/RxKotlin/RxNetwork, Room/Paging Rx, Retrofit core/converter/Rx adapter, and build-logic auto Rx injection were removed;
  - a production grep for RxJava/Retrofit imports and dependency aliases returns no matches outside historical documentation.
- Localization is common-backed:
  - `core:localization` generates the common string catalog from the existing Android `strings.xml` language resources;
  - Android keeps resource drawables for country flags;
  - iOS resolves the same language codes/string keys through common `Localization` and reads the active language from `AppleLanguages`, so existing `en`, `uk`, `tr`, `ru`, and `zh` resources are shared by both platforms.
- Common-first data/domain generation pipeline has advanced:
  - `MediaStoreGateway` and `GetMediaStoreInfoUseCaseImpl` now live in `domain/commonMain`;
  - shared/iOS uses `NoOpMediaStoreGateway`, while Android keeps the existing real MediaStore implementations in `data/androidMain`;
  - demo contracts (`TextToImageDemo`, `ImageToImageDemo`) moved to `domain/commonMain`, with no-op shared fallbacks and the existing Android `demoModule` implementation preserved;
  - cloud generation repositories for A1111, Swarm UI, Horde, HuggingFace, OpenAI, and Stability AI moved to `data/commonMain`;
  - `CoreGenerationRepository`/`CoreMediaStoreRepository` moved to common and export generated images through a platform `expect/actual` byte encoder, preserving Android Bitmap re-encode behavior behind `androidMain`;
  - `TextToImageUseCaseImpl`, `ImageToImageUseCaseImpl`, `InterruptGenerationUseCaseImpl`, local generation repository contracts, and Local Diffusion status observation moved to `domain/commonMain`;
  - iOS binds no-op Local Diffusion/MediaPipe repositories, while Android keeps Local Diffusion and MediaPipe implementations Android-only.
- More Android UI has moved into common Compose:
  - Donate state/ViewModel/router/content already live in `presentation/commonMain`; Android is a thin resources/effect wrapper and iOS uses the same shared ViewModel/content;
  - `ReportScreenContent` now lives in `presentation/commonMain`;
  - Android `ReportScreen` is a thin wrapper that maps Android resources/MVI/modal rendering into the shared content;
  - report image preview decodes base64 through the shared `decodeBase64ImageBitmap()` expect/actual path, not Android `Bitmap` state.
- The latest common UI extraction pass moved more reusable presentation surface:
  - `AiSdApp`, `RootAppRouter`, and iOS `MainViewController()` now live under `presentation.app`, and the iOS framework is `AiSdPresentation.framework` instead of a `Shared`-named binary;
  - dialog wrappers/content, `GridBottomSheet`, `ErrorState`, `ExtraType`, `LaunchSource`, `MotionEvent`, and `ErrorComposable` live in `presentation/commonMain`;
  - chip text fields, `ExtrasFormatter`, `SliderTheme`, `GenerationBottomToolbar`, and the remote generation input form live in `presentation/commonMain`;
  - Android txt2img/img2img screens now pass common form events through a small Android MVI intent mapper while preserving the existing router/modal behavior;
  - `DownloadDialog`, `InputHistory`, `BackgroundWorkWidget`, gallery list, gallery details, and `ZoomableImage` now have common ViewModels/content;
  - gallery list/detail state uses common `ImageBitmap` decoding via platform `expect/actual`, not Android `Bitmap` state;
  - gallery detail sharing/copy uses platform actions: Android preserves `Bitmap` -> cache file -> `FileProvider` image sharing, and iOS uses native `UIActivityViewController`/`UIPasteboard`.
  - Settings now lives in `presentation/commonMain` as `SettingsViewModel`, `SettingsState`, `SettingsIntent`, `SettingsScreen`, and a `SettingsRouter` contract;
  - Android Settings keeps platform-only permission/url/report/app-settings actions behind `rememberSettingsPlatformActions()`, while iOS uses the same common UI with UIKit URL/settings hooks and safe no-op report/permission behavior;
  - `ClearAppCacheUseCase` moved to `domain/commonMain`; Android preserves log-file cleanup through an Android `AppCacheCleaner`, and iOS/shared uses a no-op cleaner after clearing shared generated-result persistence.
  - remote img2img now has a shared KMP path: `DefaultImageToImageUseCaseImpl` lives in `domain/commonMain` for A1111, Swarm UI, Horde, HuggingFace, and Stability AI; `ImageToImageViewModel`, state, intents, platform image-pick actions, and Compose content live in `presentation/commonMain`; Android/iOS provide only camera/gallery actuals and native save/share glue;
  - `ImageToImageViewModel` now owns the inpaint state in common, renders the mask editor from common Compose, maps mask parameters into `ImageToImagePayload`, and uses Android/iOS actual encoders to export the drawn mask as PNG/base64;
  - Android `AiStableDiffusionActivity` now renders `AiSdApp()` directly, and Android `presentationModule` keeps only platform service bindings (`uiUtilsModule`) instead of the old Android Navigation/ViewModel graph;
  - the old Android-only presentation navigation shell, generation ViewModels, txt2img/img2img wrappers, drawer/home nav, and Android inpaint screen/producer were removed after their common replacements were wired;
  - onboarding and splash startup logic moved into the shared path: `SplashNavigationUseCaseImpl` is now common and preference-based, `OnBoardingViewModel`/state/intent/content live in `presentation/commonMain`, Android keeps only `BackHandler`/Local Diffusion onboarding availability actuals, and iOS hides the Local Diffusion onboarding page.
- Current verified gates after these changes:
  - `:storage:compileDebugKotlinAndroid`
  - `:storage:compileKotlinIosSimulatorArm64`
  - `:network:compileDebugKotlinAndroid`
  - `:domain:compileDebugKotlinAndroid`
  - `:data:compileDebugKotlinAndroid`
  - `:demo:compileDebugKotlinAndroid`
  - `:feature:onnx:compileDebugKotlinAndroid`
  - `:feature:mediapipe:compileFullDebugKotlinAndroid`
  - `:feature:mediapipe:compileFossDebugKotlinAndroid`
  - `:feature:work:compileDebugKotlinAndroid`
  - `:presentation:compileDebugKotlinAndroid`
  - `:presentation:compileKotlinIosSimulatorArm64`
  - `:presentation:testDebugUnitTest`
  - `:presentation:linkDebugFrameworkIosSimulatorArm64`
  - `:domain:compileKotlinIosSimulatorArm64`
  - `:domain:compileDebugKotlinAndroid`
  - `:data:compileKotlinIosSimulatorArm64`
  - `:data:compileDebugKotlinAndroid`
  - `:presentation:compileDebugUnitTestKotlinAndroid`
  - `:presentation:linkDebugFrameworkIosSimulatorArm64`
  - `:app:assembleDebug`
  - `:app:assembleFullDebug`
  - `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`
  - targeted `GalleryDetailViewModelTest`
  - targeted `SettingsViewModelTest`
- Main remaining cleanup blocks:
  - continue tightening shared presentation parity around the common navigation shell and remaining modal/platform edges;
  - keep removing Android `Bitmap`/`Modal.Image.Crop` boundaries by moving image state to base64/common `ImageBitmap` and platform actions;
  - keep `:presentation:testDebugUnitTest` as a clean gate while older Android-only tests are replaced by common ViewModel tests;
  - keep Local Diffusion/MediaPipe actual implementations Android-only while their public Android contracts stay coroutine-based.

The sections below keep the detailed migration log and module plan. When an older historical line conflicts with this checkpoint, this checkpoint is the source of truth.

- Added KMP/CMP build-logic plugins: `generic.kmp.library` and `generic.kmp.compose`.
- Converted `core:validation` to KMP and moved validators to `commonMain`.
- Converted `core:common` to KMP, moving pure utilities/build info/link descriptors to `commonMain` while keeping Android/Rx/Timber/file implementations in `androidMain`.
- Converted `core:localization` to KMP. `Localization.entries` and `DurationFormatter` are common; Android flag drawables stay Android resources; iOS currently returns no platform flag resource.
- Converted `core:imageprocessing` to KMP as an Android implementation module. Current `Bitmap`/Base64/Rx converters remain in `androidMain`; iOS image codec work is still pending.
- Converted `core:notification` to KMP as an Android implementation module. Current `NotificationCompat` manager and Android notification resources remain Android-only.
- Converted `core:ui` to KMP/CMP and added a lightweight common coroutine MVI runtime:
  - common `MviState`/`MviIntent`/`MviEffect` markers;
  - common KMP ViewModel-backed MVI runtime using `StateFlow` and `Flow`;
  - common Compose `MviComponent` based on `collectAsState`;
  - existing Android `Context`/resource helpers, AndroidX Lifecycle ViewModels, Paging Compose helpers, and AndroidCoreMVI wrappers remain in `androidMain` for screens not migrated yet.
- Converted `domain` to KMP with a first common domain slice:
  - shared entities/config contracts in `commonMain`;
  - Rx use cases, Android `Bitmap`/`Uri`, `Date`/`File`/`Serializable` entities, Local Diffusion and MediaPipe contracts remain in `androidMain`.
- Converted `feature:auth` to KMP as an Android implementation module. EncryptedSharedPreferences storage remains Android-only; shared auth contracts live in `domain/commonMain`.
- Converted `network` to KMP as an Android implementation module. Current Retrofit/OkHttp/Gson/Rx stack remains in `androidMain`; Ktor/Darwin is used by migrated provider slices and is still expanding workflow-by-workflow.
- Converted `storage` to KMP as an Android implementation module. Current Room/Rx DAO stack remains in `androidMain`; KSP is wired through `kspAndroid` with explicit Room schema location.
- Converted `demo` to KMP as an Android implementation module. Demo data providers remain in `androidMain`; extracting reusable demo contracts to common is still pending.
- Converted `feature:work` to KMP as an Android implementation module. WorkManager, foreground notifications, and Local Diffusion status observation remain Android-only.
- Converted `data` to KMP as an Android implementation module. Existing repository/data-source implementations remain in `androidMain` and still use current `storage`, `network`, Rx, Gson, and Android preferences stacks.
- Converted `presentation` to KMP/CMP as an Android implementation module. Existing Android Compose screens, AndroidX Navigation, Android ViewModel/Koin integration, resources, permissions, WebView, and `Bitmap` usage mostly remain in `androidMain`; shared screen extraction is now proceeding screen-by-screen.
- Added the first common Compose entry point in `presentation/commonMain` and an iOS `MainViewController()` bridge in `presentation/iosMain`; `presentation` now builds `AiSdPresentation.framework`.
- Added a minimal SwiftUI `iosApp` host that imports `AiSdPresentation.framework` through an Xcode build phase calling `:presentation:embedAndSignAppleFrameworkForXcode`.
- Renamed `feature:diffusion` to `feature:onnx` as the Android-only Local Diffusion boundary. ONNX Runtime Android and the current local generation implementation remain in `androidMain`; iOS has no Local Diffusion implementation in this module.
- Converted `feature:mediapipe` to KMP as a flavor-aware Android-only boundary. `full` and `playstore` keep the MediaPipe image generator implementation; `foss` keeps the unsupported stub; iOS has no MediaPipe implementation.
- Added the first shared Ktor client foundation in `network/commonMain` with platform engines in `network/androidMain` and `network/iosMain`.
- Added the first provider-level Ktor slices:
  - `SdaiAppApi.fetchSupporters()` is available as a common `suspend` API, with `SupporterRaw` and `Supporter` moved to common source sets.
  - `HuggingFaceModelsApi.fetchTextToImageModels()` is available as a common `suspend` API and loads currently supported `hf-inference` text-to-image models from the Hugging Face Hub API.
  - `StabilityAiEnginesApi.fetchEngines(apiKey)` is available as a common Ktor `suspend` API, with `StabilityAiEngineRaw` moved to `network/commonMain`.
  - `OpenAiGenerationApi.validateBearerToken(apiKey)` and `OpenAiGenerationApi.generateImage(apiKey, request)` are available as common Ktor `suspend` APIs, with OpenAI request/response/image DTOs moved to `network/commonMain`.
  - `HuggingFaceGenerationApi.validateBearerToken(apiKey)` and `HuggingFaceGenerationApi.generate(apiKey, model, request)` are available as common Ktor `suspend` APIs for HuggingFace txt2img/img2img generation.
  - `StabilityAiGenerationApi.validateBearerToken(apiKey)`, `StabilityAiGenerationApi.fetchCredits(apiKey)`, `StabilityAiGenerationApi.textToImage(apiKey, engineId, request)`, and `StabilityAiGenerationApi.imageToImage(apiKey, engineId, imageBytes, parameters)` are available as common Ktor `suspend` APIs for Stability AI account checks, credits, txt2img, and img2img generation.
  - `HordeGenerationApi.generateAsync(apiKey, request)`, `HordeGenerationApi.checkGeneration(apiKey, id)`, `HordeGenerationApi.checkStatus(apiKey, id)`, `HordeGenerationApi.checkHordeApiKey(apiKey)`, `HordeGenerationApi.cancelRequest(apiKey, requestId)`, and `HordeGenerationApi.downloadImage(url)` are available as common Ktor `suspend` APIs for Horde txt2img/img2img generation, polling, cancellation, API-key validation, and image retrieval.
  - `SwarmUiModelsApi` is available as a common Ktor `suspend` API for session creation and model listing, with Basic auth DTOs, Swarm session/model DTOs, and bad-session mapping in `network/commonMain`.
  - `SwarmUiGenerationApi.generate(baseUrl, request, authorization)` and `SwarmUiGenerationApi.downloadImage(url, authorization)` are available as common Ktor `suspend` APIs for Swarm UI txt2img/img2img generation and image retrieval.
  - `Automatic1111MetadataApi.fetchLoras(baseUrl, authorization)`, `Automatic1111MetadataApi.fetchEmbeddings(baseUrl, authorization)`, and `Automatic1111MetadataApi.fetchHyperNetworks(baseUrl, authorization)` are available as common Ktor `suspend` APIs for metadata lists.
  - `Automatic1111MetadataApi.fetchModels(baseUrl, authorization)`, `Automatic1111MetadataApi.fetchSamplers(baseUrl, authorization)`, `Automatic1111MetadataApi.fetchConfiguration(baseUrl, authorization)`, and `Automatic1111MetadataApi.updateConfiguration(baseUrl, authorization, request)` are available as common Ktor `suspend` APIs for A1111 models, samplers, and server options.
  - `Automatic1111GenerationApi.healthCheck(baseUrl, authorization)`, `Automatic1111GenerationApi.textToImage(baseUrl, authorization, request)`, `Automatic1111GenerationApi.imageToImage(baseUrl, authorization, request)`, and `Automatic1111GenerationApi.interrupt(baseUrl, authorization)` are available as common Ktor `suspend` APIs for A1111 connectivity, txt2img/img2img generation, and cancellation.
  - `data/commonMain` now contains Ktor-backed remote data sources and raw-to-domain mappers for supporters, HuggingFace models, HuggingFace generation, Stability AI engines/generation/credits, Horde generation/status, OpenAI generation, Swarm UI models, Swarm UI generation, A1111 generation, A1111 models/samplers/server configuration, A1111 loras/embeddings/hypernetworks, and Swarm UI loras/embeddings.
  - Android still uses the existing Retrofit/Rx data sources and repositories until each workflow is explicitly rewired to the coroutine path; supporters, HuggingFace models/generation, Stability AI engines/generation/credits, Horde generation/status, OpenAI generation, Swarm UI models/generation, A1111 generation/connectivity/interrupt, A1111 models/samplers/server configuration, loras, embeddings, and hypernetworks are now rewired to shared Ktor/coroutine contracts where applicable.
- Added KMP Room/SQLite dependencies and iOS KSP wiring in `storage` while keeping the current Android Room/Rx DAO implementation in `androidMain`.
- Added the first common presentation screen logic slice:
  - `DispatchersProvider` moved to `core:common/commonMain`;
  - `DonateState`, `DonateIntent`, `DonateEffect`, and `DonateViewModel` moved to `presentation/commonMain`;
  - `DonateScreenContent` and `SupporterItem` moved to `presentation/commonMain`;
  - pure Compose helpers such as `fadedEdge`, `gesturesDisabled`, and `measureTextWidth` moved to `core:ui/commonMain`;
  - Android `DonateViewModel` is now a thin AndroidX lifecycle wrapper around the common `DonateViewModel`;
  - Android Donate UI uses the new common `MviComponent` and passes Android string/drawable resources into the shared layout through parameters/slots;
  - `DonateViewModel` now depends on a common `DonateRouter` contract instead of a navigation lambda; Android `MainRouter` implements that contract;
  - common Koin modules were added for the first shared path: `KtorSdaiAppApi`, Ktor supporters/HuggingFace data sources, common use cases, shared providers, and `DonateViewModel`;
  - iOS `AiSdPresentation` now starts the common Koin graph and renders Donate through the real common `DonateViewModel`/Ktor supporters path;
  - Android supporter loading now uses the shared coroutine use case and an Android cached repository implementation instead of an Rx-backed coroutine adapter.
- Added the first common DebugMenu UI slice:
  - `DebugMenuScreenContent`, strings, state, and UI actions now live in `presentation/commonMain`;
  - Android `DebugMenuScreen` is a thin wrapper that maps existing Android Rx MVI state/intents/effects to the shared content;
  - Android WorkManager, toast, modal, scheduler, and Local Diffusion debug actions remain Android-only;
  - iOS can reuse the common DebugMenu content later with WorkManager/Local Diffusion sections hidden or disabled.
- Added a common Logger UI slice:
  - `LoggerScreenContent`, strings, state, and UI actions now live in `presentation/commonMain`;
  - Android `LoggerScreen` is a thin wrapper that maps the existing Android log reader state/intents to the shared content;
  - Android file-log reading and router handling remain in the Android `LoggerViewModel`;
  - iOS can reuse the common Logger content later with an iOS log source or an explicit empty/unsupported state.
- Added more common stateless presentation slices:
  - `ConfigurationLoaderScreenContent` now lives in `presentation/commonMain`; Android maps its existing `UiText` state to plain `statusText`;
  - `SplashScreenContent` now lives in `presentation/commonMain`; Android `SplashScreen` remains a thin ViewModel wrapper;
  - `HomeNavigationBar` now lives in `presentation/commonMain`; Android `HomeNavigationScreen` keeps AndroidX `NavHost`/`NavController` and maps current `NavItem`s into common bar items.
- Added common drawer/error UI pieces:
  - `DrawerSheetContent` now lives in `presentation/commonMain`; Android `DrawerScreen` keeps AndroidX back stack state, Android logo/resources, Koin `DrawerViewModel`, and root/home navigation callbacks;
  - `ErrorComposableContent` now lives in `presentation/commonMain`; Android `ErrorComposable` keeps the existing `ErrorState`/`UiText` mapping.
- Added common dialog/connectivity/settings widget slices:
  - `InfoDialogContent`, `ErrorDialogContent`, `DecisionInteractiveDialogContent`, and progress dialog content now live in `presentation/commonMain`; Android keeps existing `UiText`/`@StringRes` public wrappers;
  - `ConnectivityComposableContent` and `ConnectivityStatus` now live in `presentation/commonMain`; Android maps resources/colors into common UI;
  - `Modifier.shimmer()` moved to `core:ui/commonMain`;
  - `SettingsHeaderContent`, string-based `SettingsItem`, and string-based `SettingsItemContent` now live in `presentation/commonMain`; Android keeps existing `UiText`/`ImageVector` wrappers.
- Converted the connectivity status path from Rx to coroutine/KMP contracts:
  - `ServerConnectivityGateway` and `ObserveSeverConnectivityUseCase` now live in `domain/commonMain` and expose `Flow<Boolean>`;
  - shared no-op connectivity/monitor bindings were added for the iOS shared graph;
  - Android `ConnectivityMonitor` and `ServerConnectivityGatewayImpl` now expose `Flow` instead of Rx streams;
  - `ConnectivityState` and `ConnectivityViewModel` now live in `presentation/commonMain`;
  - Android `ConnectivityViewModel` is now an AndroidX lifecycle wrapper around the common `ConnectivityViewModel`;
  - Android `PreferenceManager.observe()` remains legacy Rx for now, wrapped behind small Flow-based monitor-connectivity use cases to avoid migrating the full settings subsystem in one risky step.
- Converted the splash/navigation bootstrap path to coroutine/KMP contracts:
  - `SplashNavigationUseCase` and the preference-based `SplashNavigationUseCaseImpl` now live in `domain/commonMain` as a `suspend` contract;
  - Android and iOS/shared both use the same onboarding/setup/home decision logic from common code;
  - `SplashViewModel` now lives in `presentation/commonMain`;
  - Android `SplashViewModel` is now an AndroidX lifecycle wrapper around the common `SplashViewModel`;
  - navigation is routed through a common `SplashRouter` contract, with Android adapting it to the existing `MainRouter`.
- Converted the post-splash configuration loader path to coroutine/KMP contracts:
  - `DataPreLoaderUseCase` now lives in `domain/commonMain` as a `suspend` contract;
  - Android keeps the current mixed legacy Rx/coroutine repository chain behind the Android implementation by blocking only inside the Android adapter boundary;
  - iOS/shared Koin gets a no-op preloader default;
  - `ConfigurationLoaderState` and `ConfigurationLoaderViewModel` now live in `presentation/commonMain`;
  - Android `ConfigurationLoaderViewModel` is now an AndroidX lifecycle wrapper around the common `ConfigurationLoaderViewModel`;
  - loader navigation is routed through a common `ConfigurationLoaderRouter` contract, with Android adapting it to the existing `MainRouter`.
- Converted the Android navigation router event bus from Rx to coroutine contracts:
  - Android `Router.observe()` now exposes `Flow<T>` instead of Rx `Observable<T>`;
  - `MainRouterImpl`, `DrawerRouterImpl`, and `HomeRouterImpl` use hot `MutableSharedFlow` streams and keep the existing router-based navigation shape;
  - `AiStableDiffusionViewModel` and `HomeNavigationViewModel` collect router effects through `viewModelScope`;
  - remaining Rx work in these screens is now limited to still-unmigrated Android data/use-case contracts outside the router event bus.
- Converted the Android generation form event bridge from Rx to coroutine contracts:
  - `GenerationFormUpdateEvent` now exposes `Flow` and uses `MutableSharedFlow`/`MutableStateFlow` instead of Rx `PublishSubject`/`BehaviorSubject`;
  - `HomeNavigationViewModel`, `TextToImageViewModel`, and `ImageToImageViewModel` collect form/navigation events through `viewModelScope`;
  - `AiGenerationResult` now lives in `domain/commonMain`;
  - `AiGenerationResult.createdAt` now uses epoch millis as `Long`, while Android Room continues to use the existing `Date` entity field behind mapper conversion, preserving the database boundary;
  - `GenerationFormUpdateEvent` now lives in `presentation/commonMain` and is registered in the shared Koin presentation graph.
- Converted the Android InPaint state bridge from Rx to coroutine contracts:
  - `InPaintStateProducer` now exposes hot `Flow` streams backed by `MutableSharedFlow` instead of `BehaviorSubject`/`Flowable`;
  - `InPaintViewModel` and `ImageToImageViewModel` collect InPaint updates through `viewModelScope`;
  - the feature remains Android-only for now because the bridge still carries `android.graphics.Bitmap`.
- Converted `HomeNavigationViewModel` delayed preference refresh from an Rx `Observable.timer()` subscription to `viewModelScope` + `delay()`; `PreferenceManager.refresh()` itself remains an Android Rx boundary until the settings/preferences contract is migrated.
- Added Android presentation `Flowable.asCoroutineFlow()`, `Single.asCoroutineFlow()`, and `Completable.asCoroutineFlow()` adapters for legacy Rx streams and used them to move more observers/actions into `viewModelScope`:
  - `AiStableDiffusionViewModel` now collects drawer-item settings updates as `Flow`;
  - `AiSdAppThemeViewModel` now collects theme settings updates as `Flow`;
  - `DebugMenuViewModel` now collects Local Diffusion debug settings updates as `Flow`;
  - `GalleryViewModel` now collects gallery-grid settings and background-work refresh events as `Flow`;
  - `BackgroundWorkViewModel` now collects status/result streams with coroutine `combine`, and decodes notification preview images through the `Single` adapter instead of owning a `SchedulersProvider` subscription;
  - `DownloadDialogViewModel` now loads local model source metadata through the `Single` adapter on the coroutine IO dispatcher;
  - `ExtrasViewModel` now loads extras from `viewModelScope`; the loras and HyperNetworks branches use suspend use cases after the metadata workflow migration;
  - `EngineSelectionViewModel` now uses coroutine `combine` for the engine/model selector state and a coroutine action path for A1111 model selection;
  - Historical note: this was later superseded by the common `SettingsViewModel`; Settings now combines app version, SD models, settings, and Stability credits from `presentation/commonMain`, and cache clearing uses the common suspend `ClearAppCacheUseCase` with platform `AppCacheCleaner`;
  - `DebugMenuViewModel` now runs the debug bad-base64 insert through the `Completable` adapter;
  - `ReportViewModel` now loads report image data and sends reports from `viewModelScope`; Android `Bitmap` conversion remains an Android-only imageprocessing boundary;
  - underlying `PreferenceManager`, `BackgroundWorkObserver`, imageprocessing converters, model repositories, report/cache repositories, gallery paging/export/delete, and debug insert contracts remain Android/Rx boundaries until their modules are migrated.
- Converted the HuggingFace models workflow from the Android Rx chain to common coroutine contracts:
  - `HuggingFaceModelsRepository` now lives in `domain/commonMain` with `suspend` methods;
  - shared/iOS Koin uses a remote-only Ktor repository that falls back to `HuggingFaceModel.default` on network failure;
  - Android Koin binds a cached repository implementation that fetches through the shared Ktor remote data source, saves into the existing Room table, and falls back to local cache on remote/save failure;
  - Android `HuggingFaceModelDao` and `HuggingFaceModelsDataSource.Local` were converted from Rx `Single`/`Completable` APIs to Room `suspend` APIs for this DAO;
  - `FetchHuggingFaceModelsUseCase` is now the shared use case used by Android presentation and iOS/shared graph; the old Android-only `FetchAndGetHuggingFaceModelsUseCase` was removed;
  - `EngineSelectionViewModel` and `ServerSetupViewModel` now call the shared suspend use case for HuggingFace models instead of a ViewModel-level Rx adapter.
- Converted the supporters/Donate loading workflow from the Android Rx chain to common coroutine contracts:
  - `SupportersRepository` now lives in `domain/commonMain` with `suspend` methods;
  - shared/iOS Koin uses a remote-only Ktor repository that returns an empty list on network failure;
  - Android Koin binds a cached repository implementation that fetches through the shared Ktor remote data source, saves into the existing Room table, and falls back to local cache on remote/save failure;
  - Android `SupporterDao` and `SupportersDataSource.Local` were converted from Rx `Single`/`Completable` APIs to Room `suspend` APIs for this DAO;
  - `FetchSupportersUseCase` is now backed by the shared repository contract on both Android and iOS; the old Android-only `FetchAndGetSupportersUseCase` and Rx await adapter were removed;
  - `DonateViewModel`/`DonateViewModel` continue to use the same shared suspend use case with Android cache behavior preserved below the domain boundary.
- Converted the Stability AI engines loading workflow from the Android Rx chain to common coroutine contracts:
  - `StabilityAiEnginesRepository` now lives in `domain/commonMain` with a `suspend` API that accepts an explicit API key;
  - shared/iOS Koin now has `KtorStabilityAiEnginesApi`, `KtorStabilityAiEnginesRemoteDataSource`, `RemoteStabilityAiEnginesRepository`, and `FetchStabilityAiEnginesUseCase`;
  - Android Koin binds the same common repository/remote path, while the Android-only `FetchAndGetStabilityAiEnginesUseCase` remains as a thin suspend wrapper around `PreferenceManager` to preserve current default engine-id selection behavior;
  - the old Android-only `StabilityAiEnginesDataSource`, Rx remote data source, and Rx repository implementation were removed;
  - `EngineSelectionViewModel` now loads Stability AI engines through the suspend use case instead of `Single.asCoroutineFlow()`.
- Converted the Swarm UI model selector workflow from the Android Rx chain to common coroutine contracts:
  - `SwarmUiModelsApi`, `SwarmUiModelsRemoteDataSource`, and `FetchSwarmUiModelsUseCase` now live in common source sets and accept explicit base URL, session ID, and authorization credentials so iOS can call the same provider path;
  - shared/iOS Koin now binds the Ktor Swarm UI models remote data source and common use case;
  - Android `SwarmUiModelsRepositoryImpl` now uses the common Ktor remote data source, keeps the existing Room cache, preserves local-cache fallback on remote/save failure, and renews `SessionPreference.swarmUiSessionId` on empty/bad sessions;
  - Android `SwarmUiModelDao` and `SwarmUiModelsDataSource.Local` were converted from Rx `Single`/`Completable` APIs to Room `suspend` APIs for this DAO;
  - the old Android-only `SwarmUiModelsDataSource.Remote` contract and Rx `SwarmUiModelsRemoteDataSource` implementation were removed;
  - `EngineSelectionViewModel` now loads Swarm UI models through the suspend use case instead of `Single.asCoroutineFlow()`;
  - Swarm generation now reuses the same common session renewal path, so the legacy Android/Rx `SwarmUiSessionDataSource` was removed in the generation workflow migration.
- Converted the A1111/Swarm loras and embeddings workflows from Android Rx chains to common coroutine contracts:
  - `LorasDataSource`, `EmbeddingsDataSource`, `LorasRepository`, `EmbeddingsRepository`, `FetchAndGetLorasUseCase`, and `FetchAndGetEmbeddingsUseCase` now live in `domain/commonMain` with `suspend` APIs;
  - shared/iOS Koin now binds common Ktor remote data sources for A1111 loras/embeddings and Swarm UI loras/embeddings;
  - Android `LorasRepositoryImpl` and `EmbeddingsRepositoryImpl` now select A1111 vs Swarm from the existing Android `PreferenceManager`, use `AuthorizationStore` credentials, preserve local-cache fallback in `fetchAndGet*`, and renew Swarm sessions through the common Ktor session path on empty/bad sessions;
  - Android `StableDiffusionLoraDao`, `StableDiffusionEmbeddingDao`, `LorasDataSource.Local`, and `EmbeddingsDataSource.Local` were converted from Rx `Single`/`Completable` APIs to Room `suspend` APIs for these DAOs;
  - old Android-only Retrofit/Rx remote data sources for A1111 loras/embeddings and Swarm UI loras/embeddings were removed;
  - `EmbeddingViewModel` now calls the suspend embeddings use case directly, and `ExtrasViewModel` calls the suspend loras use case directly;
  - Android `DataPreLoaderUseCaseImpl` now calls loras/embeddings as suspend repositories inside the existing Android preloader boundary.
- Converted the A1111 HyperNetworks workflow from an Android Rx chain to common coroutine contracts:
  - `StableDiffusionHyperNetworksDataSource`, `StableDiffusionHyperNetworksRepository`, and `FetchAndGetHyperNetworksUseCase` now live in `domain/commonMain` with `suspend` APIs;
  - shared/iOS Koin now binds the common Ktor A1111 HyperNetworks remote data source;
  - Android `StableDiffusionHyperNetworksRepositoryImpl` uses the existing `PreferenceManager` A1111 URL and `AuthorizationStore` credentials, preserves local-cache fallback in `fetchAndGetHyperNetworks`, and no-ops for non-A1111 sources because HyperNetworks are not exposed by the Swarm UI feature set;
  - Android `StableDiffusionHyperNetworkDao` and `StableDiffusionHyperNetworksDataSource.Local` were converted from Rx `Single`/`Completable` APIs to Room `suspend` APIs;
  - the old Android-only Retrofit/Rx HyperNetworks remote data source was removed;
  - `ExtrasViewModel` now calls both loras and HyperNetworks suspend use cases directly, removing its last extras-specific Rx adapter call;
  - Android `DataPreLoaderUseCaseImpl` now calls HyperNetworks as a suspend repository inside the existing Android preloader boundary.
- Converted the Swarm UI generation workflow from Android Rx/Retrofit/Bitmap data sources to common coroutine/Ktor contracts:
  - `TextToImagePayload`, `ImageToImagePayload`, `OpenAiModel`, Stability AI style/clip enums, `SwarmUiGenerationDataSource`, and `SwarmUiGenerationRepository` now live in `domain/commonMain` with `suspend` APIs;
  - common `KtorSwarmUiGenerationRemoteDataSource` maps txt2img/img2img payloads to Swarm requests, downloads generated image bytes through Ktor, and base64-encodes them in common code without Android `Bitmap`;
  - shared/iOS Koin binds the common Ktor generation API and remote data source;
  - Android `SwarmUiGenerationRepositoryImpl` now uses the common session/model/generation Ktor path, preserves the existing generated-result persistence/media-store behavior below the Android boundary, and retries once with a renewed session on `SwarmUiBadSessionException`;
  - old Android-only `SwarmUiGenerationRemoteDataSource`, `SwarmUiSessionDataSourceImpl`, and their domain contracts were removed;
  - Android multi-provider text/image generation now routes through common suspend use-case contracts; `TestSwarmUiConnectivityUseCaseImpl` also uses the shared suspend setup/connectivity contract.
- Converted the OpenAI text-to-image workflow from Android Rx/Retrofit/interceptor data sources to common coroutine/Ktor contracts:
  - `OpenAiGenerationDataSource`, `OpenAiGenerationRepository`, `OpenAiQuality`, `OpenAiSize`, and `OpenAiStyle` now live in `domain/commonMain` with `suspend` APIs where applicable;
  - common `OpenAiGenerationApi` uses explicit API-key parameters for validation and image generation so iOS can call the same network path without Android interceptors/preferences;
  - common `KtorOpenAiGenerationRemoteDataSource` maps `TextToImagePayload` to OpenAI requests and maps base64 responses to `AiGenerationResult`;
  - shared/iOS Koin binds the common Ktor OpenAI API and remote data source;
  - Android `OpenAiGenerationRepositoryImpl` now reads `PreferenceManager.openAiApiKey`, uses the common remote data source, and preserves existing generated-result persistence/media-store behavior below the Android boundary;
  - old Android-only Retrofit `OpenAiApi`, OpenAI request/response/image DTOs, Rx remote data source, and Android-only domain contracts were removed;
  - Android `TextToImageUseCaseImpl` now exposes the common suspend generation contract; `TestOpenAiApiKeyUseCaseImpl` uses the shared suspend setup/connectivity contract.
- Converted the HuggingFace txt2img/img2img workflow from Android Rx/Retrofit/Bitmap data sources to common coroutine/Ktor contracts:
  - `HuggingFaceGenerationDataSource` and `HuggingFaceGenerationRepository` now live in `domain/commonMain` with `suspend` APIs;
  - common `HuggingFaceGenerationApi` keeps explicit API-key parameters and separate HuggingFace API/inference base URLs so iOS can validate keys and generate images through the same network path;
  - common `KtorHuggingFaceGenerationRemoteDataSource` maps `TextToImagePayload`/`ImageToImagePayload` to typed HuggingFace request DTOs, base64-encodes returned image bytes in common code, and maps them to `AiGenerationResult` without Android `Bitmap`;
  - shared/iOS Koin binds the common Ktor HuggingFace generation API and remote data source;
  - Android `HuggingFaceGenerationRepositoryImpl` now reads `PreferenceManager.huggingFaceApiKey` and `PreferenceManager.huggingFaceModel`, uses the common remote data source, and preserves existing generated-result persistence/media-store behavior below the Android boundary;
  - old Android-only Retrofit `HuggingFaceApi`, `HuggingFaceInferenceApi`, HuggingFace generation request/error DTOs, Bitmap remote data source, and Android-only domain contracts were removed;
  - Android `TextToImageUseCaseImpl` and `ImageToImageUseCaseImpl` now expose common suspend generation contracts; `TestHuggingFaceApiKeyUseCaseImpl` uses the shared suspend setup/connectivity contract.
- Converted the Stability AI txt2img/img2img and remote credits workflow from Android Rx/Retrofit/Gson/Bitmap preparation to common coroutine/Ktor contracts:
  - `StabilityAiGenerationDataSource`, `StabilityAiCreditsRemoteDataSource`, and `StabilityAiGenerationRepository` now live in `domain/commonMain` with `suspend` APIs;
  - common `StabilityAiGenerationApi` uses explicit API-key/engine-id parameters, typed JSON DTOs, and multipart img2img upload with common base64-to-`ByteArray` decoding;
  - common `KtorStabilityAiGenerationRemoteDataSource` maps txt2img/img2img payloads to Stability AI requests and maps returned base64 artifacts to `AiGenerationResult` without Android `Bitmap` conversion;
  - common `KtorStabilityAiCreditsRemoteDataSource` fetches credits through the same Ktor API path;
  - shared/iOS Koin binds the common Ktor Stability AI generation API and remote data sources;
  - Android `StabilityAiGenerationRepositoryImpl` now reads `PreferenceManager.stabilityAiApiKey` and `PreferenceManager.stabilityAiEngineId`, uses the common remote data source, preserves existing generated-result persistence/media-store behavior below the Android boundary, and refreshes credits best-effort after generation;
  - old Android-only Retrofit `StabilityAiApi`, `StabilityAiErrorMapper`, generation/credits DTOs, Rx generation/credits remote data sources, and Android-only generation domain contracts were removed;
  - Android `TextToImageUseCaseImpl` and `ImageToImageUseCaseImpl` now expose common suspend generation contracts; `TestStabilityAiApiKeyUseCaseImpl` uses the shared suspend setup/connectivity contract.
- Converted the Stability AI credits observation/cache workflow from Android Rx to common coroutine contracts:
  - `StabilityAiCreditsDataSource.Local`, `StabilityAiCreditsRepository`, and `ObserveStabilityAiCreditsUseCase` now live in `domain/commonMain` with `suspend`/`Flow` APIs;
  - common `RemoteStabilityAiCreditsRepository` was added for shared/iOS Koin, returning a safe `0f` state until an API-key provider is wired into the iOS settings path;
  - Android `StabilityAiCreditsLocalDataSource` now uses `MutableStateFlow` instead of `BehaviorSubject`/`Flowable`;
  - Android `StabilityAiCreditsRepositoryImpl` now calls the common Ktor credits remote directly from suspend code and preserves the existing source guard plus local fallback behavior;
  - Android `StabilityAiGenerationRepositoryImpl` refreshes credits through the suspend local data source after generation;
  - Settings now collects Stability credits as a direct `Flow` from the common `SettingsViewModel` and no longer uses `asCoroutineFlow()` for this path.
- Converted the Horde txt2img/img2img/status/cancel workflow from Android Rx/Retrofit/BitmapFactory/URL polling to common coroutine/Ktor contracts:
  - `HordeGenerationDataSource`, `HordeGenerationRepository`, and `ObserveHordeProcessStatusUseCase` now live in `domain/commonMain` with `suspend`/`Flow` APIs;
  - common `HordeGenerationApi` accepts explicit API-key parameters and covers async generation, polling, full status retrieval, API-key validation, cancellation, and image download;
  - common `KtorHordeGenerationRemoteDataSource` performs polling with `delay`, emits `HordeProcessStatus` through a common `MutableSharedFlow` status source, and base64-encodes downloaded bytes without Android `Bitmap`;
  - shared/iOS Koin binds the common Ktor Horde generation API, status source, and remote data source;
  - Android `HordeGenerationRepositoryImpl` reads `PreferenceManager.hordeApiKey` with the existing default key fallback, uses the common remote data source, and preserves generated-result persistence/media-store behavior below the Android boundary;
  - Android presentation and WorkManager tasks collect Horde status as `Flow`; Local Diffusion status remains Android/Rx and Android-only;
  - old Android-only Retrofit `HordeRestApi`, Horde DTOs, Bitmap/URL remote data source, and Android-only Horde domain contracts were removed;
  - Android `TextToImageUseCaseImpl`, `ImageToImageUseCaseImpl`, and `InterruptGenerationUseCaseImpl` now expose common suspend generation/cancel contracts; `TestHordeApiKeyUseCaseImpl` uses the shared suspend setup/connectivity contract.
- Converted the Automatic1111 txt2img/img2img/connectivity/interrupt workflow from Android Rx/Retrofit generation endpoints to common coroutine/Ktor contracts:
  - `StableDiffusionGenerationDataSource` and `StableDiffusionGenerationRepository` now live in `domain/commonMain` with `suspend` APIs;
  - common `Automatic1111GenerationApi` accepts explicit base URL and optional Basic auth and covers health check, txt2img, img2img, and interrupt;
  - A1111 generation request/response DTOs moved to `network/commonMain` with kotlinx serialization;
  - common `KtorStableDiffusionGenerationRemoteDataSource` maps generation payloads, parses seed/subseed info with kotlinx serialization, and emits `AiGenerationResult` without Android APIs;
  - shared/iOS Koin binds the common Ktor A1111 generation API and remote data source;
  - Android `StableDiffusionGenerationRepositoryImpl` reads `PreferenceManager.automatic1111ServerUrl` and `AuthorizationStore`, preserves demo mode plus generated-result persistence/media-store below the Android boundary;
  - old Android-only A1111 Retrofit generation methods, Rx remote data source, Android-only generation DTOs, and Rx domain generation contracts were removed;
  - Android generation use cases expose common suspend public contracts; A1111 setup connectivity uses the shared suspend setup/connectivity contract.
- Converted the Automatic1111 models/samplers/server configuration workflow from Android Rx/Retrofit/Room-Rx to common coroutine/Ktor contracts:
  - `StableDiffusionModelsDataSource`, `StableDiffusionSamplersDataSource`, `ServerConfigurationDataSource`, and their repositories now live in `domain/commonMain` with `suspend` APIs;
  - `GetStableDiffusionModelsUseCase`, `GetStableDiffusionSamplersUseCase`, and `SelectStableDiffusionModelUseCase` now use common suspend contracts;
  - common `Automatic1111MetadataApi` covers A1111 models, samplers, options fetch, and options update with explicit base URL and optional Basic auth;
  - common Ktor DTOs/mappers/remotes map models, samplers, and server configuration without Android APIs;
  - Android cached repositories read `PreferenceManager.automatic1111ServerUrl` and `AuthorizationStore`, preserve cache fallback semantics for models/config, and write through the existing Room cache;
  - Android Room DAOs/local data sources for A1111 models, samplers, and server configuration were converted from Rx return types to suspend APIs;
  - old Android-only Retrofit/Rx remote data sources and Retrofit endpoints for models/samplers/options were removed;
  - Android generation/settings/presentation callers now invoke these use cases directly from coroutines instead of `Single.asCoroutineFlow()` adapters.
- Converted the setup remote-provider connection/configuration workflow from Android Rx chains to suspend/common contracts:
  - `GetConfigurationUseCase`, `SetServerConfigurationUseCase`, remote `ConnectTo*UseCase` contracts, `PingStableDiffusionServiceUseCase`, provider API-key test use cases, A1111 connectivity test, and Swarm UI connectivity test now live in `domain/commonMain` with `suspend` APIs;
  - Android implementations still use the existing `PreferenceManager` and `AuthorizationStore`, preserving current preference keys and Basic auth storage while exposing coroutine contracts to shared/iOS callers;
  - Android A1111 and Swarm connection use cases now call suspend connectivity repositories directly, keep the existing 30-second timeout behavior, and roll back the previous configuration on failure;
  - Android A1111 connection keeps the existing 5-second post-connect preload delay and retry behavior, now implemented with coroutines instead of Rx timers/retry operators;
  - Horde, HuggingFace, OpenAI, and Stability AI setup flows now validate keys through direct suspend provider checks instead of `Single` wrappers around `runBlocking`;
  - Local Diffusion and MediaPipe setup connection use cases remain Android-only, but their Android contracts are now suspend so `ServerSetupViewModel` can use one coroutine call-chain without Rx adapters;
  - `ServerSetupViewModel` now performs setup connection through `viewModelScope` and the existing `MainRouter`, and `EngineSelectionViewModel` now reads configuration through direct suspend calls instead of `Single.asCoroutineFlow()`.
- Added the shared/iOS setup configuration persistence foundation:
  - `ConfigurationStore` now lives in `domain/commonMain` as the common remote-provider configuration boundary used by shared setup code;
  - `data/commonMain` now provides key-value backed `ConfigurationStore` and `AuthorizationStore` implementations for the shared graph;
  - `data/iosMain` persists shared configuration/auth values with `NSUserDefaults`, giving iOS real provider/source/API-key storage before the setup UI moves to common;
  - Android app runtime still uses the existing Android `PreferenceManager` and encrypted auth module, preserving current Android preference keys and credential storage behavior;
  - shared/iOS Koin now binds `GetConfigurationUseCase`, `SetServerConfigurationUseCase`, A1111/Swarm/Horde/HuggingFace/OpenAI/Stability remote connect use cases, and their Ktor-backed connectivity/API-key checks;
  - Local Diffusion and MediaPipe setup bindings remain absent from the shared/iOS graph by design.
- Added the first shared/iOS remote setup presentation slice:
  - `ServerSetupState`, `ServerSetupIntent`, `ServerSetupEffect`, and `ServerSetupViewModel` now live in `presentation/commonMain`;
  - setup navigation uses a common `ServerSetupRouter` contract, keeping the existing router-based architecture instead of lambdas in the store;
  - `ServerSetupViewModel` uses common validators plus shared configuration/connect use cases for A1111, Swarm UI, Horde, HuggingFace, OpenAI, and Stability AI;
  - `ServerSetupContent` now provides a Compose Multiplatform setup UI for remote providers, localhost confirmation, validation errors, and provider info links;
  - iOS `AiSdPresentation` now renders setup through the common Koin graph instead of the Donate placeholder;
  - Local Diffusion and MediaPipe are hidden from the shared/iOS setup source list by default, while Android keeps its existing Android-only setup screen and local setup behavior.
- Added a first shared/iOS navigation shell:
  - `RootAppRouter` now owns a common route `StateFlow` and implements the existing splash/config-loader/setup router contracts plus a small home router contract;
  - iOS setup success now navigates to the shared configuration loader route, and the loader then navigates to a common home route;
  - `HomeViewModel` and `HomeScreenContent` provide a minimal common home surface that reads persisted configuration and can route back to setup;
  - loader/home ViewModels are created only when their route is active, preventing preload side effects from bypassing setup at app startup.
- Converted the Android multi-provider generation presentation/work boundary from Rx use cases to coroutine contracts:
  - `TextToImageUseCase`, `ImageToImageUseCase`, and `InterruptGenerationUseCase` now live in `domain/commonMain` and expose `suspend` APIs;
  - Android `TextToImageUseCaseImpl`, `ImageToImageUseCaseImpl`, and `InterruptGenerationUseCaseImpl` call suspend repositories directly for A1111, Swarm UI, Horde, HuggingFace, OpenAI, and Stability AI without `runBlocking` or `Single` wrappers;
  - Android Local Diffusion and MediaPipe generation repositories now expose suspend contracts to the domain use cases while preserving their Android-only Rx internals for the out-of-scope local engine implementation;
  - `TextToImageViewModel` and `ImageToImageViewModel` now run foreground generation as cancellable coroutine `Job`s instead of Rx `Disposable`s, preserving wakelock, modal, notification, and router behavior;
  - WorkManager generation tasks now call suspend generation use cases through a cancellable worker-boundary bridge; the broader `Rx3Worker` infrastructure remains Android-only legacy until the work module is refactored separately;
  - domain generation tests were rewritten around `runTest`/`coEvery`, covering provider routing, batch repetition, failure propagation, and interrupt no-op behavior.
- Added the first shared/iOS text-to-image generation route:
  - common `TextToImageUseCaseImpl` routes foreground remote txt2img generation through the existing common Ktor data sources for A1111, Swarm UI, Horde, HuggingFace, OpenAI, and Stability AI;
  - Android keeps its existing Android-only `TextToImageUseCaseImpl` binding, preserving persistence, media-store, wakelock, notifications, WorkManager, Local Diffusion, and MediaPipe behavior;
  - shared/iOS Koin now binds the common remote-only `TextToImageUseCase`;
  - `TextToImageViewModel`, state, intent, router contract, and Compose screen content now live in `presentation/commonMain` and use coroutines end-to-end;
  - shared navigation remains router-based: Home routes to txt2img through `HomeRouter`, and txt2img routes back/setup through `TextToImageRouter`;
  - generated base64 image rendering is shared at the UI level with platform `expect/actual` decoders: Android uses `BitmapFactory`, iOS uses Skia;
  - shared txt2img result cards expose common save/share actions backed by platform `ImageSaver` and `ImageSharer` contracts: iOS saves generated images to Photos with add-only permission and opens the native share sheet, while Android keeps the existing Android MediaStore/share flows unchanged;
  - Local Diffusion and MediaPipe remain Android-only for this milestone and are hidden from shared/iOS setup by default.
- Expanded the shared generation presentation route toward Android parity:
  - common `TextToImageState` now implements the same `GenerationInputFormState` as the Android generation form and keeps the full prompt, negative prompt, dimensions, sampler, seed, subseed, CFG, restore faces, NSFW, OpenAI, Stability AI, advanced visibility, and batch state;
  - common `TextToImageContent` now renders the shared `GenerationInputForm` instead of the earlier reduced form, and `PromptTagEditRequest` plus common `EditTagDialog` handle tagged prompt editing without Android-only modal classes;
  - common txt2img payload mapping now preserves OpenAI model/size/quality/style, Stability AI style/clip guidance, samplers, seeds, restore faces, NSFW, and provider-specific batch behavior;
  - common `TextToImageViewModel` now observes `PreferenceManager.observe()` for provider/form flags, loads A1111/Stability samplers from the common sampler use case, and is registered in the shared Koin presentation graph;
  - common `ImageToImageViewModel` now follows the same preferences/samplers/tag-edit path, so txt2img and img2img no longer diverge in shared form behavior;
  - `GenerationPlatformServices` isolates notifications and wakelock from common ViewModels: shared/iOS uses no-op services, Android has an implementation backed by `PushNotificationManager` and `WakeLockInterActor`;
  - common txt2img/img2img foreground generation now calls platform services for wakelock and success/failure notifications, and common background scheduling uses `BackgroundTaskManager`/`BackgroundWorkObserver` with Android WorkManager bindings and iOS no-op bindings;
  - Local Diffusion remains Android-only at the repository/DI boundary; the shared setup still hides it for iOS, and the common ViewModel no longer hard-blocks local provider at the UI layer.

Latest generation presentation checkpoint gate:

- `./gradlew :domain:compileKotlinIosSimulatorArm64 :domain:compileDebugKotlinAndroid :data:compileKotlinIosSimulatorArm64 :data:compileDebugKotlinAndroid :presentation:compileKotlinIosSimulatorArm64 :presentation:compileDebugKotlinAndroid :app:assembleFullDebug --no-daemon`
- `./gradlew :presentation:linkDebugFrameworkIosSimulatorArm64 :presentation:testDebugUnitTest --no-daemon` links the iOS framework successfully; the earlier presentation unit-test debt was later fixed by making localized expectations explicit, replacing Android bitmap setup in JVM tests with a mocked common `ImageBitmap`, and using finite test flows for observer tests.

- Switched Android txt2img/img2img tabs to the common generation presentation path:
  - Android `TextToImageScreen` and `ImageToImageScreen` are now thin wrappers that construct common `TextToImageViewModel`/`ImageToImageViewModel` and render common `TextToImageContent`/`ImageToImageContent`;
  - Android generation navigation remains router-based through `TextToImageRouterImpl` and `ImageToImageRouterImpl`, both delegating to `MainRouter` with `LaunchSource.SETTINGS` for provider setup;
  - Android Koin now binds `ImageSaver`, `ImageSharer`, `DebugMenuAccessor`, `TextToImageRouter`, and `ImageToImageRouter` for common presentation screens without pulling shared no-op router bindings into the Android app graph;
  - `BackgroundWorkViewModel` now clears title/subtitle/error/image state on dismiss, matching the hidden-state test expectation.

Latest Android/common generation route gate:

- `./gradlew :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :presentation:compileDebugKotlinAndroid :presentation:compileKotlinIosSimulatorArm64 --no-daemon`
- `./gradlew :app:assembleFullDebug --no-daemon`

- Moved `TimeProvider` to `core:common/commonMain` after removing the unused Android-only `currentDate()` contract.
- Hardened `MediaStoreGatewayFactoryTest` by injecting the SDK media-store capability check instead of mutating `Build.VERSION.SDK_INT` through reflection.
- Android Local Diffusion implementation behavior was not changed; only Gradle/source-set boundaries were moved.

Verified gates at this checkpoint:

- `./gradlew :core:validation:testDebugUnitTest :core:validation:compileKotlinIosSimulatorArm64 --no-daemon`
- `./gradlew :core:common:testDebugUnitTest :core:common:compileKotlinIosSimulatorArm64 --no-daemon`
- `./gradlew :core:localization:compileKotlinIosSimulatorArm64 :app:assembleFullDebug --no-daemon`
- `./gradlew :domain:compileKotlinIosSimulatorArm64 :domain:testDebugUnitTest :app:assembleFullDebug --no-daemon`
- `./gradlew :feature:auth:compileKotlinIosSimulatorArm64 :app:assembleFullDebug --no-daemon`
- `./gradlew :network:compileKotlinIosSimulatorArm64 :network:testDebugUnitTest :app:assembleFullDebug --no-daemon`
- `./gradlew :core:imageprocessing:compileKotlinIosSimulatorArm64 :app:assembleFullDebug --no-daemon`
- `./gradlew :core:notification:compileKotlinIosSimulatorArm64 :app:assembleFullDebug --no-daemon`
- `./gradlew :demo:compileKotlinIosSimulatorArm64 :demo:testDebugUnitTest :app:assembleFullDebug --no-daemon`
- `./gradlew :feature:work:compileKotlinIosSimulatorArm64 :app:assembleFullDebug --no-daemon`
- `./gradlew :core:ui:compileKotlinIosSimulatorArm64 :app:assembleFullDebug --no-daemon`
- `./gradlew :data:compileKotlinIosSimulatorArm64 :data:testDebugUnitTest :app:assembleFullDebug --no-daemon`
- `./gradlew :storage:compileKotlinIosSimulatorArm64 :storage:compileDebugKotlinAndroid :app:assembleFullDebug --no-daemon`
- `./gradlew :presentation:compileKotlinIosSimulatorArm64 :presentation:testDebugUnitTest :app:assembleFullDebug --no-daemon`
- `./gradlew :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 :app:assembleFullDebug --no-daemon`
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`
- `./gradlew :feature:onnx:compileKotlinIosSimulatorArm64 :feature:onnx:compileDebugKotlinAndroid :app:assembleFullDebug --no-daemon`
- `./gradlew :feature:mediapipe:compileKotlinIosSimulatorArm64 :feature:mediapipe:compileFullDebugKotlinAndroid :feature:mediapipe:compileFossDebugKotlinAndroid :feature:mediapipe:compilePlaystoreDebugKotlinAndroid :feature:mediapipe:testFullDebugUnitTest :app:assembleFullDebug --no-daemon`
- `./gradlew :network:compileKotlinIosSimulatorArm64 :network:compileDebugKotlinAndroid :storage:compileKotlinIosSimulatorArm64 :storage:compileDebugKotlinAndroid :app:assembleFullDebug --no-daemon`
- `./gradlew :network:compileKotlinIosSimulatorArm64 :network:compileDebugKotlinAndroid :data:testDebugUnitTest :app:assembleFullDebug --no-daemon`
- `./gradlew :domain:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :data:testDebugUnitTest :domain:testDebugUnitTest :app:assembleFullDebug --no-daemon`
- `./gradlew :network:compileKotlinIosSimulatorArm64 :domain:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :data:testDebugUnitTest :domain:testDebugUnitTest :app:assembleFullDebug --no-daemon`
- `./gradlew :core:ui:compileKotlinIosSimulatorArm64 :core:ui:compileDebugKotlinAndroid :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 :presentation:compileDebugKotlinAndroid :presentation:testDebugUnitTest :app:assembleFullDebug --no-daemon`
- `./gradlew :core:common:compileKotlinIosSimulatorArm64 :network:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :domain:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :presentation:testDebugUnitTest :app:assembleFullDebug --no-daemon`
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`
- `./gradlew :presentation:compileKotlinIosSimulatorArm64 :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :presentation:compileKotlinIosSimulatorArm64 :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :presentation:linkDebugFrameworkIosSimulatorArm64 :presentation:testDebugUnitTest :app:assembleFullDebug --no-daemon`
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`
- `./gradlew :network:testDebugUnitTest :domain:testDebugUnitTest :data:testDebugUnitTest :presentation:testDebugUnitTest :feature:work:testDebugUnitTest --no-daemon`
- `./gradlew :network:compileKotlinIosSimulatorArm64 :domain:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :app:assembleFullDebug --no-daemon`
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`
- `./gradlew :domain:compileDebugKotlinAndroid :data:compileDebugKotlinAndroid :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :domain:testDebugUnitTest :data:testDebugUnitTest :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :network:compileKotlinIosSimulatorArm64 :domain:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :app:assembleFullDebug --no-daemon`
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`
- `./gradlew :domain:testDebugUnitTest :data:testDebugUnitTest :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :network:compileKotlinIosSimulatorArm64 :domain:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :app:assembleFullDebug --no-daemon`
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`
- `./gradlew :presentation:compileKotlinIosSimulatorArm64 :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :presentation:compileKotlinIosSimulatorArm64 :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :presentation:linkDebugFrameworkIosSimulatorArm64 :presentation:testDebugUnitTest :app:assembleFullDebug --no-daemon`
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`
- `./gradlew :presentation:compileKotlinIosSimulatorArm64 :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :core:ui:compileKotlinIosSimulatorArm64 :core:ui:compileDebugKotlinAndroid :presentation:compileKotlinIosSimulatorArm64 :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :core:ui:compileKotlinIosSimulatorArm64 :core:ui:compileDebugKotlinAndroid :presentation:linkDebugFrameworkIosSimulatorArm64 :presentation:testDebugUnitTest :app:assembleFullDebug --no-daemon`
- `./gradlew :domain:compileKotlinIosSimulatorArm64 :domain:testDebugUnitTest :network:compileDebugKotlinAndroid :data:compileDebugKotlinAndroid :data:testDebugUnitTest :presentation:compileKotlinIosSimulatorArm64 :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :domain:compileKotlinIosSimulatorArm64 :domain:testDebugUnitTest :presentation:compileKotlinIosSimulatorArm64 :presentation:compileDebugKotlinAndroid :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :presentation:compileDebugKotlinAndroid :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 :app:assembleFullDebug --no-daemon`
- `./gradlew :network:compileDebugKotlinAndroid :domain:compileDebugKotlinAndroid :data:compileDebugKotlinAndroid :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :network:testDebugUnitTest :domain:testDebugUnitTest :data:testDebugUnitTest :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :network:compileKotlinIosSimulatorArm64 :domain:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :app:assembleFullDebug --no-daemon`
- `git diff --check`
- `./gradlew :app:assembleFullDebug --no-daemon`
- `./gradlew :domain:compileDebugKotlinAndroid :data:compileDebugKotlinAndroid :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :domain:testDebugUnitTest :data:testDebugUnitTest :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :network:compileKotlinIosSimulatorArm64 :domain:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :app:assembleFullDebug --no-daemon`
- `./gradlew :network:compileDebugKotlinAndroid :domain:compileDebugKotlinAndroid :data:compileDebugKotlinAndroid :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :network:testDebugUnitTest :domain:testDebugUnitTest :data:testDebugUnitTest :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :network:compileKotlinIosSimulatorArm64 :domain:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :app:assembleFullDebug --no-daemon`
- `./gradlew :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :app:assembleFullDebug --no-daemon`
- `./gradlew :domain:compileDebugKotlinAndroid :data:compileDebugKotlinAndroid :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :domain:testDebugUnitTest :data:testDebugUnitTest :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :app:assembleFullDebug --no-daemon`
- `./gradlew :domain:compileKotlinIosSimulatorArm64 :domain:testDebugUnitTest :data:compileDebugKotlinAndroid :data:testDebugUnitTest :demo:compileDebugKotlinAndroid :demo:testDebugUnitTest :presentation:compileDebugKotlinAndroid :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :domain:compileKotlinIosSimulatorArm64 :domain:testDebugUnitTest :presentation:compileKotlinIosSimulatorArm64 :presentation:compileDebugKotlinAndroid :presentation:testDebugUnitTest :app:assembleFullDebug --no-daemon`
- `./gradlew :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :core:common:compileKotlinIosSimulatorArm64 :core:common:compileDebugKotlinAndroid :demo:compileDebugKotlinAndroid :demo:testDebugUnitTest :presentation:compileDebugKotlinAndroid :presentation:testDebugUnitTest :app:assembleFullDebug --no-daemon`
- `./gradlew :presentation:compileDebugKotlinAndroid :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 :app:assembleFullDebug --no-daemon`
- `./gradlew :app:assembleFullDebug --no-daemon`
- `./gradlew :presentation:compileDebugKotlinAndroid :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 :app:assembleFullDebug --no-daemon`
- `./gradlew :network:compileDebugKotlinAndroid :domain:compileDebugKotlinAndroid :storage:compileDebugKotlinAndroid :data:compileDebugKotlinAndroid :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :network:testDebugUnitTest :domain:testDebugUnitTest :storage:testDebugUnitTest :data:testDebugUnitTest :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :network:compileKotlinIosSimulatorArm64 :domain:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :app:assembleFullDebug --no-daemon`
- `./gradlew :data:testDebugUnitTest --no-daemon`
- `./gradlew :network:compileDebugKotlinAndroid :domain:compileDebugKotlinAndroid :storage:compileDebugKotlinAndroid :data:compileDebugKotlinAndroid :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :domain:testDebugUnitTest :data:testDebugUnitTest :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :network:compileKotlinIosSimulatorArm64 :domain:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :app:assembleFullDebug --no-daemon`
- `./gradlew :app:assembleFullDebug --no-daemon`
- `./gradlew :network:compileDebugKotlinAndroid :domain:compileDebugKotlinAndroid :storage:compileDebugKotlinAndroid :data:compileDebugKotlinAndroid :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :domain:testDebugUnitTest :data:testDebugUnitTest :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :network:compileKotlinIosSimulatorArm64 :domain:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :network:compileDebugKotlinAndroid :domain:compileDebugKotlinAndroid :data:compileDebugKotlinAndroid :presentation:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :network:testDebugUnitTest :domain:testDebugUnitTest :data:testDebugUnitTest :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :network:compileKotlinIosSimulatorArm64 :domain:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :network:compileDebugKotlinAndroid :domain:compileDebugKotlinAndroid :data:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :domain:testDebugUnitTest :data:testDebugUnitTest --no-daemon`
- `./gradlew :network:testDebugUnitTest :domain:testDebugUnitTest :data:testDebugUnitTest :presentation:testDebugUnitTest :feature:work:testDebugUnitTest --no-daemon`
- `./gradlew :network:compileKotlinIosSimulatorArm64 :domain:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :app:assembleFullDebug --no-daemon`
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`

Latest setup/configuration/connectivity coroutine checkpoint gates:

- `./gradlew :domain:compileDebugKotlinAndroid :presentation:compileDebugKotlinAndroid :domain:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 --no-daemon`
- `./gradlew :domain:testDebugUnitTest :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :app:assembleFullDebug --no-daemon`
- `./gradlew :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`

Latest iOS configuration storage checkpoint gates:

- `env GRADLE_USER_HOME=/Users/shifthackz/Develop/Stable-Diffusion-Android/.gradle-user-home ./gradlew :domain:compileKotlinIosSimulatorArm64 :data:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 --no-daemon`
- `env GRADLE_USER_HOME=/Users/shifthackz/Develop/Stable-Diffusion-Android/.gradle-user-home ./gradlew :domain:compileDebugKotlinAndroid :data:compileDebugKotlinAndroid :presentation:compileDebugKotlinAndroid --no-daemon`
- `env GRADLE_USER_HOME=/Users/shifthackz/Develop/Stable-Diffusion-Android/.gradle-user-home ./gradlew :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`

Latest iOS setup presentation checkpoint gates:

- `env GRADLE_USER_HOME=/Users/shifthackz/Develop/Stable-Diffusion-Android/.gradle-user-home ./gradlew :presentation:compileDebugKotlinAndroid --no-daemon`
- `env GRADLE_USER_HOME=/Users/shifthackz/Develop/Stable-Diffusion-Android/.gradle-user-home ./gradlew :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`

Latest iOS navigation shell checkpoint gates:

- `./gradlew :presentation:compileDebugKotlinAndroid :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`

Latest multi-provider generation coroutine checkpoint gates:

- `./gradlew :domain:compileDebugKotlinAndroid :data:compileDebugKotlinAndroid :presentation:compileDebugKotlinAndroid :feature:work:compileDebugKotlinAndroid --no-daemon`
- `./gradlew :domain:testDebugUnitTest :presentation:testDebugUnitTest :feature:work:testDebugUnitTest --no-daemon`
- `./gradlew :domain:compileKotlinIosSimulatorArm64 :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 :app:assembleFullDebug --no-daemon`
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`

Latest shared/iOS text-to-image route checkpoint gates:

- `./gradlew :presentation:compileDebugKotlinAndroid :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :app:assembleDebug --no-daemon`
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`

Latest shared/iOS text-to-image save/share checkpoint gates:

- `./gradlew :presentation:compileDebugKotlinAndroid :presentation:compileKotlinIosSimulatorArm64 :presentation:linkDebugFrameworkIosSimulatorArm64 --no-daemon`
- `./gradlew :presentation:testDebugUnitTest --no-daemon`
- `./gradlew :app:assembleDebug --no-daemon`
- `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath /private/tmp/sdai-ios-derived CODE_SIGNING_ALLOWED=NO build`
- `git diff --check`

## Главный принцип миграции

Миграция должна быть additive-first:

1. Сначала добавить KMP/CMP инфраструктуру рядом с существующей Android-инфраструктурой.
2. Затем переносить чистые модули в `commonMain`.
3. Потом выносить Android API в `androidMain`.
4. После этого добавлять `iosMain` реализации.
5. Только после стабилизации удалять старые Android-only прослойки.

Если модуль содержит Android-only функционал, он не блокирует миграцию всего приложения: публичный контракт становится multiplatform, Android-реализация остается в `androidMain`, а iOS получает реальную реализацию, no-op, unsupported state или альтернативный платформенный adapter.

## Целевая структура

```text
app/                         Android application host, remains Android-only
iosApp/                      iOS SwiftUI/UIKit host for Compose
core/common/                 KMP common utilities and platform providers
core/validation/             KMP validators
core/imageprocessing/        KMP image abstractions, Android/iOS codecs in platform source sets
core/localization/           CMP resources and localization facade
core/ui/                     CMP UI primitives, UiText, common widgets
core/notification/           Platform notification contract + Android implementation
domain/                      KMP domain entities, repositories, use cases
network/                     KMP Ktor client + API models
storage/                     KMP persistence, preferably Room KMP or SQLDelight
data/                        KMP repository implementations and platform gateways
feature/auth/                KMP auth contracts + Android encrypted prefs / iOS NSUserDefaults
feature/work/                Platform background work contract, Android WorkManager preserved
feature/onnx/                Android-only Local Diffusion ONNX implementation
feature/mediapipe/           Android-only MediaPipe implementation
presentation/ or composeApp/ Common Compose UI and screen ViewModels
demo/                        KMP demo data where possible
```

Final naming can be adjusted during implementation. The important rule is that shared UI and shared logic live in KMP source sets, while Android app startup and iOS app startup stay platform-specific.

## Current Android-only leaks to remove from shared code

- `domain` currently imports `android.graphics.Bitmap`, `android.net.Uri`, and `android.os.PowerManager`.
- `core/ui` currently depends on Android `Context`, `Resources`, `stringResource`, and `androidx.lifecycle.ViewModel`.
- `presentation/androidMain` currently uses Android Activity APIs, Android Koin ViewModel APIs, Android resources, Android Navigation Compose, `LocalContext`, Android permissions, WebView, and Android `Bitmap`.
- `network` currently uses Retrofit, OkHttp JVM APIs, RxJava adapters, and Gson.
- `storage/androidMain` currently uses Room Android with RxJava DAO return types.
- `feature/work` currently depends on WorkManager and Android foreground notifications.
- `feature/onnx/androidMain` and `feature/mediapipe` Android flavor source sets are Android-only by design for the first iOS release.

## Common Model Replacements

Before converting modules to KMP, introduce these common abstractions:

```text
android.graphics.Bitmap      -> AppImage / ImageBytes / PlatformImage adapter
android.net.Uri              -> PlatformUri or String-backed MediaUri
PowerManager.WakeLock        -> WakeLockController contract
Context/Resources in common  -> StringResolver / ResourceResolver facade
Android file paths           -> PlatformFileSystem or AppDirectories provider
WorkManager                  -> BackgroundTaskManager platform implementation
NotificationCompat           -> NotificationGateway platform implementation
```

For Android, adapters should wrap the existing implementation. For iOS, adapters should use Foundation/UIKit/Photos APIs or return explicit unsupported states where the feature is out-of-scope.

## Phase 0: Baseline safety

Goal: ensure every later step can prove Android was not broken.

Tasks:

- Record current branch baseline.
- Keep Android application module untouched except for wiring new shared modules.
- Add a migration checklist to PRs.
- Establish repeatable local gates:
  - `./gradlew testDebugUnitTest`
  - `./gradlew :app:assembleFullDebug`
  - `./gradlew :app:assembleFossDebug`
  - `./gradlew :app:assemblePlaystoreDebug`
- Keep Local Diffusion smoke checks on Android whenever `feature:onnx`, `feature:mediapipe`, `domain`, or `data` generation contracts are touched.

Exit criteria:

- Existing Android tests pass or known failures are documented.
- Android debug APKs still assemble.
- No iOS code has been added yet except optional empty host scaffolding.

## Phase 1: Build infrastructure

Goal: support KMP/CMP modules without changing existing Android modules.

Tasks:

- Add version catalog entries for:
  - Kotlin Multiplatform Gradle plugin.
  - JetBrains Compose Multiplatform plugin.
  - Ktor client core, content negotiation, serialization, OkHttp engine, Darwin engine.
  - kotlinx.coroutines core.
  - kotlinx.serialization json.
  - Koin KMP and Koin Compose Multiplatform artifacts.
  - Room KMP + SQLite driver or SQLDelight, after choosing storage path.
- Add build-logic plugins:
  - `generic.kmp.library`
  - `generic.kmp.compose`
  - optional `generic.kmp.android-target`
- Configure standard targets:
  - `androidTarget()`
  - `iosX64()`
  - `iosArm64()`
  - `iosSimulatorArm64()`
- Keep existing `generic.library`, `generic.compose`, and `generic.application` unchanged.
- Create a minimal KMP smoke module only if needed to verify toolchain.

Exit criteria:

- Existing Android modules still build with old convention plugins.
- New KMP convention plugin compiles.
- No existing runtime behavior changes.

## Phase 2: Common foundations

Goal: migrate low-risk pure Kotlin code first.

Candidate modules:

- `core:validation`
- pure parts of `core:common`
- pure model helpers from `core:ui`

Tasks:

- Convert validators to KMP or move pure validators into `commonMain`.
- Split `core:common`:
  - common math, date formatting models, links interfaces, build info interfaces to `commonMain`.
  - Android file provider, Android clipboard, Android URI helpers to `androidMain`.
  - iOS app directories and clipboard adapters to `iosMain` when needed.
- Keep Timber logging behind a common `Logger` facade.
- Replace Java-only APIs with Kotlin/common alternatives.

Exit criteria:

- Android tests for validators and common helpers still pass.
- iOS source set compiles for the migrated modules.

## Phase 3: Domain module

Goal: make `domain` platform-neutral.

Tasks:

- Replace `Bitmap` in domain contracts with a shared image representation.
- Replace `Uri` in domain entities with a common media reference.
- Replace `PowerManager.WakeLock` repository exposure with `WakeLockController`.
- Move Android-only capability checks behind platform contracts.
- Preserve all current use case names and behavior where possible.
- Introduce an explicit Local Diffusion capability model:

```kotlin
enum class LocalGenerationSupport {
    Supported,
    UnsupportedOnPlatform,
}
```

- On Android, support remains backed by existing ONNX/MediaPipe.
- On iOS, Local Diffusion returns `UnsupportedOnPlatform` and the UI hides or disables that path.

Exit criteria:

- `domain` compiles for Android and iOS.
- Domain tests are converted away from Android test doubles where practical.
- Android generation source selection keeps all existing sources.

## Phase 4: Networking

Goal: replace Retrofit/Rx/Gson Android/JVM networking with KMP networking.

Preferred target:

- Ktor client.
- OkHttp engine on Android.
- Darwin engine on iOS.
- kotlinx.serialization for request/response DTOs.

Tasks:

- Create KMP HTTP client factory with platform engines.
- Port API clients one provider at a time:
  1. SDAI static config APIs.
  2. Automatic1111.
  3. SwarmUI.
  4. Horde.
  5. HuggingFace.
  6. OpenAI.
  7. Stability AI.
  8. Report/donate/update APIs.
- Preserve existing timeout semantics for long image-generation calls.
- Replace Rx return types in API layer with `suspend` functions and `Flow` only where streaming/progress is needed.
- Add compatibility adapters so Android repositories can migrate gradually.

Exit criteria:

- Android remote generation behavior matches current behavior.
- iOS can call remote provider APIs from shared code.
- Existing network tests are ported or replaced with Ktor mock engine tests.

## Phase 5: Storage

Goal: make persisted configuration, cache, and gallery metadata available to Android and iOS.

Preferred path:

- Use Room KMP if schema compatibility and migration effort stay reasonable.
- Use SQLDelight if Room KMP migration becomes too constrained by current Rx DAO design.

Tasks:

- Upgrade Room to KMP-capable version if Room remains the chosen path.
- Replace Rx DAO APIs with `suspend` and `Flow`.
- Add platform database builders:
  - Android: current app database path.
  - iOS: documents/application support directory.
- Preserve current schema versions and migrations.
- Keep Android data migration intact.
- Add iOS fresh-install schema tests.

Exit criteria:

- Existing Android users keep their database.
- Android DAO/repository tests pass.
- iOS can persist settings, cached metadata, and generated history.

## Phase 6: Preferences and auth

Goal: share settings and credential flows without weakening Android storage.

Tasks:

- Move preference contract to common.
- Android implementation keeps current SharedPreferences/encrypted storage behavior.
- iOS implementation currently stores remote-provider auth data in an `NSUserDefaults` suite through the KMP auth key-value actual.
- Non-sensitive settings use NSUserDefaults through the shared key-value actuals.
- Port `feature:auth` to KMP:
  - common credentials models.
  - Android encrypted implementation.
  - Optional iOS Keychain migration if stronger credential storage becomes a release requirement.

Exit criteria:

- Existing Android preference keys remain compatible.
- iOS can configure all remote providers.
- Auth tests cover both common mapping and platform storage behavior where possible.

## Phase 7: Data repositories and gateways

Goal: make repository implementations common where the dependencies are now KMP.

Tasks:

- Convert repositories from Rx to coroutines/Flow incrementally.
- Move shared repository logic into `commonMain`.
- Keep Android gateways in `androidMain`:
  - MediaStore.
  - WakeLock.
  - WorkManager-related state.
  - Android file export/share.
- Add iOS gateways:
  - Photos save/load where needed.
  - File export/share through UIKit/SwiftUI bridge.
  - Wake lock equivalent: no-op or idle timer adapter if required.
- Preserve Android Local Diffusion repository implementation as Android-only.
- Add iOS Local Diffusion repository implementation returning unsupported.

Exit criteria:

- Android repository tests pass.
- iOS remote generation and persistence repositories compile and run.

## Phase 8: Common Compose UI

Goal: reuse the current Compose UI on Android and iOS.

Tasks:

- Create or convert a shared Compose module using Compose Multiplatform.
- Move stateless composables first:
  - form fields.
  - dialogs.
  - gallery item views.
  - setup components.
  - theme primitives.
- Replace Android resources:
  - `R.string.*` -> Compose Multiplatform `Res.string.*`.
  - `painterResource(id = R.drawable.*)` -> CMP resources.
- Replace `LocalContext` usage with explicit platform services:
  - open URL.
  - share image.
  - save image.
  - pick file/image.
  - request permissions.
  - show WebView.
- Replace Android-only `ViewModel` inheritance with a common ViewModel/state-holder strategy.
- Replace `koinViewModel` Android calls with multiplatform DI entry points or parent injection.
- Use multiplatform navigation APIs where possible.
- Keep Android Activity as Android host only.
- Add iOS Compose host under `iosApp`.

Exit criteria:

- Android screens look and behave the same.
- iOS can launch shared Compose UI.
- iOS hides/disables Local Diffusion setup/generation modes.

## Phase 9: Background work and notifications

Goal: preserve Android background generation and provide iOS-appropriate behavior.

Tasks:

- Keep Android WorkManager implementation intact.
- Move `BackgroundTaskManager` and `BackgroundWorkObserver` contracts to common.
- Android implementation remains WorkManager + foreground notification.
- iOS implementation:
  - run remote generation as an in-app coroutine task for first release.
  - optionally integrate BGTaskScheduler later if product requirements demand it.
  - expose the same UI state transitions where possible.
- Keep Android notification permissions and channels platform-specific.
- Add iOS notification permission only if there is a product need for generation completion notifications.

Exit criteria:

- Android background generation behavior is unchanged.
- iOS remote generation works while app is active.
- Unsupported background behavior is explicit in product decisions.

## Phase 10: Local Diffusion boundary

Goal: protect Android Local Diffusion while making iOS limitation explicit.

Tasks:

- Keep `feature:onnx` Android-only.
- Keep `feature:mediapipe` Android-only.
- Move only contracts/capability flags to common.
- Android source selection still includes:
  - local ONNX.
  - local MediaPipe where flavor supports it.
- iOS source selection excludes local generation sources.
- Add tests that assert iOS cannot select Local Diffusion in common setup logic.

Exit criteria:

- Android Local Diffusion compiles and runs through the current code path.
- iOS build does not depend on ONNX Runtime Android or MediaPipe Android artifacts.

## Phase 11: iOS host

Goal: ship a real iOS app shell around shared code.

Tasks:

- Add `iosApp` Xcode project or generated iOS host structure.
- Initialize Koin/shared DI from Swift.
- Mount Compose UI in SwiftUI/UIKit.
- Add app icons, bundle id, permissions, and Info.plist entries.
- Add platform adapters:
  - browser/open URL.
  - share sheet.
  - image picker.
  - photo library save.
  - file picker for remote workflows where needed.
- Add basic iOS smoke tests or scripted simulator launch.

Exit criteria:

- iOS simulator launches.
- User can configure a remote provider.
- User can run text-to-image remote generation.
- Generated result can be viewed and saved/shared.

## Module migration order

Recommended order:

1. Build infrastructure.
2. `core:validation`.
3. pure `core:common`.
4. `domain` contracts and entities.
5. `network`.
6. `storage`.
7. `feature:auth`.
8. `data`.
9. `core:imageprocessing`.
10. `core:ui`.
11. `presentation` shared screens.
12. `feature:work`.
13. `app` Android wiring cleanup.
14. `iosApp` host.
15. Android-only Local Diffusion boundary hardening.

This order intentionally delays shared UI until domain/network/storage contracts are stable enough to support real screens on both platforms.

## Vertical slice strategy

After foundations are ready, migrate by user workflow:

1. App startup + theme + navigation shell.
2. Setup remote provider.
3. Text-to-image remote generation.
4. Image result preview/save/share.
5. Gallery/history.
6. Image-to-image remote generation.
7. Inpaint remote flow.
8. Provider-specific extras: models, samplers, loras, embeddings, credits.
9. Reports, donate, logger/debug where applicable.
10. Android-only Local Diffusion preservation pass.

Each slice must work on Android before expanding on iOS.

## Android preservation checklist

For every PR or migration step:

- No Android source is deleted until the replacement is wired and tested.
- Android flavors still compile: full, foss, playstore.
- Existing preference keys are preserved.
- Existing Room database schema and migrations are preserved.
- Existing Local Diffusion source selection remains available on Android.
- Existing WorkManager flow remains available on Android.
- Android resources are not removed until CMP resources are wired.
- Tests are updated rather than abandoned.

## iOS first-release checklist

Required:

- Launch shared Compose UI.
- Configure remote providers.
- Persist settings securely enough for API keys.
- Run remote text-to-image.
- View result.
- Save/share result.
- Browse generation history.
- Clearly hide or disable Local Diffusion.

Deferred:

- On-device iOS diffusion.
- iOS background generation parity with Android WorkManager.
- iOS push/local notifications unless product requires them.
- Native iOS redesign.

## Risk register

| Area | Risk | Mitigation |
| --- | --- | --- |
| RxJava to coroutines | Large API churn | Use adapters during migration, module by module |
| Room KMP | DAO/migration incompatibilities | Spike Room KMP early; switch to SQLDelight only if needed |
| Compose resources | Large Android `R` usage | Move resources screen by screen |
| Bitmap/Image handling | Android `Bitmap` leaks everywhere | Introduce `AppImage` boundary before UI migration |
| Local Diffusion | Android regression risk | Keep implementation Android-only and covered by Android gates |
| Background work | iOS cannot match WorkManager exactly | Product-scope iOS first release to active app remote generation |
| DI | Android Koin APIs leak into UI | Move to Koin KMP/Compose APIs or explicit injection at hosts |

## Done definition

The migration is done when:

- Android app behavior is equivalent to pre-migration behavior.
- Android Local Diffusion remains functional.
- iOS app ships the shared Compose UI.
- iOS supports remote generation workflows.
- iOS explicitly excludes Local Diffusion without broken UI paths.
- Common modules compile for Android and iOS.
- Platform-specific code is isolated in `androidMain`, `iosMain`, `app`, or `iosApp`.
