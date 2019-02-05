package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * SudokuServer/server: RegisterSudokuServer.java
 * Creates and registers a server object
 */
public class RegisterSudokuServer {
     public static void main(String[] args) {
         try {
             SudokuServerInterface server = new SudokuServer();
             Registry registry = LocateRegistry.createRegistry(1099);
             registry.rebind("sudoku", server);

             System.out.println("Server " + server + " is registered successfully.");

         }
         catch(Exception e) {
             e.printStackTrace();
             System.out.println("ERROR " + e);
         }
     }
}
