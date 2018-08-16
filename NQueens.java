/*
Author: Alexander Rickford
Purpose: generate an empty chessboard and try to find a valid
configuration of queens
*/
import java.util.Stack;

public class NQueens {
  //method that uses the method validPosition and loops to solve 
  //the number of queen arrangements that are possible.
  //It returns the number of valid arrangements
  //@int n: the size of the board given
  public static int solve(int n) {
    int result = 0, row = 0, position = 0;
    Stack<Integer> mystack = new Stack<Integer>();
    do{
      if(validPosition(mystack, n, row, position)){
        mystack.push(position);
        row++;
        if(row == n){
          result++;
          printSolution(mystack);
          while(position == n){
            if(!mystack.empty()){
              position = mystack.pop()+1;
              row--;
            }
            else {
              //end all loops
              position = n + 1;
            }
          }//while
        }
        else position = 0;
      }
      else {
        position++;
        while(position == n){
          if(!mystack.empty()){
            position = mystack.pop()+1;
            row--;
          }
          else {
            //end all loops
            position = n + 1;
          }
        }//while
      }
    }while(position != n + 1);
    return result;
  }
  //private int method that pops the stack while it equals the
  //private boolean method that tests whether a given position is valid. returns the result
  //@Stack<Integer> test: stack containing previous positions
  //@int position: possible position for next queen
  private static boolean validPosition(Stack<Integer> test, int number, int row, int position){
    if(row >= number) return false;
    if(position == number) return false;
    for(int c = 0; c < test.size(); c++){
      if(test.get(c) == position) {
        return false;
      }
      if((test.get(c) - c == position - row)){
        return false;
      }
      if ((test.get(c) + c == position + row)) {
        return false;
      }
    }//for
    return true;
  }
  //this method prints out a solution from the current stack
  private static void printSolution(Stack<Integer> s) {
    for (int i = 0; i < s.size(); i ++) {
      for (int j = 0; j < s.size(); j ++) {
        if (j == s.get(i))
          System.out.print("Q ");
        else
          System.out.print("* ");
      }//for
      System.out.println();
    }//for
    System.out.println();  
  }//printSolution()
  
  // ----- the main method -----
  public static void main(String[] args) {
  
  int n = 8;
  
  // pass in parameter n from command line
  if (args.length == 1) {
    n = Integer.parseInt(args[0].trim());
    if (n < 1) {
      System.out.println("Incorrect parameter");
      System.exit(-1);
    }  
  }
  
  int number = solve(n);
  System.out.println("There are " + number + " solutions to the " + n + "-queens problem.");
 }//main()
  
}