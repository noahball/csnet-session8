package Model;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 *
 * DO NOT MODIFY THIS FILE
 *
 * This message class stores the details of a message from the database.
 *
 * @author jessicaturner
 */
public class Message {

    //The unique identifier of the message
    private int id;
    //The username of the person who sent the message
    private String name;
    //The message details
    private String message;
    //The time the message was sent
    private Date date;

    public Message(int i, String n, String m, Date d){
        id = i;
        name = n;
        message = m;
        date = d;
    }

    @Override
    public String toString(){
        return name + ": " + message;
    }

    /**
     * Get the timestamp is a displayable format.
     * @return the timestamp as a string e.g. 10-12-1928 15:30
     */
    public String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        return sdf.format(date);
    }

    public Date getTime(){
        return date;
    }

    /**
     * Overriding the equals function so that messages are compared on ID instead of memory addresses
     * @param o the object to compare with
     * @return true or false based on the equivalence of the message ID
     */
    @Override
    public boolean equals(Object o){
        if(o==this) return true;
        if(!(o instanceof Message)) return false;
        Message m = (Message) o;
        return this.id==m.id;
    }
}
