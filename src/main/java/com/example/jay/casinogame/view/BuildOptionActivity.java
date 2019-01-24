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

public class BuildOptionActivity extends AppCompatActivity {
   private Round round;
   private Card card;
   private Player player;

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
         3) Create the buildButton, extendButton, multipleButton and cancelButton
Assistance Received: none
********************************************************************* */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_build);

      Intent intent = getIntent();

      round = (Round) intent.getSerializableExtra("round");
      card = (Card) intent.getSerializableExtra("chosenCard");
      player = (Player) intent.getSerializableExtra("player");

      updateLayout();

      Button buildButton = findViewById(R.id.buildButton);
      buildButton.setOnClickListener(new View.OnClickListener(){
         @Override
         public void onClick(View v){
            if(player.doesPlayerOwnBuild(round.getBuilds())){
               Toast.makeText(BuildOptionActivity.this,"Already Own a Build",
                     Toast.LENGTH_LONG).show();

               return;
            }
            startActivityChooseAction(1);
         }
      });

      Button extendButton = findViewById(R.id.extendButton);
      extendButton.setOnClickListener(new View.OnClickListener(){
         @Override
         public void onClick(View v){
            if(player.checkExtendBuildConditions(card, round.getBuilds())){
               Build build = round.getBuilds().get(round.getComputerObj().getId());
               player.extendBuild(card, build, round.getTable(), round.getBuilds());
               round.getHumanObj().removeCardFromHand(card);

               Intent output = new Intent();
               output.putExtra("round", round);
               setResult(RESULT_OK, output);

               finish();
               return;
            }
            Toast.makeText(BuildOptionActivity.this,"Cannot Extend Opponent's build",
                  Toast.LENGTH_LONG).show();
         }
      });

      Button multipleButton = findViewById(R.id.multipleButton);
      multipleButton.setOnClickListener(new View.OnClickListener(){
         @Override
         public void onClick(View v){
            if (!player.checkIfCanBuildMultiple(round.getTable(), round.getBuilds())) {
               Toast.makeText(BuildOptionActivity.this,"Cannot Create a Multiple Build",
                     Toast.LENGTH_LONG).show();
               return;
            }
            startActivityChooseAction(2);

         }
      });

      // Setup cancel Button
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
         drawable, a Drawable variable that is initialized to the card's name
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
Function Name: startActivityChooseCard
Purpose: To start the Activity chooseActionActivity
Parameters:
         option, an integer that contains an indicator for the type of action
            taken by the player
Return Value: None
Local Variables:
         intent, an Intent variable that contains extra information such as
            card, round, player, and option
Algorithm:
Assistance Received: none
********************************************************************* */
   public void startActivityChooseAction(int option){
      Intent intent = new Intent(this, ChooseTableCardActivity.class);
      intent.putExtra("chosenCard", card);
      intent.putExtra("round", round);
      intent.putExtra("player", player);
      intent.putExtra("option", option);

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
Assistance Received: none
********************************************************************* */
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data){
      // Check if the resultCode is equal to RESULT_OK
      if (resultCode == RESULT_OK) {
         setResult(RESULT_OK, data);
         super.finish();
         return;
      }
   }
}
