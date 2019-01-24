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

public class Card implements Serializable {
    protected String cardName;
    protected int numValue;
    private String symbol;
    protected String strValue;

/* *********************************************************************
Function Name: Card
Purpose: To create a card object
Parameters:
            None
Return Value: None
Local Variables:
            None
Algorithm:
            1) Set the card values to default
Assistance Received: none
********************************************************************* */
    public Card(){
       this.cardName = "";
       this.numValue = -1;
       this.symbol = "";
       this.strValue = "";
    }

/* *********************************************************************
Function Name: Card
Purpose: To create a card object
Parameters:
            suit, a char passed by value representing the card's suit
            value, an integer passed by value holding the card's value
Return Value: None
Local Variables: None
Algorithm:
            1) Set the Card's values to the parameter's value
Assistance Received: none
********************************************************************* */
    public Card(String symbol, int numValue) {
        this.numValue = numValue;
        this.symbol = symbol;
        this.strValue = intToString(numValue);
        this.cardName = symbol + strValue;
    }

/* *********************************************************************
Function Name: Card
Purpose: To create a Card object
Parameters:
            symbol, a string that contains the card's suit passed by value
            value, a string passed by value that contains the card's value
Return Value: None
Local Variables:
            None
Algorithm:
            1) Set the Card's values to the parameter's value
Assistance Received: none
********************************************************************* */
    public Card(String symbol, String charValue) {
        this.numValue = stringToInt(charValue);
        this.symbol = symbol;
        this.strValue = intToString(numValue);
        this.cardName = symbol + strValue;
    }

/* *********************************************************************
Function Name: Card
Purpose: Create a Deep copy of a Card object
Parameters:
            card, a Card object passed by value
Return Value: None
Local Variables:
            None
Algorithm:
            1) Copy the card's contents
Assistance Received: none
********************************************************************* */
    public Card(Card card){
       this.numValue = card.getNumValue();
       this.symbol = card.getCardSymbol();
       this.strValue = card.getCardStrVal();
       this.cardName = card.getCardName();
    }

   /* *********************************************************************
Function Name:getNumValue
Purpose: Get numValue
Parameters:
            None
Return Value: an integer containing the numValue
Local Variables:
            None
Algorithm:
            1) Return the numValue
Assistance Received: none
********************************************************************* */
    public int getNumValue(){
        return numValue;
    }

   /* *********************************************************************
Function Name: getCardName
Purpose: get Card's name
Parameters:
            1) None
Return Value: A string containing the card's name
Local Variables:
            None
Algorithm:
            1) return cardName
Assistance Received: none
********************************************************************* */
    public String getCardName(){
        return cardName;
    }

/* *********************************************************************
Function Name: intToString
Purpose: To convert an integer value to a Char value
Parameters:
         value, an integer that contains the value of a card
Return Value: A character value that represents the numerical value of a card
Local Variables:
         None

Algorithm:
         1) Check if the value is between 1 and 10
            a) return the value parameter plus '0' char
         2) Switch receives the value
         3) Return a char depending on the integer value
Assistance Received: None
********************************************************************* */
    public String intToString(int value) {
        String stringVal = "";

        switch (value) {
            case 1:
                stringVal = "A";
                break;
            case 10:
                stringVal = "X";
                break;
            case 11:
                stringVal = "J";
                break;
            case 12:
                stringVal = "Q";
                break;
            case 13:
                stringVal = "K";
                break;
            default:
                stringVal = String.valueOf(value);
                break;
        }
        return stringVal;
    }

   /* *********************************************************************
   Function Name: convertCardValueToInt
   Purpose: To convert the char value to an integer
   Parameters:
            value, a string passed by value that represents a value of a card
   Return Value: An integer containing a numerical value
   Local Variables:
            numVal, an integer used to hold the integer value of a card
   Algorithm:
            1) Switch takes in Char value
            2) Return an integer indicated by the char's case
   Assistance Received: None
   ********************************************************************* */
    public int stringToInt(String value) {
        int numVal = 0;

        switch (value) {
            case "A":
                numVal = 1;
                break;
            case "X":
                numVal = 10;
                break;
            case "J":
                numVal = 11;
                break;
            case "Q":
                numVal = 12;
                break;
            case "K":
                numVal = 13;
                break;
            default:
                numVal = Integer.parseInt(value);
                break;
        }
        return numVal;
    }

   /* *********************************************************************
Function Name: copy
Purpose: Copy a card's contents
Parameters:
            card, a Card object passed by value
Return Value: None
Local Variables:
            None
Algorithm:
            1) Copy contents
Assistance Received: none
********************************************************************* */
    public void copy(Card card){
       this.numValue = card.getNumValue();
       this.symbol = card.getCardSymbol();
       this.strValue = card.getCardStrVal();
       this.cardName = card.getCardName();
    }

   /* *********************************************************************
Function Name: getCardSymbol
Purpose: Get card's symbol
Parameters:
            None
Return Value: A string containing the card's symbol
Local Variables:
            None
Algorithm:
            1) Return symbol
Assistance Received: none
********************************************************************* */
    public String getCardSymbol(){
        return symbol;
    }

   /* *********************************************************************
Function Name: setNumValue
Purpose: Return numValue
Parameters:
            None
Return Value: An integer containing numValue
Local Variables:
            None
Algorithm:
            1) Return numValue
Assistance Received: none
********************************************************************* */
    public void setNumValue(int value){
        this.numValue = value;
    }

   /* *********************************************************************
Function Name: setCardName
Purpose: Set card's name
Parameters:
            cardName, a string passed by value representing the card's name
Return Value: None
Local Variables:
            None
Algorithm:
            1) Set the cardName
Assistance Received: none
********************************************************************* */
    public void setCardName(String cardName){
        this.cardName = cardName;
    }

   /* *********************************************************************
Function Name: getCardStrVal
Purpose: Get card's strValue
Parameters:
            None
Return Value: a string containing the card's strValue
Local Variables:
            None
Algorithm:
            1) Return strValue
Assistance Received: none
********************************************************************* */
    public String getCardStrVal(){
       return this.strValue;
    }
}

