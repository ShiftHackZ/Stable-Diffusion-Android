# Git Workflow

This repository uses a release branch plus integration branch model.

- `master` is the released App Store / Google Play line.
- `develop` is the integration and experiment line.
- Feature branches are created from `develop`.
- Release tags are created from `master`.
- Hotfixes may start from `master`, but must be brought back into `develop`.

## Branch Roles

### master

`master` must represent the latest released or review-submitted state.

Use it for:

- App Store / Google Play review builds.
- Release tags.
- Compliance hotfixes.
- GitHub Pages published content.

Do not land experimental features directly into `master`.

### develop

`develop` is the normal integration branch.

Use it for:

- Feature PR targets.
- Experimental providers and UI work.
- Integration testing before a release train moves to `master`.

Do not configure GitHub Pages to deploy from `develop`.

### feature branches

Create feature branches from `develop`:

```bash
git fetch origin
git switch develop
git pull --ff-only
git switch -c feature/my-feature
```

Feature PRs target `develop`.

Feature PRs may be squash-merged into `develop`. This keeps the integration branch readable and makes generated documentation conflicts easier to replace with one final generated output.

Use `--force-with-lease`, not plain `--force`, when refreshing a feature branch after a rebase:

```bash
git push --force-with-lease origin feature/my-feature
```

## Release Flow: develop to master

The release merge from `develop` to `master` must preserve commit object IDs. Do not use GitHub's PR merge buttons for `develop -> master`.

GitHub's merge UI can create new server-side commits or rewrite commits depending on the selected merge strategy:

- Squash merge creates a new commit.
- Rebase merge creates new commits with new object IDs.
- Merge commit preserves the commits being merged, but creates a GitHub-generated merge commit and makes the branch tips differ.

For this project, the release goal is stricter: the commits that are in `develop` should appear in `master` with the same object IDs. Use a local fast-forward release.

```bash
git fetch origin
git switch master
git pull --ff-only origin master
git merge --ff-only origin/develop
git push origin master
```

After the push, tag the release from `master`:

```bash
git tag vX.Y.Z
git push origin vX.Y.Z
```

If `git merge --ff-only origin/develop` fails, `master` and `develop` have diverged. Do not use GitHub UI to solve it. First sync the `master`-only commits back into `develop`.

```bash
git switch develop
git pull --ff-only origin develop
git merge --no-ff origin/master
git push origin develop
```

Then retry the fast-forward release from `develop` to `master`.

## Hotfix Flow

Hotfixes start from `master` when the release or review-submitted app needs a compliance, store, or production fix.

```bash
git fetch origin
git switch master
git pull --ff-only origin master
git switch -c hotfix/compliance-fix
```

After review and validation, land the hotfix into `master` with a local fast-forward whenever possible:

```bash
git switch master
git pull --ff-only origin master
git merge --ff-only hotfix/compliance-fix
git push origin master
```

Then sync the hotfix back into `develop` without rewriting it:

```bash
git switch develop
git pull --ff-only origin develop
git merge --no-ff origin/master
git push origin develop
```

This preserves the hotfix commit object ID and makes the next release fast-forwardable.

## GitHub Pages

GitHub Pages must never deploy from `develop`.

The allowed Pages source is the released line:

- GitHub Pages source: `master` / `docs`.
- If a Pages workflow is added later, restrict it to `master` or release tags only.
- Do not add `develop` to any Pages deployment trigger.

Allowed workflow trigger shape:

```yaml
on:
  push:
    branches: [ master ]
```

If manual deployment is added, the job must still guard against `develop`:

```yaml
if: github.ref == 'refs/heads/master'
```

It is fine to regenerate `docs/docs` on feature branches and `develop`; publishing is restricted to `master`.

## Generated Dokka Documentation

Generated Dokka output lives under `docs/docs`.

Feature branches may include a final docs-only commit when useful, but generated docs are expected to conflict when two feature branches both change public APIs.

Preferred feature shape:

1. Implementation commits.
2. One final Dokka commit:

```bash
./gradlew dokkaGeneratePublicationHtml --no-daemon
git add docs/docs
git commit -m "Regenerate Dokka for <feature>"
```

When two feature PRs both regenerate Dokka, merge one PR first. Then refresh the remaining PR:

```bash
git fetch origin
git switch feature/remaining-feature
git rebase origin/develop
```

If the rebase conflicts only in the docs-only Dokka commit, skip that docs commit and regenerate Dokka after the rebase:

```bash
git rebase --skip
./gradlew dokkaGeneratePublicationHtml --no-daemon
git add docs/docs
git commit -m "Regenerate Dokka for <feature>"
git push --force-with-lease origin feature/remaining-feature
```

If the rebase has source conflicts, resolve source code first, then regenerate Dokka from the final source tree. Do not hand-edit generated Dokka HTML to resolve semantic API conflicts.

## Current Migration Note

At the time this workflow was introduced, two feature branches were already based on `master`:

- `feature/backport-614-patch1`
- `feature/ios-coreml-local-provider`

Both contain generated Dokka updates under `docs/docs`, so their PRs are expected to conflict if both are opened against `develop`.

Recommended order:

1. Rebase both branches onto `develop`.
2. Open both PRs against `develop`.
3. Merge one PR into `develop`.
4. Rebase the remaining PR onto updated `develop`.
5. Skip or replace the stale docs-only commit if needed.
6. Regenerate Dokka and force-push the remaining branch with `--force-with-lease`.

After both PRs are merged into `develop`, run the normal smoke tests and only then fast-forward `master` from `develop` for the next release.
