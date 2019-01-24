/*
 **************************************************************
 * Name:  Jay Pendon						                     *
 * Project: Casino Game								         *
 * Class: CMPS 366 01 - Organization of Programming Languages *
 * Date:  Decemeber 11, 2018		                             *
 **************************************************************
 */

package com.example.jay.casinogame.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jay.casinogame.R;
import com.example.jay.casinogame.model.Build;
import com.example.jay.casinogame.model.Card;
import com.example.jay.casinogame.model.Player;
import com.example.jay.casinogame.model.Round;

import java.util.Vector;

public class ChooseTableCardActivity extends AppCompatActivity {
   private Round round;
   private Card card;
   private Player player;
   private Vector<Card> userChosenCards = new Vector<Card>();
   private int option;

   /* *********************************************************************
Function Name: onCreate
Purpose: To initialize the Activity
Parameters:
         savedInstanceState, Bundle object passed by value
Return Value: None
Local Variables:
         intent, an Intent variable containing the intent
         round, a Round variable containing the round
         card, a Card variable holding the chosen card
         player, a Player variable initialized to the human player
         option, an integer initialized to the key option
Algorithm:
         1) Get the round, card, option and player from the intent
         2) Update the layout
         3) Add a check button to cards to indicate user chosen cards
         4) Create the submit button
Assistance Received: none
********************************************************************* */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_choose_card);

      Intent intent = getIntent();

      round = (Round) intent.getSerializableExtra("round");
      card = (Card) intent.getSerializableExtra("chosenCard");
      player = (Player) intent.getSerializableExtra("player");
      option = (int) intent.getSerializableExtra("option");

      updateLayout();

      Button submitButton = findViewById(R.id.submitButton);
      submitButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

            switch(option) {
               case 1:
                  if (checkIfValidBuild()) {
                     player.createBuild(card, userChosenCards, round.getTable(), round.getBuilds());
                     round.getHumanObj().removeCardFromHand(card);

                     Intent output = new Intent();
                     output.putExtra("round", round);
                     setResult(RESULT_OK, output);

                     finish();
                     return;
                  }
               case 2:
                  if (checkIfValidBuild()){
                     userChosenCards.add(card);
                     round.getHumanObj().createMultipleBuild(card, userChosenCards, round.getTable(), round.getBuilds());

                     round.getHumanObj().removeCardFromHand(card);

                     Intent output = new Intent();
                     output.putExtra("round", round);
                     setResult(RESULT_OK, output);

                     finish();
                     return;
                  }
               case 3:
                  int setVal = 0;

                  for (Card chosenCard:userChosenCards){
                     setVal += chosenCard.getNumValue();
                  }

                  if (setVal == card.getNumValue() && userChosenCards.size() == 2){
                     round.getHumanObj().captureSet(userChosenCards.elementAt(0),
                           userChosenCards.elementAt(1), round.getTable());
                     round.setPlayerLastCaptured(player);
                     round.getHumanObj().removeCardFromHand(card);

                     Intent output = new Intent();
                     output.putExtra("round", round);
                     output.putExtra("capturedSet", true);
                     setResult(RESULT_OK, output);

                     finish();
                     return;
                  }

            }
            if (option != 3) {
               Toast.makeText(ChooseTableCardActivity.this,
                     "Invalid Build - Incorrect Build Value", Toast.LENGTH_LONG).show();
               return;
            }
            Toast.makeText(ChooseTableCardActivity.this,
                  "Invalid Set", Toast.LENGTH_LONG).show();

         }
      });

      // Set up Cancel Button
      Button cancelButton = findViewById(R.id.cancelButton);
      cancelButton.setOnClickListener(new View.OnClickListener(){
         @Override
         public void onClick(View v){
            setResult(RESULT_CANCELED, null);
            finish();
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
         drawable, a Drawable varable that is initialized to the card's name
         counter, an integer that contains the amount of cards created
Algorithm:
         1) Create the layout depending on the option
Assistance Received: none
********************************************************************* */
   public void updateLayout(){
      //chosenCardLayout contains the layout for chosenCard
      LinearLayout chosenCardLayout = findViewById(R.id.chosenCard);

      // tableLayout contains the layout for cards from the table
      GridLayout tableLayout = findViewById(R.id.tableCardLayout);

      // layoutParams contains parameters for layout
      GridLayout.LayoutParams layoutParams;

      ImageView imageView = new ImageView(this);
      imageView.setLayoutParams(new ViewGroup.LayoutParams(1, 2));

      Drawable drawable = this.getResources().getDrawable(this.getResources().getIdentifier(
            card.getCardName().toLowerCase(), "drawable", getPackageName()));
      imageView.setBackgroundDrawable(drawable);


      layoutParams = new GridLayout.LayoutParams(GridLayout.spec(0, 1), GridLayout.spec(0));
      layoutParams.setMargins(2, 0, 2, 0);
      layoutParams.width = 100;
      layoutParams.height = 150;

      chosenCardLayout.addView(imageView, layoutParams);


      // Set table Card image
      int counter = 0;
      for (final Card card: round.getTable()){

         switch(option) {
            case 1:
               counter = createBuildLayout(card, tableLayout, counter);
               break;
            case 2:
               counter = createBuildLayout(card, tableLayout, counter);
               break;
            case 3:
               counter = createBuildLayout(card, tableLayout, counter);
         }
      }

   }

   /* *********************************************************************
Function Name: createBuildLayout
Purpose: To create the build layout
Parameters:
         card, A card that contains the build
         tableLayout, a GridLayout variable that contains the layout for the table
         counter, an integer indicating the amount of cards placed in the view
Return Value: An integer indicating the amount of cards placed
Local Variables:
         layoutParams, a GridLayout.LayoutParams variable that contains the parameters
            for the card
         build, a TextView variable that contains the build's name
         drawable, a Drawable variable that contains the drawable picture
Algorithm:
         1) Check if the card is a build
            a) if the card is a build, then create the card as text
            b) if not use an image
         2) Add a checkBox to the card
         3) Increase and return the counter
Assistance Received: none
********************************************************************* */
   public int createBuildLayout(final Card card, GridLayout tableLayout, int counter){
      GridLayout.LayoutParams layoutParams;
      Drawable drawable;
      if (card instanceof Build) {
         TextView build = new TextView(this);
         layoutParams = new GridLayout.LayoutParams(GridLayout.spec(0, 1),
               GridLayout.spec(counter));

         String buildName = "\t" + card.getCardName();
         build.setText(buildName);

         if (((Build) card).getOwnerId() == 1) {
            build.setTextColor(Color.rgb(220, 20, 60));
         }
         else{
            build.setTextColor(Color.rgb(0,191,255));
         }

         tableLayout.addView(build, layoutParams);

         return ++counter;
      }
      final CheckBox checkBox = new CheckBox(this);
      checkBox.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (checkBox.isChecked()) {
               userChosenCards.add(card);
               return;
            }
            userChosenCards.remove(card);
         }
      });


      checkBox.setLayoutParams(new ViewGroup.LayoutParams(1, 2));

      layoutParams = new GridLayout.LayoutParams(GridLayout.spec(0, 1), GridLayout.spec(counter));

      layoutParams.setMargins(2, 0, 2, 0);

      drawable = this.getResources().getDrawable(this.getResources().getIdentifier(
            card.getCardName().toLowerCase(), "drawable", getPackageName()));
      checkBox.setBackgroundDrawable(drawable);
      layoutParams.width = 100;
      layoutParams.height = 150;

      checkBox.setWidth(100);
      checkBox.setHeight(150);

      tableLayout.addView(checkBox, layoutParams);

      return ++counter;
   }

   /* *********************************************************************
Function Name: checkIfValidBuild
Purpose: To check if the build is valid
Parameters:
         None
Return Value: A Boolean value indicating if the build was valid
Local Variables:
         buildValue, an integer containing the build's value
Algorithm:
         1) Calculate the build's value
         2) Check if the build is valid based on the option
Assistance Received: none
********************************************************************* */
   public Boolean checkIfValidBuild(){
      int buildValue = 0;
      for (Card card: userChosenCards){
         buildValue += card.getNumValue();
      }

      buildValue += card.getNumValue();

      switch (option){
         case 1:
            if(userChosenCards.size() == 0){
               Toast.makeText(ChooseTableCardActivity.this,
                     "Invalid Build - Cannot create a build with just one card",
                     Toast.LENGTH_LONG).show();
               return false;
            }

            return player.checkBuildValueInHand(player.getPlayerHandVec(), buildValue);
         case 2:
            if (buildValue == round.getBuilds().get(player.getId()).getNumValue()){
               return true;
            }
            return false;
      }
      return false;
   }


}
