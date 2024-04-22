# ClaireBot
![Build Status](https://img.shields.io/github/actions/workflow/status/Sidpatchy/ClaireBot/gradle.yml?style=flat-square)
[![License](https://img.shields.io/github/license/Sidpatchy/ClaireBot?style=flat-square)](https://github.com/Sidpatchy/ClaireBot/blob/main/LICENSE)
![Last Commit](https://img.shields.io/github/last-commit/Sidpatchy/ClaireBot?style=flat-square)

ClaireBot is a feature-packed Discord bot written in Java.

Want to host ClaireBot? That's documented right [here](https://github.com/Sidpatchy/ClaireBot/wiki/Hosting-ClaireBot)

You can learn more about, and invite ClaireBot to your server at https://www.clairebot.net/

## Where are ClaireBot 1 and 2?
ClaireBot 1 was initially built for my own use on a single server. It was only a member of *that* server, and it only worked on *that* server.

Eventually, as things changed with that server, the requirements of ClaireBot changed too, so I ended up rewriting it from scratch, and thus, ClaireBot 2 was born.

ClaireBot 2 much resembled today's ClaireBot 3. In fact, a lot of ClaireBot 3's design was nicked from ClaireBot v2.10.2. Most users would likely not notice the difference between the two if it weren't for v3's reliance on slash commands.

But, like ClaireBot 1, it was primarily used in a handful of servers, and still had quite a few locked down features. It was never built with more than that in mind. But at some point I started to forget that, and began building new features that would work on any server.

The problems with this hacky approach were two-fold. First, a large portion of the bot was written hastily without much thought being put into the future, and second, the code, was godawful and and utterly tangled together.

I'd want to change one feature, but that would break many other features along with it. The design wasn't sustainable, but I kept working on it because it was "good enough."

Then, like many others, I had to build a whole new version due to the unfortunate discontinuation of Discord.py, and the rest is history.
