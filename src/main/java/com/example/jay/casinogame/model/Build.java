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
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Build extends Card implements Serializable {
   private Vector<Card> cardParts = new Vector<Card>();
   private int ownerId;
   private Boolean isMultipleBuild = false;
   private Vector<Build> buildParts = new Vector<Build>();

/* *********************************************************************
Function Name: Build
Purpose: Construct a Build object
Parameters:
            None
Return Value: None
Local Variables:
            None
Algorithm:
Assistance Received: none
********************************************************************* */

   public Build(){
      super(null, -1);
   }

/* *********************************************************************
Function Name: Build
Purpose: Construct a Build object
Parameters:
            cardParts, a vector of cards containg card objects passed by value
            buildValue, an integer representing the build's value passed by value
            ownerId, an integer representing the build's owner passed by value
Return Value: None
Local Variables:
            None
Algorithm:
            1) Create the build
Assistance Received: none
********************************************************************* */
   public Build(Vector<Card> cardParts, int buildValue, int ownerId){
      super(null, buildValue);

      this.cardParts = cardParts;
      this.cardName = getBuildName(cardParts);
      this.ownerId = ownerId;
      this.isMultipleBuild = false;
      this.buildParts.add(this);
      this.strValue = "";
   }

/* *********************************************************************
Function Name: Build
Purpose: Construct a Build object
Parameters:
            name, a string passed by value containing the build's name
            id, an integer passed by value representing the player's id
Return Value: None
Local Variables:
            allCards, a vector of strings that contain the card names of the
                build
            newBuildName, a string holding the new build's name
            buildVal, an integer that represents the build's value
Algorithm:
            1) Indicate that the Build is not part of a multiple build
            2) Find the build's name
            3) Create the build
Assistance Received: none
********************************************************************* */
   public Build(String name, int id) {
      super(null, 0);
      this.isMultipleBuild = false;
      this.ownerId = id;
      this.cardName = name;
      this.cardParts = new Vector<Card>();
      this.strValue = "";

      // allCards is contains all the Card's build names and is initialized to getAllCardNames return value
      Vector<String> allCards = getAllCardNames();

      // newBuildName contains the new build's name
      String newBuildName = "[";
      // Loop through all the cards' name
      for (String cardName: allCards) {
         // Append the card's name to newBuildName
         newBuildName += cardName;
         // Add a space to the build name
         newBuildName += " ";

      }

      newBuildName = newBuildName.substring(0, newBuildName.length()-1);
      newBuildName += "]";
      // Set the build's name to the new build name
      setCardName(newBuildName);

      // Create Cards
      for (String cardName: allCards){
         Card card = new Card(Character.toString(cardName.charAt(0)), Character.toString(cardName.charAt(1)));
         this.cardParts.add(card);
      }

      // buildVal contains the build's value and is intialized to 0
      int buildVal = 0;

      // Loop through the cards
      for (Card card: cardParts) {
         // Add the current card's numerical value to build value
         buildVal += card.getNumValue();
      }

      // Set the numerical value to the current buildVal
      setNumValue(buildVal);

      this.buildParts.add(this);
   }

/* *********************************************************************
Function Name: Build
Purpose: Create a deep copy of a Build object
Parameters:
            build, a build object passed by value
Return Value: None
Local Variables:
            allCards, a vector of strings that contain the card names of the
                build
            newBuildName, a string holding the new build's name
            buildVal, an integer that represents the build's value
Algorithm:
            1) Indicate that the Build is part of a build
            2) Indicate that the Build is not part of a multiple build
            3) Find the build's name
            4) Create the build
Assistance Received: none
********************************************************************* */

   public Build (Build build){
      super(null,build.getOwnerId());

      this.cardParts = build.getCardParts();
      this.buildParts = build.getBuildParts();
      this.cardName = build.getCardName();
      this.ownerId = build.getOwnerId();
      this.isMultipleBuild = build.getIsMultipleBuild();
      this.numValue = build.getNumValue();
      this.strValue = "";
   }

/* *********************************************************************
Function Name: getAllCardNames
Purpose: To parse all card names
Parameters:
            None
Return Value: A vector of strings containing card names
Local Variables:
          name, a string containing a card's name
          allCards, a string vector used to contain all card names
          pattern, a pattern object
          matcher, a matcher obhject
Algorithm:
            1) Search for card names using the regular expression
            2) Push the card name into the allCards vector
            3) Increase the current match iterator
            4) Continue until there are no more matches
            5) Return the allCards
Assistance Received: none
********************************************************************* */
   private Vector<String> getAllCardNames(){
      // name contains the build's name
      String name = getCardName();
      // allCards contains the names of all the cards
      Vector<String> allCards = new Vector<String>();
      // Regular expression to find card names
      Pattern pattern = Pattern.compile("[A-Z].");
      Matcher matcher = pattern.matcher(this.cardName);

      while (matcher.find()) {
         String match = matcher.group(0);
         // Push the match into the allCards vector
         allCards.add(match);
      }
      // return the vector of card names
      return allCards;
   }

/* *********************************************************************
Function Name: getBuildName
Purpose: To get all parts of a multiple build
Parameters:
         cardParts, vector of Card objects passed by value
Return Value: A vector containing Build*
Local Variables:
          buildName, a string containing the build's name
Algorithm:
         1) Loop through all cards
            a) Add card's name to buildName
Assistance Received: none
********************************************************************* */
   public String getBuildName(Vector<Card>cardParts){
      String buildName = "[";

      for (Card card: cardParts){
         buildName += card.getCardName();
         buildName += " ";
      }

      buildName = buildName.substring(0, buildName.length() - 1);
      buildName += "]";

      return buildName;
   }

/* *********************************************************************
Function Name:  getCardParts
Purpose: Return vector of cards
Parameters:
            None
Return Value: Vector containing Card objects
Local Variables:
            None
Algorithm:
            1) Return vector
Assistance Received: none
********************************************************************* */

   public Vector<Card> getCardParts(){
      return cardParts;
   }

   /* *********************************************************************
Function Name:  setCardParts
Purpose: Set cardParts to the parameter
Parameters:
            cardParts, vector of Cards passed by valie
Return Value: None
Local Variables:
            None
Algorithm:
            1) Set the cardParts
Assistance Received: none
********************************************************************* */
   public void setCardParts(Vector<Card> cardParts){
      this.cardParts = cardParts;
   }

   /* *********************************************************************
Function Name:  addCard
Purpose: Add card to cardParts
Parameters:
            card, a Card object passed by value
Return Value: None
Local Variables:
            None
Algorithm:
            1) Add card to cardParts
Assistance Received: none
********************************************************************* */
   public void addCard(Card card){
      this.cardParts.add(card);
   }

   /* *********************************************************************
Function Name:  getOwnerId
Purpose: Get build's owner id
Parameters:
            None
Return Value: An integer containing the owner's id
Local Variables:
            None
Algorithm:
            1) Return owner's id
Assistance Received: none
********************************************************************* */
   public int getOwnerId(){
      return ownerId;
   }

   /* *********************************************************************
Function Name:  setBuildName
Purpose: To set the build's name
Parameters:
            buildName, a string passed by value containing the build's name
Return Value: None
Local Variables:
            None
Algorithm:
            1) Set the build's name
Assistance Received: none
********************************************************************* */
   public void setBuildName(String buildName){
      this.cardName = buildName;
   }

   /* *********************************************************************
Function Name:  getBuildParts
Purpose: Get the build's parts
Parameters:
            None
Return Value: Vector of Builds
Local Variables:
            None
Algorithm:
            Return the vector of Builds
Assistance Received: none
********************************************************************* */
   public Vector<Build> getBuildParts(){
      return buildParts;
   }



   /* *********************************************************************
Function Name:  addBuildPart
Purpose: To add a build to build parts
Parameters:
            build, a Build object passed by value
Return Value: None
Local Variables:
            None
Algorithm:
            1) Add the build to buildParts
Assistance Received: none
********************************************************************* */
   public void addBuildPart(Build build){
      this.buildParts.add(build);
   }

   /* *********************************************************************
Function Name:  getIsMultipleBuild
Purpose: Get the isMultipleBuild value
Parameters:
            None
Return Value: Boolean value indicating if the build is a multiple build
Local Variables:
            None
Algorithm:
            1) Return isMultipleBuild
Assistance Received: none
********************************************************************* */
   public Boolean getIsMultipleBuild(){
      return this.isMultipleBuild;
   }

   /* *********************************************************************
Function Name:  setMultipleBuild
Purpose: Set the isMultipleBuild's value
Parameters:
            value, a Boolean value indicating if the build is a multiple Build passed by value
Return Value: None
Local Variables:
            None
Algorithm:
            1) Set isMultipleBuild's value
Assistance Received: none
********************************************************************* */
   public void setMultipleBuild(Boolean value){
      this.isMultipleBuild = value;
   }

   /* *********************************************************************
Function Name: setOwnerId
Purpose: Set build's owner
Parameters:
            ownerId, an integer containing the owner's id
Return Value:None
Local Variables:
            None
Algorithm:
            1) Set the ownerId
Assistance Received: none
********************************************************************* */
   public void setOwnerId(int ownerId){
      this.ownerId = ownerId;
   }
}
