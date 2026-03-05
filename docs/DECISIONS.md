# LightningFE Architectural Spec & Decisions

## Project Name
LightningFE

## Objective
Augment the vanilla Lightning Rod so that when it is struck by lightning it emits a massive, single tick FE burst to adjacent blocks.

## Targets
Minecraft 1.21.11 on NeoForge 21.11.38-beta (Java 21).

## Non-goals
- No energy storage.
- No buffering. Any unaccepted FE is discarded immediately.
- Do not replace the Lightning Rod block.
- Do not add a BlockEntity.
- Do not use mixins.
- No new blocks, items, worldgen, UI, config screens in Phase 1.

## Behaviour
- Emits a massive, single tick FE burst to adjacent blocks when the vanilla Lightning Rod is struck by lightning.
- Default `fePerStrike` = 20,000,000 FE.

## Acceptance Criteria
- Place lightning rod adjacent to at least one FE battery.
- Trigger lightning naturally and via channeling trident during a thunderstorm.
- Observe battery charge increase.
- Test with no adjacent FE blocks (ensure no crash).
- Test multiple adjacent FE blocks (ensure distribution occurs in order of faces checked).

## Decisions
- Server authoritative only. No client side logic beyond what is required for loading.
- FE only. Use NeoForge Energy capability only.
