# Defenders for Forge 1.12.2

<p align="center">
  <img src="media/defenders-logo.png" alt="Defenders logo" width="256">
</p>

Version 1.1.0 of Nanonaitor's off-hand melee Defenders. The same JAR runs with
vanilla Forge or enables its optional RLCraft/Dregora materials when their mods
and ingredients are present.

## Combat

- Hold a Defender in the off hand and right-click to guard.
- A permitted hit in the first 20 ticks is perfectly parried for zero damage.
- Perfect parries apply Slowness III and personal Vulnerable for 40 ticks,
  knock back the attacker, play a sound/particle cue, and cost 2 durability.
- Later permitted hits are reduced by 30% and cost 1 durability.
- Only direct melee damage is permitted by default. Projectiles can never be
  perfectly parried. The config can separately enable magic, explosions, fire, falling, drowning, other
  environmental sources, armor-bypassing sources, or all non-void damage.
- Blocking moves at 50% normal walking speed and still permits main-hand attacks.
- Defenders can be used as fast, short main-hand weapons, but cannot guard from
  the main hand. A second Defender in the off hand contributes its normal bonus.

## Tiers and default damage

Main-hand damage is 80% of the corresponding sword value, rounded down to the
nearest 0.5. Every value and attack speed can be changed in the config.

| Tier | Off-hand bonus | Main-hand damage |
|---|---:|---:|
| Wood | 1 | 3 |
| Stone | 1.25 | 4 |
| Gold | 1.25 | 3 |
| Iron / Umbrium | 2 | 4.5 |
| Diamond | 3 | 5.5 |
| Silver | 2.25 | 4 |
| Bronze | 1.5 | 4.5 |
| Steel | 2.5 | 5 |
| Dragonbone | 3.25 | 6 |
| Flamed / Iced / Electric Dragonbone | 3.5 | 7.5 |
| Desert / Jungle Myrmex and Stinger | 2.5 | 4 |
| Living | 4 | 12 |
| Sentient | 4.5 | 16 |

## Optional compatibility

- Silver adds 2 main-hand damage against undead and perfect parries deal 1
  damage to undead attackers.
- All Myrmex variants add 4 main-hand damage against non-arthropods and retain
  Ice and Fire's separate Death Worm handling.
- Dragon-blooded variants apply their native fire, frozen, or chain-lightning
  behavior on main-hand hits and perfect parries.
- Desert and Jungle Myrmex Stinger variants apply Poison III for 10 seconds on
  main-hand hits and perfect parries. Their recipes replace the top chitin in
  the normal Myrmex Defender recipe with a Myrmex Stinger.
- Living perfect parries apply Immalleable I for 5 seconds. Sentient also
  applies Viral I for 5 seconds.
- Living Defenders record defeated parasites' maximum health in the native
  `srpkills` NBT value, use SRP's native point display, and evolve in the same hand slot at SRP's configured
  Living-to-Sentient threshold. NBT, enchantments, name, lore, and relative
  durability are retained. Parasite-only progression is enabled by default and
  can be changed to all living creatures in the config.
- Optional items have stable registry names but are hidden and have no recipes
  when their source material is unavailable.

## Enchantments

- Footwork I-III raises blocking movement speed to 60%, 70%, and 80%.
- Fortification I-III adds 5 percentage points of guarded reduction per level.
- Reprisal I-III adds its level as damage to the next melee hit after a parry.
- Finesse I-III adds 0.5 melee damage per level while its Defender is equipped
  in the off hand. Finesse has Rare enchantment weight.
- Reflexes I-III adds 2 ticks to the perfect-parry window per level.
- Deflection I-III disables perfect parries and reduces all non-void damage by
  10% per level while blocking. Reflexes and Deflection are incompatible.
- 6th Sense I has a 10% chance to auto-parry direct melee attacks while a
  Defender is equipped off hand. Any successful parry makes the attacker glow
  for 5 seconds. 6th Sense and Deflection are incompatible.
- Unbreaking, Mending, and standard sword enchantments are supported.
- Descriptions appear on enchanted Defenders and enchanted books.
- Modpack authors can add safe optional enchantment registry IDs through the
  `additionalAllowedEnchantments` configuration list.
- A master switch and individual configuration switches can disable Defender
  enchantments from generation and deactivate their effects without removing
  their registry entries from existing worlds.

## Test commands

    /give @p defenders:defender_iron
    /give @p defenders:defender_flamed_dragonbone
    /give @p defenders:defender_living
    /enchant @p defenders:footwork 3
    /enchant @p defenders:fortification 3
    /enchant @p defenders:reprisal 3
    /enchant @p defenders:finesse 3
    /enchant @p defenders:reflexes 3
    /enchant @p defenders:deflection 3
    /enchant @p defenders:sixth_sense 1

## Build

Use Java 11 and the supplied Gradle wrapper:

```powershell
.\gradlew.bat clean build
```

The distributable JAR is created at
`build/libs/defenders-1.12.2-1.1.0.jar`.

See [CHANGELOG.md](CHANGELOG.md) for release notes.

## License

Copyright 2026 Nanonaitor. See [LICENSE.md](LICENSE.md).
