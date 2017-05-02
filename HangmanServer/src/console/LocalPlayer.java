/*
 * Code used in the "Software Engineering" course.
 *
 * Copyright 2017 by Claudio Cusano (claudio.cusano@unipv.it)
 * Dept of Electrical, Computer and Biomedical Engineering,
 * University of Pavia.
 */
package console;

import hangman.Player;
import hangman.Game;
import hangman.GameResult;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manage a player playing with the terminal.
 * 
 * @author Claudio Cusano <claudio.cusano@unipv.it>
 */
public class LocalPlayer extends Player {
    
    Console console;
    Socket socket;
    PrintWriter pw = null;    
    
    /**
     * Constructor.
     */
    public LocalPlayer(Socket socket) {
        console = System.console();
        this.socket=socket;
        
    }
    
    @Override
    public void update(Game game,Socket socket) {
        try {
            pw = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(LocalPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        switch(game.getResult()) {
            case FAILED:
                printBanner("Hai perso!  La parola da indovinare era '" +
                            game.getSecretWord() + "'");
                break;
            case SOLVED:
                printBanner("Hai indovinato!   (" + game.getSecretWord() + ")");
                pw.println("bravo");
        {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(LocalPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                break;
            case OPEN:
                pw.println(gameRepresentation(game));
                
                int rem = Game.MAX_FAILED_ATTEMPTS - game.countFailedAttempts();
                System.out.print("\n" + rem + " tentativi rimasti\n");
                System.out.println(this.gameRepresentation(game));
                System.out.println(game.getKnownLetters());
                break;
        }
    }

    private String gameRepresentation(Game game) {
        int a = game.countFailedAttempts();
        
        String s = "   ___________\n  /       |   \n  |       ";
        s += (a == 0 ? "\n" : "O\n");
        s += "  |     " + (a == 0 ? "\n" : (a < 5
                ? "  +\n"
                : (a == 5 ? "--+\n" : "--+--\n")));
        s += "  |       " + (a < 2 ? "\n" : "|\n");
        s += "  |      " + (a < 3 ? "\n" : (a == 3 ? "/\n" : "/ \\\n"));
        s += "  |\n================\n";
        return s;
    }
    
    private void printBanner(String message) {
        System.out.println("");
        for (int i = 0; i < 80; i++)
            System.out.print("*");
        System.out.println("\n***  " + message);
        for (int i = 0; i < 80; i++)
            System.out.print("*");
        System.out.println("\n");
    }

    /**
     * Ask the user to guess a letter.
     * 
     * @param game
     * @return
     */
    @Override
    public char chooseLetter(Game game) {
        
        for (;;) {
            
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = br.readLine();
                if (line.length() == 1 && Character.isLetter(line.charAt(0))) {
                    if(game.getResult()==GameResult.SOLVED){
                    pw.println("bravo");
                    }
                    return line.charAt(0);
                } else {
                System.out.println("Lettera non valida.");
                }
                
            } catch (IOException ex) {
                Logger.getLogger(LocalPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
//            System.out.print("Inserisci una lettera: ");
//            String line = console.readLine().trim();
            
        }
    }
}
