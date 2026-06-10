# Screenshot Generation

This project generates store, website, and README screenshots from real app
screens captured on Android emulators and iOS simulators. The marketing template
is applied after capture, so rendered store images never fall back to older
screenshots.

## Script

```bash
node scripts/generate_store_screenshots.mjs <command> [options]
```

Commands:

- `plan` prints the localized screenshot deck and expected raw screenshot files.
- `capture` runs a Maestro flow on a real simulator/emulator and saves raw app
  screenshots.
- `render` turns raw app screenshots into store, website, and README marketing
  assets.
- `all` runs `capture`, then `render`.

## Requirements

- Node.js.
- `rsvg-convert` from librsvg for rendering SVG templates to PNG.
- Maestro for automated navigation and capture.
- Android: `adb` and a running emulator.
- iOS: Xcode command line tools and a booted iOS simulator.

The capture flow assumes the app is already installed on the selected device.
After changing demo images or other compiled app resources, rebuild and reinstall
the app before running `capture`.

## Locales

Supported locales:

- `en-US`
- `ru`
- `uk`

The script maps them to Android app locales and iOS simulator languages.

## Android

Default Android package is the `full` flavor:

```bash
node scripts/generate_store_screenshots.mjs capture --platform android --locale en-US --flavor full
node scripts/generate_store_screenshots.mjs render --platform android --locale en-US --targets fastlane,googleplay
```

Useful Android options:

- `--device emulator-5554` selects an adb device.
- `--flavor playstore|full|foss` selects the package id.
- `--package com.example.app` overrides the Android package id.
- `--keep-state` keeps existing app data.
- `--prepare existing` skips onboarding/demo setup and captures the current app
  state.

Render all Android locales:

```bash
node scripts/generate_store_screenshots.mjs render --platform android --locales en-US,ru,uk --targets fastlane,googleplay
```

Android outputs:

- F-Droid fastlane: `fastlane/metadata/android/<locale>/images/phoneScreenshots/`
- Google Play: `docs/screenshots/googleplay/<locale>/phoneScreenshots/`

## iOS

Capture from a booted simulator:

```bash
node scripts/generate_store_screenshots.mjs capture --platform ios --locale en-US
node scripts/generate_store_screenshots.mjs render --platform ios --locale en-US --targets appstore,site
```

Useful iOS options:

- `--device booted` uses the currently booted simulator.
- `--device <udid>` targets a specific simulator UDID.
- `--bundle com.shifthackz.aisdv1.app` overrides the iOS bundle id.
- `--keep-state` keeps existing app data.
- `--prepare existing` skips onboarding/demo setup and captures the current app
  state.

Render all iOS locales:

```bash
node scripts/generate_store_screenshots.mjs render --platform ios --locales en-US,ru,uk --targets appstore
```

iOS outputs:

- App Store 6.9-inch screenshots:
  `docs/screenshots/appstore/<locale>/iphone-6.9/`
- Website banners from the iOS frame, English only:
  `docs/screenshots/site/ios/en-US/`
- README screenshot rows from the English iOS App Store screenshots:
  `docs/screenshots/site/readme-row-1.png`
  `docs/screenshots/site/readme-row-2.png`

Website screenshots are intentionally generated only from iOS `en-US` output.
README rows are also intentionally generated from the mobile App Store screenshots,
not from website banners.

Generate website banners and README rows:

```bash
node scripts/generate_store_screenshots.mjs render --platform ios --locale en-US --targets appstore,site
```

## Raw Screenshots

Raw simulator captures are stored in:

```text
docs/screenshots/raw/<platform>/<locale>/
```

Expected slide files:

- `01-create.png`
- `02-controls.png`
- `03-providers.png`
- `04-gallery.png`
- `05-settings.png`
- `06-open.png`

The `05-settings` slide is derived from three raw captures:

- `settings-light.png`
- `settings-dark.png`
- `settings-dark-accent.png`

The script combines them into a diagonal light/dark Look & Feel screenshot before
rendering store assets.

## Full Regeneration

After changing demo gallery assets:

1. Rebuild and reinstall the app on Android and iOS devices.
2. Capture all required locales.
3. Render store/site outputs from the fresh raw screenshots.

Example:

```bash
node scripts/generate_store_screenshots.mjs capture --platform android --locale en-US --flavor full
node scripts/generate_store_screenshots.mjs capture --platform android --locale ru --flavor full
node scripts/generate_store_screenshots.mjs capture --platform android --locale uk --flavor full

node scripts/generate_store_screenshots.mjs capture --platform ios --locale en-US
node scripts/generate_store_screenshots.mjs capture --platform ios --locale ru
node scripts/generate_store_screenshots.mjs capture --platform ios --locale uk

node scripts/generate_store_screenshots.mjs render --platform android --locales en-US,ru,uk --targets fastlane,googleplay
node scripts/generate_store_screenshots.mjs render --platform ios --locales en-US,ru,uk --targets appstore
node scripts/generate_store_screenshots.mjs render --platform ios --locale en-US --targets appstore,site
```

## Partial Regeneration

The render step always renders the full deck for the selected platform/locales.
If only gallery images changed, it is still best to recapture at least the flow
that produces:

- `01-create.png`, because it shows the generated image result.
- `04-gallery.png`, because it shows the gallery grid.

Running full capture is usually safer because the Maestro flow naturally creates
the generated result, fills the gallery with six images, opens details, and then
captures settings.

## Troubleshooting

- Missing raw screenshot: run `capture` for the same `--platform` and `--locale`.
- Wrong language: clear app state or omit `--keep-state`, then capture again.
- Old demo images: rebuild/reinstall the app after editing `DemoAssets.kt`.
- Empty gallery: capture in normal demo preparation mode, not `--prepare existing`.
- Android status bar is not normalized: keep `--demo-status-bar` enabled.
- Need current app state only: use `--prepare existing --keep-state`.
