# Quotes of Wisdom — Release Checklist

A checked CI item is not a substitute for physical-device testing.

## A. Shipaton / judge release

### Completed

- [x] RevenueCat uses SDK-generated anonymous App User IDs; no device-derived billing identifier remains.
- [x] RevenueCat `CustomerInfo` is the paid-entitlement source of truth; no persistent Boolean Pro snapshot remains.
- [x] Purchase success requires active `pro_access` in returned `CustomerInfo`.
- [x] Restore without active `pro_access` produces an explicit failure result.
- [x] RevenueCat listener installation is robust when the SDK was already configured in-process.
- [x] Test Store prices come from RevenueCat; the app has no location inference or production fallback prices.
- [x] Production quote validation passes.
- [x] Unit tests pass.
- [x] QA lint passes.
- [x] Debug and QA APKs build successfully.
- [x] Release AAB path validates in CI.
- [x] Stable CI signer verification passes.
- [x] `v1.0.1` / `versionCode 2` built from commit `2c1d9ddb83384f4dc0764059b686dbcdf770762b`.
- [x] Android CI run `34281375036` is green through artifact upload.
- [x] Permanent hardened release published: <https://github.com/noob-express3000/quotes_of_wisdom/releases/tag/v1.0.1>
- [x] Judge APK SHA-256: `3fd037fa01b396c5aef129febf5b4a554fe071f9590cb9e4734ee85fb37f8092`.
- [x] Original `v1.0.0` release retained for provenance.
- [x] README, project state, Shipaton guide, Privacy draft, and Data Safety draft match the anonymous RevenueCat identity model.

### RevenueCat dashboard gate

- [ ] Set the **sandbox/Test Store** restore behavior to **Transfer to new App User ID**.
- [ ] Before Google Play launch, set the production restore behavior to **Transfer to new App User ID** as well.
- [ ] Confirm `pro_access`, `qow_weekly`, `qow_monthly`, and `qow_lifetime` remain attached to the Current Offering as expected.

### Physical smoke

Use `Quotes-of-Wisdom-v1.0.1-judge.apk` from the permanent release.

- [ ] Clean install and cold launch.
- [ ] Trial paywall is dismissible; Info and Close work repeatedly.
- [ ] Weekly, Monthly, and Lifetime show `$0.99`, `$2.99`, and `$29.99` in Test Store.
- [ ] Unavailable/loading plans cannot be purchased and never show invented prices.
- [ ] Lifetime Test Store purchase activates `PRO`.
- [ ] Force-stop/reopen online preserves Pro.
- [ ] Offline reopen follows RevenueCat cached-entitlement behavior without an app-owned fallback database.
- [ ] Reinstall/new anonymous identity + Restore Purchases recovers Pro after the sandbox restore setting is configured.
- [ ] Trial/non-Pro renders default Lora and gates quote-font selection behind `See Pro`.
- [ ] Pro exposes all quote fonts, themes, TTS controls, and custom reminder time.
- [ ] Replay, Next, Favorite, Share, settings, system Back, streak effect, Pro spin, and notifications work.
- [ ] Repeat the critical path on another Android device if practical.

### Submission assets

- [ ] Record the concise demo in `docs/SHIPATON_SUBMISSION.md`.
- [ ] Capture clean portrait screenshots.
- [ ] Add the public repository and `v1.0.1` GitHub Release to the submission.
- [ ] Add BuildInPublic evidence.
- [ ] Re-check the live Shipaton form immediately before submission.

## B. Google Play production release

The QA judge APK must not be uploaded to Google Play.

### Play / signing

- [ ] Create/confirm Play Console app `com.shipaton.quotesofwisdom`.
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

- [ ] Finalize and host Privacy Policy and Terms.
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
