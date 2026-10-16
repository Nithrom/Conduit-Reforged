# Conduit Reforged

Fabric mod for **Minecraft 26.3+** that makes conduits portable.

📦 Download: [Modrinth](https://modrinth.com/mod/conduit-reforged)

## How it works
1. Build an active conduit as usual.
2. **Right-click it with an empty hand** and confirm.
3. The prismarine frame is consumed (it turns into water) and the conduit drops **with its power stored**.
4. Equip it in the **Relics** screen (default key: **C**) or keep it in your inventory.

## Power levels
| Level | Frame blocks | Effects |
|---|---|---|
| I | 16 – 27 | Conduit Power |
| II | 28 – 41 | + Dolphin's Grace |
| III | 42 (full) | + Attacks hostile mobs in water within 8 blocks |

The effects only work while you're in water (rain is optional and can be enabled in the config).

## Features
- The tooltip shows the stored level and frame blocks
- Optional confirmation screen before consuming the frame
- Quick swap: **sneak + use** with a portable conduit in your hand
- In-game config: `/conduitreforged config` or Mod Menu
- Configurable: keep the equipped conduit on death

## Compatibility
Shares the Relics screen with [Beacons Reforged](https://github.com/Nithrom/beacons-reforged) and [Totem Slot](https://github.com/Nithrom/Totem-Slot).

## Requirements
- Fabric Loader + Fabric API
- Required on both client and server

## Building
This mod depends on [Relic Slots](https://github.com/Nithrom/Relic-Slots), which is bundled into the jar.
Clone it next to this repo, in a folder named `CereWare Slots`, then run: ./gradlew build
Requires JDK 25.

## License
LGPL-3.0-only
