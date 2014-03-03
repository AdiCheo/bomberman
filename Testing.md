#Testing Procedure

## 1. Testing 1 Player and Exit
RunServer /MapsNBots/testMap1.txt
RunSpectator
RunBot /MapsNBots/testBot1.txt

## 2. Testing 2 Players and Exit (No Touching)
RunServer /MapsNBots/testMap2n3.txt
RunSpectator
RunBot /MapsNBots/testBot2.txt
RunBot /MapsNBots/testBot2n3.txt

## 3. Testing 2 Players Touching
RunServer /MapsNBots/testMap2n3.txt
RunSpectator
RunBot /MapsNBots/testBot2n3.txt
RunBot /MapsNBots/testBot2n3.txt




