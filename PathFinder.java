/*
Author: Alexander Rickford
Purpose: generate a maze from a given text file and demonstrate stack
and queue search
*/
import java.io.*;
import java.util.Scanner;
import java.util.ArrayDeque;

/*
* Recursive class to represent a position in a path
*/
class Position{
	public int i;     //row
	public int j;     //column
	public char val;  //1, 0, or 'X'
	
	// reference to the previous position (parent) that leads to this position on a path
	Position parent;
	
	Position(int x, int y, char v){
		i = x; j = y; val = v;
	}
	
	Position(int x, int y, char v, Position p){
		i = x; j = y; val = v;
		parent=p;
	}
	
}

/*
* Main class
*/
public class PathFinder {
	
	public static void main(String[] args) throws IOException {
		if(args.length < 1){
			System.err.println("***Usage: java PathFinder maze_file");
			System.exit(-1);
		}
		
		char [][] maze;
		maze = readMaze(args[0]);
		printMaze(maze);
		Position [] path = stackSearch(maze);
		System.out.println("stackSearch Solution:");
		printPath(path);
		printMaze(maze);
		
		char [][] maze2 = readMaze(args[0]);
		path = queueSearch(maze2);
		System.out.println("queueSearch Solution:");
		printPath(path);
		printMaze(maze2);
	}//main
	
	/**
	* Uses stack data search to solve the maze
	* @param 	maze	given character maze
	* @return 			path solution to maze
	*/
	public static Position [] stackSearch(char [] [] maze){
		int row = 0, col = 0;
		//stack data structure
		ArrayDeque<Position> mystack = new ArrayDeque<Position>();
		//create a copy of the maze to mark previous paths
		char [][] copyMaze = new char [maze.length][maze[col].length];
		for(int c = 0; c < maze.length; c++)
			for(int i = 0; i < maze[0].length; i++)
				copyMaze[c][i] = maze[c][i];
		copyMaze[0][0] = 'x';
		Position current = new Position(0, 0, maze[0][0]);
		mystack.push(current);
		while(!mystack.isEmpty()){
			boolean test = true;
			current = mystack.peek();
			row = current.i;
			col = current.j;
			//end of maze condition
			if(row == copyMaze[col].length - 1 && col == copyMaze.length - 1){
				int count = 0;
				for(Position x = mystack.peek(); x != null; x = x.parent) {
					maze[x.j][x.i] = 'x';
					count++;
				}
				Position[] result = new Position[count];
				count -= 1;
				for(Position x = mystack.peek(); x != null; x = x.parent)
					result[count--] = x;
				return result;
			}
			current = mystack.pop();
			for(int count = 0; count < 4 && test; count++){
				row = current.i;
				col = current.j;
				switch(count){
					case 0:
						if(current.i - 1 >= 0){
							row = current.i - 1;
							break;
						}
						else count++;
					case 1:
						if(current.j - 1 >= 0){
							col = current.j - 1;
							break;
						}
						else count++;
					case 2:
						if(current.i < maze[col].length - 1){
							row = current.i + 1;
							break;
						}
						else count++;
					case 3:
						if(current.j < maze.length - 1){
							col = current.j + 1;
							break;
						}
						else count++;
					default:
						test = false;
							
				}//switch
				if(test && copyMaze[col][row] == '0'){
					copyMaze[col][row] = '-';
					Position temp = 
						new Position(row, col, maze[col][row], current);
					mystack.push(temp);
				}
			}//for
		}//while
				
		return null;
	}//stackSearch

	/**
	* Uses queue data search to solve the maze
	* @param 	maze	given character maze
	* @return 			path solution to maze
	*/
	public static Position [] queueSearch(char [] [] maze){
		int row = 0, col = 0;
		//queue data structure
		ArrayDeque<Position> mystack = new ArrayDeque<Position>();
		//create a copy of maze to mark previous paths
		char [][] copyMaze = new char [maze.length][maze[col].length];
		for(int c = 0; c < maze.length; c++)
			for(int i = 0; i < maze[0].length; i++)
				copyMaze[c][i] = maze[c][i];
		copyMaze[0][0] = 'x';
		Position current = new Position(0, 0, maze[0][0]);
		mystack.add(current);
		while(!mystack.isEmpty()){
			boolean test = true;
			current = mystack.peek();
			row = current.i;
			col = current.j;
			//end of maze condition
			if(row == copyMaze[col].length - 1 && col == copyMaze.length - 1){
				int count = 0;
				for(Position x = mystack.peek(); x != null; x = x.parent) {
					maze[x.j][x.i] = 'x';
					count++;
				}
				Position[] result = new Position[count];
				count -= 1;
				for(Position x = mystack.peek(); x != null; x = x.parent){
					result[count--] = x;
				}
				return result;
			}
			current = mystack.remove();
			for(int count = 0; count < 4 && test; count++){
				row = current.i;
				col = current.j;
				switch(count){
					case 0:
						if(current.i - 1 >= 0){
							row = current.i - 1;
							break;
						}
						else count++;
					case 1:
						if(current.j - 1 >= 0){
							col = current.j - 1;
							break;
						}
						else count++;
					case 2:
						if(current.i < maze[col].length - 1){
							row = current.i + 1;
							break;
						}
						else count++;
					case 3:
						if(current.j < maze.length - 1){
							col = current.j + 1;
							break;
						}
						else count++;
					default:
						test = false;
							
				}
				if(test && copyMaze[col][row] == '0'){
					copyMaze[col][row] = '-';
					Position temp = 
						new Position(row, col, maze[col][row], current);
					mystack.add(temp);
				}
			}
		}
		
		return null;
	}//queueSearch

	/**
	* Prints out valid path for maze if it exists
	* @param 	path	route found to solve maze
	* @return 			void
	*/
	
	public static void printPath(Position [] path){
		if(path != null){
			System.out.print("The correct path for this maze is: ");
			for(int c = 0; c < path.length; c++){
				if(c % 10 == 0) System.out.println();
				if(c < path.length - 1) 
					System.out.print("[" + path[c].j + "]["
						+ path[c].i + "], ");
				else System.out.println("[" + path[c].j + "]["
						+ path[c].i + "]");
			}
		}
		else System.out.println("There is no correct path.");
	}//printPath
	
	/**
	* Reads maze file in format:
	* N  -- size of maze
	* 0 1 0 1 0 1 -- space-separated 
	* @param 	filename	file to open
	* @return 				character generated maze
	* @throws 	IOException
	*/
	public static char [][] readMaze(String filename) throws IOException{
		char [][] maze;
		Scanner scanner;
		try{
			scanner = new Scanner(new FileInputStream(filename));
		}
		catch(IOException ex){
			System.err.println("*** Invalid filename: " + filename);
			return null;
		}
		
		int N = scanner.nextInt();
		scanner.nextLine();
		maze = new char[N][N];
		int i = 0;
		while(i < N && scanner.hasNext()){
			String line =  scanner.nextLine();
			String [] tokens = line.split("\\s+");
			int j = 0;
			for (; j< tokens.length; j++){
				maze[i][j] = tokens[j].charAt(0);
			}//for
			if(j != N){
				System.err.println("*** Invalid line: " + i + " has wrong # columns: " + j);
				return null;
			}
			i++;
		}//while
		if(i != N){
			System.err.println("*** Invalid file: has wrong number of rows: " + i);
			return null;
		}
		return maze;
	}//readMaze
	
	/**
	* Print Given Maze
	* @param 	maze 	character generated maze
	* @return 			void
	*/
	public static void printMaze(char[][] maze){
		
		if(maze==null || maze[0] == null){
			System.err.println("*** Invalid maze array");
			return;
		}
		
		for(int i=0; i< maze.length; i++){
			for(int j = 0; j< maze[0].length; j++){
				System.out.print(maze[i][j] + " ");	
			}
			System.out.println();
		}//for
		
		System.out.println();
	}//printMaze

}//PathFinder
