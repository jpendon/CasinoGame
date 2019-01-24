/*
 **************************************************************
 * Name:  Jay Pendon						                     *
 * Project: Casino Game								         *
 * Class: CMPS 366 01 - Organization of Programming Languages *
 * Date:  Decemeber 11, 2018		                             *
 **************************************************************
 */

package com.example.jay.casinogame.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jay.casinogame.R;
import com.example.jay.casinogame.model.Build;
import com.example.jay.casinogame.model.Card;
import com.example.jay.casinogame.model.Computer;
import com.example.jay.casinogame.model.Human;
import com.example.jay.casinogame.model.Player;
import com.example.jay.casinogame.model.Round;
import com.example.jay.casinogame.model.Serialization;

import java.util.Vector;

public class RoundActivity extends AppCompatActivity {
   private Round round;
   private String computerAction = "";

   /* *********************************************************************
   Function Name: onCreate
   Purpose: To initialize the Activity
   Parameters:
            savedInstanceState, Bundle object passed by value
   Return Value: None
   Local Variables:
            intent, an Intent variable containing the intent
            round, a Round variable containing the round
   Algorithm:
            1) Get the round, card, and player from the intent
            2) Update the layout
            3) Create the saveButton, hintButton, and debugButton
   Assistance Received: none
   ********************************************************************* */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_round);

      Intent intent = getIntent();

      //Get the round from intent
      round = (Round) intent.getSerializableExtra("round");

      if (round.getNextPlayer() == -1) {
         // flip is equal to the random number of 0 or 1
         final int flip = (int) (Math.random() * (2));

         AlertDialog.Builder flipCoinMessage = new AlertDialog.Builder(RoundActivity.this);
         flipCoinMessage.setMessage("Flip a Coin to see who goes first. Heads or Tails?")
               .setPositiveButton("Heads", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                     if (flip == 0) {
                        round.setNextPlayer(round.getHumanObj().getId());
                        return;
                     }
                     round.setNextPlayer(round.getComputerObj().getId());
                  }
               }).setNegativeButton("Tails", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               if (flip == 1) {
                  round.setNextPlayer(round.getHumanObj().getId());
                  return;
               }
               round.setNextPlayer(round.getComputerObj().getId());
            }
         }).setCancelable(false);

         AlertDialog.Builder flipCoinResultMessage = new AlertDialog.Builder(RoundActivity.this);
         if (flip == 0) {
            flipCoinResultMessage.setMessage("The Coin Landed on Heads!");
         } else {
            flipCoinResultMessage.setMessage("The Coin Landed on Tails!");
         }
         flipCoinResultMessage.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               playRound();
               updateLayout();
            }
         });

         flipCoinResultMessage.show();
         flipCoinMessage.show();
      }

      // Check if the round has resumed
      if (!round.getResumedGame()) {
         // Draw cards
         round.bothPlayersDrawCards();
         round.drawLooseCards();
      }

      updateLayout();

      // Create View Pile button
      Button viewPileButton = findViewById(R.id.viewPileButton);
      viewPileButton.setOnClickListener(new View.OnClickListener(){
         @Override
         public void onClick(View v){
            new AlertDialog.Builder(RoundActivity.this)
                  .setTitle("Pile: ")
                  .setMessage(getPlayerPile(round.getPlayers()))
                  .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {

                     }
                  })
                  .setCancelable(false)
                  .show();
         }
      });

      // Create the save button
      Button saveButton = findViewById(R.id.saveButton);
      saveButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            AlertDialog.Builder saveAlert = new AlertDialog.Builder(RoundActivity.this);
            saveAlert.setMessage("Do you want to save and quit the game?")
                  .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                        Intent roundOutput = new Intent();
                        roundOutput.putExtra("round", round);
                        setResult(RESULT_CANCELED, roundOutput);

                        finish();
                     }
                  }).setNegativeButton("No", null);

            saveAlert.show();
         }
      });

      // Create the hint button
      Button hintButton = findViewById(R.id.hintButton);
      hintButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            // Create a Copy of the round
            Round roundCopy = new Round(round);

            Human human = roundCopy.getHumanObj();
            Computer computerHelper = new Computer(human.getPlayerHandVec(), human.getId());

            String helpString = computerHelper.offerHelp(roundCopy.getTable(), roundCopy.getBuilds());

            Toast.makeText(RoundActivity.this, helpString, Toast.LENGTH_LONG).show();
         }
      });

      // Create the debug Button
      Button debugButton = findViewById(R.id.debugButton);
      debugButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            new AlertDialog.Builder(RoundActivity.this)
                  .setMessage(createDebugString())
                  .setPositiveButton("Ok", null)
                  .show();
         }
      });
   }

   /* *********************************************************************
Function Name: updateLayout
Purpose: To create and update the View
Parameters:
         None
Return Value: None
Local Variables:
         chosenCardLayout, a LinearLayout variable that is initialized to
            the chosenCard view in the xml file
         tableLayout, a GridLayout variable that is initialized to
            the tableCardLayout view in the xml file
         layoutParams, a GridLayout.LayoutParams variable that contains the
            parameters for the layout
         imageView, an ImageView variable that contains the imageView object
         drawable, a Drawable variable that is initialized to the card's name
         counter, an integer that contains the amount of cards created
Algorithm:
Assistance Received: none
********************************************************************* */
   public void updateLayout() {
      // Check if the round has ended
      if (round.hasRoundEnded()) {
         // Give the rest of the cards to the player who last Captured
         for (Player player : round.getPlayers()) {
            if (player.getId() == round.getPlayerLastCaptured().getId()) {
               round.addToPlayerPile(player);
            }
         }

         // Create an AlertDialogBuilder to hold the post round text
         AlertDialog.Builder message = new AlertDialog.Builder(this);
         StringBuilder roundScore = new StringBuilder();

         roundScore.append("Round Over\n\n");
         roundScore.append(round.computeRoundScore());

         message.setMessage(roundScore.toString()).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               Intent roundOutput = new Intent();
               roundOutput.putExtra("round", round);
               setResult(RESULT_OK, roundOutput);

               finish();
            }
         })
               .setOnCancelListener(new DialogInterface.OnCancelListener() {
                  @Override
                  public void onCancel(DialogInterface dialog) {
                     Intent roundOutput = new Intent();
                     roundOutput.putExtra("round", round);
                     setResult(RESULT_OK, roundOutput);

                     finish();
                  }
               });
         message.setCancelable(false);
         message.show();
         return;
      }

      checkIfHandsEmpty();

      Vector<Card> table = round.getTable();
      Vector<Card> humanHand = new Vector<Card>();
      Vector<Card> computerHand = new Vector<Card>();
      Vector<Player> players = new Vector<Player>();

      players.addAll(round.getPlayers());
      int roundNum = round.getRound();

      // Set the roundText to the current number of Rounds
      TextView roundText = findViewById(R.id.roundNumText);
      roundText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
      roundText.setText(Integer.toString(roundNum));

      // Set the scores for each of the text to the GUI
      TextView computerScore = findViewById(R.id.computerScoreLabel);
      TextView humanScore = findViewById(R.id.humanScoreLabel);

      // Set Score Text
      String humanText = "Score: ";
      String computerText = "Score: ";
      int humanScoreNum, computerScoreNum;

      if (players.elementAt(0) instanceof Human) {
         humanScoreNum = players.elementAt(0).getScore();
         computerScoreNum = players.elementAt(1).getScore();
      } else {
         humanScoreNum = players.elementAt(1).getScore();
         computerScoreNum = players.elementAt(0).getScore();
      }
      humanText += Integer.toString(humanScoreNum);
      computerText += Integer.toString(computerScoreNum);

      computerScore.setText(computerText);
      humanScore.setText(humanText);

      // Set Player's hands
      getPlayerHands(players, humanHand, computerHand);


      //GridLayout layout = (GridLayout) findViewById(R.id.layout);
      GridLayout tableLayout = findViewById(R.id.tableCardLayout);
      GridLayout humanHandLayout = findViewById(R.id.humanHandLayout);
      GridLayout computerHandLayout = findViewById(R.id.computerHandLayout);

      //layout.removeAllViews();
      tableLayout.removeAllViews();
      humanHandLayout.removeAllViews();
      computerHandLayout.removeAllViews();

      addCardsToLayout(table, tableLayout, false, true);
      addCardsToLayout(humanHand, humanHandLayout, true, false);
      addCardsToLayout(computerHand, computerHandLayout, false, false);
   }

   /* *********************************************************************
Function Name: getPlayerHands
Purpose: To get Player hands
Parameters:
         players, a Vector of Players passed by value containing players
         humanHand, a Vector of Cards passed by value representing the human's hand
         computerHand, a Vector of Cards passed by value representing the
            computer's hand
Return Value: None
Local Variables:
         None
Algorithm:
         1) Check if the first element is a Human object
            a) Copy all cards to the computer and human hands
Assistance Received: none
********************************************************************* */
   public void getPlayerHands(Vector<Player> players, Vector<Card> humanHand, Vector<Card> computerHand) {
      if (players.elementAt(0) instanceof Human) {
         humanHand.addAll(players.elementAt(0).getPlayerHandVec());
         computerHand.addAll(players.elementAt(1).getPlayerHandVec());
         return;
      }

      humanHand.addAll(players.elementAt(1).getPlayerHandVec());
      computerHand.addAll(players.elementAt(0).getPlayerHandVec());
   }

   /* *********************************************************************
Function Name: getPlayerPile
Purpose: To get the player's pile
Parameters:
         players, a vector of Players containing Player objects
Return Value: A string containing the player's hand
Local Variables:
         humanPileString, a string containing the player's pile
Algorithm:
         1) Find the player
         2) Create the string
Assistance Received: none
********************************************************************* */
   public String getPlayerPile(Vector<Player> players) {
      // Set players' pile text
      String humanPileString = "";

      if (players.elementAt(0) instanceof Human) {
         humanPileString += players.elementAt(0).getPlayerPile();
      } else {
         humanPileString += players.elementAt(1).getPlayerPile();
      }
      return humanPileString;
   }

/* *********************************************************************
Function Name: addCardsToLayout
Purpose: To add cards to the layout
Parameters:
         cards, a Vector of cards containing Card objects passed by value
         layout, a Gridlayout variable containing the layout
         createButton, a Boolean value indicating if a button should be
            created
         tableCard, a Boolean value indicating if the card is a table card
Return Value: None
Local Variables:
         layoutParams, a GridLayout.LayoutParams variable containing the
            parameters for the card
         counter, an integer containing the amount of cards added to the
            layout
         imageView, an ImageView variable that contains the imageView object
         drawable, a Drawable variable that is initialized to the card's name
Algorithm:
         1) Loop through the cards vector
            a) Check if a button should be made and it is not a tableCard
            b) Perform an action based on the condition when creating a card for
               the layout
Assistance Received: none
********************************************************************* */
   public void addCardsToLayout(Vector<Card> cards, GridLayout layout, Boolean createButton,
                                Boolean tableCard) {
      GridLayout.LayoutParams layoutParams;
      int counter = 0;
      for (final Card card : cards) {
         if (createButton && !tableCard) {
            ImageButton button = new ImageButton(this);
            button.setLayoutParams(new ViewGroup.LayoutParams(1, 2));

            button.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                  // Have ACE VALUE CHOSEN HERE
                  if (card.getCardStrVal().equals("A")) {
                     new AlertDialog.Builder(RoundActivity.this)
                           .setMessage("You have Chosen an Ace. Choose the value the card represents.")
                           .setPositiveButton("1", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                 card.setNumValue(1);
                                 startChooseActionActivity(card);
                              }
                           })
                           .setNegativeButton("14", new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                 card.setNumValue(14);
                                 startChooseActionActivity(card);

                              }
                           }).show();
                     return;
                  }
                  startChooseActionActivity(card);

               }
            });
            layoutParams = new GridLayout.LayoutParams(GridLayout.spec(0, 1), GridLayout.spec(counter));

            layoutParams.setMargins(2, 0, 2, 0);

            Drawable drawable = this.getResources().getDrawable(this.getResources().getIdentifier(
                  card.getCardName().toLowerCase(), "drawable", getPackageName()));
            button.setBackgroundDrawable(drawable);
            layoutParams.width = 100;
            layoutParams.height = 150;

            layout.addView(button, layoutParams);
         } else if (card instanceof Build) {
            TextView build = new TextView(this);
            String text = "\t" + card.getCardName();
            build.setText(text);
            if (((Build) card).getOwnerId() == 1) {
               build.setTextColor(Color.rgb(220, 20, 60));
            } else {
               build.setTextColor(Color.rgb(0, 191, 255));
            }

            layout.addView(build);
         } else {
            ImageView imageView = new ImageView(this);

            imageView.setLayoutParams(new ViewGroup.LayoutParams(1, 2));


            layoutParams = new GridLayout.LayoutParams(GridLayout.spec(0, 1), GridLayout.spec(counter));

            layoutParams.setMargins(2, 0, 2, 0);

            Drawable drawable;
            if (tableCard) {
               drawable = this.getResources().getDrawable(this.getResources().getIdentifier(
                     card.getCardName().toLowerCase(), "drawable", getPackageName()));
            } else {
               drawable = this.getResources().getDrawable(this.getResources().getIdentifier(
                     "back", "drawable", getPackageName()));
            }
            imageView.setBackgroundDrawable(drawable);
            layoutParams.width = 75;
            layoutParams.height = 125;

            layout.addView(imageView, layoutParams);
         }

         counter++;
      }
   }

/* *********************************************************************
Function Name: playRound
Purpose: To play the round
Parameters:
         None
Return Value: None
Local Variables:
         computerActionText, a string containing the computer's action
Algorithm:
         1) Check if it is the computer's turn
            a) Play the computer's turn
            b) Set the Human as the next player
            c) Create an AlertDialog.Builder that contains the computer's action
         2) Update the layout
Assistance Received: none
********************************************************************* */
   public void playRound() {
      // Check if the computer is the next player
      if (round.getComputerObj().getId() == round.getNextPlayer()) {
         computerAction = round.getComputerObj().chooseCard(round.getTable(), round.getBuilds());

         // Check if the computer captured
         if (computerAction.indexOf("because it wanted to capture") > 0) {
            round.setPlayerLastCaptured(round.getComputerObj());
         }

         // Set the next player as the human
         round.setNextPlayer(round.getHumanObj().getId());

         // computerActionText contains the computer's action
         String computerActionText = "Computer's Action: \n\n" + computerAction;

         // Create the AlertDialog to be displayed
         new AlertDialog.Builder(RoundActivity.this)
               .setMessage(computerActionText)
               .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                     updateLayout();
                  }
               })
               .setOnCancelListener(new DialogInterface.OnCancelListener() {
                  @Override
                  public void onCancel(DialogInterface dialog) {
                     updateLayout();
                  }
               })
               .setCancelable(false)
               .show();
         return;
      }
      // update the layout
      updateLayout();
   }

/* *********************************************************************
Function Name: checkIfHandsEmpty
Purpose: To check if the hands are empty
Parameters:
         None
Return Value: None
Local Variables:
         None
Algorithm:
         1) Check if the computer's hand is empty
         2) Check if the human's hand is empty
         3) If both hands are empty, draw cards
Assistance Received: none
********************************************************************* */
   public void checkIfHandsEmpty() {
      if (!(round.getComputerObj().getPlayerHandVec().isEmpty())) {
         return;
      }
      if (!(round.getHumanObj().getPlayerHandVec().isEmpty())) {
         return;
      }
      round.bothPlayersDrawCards();
   }


/* *********************************************************************
Function Name: createDebugString
Purpose: To create the debug string
Parameters:
         None
Return Value: A string containing the debug string
Local Variables:
         serialization, a variable containing serialization
         debusString, a string containing the debug string
Algorithm:
         1) Create a serialization object
         2) Call getRoundSaveString to get the debug string
         3) Add the computer's action
         4) Return the string
Assistance Received: none
********************************************************************* */
   public String createDebugString() {
      Serialization serialization = new Serialization();
      String debugString = serialization.getRoundSaveString(round);
      debugString += "\n\n\n";
      debugString += computerAction;
      return debugString;
   }

   /* *********************************************************************
Function Name: startChooseActionActivity
Purpose: To start the ChooseActionActivity
Parameters:
         card, a Card object containing the chosen card
Return Value: None
Local Variables:
         intent, an Intent variable containing the intent
Algorithm:
         1) Create an intent variable
         2) Add the chosen card to the intent
         3) Add the round to the intent
Assistance Received: none
********************************************************************* */
   public void startChooseActionActivity(Card card) {
      Intent intent = new Intent(this, ChooseActionActivity.class);
      intent.putExtra("chosenCard", card);
      intent.putExtra("round", round);
      startActivityForResult(intent, 1);
   }

   /* *********************************************************************
Function Name: onActivityResult
Purpose: To receive the result of the activity
Parameters:
         reqCode, an integer containing the requestCode,
         rescode, an integer containing the result code
         data, an intent containing the data from the activity
Return Value: None
Local Variables:
         None
Algorithm:
         1) Check if the resCode is equal to RESULT_CANCELED
         2) If not, then get the round from the data
         3) Set the next Player as the computer
         4) Call playRound
Assistance Received: none
********************************************************************* */
   @Override
   protected void onActivityResult(int reqCode, int resCode, Intent data) {
      if (resCode == RESULT_CANCELED) {
         return;
      }
      this.round = (Round) data.getSerializableExtra("round");
      updateLayout();

      if (!round.hasRoundEnded()) {
         round.setNextPlayer(round.getComputerObj().getId());
         //Computer turn
         playRound();
      }
   }

}
