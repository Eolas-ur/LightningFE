# LOG

## Phase 2 Test Plan and Results
- Place lightning rod adjacent to at least one FE battery. Verify battery charge increases.
- Place lightning rod adjacent to multiple FE blocks. Verify distribution occurs in the order of faces checked and excess is discarded.
- Trigger lightning naturally and via channeling trident during a thunderstorm. Verify it triggers the FE burst.
- Test with no adjacent FE blocks. Verify no crash occurs.
## Phase 2A-2E Updates
- Package refactor completed (personal identifier removed).
- Event migrated to VanillaGameEvent.
- Duplicate guard made dimension-safe.
## Phase 3 Updates
- Added MIT LICENSE.
- Added comprehensive README.md.
- Updated `neoforge.mods.toml` and `gradle.properties` metadata for release.
- Locked version to 0.1.0 using dynamically linked property `${file.jarVersion}`.

## Phase 4 Updates
- Upgraded target platform to Minecraft 1.21.11 and NeoForge 21.11.38-beta.
- Bumped version to 0.2.0.
- Migrated code to the new `net.neoforged.neoforge.transfer.energy.EnergyHandler` API and `Transaction` system.
- Replaced deprecated `level.dimension().location()` usage with `level.dimension().toString()`.