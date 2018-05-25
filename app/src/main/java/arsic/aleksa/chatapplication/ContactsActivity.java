package arsic.aleksa.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

public class ContactsActivity extends AppCompatActivity {

    /* Layout representatives */
    private Button btnLogOut;
    private ImageButton btnRefresh;
    private ListView listViewContacts;
    private TextView loggedUser;

    /* Boolean for exiting on two times pressed back button */
    private boolean backPressedOnce = false;

    /* Shared Preference to read logged user ID */
    private SharedPreferences sharedPref;
    private int userID = 0;
    private String userName;
    private String sessionId;

    /* Database entries */
    mDataBaseHelper dataBaseH;
    Contact[] contacts;

    private HttpHelper httpHelper;
    private Handler handler;

    String[] usernames;
    ContactsAdapter contactsAdapter;

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

        loggedUser = findViewById(R.id.LoggedUser);
        btnLogOut = findViewById(R.id.ContactsLogOutBtn);
        btnRefresh = findViewById(R.id.RefreshContacts);

        /* HTTP helper class */
        httpHelper = new HttpHelper();
        /* Used for hanndling UI components in seperate thread */
        handler = new Handler();

        /* List view items */
        listViewContacts = findViewById(R.id.ContactsListView);
        contactsAdapter = new ContactsAdapter(this);

        /* Get sessionId from Shared Preferences */
        sharedPref = getApplicationContext().getSharedPreferences("aleksa.arsic.chatapplication",
                Context.MODE_PRIVATE);
        sessionId = sharedPref.getString(MainActivity.SESSION_ID, null);

        /* Parse username from sessionId */
        parseUsernameFromSessionID(sessionId);

        dataBaseH = new mDataBaseHelper(this);
        /* Calling method to add database data to custom adapter */
        //addContactsToList(contactsAdapter);
        /* Method for populating contactsAdapter with HTTP server information */
        populateListView(contactsAdapter);
        listViewContacts.setAdapter(contactsAdapter);

        loggedUser.setText(userName);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Method for refreshing contactsAdapter with HTTP server information */
                Toast.makeText(ContactsActivity.this, getString(R.string.refresh_contacts), Toast.LENGTH_SHORT).show();
                contactsAdapter.clearAdapter();
                populateListView(contactsAdapter);
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = new JSONObject();

                        try{
                            jsonObject.put("sessionid", sessionId);

                            final boolean success = httpHelper.postJsonObjectWithSessionID(MainActivity.SERVER_URL + MainActivity.LOGOUT,
                                    jsonObject, sessionId);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(success){
                                        Toast.makeText(ContactsActivity.this, R.string.logout_successful, Toast.LENGTH_SHORT).show();
                                        toMainActivity();
                                    }
                                    else Toast.makeText(ContactsActivity.this, R.string.logout_failed, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }catch (JSONException e){
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }


    /* GET JSON Array from server and populate listView with JSON array contacts */

    private void populateListView(final ContactsAdapter contactsAdapter){

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray jsonArray = new JSONArray();

                try{
                    jsonArray = httpHelper.getJSONArrayFromURL(MainActivity.SERVER_URL + MainActivity.CONTACTS,
                            sessionId);

                    usernames = new String[jsonArray.length()];

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        usernames[i] = jsonObject.getString("username");
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(usernames.length != 0){
                                for(int i = 0; i < usernames.length; i++){
                                    if(usernames[i].equals(userName)) continue;
                                    else contactsAdapter.addContact(new Contact(usernames[i], getDrawable(R.drawable.send_button)));
                                }
                            }else Toast.makeText(ContactsActivity.this, R.string.zero_contacts, Toast.LENGTH_SHORT).show();
                        }
                    });

                }catch(JSONException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();

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

    /* Parsing username from sessionId */
    private void parseUsernameFromSessionID(String sessionId){
        userName = sessionId.substring(0, sessionId.indexOf('-'));
        Log.d("USERNAME: ", userName);
    }
}
