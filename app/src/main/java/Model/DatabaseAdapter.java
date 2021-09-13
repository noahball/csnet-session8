package Model;

import android.os.AsyncTask;

import androidx.core.text.PrecomputedTextCompat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 *
 * DO NOT MODIFY THIS FILE
 *
 * This class connects to a freesqldatabase in order to store chat messages in a relational database. Access to this database is only for 30 days at which point you will
 * need to change the database connection strings. The adapter implementation is lazy in that it requests all messages from the database but only adds the ones it hasn't
 * seen yet to the messages list. This could be improved by altering the SQL request to only get messages it hasn't seen since the last message was uploaded, but isn't
 * necessary for this example.
 *
 * @author jessicaturner
 */
public class DatabaseAdapter extends AsyncTask<Boolean, Integer, ArrayList<Message>> {

    //The database connection strings for jdbc
    private static String DB_NAME = "sql6435778";
    private static String USER="sql6435778";
    private static String PASSWORD="NdxGKqjCur";
    private static String HOST = "sql6.freesqldatabase.com";

    //A list to store all the messages that will be displayed in the application
    public ArrayList<Message> messages = new ArrayList<Message>();

    /**
     *  This method connects to the mysql database and queries all the messages stored then adds them to the messages list
     *  */
    private ArrayList<Message> getMessages(){
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            statement.execute("SET time_zone='+12:00'");
            ResultSet resultSet = statement.executeQuery("SELECT * FROM chat;");
            while(resultSet.next()){
                Message m = new Message(resultSet.getInt(1),resultSet.getString(2),resultSet.getString(3),new Date(resultSet.getTimestamp(4).getTime()));
                if(!messages.contains(m)){
                    messages.add(m);
                }
            }
            connection.close();
            return messages;
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * This method opens a new connection to the database using the supplied credentials.
     * @return Database connection
     */
    private Connection getConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String instanceUrl = "jdbc:mysql://" + HOST + ":3306/" + DB_NAME + "?user=" + USER + "&password=" + PASSWORD;

            Connection connection = DriverManager.getConnection(instanceUrl);
            return connection;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * This method executes the getMessages function so that they can be displayed to the user.
     * @param booleans true or false to indicate execution.
     * @return The list of messages to display.
     */
    @Override
    protected ArrayList<Message> doInBackground(Boolean... booleans) {
        ArrayList<Message> messages = getMessages();
        return messages;
    }

    /**
     * Send a message to be stored in the database.
     * @param username the username of the message sender.
     * @param message the message to be stored.
     */
    public void sendMessage(String username, String message){
        try {
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            //If we don't have a connection to the database, then do nothing
            if (statement==null) {
                return;
            //Otherwise, insert the new message into the database
            } else {
                statement.execute("SET time_zone='+12:00'");
                String query = "INSERT INTO chat (UserName,Message,Timestamp) VALUES ('" + username + "','" + message + "',CURRENT_TIMESTAMP)";
                statement.executeUpdate(query);
            }
            connection.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
}

