# Changelog

## 1.0.1 - Guarded attack fix

- Fixed entity attacks being suppressed while blocking with a Defender.
- Guarded attacks now use Minecraft's normal attack packets, cooldown damage,
  enchantments, reach handling, and compatible combat hooks.
- Fixed guarded hit and air-whiff swing behavior.
- Added normal block breaking while guarding.
- Removed the obsolete custom guarded-attack network path.

## 1.0.0 - Initial release

- Added Wood, Stone, Gold, Iron, and Diamond Defenders.
- Added optional Bronze, Steel, Silver, Umbrium, Dragonbone, dragon-blooded,
  Myrmex, Myrmex Stinger, Living, and Sentient variants.
- Added perfect parries, sustained melee guarding, attacks while blocking,
  durability costs, and configurable 50% blocking movement speed.
- Added configurable main-hand and off-hand combat values.
- Added Footwork, Fortification, Reprisal, and Finesse enchantments.
- Added native-style Silver, Myrmex, poison, elemental Dragonbone, and
  Scape and Run: Parasites compatibility effects.
- Added parasite-only Living-to-Sentient evolution using SRP's `srpkills`
  progression and native point display, with configurable kill eligibility.
- Added conditional recipes that keep the mod safe in vanilla Forge instances.
- Added original item sprites, first-person blocking models, localized effect
  tooltips, combined Finesse off-hand stats, and modpack configuration.
