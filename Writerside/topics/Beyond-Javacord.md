# Beyond Javacord

Since day one, ClaireBot 3 has been based on Javacord due to it being the easiest Java-based Discord API wrapper
to work with. As far as I am concerned, that remains true today. On top of that, it has great documentation 
and a lovely community.

Despite how much I like Javacord, I recognize one major issue with it: 
it's development has come to a complete standstill.

**This leaves me with two realistic options**:
1. Contribute to Javacord and work to bring it up to date.
2. Migrate to a different API wrapper (JDA, Discord4J, etc.)

## Option #1: Contributing to Javacord
In an ideal world, this is the path I'd choose, however, Javacord is quite out of date at this point, and I'd rather
put that effort into improving ClaireBot or working on one of my various other hobbies... which I have too many of.

Javacord is currently using the latest Discord API version (v10, see: [Javacord.java](https://github.com/Javacord/Javacord/blob/aa5afd1dde791a8811ccdbc881b44d29cb629699/javacord-api/src/main/java/org/javacord/api/Javacord.java#L95) and [Discord Develpoer Portal](https://discord.com/developers/docs/reference)), 
sooooo it wouldn't be completely out of the realm of reason to implement new features.

## Option #2: 
From a quick glance, Discord4J seems like the best option if ClaireBot were to switch libraries. It uses Spring's Mono
classes which _can_ be converted to CompletableFuture. This would hopefully eliminate the need to do major rewrites to
large parts of ClaireBot as structure could remain quite similar.