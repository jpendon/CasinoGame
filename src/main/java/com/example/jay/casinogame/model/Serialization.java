/*
 **************************************************************
 * Name:  Jay Pendon						                     *
 * Project: Casino Game								         *
 * Class: CMPS 366 01 - Organization of Programming Languages *
 * Date:  Decemeber 11, 2018		                             *
 **************************************************************
 */

package com.example.jay.casinogame.model;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Serialization implements Serializable {
   private Vector<Player> players;
   private int round;
   private Vector<Card> deck;
   private Vector<Card>table;
   private HashMap<Integer, Build> builds;
   private int nextPlayer;
   private int lastCaptured;

   /* *********************************************************************
   Function Name: Serialization
   Purpose: To create a Serialization object
   Parameters:
               None
   Return Value: None
   Local Variables:
               None
   Algorithm:
               1) Set the default values for Serialization
   Assistance Received: none
   ********************************************************************* */
   public Serialization(){
      this.players = new Vector<Player>();
      this.round = 0;
      this.deck = new Vector<Card>();
      this.table = new Vector<Card>();
      this.builds = new HashMap<>();
      this.nextPlayer = 0;
      this.lastCaptured = 0;
   }

/* *********************************************************************
Function Name: saveGame
Purpose: To save the game
Parameters:
            file, a File object containing the File object passed by value
            round, an integer containing the current round passed by value
Return Value: None
Local Variables:
            roundSaveString, a string containing the text to be saved
            fileOutputStream, a FileOutputStream object used to save the
               string to text file
Algorithm:
            1) Get the string to be saved
            2) Save the string to a text file
Assistance Received: none
********************************************************************* */
   public void saveGame(File file, Round round){
      String roundSaveString = getRoundSaveString(round);

      try{
         FileOutputStream fileOutputStream = new FileOutputStream(file);
         fileOutputStream.write(roundSaveString.getBytes());
         fileOutputStream.close();
      }
      catch (IOException err){
         err.printStackTrace();
      }
   }

   /* *********************************************************************
Function Name: getRoundSaveString
Purpose: Get the string containing the information to be saved
Parameters:
         round, a Round object passed by value
Return Value: A string containing the information to be saved
Local Variables:
         humanPlayer, a Human object
         computerPlayer, a Computer object
         roundSaveString, a string containing the information to be saved

Algorithm:
         1) Save the round, computer information, human information,
            table information, build owners, deck and the next player's
            id
Assistance Received: none
********************************************************************* */
   public String getRoundSaveString(Round round){
      // humanPlayer and computerPlayer are objects to their respective classes
      Human humanPlayer;
      Computer computerPlayer;
      String roundSaveString = "";

      // Cast players to their derived classes
      if (round.getPlayers().elementAt(0) instanceof Human) {
         humanPlayer = (Human)round.getPlayers().elementAt(0);
         computerPlayer = (Computer)round.getPlayers().elementAt(1);
      }
      else {
         humanPlayer = (Human)round.getPlayers().elementAt(1);
         computerPlayer = (Computer)round.getPlayers().elementAt(0);
      }

      // Save the current round to file
      roundSaveString += "Round: ";
      roundSaveString +=  Integer.toString(round.getRound());

      // Save the Computer specifier to file
      roundSaveString += "\n\nComputer: \n";

      // Call the outputPlayers function for computer
      roundSaveString += outputPlayers(computerPlayer);

      // Save the Human specifier to the file
      roundSaveString += "Human: \n";
      // Call the outputPlayers function for Player
      roundSaveString += outputPlayers(humanPlayer);

      //Table Output
      roundSaveString += "Table: ";
      // Loop through the table
      for (Card card : round.getTable()) {
         // Save the card name to file
         roundSaveString += card.getCardName();
         roundSaveString += " ";
      }

      String line = "";
      // Check if builds is empty
      if (!round.getBuilds().isEmpty()) {
         // Loop through builds
         Iterator it = round.getBuilds().entrySet().iterator();

         while(it.hasNext()){
            HashMap.Entry pair = (HashMap.Entry) it.next();
            Build build = (Build)pair.getValue();

            // append the build name to line
            line += build.getCardName();

            // Check the Id of the owner of the build
            if (build.getOwnerId() == humanPlayer.getId()){
               // Append human to the line
               line += " Human ";
            }
            else {
               // Append Computer to the line
               line += " Computer ";
            }
         }

         // Save the specifier Build Owner and line to the file
         roundSaveString += "\n\nBuild Owner: ";
         roundSaveString += line;
      }

      roundSaveString += "\n\nLast Capturer: ";

      // Save Last Capturer
      if (round.getPlayerLastCaptured() instanceof Human){
         roundSaveString += "Human";
      }
      else{
         roundSaveString += "Computer";
      }

      // Save the Deck
      roundSaveString += "\n\nDeck: ";
      // Call outputDeck
      roundSaveString += round.getDeck().getAllCardNames();
      // Save Next Player
      roundSaveString += "\n\nNext Player: ";

      // Check if the nextPlayer is equal to the computer's Id
      if (nextPlayer == computerPlayer.getId()) {
         // Save Computer as the next player
         roundSaveString += "Computer";
      }
      else {
         // Save Human as the next Player
         roundSaveString += "Human";
      }

      return roundSaveString;
   }

/* *********************************************************************
Function Name: outputPlayers
Purpose: To save player information
Parameters:
            player, a Player object that is passed by value
Return Value: None
Local Variables:
            None
Algorithm:
            1) Print the score
            2) Print the hand
            3) Print the file
Assistance Received: none
********************************************************************* */
   public String outputPlayers(Player player){
      String playerSaveString = "";
      playerSaveString += "   Score: ";
      playerSaveString += Integer.toString(player.getScore());
      playerSaveString +="\n   Hand: ";
      playerSaveString += player.getPlayerHand();
      playerSaveString +="\n   Pile: ";
      playerSaveString += player.getPlayerPile();
      playerSaveString += "\n\n";

      return playerSaveString;
   }

   /* *********************************************************************
Function Name: resumeGame
Purpose: To resume the game
Parameters:
         file, an InputStream object containing the information
Return Value: None
Local Variables:
         line, a String that holds a line from the file
         playerCounter, an integer containing the amount of player's found
         humanId, an integer containing an id
         computerId, an integer containing an id
         fileBuff, a BufferedReader object that holds the buffer
         token, a string indicating the line's header
Algorithm:
            1) Parse through the file
            2) Perform action based on the token of the line
Assistance Received: none
********************************************************************* */
   public void resumeGame(InputStream file) throws IOException{
      // line holds the line from the file
      String line = "";
      // playerCounter holds an integer value that counts the amount of players found
      int playerCounter = 0;
      //humanId is equal to 0 indicating its Id
      int humanId = 0;
      // computerId holds the value of 1 indicating its Id
      int computerId = 1;
      // currentPlayer holds a Player
      Player currentPlayer = null;

      BufferedReader fileBuff = new BufferedReader(new InputStreamReader(file));
      // Loop continues until the end of the file
      while ((line = fileBuff.readLine()) != null) {
         // token word that specifies what the line is for
         String token;

         // Remove leading whitespace
         line = line.trim();
         // Split
         String [] splitLine = line.split(":");

         // token is initialized to the phrase before the delimiter
         token = splitLine[0];

         // Check if the token is equal to Round
         if (token.equals("Round")) {
            // round is equal to the value after Round
            this.round = Integer.parseInt(splitLine[1].trim());
         }
         // Check if the token is equal to Computer
         else if (token.equals("Computer")) {
            // Create a new Computer with an Id of 1
            Computer computer = new Computer(computerId);
            // Push the computer into the players vector
            this.players.add(computer);
         }
         // Check if the token is equal to Human
         else if (token.equals("Human")) {
            // Create a human with its Id as 0
            Human human = new Human(humanId);
            // Push the human into the players vector
            this.players.add(human);
         }
         // Check if the token is equal to Score
         else if (token.equals("Score")) {
            // currentPlayer is equal to the value from players at the playerCounter
            currentPlayer = this.players.elementAt(playerCounter);
            // Set the current player's score to the line's value
            currentPlayer.setScore(Integer.parseInt(splitLine[1].trim()));
         }
         // Check if the token is equal to Hand
         else if (token.equals("Hand")) {
            // currentPlayer is equal to the value from players at the playerCounter
            currentPlayer = this.players.elementAt(playerCounter);
            // Set the player's hand to the value from line
            if (splitLine.length != 1) {
               currentPlayer.setPlayerHand(getCardNames(splitLine[1]));
            }
         }
         // Check if the token is equal to Pile
         else if (token.equals("Pile")) {
            // currentPlayer is equal to the value from players at the playerCounter
            currentPlayer = this.players.elementAt(playerCounter);
            // Set the current player's pile to the line
            currentPlayer.setPlayerPile(getCardNames(splitLine[1]));
            // Increase playerCounter indicating that all of the information has been set
            playerCounter++;
         }
         // Check if the token is equal to Deck
         else if (token.equals("Deck")) {
            // Deck is equal to the return value of createCardVec
            if (splitLine.length != 1) {
               this.deck = createCardVec(getCardNames(splitLine[1].trim()));
            }
         }
         // Check if the token is equal to Table
         else if (token.equals("Table")) {
            // Create the table
            this.table = createTableVec(splitLine[1].trim());

         }
         // Check if the token is equal to Build Owner
         else if (token.equals("Build Owner")) {
            if (splitLine.length == 2) {
               HashMap<Player, String> buildOwners = findBuildOwner(splitLine[1].trim(), this.players);

               for (HashMap.Entry<Player, String> entry : buildOwners.entrySet()) {
                  createTableBuild(entry.getValue().trim(), this.table, this.builds, entry.getKey());
               }
            }
         }
         // Check if the token is equal to Last Capturer
         else if (token.equals("Last Capturer")){
            if (token.trim().equals("Human")){
               this.lastCaptured = humanId;
               continue;
            }
            this.lastCaptured = computerId;
         }

         // Check if the token is equal to Next Player
         else if (token.equals("Next Player")) {
            // Trim the line and check if it equal to Human
            if (splitLine[1].equals("Human")) {
               // nextPlayer is equal to humanId
               this.nextPlayer = humanId;
            }
            else {
               this.nextPlayer = computerId;
            }
         }
      }

   }

/* *********************************************************************
Function Name: getCardNames
Purpose: To get all the card names
Parameters:
            line, a string containing the line from the file passed by value
Return Value: A vector of strings containing card names
Local Variables:
            allCards, a vector of string containing all cards
            reg, a regex object containing the regular expression
            pattern, a Pattern object
            matcher, a Matcher object
Algorithm:
            1) Search for card names
            2) Return all card names
Assistance Received: none
********************************************************************* */
   public Vector<String> getCardNames(String line){
      // allCards is a vector that holds all cards
      Vector<String> allCards = new Vector<String>();

      Pattern pattern = Pattern.compile("([^\\[ ][^ ])");
      Matcher matcher = pattern.matcher(line);

      while(matcher.find()){
         allCards.add(matcher.group(0));
      }
      return allCards;
   }

/* *********************************************************************
Function Name: createCardVec
Purpose: To create cards
Parameters:
            allCards, a vector of strings containing all card names passed
                by value
Return Value: A vector of cards
Local Variables:
            cardVec, a vector of Cards
            card, a Card object
Algorithm:
            1) Go through the vector
            2) Create a Card
            3) Return all cards
Assistance Received: none
********************************************************************* */
   public Vector<Card> createCardVec(Vector<String> allCards){
      // cardVec is a vector that holds cards
      Vector<Card> cardVec = new Vector<Card>();
      // Loop through allCards vector
      for (String cardName: allCards) {
         // Create a new Card with the iterator's name
         Card card = new Card(Character.toString(cardName.charAt(0)), Character.toString(cardName.charAt(1)));
         // Push the card into the cardVec
         cardVec.add(card);
      }
      // Return the vector containing cards
      return cardVec;
   }

/* *********************************************************************
Function Name: createTableVec
Purpose: To create the table vector
Parameters:
            line, a string containing a line from the file
Return Value: A vector of Cards representing the table
Local Variables:
            tableCards, a vector of Card representing the table
            cardsInBuildReg, is a regex object to find cards in builds
            pattern, a Pattern object
            matcher, a Matcher object
            match, a string containing the card name
            newCard, a card to a new card
Algorithm:
            1) Go through the line
            2) Find card names
            3) Create cards
            4) Return cards
Assistance Received: none
********************************************************************* */
   public Vector<Card> createTableVec(String line){
      Vector<Card> tableCards = new Vector<Card>();

      //Cards In Build Vector
      Pattern pattern = Pattern.compile("([^\\[ ][^ ])");
      Matcher matcher = pattern.matcher(line);

      while (matcher.find()) {
         String match = matcher.group(0);

         // Create a new card
         Card newCard = new Card(Character.toString(match.charAt(0)), Character.toString(match.charAt(1)));
         // push the card into the table vector
         tableCards.add(newCard);

      }
      // Return the tableCards vector
      return tableCards;
   }

/* *********************************************************************
Function Name: findBuildOwner
Purpose: To find the build owners
Parameters:
           line, a string passed by value containing the line from
               the file
           players, a vector of players passed by value
Return Value: A player to the owner of the build
Local Variables:
           buildOwner, player who owns the build
           pattern, a Pattern object
           matcher, a Matcher object
Algorithm:
           1) Find the position of the human
               a) Set the owner as the human
           2) If not found find the computer
           3) Set the computer as owner of the build
Assistance Received: none
********************************************************************* */
   public HashMap<Player, String> findBuildOwner(String line, Vector<Player> players){
      HashMap<Player, String> playerBuilds = new HashMap<Player, String>();
      Player buildOwner;

      Pattern pattern = Pattern.compile("\\[ \\[[A-Z].( [A-Z].)*]( \\[[A-Z].( [A-Z].)*])* ] Human");
      Matcher matcher = pattern.matcher(line);

      while (matcher.find()){
         pattern = Pattern.compile("\\[ \\[[A-Z].( [A-Z].)*]( \\[[A-Z].( [A-Z].)*])* ]");
         matcher = pattern.matcher(matcher.group(0));

         buildOwner = players.elementAt(1);

         while(matcher.find()) {
            playerBuilds.put(buildOwner, matcher.group(0));
         }
      }

      // Single Build Pattern Human
      pattern = Pattern.compile("\\[[A-Z].( [A-Z].)*] Human");
      matcher = pattern.matcher(line);

      while (matcher.find()){
         pattern = Pattern.compile("\\[[A-Z].( [A-Z].)*]");
         matcher = pattern.matcher(matcher.group(0));

         buildOwner = players.elementAt(1);

         while(matcher.find()) {
            playerBuilds.put(buildOwner, matcher.group(0));
         }
      }

      pattern = Pattern.compile("\\[ \\[[A-Z].( [A-Z].)*]( \\[[A-Z].( [A-Z].)*])* ] Computer");
      matcher = pattern.matcher(line);

      // Computer
      while (matcher.find()){
         pattern = Pattern.compile("\\[ \\[[A-Z].( [A-Z].)*]( \\[[A-Z].( [A-Z].)*])* ]");
         matcher = pattern.matcher(matcher.group(0));


         while(matcher.find()) {
            buildOwner = players.elementAt(1);
            playerBuilds.put(buildOwner, matcher.group(0));
         }
      }

      // Single Build Pattern Computer
      pattern = Pattern.compile("\\[[A-Z].( [A-Z].)*] Computer");
      matcher = pattern.matcher(line);

      while (matcher.find()){
         pattern = Pattern.compile("\\[[A-Z].( [A-Z].)*]");
         matcher = pattern.matcher(matcher.group(0));

         buildOwner = players.elementAt(0);

         while(matcher.find()) {
            playerBuilds.put(buildOwner, matcher.group(0));
         }
      }



      return playerBuilds;
   }

   /* *********************************************************************
   Function Name: createTableBuild
   Purpose: To create table builds
   Parameters:
               line, a string passed by value containing the line from
                   the file
               table, a vector of Cards passed by value containing
                   table cards
               builds, a list of a pair containing an integer and a Build
                   passed by value. It represents the owner of the build's
                   Id and the build.
               player, an object of player passed by value
   Return Value: None
   Local Variables:
               cardNames, a vector of string indicating card names
               cardsInBuilds, a vector of string indicating cards in builds
               multipleBuild, a boolean value indicating that the build
                   was a multiple build
               pattern, a Pattern object
               matcher, a Matcher object
   Algorithm:
               1) Check if the build is a multiple build
               2) find all cards in the build
               3) Create the build
               4) Add the card to the table and list
   Assistance Received: none
   ********************************************************************* */
   public void createTableBuild(String line, Vector<Card> table, HashMap<Integer, Build> builds,
                                Player player){
      Vector<String> cardNames = new Vector<String>();
      Vector<String> cardsInBuilds = new Vector<String>();
      Boolean multipleBuild = false;


      //Check if build is a multiple build
      multipleBuild = checkIfMultipleBuild(line);

      //Find all Cards in the build
      Pattern pattern = Pattern.compile("[A-Z].( [A-Z].)*");
      Matcher matcher = pattern.matcher(line);

      while (matcher.find()) {
         String match = matcher.group(0);

         cardNames.add(match);

         String[] cardSplit = match.split(" ");

         for (String cardName: cardSplit){
            cardsInBuilds.add(cardName);
         }
      }

      //Remove Cards in Builds from table

      for (Iterator<Card> it = table.iterator(); it.hasNext();){
         Card card = it.next();
         for (String cardName: cardsInBuilds) {
            if (cardName.equals(card.getCardName())) {
               it.remove();
               break;
            }
         }
      }


      // vector newTableBuilds Holds all builds
      Vector<Build> newTableBuilds = new Vector<Build>();

      //Create Build(s)
      for (String cardName: cardNames) {
         Build newBuild = new Build(cardName, player.getId());
         newTableBuilds.add(newBuild);
      }



      //Check if a multiple build is needed to be created
      if (multipleBuild) {
         Build newMultipleBuild = new Build();
         newMultipleBuild = createMultipleBuild(newTableBuilds, table, player);
         newMultipleBuild.setMultipleBuild(true);
         builds.put(player.getId(), newMultipleBuild);
      }
      else {
         Build oldBuild = newTableBuilds.elementAt(0);
         Build newBuild = new Build(oldBuild);

         table.add(newBuild);
         builds.put(player.getId(), newBuild);
      }

   }

/* *********************************************************************
Function Name: checkIfMultipleBuild
Purpose: To check if the build is a multiple build
Parameters:
            line, a string passed by value from the file
Return Value: A boolean value indicating if the build is part of
                a multiple build
Local Variables:
            reg, a regex object containing the regular expression
            pattern, a Pattern object
            matcher, a Matcher object
Algorithm:

Assistance Received: none
********************************************************************* */
   public Boolean checkIfMultipleBuild(String line) {
      //Regex Expression to Determine if line contains a multiple Build
      Pattern pattern = Pattern.compile("\\[ \\[[A-Z].( [A-Z].)*\\]( \\[[A-Z].( [A-Z].)*\\])* \\]");
      Matcher matcher = pattern.matcher(line);

      while (matcher.find()) {
         return true;
      }
      return false;
   }

/* *********************************************************************
Function Name: createMultipleBuild
Purpose: To create a multiple build
Parameters:
         newTableBuilds, a vector of Builds passed by value
         table, a vector of card passed by value
         player, a Player passed by value
Return Value: A new build
Local Variables:
         newMultipleBuild, is a new build
         multipleBuildName, is a string containing the new build's name
Algorithm:
         1) Create the new multiple build's name
         2) Set the values equal to the name
Assistance Received: none
********************************************************************* */
   public Build createMultipleBuild(Vector<Build> newTableBuilds, Vector<Card>table, Player player){
      // Create a new multiple build
      Build newMultipleBuild = new Build();
      //
      Vector<Card> cardParts = new Vector<Card>();

      // multipleBuildName holds the new build's name
      String multipleBuildName = "[ ";
      for (Build build:newTableBuilds) {
         multipleBuildName += build.getCardName();
         multipleBuildName += " ";

         for (Card card: build.getCardParts()){
            cardParts.add(card);
         }

         newMultipleBuild.addBuildPart(build);
      }

      multipleBuildName += "]";

      newMultipleBuild.setCardName(multipleBuildName);
      newMultipleBuild.setNumValue(newTableBuilds.elementAt(0).getNumValue());
      newMultipleBuild.setOwnerId(player.getId());
      newMultipleBuild.setMultipleBuild(true);
      newMultipleBuild.setCardParts(cardParts);
      table.add(newMultipleBuild);

      // Remove used Cards
      for (Iterator<Card> iter = this.table.iterator(); iter.hasNext();){
         Card tableCard = iter.next();
         for (Card card: cardParts){
            if (tableCard.getCardName().equals(card.getCardName())){
               iter.remove();
            }
         }
      }

      return newMultipleBuild;
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
   public Vector<Card> getDeck(){
      return this.deck;
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
Function Name: getBuilds
Purpose: Get the builds
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
Function Name: getLastCaptured
Purpose: get the Player's id who last captured
Parameters:
Return Value: an integer indicating the player who last captured
Local Variables:
         None
Algorithm:
         1) get the lastCaptured
Assistance Received: none
********************************************************************* */
   public int getLastCaptured(){
      return this.lastCaptured;
   }
}
