# Minecraft Mod - Critical Combat

A multiloader (Fabric + NeoForge) Minecraft mod that brings variety to vanilla's combat through various new weapon types.

## License

Critical Combat is licensed under the [GNU General Public License v3.0](LICENSE).

## Overview

Critical Combat - Minecraft Mod

5 weapon classes:
**Defaults** which deal crits at full charge while falling (vanilla combat).
**Blades** which can block, deal aoe damage at full charge (innate sweeping edge), and crit when your camera is moving fast at full charge.
**Light Weapons** which function like 1.8, deal reduced knockback, and deal crits when up close.
**Heavy Weapons** which have to charge up before swinging and can be charged up extra to deal a crit. charging slows you down, just like charging a bow. swings in front of you in an arc, hitting all entities in range of you. Heavy weapons require two hands, disabling whatever's in your offhand slot and displaying a special holding animation. Requires charging up to mine blocks, too.
**Polearms** which crit at full charge while sprinting, and can be thrown.

New tool properties:
pickaxes have 50% armor pen
shovels deal upwards knockback instead of forwards knockback
hoes break and automatically replant mature crops in a 3x3

New tool type: knives
light weapons
same functionality as shears (silk touch + faster mining on foliage)
when farmer's delight is installed, removes this mod's knives in favor of theirs

New tool type: hammers
heavy weapons
breaks the same type of blocks that pickaxes break
breaks multiple blocks at once
has special interactions with certain blocks (cracks bricks, chance to turn cobblestone into pebbles when NML is installed...)
by default, this is just the mace, a new sledgehammer item, and a new cudgel item (one stick and two logs)

Default polearms are the trident, a new "spear" item made from flint and two sticks, vanilla spears if they exist, and pikes if spear fishing is installed.

When tools are capable of critically hitting, the item in your hotbar will twinkle to give a hint and to give feedback. 

When an item is not assigned to a weapon class, it cannot critically hit.

## DEV

### Feature 1 - Crit Criteria

_Assign weapons "types" that each follow unique crit criteria._

Tasks
- ~~LIGHT-type weapons crit when up-close~~
- HEAVY-type weapons crit when their charge is 200%
- ~~BLADE-type weapons crit when the camera/player is moving fast~~
- ~~POLEARM-type weapons crit while sprinting~~
- ~~DEFAULT-type weapons follow vanilla crit logic~~

### Feature 2 - Light Weapons 1.8 Combat

_Bring 1.8 combat to knives from Farmer's Delight._

Tasks
- ~~Attack speed attribute is now used for bypassing immunity ticks for damage. 2.0 speed bypasses 5/10 immunity ticks, and 4.0 bypasses 9/10 immunity ticks.~~
- ~~Mobs wielding LIGHT-type weapons will attack twice as fast~~
- ~~"Heat" that accrues with each hit that successfully does damage, reducing your damage as your heat gets lower to a minimum of 50%. Persists even if you swap away from the item (heat is bound to the player through CCPlayerExtension)~~
- ~~Deals reduced knockback when not critting + knockback scales with heat.~~
- ~~Attack indicator feedback for heat and swinging~~
- ~~Spawns a tiny slash particle and plays a very light/faint slash sound each swing~~
- ~~Alternate the slash direction of the slash particles if you're swinging in rapid succession~~
- ~~Cooldown for LIGHT-type weapons to lose durability from attacking~~

### Feature 3 - Dagger

_Add a unique LIGHT-type weapon to the base game._

Tasks
- Add a "Black Shard" item, found rarely in nether fortress chests
~~- Add a "Dagger" item, crafted from putting a Black Shard on top of a Blaze Rod~~
~~- Create datagen system for item models, tags, recipes, etc.~~
- Add a "Detonation" enchantment for the Dagger, which makes your attacks explode and deal 10x knockback at >= 0.9 heat (resets your heat to 0 on proc)
- Add a "Richochet" enchantment for the Dagger, which makes the knife projectile bounce between nearby enemies before bouncing back in your direction
- Give the Dagger compatibility with loyalty, sharpness, unbreaking, smite, bane of arthropods, and fire aspect
- Add a "Blitz" enchantment for the Dagger, which teleports you to
enemies hit by your projectile, instantly maxes out your heat, and makes you temporarily immune to taking damage from them. Also instantly returns the dagger to your inventory on successful hits. Exclusive with richochet and loyalty.

### Feature 4 - Two-handed Items

_Make certain items require two hands to hold, disabling your offhand._

Tasks
- Refactor the WeaponType enum into its own file
- Change the WeaponTypes class which handles WeaponType mappings into a new ItemWeaponData class, which also contains a static helper method for detecting when an item is two-handed
- Procedurally grant two-handed status to items below a config-driven attack speed
- Manually grant two-handed status to items defined in a two_handed tag
- Define the spear and the mace in the default two_handed tag file
- Disable the ability to use your offhand slot while holding a two-handed item in your mainhand slot
- Disable two-handed items from being able to be placed in the offhand slot, manually or through the F keybind
- Render the offhand slot and its item in your HUD as slightly greyed-out and transparent
- Quiver the offhand slot in your HUD slightly when attempting to put a two-handed item into it with the F keybind (only while it's visible)

### Feature 5 - Blade Weapons

_Give BLADE-type items innate sweeping edge on every swing, and add functionality for parrying with them as substitutes for shields._

Tasks
- Give blade-type weapons unique interact functionality, wired through CCItemExtension, where they block attacks in a 0.5s (configurable) window.
- Unique first-person animation for blocking that resembles 1.8 blocking. The sword falls down a bit after the block window closes.
- Show the shield block icon when your parry window is active, and show a smaller gray shield icon after your parry window closes.
- Make all swings with blade-type weapons activate sweeping edge, whether you landed a hit or not
- Alternate the slash direction of the sweeping edge particles if you're swinging in rapid succession

### Feature 6 - Heavy Weapons

_Add a HEAVY classification for items that changes their attack/destroy functionality to be based on charging up and releasing._

Tasks
- When an item is marked as HEAVY, delay attack/destroy to mouseRelease
- Make mouseDown begin charging, and stop charging on mouseRelease
- Charge goes from 0% to 100% in the same time it would take for the item's attack cooldown to refill from empty
- Scale attack knockback with charge level, mapping to charge level identically to how it maps to attack cooldown for regular items.
- Charging goes over a "hump" at 50%, pausing for a moment and then continuing
- Being over 60% charge makes attacks spawn a vertical AoE, just like sweeping edge but without slash particles. The AoE gets wider horizontally as your charge increases.
- Swinging plays a deep, low-pitched swoosh noise that's louder and lower-pitched the more you charged up and combines with another "juicier" sound above 60% charge
- Restrict critting with heavy weapons to require 90% charge
- Landing a crit with a heavy weapon plays a subtle "crunch" sound on top of the regular crit sound
- Knockback is doubled when you crit
- Swinging above 60% charge spawns an "arc" particle that follows the intended arc of your swing. Swinging above the crit threshold uses a chunkier, more intense arc particle.

### Feature 7 - Heavy Mining

_Make HEAVY-type items mine blocks when you smack them, leaving lasting cracks that you can build upon to break large areas over the course of multiple swings._

Tasks [WIP]
- Damage dealt to blocks with HEAVY-type items persists for longer and slowly wears off instead of immediately disappearing

### Feature 8 - Animations

_Give weapons unique animations in the first and third person based on their class and two-handedness._

Tasks
- Unique first-person animation for holding two-handed items
- Unique first-person animation for swinging two-handed items
- Unique third-person animation for holding two-handed items
- Unique third-person animation for swinging two-handed items
- Unique third-person animation for blocking with BLADE-type items
- Unique third-person animation for blocking with two-handed BLADE-type items
- Unique first-person animation for charging HEAVY-type items
- Unique first-person animation for swinging HEAVY-type items
- Unique third-person animation for charging HEAVY-type items
- Unique third-person animation for swinging HEAVY-type items
- Unique first-person animation for charging two-handed HEAVY-type items
- Unique first-person animation for swinging two-handed HEAVY-type items
- Unique third-person animation for charging two-handed HEAVY-type items
- Unique third-person animation for swinging two-handed HEAVY-type items
- Unique first-person animation for swinging LIGHT-type items (slashes instead of smacking downward, and alternates directions if done in rapid succession)
- Unique third-person animation for swinging LIGHT-type items (slashes instead of smacking downward, and alternates directions if done in rapid succession)
- Unique first-person animation for holding BLADE-type items (tilted sideways)
- Unique first-person animation for swinging BLADE-type items (slashes instead of smacking downward, and alternates directions if done in rapid succession)
- Unique third-person animation for swinging BLADE-type items (slashes instead of smacking downward, and alternates directions if done in rapid succession)
- Unique first-person animation for swinging POLEARM-type items (stabs forward)
- Unique third-person animation for swinging POLEARM-type items (stabs forward)
- Unique first-person animation for swinging two-handed POLEARM-type items (stabs forward)
- Unique third-person animation for swinging two-handed POLEARM-type items (stabs forward)
- Ensure compatibility with EMF/ETF
- Ensure compatibility with Sodium
- Ensure compatibility with Veil
- Ensure compatibility with PlayerAnimator
- Ensure compatibility with the FirstPersonAnimations mod
- Ensure compatibility with Create Aeronautics (Flywheel)
- Document Hold My Items (HMI) as incompatible in the README
- Client config toggle for custom two-handed animations
- Client config toggles for custom weapon type animations

### Polish

- Add arrows to the deflectable_projectiles entity tag
- Unique "stab" attack particle for polearms
- ~~Remove the extra durability loss from attacking with digging tools~~
