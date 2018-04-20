package arsic.aleksa.chatapplication;

import android.graphics.drawable.Drawable;

/**
 * Created by Aleksa Arsic on 3/29/2018.
 */

public class ContactsRow {

    public String contactName;
    public String firstLetter;
    public Drawable sendBtn;

    public ContactsRow(String name, Drawable drawable){
        contactName = name;
        firstLetter = contactName.substring(0, 1);
        sendBtn = drawable;
    }

}
