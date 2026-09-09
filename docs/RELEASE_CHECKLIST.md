# myQuote — Release Checklist

A checked CI item is not a substitute for physical-device testing.

## A. Shipaton / judge release

### Completed

- [x] Public product name changed to **myQuote**.
- [x] Launcher/app label and demo notification branding use **myQuote**.
- [x] Gothic `Q` remains the product mark.
- [x] Stable technical package/application ID remains `com.shipaton.quotesofwisdom`.
- [x] Repository name remains `quotes_of_wisdom` for link/history stability.
- [x] RevenueCat product IDs remain `qow_weekly`, `qow_monthly`, and `qow_lifetime`.
- [x] Existing local storage identifiers remain unchanged for update compatibility.
- [x] RevenueCat uses SDK-generated anonymous App User IDs; no device-derived billing identifier remains.
- [x] RevenueCat `CustomerInfo` is the paid-entitlement source of truth; no persistent Boolean Pro snapshot remains.
- [x] Purchase success requires active `pro_access` in returned `CustomerInfo`.
- [x] Restore without active `pro_access` produces an explicit failure result.
- [x] Test Store prices come from RevenueCat; the app has no location inference or production fallback prices.
- [x] README, project state, submission guide, privacy draft, terms draft, Data Safety draft, product spec, NOTICE, and curation policy aligned to myQuote.
- [x] Renamed binary version is `1.0.2` / `versionCode 3`.
- [x] Exact source commit `9facebb8dc647048802cff2da392f319b4e24ee8` passed quote validation, tests, lint, Debug/QA builds, Release bundle validation, signer verification, and artifact upload in Android CI run `34399275732`.
- [x] QA artifact ID `10122949515` recorded and archive digest verified.
- [x] Permanent release published: <https://github.com/noob-express3000/quotes_of_wisdom/releases/tag/v1.0.2>
- [x] Judge APK asset: `myQuote-v1.0.2-judge.apk`.
- [x] Judge APK SHA-256: `3439768959570d5962edc845f72dce0ee082b6e0f90b9913035952585e6ad053`.
- [x] Original `v1.0.0` and `v1.0.1` releases retained for provenance.

### RevenueCat dashboard gate

- [ ] Set the **sandbox/Test Store** restore behavior to **Transfer to new App User ID**.
- [ ] Before Google Play launch, set the production restore behavior to **Transfer to new App User ID** as well.
- [ ] Confirm `pro_access`, `qow_weekly`, `qow_monthly`, and `qow_lifetime` remain attached to the Current Offering as expected.

### Physical smoke

Use `myQuote-v1.0.2-judge.apk` from the permanent release.

- [ ] Clean install/update and confirm the launcher/app identity displays `myQuote`.
- [ ] Confirm the existing Gothic Q launcher icon remains correct.
- [ ] Trial paywall is dismissible; Info and Close work repeatedly.
- [ ] Weekly, Monthly, and Lifetime show `$0.99`, `$2.99`, and `$29.99` in Test Store.
- [ ] Unavailable/loading plans cannot be purchased and never show invented prices.
- [ ] Lifetime Test Store purchase activates `PRO`.
- [ ] Force-stop/reopen online preserves Pro.
- [ ] Offline reopen follows RevenueCat cached-entitlement behavior without an app-owned fallback entitlement database.
- [ ] Reinstall/new anonymous identity + Restore Purchases recovers Pro after the sandbox restore setting is configured.
- [ ] Trial/non-Pro renders default Lora and gates quote-font selection behind `See Pro`.
- [ ] Pro exposes all quote fonts, themes, TTS controls, and custom reminder time.
- [ ] Replay, Next, Favorite, Share, settings, system Back, streak effect, Pro spin, and notifications work.
- [ ] Trigger the debug/demo notification and confirm its title displays `myQuote`.
- [ ] Repeat the critical path on another Android device if practical.

### Submission assets

- [ ] Use **myQuote** consistently in the HackerNoon article, Shipaton form, screenshots, captions, and demo narration.
- [ ] Clearly state: working Android release candidate; RevenueCat Test Store tested; Google Play production configuration/testing pending developer-account access.
- [ ] Include the RevenueCat State of Subscription Apps findings that informed the 30-day trial and low-price/hybrid monetization direction.
- [ ] Do not claim RevenueCat prescribed the exact `$0.99 / $2.99 / $29.99` prices; those are product decisions.
- [ ] Add the public repository and `v1.0.2` GitHub Release to the submission.
- [ ] Add BuildInPublic evidence.
- [ ] Re-check the live Shipaton form immediately before submission.

## B. Google Play production release

The QA judge APK must not be uploaded to Google Play.

### Play / signing

- [ ] Create/confirm the Play Console app with public name **myQuote** and package `com.shipaton.quotesofwisdom`.
- [ ] Create and activate `qow_weekly`, `qow_monthly`, and `qow_lifetime`.
- [ ] Configure lifetime as a non-consumable one-time product.
- [ ] Supply the real RevenueCat Google Play public SDK key outside source control.
- [ ] Inject the private Play upload signing key outside the repository.
- [ ] Generate and verify a production-signed Release AAB.
- [ ] Upload first to Internal or Closed testing.

### Billing acceptance

- [ ] Verify weekly/monthly renewal configuration.
- [ ] Verify Current Offering package mappings and localized prices.
- [ ] Test purchase, cancellation, pending purchase, expiry, refund/revocation, restore, reinstall, second-device restore, and offline launch through Play testing.
- [ ] Confirm an inactive RevenueCat response removes Pro and resets the free reminder time to 09:00.

RevenueCat launch reference: <https://www.revenuecat.com/docs/test-and-launch/launch-checklist>

### Policy / support

- [ ] Finalize and host the **myQuote Privacy Policy** and **myQuote Terms of Use**.
- [ ] Replace all owner/contact/jurisdiction placeholders in the drafts.
- [ ] Add production paywall renewal/cancellation disclosure plus Privacy/Terms links.
- [ ] Complete Play Data Safety from the final SDK/data inventory.
- [ ] Add a monitored support contact and practical anonymous-user RevenueCat support/deletion process.
- [ ] Complete content rating, target audience, app access, ads, and deletion declarations.

### Production device acceptance

- [ ] Test Play-installed build on more than one Android device.
- [ ] Verify cold launch, background/resume, process recreation, system Back, and update install.
- [ ] Verify TTS engine/voice edge cases and notification rescheduling.
- [ ] Verify Trial, Grace, Locked, Pro, cancellation/expiry, and restored states.
- [ ] Run TalkBack, large-text, display-scaling, contrast, and reduced-animation checks.

### Final release record

```bash
python3 tools/validate_production_quotes.py app/src/main/assets/quotes.json
./gradlew :app:testDebugUnitTest :app:lintRelease
./gradlew :app:bundleRelease -PREVENUECAT_API_KEY=goog_your_public_sdk_key
```

- [ ] Confirm CI is green at the exact production release commit.
- [ ] Record version code/name, commit, AAB SHA-256, signing certificate, RevenueCat environment, Play track, and rollout percentage.
- [ ] Save rollback/support notes before production rollout.
