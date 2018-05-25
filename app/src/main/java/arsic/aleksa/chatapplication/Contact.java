package arsic.aleksa.chatapplication;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Aleksa Arsic on 3/29/2018.
 */

public class Contact {

    public int id;
    public String username;
    public String contactName;
    public String firstName;
    public String lastName;
    public String firstLetter;
    public Drawable sendBtn;

    public Contact(String username, String firstName, String lastName, int id, Drawable drawable){
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        contactName = firstName + " " + lastName;
        firstLetter = contactName.substring(0, 1);
        sendBtn = drawable;
    }

    public Contact(String username, String firstName, String lastName){
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        contactName = firstName + " " + lastName;
        firstLetter = contactName.substring(0, 1);
    }

    public Contact(String username, String firstName, String lastName, int id){
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        contactName = firstName + " " + lastName;
        firstLetter = contactName.substring(0, 1);
    }

    public Contact(String username, Drawable drawable){
        this.username = username;
        contactName = username;
        firstLetter = contactName.substring(0, 1);
        this.sendBtn = drawable;
    }


    public int getId() {
        return id;
    }

    public String getContactName() {
        return contactName;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
