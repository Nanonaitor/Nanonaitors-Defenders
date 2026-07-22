# Nanonaitor's Defenders for Forge 26.1.2

<p align="center">
  <img src="src/main/resources/assets/defenders/logo.png" alt="Nanonaitor's Defenders logo" width="256">
</p>

Version 1.1.1 of Nanonaitor's off-hand melee shield-weapons, ported to
Minecraft 26.1.2 and Forge 64.0.12.

## Combat

- Equip a Defender off hand and hold Use to guard.
- Hits during the first second are perfectly parried for zero damage.
- Perfect parries slow and expose the attacker, knock it back, play a clear
  confirmation cue, and consume durability.
- Continued guarding reduces incoming melee damage by 30% and consumes
  durability.
- Projectiles are not blocked by the base Defender mechanic.
- Defenders add flat melee damage and attack speed while equipped off hand.
- Defenders can also serve as fast, short main-hand weapons, but they only guard
  from the off hand.
- Wood, Stone, Copper, Gold, Iron, Diamond, and Netherite tiers are included.
- Netherite Defenders use the vanilla smithing-table upgrade progression.

## Enchantments

- Footwork I-III reduces the movement penalty while guarding.
- Reprisal I-III empowers the next melee attack after a perfect parry.
- Finesse I-III adds 0.5 off-hand melee damage per level.
- Reflexes I-III extends the perfect-parry window.
- Deflection I-III improves damage reduction while blocking.
- 6th Sense I can automatically parry a melee attack and makes parried attackers
  glow.

Defenders also support compatible vanilla weapon, Unbreaking, and Mending
enchantments.

## Configuration

The generated common configuration controls parry timing, recovery, damage
reduction, supported damage categories, movement penalties, durability costs,
sound volume, and combat values.

## Version branches

This branch contains only the Minecraft 26.1.2 port. The `1.12.2` and `1.20.1`
branches preserve their respective releases independently.

## Build

Use Java 25 and the supplied Gradle wrapper:

```powershell
.\gradlew.bat clean build
```

The distributable JAR is created at
`build/libs/defenders-26.1.2-1.1.1.jar`.

See [CHANGELOG.md](CHANGELOG.md) for release notes.

## License

Copyright 2026 Nanonaitor. See [LICENSE.md](LICENSE.md).
