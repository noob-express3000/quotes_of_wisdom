# Quotes of Wisdom — Canonical Project State

_Last updated: 2026-08-26_

This is the handoff checkpoint. Read this file, then `docs/product-spec.md`, before changing product behavior.

## Working mode

This project is intentionally vibe-coded. The user/product owner defines behavior, tests real-device builds, reviews source, and proposes changes. ChatGPT performs most implementation, architecture, testing scaffolding, CI maintenance, and debugging.

Do not redesign the product idea unless explicitly asked.

## Repository

Canonical repo: `noob-express3000/quotes_of_wisdom`

GitHub is source of truth. GitHub Actions is the canonical clean Android build.

## Product summary

Android-only, local-first quote app using Android Text-to-Speech and RevenueCat for paid entitlement management.

No login, registration, custom backend, or user account system.

```text
local corpus
 -> global shuffled/no-repeat deck
 -> favorites lightly personalize classifications
 -> quote display
 -> TTS
 -> replay / next / direct text scroll
 -> favorites / share / themes / streak
```

## Frozen visual / interaction rules

- Exactly 3 base colors per theme; perceptual 60/30/10 dominant/secondary/accent.
- Material tertiary is the same theme accent; it is not a fourth color.
- All Material roles stay inside those same 3 colors.
- Card/container borders use accent/tertiary.
- Primary readable text uses secondary.
- Theme library is **100 themes**: 2 Trial + 98 Pro.
- Settings gear: top-left, accent/tertiary.
- Streak: centered in Home header as compact flame/count treatment.
- Access label: top-right floating accent text with no background pill.
- Favorite: bottom-left inside quote card below author.
- Share: bottom-right inside quote card below author.
- Long quote text scrolls directly by touch.
- No decorative quote marks around quote text.
- Custom bold minimal Q launcher icon replaces generic bulb imagery.

## Settings / Favorites

- Favorites launcher first.
- Speech controls second.
- Themes last among user-facing settings.
- Debug-only access preview may follow in debug builds.
- Streak is not duplicated in Settings.
- Favorites open in a dedicated screen with close control and per-item removal.
- Empty Favorites shows no explanatory bookmark message.
- Settings copy stays terse.

## Quote personalization

The production corpus remains globally shuffled with no repeats during a cycle. Favorites act as a lightweight taste signal:

- determine the user's top 3 favorited classifications;
- approximately 70% of eligible `Next` draws are biased toward those classifications;
- the same global deck is consumed, preserving exploration and no-repeat behavior.

## Speech rules

Android/system TTS remains the v1 speech backend; custom neural model packs are deferred.

Trial:

- uses the system-default TTS engine;
- one fixed English voice + 1.0x speed;
- prefer the best available local/on-device English voice using Android TTS quality/latency metadata;
- OEM TTS failures degrade to text instead of taking down the app;
- `Get more voices` remains available as a compatibility action and is not Pro-gated.

Pro:

- enumerate all installed Android TTS engines;
- engine selector appears above the voice selector;
- selected engine persists in DataStore;
- changing engine reinitializes TTS against that package and refreshes installed English voices;
- installed English voices selectable, with local versus online labels;
- voices Android marks as not yet installed are excluded from the ready-to-use list;
- rate 0.7x–1.4x;
- engine/voice/rate persist in DataStore.

Voice-data flow:

- `Get more voices` first launches the selected engine's `ACTION_INSTALL_TTS_DATA` activity;
- falls back to the generic Android TTS install-data action;
- falls back to system Settings if neither installer exists;
- returning to the app reinitializes the current engine so new voice data is rediscovered.

The multi-engine/voice-data code checkpoint `44387c239a2ce5b4b3fb89f392e62acb88d17c89` passed GitHub Actions run #142 (`32978068593`), including production quote validation, debug APK build and artifact upload.

## Retention / access

- streak day = opening app at least once in local calendar day;
- multiple opens same day count once;
- missed day breaks streak;
- broken streak gets strong motivational opening quote;
- daily notifications still to be implemented;
- Trial launch paywall: dismissible;
- Grace launch paywall: dismissible, TTS off;
- Locked: paywall non-dismissible;
- Pro: no launch paywall.

```text
DAY 0-30   TRIAL_ACTIVE
DAY 31-33  GRACE_TEXT_ONLY
DAY 34+    LOCKED

ANY STATE + active pro_access -> PRO
```

Debug Trial/Grace/Locked/Pro overrides are debug-only and persist across app restarts. Release builds ignore them.

## Current paywall presentation

Target prices remain:

- Weekly ≈ $0.50
- Monthly ≈ $1.00
- Lifetime ≈ $29

Current UI:

- headline `Choose your plan`;
- three centered clickable plan cards;
- cards contain only plan name + price;
- `Try It!`, `Best Value!`, `Own It!`, separate `Choose` buttons, spinning presentation and bottom filler copy are removed;
- Monthly retains the thicker emphasis border;
- plan-card borders use accent/tertiary;
- plan-card text uses secondary;
- small top-left accent/tertiary info button explains the immediate value of upgrading, with state-aware copy for Trial/Grace/Locked;
- production RevenueCat build must replace placeholder target prices with localized store pricing.

All products eventually grant the single RevenueCat entitlement `pro_access`.

## Trial hardening plan for RevenueCat milestone

Use a deterministic opaque device-scoped App User ID:

```text
ANDROID_ID + package + signing certificate fingerprint
 -> SHA-256
 -> opaque RevenueCat App User ID
```

Never expose/transmit the raw Android ID as the RevenueCat identifier.

Layer trial clocks using persisted wall time, monotonic elapsed time, and RevenueCat first-seen/customer timing so clock rollback cannot extend the trial.

## M0 — COMPLETE

- Kotlin + Compose Android project
- API 36 toolchain
- Actions APK build
- physical-device launch validated

## M1 — COMPLETE

Merged PR #4.

- asset quote repository
- quote model
- global shuffled/no-repeat deck
- StateFlow ViewModel
- Next button
- strict three-color default theme

## PRODUCTION QUOTE CORPUS — COMPLETE

Merged PR #8, merge commit `1a2363e189951bf65c7cfc34654d0c2bc1f6e55e`.

- 1,063 shipping quotes
- 356 authors
- 12 categories, each >=20
- per-author cap 20
- exact schema `id`, `text`, `author`, `classification`
- provenance ledger for every shipped record
- production validator and Android CI passed

Do not casually reopen the corpus gate.

## CURRENT — PR #9 PHYSICAL-DEVICE PRODUCT TEST

Branch: `m2-product-test`
PR: #9 `M2: physical-device product test build`

Latest requested refinements implemented on branch:

- 50 -> 100 strict three-color themes;
- empty Favorites explanatory text removed;
- paywall eyebrow statements removed;
- plan cards centered horizontally and as a centered screen group;
- accent/tertiary borders enforced across Home, Favorites, Settings/theme tiles and paywall;
- secondary text role retained for readable copy;
- top-left accent/tertiary paywall info control added with state-aware immediate-upgrade value copy;
- all installed Android TTS engines are now discoverable;
- Pro can switch TTS engine and persist the choice;
- changing engine refreshes that engine's installed English voice catalog;
- `Get more voices` opens engine/system voice-data installation and refreshes TTS on return.

One hardware note remains to verify on-device: an earlier physical test mentioned the app not running on a `V50 Lite`. Android version/minSdk is not the obvious cause. If a current APK still fails there, capture the exact install error/crash behavior or log before changing compatibility settings blindly.

RevenueCat purchase actions are still placeholders in PR #9.

## Immediate next action

1. Run final branch-head Android CI after this documentation checkpoint.
2. Download the branch-head `quotes-of-wisdom-debug` artifact.
3. Install/test the revised APK, specifically TTS engine switching and `Get more voices` on the Samsung device.
4. Verify newly downloaded voices appear after returning to the app.
5. Test the V50 Lite if available and capture exact failure details if it still fails.
6. Fix any remaining findings.
7. Merge PR #9 only after physical-device signoff and green branch-head CI.
8. Begin RevenueCat Test Store milestone.

## Later milestones

```text
M3  consolidate/polish persistence, favorites, themes and share UX prototyped in M2
M4  harden access/trial clocks/paywall shell prototyped in M2
M5  RevenueCat Test Store + deterministic ID + offerings/purchase/restore/pro_access
M6  notifications + streak/conversion flow + hardening/analytics
M7  Google Play/store release hardening
M8  license/README/demo/BuildInPublic + Next Gen submission polish
```

Galaxy Store remains optional/deprioritized.

## Development discipline

```text
spec -> implement -> CI -> physical-device test -> review -> merge
```

No giant framework expansion, backend creep, or unverified merges.
