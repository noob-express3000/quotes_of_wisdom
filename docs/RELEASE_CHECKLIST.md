# Production release checklist

## Release configuration

- [ ] Increment `versionCode` for every uploaded build.
- [ ] Confirm `versionName` matches the public release.
- [ ] Supply the RevenueCat Google Play public SDK key with `-PREVENUECAT_API_KEY=goog_...`.
- [ ] Keep the upload keystore and passwords outside the repository.
- [ ] Generate a signed release app bundle and verify its certificate.
- [ ] Upload the bundle to a closed or internal testing track before production.

## RevenueCat and Google Play

- [ ] Confirm the `pro_access` entitlement is active.
- [ ] Confirm the current offering includes weekly, monthly, and lifetime packages.
- [ ] Confirm package products map to `qow_weekly`, `qow_monthly`, and `qow_lifetime`.
- [ ] Confirm localized store prices and billing periods match the paywall.
- [ ] Test successful purchase, cancellation, restore, pending purchase, and offline launch.
- [ ] Test entitlement loss and verify the reminder returns to 09:00.

## Device acceptance

- [ ] Run the optimized QA build on the Samsung and itel devices.
- [ ] Verify the launcher Q in normal, round, and themed-icon modes.
- [ ] Verify cold launch, background/resume, rotation, process recreation, and system Back.
- [ ] Verify local and network TTS voices, missing voice data, replay, and engine switching.
- [ ] Verify notifications after permission grant, denial, reboot, clock change, and timezone change.
- [ ] Verify the 30-day trial, text-only grace state, locked state, and Pro state.
- [ ] Verify all paywall products display live localized prices before they become tappable.
- [ ] Run an accessibility pass with TalkBack, large text, display scaling, and reduced animations.

## Store and policy

- [ ] Publish a privacy-policy URL that accurately describes RevenueCat purchase processing and the device-scoped app user identifier.
- [ ] Complete the Google Play Data safety form from the final SDK inventory.
- [ ] Add support contact details and store-listing screenshots.
- [ ] Confirm the content rating, target audience, ads declaration, and app-access declarations.
- [ ] Verify the final store listing contains no trial or pricing claims that differ from Google Play and RevenueCat.

## Final verification

```bash
python3 tools/validate_production_quotes.py app/src/main/assets/quotes.json
./gradlew :app:testDebugUnitTest :app:lintRelease
./gradlew :app:bundleRelease -PREVENUECAT_API_KEY=goog_your_public_sdk_key
```

- [ ] Confirm CI is green at the exact release commit.
- [ ] Download and install the build produced from that commit.
- [ ] Record the release commit, version code, bundle digest, and rollout percentage.
