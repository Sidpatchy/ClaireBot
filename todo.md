# MOVED TO 

# Known Issues
1) Avatar command not sending 4096x4096 image - FIXED
2) Help command fucking dies whenever a command is added / name changed. Caused by using a seperate commands list from
the one used by everything else. Recommendation: Delete ClaireMusic - DELETED CLAIREMUSIC
3) Race condition issue with the leveling system -  see https://github.com/Sidpatchy/ClaireBot/issues/3

# Commands
1) 8ball - Done but needs optimizations
2) Avatar - Done, see known issues #1
3) Config
   - Server preferences - DONE
     - Messaging Channels - DONE
     - Language - DONE
   - User preferences
     - Colour
       - Common colours - DONE
       - Hex entry - DONE
     - Language - NYI (see #14)
       - Need to create a system for multi-lang.
       - For plans see Specs/ClaireLang, ClaireConfig, ClairePAPI
4) Help - DONE, see known issues #2 DONE, see #16
5) Info - NYI, literally just copy RomeBot's info command. -- DONE
6) Leaderboard
    - DB implementation complete. Finalize API implementation.
    - API implementation complete. Need to create the command.
    - Command framework is completed. Need to prevent querying, but more importantly, displaying users who are not in a guild.
    - Race condition issue https://github.com/Sidpatchy/ClaireBot/issues/3
7) Level - NYI
    - API prepared for command creation.
    - Command created
    - Listeners partially implemented
    - Race condition issue https://github.com/Sidpatchy/ClaireBot/issues/3
8) Poll - DONE
9) Request - DONE
10) Server - DONE
11) User - DONE
12) ClaireBot on top! - DONE
13) Zerfas
14) ClaireBot Language System (ClaireLang)
    - Language Manager
    - Collect EVERY language string and add it to a YAML file.
    - Rewrite EVERY command to make use of the language manager.
        - 8ball
        - avatar
        - help
        - info
        - leaderboard
        - level
        - poll
        - request
        - server
        - user
        - config
15) Probably gonna want to do something with the points system you added to the API... - Sorta kinda started
    - Need to add system of gaining points.
    - Mostly done, could probably make gaining points way more robust.
    - Fix race condition issues: https://github.com/Sidpatchy/ClaireBot/issues/3
16) Rewrite the help command to be actually informative. There's literally no additional value added with the current system. The command spec already supports a more advanced help string for each command. MAKE USE OF IT. The architecture of the command is great, it just needs to be more useful. The main screen could be a little more useful, point to starters like /config, and explain breifly what it does.


# General Features
## ClaireWeb
A website needs to be designed. In addition, ClaireBot needs to have an API endpoint (in addition to ClaireData) for it to query from.

## 