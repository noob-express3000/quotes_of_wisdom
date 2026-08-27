# Quotes of Wisdom — Canonical Project State

_Last updated: 2026-08-27_

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
- Theme display names are audited to a maximum of two words and must match the actual palette.
- Theme rows are first-class `LazyColumn` items so off-screen theme tiles are not all composed at once.
- Theme rows expose a stable content type and the three swatches in each tile are rendered by one Canvas to reduce per-row composition overhead.
- App presentation is immersive full-screen: Android status/navigation bars are hidden during normal use so the dominant theme color owns the complete display.
- Platform system bars remain transiently reachable with the normal edge gesture/swipe and re-hide afterward.
- The window is allowed into short-edge display-cutout regions. Home gear/access controls sit close to the physical top corners while the centered streak sits below the likely camera line; screens no longer reserve one global safe-drawing strip.
- Settings gear: top-left, accent/tertiary.
- Streak: centered in Home header as compact flame/count treatment. Tapping it launches one approximately 820 ms full-screen flame surge using only the active palette; no streak state changes and no continuous particle loop.
- Access label: top-right floating accent text with no background pill. When the label is `PRO`, tapping it performs one 360-degree in-place spin with a subtle scale pulse; it is decorative only.
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
- Tapping a saved favorite plays that quote through TTS when the current access state permits speech and TTS is ready.
- Settings and Favorites headers use cutout-aware horizontal placement and sit close to the physical top edge in immersive mode.
- Settings copy stays terse.

## Quote personalization

The production corpus remains globally shuffled with no repeats during a cycle. Favorites act as a lightweight taste signal:

- determine the user's top 3 favorited classifications;
- approximately 70% of eligible `Next` draws are biased toward those classifications;
- the same global deck is consumed, preserving exploration and no-repeat behavior.

## Speech rules

Android/system TTS remains the v1 speech backend; custom neural model packs are deferred.

Opening narration:

- quote text appears immediately;
- narration begins as soon as TTS is ready and access permits speech;
- there is no artificial 2-second app-side delay.

Trial:

- uses the system-default TTS engine;
- one fixed English voice + 1.0x speed;
- prefer the best available local/on-device English voice using Android TTS quality/latency metadata;
- OEM TTS failures degrade to text instead of taking down the app;
- additional voice-data installation is not exposed in Trial settings.

Pro:

- enumerate all installed Android TTS engines;
- engine selector appears above the voice selector;
- selected engine persists in DataStore;
- changing engine reinitializes TTS against that package and refreshes installed English voices;
- installed English voices selectable, with local versus online labels;
- voices Android marks as not yet installed are excluded from the ready-to-use list;
- `Get more voices` is Pro-only;
- rate 0.7x–1.4x;
- engine/voice/rate persist in DataStore.

Voice-data flow:

- Pro `Get more voices` first launches the selected engine's `ACTION_INSTALL_TTS_DATA` activity;
- falls back to the generic Android TTS install-data action;
- falls back to system Settings if neither installer exists;
- returning to the app reinitializes the current engine so new voice data is rediscovered.

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

Current physical-test placeholders:

- Weekly: `$0.50 / week`
- Monthly: `$1 / month`
- Lifetime: `$29 / once`

Current UI:

- headline `Choose your plan`;
- three centered clickable plan cards;
- cards contain only plan name + price;
- `Try It!`, `Best Value!`, `Own It!`, separate `Choose` buttons, continuous/spinning paywall presentation and bottom filler copy are removed;
- Monthly retains the thicker emphasis border;
- plan-card borders use accent/tertiary;
- plan-card text uses secondary;
- small top-left accent/tertiary info button opens a prominent Pro access card at the top of the display rather than expanding/reflowing the paywall;
- Pro access card is wider and visually stronger than the previous compact centered modal, with a 2dp accent border and larger heading/body copy;
- the Pro access card itself respects the display cutout, includes state-aware Trial/Grace/Locked copy plus Pro voice-download value, and can be dismissed by its close control or outside tap;
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

- 100 strict three-color themes, with theme gallery lazily composed by row;
- theme labels audited to two words maximum, including inaccurate legacy labels corrected without changing persisted theme IDs;
- theme-gallery scrolling tuned further for weaker hardware by giving lazy rows a shared content type and replacing three separate swatch surfaces per tile with one Canvas draw;
- app uses immersive full-screen mode so the dominant theme color occupies the complete display and Android system bars are transient-by-swipe;
- global safe-drawing padding removed; the window uses the display-cutout region and places top-corner controls around the likely camera area while keeping the streak below the center camera line;
- tapping the streak runs a short full-screen palette-aware flame surge with no persistent animation workload;
- tapping the Home `PRO` label performs one 360-degree spin and subtle scale pulse without changing state or navigation;
- opening TTS artificial 2-second delay removed;
- all installed Android TTS engines discoverable and Pro-switchable;
- `Get more voices` moved behind Pro and opens engine/system voice-data installation only in Pro settings;
- tapping a favorite speaks the saved quote when speech is permitted;
- paywall info control opens a larger, top-positioned Pro access card rather than inserting copy into the pricing layout;
- placeholder plan formatting is `$0.50 / week`, `$1 / month`, `$29 / once`;
- empty Favorites explanatory text removed;
- plan cards centered, simplified and strict three-color styling retained.

Physical-performance note from testing: theme scrolling was slightly laggy on the Samsung test phone while remaining smooth on the itel device. The current branch includes targeted composition reductions for that path. Debug APKs are still expected to be less representative of final release performance than optimized release builds.

One hardware note remains to verify on-device: an earlier physical test mentioned the app not running on a `V50 Lite`. Android version/minSdk is not the obvious cause. If a current APK still fails there, capture the exact install error/crash behavior or log before changing compatibility settings blindly.

RevenueCat purchase actions are still placeholders in PR #9.

## Immediate next action

1. Run final branch-head Android CI after this polish checkpoint.
2. Download the branch-head `quotes-of-wisdom-debug` artifact.
3. Physically verify the streak flame surge on the Samsung and itel, checking visual coverage and whether the short animation remains smooth enough on the weaker device.
4. Verify the Home `PRO` label performs exactly one spin/scale pulse per tap and remains purely decorative.
5. Re-test the camera/cutout-aware top layout on both devices and check that no header control overlaps the front camera.
6. Verify the larger top-positioned Pro access info card and `$29 / once` placeholder.
7. Re-test theme scrolling on the Samsung and compare it with the previous APK; treat small remaining debug-build jank separately from release-build performance.
8. Verify Trial Settings no longer exposes `Get more voices`, while Pro still does and refreshes TTS after returning.
9. Re-check immediate narration, favorite tap playback, theme labels and TTS engine switching.
10. Test the V50 Lite if available and capture exact failure details if it still fails.
11. Fix any remaining findings.
12. Merge PR #9 only after physical-device signoff and green branch-head CI.
13. Begin RevenueCat Test Store milestone.

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
