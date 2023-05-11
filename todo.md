## Known Issues
1) Avatar command not sending 4096x4096 image - FIXED
2) Help command fucking dies whenever a command is added / name changed. Caused by using a seperate commands list from
the one used by everything else. Recommendation: Delete ClaireMusic - DELETED CLAIREMUSIC

## Commands
1) 8ball - Done but needs optimizations
2) Avatar - Done, see known issues #1
3) Config
   - Server preferences - NYI
     - Messaging Channels - NYI
     - Language - NYI
   - User preferences
     - Colour
       - Common colours - DONE
       - Hex entry - DONE
     - Language - NYI (see #14)
       - Need to create a system for multi-lang.
       - >ClaireLang when?
4) Help - DONE, see known issues #2
5) Info - NYI, literally just copy RomeBot's info command.
6) Leaderboard - NYI, need DB implementation
    - DB implementation complete. Finalize API implementation.
    - API implementation complete. Need to create the command.
7) Level - NYI
    - API prepared for command creation.
8) Poll - DONE
9) Request - NYI, see commands #3
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
16) Rewrite the help command to be actually informative. There's literally no additional value added with the current system. The command spec already supports a more advanced help string for each command. MAKE USE OF IT. The architecture of the command is great, it just needs to be more useful. The main screen could be a little more useful, point to starters like /config, and explain breifly what it does.
