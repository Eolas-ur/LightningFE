# LightningFE

## What It Does
LightningFE augments the vanilla Lightning Rod. When struck by lightning, the rod emits a single-tick burst of 20,000,000 FE into adjacent blocks.

## What It Does Not Do
It does not store or buffer energy. Excess energy that cannot be immediately accepted by adjacent blocks is discarded. It does not replace the vanilla Lightning Rod block or add a BlockEntity.

## Technical Behaviour
- Single tick delivery
- 20,000,000 FE per strike
- No internal storage
- Server-side only logic
- Vanilla Lightning Rod unchanged

## Compatibility
Compatible with any mod using the NeoForge Energy capability.

## License
MIT License
Copyright (c) 2026 Eolas-Ur