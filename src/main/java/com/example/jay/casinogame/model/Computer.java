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

public class Computer extends Player implements Serializable {
   private int computerAction;
   private Boolean isHelping = false;

/* *********************************************************************
Function Name: Computer
Purpose: To create a Computer object
Parameters:
            None
Return Value: None
Local Variables:
            None
Algorithm:
            None
Assistance Received: none
********************************************************************* */
   public Computer(int id){
      super(id);
   }

/* *********************************************************************
Function Name: Computer
Purpose: To create a Computer object
Parameters:
            playerHand, a vector containing Cards that represents
                a player's hand
            playerId, an integer passed by value containing the player's id
Return Value: None
Local Variables:
            None
Algorithm:
            1) Set the Computer's values to the correct values
Assistance Received: none
********************************************************************* */
   public Computer(Vector<Card> playerHand, int playerId){
      super(playerId);

      this.playerHand = playerHand;
      this.isHelping = true;
   }

   /* *********************************************************************
Function Name: Computer
Purpose: Create a Deep copy of a Computer object
Parameters:
            player, Computer Object passed by value
Return Value: None
Local Variables:
            None
Algorithm:
            1) Copy the player's hand
Assistance Received: none
********************************************************************* */
   public Computer(Computer player){
      super(player.getId());

      for (Card card: player.getPlayerHandVec()) {
         this.playerHand.add(new Card(card));
      }
   }

/* **********************************************************************
 Function Name: chooseCard
 Purpose: Choose a card and perform an action
 Parameters:
          tableCards, a vector of Card passed by value. It holds
          table cards and builds.
          builds, a list containing a pair of integer and a Build
          that is passed by value.
          playerLastCapturedId, an integer passed by value. It holds the Id
          of the player who last captured
 Return Value: None
 Local Variables:
          usedCards, a vector containing Card. The vector holds
          the cards used by the computer
          computerAction, an integer that represents the computer's action
          currentCard, a Card that represents a card being used
          canCapture, a boolean variable that is used to indicate if the computer can build
          canBuild, a boolean variable that indicates if the computer can build
          canCreateMultipleBuild, a boolean variable that indicates if the computer
          can create a multiple build
          canExtend, a boolean variable that is used to represent if the computer can extend
          a build
 Algorithm:
          1) Check if the computer can build
          a) Create a build
          2) Check if the computer can create a multiple build
          a) Create a multiple build
          3) Check if the computer can extend a build
          a) Create an extended build
          4) Check if the computer has not built
          a) Loop through the computer's hand
          b) Check if computer's current card matches a numerical value
          c) Capture the card
          5) Check if the computer has not build or captured
          a) Find the lowest valued card
          b) Trail the lowest valued card

 Assistance Received: None
 ********************************************************************* */

   public String chooseCard(Vector<Card> table, HashMap<Integer, Build> builds){
      // usedCards contains all the cards used by the computer
      Vector<Card> usedCards  = new Vector<Card>();
      // currentCard contains the Card object used by the computer
      Card currentCard = new Card();
      // Boolean variables to that represents a possible Action
      Boolean canCapture = false, canBuild = false, canCreateMultipleBuild = false, canExtend = false;

      // Check if the computer can build a card
      canBuild = checkIfCanBuild(table, builds);

      if (canBuild){
         // Set the computerAction to 1 to indicate that the computer is creating a build
         computerAction = 1;

         // create a build
         createBuildOption(usedCards, table, currentCard, builds);
      }

      // Check if the computer can extend a build
      canExtend = checkExtendBuildConditions(builds);

      // Check if canExtend is true and canBuild is false
      if (canExtend && !canBuild){
         // Set the computerAction to 4 to indicate that the computer is extending a build
         computerAction = 4;
         // create an Extended Build
         extendBuildOption(builds, table, usedCards, currentCard);

         canBuild = true;
      }

      canCreateMultipleBuild = checkIfCanBuildMultiple(table, builds);

      if (canCreateMultipleBuild && !canBuild){
         // Set the computerAction to 5 to indicate the computer created a Multiple build
         computerAction = 5;

         // Set canBuild to true
         canBuild = true;

         // Create a multiple Build
         createMultipleBuildOption(builds, table, usedCards, currentCard);
      }

      // Check if the computer has build
      if (!canBuild){
         // Loop through the computer's hand
         for (Iterator<Card> iter = playerHand.iterator(); iter.hasNext();){
            Card playerCard = iter.next();

            // Check if the computer can capture
            canCapture = checkCanCapture(playerCard, table);

            if (canCapture){
               // computerAction is set to 2 indicating that a capture occured
               computerAction = 2;
               // currentCard is equal to the iterator's current card
               currentCard = playerCard;
               // Capture the table Cards
               captureOption(currentCard, table, builds, usedCards);

               // Capture set cards
               while(isThereASet(currentCard, table)){
                  // Capture Sets
                  captureSetOption(currentCard, table, usedCards);
               }
               break;
            }
         }
      }

      // Trail
      if (!canCapture && !canBuild){
         // computerAction is equal to 3 indicating a trail will occur
         computerAction = 3;
         // currentCard is equal to the lowest numerical valued card
         currentCard = findLowestValCard();
         // Trail the lowest card
         table.add(currentCard);
         // Remove the card from the computer's hand
         playerHand.remove(currentCard);
      }

      return getComputerReasoning(this.isHelping, computerAction, currentCard, usedCards);
   }

/* **********************************************************************
 Function Name: checkIfCanBuild
 Purpose: To check if the computer can build
 Parameters:
          playerHand, a vector of Card passed by value. It
          represents the computer's hand
          tableOptions, a vector of Card passed by value. It holds table cards and builds
          builds, a list containing a pair of integer and a Build
          that is passed by value.
 Return Value: A boolean value that indicates if a build can occur
 Local Variables:
          canBuild, boolean value that indicates that a build can occur
          cardsUsedInBuild, an integer that counts the amount of cards used in a build
          currentCard, card contains a card
 Algorithm:
          1) Check if the computer owns a build
          a) If the computer does own a build, return false
          2) Find the largest Build
          3) Check if a build is possible
          4) Return true or false depending on if a build is possible
 Assistance Received: None
 ***********************************************************************/
   private Boolean checkIfCanBuild(Vector<Card>table, HashMap<Integer, Build> builds){
      Boolean canBuild = false;

      Card currentCard = null;

      // Check if the Computer owns a build
      if (builds.containsKey(getId())){
         return canBuild;
      }


      // potentialBuild is a vector of Cards intialized to the return value of determineBuilds
      Vector<Card> potentialBuild = determineBuilds(table);

      // Check if the potentialBuild is empty
      if (!potentialBuild.isEmpty()){
         // canBuild is equal to true indicating that a build can be created
         canBuild = true;
      }
      return canBuild;
   }


/* **********************************************************************
Function Name: determineBuilds
Purpose: To find the largest build
Parameters:
            playerHand, a vector of Card passed by value. It
                represents the computer's hand
            tableOptions, a vector of Card passed by value. It holds table cards and builds
            currentCard, a Card passed by value. It points to the card chosen by the computer
Return Value: A vector of Cards that hold the largest build possible to create
Local Variables:
            potentialBuild, a vector of Card that represents a potential build
            looseCards, a vector of Card that represents loose cards
            largestBuild, a vector of Card that the largest possible build
            bestBuildOption, a vector of Card that represents the best Build to create
Algorithm:
            1) Go through the computer's hand
                a) Find all the loose cards lower than the current card in hand
                b) Calculate the largest possible build with the given card in hand
                c) Check if the largest possible build is greater than the current best build
            2) Return the best possible build

Assistance Received: None
***********************************************************************/
   private Vector<Card> determineBuilds(Vector<Card> table){
      Vector<Card> potentialBuild = new Vector<Card>();
      Vector<Card> looseCards = new Vector<Card>();
      Vector<Card> bestBuildOption = new Vector<Card>();

      // Loop through the player's hand
      for (Iterator<Card> iter = playerHand.iterator(); iter.hasNext();){
         Card chosenCard = iter.next();
         looseCards = getLooseCards(table);
         // Loop through the cards in the player's hand
         for (Card playerCard: playerHand){
            // Get all of the loose cards from the table that are less than the current card's numerical value

            if (!(chosenCard.getCardName().equals(playerCard.getCardName()))) {
               //Create a temporary build
               Build largestBuild = new Build(new Vector<Card>(), -1, getId());

               // Calculate the largest possible build
               calculateBuilds(looseCards, potentialBuild, chosenCard, playerCard, largestBuild);
               // Check if the amount of cards in the bestBuildOption is less than the largestBuild from calculateBuilds
               if (bestBuildOption.size() < largestBuild.getCardParts().size()) {
                  // bestBuildOption is equal to the largestBuild
                  bestBuildOption = largestBuild.getCardParts();
               }
            }
         }
      }
      // Return the Vector containing the Best Possible Build
      return bestBuildOption;
   }

   /* **********************************************************************
   Function Name: createBuildOption
   Purpose: To create a build
   Parameters:
            usedCards, a vector of Card passed by value. It holds
                  cards used by the computer.
            tableCards, a vector of Card passed by value. It holds
                  cards from the table.
            currentCard, a Card that is passed by value. It holds
                  the card used by the computer.
            builds, a list containing a pair of integer and a Build
                  that is passed by value.
   Return Value: None
   Local Variables:
            buildCards, a vector of Card that holds cards in a build
   Algorithm:
            1) Find the build with the most cards
            2) Remove all of the cards from the table
            3) Create a new build that contains all of the cards
            4) Add the card into the table
            5) Add the card into usedCards

   Assistance Received: None
   ********************************************************************* */
   private void createBuildOption(Vector<Card> usedCards, Vector<Card> table, Card currentCard,
                                 HashMap<Integer, Build> builds){
      // buildCards is initialized to the largest build returned by the determineBuilds function
      Vector<Card> buildCards = determineBuilds(table);

      currentCard = new Card(buildCards.lastElement());

      // usedCards is equal to the buildCards vector
      for (Card card: buildCards){
         usedCards.add(new Card(card));
      }

      // buildValue contains the build's numerical value
      int buildValue = 0;

      // Loop through the usedCards vector
      for (Card usedCard: usedCards){
         buildValue += usedCard.getNumValue();
      }
      // Create the newBuild
      Build newBuild = new Build(usedCards, buildValue, getId());
      // Add the new Build to the table Vector
      table.add(newBuild);
      // Add the new Build from the HashMap
      builds.put(getId(), newBuild);
      // Remove the chosen Card from the computer's hand
      removeCardFromHand(currentCard);
      // Remove the card(s) for the build from the table
      removeCardsFromTable(usedCards, table);
   }

/* **********************************************************************
Function Name: extendBuildOption
Purpose: To create a new extended build
Parameters:
         builds, a list containing a pair of integer and a Build 
            that is passed by value.
         tableCards, a vector of Card passed by value. It holds
            cards from the table.
         usedCards, a vector of Card passed by value. It holds
            cards used by the computer.
         currentCard, a Card that is passed by value. It holds
            the card used by the computer.
Return Value: None
Local Variables:
         oldCards, a vector of Card that hold cards the computer used
         newBuild, a Build contains a new build
         oldBuild, a Build contains the old build
         newBuildName, a string that contains the new build's name
         opponentBuild, a list iterator that represents the first element of the list
Algorithm:
         1) Find the first and only build in the list
         2) Remove the build from the list
         3) Remove the build from the table
         4) Go through the player's hand and find a card to represent a target build value
            a) Find a card that can be added to the original build that equals the target build value
         5) Create the new build with the old build and a card in hand
         6) Add the build to the table and list
Assistance Received: None
***********************************************************************/
   private void extendBuildOption(HashMap<Integer, Build> builds, Vector<Card> table,Vector<Card> usedCards,
                                 Card currentCard){
      // newBuild and oldBuild contain Build objects according to their name
      Build oldBuild;
      // newBuildName contains the string for the new build's name
      String newBuildName = "";

      // entry contains the Key,Value of the only build in builds
      HashMap.Entry<Integer, Build> entry = builds.entrySet().iterator().next();
      // oldbuild is equal to the opponent's build
      oldBuild = entry.getValue();
      // Add the oldBuild to the usedCards vector
      usedCards.add(new Build(oldBuild));

      // Loop through the player's hand
      for (Iterator<Card> iter = playerHand.iterator(); iter.hasNext();){
         Card handCard = iter.next();

         for (Card playerCard: playerHand){
            int buildValue = handCard.getNumValue() + oldBuild.getNumValue();

            if (buildValue == playerCard.getNumValue()){
               currentCard.copy(handCard);


               newBuildName += " ";
               newBuildName += currentCard.getCardName();

               usedCards.add(new Card(currentCard));
               usedCards.add(new Build(oldBuild));
               break;
            }
         }
      }

      usedCards.add(extendBuild(currentCard, oldBuild, table, builds));

   }

/* **********************************************************************
Function Name: createMultipleBuildOption
Purpose: To create a multiple build
Parameters:
         builds, a list containing a pair of integer and a Build
            that is passed by value.
         tableCards, a vector of Card passed by value. It holds
            cards from the table.
         usedCards, a vector of Card passed by value. It holds
            cards used by the computer.
         currentCard, a Card that is passed by value. It holds
            the card used by the computer.
Return Value: None
Local Variables:
         originalBuild, a Card that represents the original build
         looseCards, vector of Card that represents loose cards on the table
         possibleBuild, a vector of Card that represents a possible build
         finalBuild, a vector of Card that represents the final build
         currentCard, a Card that represents the card used by the computer
Algorithm:
         1) Find the Build created by the player
         2) Find all loose cards
         3) Go through the player's hand and find the largest build created with loose cards
         4) Put the builds together to create a multiple build
         5) Add the build to the table and list
Assistance Received: None
***********************************************************************/
   private void createMultipleBuildOption(HashMap<Integer, Build> builds, Vector<Card> table, Vector<Card> usedCards,
                                         Card currentCard){
      // originalBuild contains an object of the build on the table
      Card originalBuild = null;
      //
      Vector<Card> possibleBuild = new Vector<Card>();
      Vector<Card> looseCards = new Vector<Card>();
      Vector<Card> finalBuild = new Vector<Card>();

      // Loop through the HashMap for build
      for (HashMap.Entry<Integer, Build> pair: builds.entrySet()){
         // Check if the computer's id matches the key,value pair's key
         if (getId() == pair.getKey()){
            // originalBuild is equal to the computer's existing build
            originalBuild = pair.getValue();
            break;
         }
      }

      // Find all Loose Cards
      for (Card card: table){
         // Check if the card is not a build
            if (!(card instanceof Build)){
            // Add the card to the vector containing loose cards
            looseCards.add(card);
         }
      }

      // Loop through the player's hand
      for (Iterator<Card> iter = playerHand.iterator(); iter.hasNext();){
         Card playerCard = iter.next();

         //Create a temporary build
         Build largestBuild = new Build(new Vector<Card>(), -1, getId());

         // Calculate all of the builds
         calculateBuilds(looseCards, possibleBuild, playerCard, originalBuild, largestBuild);

         // Check if the player's hand is equal to the original build's value
         if (playerCard.getNumValue() == originalBuild.getNumValue()){
            // Add the Card into the largestBuild Vector
            largestBuild.addCard(playerCard);
         }

         // Check if the amount of cards of finalBuild is smaller than the largestBuild
         if (finalBuild.size() < largestBuild.getCardParts().size()){
            // finalBuild is equal to the largest Build
            finalBuild = largestBuild.getCardParts();

            // currentCard is equal to the current card in hand
            currentCard.copy(playerCard);
         }
      }

      createMultipleBuild(currentCard, finalBuild, table, builds);

      usedCards.add(builds.get(getId()));

   }

/* **********************************************************************
Function Name: captureSetOption
Purpose: To capture a set
Parameters:
            cardChosen, a card passed by value that represents the computer's chosen card
            tableCards, a vector of Card passed by value. It holds
                cards from the table.
            usedCards, a vector of Card passed by value. It holds
                cards used by the computer.

Return Value: A boolean value indicating if a capture occured
Local Variables:
            firstCard, a card that represents the card from the first iterator
            secondCard, a card that represents the card from the second iterator
            setExists, a
Algorithm:
            1) Loop through the table cards
                b) Loop through the table cards again
                c) Find a pair of cards equal to the chosen card's numerical value
                d) Capture the set
            3) Return if a set exists
Assistance Received: None
***********************************************************************/
   private Boolean captureSetOption(Card chosenCard, Vector<Card> table, Vector<Card> usedCards){
      // setExists represents if a set exists
      Boolean setExists = false;
      // Loop through the table cards
      for (Iterator<Card> iter = table.iterator(); iter.hasNext();){
         Card firstCard = iter.next();

         //Loop through the cards a second time
         for (Card secondCard: table){
            Vector<Card> set = new Vector<Card>();

            // setExists is equal to the return value of checkSetConditions
            setExists = checkSetConditions(firstCard, secondCard, chosenCard);
            // Check if the set exists
            if (setExists){
               // Add cards into the set Vector
               set.add(firstCard);
               set.add(secondCard);

               // Add both cards into usedCards vector
               usedCards.add(firstCard);
               usedCards.add(secondCard);

               // Add both cards into the player's pile
               playerPile.add(firstCard);
               playerPile.add(secondCard);

               // Remove cards from table
               removeCardsFromTable(set, table);

               // return true indicating a set has been made
               return true;
            }
         }
      }
      // Return false indicating a set has not been made
      return false;
   }

/* **********************************************************************
 Function Name: findLowestValCard
 Purpose: To find the lowest valued card
 Parameters:
         None
 Return Value: A Card that represents the lowest valued card
 Local Variables:
         lowestValCard, a card that represents the lowest valued card
 Algorithm:
        1) Go through the player's hand
        2) Find the lowest valued card
        3) Return the lowest valued card
 Assistance Received: None
 ***********************************************************************/
   private Card findLowestValCard(){
      // lowestValCard is initialized at the beginning of the player's hand
      Card lowestValCard = playerHand.get(0);

      // Loop through the computer's hand
      for (Card playerCard: playerHand){
         // Check if the current card in hand is less than the numerical value of the current lowestValCard
         if (playerCard.getNumValue() < lowestValCard.getNumValue()) {
            // lowestValCard is equal to the current card
            lowestValCard = playerCard;
         }
      }
      // Return the lowest valued card
      return lowestValCard;
   }

   public String offerHelp(Vector<Card> table, HashMap<Integer, Build> builds){
      return chooseCard(table, builds);
   }

/* *********************************************************************
Function Name: getComputerReasoning
Purpose: To get the computer's reasoning
Parameters:
            isHelping, a boolean value indicating if the computer is 
                helping the player passed by value
            computerAction, an integer passed by value representing the 
                computer's choice
            cardChosen, a Card passed by value indicating the chosen card
            cardsUsed, a vector of card passed by value containing
                the cards used for the action
Return Value: a String containing the computer's reasoning
Local Variables:
            computerReasoning, a string containing the computer's reasoning
Algorithm:
            1) Create the computer's reasoning based on the action
Assistance Received: none
********************************************************************* */
   private String getComputerReasoning(Boolean isHelping, int computerAction, Card cardChosen,
                                      Vector<Card>cardsUsed){
      String computerReasoning = ""; 
      
      switch (computerAction) {
         case 1:
            computerReasoning += "\nThe Computer ";

            if (isHelping) {
               computerReasoning +=  "would advise you to ";
            }
            else {
               computerReasoning +=  "choose to ";
            }
            computerReasoning +=  "play and build using ";
            computerReasoning +=  cardsUsed.lastElement().getCardName();
            computerReasoning +=  ".\nThe Build was created using the cards ";

            for (Card card: cardsUsed) {
               computerReasoning +=  card.getCardName(); 
               computerReasoning += " ";
            }

            computerReasoning += ".\n";
            computerReasoning += "\nThis build contains the maximum amount of cards that could be built.\n";
            break;
         case 2:
            computerReasoning += "\nThe Computer ";
            if (isHelping) {
               computerReasoning += "would advise you to ";
            }
            else {
               computerReasoning += "choose to ";
            }
            computerReasoning += "play and capture using ";
            computerReasoning += cardChosen.getCardName();
            computerReasoning += " because it wanted to capture ";
            for (Card card: cardsUsed) {
               computerReasoning += card.getCardName();
               computerReasoning += " ";
            }
            computerReasoning += "\n";
            break;
         case 3:
            if (isHelping) {
               computerReasoning += "\nThe Computer would advise you to trail the lowest Card which is ";
               computerReasoning += cardChosen.getCardName();
               computerReasoning += " because it could not build or capture any cards.\n";
            }
            else {
               computerReasoning += "\nThe Computer could not build or capture any cards. ";
               computerReasoning += "\nTherefore, the computer choose to trail the lowest Card which was ";
               computerReasoning += cardChosen.getCardName();
               computerReasoning += ".\n";
            }
            break;
         case 4:
            if (isHelping) {
               computerReasoning += "\nThe Computer would advise you to extend its opponent's build with ";
               computerReasoning += cardChosen.getCardName();
               computerReasoning += "\nThis action would create the build ";
               computerReasoning += cardsUsed.lastElement().getCardName();
               computerReasoning += ".\n" ;
            }
            else {
               computerReasoning += "\nThe Computer decided to extend its opponent's build with ";
               computerReasoning += cardChosen.getCardName();
               computerReasoning += ".\nIt created the build ";
               computerReasoning += cardsUsed.lastElement().getCardName();
               computerReasoning += ".\n";
            }
            break;
         case 5:
            if (isHelping) {
               computerReasoning += "\nThe Computer would advise you to create a multiple build ";
               computerReasoning += cardChosen.getCardName();
               computerReasoning += ".\nThis will create the multiple build ";
               computerReasoning += cardsUsed.lastElement().getCardName();
               computerReasoning += ".";
            }
            else {
               computerReasoning += "\nThe Computer decided to create a multiple build using ";
               computerReasoning += cardChosen.getCardName();
               computerReasoning += ".\nThe Computer built ";
               computerReasoning += cardsUsed.lastElement().getCardName();
               computerReasoning += ".\n";
            }
            break;
         default:
            break;
      }

      return computerReasoning;
   }

}
