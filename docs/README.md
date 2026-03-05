# LightningFE Documentation

Welcome to the internal documentation for LightningFE.

## Overview
LightningFE is a lightweight server-side NeoForge mod that augments the vanilla Lightning Rod to act as a massive power generator when struck by lightning.

## Design Philosophy
The mod strictly avoids adding new blocks, items, or BlockEntities. It uses a `VanillaGameEvent` listener to detect lightning strikes and distributes a massive, single-tick FE burst (20,000,000 FE by default) to any adjacent blocks that support the NeoForge `IEnergyStorage` capability.

- **No Storage:** The rod does not buffer energy.
- **Single-Tick Delivery:** All energy distribution happens in the exact tick the lightning strikes.
- **Lossy:** Any energy that cannot be immediately accepted by adjacent blocks is lost.
- **Dimension-Safe:** It includes a per-tick duplicate guard per block position and dimension to ensure overlapping strikes don't double-charge incorrectly.

## Directory Structure
- `DECISIONS.md`: Architectural decisions and goals.
- `PORTING.md`: Target platforms and versioning information.
- `LOG.md`: Implementation log and phase history.
- `NEXT.md`: Pending tasks (e.g., config, balancing).
- `PROMPTS.md`: AI system prompt context.