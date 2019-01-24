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

public class Human extends Player implements Serializable {

/* *********************************************************************
Function Name: Human
Purpose: To create a Human object
Parameters:
            None
Return Value: None
Local Variables:
            None
Algorithm:
            None
Assistance Received: none
********************************************************************* */
   public Human(){super(null); }

/* *********************************************************************
Function Name: Human
Purpose: To create a Human object
Parameters:
            id, an integer containing the player's id
Return Value: None
Local Variables:
            None
Algorithm:
            None
Assistance Received: none
********************************************************************* */
   public Human(int id){
      super(id);
   }

/* *********************************************************************
Function Name: Human
Purpose: To create a deep copy of a  Human object
Parameters:
            player, a Human object passed by value
Return Value: None
Local Variables:
            None
Algorithm:
            1) Copy the player's hand
Assistance Received: none
********************************************************************* */
   public Human(Human player){
      super(player.getId());

      for (Card card: player.getPlayerHandVec()) {
         this.playerHand.add(new Card(card));
      }
   }

}
