# Overview

> This documentation site is currently a work in progress. While efforts have been made to ensure accuracy, some 
> details may be incomplete or subject to change. Critical information should be verified independently. Once the 
> initial version is finalized, the documentation will be open-sourced and available at 
> [Sidpatchy/ClaireBot-Docs](https://github.com/Sidpatchy/ClaireBot-Docs).
{style="warning"}

Documentation for ClaireBot, ClaireData, and ClaireBot Docker all in one place, authored using Jetbrains Writerside.

To contribute to these docs, or ClaireBot in general, please see: [Contributing](Contributing-guide.md)

## What is ClaireBot?

ClaireBot is a Discord bot written in Java with a focus on ease-of-use and beauty.

### Background
ClaireBot 1 and 2 were initially built for a group of friends to use, and it was only used on a handful of servers.

As time went on, I grew more and more interested in using ClaireBot elsewhere, in my public servers, so I rewrote bits
and pieces of her to work in different servers, and, eventually had rewritten nearly everything. This was ClaireBot 2.

Over time, I wanted more, and more. But ClaireBot 2 was still using an architecture very similar to the original. It was
a pain to continue developing new features as I'd find myself tripping over technical debt nearly constantly, but I
persisted. It wasn't worth taking the time to rewrite ALL of ClaireBot, at least that's what I had told my self.

Eventually, Discord.py was discontinued, this forced my hand. If I wanted to keep developing ClaireBot, I would have
to rewrite it from the ground up. Thus, ClaireBot 3 was born.

It took me over a year and a half to finally finish the port, because procrastination is strong. That leads us to today.

### Present Day

ClaireBot is currently developed primarily in Java, some components have been and are in Kotlin, but this is currently
a very minor portion of the codebase.

ClaireBot uses the Javacord library for interacting with Discord. Without getting sidetracked too much, this is subject
to change as Javacord's development has greatly slowed (see: [Beyond Javacord](Beyond-Javacord.md)).

At the time of writing ClaireBot (v3.3.2) has the following commands:

| Command      | Description                                                                                     | Documentation          |
|--------------|-------------------------------------------------------------------------------------------------|------------------------|
| /8ball       | Exactly what it sounds like, though it's worth noting that ClaireBot only speaks in truth.      | [Docs](8ball.md)       |
| /avatar      | Gets a user's profile image.                                                                    | [Docs](avatar.md)      |
| /help        | How use ClaireBot???                                                                            | [Docs](help.md)        |     
| /info        | Displays various nuggets of information (uptime, version, support info, etc.).                  | [Docs](info.md)        |
| /leaderboard | Provides details on ClaireBot's (currently rudimentary) implementation of a Leaderboard system. | [Docs](leaderboard.md) |
| /level       | Displays your ClaireBot level (tied together with the leaderboard system).                      | [Docs](level.md)       |
| /poll        | Creates a poll. Leave arguments empty for an interactive popup.                                 | [Docs](poll.md)        |
| /quote       | Picks a random message from the channel the command is executed in and displays it.             | [Docs](quote.md)       |
| /request     | Same system as /poll, but directs it to the server's requests channel.                          | [Docs](poll.md)        |
| /server      | Reports various bits of info about the (optionally specified) server / guild.                   | [Docs](server.md)      |
| /user        | Reports various bits of info about the specified user.                                          | [Docs](user.md)        |
| /config      | Allows for modifying user or server preferences.                                                | [Docs](config.md)      |
| /santa       | Tool for organizing secret santa gift exchanges.                                                | [Docs](santa.md)       |

## Glossary

ClaireBot
: ClaireBot refers to both the ClaireBot project as a whole, and the Discord bot component.
<br/><br/>The Discord bot component is the key piece of software that users interact with. It is what communicates with
the Discord API and responds to user calls.

ClaireData
: ClaireData is the piece of software used to store long-term, persistent data. It serves a REST API that ClaireBot
interfaces with. ClaireData uses Postgres as a database.

ClaireBot Docker
: A series of Docker containers, and a docker-compose.yml for quickly and easily setting up a working copy of ClaireBot.