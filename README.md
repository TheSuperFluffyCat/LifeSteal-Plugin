# LifeSteal Plugin Version 2

A lightweight, highly configurable Minecraft plugin introducing a **LifeSteal mechanic**. Perfect for servers of any size, from small communities to large-scale networks!

## Features
- **LifeSteal Mechanic**: Players gain hearts by defeating others in PvP.
- **Heart Items**: Withdraw hearts into items and redeem them later.
- **Configurable**: Adjust max/min health, heart item names, and messages with full Adventure MiniMessage support.
- **Commands**:
  - `/withdrawheart <amount>`: Withdraw hearts into tradable items.
  - `/resethearts <player>`: Reset a player's health to default.
  - `lifestealplugin reload`: Reloads the config of LifeSteal

## How It Works
- **Death Mechanics**: Players lose 1 heart (2 health) upon death, and the killer gains 1 heart (if below max health).
- **Item Interactions**: Heart can be redeemed, and traded.

## Why Choose LifeSteal?
- **Performance**: Designed to run smoothly, even on large servers with many players.
- **Flexibility**: Fully customizable via `config.yml`, making it adaptable to your server's unique needs.
- **Adventure MiniMessage**: Use formatted text for action bars and messages.
- **Fast Dupe / Bug Fixes**: I update my GitHub side fast with fixes and if everything works 100% i will upload them to spigot or modrinth
- **Open Source**: The Projekt is fully open source 

## Installation
1. Download the `.jar` file.
2. Place it in your server's `/plugins` folder.
3. Start your server and configure `config.yml` to fit your server's style.


## Future plans on Version 2

1. PlayerDeathListener logic ✅
2. Dupe fixing ✅
3. Reload command ✅
4. Crafteble Heart Items with config settings ⭕
5. People with 0 heart get banned and can be respawened with a special item
   by other people - config ⭕


(Don't reupload this projekt as yours)
---

💡 *Perfect for PvP-focused gameplay or unique server concepts – scales seamlessly for small and large servers alike!*
