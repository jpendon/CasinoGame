/*
 **************************************************************
 * Name:  Jay Pendon						                     *
 * Project: Casino Game								         *
 * Class: CMPS 366 01 - Organization of Programming Languages *
 * Date:  Decemeber 11, 2018		                             *
 **************************************************************
 */

package com.example.jay.casinogame.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jay.casinogame.R;
import com.example.jay.casinogame.model.Build;
import com.example.jay.casinogame.model.Card;
import com.example.jay.casinogame.model.Human;
import com.example.jay.casinogame.model.Player;
import com.example.jay.casinogame.model.Round;


public class ChooseActionActivity extends AppCompatActivity {
   private Round round;
   private Card card;
   private Player player;
   private Boolean capturedSet = false;

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
Algorithm:
         1) Get the round, card, and player from the intent
         2) Update the layout
         3) Create the trailButton, captureButton, buildButton and cancelButton
Assistance Received: none
********************************************************************* */
   @Override
   public void onCreate(Bundle savedInstanceState){
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_options);

      Intent intent = getIntent();

      round = (Round)intent.getSerializableExtra("round");
      card = (Card)intent.getSerializableExtra("chosenCard");

      if (round.getPlayers().elementAt(0) instanceof Human){
         player = round.getPlayers().elementAt(0);
      }
      else{
         player = round.getPlayers().elementAt(1);
      }

      updateLayout();

      // Set up Trail Button
      Button passButton = findViewById(R.id.trailButton);
      passButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            // Check if the player can trail
            if (!round.checkTableCards(card) && !player.doesPlayerOwnBuild(round.getBuilds())) {
               player.trailCard(card, round.getTable());
               player.removeCardFromHand(card);

               Intent output = new Intent();
               output.putExtra("round", round);
               setResult(RESULT_OK, output);
               finish();
               return;
            }

            Toast.makeText(ChooseActionActivity.this,"Cannot Trail", Toast.LENGTH_LONG).show();
         }
      });

      // Setup captureButton
      Button captureButton = findViewById(R.id.captureButton);
      captureButton.setOnClickListener(new View.OnClickListener(){
         @Override
         public void onClick(View v){
            // Check if a capture can occur
            if(round.checkTableCards(card)){
               // capture the cards from the table
               round.getHumanObj().captureOption(card, round.getTable(), round.getBuilds());
               // Remove the card from the player's hand
               player.removeCardFromHand(card);
               // Set the player has the person who last captured
               round.setPlayerLastCaptured(round.getHumanObj());
               capturedSet = true;
            }

            // Check if there is a set
            if(player.isThereASet(card, round.getTable())){
               askCaptureSetPrompt();
               return;
            }

            // check if a set has been captured
            if(capturedSet){
               // Set the human as the player who last captured
               round.setPlayerLastCaptured(round.getHumanObj());

               Intent output = new Intent();
               output.putExtra("round", round);
               setResult(RESULT_OK, output);
               finish();
               return;
            }

            Toast.makeText(ChooseActionActivity.this,"Cannot Capture", Toast.LENGTH_LONG).show();
         }
      });

      // Set up Build Button
      Button buildButton = findViewById(R.id.buildButton);
      buildButton.setOnClickListener(new View.OnClickListener(){
         @Override
         public void onClick(View v){
            startActivityBuild();
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
         if (card instanceof Build){
            TextView build = new TextView(this);
            build.setText(card.getCardName());

            if (((Build) card).getOwnerId() == 1) {
               build.setTextColor(Color.rgb(220, 20, 60));
            }
            else{
               build.setTextColor(Color.rgb(0,191,255));
            }

            tableLayout.addView(build);
            counter++;
            continue;
         }
            imageView = new ImageView(this);

            imageView.setLayoutParams(new ViewGroup.LayoutParams(1, 2));

            layoutParams = new GridLayout.LayoutParams(GridLayout.spec(0, 1), GridLayout.spec(counter));

            layoutParams.setMargins(2, 0, 2, 0);

            drawable = this.getResources().getDrawable(this.getResources().getIdentifier(
                  card.getCardName().toLowerCase(), "drawable", getPackageName()));
            imageView.setBackgroundDrawable(drawable);
            layoutParams.width = 100;
            layoutParams.height = 150;

            tableLayout.addView(imageView, layoutParams);

         counter++;
      }

   }

   /* *********************************************************************
Function Name: startActivityBuild
Purpose: To start the Activity startActivityBuild
Parameters:
         None
Return Value: None
Local Variables:
         intent, an Intent variable that contains extra information such as
            card, round, and player
Algorithm:
Assistance Received: none
********************************************************************* */
   public void startActivityBuild(){
      Intent intent = new Intent(this, BuildOptionActivity.class);
      intent.putExtra("chosenCard", card);
      intent.putExtra("round", round);
      intent.putExtra("player", player);

      startActivityForResult(intent, 1);
   }

   /* *********************************************************************
Function Name: askCaptureSetPrompt
Purpose: To ask the user if they would like to capture a set
Parameters:
         None
Return Value: None
Local Variables:
         None
Algorithm:
         1) create an AlertDialog.Builder object
         2) Ask the user if they would like to capture a set
         3) Perform an action based on response
Assistance Received: none
********************************************************************* */
   public void askCaptureSetPrompt(){
      new AlertDialog.Builder(ChooseActionActivity.this)
            .setMessage("Would you like to capture a set?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                  startActivityChooseCards();
               }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                  if (capturedSet){
                     round.getHumanObj().addToPile(card);

                     Intent output = new Intent();
                     output.putExtra("round", round);
                     setResult(RESULT_OK, output);
                     finish();
                     return;
                  }
               }
            }).show();
   }

   /* *********************************************************************
Function Name: startActivityChooseCards
Purpose: To start the activity ChooseTableCardActivity
Parameters:
         None
Return Value: None
Local Variables:
         intent, an Intent variable that contains extra information such as
            card, round, player, and option,
Algorithm:
Assistance Received: none
********************************************************************* */
   public void startActivityChooseCards(){
      Intent intent = new Intent(this, ChooseTableCardActivity.class);
      intent.putExtra("chosenCard", card);
      intent.putExtra("round", round);
      intent.putExtra("player", player);
      intent.putExtra("option", 3);

      startActivityForResult(intent, 1);
   }

   /* *********************************************************************
Function Name: onActivityResult
Purpose: To receive the result of the activity
Parameters:
         requestCode, an integer containing the requestCode,
         resultcode, an integer containing the result code
         data, an intent containing the data from the activity
Return Value: None
Local Variables:
         None
Algorithm:
         1) Check if the resultCode is equal to RESULT_CANCELED
         2) Check if the intent contains the key capturedSet
            a) set the capturedSet variable to true
         3) Check if the resultCode is equal to RESULT_OK
Assistance Received: none
********************************************************************* */
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      // Check if the user clicked on cancel button
      if(resultCode == RESULT_CANCELED){
         return; 
      }

      // Check if the user captured a set
      if (getIntent().getExtras().getSerializable("capturedSet") != null){
         capturedSet = true;
      }

      // Update the round
      round = (Round)data.getSerializableExtra("round");
      // Check if the resultCode is equal to RESULT_OK
      if (resultCode == RESULT_OK){
         if (capturedSet && player.isThereASet(card, round.getTable())){
            askCaptureSetPrompt();
            return;
         }

         setResult(RESULT_OK, data);
         super.finish();
         return;
      }
   }
}
