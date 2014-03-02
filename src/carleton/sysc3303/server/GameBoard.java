package carleton.sysc3303.server;
/*
 * Game board legend
 *
 * B represents a brick wall, W represents a wooden wall(can be blown up by bombs)
 * E represents the tile is empty, X represents the exit
 * O represents a bomb is presents, A represents an AI is present
 * Any lower case letter represents a player
 *
 * 	Game board representation(n X n)
 *  0n	1n	2n	3n	4n	5n	6n	7n	...	nn
 *  ...	...	...	...	...	...	...	...	...	...
 * 	07	17	27	37	47	57	67	77	...	n7
 * 	06	16	26	36	46	56	66	76	...	n6
 * 	05	15	25	35	45	55	65	75	...	n5
 * 	04	14	24	34	44	54	64	74	...	n4
 * 	03	13	23	33	43	53	63	73	...	n3
 * 	02	12	22	32	42	52	62	72	...	n2
 * 	01	11	21	31	41	51	61	71	...	n1
 * 	00	10	20	30	40	50	60	70	...	n0
 */


import java.util.ArrayList;
import java.util.Random;

public class GameBoard {

		private char tiles[][]; //n X n array
		private int size;
//		private ArrayList<Player> players;//Contains every player connected

		//Create board random
		public GameBoard(int s)
		{
//			players = new ArrayList<>();
			size = s; //s X s array

			//Minimum size for a game board
			if(size < 7)
				size = 7;

			//Maximum size for a game board
			if(size > 9)
				size = 9;

			tiles = new char[size][size];
			
//			randomBoardGenerator(size);
		}

		//Create board random
		//Called by the server once all players have been created
		public GameBoard(Player one, Player two, Player three, Player four, int n)
		{
//			players = new ArrayList<>();
			size = n; //n X n array

			//Minimum size for a game board
			if(size < 7)
				size = 7;

			//Maximum size for a game board
			if(size > 9)
				size = 9;
			
			tiles = new char[size][size];

			addPlayer(one);
			addPlayer(two);
			addPlayer(three);
			addPlayer(four);

			randomBoardGenerator(size);

//			//Gives players the board
//			for(int i = 0; i < 4 ; i++)
//			{
//				players.get(i).setBoard(this);
//			}
		}

		//File with 1 line is read(Represented by the String board, every character represents 1 tile on the game board
		//Called by the server once all players have been created
		public GameBoard(String board,Player one, Player two, Player three, Player four, int n)
		{
//			players = new ArrayList<>();
			int x, y;//Used to add players to the board
			size = n;

			//If string does no contain an exit return error, or place an exit under a wooden wall or empty none edge position
			if(!board.contains("X"))
			{
				System.out.println("ERROR NO EXIT");
				//return error
			}

			//Checks the size of the string is large enough to accomodate the board
			if(board.length() != (n*n))
			{
				System.out.println("Error string is not large enough");
				//return error
			}

			int p = 0;//Pivot position in the string

			//Create a predefined board here, where each character of the String board represents 1 tile on the game board
			for(int i = 0; i < n ;i++)
			{
				for(int j = 0; j < n; j++)
				{
					tiles[i][j] = board.charAt(p);
					p++;
				}
			}

			//Add players to the board
			addPlayer(one);
			addPlayer(two);

			x = one.getPosition().x;
			y = one.getPosition().y;

			tiles[x][y] = one.getAvatar();

			x = two.getPosition().x;
			y = two.getPosition().y;


			tiles[x][y] = two.getAvatar();

		}

		public char getTile(int x, int y)
		{
			return tiles[x][y];
		}

		public void setTile(int x, int y, char c)
		{
			tiles[x][y] = c;
		}

		//Add a player to the ArrayList
		public void addPlayer(Player p)
		{
			if(p != null)
			{
//				players.add(p);
			}
		}

		//Randomly generates a new board
		public void randomBoardGenerator(int n)
		{
			//i and j are used to go through the array
			int i = 0;
			int j = 0;
			int counter = 0;//Counter used to count the # of walls being added to the board
			int randomInt;//Random int to generate the board
			Random randomGenerator = new Random();//Random generator

			//Initialize every position to be empty
			for(i = 0 ; i < n ; i++)
			{
				for(j = 0; j < n; j++)
				{
					char e = 'E';
					setTile(i, j, e);
				}
			}

			//Add an exit
			while(true)
			{
				//Assumes an n X n board
				//randomInt = any number between 0 and (n-2) * 11
				//Its n-2 so as to remove the top edge and right edge from consideration
				randomInt = randomGenerator.nextInt((n - 2) * 11);
				j = randomInt % 10;
				i = randomInt % 100;



				//If the position is not an edge
				if(isAnEdge(i,j) == 5)
				{
					if(isValidPosition(i,j))
					{
						tiles[i][j] = 'X';
						break;
					}
				}
			}


			//Add 2 * n Brick Walls
			while(counter < (2 * n))
			{
				//Assumes n X n board
				//randomInt = any number between 0 and (n-2) * 11
				//Its n-2 so as to remove the top edge and right edge from consideration
				randomInt = randomGenerator.nextInt((n - 2) * 11);
				j = randomInt % 10;
				i = j % 10;

				//If the position is not an edge
				if(isAnEdge(i,j) == 5)
				{
					//Checks if the board is big enough for the given position
					if(isValidPosition(i,j))
					{
						//Checks if the current position is empty
						if(tiles[i][j] == 'E')
						{
							//Places a wall
							tiles[i][j] = 'B';
							counter++;
						}
					}
				}
			}

		}

		//Calculate if position is exist on the game board
		public boolean isValidPosition(int x, int y)
		{
			if(x >= size )
				return false;
			if(y >= size)
				return false;

			return true;
		}

		//Check to see if a tile is occupied
		public boolean isOccupied(int x,int y)
		{
			if(tiles[x][y] == 'E')//if tile is empty
				return false;
			else
				return true;
		}

		//Checks if two positions are adjacent to each other
		public boolean isAdjacent(int x, int y, int x2, int y2)
		{
			if(dif(x,x2) == 1 && dif(y,y2) == 0)
				return true;
			if(dif(x,x2) == 0 && dif(y,y2) == 1)
				return true;

			return false;
		}

		/*  First checks if player wants to move on the x-axis or the y-axis
		 * 	Once the direction of movement is determined, check for edges
		 *  If player is at an edge, he can only move in 1 direction (left or right) or (up or down)
		 *  Once its been determined the player wants to move in a valid direction, checks if that tile is occupied
		 *  If player passes all the checks he is allowed to move, else move is not valid
		 *  (x,y) represents the current position of the players
		 *  (x2,y2) represents the position the player wants to move to
		*/
		public boolean isMoveValid(int x,int y, int x2, int y2)
		{
			int edge = isAnEdge(x,y);

			//Checks if the 2 tiles are adjacent
			if(!isAdjacent(x,y,x2,y2))
			{
				return false;
			}

			//2 different x coordinate but same y coordinates, player is moving left or right
			if(dif(x,x2) == 1)
			{
				if(dif(y,y2) == 0)
				{
					//Checks if current position is an edge

					//Current position is the left edge can only move right
					if(edge % 3 == 1)
					{
						if(x2 > x)
						{
							//Check if position is occupied
							if(!isOccupied(x2,y2))
								return true;
						}

						else
							return false;
					}

					//Current position is the right edge can only move left
					if(edge % 3 == 0)
					{
						if(x2 < x)
						{
							//Check if position is occupied
							if(!isOccupied(x2,y2))
								return true;
						}

						else
							return false;
					}

					//Current position is not an edge can move left or right
					else
					{
						if(x2 + 1 == x)
						{
							//Check if new position is occupied
							if(!isOccupied(x2,y2))
								return true;
						}

						if(x2 - 1 == x)
						{
							//Check if new position is occupied
							if(!isOccupied(x2,y2))
								return true;
						}

						else
							return false;
					}
				}
			}

			//2 different y coordinates but same x coordinates, player is moving up or down
			if(dif(y,y2) == 1)
			{
				if(dif(x,x2) == 0)
				{
					//Check for top and bottom edges

					//Current position is the bottom edge can only move up
					if(edge <= 3)
					{
						if(y2 > y)
						{
						//	Check if new position is occupied
							if(!isOccupied(x2,y2))
								return true;
						}

						else
							return false;
					}

					//Current position is the top edge can only move down
					if(edge > 6 && edge < 10)
					{
						if(y2 < y)
						{
							//Check if new position is occupied
							if(!isOccupied(x2,y2))
								return true;
						}

						else
							return false;
					}

					//Current position is not an edge can move up or down
					else
					{
						if(y2 + 1 == y)
						{
							//Check if new position is occupied
							if(!isOccupied(x2,y2))
								return true;
						}

						if(y2 - 1 == y)
						{
							//Check if new position is occupied
							if(!isOccupied(x2,y2))
								return true;
						}

						else
							return false;
					}
				}
			}

			//Either the difference between the x or y coordinates is bigger then 1, or both x and y coordinates
			//have different values
			return false;
		}

		//Used to find out if a player is on an edge, and if he is what edge he is on
		/*
		 * 			Graphic representation of the return value, every value that is not 5 is an edge
		 * 			1,3,7 and 9 the corners
		 *
		 * 			7	-	8	-	9
		 * 			|				|
		 * 			4		5		6
		 * 			|				|
		 * 			1	-	2		3
		 */
		public int isAnEdge(int x, int y)
		{
			if(x == 0 && y == 0)//Bottom left corner
				return 1;
			if(x != 0 && x != size && y == 0)//Bottom edge minus the corners
				return 2;
			if(x == size && y == 0)//Bottom right corner
				return 3;
			if(x == 0 && y != 0 && y != size)//Left edge minus the corners
				return 4;
			if(x != 0 && x != size && y != 0 && y != size)//The middle not an edge
				return 5;
			if(x == size && y != 0 && y != size)//Right edge minus the corners
				return 6;
			if(x == 0 && y == size)//Top left corner
				return 7;
			if(y == size && x !=0 && x != size)//Top edge minus the corners
				return 8;
			if(x == size && y == size)//Top right corner
				return 9;

			return 0;//Else error
		}

		//Used to get the difference between 2 numbers
		//Used in error checking to make sure moves are only made between adjacent tiles
		public int dif(int i, int j)
		{
			if(i > j)
				return i - j;
			else
				return j - i;
		}

		//Checks if a player found the exit
		public void isExit(Player p)
		{
			int x, y;
			x = p.getPosition().x;
			y = p.getPosition().y;

			if(tiles[x][y] == 'X')
			{
				System.out.println(p.getName() + " has found the exit");
				System.out.println(p.getName() + " has won the game");
				//Signal clients player p as won
				//End the game
			}
		}


}
