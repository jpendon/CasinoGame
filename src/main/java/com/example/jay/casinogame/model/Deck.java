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
import java.util.Collections;
import java.util.Vector;

public class Deck implements Serializable {
   private Vector<Card> currentDeck = new Vector<Card>();

/* *********************************************************************
Function Name: Deck
Purpose: To create a Deck object
Parameters:
            None
Return Value: None
Local Variables:
            None
Algorithm:
            None
Assistance Received: none
********************************************************************* */
   public Deck(){
      this.currentDeck = createDeck();
   }

/* *********************************************************************
Function Name: Deck
Purpose: To create a Deck object
Parameters:
            cards, a vector of Cards passed by value that represents
                a deck
Return Value: None
Local Variables:
            None
Algorithm:
            1) Create a deck with given cards
Assistance Received: none
********************************************************************* */
   public Deck(Vector<Card> deck){
      this.currentDeck = new Vector<Card>();
      for (Card card: deck){
         currentDeck.add(card);
      }
   }

   /* *********************************************************************
Function Name: Deck
Purpose: To create a deep copy of Deck
Parameters:
         deck, a Deck object passed by value
Return Value: None
Local Variables:
         None
Algorithm:

Assistance Received: none
********************************************************************* */
   public Deck(Deck deck){
      for(Card card: deck.getDeckVec()){
         currentDeck.add(new Card(card));
      }
   }

   /* **********************************************************************
   Function Name: createDeck
   Purpose: To create a deck
   Parameters:
               None
   Return Value: None
   Local Variables:
               None
   Algorithm:
               1) Loop 14 times
                   a) Create a Spade, Club, Heart, and Diamond
                   b) Put the Card into the deck
   Assistance Received: None
   ***********************************************************************/
   private Vector<Card> createDeck(){
      // newDeck contains Card objects
      Vector<Card> newDeck = new Vector();

      // Create 13 Cards Of 4 Different suits
      for (int i = 1; i < 14; i++) {
         newDeck.add(new Card("S", i));
         newDeck.add(new Card("D", i));
         newDeck.add(new Card("H", i));
         newDeck.add(new Card("C", i));
      }
      // Shuffle the Deck
      Collections.shuffle(newDeck);

      return newDeck;
   }


   /* **********************************************************************
   Function Name: drawCard
   Purpose: To draw a card
   Parameters: None
   Return Value: A Card that represents a card in the deck
   Local Variables:
               drawnCard, a Card that points to the drawn card
   Algorithm:
               1) Get the first card at the top of the deck
               2) Remove the Card from the deck
   Assistance Received: None
   ***********************************************************************/
   public Card drawCard(){
      // drawnCard holds the topmost Card
      Card drawnCard = currentDeck.firstElement();
      // Remove the card from the deck
      currentDeck.remove(0);
      // Return the topmost Card
      return drawnCard;
   }

   /* *********************************************************************
Function Name: getAllCardNames
Purpose: Get a string representing all cards
Parameters:
         None
Return Value: a string containing all cards
Local Variables:
         strAllCards, a string representing all cards
Algorithm:
         1) Loop through the deck and add the name to the string
Assistance Received: none
********************************************************************* */
   public String getAllCardNames(){
      String strAllCards = "";

      for (Card card : currentDeck){
         strAllCards += card.getCardName();
         strAllCards += " ";
      }

      return strAllCards;
   }

   /* *********************************************************************
Function Name: getDeckVec
Purpose: Get the currentDeck vector
Parameters:
         1) None
Return Value: Vector of cards containing the deck
Local Variables:
         None
Algorithm:
         1) REturn the currentDeck
Assistance Received: none
********************************************************************* */
   public Vector<Card> getDeckVec(){
      return this.currentDeck;
   }

   /* *********************************************************************
Function Name: isDeckEmpty
Purpose: check if deck is empty
Parameters:
         None
Return Value: a boolean value indicating if the deck is empty
Local Variables:
         None
Algorithm:
         1) Check if the deck is empty and return the value
Assistance Received: none
********************************************************************* */
   public boolean isDeckEmpty(){
      return currentDeck.isEmpty();
   }

}
