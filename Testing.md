Testing Procedure
=================

#### 1. Testing 1 Player and Exit
* RunServer /MapsNBots/testMap1.txt
* RunSpectator
* RunBot /MapsNBots/testBot1.txt

##### Purpose
* Checks 1 player movement
* Collision with walls and map boundary
* Exit and end game

#### 2. Testing 2 Players and Exit (No Touching)
* RunServer /MapsNBots/testMap2n3.txt
* RunSpectator
* RunBot /MapsNBots/testBot2.txt
* RunBot /MapsNBots/testBot2n3.txt

##### Purpose
* Checks 2 player movements
* Collision with walls and map boundary
* Exit and end game


#### 3. Testing 2 Players Touching
* RunServer /MapsNBots/testMap2n3.txt
* RunSpectator
* RunBot /MapsNBots/testBot2n3.txt
* RunBot /MapsNBots/testBot2n3.txt

##### Purpose
* Checks 2 player movement
* Collision with walls and map boundary
* Collision between players
