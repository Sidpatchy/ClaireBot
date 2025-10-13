# API Reference

| Endpoint                      | Method | Description         | Documentation                           |
|-------------------------------|--------|---------------------|-----------------------------------------|
| ```/api/v1/user```            | GET    | Retrieve all users  | [Get all users](Get_all_users.md)       |
| ```/api/v1/user```            | POST   | Create a new user   | [Create new user](Create_new_user.md)   |
| ```/api/v1/user/{userID}```   | GET    | Get user by ID      | [Get used by ID](Get_user_by_ID.md)     |
| ```/api/v1/user/{userID}```   | PUT    | Update user         | [Update user](Update_user.md)           |
| ```/api/v1/user/{userID}```   | DELETE | Delete user         | [Delete user](Delete_user.md)           |
| ```/api/v1/guild```           | GET    | Retrieve all guilds | [Get all guilds](Get_all_guilds.md)     |
| ```/api/v1/guild```           | POST   | Create a new guild  | [Create new guild](Create_new_guild.md) |
| ```/api/v1/guild/{guildID}``` | GET    | Get guild by ID     | [Get guild by ID](Get_guild_by_ID.md)   |
| ```/api/v1/guild/{guildID}``` | PUT    | Update guild        | [Update guild](Update_guild.md)         |
| ```/api/v1/guild/{guildID}``` | DELETE | Delete guild        | [Delete guild](Delete_guild.md)         |

All endpoints require Basic Authentication and accept/return JSON data.

