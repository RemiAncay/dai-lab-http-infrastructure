# API specs

## Summary
`foosball-api` is a server-side HTTP API that we developed for a lab during the DAI course at HEIG-VD.

This API provides an CRUD interface to manage a database of foosball players and matches. Players can be created, read, updated and modified and matches can be read, created and deleted if necessary.
For simplicity reasons, we decided not to support data modification for matches.

## Data
This section contains a definition of all the data that is stored and managed by our API.

### Players
Players have the following attributes :
- **name** (first name + last name)
- **date** of birth

### Matches
Foosball matches can either be *singles* (1 players versus 1 player) or *doubles* (2 versus 2). For simplicity reasons, we decided to only manage singles matches in our API.

A singles match has the following attributes :
- **date** at which the match took place
- **place** where the match took place
- **name** of the first player
- **name** of the other player
- **winner** of the match

## Data storage
To avoid making our infrastructure more complex and because this is just a lab, we decided to not use a SQL database to store the data. Instead, we will store all the data in a JSON file that will be serialized (read) and deserialized (written) when the API starts and stop.

In a real-world scenario, it would be preferable to use a database as it is more reliable than a simple file and allows the use of SQL requests. 