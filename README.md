# Nanonaitor's Defenders for Forge 1.20.1

<p align="center">
  <img src="src/main/resources/assets/defenders/logo.png" alt="Nanonaitor's Defenders logo" width="256">
</p>

Version 1.1.1 of Nanonaitor's off-hand melee Defenders, ported to Minecraft
1.20.1 and Forge 47.4.18. Optional materials and effects activate only when
their source mods are installed.

## Combat

- Equip a Defender off hand and hold Use to guard.
- A permitted hit during the first 20 ticks is perfectly parried for zero damage.
- Perfect parries apply Slowness III and Vulnerable for 40 ticks, knock back the
  attacker, play a sound/particle cue, and cost durability.
- Later permitted hits are reduced by 30% and cost durability.
- Only direct melee damage is guarded by default. Projectiles cannot be parried
  or blocked by the base Defender mechanic.
- Attacking and uninterrupted block breaking continue while guarding. A Defender
  can be raised or lowered without releasing the attack/mining key.
- Forgotten Nunchakus held attacks continue while entering, holding, and leaving
  Defender guard when that optional mod is installed.
- Shield-disabling weapons can disable an active Defender.
- Defenders work as fast, short main-hand weapons but only guard from the off hand.

## Tiers and default stats

The standard main-hand attack speed is 1.8. Gold uses 2.0. Every listed value is
configurable.

| Tier | Off-hand damage | Off-hand attack speed | Main-hand damage |
|---|---:|---:|---:|
| Wood | 1 | +0.15 | 3 |
| Stone | 1.25 | +0.05 | 4 |
| Copper | 1.5 | +0.10 | 4.5 |
| Gold | 1.25 | +0.25 | 3 |
| Iron | 2 | +0.10 | 4.5 |
| Diamond | 3 | +0.15 | 5.5 |
| Netherite | 3.5 | +0.10 | 6.5 |
| Bronze | 1.5 | +0.10 | 4.5 |
| Silver | 2.25 | +0.20 | 4 |
| Steel | 2.5 | +0.05 | 5 |
| Umbrium | 2 | +0.15 | 4.5 |
| Dragonbone / dragon-blooded | 3.25 / 3.5 | +0.10 | 6 / 7.5 |
| Desert / Jungle Myrmex and Stinger | 2.5 | +0.20 | 4 |
| Living | 4 | +0.15 | 12 |
| Sentient | 4.5 | +0.20 | 16 |

## Material identities

- Wood has a smaller movement penalty while guarding.
- Stone perfect parries have stronger knockback.
- Gold retains high enchantability and attacks faster.
- Diamond perfect-parry debuffs last one additional second.
- Steel guards 5 percentage points more sustained damage.
- Umbrium has high enchantability with Iron-equivalent combat stats.
- Silver adds 2 main-hand damage against undead; perfect parries deal 1 damage
  to undead attackers.
- Myrmex variants add 4 main-hand damage against non-anthropods and Death Worms.
- Myrmex Stinger variants also apply Poison III for 10 seconds on main-hand hits
  and perfect parries.
- Flamed, Iced, and Electric Dragonbone variants apply elemental effects on
  main-hand hits and perfect parries.
- Living perfect parries apply Immalleable I for 5 seconds when available.
  Sentient parries additionally apply Viral I for 5 seconds.

## Enchantments

- Footwork I-III: reduces the movement penalty while guarding.
- Reprisal I-III: adds damage to the next melee hit after a perfect parry.
- Finesse I-III: adds 0.5 off-hand melee damage per level.
- Reflexes I-III: extends the perfect-parry window by 2 ticks per level.
- Deflection I-III: adds 5% melee guard reduction per level or reduces other
  damage by 10% per level while blocking. Perfect parries remain available.
- 6th Sense I: 10% chance to auto-parry direct melee attacks; successful parries
  make the attacker glow for 5 seconds.

Deflection cannot combine with Reflexes or 6th Sense. Unbreaking, Mending,
standard sword enchantments, compatible Advanced Mending enchantments, and
configured optional enchantments are supported.

## Optional integrations

- Ice and Fire: Dragons
- Spartan Weaponry
- Defiled Lands: Reborn
- SRP Spartans
- Forgotten Nunchakus
- Enchantment Descriptions

Missing optional mods do not prevent the vanilla, Copper, or Netherite tiers
from loading. Optional Defender recipes use Forge mod-loaded conditions.

Living Defenders use the `srpkills` NBT value and evolve into Sentient Defenders
at the configured threshold. Parasite-only progression is enabled by default;
modpacks without parasite entities can enable progression from all kills.

## Configuration

The generated `config/defenders-common.toml` controls parry timing and recovery,
damage reduction, supported non-projectile damage categories, main/off-hand
stats, movement, durability, sound volume, material traits, enchantments, and
Living Defender evolution.

## Build

Use Java 17 and the supplied Gradle wrapper:

```powershell
.\gradlew.bat clean build
```

The distributable JAR is created at
`build/libs/defenders-1.20.1-1.1.1.jar`.

See [CHANGELOG.md](CHANGELOG.md) for release notes.

## License

Copyright 2026 Nanonaitor. See [LICENSE.md](LICENSE.md).
