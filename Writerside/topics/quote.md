# /quote

Picks a random message from the channel the command is executed in and displays it.

On Discord desktop, you can click the author's name to jump to the original message.

On desktop and mobile, you can select the "View Original" button to jump to the original message.
ClaireBot will send an ephemeral message which contains a link to the original.

### Known Issues

/quote is ungodly levels of inefficient reference [ClaireBot #4](https://github.com/Sidpatchy/ClaireBot/issues/4) to
track this issue.

Functions by pulling down (up-to) 50,000 of the most recently posted messages in the channel the
command was executed in. After this, the bot will attempt to pick a random message from the selected user.

All this is to say it is very slow. Be patient.

## Command

### Syntax

```shell
/quote [optional: user]
```

### Options

user (optional)
: The user you wish to quote.
<br/> If left blank, ClaireBot will quote whoever executed the command.

<seealso>
    <!--Provide links to related how-to guides, overviews, and tutorials.-->
</seealso>