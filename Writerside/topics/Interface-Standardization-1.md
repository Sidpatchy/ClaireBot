# Interface Standardization and Updates #1
A place to document various interface changes that need to occur.

## Issue #1
ClaireBot currently uses a mix of CamelCase and kebab-case in user-facing resources. This should be standardized 
to kebab-case.

Internal naming will retain CamelCase (variable names, etc.). All user-facing elements need to be transitioned 
to kebab-casing.

## Issue #2
/help command info should include a link to the relevant documentation webpage. This is blocked pending completion of
the documentation for all commands.

Will either need to update Robin's implementation of the commands.yml standard, or just pull that implementation into
ClaireBot.

## Issue #3
What happens if there is no requests channel??? I'm pretty sure ClaireBot will just throw an error and to the user,
fail completely silently. This is very poor user experience.

## Issue #4
/user doesn't do very much right now. More fields should be considered for addition.