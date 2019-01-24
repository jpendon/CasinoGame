/*
 **************************************************************
 * Name:  Jay Pendon						                     *
 * Project: Casino Game								         *
 * Class: CMPS 366 01 - Organization of Programming Languages *
 * Date:  Decemeber 11, 2018		                             *
 **************************************************************
 */

package com.example.jay.casinogame.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.example.jay.casinogame.R;
import com.example.jay.casinogame.model.Round;
import com.example.jay.casinogame.model.Serialization;
import com.example.jay.casinogame.model.Tournament;


import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity{
   private int STORAGE_PERMISSION_CODE = 1;
   private String userInput = "";
   Tournament tournament;

   /* *********************************************************************
Function Name: onCreate
Purpose: To initialize the Activity
Parameters:
         savedInstanceState, Bundle object passed by value
Return Value: None
Local Variables:
         tournament, a tournament variable initialized to a new tournament
         files[], a File array that is used to contain the Files from the Download
            Directory
         fileNames[], a String array used to hold the names of the files
         filePath, a String used to contain the path to the file
         messages, a AlertDialog.Builder variable used to contain an alert to be
            displayed to the user
Algorithm:
         1) Create the startButton
         2) Check if the user is starting a new game or resuming
         3) Create an AlertDialog
Assistance Received: none
********************************************************************* */
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      tournament = new Tournament();

      Button startButton = findViewById(R.id.startButton);
      startButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                  Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
               Toast.makeText(MainActivity.this, "Permission Already Granted", Toast.LENGTH_SHORT).show();
            } else {
               requestStoragePermission();
            }

            // Check if the user is starting a new Game or Resuming
            AlertDialog.Builder messages = new AlertDialog.Builder(MainActivity.this);
            messages.setMessage("Start a New Game or Resume a Game?")
                  .setPositiveButton("Resume Game", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                        // Create an alert for choosing a file name
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setTitle("Choose the name of the File: ");

                        // files array contains the files from the download directory
                         File files[] = Environment.getExternalStoragePublicDirectory(
                              Environment.DIRECTORY_DOWNLOADS).listFiles(new FilenameFilter() {
                           @Override
                           public boolean accept(File file, String fileName) {
                              return true;
                           }
                        });

                         // fileNames contains the names of the files found
                        String fileNames[] = new String[files.length];
                        for (int i = 0; i < files.length; ++i) {
                           // Get the name of file
                           fileNames[i] = files[i].getName();
                        }

                        alert.setItems(fileNames, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                              ListView listview = ((AlertDialog) dialog).getListView();
                              String fileName = (String) listview.getAdapter().getItem(which);

                              // filePath contains the path chosen by the user
                              String filePath = Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName;

                              try {
                                 // inputStream contains the data from the filePayh
                                 InputStream inputstream = new FileInputStream(filePath);
                                 startRoundActivity(tournament.resumeGame(inputstream));
                              } catch (IOException e) {
                                 e.printStackTrace();
                              }
                           }
                        });
                        alert.show();
                     }
                  })
                  .setNegativeButton("New Game", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                        // Play the game
                        startRoundActivity(tournament.createNewRound());
                     }
                  });
            messages.show();
         }
      });
   }

/* *********************************************************************
Function Name: startRoundActivity
Purpose: To start the RoundActivity
Parameters:
         round, a Round object passed by value
Return Value: None
Local Variables:
         intent, an Intent variable containing the round
Algorithm:
Assistance Received: none
********************************************************************* */
   public void startRoundActivity(Round round) {
      Intent intent = new Intent(this, RoundActivity.class);
      intent.putExtra("round", round);
      startActivityForResult(intent, 1);
   }

/* *********************************************************************
Function Name: requestStoragePermission
Purpose: To requestStorage permission
Parameters:
         None
Return Value: None
Local Variables:
         None
Algorithm:
         1) Check if the user has or has not allowed permission
Assistance Received: Coding in Flow Video on Requesting Runtime Permissions
********************************************************************* */
   private void requestStoragePermission() {
      // Check to see if the user needs to allow access to the files
      if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
         new AlertDialog.Builder(this).setTitle("Permission needed")
               .setMessage("This Permission is needed to write and read to storage")
               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {

                  }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                  }
               }).create().show();
      }
      else {
         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
               STORAGE_PERMISSION_CODE);
         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
               STORAGE_PERMISSION_CODE);
      }
   }

   /* *********************************************************************
Function Name: onRequestPermissionsResult
Purpose: check if the permission was granted
Parameters:
         requestCode, an integer containing the request code
         permission, a string array containing the permission
         grantResults, an array containing the granted results
Return Value: None
Local Variables:
         None
Algorithm:
         1) Check if the requestCode matches the STORAGE_PERMISSION_CODE
Assistance Received: Coding in Flow Video on Requesting Runtime Permissions
********************************************************************* */
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, int[] grantResults){
      // Check to see if the user granted access to files
      if (requestCode == STORAGE_PERMISSION_CODE){
         if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
         }
         else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
         }
      }
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
         round, a Round variable containing the round
Algorithm:
         1) Initialize the round variable
         2) Check if the user wanted to save
            a) Save the game
         3) Update the tournament
         4) Check check player scores
         5) Check if the game has ended
            a) Print the post Game text to user
         6) Create a new round and start the activity
Assistance Received: none
********************************************************************* */
   @Override
   protected void onActivityResult(int reqCode, int resCode, Intent data) {
      final Round round = (Round)data.getSerializableExtra("round");

      // Check if the user wants to save
      if (resCode == RESULT_CANCELED){
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle("Enter the File Name: ");

         final EditText input = new EditText(this);
         input.setInputType(InputType.TYPE_CLASS_TEXT);
         builder.setView(input);

         // Set up the button
         builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               userInput = input.getText().toString();

               // filePath contains the directory
               String filePath = Environment.getExternalStoragePublicDirectory(
                     Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + userInput + ".txt";

               Serialization serialization = new Serialization();
               serialization.saveGame(new File(filePath), round);

               new AlertDialog.Builder(MainActivity.this).setMessage("Game Has Been saved!")
                     .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                     }).show();
            }
         })
               .setCancelable(false)
               .show();
         return;
      }

      // Update the tournament
      tournament.updateTournament(round);
      // Check the player's score
      tournament.checkPlayerScore();

      // Check to see if the game has ended
      if (tournament.hasGameEnded()) {
         // Create the AlertDialog for post game information
         AlertDialog.Builder endGameMessage = new AlertDialog.Builder(this);
         // Set the message to the return value of getWinnerBoardString
         endGameMessage.setMessage(tournament.getWinnerBoardString())
               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                  }
               });
         endGameMessage.setCancelable(false);
         endGameMessage.show();
         return;
      }

      // Continue the game
      startRoundActivity(tournament.createNewRound());
   }


}
