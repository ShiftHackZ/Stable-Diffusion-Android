# Mobile Auto-QA

This folder contains a first-pass concept for real-device smoke tests with [Maestro](https://maestro.mobile.dev/).

Secrets and local endpoints should live in `qa/mobile/.env` and must not be committed. Start from:

```bash
cp qa/mobile/.env.example qa/mobile/.env
```

Initial target coverage:

- Automatic1111 `txt2img`, `img2img`, and `inpaint` against `SDAI_A1111_URL`.
- SwarmUI `txt2img` smoke against `SDAI_SWARMUI_URL`.

Run concept flows:

```bash
source qa/mobile/.env
maestro test \
  -e APP_ID="$SDAI_APP_ID" \
  -e A1111_URL="$SDAI_A1111_URL" \
  -e SWARMUI_URL="$SDAI_SWARMUI_URL" \
  qa/mobile/maestro
```

The current flows are intentionally smoke-level and text driven. As the Compose UI gains stable test tags, these flows should move from visible text selectors to explicit ids.
