package arsic.aleksa.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ContactsActivity extends AppCompatActivity {

    /* Layout representatives */
    private Button btnLogOut;
    private ListView listViewContacts;

    /* Boolean for exiting on two times pressed back button */
    private boolean backPressedOnce = false;

    /* Shared Preference to read logged user ID */
    SharedPreferences sharedPref;
    int userID = 0;

    /* Database entries */
    mDataBaseHelper dataBaseH;
    Contact[] contacts;

    /* Disable back button on Contacts Activity */
    @Override
    public void onBackPressed() {

        if(backPressedOnce){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXTRA", true);
            startActivity(intent);
        }

        backPressedOnce = true;

        Toast.makeText(getApplicationContext(), getString(R.string.exitMsg), Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        btnLogOut = findViewById(R.id.ContactsLogOutBtn);

        /* List view items */
        listViewContacts = findViewById(R.id.ContactsListView);
        ContactsAdapter contactsAdapter = new ContactsAdapter(this);

        dataBaseH = new mDataBaseHelper(this);
        /* Calling method to add database data to custom adapter */
        addContactsToList(contactsAdapter);
        listViewContacts.setAdapter(contactsAdapter);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainActivity();
            }
        });
    }

    private void launchMessageActivity(){
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
    }

    private void toMainActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /* Method for adding database data to list view */
    private void addContactsToList(ContactsAdapter contactsAdapter) {

        /* Shared Preference to read logged user ID */
        sharedPref = getApplicationContext().getSharedPreferences("arsic.aleksa.chatapplication",
                Context.MODE_PRIVATE);
        userID = sharedPref.getInt(MainActivity.ID_SHARED_PREF_KEY, 0);
        contacts = dataBaseH.readContacts();

        for(int i = 0; i < contacts.length; i++){
            if(userID == contacts[i].id) continue;
            else{
                contactsAdapter.addContact(new Contact(contacts[i].username, contacts[i].firstName,
                        contacts[i].lastName, contacts[i].id, getDrawable(R.drawable.send_button)));
            }
        }
    }
}
