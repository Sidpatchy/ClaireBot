# /poll & /request

Allows for creating interactive, inline polls and requests.

Polls and requests are effectively the same system. In fact, they use identical backend code. The key difference is that
Polls are inserted into the channel where the command is run, and requests are sent to the server's requests channel.

For more info on how the requests channel is selected, please see: [_Requests Channel_](Requests-Channel.md)

## Command

### Syntax

```shell
/poll [question] [optional: allow-multiple-choices] [optional: queries]
/poll
```
```shell
/request [question] [optional: allow-multiple-choices] [optional: queries]
/request
```

### Options

question
: The main question of your poll. 

allow-multiple-choices (optional)
: Determines whether Clairebot will allow users to vote for multiple options.

queries (optional)
: Allows for specifying multiple choice answers. You can have up to 9 choices.

When run with no options
: If none of the previous options are specified, ClaireBot will send your Discord client a popup window allowing you to
create a poll / request in a more graphical manner. 
<br/><br/>This is better for situations where you have a lot to type out, or when you want to avoid fiddling 
with the various options.

<seealso>
    <!--Provide links to related how-to guides, overviews, and tutorials.-->
</seealso>