Protocol Specification
======================

Each message is in the following format:

    <Message Type>:arg1,arg2,arg3...argN


Message type specifications
---------------------------

### MapMessage

#### Direction

Server -> Client

#### Description

Used by the server to send the current map state to the client which caches it.

#### Arguments

a `|` delimited string of integers, each separator representing
a new row of tiles. Each number represents the tile type in the `Tile`
Enum in the `common` package.


### MetaMessage

#### Direction

Both.

#### Description

Low-level communication between the client and server networking stacks.

#### Arguments

* One of the values from the Type Enum in the `MetaMessage` class
* Optional arguments depending on type


### MoveMessage

#### Direction

Client -> Server

#### Description

Client attempts to move. The direction is specified only (relative).

#### Arguments

See the `Direction` Enum in the `MoveMessage` class.


### PosMessage

#### Direction

Server -> Client

#### Description

Server tells clients where a player is on the map.

#### Arguments

* player's unique id
* x coordinate
* y coordinate


### StateMessage

#### Direction

Both.

#### Description

Server and client communicate about the current status of the game.

#### Arguments

See the `State` Enum in the `StateMessage` class.
