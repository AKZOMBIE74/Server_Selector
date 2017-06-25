# Server Selector/ServSel
This plugin lets you select servers connected to your Bungeecord server.
All of this plugin was written by me.
The commits you see made by "invalid-email-address"are commits being pushed from my Intellij IDE and those commits are being pushed by me :).

How To Install
======
1. Download the plugin from either https://www.spigotmc.org/resources/servsel.14485/ or https://dev.bukkit.org/projects/server-selector
2. Put the plugin in your server's "plugins" folder.
3. You are done. Now you can just start the server and hopefully enjoy this plugin.

Config.yml (Supports Color Codes!)
======
[My example config.yml (contains tutorial)](/src/config.yml)

Lang.yml (Supports Color Codes!)
======
  [Default lang.yml](/src/lang.yml)
  
  **Placeholders:**
  + server-not-found-message
    + %pn - Displays the teleporting player's name.
    + %pdn - Displays the teleporting player's display name.
    + %s - Displays the server name
  + teleported-message
    + %pn - Displays the teleporting player's name.
    + %pdn - Displays the teleporting player's display name.
    + %s - Displays the server name
  + only-players-message
    + **_None Exist!_**
    
Command
======
  + /ss [server name] - Allows the player executing the command to connect to the specified server.
