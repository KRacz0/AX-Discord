# AX-Discord Plugin

The `AX-Discord` plugin facilitates the integration of Minecraft servers with Discord, enabling automatic synchronization of accounts and roles. This advanced tool enhances the interaction between players on both platforms.

## Features

- **Account Synchronization:** Allows players to generate a unique code to link their Minecraft account with their Discord account.
- **Database Integration:** Supports MySQL for data management.
- **GUI:** An intuitive user interface for easy synchronization management.
- **LuckPerms Integration:** Automatically updates Discord roles based on in-game rank changes.
- **PlaceholderAPI Support:** Utilizes placeholders to display the account synchronization status.

## Prerequisites & Dependencies

- **Spigot:** Designed for Spigot, tested on Minecraft version 1.19.
- **Java:** Developed using JDK 17.
- **Databases:** MySQL support.
- **Dependencies:**
  - **HikariCP:** For database connection pooling.
  - **JDA:** For Discord integration.
  - **LuckPerms:** For managing roles and permissions.
  - **PlaceholderAPI:** For handling placeholders within the plugin.

## Installation

1. Clone or download the `AX-Discord` plugin source code from the repository.
2. Navigate to the project directory and execute the Maven command `mvn clean install` to fetch dependencies and build the `.jar` file.
3. Locate the `.jar` file in the `target` directory post-build.
4. Place the `.jar` file into your server's `plugins` folder.
5. Start the server to generate configuration files.
6. Adjust the plugin settings as needed using the `config.yml` file.
7. Restart the server to apply changes.

## Configuration

The AX-Discord plugin offers extensive configuration options in the `config.yml` file, allowing customization of features such as database connections and synchronization settings. Below is an example configuration:

```yaml
database:
  host: "localhost"
  port: "3306"
  name: "minecraft"
  username: "username"
  password: "password"

bot_token: "YOUR_TOKEN"
channel_id: "CHANNEL_ID"
guild_id: "GUILD_ID"

verified_role_id: "000000000000000000"

GroupRoleSynchronization:
  svip: "000000000000000000"
  vip: "000000000000000000"

messages:
  minecraft:
    already-synced: '&cYour account is already synchronized!'
    code-generated: '&aYour code is: &e%code% &aJoin our Discord server &5discord.gg/YourServer &aand use your code on the channel 「」skyblock'
    no-permission: '&cYou do not have permission to use this command!'
    incorrect-usage: '&cIncorrect command usage! Use /discord or /discord reload.'
    successful-sync-broadcast: "&aPlayer %player% successfully synchronized their Discord account!"
  discord:
    code-accepted: "**Congratulations!** You've received your reward."
    code-acceptedOffline: "The code has been accepted, but you are not online on the server!"
    code-invalid: "Invalid code!"

commands_for_link:
  - "give %player% diamond 1"
```
## Configuration Description
`database`: Configuration for MySQL database connections.
`bot_token`, channel_id, guild_id: Required for setting up the Discord bot communication.
`verified_role_id`: The ID of the role assigned on Discord after successful account synchronization.
`GroupRoleSynchronization`: Mapping of Minecraft server ranks to Discord roles.
`messages`: Customizable messages for various plugin interactions.
`commands_for_link`: Commands executed on the Minecraft server upon successful Discord synchronization.

## Commands
`/discord`: Generates a unique code for account synchronization.
`/discord reload`: Reloads the plugin's configuration.


## Permissions
`axdiscord.reload`: Allows reloading the plugin's configuration.

## Support
For any issues related to the plugin, please contact us via GitHub.

## License
This project is licensed under the MIT License. More details can be found in the [LICENSE](https://github.com/KRacz0/AX-Discord/blob/master/LICENSE) file.
