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

When you're not holding anything out, your fists function as a light weapon.
