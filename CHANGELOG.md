# Changelog

## 1.1.0 - Enchantments and modpack configuration

- Added the very rare 6th Sense I enchantment.
- 6th Sense has a configurable 10% chance to auto-parry an otherwise damaging
  direct-melee attack while its Defender is equipped off hand.
- Manual and automatic parries with 6th Sense apply vanilla Glowing to the
  attacker for a configurable 5 seconds.
- Auto-parries use normal perfect-parry durability and recovery costs.
- 6th Sense cannot auto-parry projectiles and is incompatible with Deflection.
- Added a dedicated configuration switch for 6th Sense availability.
- Added a master switch for all Defender enchantments.
- Added individual enable switches for Footwork, Fortification, Reprisal,
  Finesse, Reflexes, Deflection, and 6th Sense.
- Disabled enchantments no longer generate on books or enchanting tables and
  provide no effects, while remaining registered for save compatibility.
- Added Reflexes I-III, extending the perfect-parry window by two ticks per level.
- Added Deflection I-III, reducing all non-void damage by 10% per level while
  blocking but disabling perfect parries.
- Made Reflexes and Deflection mutually incompatible.
- Projectiles can no longer be enabled for perfect parries through configuration.
- Added configurable perfect-parry sound volume.
- Added a registry-ID allow-list for optional modpack enchantment compatibility.
- Added explicit vanilla Mending support for Defenders.
- Confirmed and retained vanilla Unbreaking support.
- Added optional So Many Enchantments Advanced Mending compatibility by
  registry ID, without making the mod a required dependency.

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
