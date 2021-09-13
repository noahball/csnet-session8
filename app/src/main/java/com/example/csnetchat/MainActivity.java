package com.example.csnetchat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import Model.DatabaseAdapter;
import Model.Message;

/**
 * The main activity class displays all the chat messages retrieved and let's users enter their username and send messages.
 *
 * @author jessicaturner
 *
 */
public class MainActivity extends AppCompatActivity {

    //Used to retrieve database messages to display
    private DatabaseAdapter da = new DatabaseAdapter();
    //The username for this chat activity
    private String USERNAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //A timed thread task which continually updates the messages retrieved from the database
        TimerTask updateMessages = new TimerTask(){
            @Override
            public void run() {
                da = new DatabaseAdapter();
                da.execute(true);
            }
        };

        //A timer to execute the updateMessages thread every 2 seconds
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(updateMessages, 0, 2000);

        //The messages display needs to also be updated every time we find new messages, this must be completed with respect to the Android Activity Lifecycle
        LinearLayout ll = (LinearLayout) findViewById(R.id.messages);
        //We use a handler instead of a timer which takes into account the Android Activity Lifecycle
        final Handler timerHandler = new Handler();

        //The method to update the messages to display every 2 seconds
        Runnable updater = new Runnable() {
            @Override
            public void run() {
                displayMessages();
                timerHandler.postDelayed(this, 2000);
            }
        };
        //Start the updater
        timerHandler.post(updater);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Update the messages each time the application/activity is resumed
        displayMessages();
    }

    /**
     * A function which displays the messages in the appropriate linear layout
     */
    private void displayMessages(){
        try {
            //Retrieve the messages from the database
            ArrayList<Message> messages = da.get();
            LinearLayout ll = (LinearLayout) findViewById(R.id.messages);
            //Clear what was displayed
            ll.removeAllViews();

            for (Message m : messages) {
                //Create a new linear layout for each message
                LinearLayout message = new LinearLayout(this);
                message.setOrientation(LinearLayout.VERTICAL);
                message.setPadding(5, 5, 5, 5);

                //Display the message
                TextView info = new TextView(this);
                info.setText(m.toString());

                //Display the timestamp
                TextView date = new TextView(this);
                date.setText(m.getTimestamp());
                date.setTextSize(10);

                message.addView(info);
                message.addView(date);

                ll.addView(message,0);
            }
        } catch (Exception ex){
            ex.printStackTrace();
            //Send a message to the user if messages could not be loaded
            Toast.makeText(this, "Could not load messages",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Starts a new thread which handles sending the new message to the database.
     * @param username The username of the sender
     * @param message The message that they want to send
     */
    private void sendMessageToDatabase(String username, String message){
        Thread thread = new Thread(){
            public void run(){
                da.sendMessage(username, message);
            }
        };
        thread.start();
    }

    /**
     * Hides the keyboard from the screen, retrieved from https://rmirabelle.medium.com/close-hide-the-soft-keyboard-in-android-db1da22b09d2
     */
    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if(view == null){
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * A method that sends the message the user has currently entered to the database to be displayed
     * @param view The view that was clicked to trigger this function
     */
    public void sendMessage(View view){
        EditText usernameBox = (EditText) findViewById(R.id.name);
        String username = usernameBox.getText().toString();
        EditText EditText = (EditText) findViewById(R.id.message);
        String message = EditText.getText().toString();
        //if(username.isEmpty()) {
            //Toast.makeText(this, "You must enter a username.", Toast.LENGTH_SHORT).show();
        //} else {
            if(message.isEmpty()) {
                Toast.makeText(this, "You must enter a message.", Toast.LENGTH_SHORT).show();
            } else {
                sendMessageToDatabase(username, message);
                EditText.setText("");
                hideKeyboard();
            }
        }
    //}

    /**
     * A method that saves the username.
     * @param view The view that was clicked to trigger this function
     */
    public void saveUsername(View view){
        EditText usernameBox = (EditText) findViewById(R.id.name);
        String username = usernameBox.getText().toString();
        //if(username.isEmpty()) {
            //Toast.makeText(this, "You must enter a username.", Toast.LENGTH_SHORT).show();
        //} else {
            hideKeyboard();
            Button nameButton = (Button) findViewById(R.id.name_button);
            if (nameButton.getText().equals("Edit")) {
                USERNAME = null;
                usernameBox.setEnabled(true);
                nameButton.setText("Save");
            } else {
                USERNAME = username;
                usernameBox.setEnabled(false);
                nameButton.setText("Edit");
            }
        }
    }
//}