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

public class Player implements Serializable {
   private int id;
   private int playerScore;
   private Boolean isWinner;
   protected Vector<Card> playerHand = new Vector<Card>();
   protected Vector<Card> playerPile = new Vector<Card>();

/* *********************************************************************
Function Name: Player
Purpose: To create a Player object
Parameters:
            None
Return Value: None
Local Variables:
            None
Algorithm:
            None
Assistance Received: none
********************************************************************* */
   public Player(int id) {
      this.id = id;
      this.playerScore = 0;
      this.isWinner = false;
      this.playerHand = new Vector<Card>();
      this.playerPile = new Vector<Card>();
   }

   /* *********************************************************************
Function Name: Player
Purpose: Create a deep copy of the Player object
Parameters:
         player, a Player object passed by value
Return Value: None
Local Variables:
         None
Algorithm:
         1) Copy the player's contents
Assistance Received: none
********************************************************************* */
   public Player(Player player){
      this.id = player.getId();
      this.playerScore = player.getScore();
      this.isWinner = player.getWinner();
      for (Card card: player.getPlayerHandVec()) {
         this.playerHand.add(new Card(card));
      }
   }

/* **********************************************************************
Function Name: drawFromDeck
Purpose: To draw from the deck
Parameters:
            currentDeck, is a Deck passed by value.
Return Value: None
Local Variables:
            None
Algorithm:
            1) Player receives four cards from the deck
Assistance Received: None
***********************************************************************/
   public void drawFromDeck(Deck deck) {
      for (int i = 0; i < 4; i++) {
         // Draw a Card from Deck
         Card card = deck.drawCard();
         // Add the card to the player's hand
         playerHand.add(card);
      }
   }

   /* *********************************************************************
Function Name: getId
Purpose: Get id
Parameters:
         None
Return Value: an integer representing the player's id
Local Variables:
         None
Algorithm:
         1) return the id
Assistance Received: none
********************************************************************* */
   public int getId(){
      return id;
   }

/* **********************************************************************
Function Name: getPlayerHand
Purpose: To get the player's hand
Parameters:
            None
Return Value: A string that contains the player's hand
Local Variables:
            hand, a string that contains the card names of the player's hand
Algorithm:
            1) Append each card's name into the string
            2) Return the string
Assistance Received: None
***********************************************************************/
   public String getPlayerHand() {
      // strPlayerHand contains a string representing all the cards in their hand
      String strPlayerHand = "";

      // Iterate through the player's hand
      for (Card card : playerHand) {
         // Add the Card's name to the string
         strPlayerHand += card.getCardName();
         strPlayerHand += " ";
      }

      return strPlayerHand;
   }

   public void setPlayerHand(Vector<String> playerHand){
      for (String cardName: playerHand){
         Card card = new Card(Character.toString(cardName.charAt(0)), Character.toString(cardName.charAt(1)));
         this.playerHand.add(card);
      }
   }

/* **********************************************************************
Function Name: getPlayerPile
Purpose: To get the player's pile
Parameters:
            None
Return Value: A string containing the player's pile
Local Variables:
            pile, a string containing a player's pile
Algorithm:
            1) Loop through a player's pile
            2) Append the card's name to a string
            3) Return the string
Assistance Received: None
***********************************************************************/
   public String getPlayerPile() {
      // strPlayerPile contains a string representing all the cards in their pile
      String strPlayerPile = "";

      // Iterate through the player's pile
      for (Card card : playerPile) {
         // Add the Card's name to the string
         strPlayerPile += card.getCardName();
         strPlayerPile += " ";
      }

      return strPlayerPile;
   }

   /* **********************************************************************
   Function Name: setPlayerPile
   Purpose: To set the player's pile
   Parameters:
               pile, a vector containing strings passed by value that contains
                   a player's pile
   Return Value: None
   Local Variables:
               newCard, a Card pointer that points to a new card
   Algorithm:
               1) Loop through the pile
               2) Add the Card to the player's pile
   Assistance Received: None
   ***********************************************************************/

   public void setPlayerPile(Vector<String> playerPile){
      for (String cardName: playerPile){
         Card card = new Card(Character.toString(cardName.charAt(0)), Character.toString(cardName.charAt(1)));
         this.playerPile.add(card);
      }
   }


/* **********************************************************************
Function Name: createBuild
Purpose: To create a build
Parameters:
         buildCards, a vector of Card passed by value. It represents
            the cards used to create a build
         chosenCard, a Card passed by value. The pointer points to a Card chosen
            by the computer
         builds, a hashmap containing a pair of integer and a Build
            that is passed by value.
Return Value: None
Local Variables:
         newBuild, a Build that points to a Build object
         newPair, a pair that contains an integer and Build pointer. It holds the id
            and a pointer to the new build
Algorithm:
         1) Create a new build with all the Cards
         2) Create a pair with the Computer's Id and the pointer to the new build
Assistance Received: None
***********************************************************************/
   public void createBuild(Card chosenCard, Vector<Card> chosenTableCards, Vector<Card> table, HashMap<Integer, Build> builds) {
      Build newBuild;
      int buildValue = 0;

      Vector<Card> cardParts = chosenTableCards;

      // Add the Card into the vector
      cardParts.add(chosenCard);

      // Add the Card's numValue to the buildValue
      for (Card card: cardParts){
         buildValue += card.getNumValue();
      }

      // Create the new build
      newBuild = new Build(cardParts, buildValue, id);
      // Add the Build to the table
      table.add(newBuild);
      // Add the build to the HashMap for Builds
      builds.put(id, newBuild);

      // Remove the Card From Table
      removeCardsFromTable(cardParts, table);

      // Remove the Card From Hand
      playerHand.remove(chosenCard);

   }

   /* *********************************************************************
Function Name: extendBuild
Purpose: Extend a build
Parameters:
         chosenCard, a Card object passed by value
         chosenBuild, a Build object passed by value
         table, a Vector of cards passed by value
         builds, a HashMap containing an integer and a build passed by value
Return Value: a Build object
Local Variables:
         newBuild, a Build object containing the new Build
         newBuildValue, an integer containing the numerical value of the build
Algorithm:
         1) Get the new Build's value
         2) Add the cards to cardParts
         3) Remove the chosen card from the player's hand
         4) Remove the cards from the table
         5) Create the Build
         6) Add the build to the table and hashmap
Assistance Received: none
********************************************************************* */
   public Build extendBuild(Card chosenCard, Build chosenBuild, Vector<Card> table, HashMap<Integer, Build> builds){
      // newBuild will contain the extended build
      Build newBuild;
      // newBuildValue will contain the new build's numerical value
      int newBuildValue = chosenBuild.getNumValue();

      // cardParts contains the Build's Cards
      Vector<Card> cardParts = new Vector<Card>();

      // Add cards to cardParts
      for (Card card: chosenBuild.getCardParts()){
         cardParts.add(new Card(card));
      }

      // Add the chosen Card to the vector
      cardParts.add(chosenCard);

      // Add the chosen Card's numerical value to the newBuildValue
      newBuildValue += chosenCard.getNumValue();

      // Remove chosen Card from hand
      playerHand.remove(chosenCard);
      // Remove chosen Build from table
      table.remove(chosenBuild);
      // Remove the chosen Build from the builds
      builds.remove(chosenBuild.getOwnerId());

      // Create the New Build
      newBuild = new Build(cardParts, newBuildValue, id);

      // Add the build to the table
      table.add(newBuild);
      // Add the build to the vector of builds
      builds.put(id, newBuild);
      // Remove card from player's hand
      removeCardFromHand(chosenCard);

      return newBuild;
   }

/* **********************************************************************
Function Name: createMultipleBuildOption
Purpose: To create a Multiple Build
Parameters:
            chosenCard, a Card passed by value that represents a chosen card
            table, a vector of Card passed by value that holds
                 builds and cards
            chosenTableCards, a vector of Card pointers that holds Card
                that the player used passed by value
            builds, a hashmap containing an integer and a build
Return Value: A Build pointer that points to the new Multiple Build
Local Variables:
            playerBuild, a build that contains the current build
            chosenMultipleBuild, a build that points to the new build
            buildPart, a vector containing Build that are part of the build
            newMultipleBuild, a Build that represents a new Build
            buildValue, an integer that holds the new build's numerical value
Algorithm:
            1) Calculate the intended build value for the multiple build
            2) Create a part of the multiple build
            3) Create the multiple builds from the parts
            4) Add the build to the list
            5) Add the build to the table
Assistance Received: None
***********************************************************************/
   public void createMultipleBuild(Card chosenCard, Vector<Card> chosenTableCards, Vector<Card> table,
                                   HashMap<Integer, Build> builds){
      // playerBuild contains the player's current build
      Build playerBuild;
      // buildValue contains the build's value
      int buildValue = 0;
      // newBuildName contains the String representing the build's name
      String newBuildName = "";

      playerBuild = builds.get(id);
      buildValue = playerBuild.getNumValue();

      // Create the Build part
      Build buildPart = new Build(chosenTableCards, buildValue, id);

      // Create the multiple build's string
      newBuildName += "[ ";
      for (Build build: playerBuild.getBuildParts()){
         newBuildName += build.getCardName();
         newBuildName += " ";
      }

      newBuildName += buildPart.getCardName();
      newBuildName +=" ]";

      Vector<Card> allCardParts = new Vector<Card>();
      Vector<Card> buildPartCards = buildPart.getCardParts();

      for (Card card: playerBuild.getCardParts()){
         allCardParts.add(new Card(card));
      }

      // Add all Cards from the second Build
      for (Card card: buildPartCards){
         allCardParts.add(new Card(card));
      }

      // Create the New Multiple Build
      Build newMultipleBuild = new Build(allCardParts, buildValue, id);
      // Set the Correct Name for the build
      newMultipleBuild.setBuildName(newBuildName);



      // Remove the Original Build From the table
      for (Card tableCard: table){
         // Check if the tableCard's name is equal to the player's build
         if (tableCard.getCardName().equals(playerBuild.getCardName())){
            // Remove the build from the table
            table.remove(tableCard);
            break;
         }
      }
      // Remove cards from table
      removeCardsFromTable(chosenTableCards, table);

      // Remove the Original Build From the HashMap
      builds.remove(id);

      // Remove the Cards used from the player's hand REFACTOR
      for (Iterator<Card> iter = playerHand.iterator(); iter.hasNext(); ){
         // currentCard contains the iterator's current Card
         Card currentCard = iter.next();
         // Iterate through the Vector of Cards in cardParts
         for (Card usedCard: allCardParts) {
            // Check if the currentCard's name is equal to the usedCard
            if (currentCard.getCardName().equals(usedCard.getCardName())) {
               // Remove the current Card from the player's hand
               iter.remove();
               break;
            }
         }
      }
      newMultipleBuild.setMultipleBuild(true);
      // Add the build to the table
      table.add(newMultipleBuild);
      // Add the build to the HashMap
      builds.put(id, newMultipleBuild);
   }

   /* *********************************************************************
Function Name: captureOption
Purpose: Provides a default value for the overloaded function
Parameters:

Return Value:
Local Variables:

Algorithm:

Assistance Received: none
********************************************************************* */
   public void captureOption(Card chosenCard, Vector<Card> table, HashMap<Integer, Build> builds) {
      Vector<Card> temp = new Vector<Card>();
      captureOption(chosenCard, table, builds, temp);
   }

   /* *********************************************************************
Function Name: captureOption
Purpose: To capture cards
Parameters:
         chosenCard, a Card object passed by value representing the player's chosen card
Return Value: None
Local Variables:
         captureValue, a numerical value representing the chosenCard's value
Algorithm:
         1) Loop through the table
            a) Check if the card's numerical value is equal to the chosenCard's value
            b) Add the card if it is equal
            c) Remove the card from the table
Assistance Received: none
********************************************************************* */
   public void captureOption(Card chosenCard, Vector<Card> table, HashMap<Integer, Build> builds,
                              Vector<Card> usedCards) {
      // captureValue contains the chosenCard's numerical value
      int captureValue = chosenCard.getNumValue();

      // Add the Card to the player's pile
      playerPile.add(chosenCard);

      // Remove the Card from the player's hand
      playerHand.remove(chosenCard);

      // Iterate through the table
      Iterator<Card> iter = table.iterator();
      while(iter.hasNext()){
         Card card = iter.next();
         // Check if the chosen value is equal to the card's numerical value
         if (captureValue == card.getNumValue()) {
            // Check if the card is part of the build
            if (card instanceof Build) {
               // Capture the Build
               captureBuild((Build) card, usedCards);
               // Remove Build from the table
               iter.remove();
               // Remove Build from build map
                  builds.remove(getBuildId((Build)card, builds));
                  continue;
            }

            // Add the card to the table
            playerPile.add(card);
            usedCards.add(card);

            // Remove the card from the table
            iter.remove();
         }
      }

      return;
   }


   /* *********************************************************************
Function Name: captureBuild
Purpose: To capture a Build
Parameters:
         build, a Build object passed by value
Return Value: None
Local Variables:
         None
Algorithm:
         1) Loop through the build's cardParts and add the card to the player's pile
Assistance Received: none
********************************************************************* */
   public void captureBuild(Build build, Vector<Card> usedCards){
      // Get the Cards from the build
      Vector<Card> cardParts = build.getCardParts();
      usedCards.add(build);

      // Iterate through the card parts
      for (Card card: cardParts){
         // Add each card to the Player's pile
         playerPile.add(card);
      }
   }

/* *********************************************************************
Function Name: getBuildId
Purpose: Get the build's owner id
Parameters:
         chosenBuild, a Build object passed by value representing the chosen build
         builds, a hashmap containing an integer and build pair
Return Value: an integer representing the build's owner id
Local Variables:
         it, an iterator used to iterate through hashmap
Algorithm:
         1) Iterate through the hashmap and find the chosen build
         2) Return the id
Assistance Received: none
********************************************************************* */
   public int getBuildId(Build chosenBuild, HashMap<Integer, Build> builds){
      Iterator it = builds.entrySet().iterator();

      while(it.hasNext()){
         HashMap.Entry pair = (HashMap.Entry) it.next();
         Build build = (Build)pair.getValue();
         if (chosenBuild.getCardName().equals(build.getCardName())){
            return (int)pair.getKey();
         }
      }

      return -1;
   }

/* **********************************************************************
Function Name: trailCard
Purpose: To trail a card
Parameters:
            cardChosen, a string that holds the name of a card chosen
                by the user passed by value
            tableCards, a vector containing cards passed by value
                that contains the table cards
Return Value: A vector of Card pointers representing the new table
Local Variables:
            None
Algorithm:
            1) Add the card to the table
            2) Return the table
Assistance Received: None
***********************************************************************/
   public void trailCard(Card card, Vector<Card> table){
      // Remove the card from the player's hand
      playerHand.remove(card);
      // Add the card to the table
      table.add(card);
   }

/* **********************************************************************
Function Name: removeCardsFromTable
Purpose: To remove a card from the table
Parameters:
            usedCards, a vector of Cards passed by
                value that represents user chosen cards and builds
            table, a vector of Cards passed by value holding
                table cards
Return Value: A vector of Card pointers with the new table
Local Variables:
Algorithm:
            1) Loop through the user chosen cards and builds
            2) Remove any card and builds from the table
            3) Return the new table
Assistance Received: None
***********************************************************************/
   public void removeCardsFromTable(Vector<Card> usedCards, Vector<Card> table){
      // Iterate through the table Vector
      for (Iterator<Card> iter = table.iterator(); iter.hasNext(); ){
         // currentCard contains the iterator's current Card
         Card currentCard = iter.next();
         // Iterate through the Vector of Cards in cardParts
         for (Card usedCard: usedCards) {
            // Check if the currentCard's name is equal to the usedCard
            if (currentCard.getCardName().equals(usedCard.getCardName())) {
               // Remove the current Card from the table
               iter.remove();
               break;
            }
         }
      }
   }

/* **********************************************************************
Function Name: removeCardFromHand
Purpose: To remove the card from hand
Parameters:
            usedCard, a Card that contains the user chosen card passed by value
Return Value: None
Local Variables:
            None
Algorithm:
            1) Loop through the player's hand
            2) Remove the user's chosen card
Assistance Received: None
***********************************************************************/
   public void removeCardFromHand(Card usedCard){
      for (Iterator<Card> iter = this.playerHand.iterator(); iter.hasNext();){
         Card card = iter.next();
         if (card.getCardName().equals(usedCard.getCardName())){
            iter.remove();
         }
      }
   }

/* **********************************************************************
Function Name: checkExtendBuildConditions
Purpose: To check for extending build conditions
Parameters:
            builds, a hashmap of pairs containing an integer and a Build
                passed by value. It represents the owner of the build's
                Id and the build.
Return Value: Returns a boolean value indicating that the player can extend
Local Variables:
            playerOwnsABuild, is a boolean variable that indicates that the player owns
                a build
            validExtend, is a boolean variable that indicates that the extend was valid
Algorithm:
            1) Check conditions
            4) Check if the opponent's build was a multiple build
            5) Create the new extended build
Assistance Received: None
***********************************************************************/
   public Boolean checkExtendBuildConditions(HashMap<Integer, Build> builds){
      Boolean validExtend = false;
      if (!checkExtendFirstConditions(builds)){
         return false;
      }

      // entry contains the Key,Value of the only build in builds
      HashMap.Entry<Integer, Build> entry = builds.entrySet().iterator().next();
      // opponentBuild contains the opponent's build
      Build opponentBuild = entry.getValue();

      // Check if the opponent's build is a multiple build
      Boolean isMultipleBuild = opponentBuild.getIsMultipleBuild();

      // Check if the build is a multiple build
      if (isMultipleBuild){
         // return false because the player cannot extend a multiple build
         return false;
      }

      // Loop through the player's hand
      for (Iterator<Card> iter = playerHand.iterator(); iter.hasNext();){
         Card currentCard = iter.next();
         // Loop through the player's hand
         for (Card card: playerHand){
            int buildValue = currentCard.getNumValue() + opponentBuild.getNumValue();

            if (buildValue == card.getNumValue()){
               validExtend = checkBuildValueInHand(playerHand, buildValue);
            }
         }
      }
      // Return validExtend indicating if the extend can or cannot occur
      return validExtend;

   }

/* **********************************************************************
Function Name: checkExtendBuildConditions
Purpose: To check for extending build conditions
Parameters:
            chosenCard, a Card object passed by value representing the player's chosen card
            builds, a hashmap of pairs containing an integer and a Build
                passed by value. It represents the owner of the build's
                Id and the build.
Return Value: Returns a boolean value indicating that the player can extend
Local Variables:
            playerOwnsABuild, is a boolean variable that indicates that the player owns
                a build
            validExtend, is a boolean variable that indicates that the extend was valid
Algorithm:
            1) Check conditions
            4) Check if the opponent's build was a multiple build
            5) Create the new extended build
Assistance Received: None
***********************************************************************/
   public Boolean checkExtendBuildConditions(Card chosenCard, HashMap<Integer, Build> builds) {
      if (!checkExtendFirstConditions(builds)){
         return false;
      }
      // entry contains the Key,Value of the only build in builds
      HashMap.Entry<Integer, Build> entry = builds.entrySet().iterator().next();
      // opponentBuild contains the opponent's build
      Build opponentBuild = entry.getValue();

      // Check if the opponent's build is a multiple build
      Boolean isMultipleBuild = opponentBuild.getIsMultipleBuild();

      // Check if the build is a multiple build
      if (isMultipleBuild){
         // return false because the player cannot extend a multiple build
         return false;
      }

      Boolean validExtend = false;
      // Loop through the player's hand
      for (Card card: playerHand){
         int buildValue = chosenCard.getNumValue() + opponentBuild.getNumValue();

         if (buildValue == card.getNumValue()){
            validExtend = checkBuildValueInHand(playerHand, buildValue);
         }

      }
      // Return validExtend indicating if the extend can or cannot occur
      return validExtend;
   }

   /* **********************************************************************
Function Name: checkExtendBuildFirstConditions
Purpose: To check for extending build conditions
Parameters:
            builds, a hashmap of pairs containing an integer and a Build
                passed by value. It represents the owner of the build's
                Id and the build.
Return Value: Returns a boolean value indicating that the player can extend
Local Variables:
            playerOwnsABuild, is a boolean variable that indicates that the player owns
                a build
            validExtend, is a boolean variable that indicates that the extend was valid
Algorithm:
            1) Check if the Player owns a build
            2) Check if the build list is empty
            3) Check if the opponent owns a build
            4) Check if the opponent's build was a multiple build
Assistance Received: None
***********************************************************************/

   public Boolean checkExtendFirstConditions(HashMap<Integer, Build> builds){
      // playerOwnsABuild holds a boolean value indicating that the player owns a build
      Boolean playerOwnsABuild = false;

      // playerOwnsABuild is equal to
      playerOwnsABuild = builds.containsKey(id);
      // Check if the playerOwnsABuild is true
      if (playerOwnsABuild){
         // Player owns a build, therefore return false
         return false;
      }
      // Check if the builds list is empty
      if (builds.isEmpty()){
         // HashMap is empty and false is returned
         return false;
      }
      // entry contains the Key,Value of the only build in builds
      HashMap.Entry<Integer, Build> entry = builds.entrySet().iterator().next();
      // opponentBuild contains the opponent's build
      Build opponentBuild = entry.getValue();

      // Check if the opponent's build is a multiple build
      Boolean isMultipleBuild = opponentBuild.getIsMultipleBuild();

      // Check if the build is a multiple build
      if (isMultipleBuild){
         // return false because the player cannot extend a multiple build
         return false;
      }

      return true;
   }

/* **********************************************************************
Function Name: checkIfCanBuildMultiple
Purpose: Check if the computer can build a multiple build
Parameters:
            playerHand, a vector of Cards passed by value. It
                represents the computer's hand
            tableCards, a vector of Cards passed by value. It holds table cards and builds
            builds, a hashmap containing a pair of integer and a Build
                that is passed by value.
Return Value: A boolean value that represents if a computer can create a multiple build
Local Variables:
            playerOwnsABuild, a boolean value that represents if the computer owns a build
            validBuild, a boolean value that represents if a build is valid
            potentialBuild, a vector of Card that represents a potential build
            looseCards, a vector of Card that represents loose cards
            largestBuild, a Build that represents largest possible build
            playerHandWithoutChosenCard, a vector of Card without the chosen card
Algorithm:
            1) Check if the computer owns a build
            2) Remove the build from the list
            3) Get loose cards less than the original build
            4) Find a build equal to the original build's numerical value
            5) Create the multiple build
            6) Return the multiple build
Assistance Received: None
***********************************************************************/
   public Boolean checkIfCanBuildMultiple(Vector<Card> table, HashMap<Integer, Build> builds){
      // playerOwnsABuild holds a boolean value indicating that the computer owns a build
      Boolean playerOwnsABuild = false;
      // validBuild holds a boolean value indicating that the computer can create a build
      Boolean validBuild = false;
      // originalBuild contains the computer's existing build
      Build originalBuild = null;

      playerOwnsABuild = builds.containsKey(getId());

      if (playerOwnsABuild){
         Vector<Card> possibleBuild = new Vector<Card>();
         Vector<Card> looseCards = new Vector<Card>();

         // Loop through the HashMap for build
         for (HashMap.Entry<Integer, Build> pair: builds.entrySet()){
            // Check if the computer's id matches the key,value pair's key
            if (getId() == pair.getKey()){
               // originalBuild is equal to the computer's existing build
               originalBuild = pair.getValue();
               break;
            }
         }

         // looseCards is equal to the return value of getLooseCards
         looseCards = getLooseCards(table);

         // playerHandWithoutChosenCard is a vector containing all the cards except the chosen card
         Vector<Card> playerHandWithoutChosenCard = new Vector<Card>();

         // Loop through the player's hand
         for (Iterator<Card> iter = playerHand.iterator(); iter.hasNext();){
            //Create a temporary build
            Build largestBuild = new Build(new Vector<Card>(), -1, getId());

            // playerCard is equal to the current iterator's card
            Card playerCard = iter.next();

            // Calculate the largest build
            calculateBuilds(looseCards, possibleBuild, playerCard, originalBuild, largestBuild);

            if (playerCard.getNumValue() == originalBuild.getNumValue()){
               // Loop through the player's hand

               for (Card handCard: playerHand){
                  // Check if the handCard is not equal to the current Card
                  if (!handCard.getCardName().equals(playerCard.getCardName())){
                     // Add the playerCard into the playerHandWithoutChosenCard
                     playerHandWithoutChosenCard.add(handCard);
                  }
               }
               // validPartialBuild is equal to the value returned by checkBuildValueInHand
               Boolean validPartialBuild = checkBuildValueInHand(playerHandWithoutChosenCard, playerCard.getNumValue());

               // Check if the build is valid
               if (validPartialBuild){
                  // Add the playerCard into the largestBuild
                  largestBuild.addCard(playerCard);
               }
            }
            // Check if the largestBuild contains cards
            if (largestBuild.getCardParts().size() != 0){
               // validBuild is equal to true because a multiple build can be created
               validBuild = true;
            }
         }
      }

      return validBuild;
   }

/* **********************************************************************
Function Name: getLooseCards
Purpose: To find all of the loose cards less than the current card's value
Parameters:
            table, a vector of Card passed by value. It holds table cards and builds.
Return Value: A vector of Card pointers containing loose cards
Local Variables:
            looseCards, a vector of Cards holding loose cards
Algorithm:
            1) Go through the table
            2) Add the Card  to the looseCard vector if it is not part of a build
            3) Return the vector of loose cards
Assistance Received: None
***********************************************************************/
   public Vector<Card> getLooseCards(Vector<Card>table){
      // looseCards contains the loose cards
      Vector<Card> looseCards = new Vector<Card>();
      // Loop through the table
      for (Card card: table){
         // Check if the card is not a build and does not equal or greater than cardVal
         if (!(card instanceof Build)){
            // Add the card to the vector containing loose cards
            looseCards.add(card);
         }
      }
      // Return the vector containing loose cards
      return looseCards;
   }

/* **********************************************************************
Function Name: calculateBuilds
Purpose: Calculate the largest build with the chosen card to play
Parameters:
            table, a vector of Card passed by value. It holds table cards and builds
            potentialBuild, a vector of Card that represents a potential build passed by value
            chosenCard, a Card that points to the card that would be chosen by the computer
                passed by value
            playerCardInHand, a Card that points to a Card in hand that a Build needs to be equal
                to passed by value
            largestBuild, a Build that the largest possible build passed by value
Return Value: None
Local Variables:
                potentialBuildValue, an integer that represents a possible build's numerical value
                playedCardValue, an integer that represents the target build needed to create a build
                remainingTableCards, a vector of Card pointers that represents the Cards remaining
                addToPartial, a Card to be added to the newPotentialBuild vector
                newPotentialBuild, a vector of Cards that represents a new potential build
Algorithm:
                1) Calculate the current potential build's numerical value
                2) Check if the potential build is equal to the targeted value
                    a) If the two values are equal check if there are more cards than the current largest build
                    b) Replace the current largest build with the potential build if it is larger
                3) Check if the current potential build value is greater
                    a) Return if it is larger
                4) Loop through the tableCards
                    a) Create a new potential build with a card that is not part of potential build
                    b) Call the function again

Assistance Received: Stanford's Lecture 10 Programming Abstractions Video by Julie Zelenski
***********************************************************************/
   public void calculateBuilds(Vector<Card>table, Vector<Card> potentialBuild, Card chosenCard, Card playerCardInHand,
                               Build largestBuild){
      // potentialBuildValue is initialized to the chosen card and holds the potential build's numerical value
      int potentialBuildValue = chosenCard.getNumValue();
      // playedCardValue contains the player's card in hand and is the target build's numerical value
      int playedCardValue = playerCardInHand.getNumValue();

      // Loop through the potentialBuild Vector
      for (Card card: potentialBuild){
         potentialBuildValue += card.getNumValue();
      }
      // If the potentialBuild has a value equal to the played Card, a Build has been found
      if (potentialBuildValue == playedCardValue){
         // Check if the potentialBuild's size is greater than the current largestBuild
         if (potentialBuild.size() > largestBuild.getCardParts().size()){
            Vector<Card> cardParts = largestBuild.getCardParts();
            for (Card card: potentialBuild){
               cardParts.add(card);
            }

            // Add the chosenCard into the newPotentialBuild vector
            cardParts.add(chosenCard);
            // largestBuild Vector is equal to the potentialBuild Vector
            //largestBuild.setCardParts(newPotentialBuild);
         }
         // Return because the next addition will be over the targeted buildValue
         return;
      }

      // Check if the potentialBuildValue is greater than the playedCardValue
      if (potentialBuildValue > playedCardValue){
         // Return because adding more cards will always be greater than the played card
         return;
      }

      // Loop through the table cards
      for (int i = 0; i < table.size(); i++){
         // remainingTableCards is a vector containing all of the remaining cards
         Vector<Card> remainingTableCards = new Vector<Card>();
         // addToPartial is the Card at the current index
         Card addToPartial = table.get(i);

         // Add all of the remaining loose cards into remainingTableCards Vector
         for (int j  = i + 1; j < table.size(); j++){
            remainingTableCards.add(table.get(j));
         }

         // newPotentialBuild holds potentialBuild's cards
         Vector<Card> newPotentialBuild = new Vector<Card>();

         for (Card card: potentialBuild){
            newPotentialBuild.add(card);
         }

         // Add the addToPartial Card into the new PotentialBuild Vector
         newPotentialBuild.add(addToPartial);

         // Call recursive function with the newPotentialBuild and remainingTableCards vectors
         calculateBuilds(remainingTableCards, newPotentialBuild, chosenCard, playerCardInHand, largestBuild);
      }
   }

/* **********************************************************************
Function Name: checkCanCapture
Purpose: To check if the computer can capture
Parameters:
         chosenCard, a Card passed by value
         table, a vector of Cards passed by value
            that contains the table cards
Return Value: A boolean value that represents if the computer can capture
Local Variables:
         None
Algorithm:
         1) Go through the player's hand
            a) Find a card in the table equal to the numerical value of the chosen card
         2) Return a boolean value indicating if the computer can capture
Assistance Received: None
***********************************************************************/
   public Boolean checkCanCapture(Card playerCard, Vector<Card> table){
      // Loop through the table Vector
      for (Card option: table){
         // Check if the option's value is equal to the playerCard's value
         if (option.getNumValue() == playerCard.getNumValue() || option.getCardStrVal().equals(playerCard.getCardStrVal())){
            // Return true if the two operands are equal
            return true;
         }

         if (isThereASet(playerCard, table)){
            return true;
         }
      }

      // Return false indicating that there are zero captures available
      return false;
   }

/* **********************************************************************
Function Name: captureSet
Purpose: To capture a set
Parameters:
            cardChosen, a card passed by value that represents the computer's chosen card
            tableCards, a vector of Card passed by value. It holds
                cards from the table.
            usedCards, a vector of Card passed by value. It holds
                cards used by the computer.

Return Value: A boolean value indicating if a capture occurred
Local Variables:
            firstCard, a card that represents the card from the first iterator
            secondCard, a card that represents the card from the second iterator
Algorithm:
            1) Loop through the table cards
                b) Loop through the table cards again
                c) Find a pair of cards equal to the chosen card's numerical value
                d) Capture the set
            3) Return if a set exists
Assistance Received: None
***********************************************************************/
   public void captureSet(Card firstCard, Card secondCard, Vector<Card> table){
      Vector<Card> set = new Vector<Card>();

      // Add cards into the set Vector
      set.add(firstCard);
      set.add(secondCard);

      // Add both cards into the player's pile
      playerPile.add(firstCard);
      playerPile.add(secondCard);

      // Remove cards from table
      removeCardsFromTable(set, table);

   }

   /* *********************************************************************
Function Name: checkBuildValueInHand
Purpose: check if the player holds a value equal to the intended build value
Parameters:
         playerHand, a vector of Cards representing the player's hand
         buildVal, an integer containing the build's value
Return Value: a boolean value indicating if the player holds a card equal to the buildValue
Local Variables:
         cardValueInHand, a Boolean variable indicating if the player holds a card equal to
            the build's value
Algorithm:
         1) Loop through the player's hand
         2) Check if the player holds a card equal to the buildValue
         3) return a boolean value indicating if the build is valid
Assistance Received: none
********************************************************************* */
   public Boolean checkBuildValueInHand(Vector<Card> playerHand, int buildValue){
      // cardValueInHand holds a boolean value indicating that the player does or does not have a card value in hand
      Boolean cardValueInHand = false;
      // Loop through the player's hand
      for (Iterator<Card> it = playerHand.iterator(); it.hasNext();) {
         Card currentCard = it.next();
         // Check if the player has an ace and that the build value is equal to 14
         if (currentCard.getCardName().equals('A') && buildValue == 14) {
            // cardValueInHand is equal to true
            cardValueInHand = true;
            // Set that card's value to 14
            currentCard.setNumValue(14);
         }
         // Check if the card's value is equal to the buildValue
         if (buildValue == currentCard.getNumValue()) {
            // cardValueInHand is equal to true indicating that the player owns a card of equal value
            cardValueInHand = true;
         }
      }
      return cardValueInHand;
   }

   /* **********************************************************************
   Function Name: isThereASet
   Purpose: To check if a set exists
   Parameters:
               chosenCard, a Card that points to a card chosen by the user
                   passed by value.
               tableCards, a vector of Card pointers that point to cards on the table
                   passed by value.
   Return Value: A boolean value indicating if a set exists
   Local Variables:
   Algorithm:
               1) Loop through the table
                   a) Loop through the table again
                   b) Add two cards together
                   c) Check if a set exists
   Assistance Received: None
   ***********************************************************************/
   public Boolean isThereASet(Card chosenCard, Vector<Card> table){
      for (Iterator<Card> iter = table.iterator(); iter.hasNext();){
         Card firstTableOption = iter.next();

         if (firstTableOption.getNumValue() < chosenCard.getNumValue()){
            // Loop through the table options
            for (Card secondTableOption: table){
               // Check if a set exists
               if (checkSetConditions(firstTableOption, secondTableOption, chosenCard)){
                  // setExists is equal to true because a set exists
                  return true;
               }
            }
         }
      }
      return false;
   }

/* **********************************************************************
Function Name: checkSetConditions
Purpose: To check if a set fits the conditions
Parameters:
                firstCard, a Card passed by value that points to the first Card
                    of a set
                secondCard, a Card passed by value that points to the second Card
                    of a set
                chosenCard, a Card passed by value that points to the player's
                    chosen card
Return Value: A boolean value that indicates if the set fits the conditions
Local Variables:
            validSet, a boolean variable that indicates if the set was valid
Algorithm:
            1) Check if the first card was part of a build
            2) Check if the second card was part of a build
            3) Add the two cards together
            4) Check if the chosen card's value was equal to the set
Assistance Received: None
***********************************************************************/
   public Boolean checkSetConditions(Card firstTableOption, Card secondTableOption, Card chosenCard){
      Boolean validSet = false;

      // firstCardIsPartOfBuild holds a boolean value if firstCard is a build
      Boolean firstCardIsPartOfBuild = isPartOfBuild(firstTableOption);
      if (firstCardIsPartOfBuild) return validSet;

      // secondCardIsPartOfBuild holds a boolean value if secondCard is a build
      Boolean secondCardIsPartOfBuild = isPartOfBuild(secondTableOption);
      if (secondCardIsPartOfBuild) return validSet;

      // cardsAreEqual holds a boolean is the two cards are equal
      Boolean cardsAreEqual = (firstTableOption.getCardName().equals(secondTableOption.getCardName()));
      if (cardsAreEqual) return validSet;

      // firstCardNumValue is initialized to the first card's numerical value
      int firstCardNumValue = firstTableOption.getNumValue();
      // secondCardNumValue is initialized to the second card's numerical value
      int secondCardNumValue = secondTableOption.getNumValue();
      // firstPlusSecond is initialized to the two card's numerical value added together
      int firstPlusSecond = firstCardNumValue + secondCardNumValue;

      // Check if the buildValues are equal
      if (firstPlusSecond == chosenCard.getNumValue()) {
         validSet = true;
      }

      // return the value of validSet
      return validSet;
   }

/* *********************************************************************
Function Name: isPartOfBuild
Purpose: Check if the card is a build
Parameters:
         card, a Card object
Return Value: a boolean value indicating if the card is a build
Local Variables:

Algorithm:
         1) Check if the card is a build
Assistance Received: none
********************************************************************* */
   public Boolean isPartOfBuild(Card card){
      if (card instanceof Build){
         return true;
      }
      return false;
   }

   /* *********************************************************************
Function Name: doesPlayerOwnBuild
Purpose: Check if the player owns a build
Parameters:
         builds, a hashMap containing pairs of an integer and Build
Return Value: a boolean value indicating if the player owns a build
Local Variables:
         None
Algorithm:
         1) Return a value indicating if the player owns a build
Assistance Received: none
********************************************************************* */
   public Boolean doesPlayerOwnBuild(HashMap<Integer, Build> builds){
      return builds.containsKey(id);
   }

   /* *********************************************************************
Function Name: addToPile
Purpose: Add the card to the player's pile
Parameters:
         card, a Card object
Return Value: None
Local Variables:
         None
Algorithm:
         1) Add the card to the player's pile
Assistance Received: none
********************************************************************* */
   public void addToPile(Card card){
      playerPile.add(card);
   }

   /* *********************************************************************
Function Name: getPlayerHandVec
Purpose: Get the player hand vector
Parameters:
         None
Return Value: return a vector of cards
Local Variables:
         None
Algorithm:
         1) Return the player's hand
Assistance Received: none
********************************************************************* */
   public Vector<Card> getPlayerHandVec(){
      return playerHand;
   }

   /* *********************************************************************
Function Name:getNumPileCards
Purpose: get the number of cards in the pile
Parameters:
         None
Return Value: an integer containing the number of cards in the pile
Local Variables:
         None
Algorithm:
         1) Return the size of the pile
Assistance Received: none
********************************************************************* */
   public int getNumPileCards(){
      return playerPile.size();
   }

   /* *********************************************************************
Function Name:getNumSpades
Purpose: get the number of spades cards in the pile
Parameters:
         None
Return Value: an integer containing the number of spades in the pile
Local Variables:
         numSpades, an integer containing the number of spades
Algorithm:
         1) Loop through the pile and check if the card is a spade
         2) Return the number of spades
Assistance Received: none
********************************************************************* */
   public int getNumSpades(){
      // numSpades is a counter that holds the number of spades found
      int numSpades = 0;

      // Loop through the player's pile
      for (Card card: playerPile) {
         // Check if the card's symbol is equal to S
         if (card.getCardSymbol().equals("S")) {
            // Increase the numSpades counter
            numSpades++;
         }
      }
      // return the number of spades a pile contains
      return numSpades;
   }

/* **********************************************************************
Function Name: getNumAces
Purpose: To get the number of aces in a pile
Parameters:
            None
Return Value: An integer containing the number of Aces in a pile
Local Variables:
            numAces, an integer containing the number of aces in a pile
Algorithm:
            1) Loop through the player's pile
            2) Find an Ace
            3) Increase the Ace counter if an Ace was found
            4) Return the counter for Aces
Assistance Received: None
***********************************************************************/
   public int getNumAces() {
      //numAces hold the number of Aces found
      int numAces = 0;

      for (Card card: playerPile) {
         //If card has they cardValue of Ace, increase numAces
         if (card.getCardStrVal().equals("A")) {
            numAces++;
         }
      }
      //return the number of Aces found in pile
      return numAces;
   }

/* **********************************************************************
Function Name: checkForCardName
Purpose: To check for a card's name in a pile
Parameters:
            cardName, a string passed by value that contains a card's name
Return Value: A boolean value indicating if the card was found
Local Variables:
            None
Algorithm:
            1) Loop through the player's pile
            2) Find the card
            3) Return a value depending on if the pile contains the card
Assistance Received: None
***********************************************************************/
   public Boolean checkForCardName(String cardName) {
      //Loop through pile
      for (Card card: playerPile) {
         //Checks if the Card has the cardName given
         if (card.getCardName().equals(cardName)) {
            //Card has been found in pile and return true
            return true;
         }
      }

      //Card has not been found
      return false;
   }

   /* *********************************************************************
Function Name: addToScore
Purpose: Add the points earned to score
Parameters:
         roundScore, an integer passed by value containing the points earned
Return Value: None
Local Variables:
         None
Algorithm:
         1) Add to the points to the player's score
Assistance Received: none
********************************************************************* */
   public void addToScore(int roundScore){
      playerScore += roundScore;
   }

   /* *********************************************************************
Function Name: getScore
Purpose: Get the player's score
Parameters:
         None
Return Value: An integer representing the player's score
Local Variables:
         None
Algorithm:
         1) Return the player's score
Assistance Received: none
********************************************************************* */
   public int getScore(){
      return playerScore;
   }

   /* *********************************************************************
Function Name: setScore
Purpose: Set the player's score
Parameters:
         score, an integer passed by value indicating the player's score
Return Value: None
Local Variables:
         None
Algorithm:
         1) Set the score
Assistance Received: none
********************************************************************* */
   public void setScore(int score){
      this.playerScore = score;
   }

   /* *********************************************************************
Function Name: getWinner
Purpose: Get the boolean value indicating if the player won
Parameters:
         None
Return Value: A boolean value indicating if the player won
Local Variables:
         None
Algorithm:
         1) Return the boolean value
Assistance Received: none
********************************************************************* */
   public Boolean getWinner(){
      return this.isWinner;
   }

   /* *********************************************************************
Function Name: setWinner
Purpose: set the player as a winner
Parameters:
         None
Return Value: None
Local Variables:
         None
Algorithm:
         1) Set the player as a winner
Assistance Received: none
********************************************************************* */
   public void setWinner(){
      this.isWinner = true;
   }

   /* *********************************************************************
Function Name: clearPile
Purpose: Clear the player's pile
Parameters:
         None
Return Value: None
Local Variables:
         None
Algorithm:
         1) Clear the Pile
Assistance Received: none
********************************************************************* */
   public void clearPile(){
      playerPile.clear();
   }
}
