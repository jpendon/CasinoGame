/*
 **************************************************************
 * Name:  Jay Pendon						                     *
 * Project: Casino Game								         *
 * Class: CMPS 366 01 - Organization of Programming Languages *
 * Date:  Decemeber 11, 2018		                             *
 **************************************************************
 */

package com.example.jay.casinogame.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


public class Round implements Serializable {
   private Vector<Player> players = new Vector<Player>();
   private Vector<Card> table = new Vector<Card>();
   private Deck roundDeck;
   private int round;
   private int nextPlayer;
   private Player playerLastCaptured;
   private HashMap<Integer, Build> builds = new HashMap<Integer, Build>();
   private Boolean resumedGame;

/* *********************************************************************
Function Name: Round
Purpose: To create a Round object
Parameters:
         None
Return Value: None
Local Variables:
         None
Algorithm:
         1) Set the default values for Round
Assistance Received: none
********************************************************************* */
   public Round(){
      this.roundDeck = new Deck();
      this.players = new Vector<Player>();
      this.round = 0;
      this.builds = new HashMap<Integer, Build>();
      this.table = new Vector<Card>();
      this.nextPlayer = -1;
      this.resumedGame = false;
   }

   /* *********************************************************************
   Function Name: Round
   Purpose: To create a Round object
   Parameters:
               players, a vector of Players that contain player information
                   passed by value
               round, an integer containing the current round passed by value
               nextPlayer, an integer passed by value that contains the id of the
                   next player

   Return Value: None
   Local Variables:
               None
   Algorithm:
               1) Set the default values for Round
   Assistance Received: none
   ********************************************************************* */
   public Round(Vector<Player> players, int round, int nextPlayer){
      this.roundDeck = new Deck();
      this.players = players;
      this.round = round;
      this.builds = new HashMap<Integer, Build>();
      this.table = new Vector<Card>();
      this.nextPlayer = nextPlayer;
      this.resumedGame = false;

      for (Player player: players){
         player.clearPile();
      }
   }

/* *********************************************************************
Function Name: Round
Purpose: To create a Round object
Parameters:
            deck, a vector of Cards that represent a deck passed by
                value
            table, a vector of Cards that represent the table passed
                by value
            players, a vector of Players that contain player information
                passed by value
            round, an integer containing the current round passed by value
            builds, a hashmap containing pairs of an integer and a Build
                passed by value that represents a player's Id and build
            nextPlayer, an integer passed by value that contains the id of the
                next player

Return Value: None
Local Variables:
            None
Algorithm:
            1) Set the default values for Round
Assistance Received: none
********************************************************************* */
   public Round(Vector<Card> table, Vector<Player> players, int round, Vector<Card> deck, int nextPlayer,
                int playerLastCaptured, HashMap<Integer, Build> builds){
      this.table = table;
      this.players = players;
      this.round = round;
      this.roundDeck = new Deck(deck);
      this.nextPlayer = nextPlayer;
      this.resumedGame = true;

      for (Player player: players) {
         if (player.getId() == playerLastCaptured) {
            this.playerLastCaptured = player;
         }

      }
      this.builds = builds;
   }

   /* *********************************************************************
   Function Name: Round
   Purpose: To create a deep copy of a Round object
   Parameters:
               round, a Round object
   Return Value: None
   Local Variables:
               None
   Algorithm:
               1) Set the default values for Round
   Assistance Received: none
   ********************************************************************* */
   public Round(Round round){
      this.roundDeck = new Deck(round.getDeck());
      this.round = round.getRound();
      this.nextPlayer = round.getNextPlayer();
      this.resumedGame = false;

      for (Player player: round.getPlayers()){
         if (player instanceof Human){
            this.players.add(new Human((Human)player));
            continue;
         }
         this.players.add(new Computer((Computer)player));
      }

      Iterator it = round.getBuilds().entrySet().iterator();
      while(it.hasNext()){
         HashMap.Entry pair = (HashMap.Entry)it.next();
         this.builds.put((Integer)pair.getKey(), new Build((Build)pair.getValue()));
      }

      for (Card card: round.getTable()){
         if (card instanceof Build){
            this.table.add(new Build((Build)card));
            continue;
         }
         this.table.add(new Card(card));
      }
      return;
   }

   /* **********************************************************************
   Function Name: bothPlayersDrawCards
   Purpose: To draw cards for players
   Parameters:
               None
   Return Value: None
   Local Variables:
               humanPlayer, a Human pointer that points to the human player
               computerPlayer, a Computer pointer that points to the computer
   Algorithm:
               1) Find the player and computer
               2) Player draws four cards
               3) Computer draws four cards
   Assistance Received: None
   ***********************************************************************/
   public void bothPlayersDrawCards() {
      // humanPlayer and computerPlayer hold pointers to their respective class
      Human humanPlayer = null;
      Computer computerPlayer = null;

      // Check if the first element of player can be casted into Human REFACTOR
      if (players.elementAt(0) instanceof Human) {
         humanPlayer = (Human)players.elementAt(0);
         computerPlayer = (Computer)players.elementAt(1);
      }
      else {
         humanPlayer = (Human)players.elementAt(1);
         computerPlayer = (Computer)players.elementAt(0);
      }

      //Human Draws 4 Cards First then the Computer Draws
      humanPlayer.drawFromDeck(roundDeck);
      computerPlayer.drawFromDeck(roundDeck);
   }


   /* **********************************************************************
   Function Name: drawLooseCards
   Purpose: To draw loose cards
   Parameters:
               None
   Return Value: None
   Local Variables:
               None
   Algorithm:
               1) Draw 4 cards
   Assistance Received: None
   ***********************************************************************/
   public void drawLooseCards() {
      // Loop four times
      for (int i = 0; i < 4; i++) {
         // Draw a card
         table.add(roundDeck.drawCard());
      }
   }

/* *********************************************************************
Function Name: checkTableCards
Purpose: Check if the table contains the value or strValue equal to the chosen
         card
Parameters:
         chosenCard, a Card object representing the player's choice passed by value
Return Value: a Boolean value indicating if the table contains a card equal to the chosen
               card
Local Variables:
         None
Algorithm:
         1) Loop through the table
         2) Check if the numerical value or strValue is equal to the chosen card
         3) Return result
Assistance Received: none
********************************************************************* */
   public Boolean checkTableCards(Card chosenCard){
      for (Card card: table){
         if(card.getNumValue() == chosenCard.getNumValue() ||
               chosenCard.getCardStrVal().equals(card.getCardStrVal())){
            return true;
         }
      }

      return false;
   }

/* *********************************************************************
Function Name: addToPlayerPile
Purpose: add Cards to the player's pile
Parameters:
         player, Player object passed by value
Return Value: None
Local Variables:
         None
Algorithm:
         1) Loop through the table and add the card to the player's pile
Assistance Received: none
********************************************************************* */
   public void addToPlayerPile(Player player){
      for (Card card: this.table){
         player.addToPile(card);
      }
   }

/* **********************************************************************
Function Name: computerRoundScore
Purpose: To compute the round scores
Parameters:
Return Value: None
Local Variables:
            humanPlayer, a Human that points to the human player
            computerPlayer, a Computer that points to the computer
            humanEarned, an integer that contains the amount of points
                the human has earned
            computerEarned, an integer that contains the amount of points
                the computer has earned
Algorithm:
            1) Check who has the biggest pile and give 3 points
            2) Check who has the most spades and give them a point
            3) Check who has the DX card and give them 2 point
            4) Check who has the S2 card and give them a point
            5) Add the amount of aces as the score for each player
            6) Call and return getPostRoundString
Assistance Received: None
***********************************************************************/
   public String computeRoundScore() {
      // humanPlayer and computerPlayer are pointers to their respective classes
      Human humanPlayer = null;
      Computer computerPlayer = null;

      // humanEarned and computerEarned are integers indicating points earned
      int humanEarned = 0, computerEarned = 0;

      // Cast players to their derived classes
      if (players.elementAt(0) instanceof Human) {
         humanPlayer = (Human)players.elementAt(0);
         computerPlayer = (Computer)players.elementAt(1);
      }
      else {
         humanPlayer = (Human)players.elementAt(1);
         computerPlayer = (Computer)players.elementAt(0);
      }


      // Check if the human has the biggest pile
      if (humanPlayer.getNumPileCards() > computerPlayer.getNumPileCards()) {
         // Give the human 3 points
         humanEarned += 3;
      }
      // Check if the computer has the biggest pile
      else if (humanPlayer.getNumPileCards() < computerPlayer.getNumPileCards()) {
         // Give the computer 3 points
         computerEarned += 3;
      }

      // Check if the human has the most spades
      if (humanPlayer.getNumSpades() > computerPlayer.getNumSpades()) {
         // Give the human 1 point
         humanEarned += 1;
      }
      // Check if the computer has the most spades
      else if (humanPlayer.getNumSpades() < computerPlayer.getNumSpades()) {
         // Give the computer 1 point
         computerEarned += 1;
      }

      // Check if the player holds the card DX
      if (humanPlayer.checkForCardName("DX")) {
         // Give the human 2 point
         humanEarned += 2;
      }
      else {
         // Give the computer 2 point
         computerEarned += 2;
      }

      //CHECK FOR 2 SPADES
      if (humanPlayer.checkForCardName("S2")) {
         // Give the human 1 point
         humanEarned += 1;
      }
      else {
         // Give the computer 1 point
         computerEarned += 1;
      }

      // humanEarned is equal to humanEarned plus the number of Aces in their pile
      humanEarned += humanPlayer.getNumAces();
      // computerEarned is equal to computerEarned plus the number of Aces in their pile
      computerEarned += computerPlayer.getNumAces();

      //Add earned points to each player's score
      humanPlayer.addToScore(humanEarned);
      computerPlayer.addToScore(computerEarned);

      return getPostRoundString(humanEarned, computerEarned);
   }

/* *********************************************************************
Function Name: getPostRoundString
Purpose: To create the post round string
Parameters:
         humanEarned, an integer representing the amount of points earned
            passed by value
         computerEarned, an integer representing the amount of points earned
            passed by value
Return Value: A string containing the post round string
Local Variables:
         postRoundString, a string containing the post round string
         humanPlayer, a Human that points to the human player
         computerPlayer, a Computer that points to the computer

Algorithm:
         1) Find the human and computer objects
         2) Create the post round board string
         3) return the string
Assistance Received: none
********************************************************************* */
   public String getPostRoundString(int humanEarned, int computerEarned){
      String postRoundString = "";

      Human humanPlayer;
      Computer computerPlayer;

      if (players.elementAt(0) instanceof Human){
         humanPlayer = (Human)players.elementAt(0);
         computerPlayer = (Computer)players.elementAt(1);
      }
      else{
         humanPlayer = (Human)players.elementAt(1);
         computerPlayer = (Computer)players.elementAt(0);
      }


      postRoundString += "Post Round Board";
      postRoundString += "\n";
      postRoundString +="\nComputer Pile: ";
      postRoundString += computerPlayer.getPlayerPile();
      postRoundString += "\nPlayer Pile: ";
      postRoundString += humanPlayer.getPlayerPile();

      postRoundString += "\n\nTotal Number of Cards: ";
      postRoundString += "\n\tComputer - " + Integer.toString(computerPlayer.getNumPileCards());
      postRoundString += "\n\tPlayer - " + Integer.toString(humanPlayer.getNumPileCards());

      postRoundString += "\n\nTotal Number of Spades: ";
      postRoundString += "\n\tComputer - " + Integer.toString(computerPlayer.getNumSpades());
      postRoundString += "\n\tPlayer - " + Integer.toString(humanPlayer.getNumSpades());

      postRoundString += "\n\nTotal Number of Aces: ";
      postRoundString += "\n\tComputer - " + Integer.toString(computerPlayer.getNumAces());
      postRoundString += "\n\tPlayer - " + Integer.toString(humanPlayer.getNumAces());

      postRoundString += "\n\nPoints Earned This Round: ";
      postRoundString += "\n\tComputer - " + Integer.toString(computerEarned);
      postRoundString += "\n\tPlayer - " + Integer.toString(humanEarned);

      postRoundString += "\n\nPress Ok to Continue";

      return postRoundString;
   }

/* *********************************************************************
Function Name: hasRoundEnded
Purpose: Check if the round has ended
Parameters:
         None
Return Value: A boolean value indicating if the round has ended
Local Variables:
         None
Algorithm:
         1) Loop through the players
         2) Check if the player has won
         3) Return the value
Assistance Received: none
********************************************************************* */
   public Boolean hasRoundEnded(){
      for (Player player: players){
         if (!(player.getPlayerHandVec().isEmpty())){
            return false;
         }
      }

      if (!(roundDeck.isDeckEmpty())){
         return false;
      }

      return true;
   }

/* *********************************************************************
Function Name: getHumanObj
Purpose: Get the Human object
Parameters:
         None
Return Value: A Human Object
Local Variables:
         None
Algorithm:
         1) Loop through the player vector
         2) Return the human object
Assistance Received: none
********************************************************************* */
   public Human getHumanObj(){
      if (players.elementAt(0) instanceof Human){
         return (Human)players.elementAt(0);
      }
      return (Human)players.elementAt(1);
   }

/* *********************************************************************
Function Name: getComputerObj
Purpose: Get the Computer object
Parameters:
         None
Return Value: A Computer Object
Local Variables:
         None
Algorithm:
         1) Loop through the player vector
         2) Return the Computer object
Assistance Received: none
********************************************************************* */
   public Computer getComputerObj(){
      if (players.elementAt(0) instanceof Computer){
         return (Computer)players.elementAt(0);
      }
      return (Computer)players.elementAt(1);
   }

/* *********************************************************************
Function Name: getBuilds
Purpose: Get the builds from round
Parameters:
         None
Return Value: A hashMap containing pairs of Integer and Builds
Local Variables:
         None
Algorithm:
         1) Return the builds hashmap
Assistance Received: none
********************************************************************* */
   public HashMap<Integer, Build> getBuilds(){
      return this.builds;
   }

/* *********************************************************************
Function Name: getDeck
Purpose: Get the deck
Parameters:
         None
Return Value: A Deck object
Local Variables:
         None
Algorithm:
         1) Return the deck object
Assistance Received: none
********************************************************************* */
   public Deck getDeck(){
      return this.roundDeck;
   }

/* *********************************************************************
Function Name: getTable
Purpose: Get the table
Parameters:
         None
Return Value: A vector of cards representing the table
Local Variables:
         None
Algorithm:
         1) Return the table
Assistance Received: none
********************************************************************* */
   public Vector<Card> getTable(){
      return this.table;
   }

/* *********************************************************************
Function Name: getPlayers
Purpose: Get the players
Parameters:
         None
Return Value: A vector of Players
Local Variables:
         None
Algorithm:
         1) Return the players
Assistance Received: none
********************************************************************* */
   public Vector<Player> getPlayers(){
      return this.players;
   }

/* *********************************************************************
Function Name: getNextPlayer
Purpose: Get the Next player's id
Parameters:
         None
Return Value: An integer containing the next player's id
Local Variables:
         None
Algorithm:
         1) Return nextPlayer
Assistance Received: none
********************************************************************* */
   public int getNextPlayer(){
      return this.nextPlayer;
   }

/* *********************************************************************
Function Name: setNextPlayer
Purpose: Set the Next player's id
Parameters:
         An integer containing the next player's id
Return Value: None
Local Variables:
         None
Algorithm:
         1) Set nextPlayer
Assistance Received: none
********************************************************************* */
   public void setNextPlayer(int id){
      this.nextPlayer = id;
   }

/* *********************************************************************
Function Name: getRound
Purpose: Get the current round number
Parameters:
         None
Return Value: An integer containing the current round number
Local Variables:
         None
Algorithm:
         1) Return the round number
Assistance Received: none
********************************************************************* */
   public int getRound(){
      return this.round;
   }

/* *********************************************************************
Function Name: getResumedGame
Purpose: Get the boolean value indicating if the game was resumed
Parameters:
         None
Return Value: A boolean value indicating if the game was resumed
Local Variables:
         None
Algorithm:
         1) Return resumedGame
Assistance Received: none
********************************************************************* */
   public Boolean getResumedGame(){
      return this.resumedGame;
   }

/* *********************************************************************
Function Name: getPlayerLastCaptured
Purpose: Get the Player who last captured
Parameters:
         None
Return Value: A Player object who last captured
Local Variables:
         None
Algorithm:
         1) Return playerLastCaptured
Assistance Received: none
********************************************************************* */
   public Player getPlayerLastCaptured(){
      return this.playerLastCaptured;
   }

/* *********************************************************************
Function Name: setPlayerLastCaptured
Purpose: Set the Player who last captured
Parameters:
         player, a Player object passed by value
Return Value: None
Local Variables:
         None
Algorithm:
         1) Set the playerLastCaptured
Assistance Received: none
********************************************************************* */
   public void setPlayerLastCaptured(Player player){
      this.playerLastCaptured = player;
   }
}
