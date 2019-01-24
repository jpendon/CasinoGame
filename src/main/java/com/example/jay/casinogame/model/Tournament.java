/*
 **************************************************************
 * Name:  Jay Pendon						                     *
 * Project: Casino Game								         *
 * Class: CMPS 366 01 - Organization of Programming Languages *
 * Date:  Decemeber 11, 2018		                             *
 **************************************************************
 */

package com.example.jay.casinogame.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;


public class Tournament {
   private Vector<Player> players;
   private int winningScore;
   private int roundNum;
   private int nextPlayer;
   private Boolean gameEnded;
   private Round round;
   private Boolean resumedGame;

/* *********************************************************************
Function Name: Tournament
Purpose: To create a Tournament object
Parameters:
            None
Return Value: None
Local Variables:
            None
Algorithm:
            None
Assistance Received: none
********************************************************************* */
   public Tournament(){
      this.gameEnded = false;
      this.roundNum = 0;
      this.winningScore = 21;
      this.resumedGame = false;
      this.players = new Vector<Player>();
      this.players.add(new Human(0));
      this.players.add(new Computer(1));
      this.nextPlayer = -1;
   }

   /* *********************************************************************
Function Name: createNewRound
Purpose: To create a new Round
Parameters:
         None
Return Value: A Round object
Local Variables:
         None
Algorithm:
         1) Increase the roundNum
         2) Create the New Round
         3) Return the Round
Assistance Received: none
********************************************************************* */
   public Round createNewRound(){
      this.roundNum++;
      return new Round(this.players, this.roundNum, this.nextPlayer);
   }

/* *********************************************************************
Function Name: updateTournament
Purpose: To update the tournament
Parameters:
         round, a Round object passed by value
Return Value: None
Local Variables:
         None
Algorithm:
         1) Update the players vector
         2) Update the nextPlayer
Assistance Received: none
********************************************************************* */
   public void updateTournament(Round round){
      this.players = round.getPlayers();
      this.nextPlayer = round.getPlayerLastCaptured().getId();
   }

   /* *********************************************************************
Function Name: checkPlayerScore
Purpose: To check the player's score
Parameters:
         None
Return Value: None
Local Variables:
         None
Algorithm:
         1) Loop through the players vector
            a) Check if the player has won
            b) Set the player as a winner if so
Assistance Received: none
********************************************************************* */
   public void checkPlayerScore() {
      // Loop through players vector
      for (Player player: this.players){
         // Check if the player has a score equal or greater than the winningScore
         if (player.getScore() >= this.winningScore) {
            // gameEnded is equal to true because the winningScore has been reached
            this.gameEnded = true;
            // Set the player as the winner being true
            player.setWinner();
         }
      }

      // Loop through players vector
      for (Player player: this.players){
         // Clear the player's pile
         player.clearPile();
      }
   }

/* *********************************************************************
Function Name: resumeGame
Purpose: To resume a game
Parameters:
         file, an InputStream object passed by value
Return Value: A Round object that contains the loaded game information
Local Variables:
            serialization, a Serialization object
            deck, a vector containing Card that represents a deck
            table, a vector containing Card that represents a table
            builds, a hashMap containing pairs of an integer and a Build pointer
                passed by value. It represents the owner of the build's
                Id and the build.
Algorithm:
            1) Parse the information from a file
            2) Set the Round to have the same information
Assistance Received: none
********************************************************************* */
   public Round resumeGame(InputStream file) throws IOException {
      Vector<Card> deck = new Vector<Card>();
      Vector<Card> table = new Vector<Card>();
      HashMap<Integer, Build> builds = new HashMap<>();
      int lastCaptured = 0;

      Serialization serialization = new Serialization();

      serialization.resumeGame(file);

      this.players = serialization.getPlayers();
      this.roundNum = serialization.getRound();
      deck = serialization.getDeck();
      table = serialization.getTable();
      builds = serialization.getBuilds();
      this.nextPlayer = serialization.getNextPlayer();
      lastCaptured = serialization.getLastCaptured();

      this.round = new Round(table, this.players, this.roundNum, deck, this.nextPlayer, lastCaptured, builds);

      this.resumedGame = true;

      return this.round;
   }

   /* *********************************************************************
Function Name: hasGameEnded
Purpose: Check to see if the game has ended
Parameters:
         None
Return Value: A boolean value indicating if the game has ended
Local Variables:
         None
Algorithm:
         1) Loop through the players vector
            a) Check to see if the player has won
         2) Return a value indicating if the game has ended
Assistance Received: none
********************************************************************* */
   public Boolean hasGameEnded(){
      for (Player player: this.players){
         if (player.getWinner()){
            return true;
         }
      }

      return false;
   }

   /* *********************************************************************
Function Name: getWinnerBoardString
Purpose: To get the String containing the text for the post game screen
Parameters:
         None
Return Value: A String containing the text for the post game screen
Local Variables:
         human, a Human object
         computer, a Computer object
         postGameString, a String containing the post game information
Algorithm:
         1) Find the human and computer objects
         2) Create the string for post game
         3) Return the string
Assistance Received: none
********************************************************************* */
   public String getWinnerBoardString(){
      Human human;
      Computer computer;

      String postGameString = "";

      if (players.elementAt(0) instanceof Human){
         human = (Human)players.elementAt(0);
         computer = (Computer)players.elementAt(1);
      }
      else{
         human = (Human)players.elementAt(1);
         computer = (Computer)players.elementAt(0);
      }

      if (human.getWinner() && !computer.getWinner()) {
         postGameString += "\nPlayer Wins\n";
      }
      else if (!human.getWinner()&& computer.getWinner()) {
         postGameString += "\nComputer Wins\n";
      }
      else {
         if (human.getScore() > computer.getScore()) {
            postGameString += "\nPlayer Wins\n";
         }
         else if (human.getScore() < computer.getScore()) {
            postGameString += "\nComputer Wins\n";
         }
         else {
            postGameString += "\nTie\n";
         }
      }

      postGameString +="\nFinal Score: \n";
      postGameString += "\tComputer: ";
      postGameString += Integer.toString(computer.getScore());
      postGameString += "\n";
      postGameString += "\tPlayer: ";
      postGameString += Integer.toString(human.getScore());
      postGameString += "\n";

      postGameString += "\n\nPress Ok to Continue";

      return postGameString;
   }


}

