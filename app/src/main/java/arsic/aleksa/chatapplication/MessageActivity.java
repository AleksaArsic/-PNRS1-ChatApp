package arsic.aleksa.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MessageActivity extends AppCompatActivity {

    /* Layout representatives */
    private Button btnLogOut, btnSend;
    private EditText Message;
    private TextView textViewContactName;
    private ImageButton btnRefresh;

    private ListView listViewMessages;
    private MessageAdapter messageAdapter = new MessageAdapter(this);

    /* Variables for getting Contact Name from ContactsActivity */
    private Bundle bundle = null;
    public String contactName = null;
    private int contactID = 0;

    /* Shared Preference to read logged user ID */
    private SharedPreferences sharedPref;
    private int userID = 0;
    private String sessionID;
    private String loggedUser;

    /* Database initialization */
    private mDataBaseHelper mDataBaseH = null;

    private HttpHelper httpHelper;
    private Handler handler;

    private Message[] messages;
    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mDataBaseH = new mDataBaseHelper(this);

        btnLogOut = findViewById(R.id.MessageLogOutBtn);
        btnSend = findViewById(R.id.MessageSendBtn);
        btnRefresh = findViewById(R.id.RefreshMessages);
        Message = findViewById(R.id.MessageEditText);
        textViewContactName = findViewById(R.id.MessageContactName);

        listViewMessages = findViewById(R.id.MessageListView);
        listViewMessages.setAdapter(messageAdapter);

        /* Get contact name and contact id from ContactsActivity Bundle Extra */
        bundle = getIntent().getExtras();
        contactName = bundle.getString("ContactName");
        contactID = bundle.getInt("ContactID");

        /* Get user id from shared preferences */
        /* Get sessionId from Shared Preferences */
        sharedPref = getApplicationContext().getSharedPreferences("aleksa.arsic.chatapplication",
                Context.MODE_PRIVATE);
        sessionID = sharedPref.getString(MainActivity.SESSION_ID, null);
        //userID = sharedPref.getInt(ID_SHARED_PREF_KEY, 0);

        /* Parse logged users username from session id */
        loggedUser = parseUsernameFromSessionID();

        Log.d("SESSIONID: ", sessionID);

        textViewContactName.setText(contactName);

        /* HTTP helper class */
        httpHelper = new HttpHelper();
        /* Used for handling UI components in separate thread */
        handler = new Handler();

        /* Database handling */
        /*
        Message[] messages;
        if((messages = mDataBaseH.readMessages(userID, contactID)) != null){
            for (int i = 0; i < messages.length; i++){
                messageAdapter.addMessage(messages[i]);
            }
        }
        */

        /* get messages from HTTP server */
        getMessages();
        listViewMessages.setSelection(listViewMessages.getAdapter().getCount() - 1);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Refresh messages */
                Toast.makeText(MessageActivity.this, getString(R.string.refresh_messages), Toast.LENGTH_SHORT).show();
                messageAdapter.clearAdapter();
                getMessages();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageIsEmpty()){
                    Toast.makeText(getApplicationContext(), getString(R.string.emptyMsg), Toast.LENGTH_LONG).show();
                }else{
                    /* Database handling
                    Message message = new Message(Message.getText().toString(), userID, contactID);

                    mDataBaseH.insertMessage(message);
                    messageAdapter.addMessage(message);
                    listViewMessages.setSelection(listViewMessages.getAdapter().getCount() - 1);
                    */

                    /* Make new Thread object
                    *  Use of tread should be applied whenever we have some heavy load that should be done
                    *  and could cause our program to work slowly
                    */
                    /* When an object implementing Runnable interface is used to create new Thread
                     * it causes objects run method to be executed in that new Thread
                     * Runnable describes what should be done in new Thread
                     * Runnable overrides our new Threads run method
                     * Runnable is interface
                     */
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject jsonObject = new JSONObject();

                            /* JSONException is thrown when to indicate a JSON API problem */
                            try {
                                jsonObject.put("receiver", contactName);
                                jsonObject.put("data", Message.getText().toString());

                                final boolean success = httpHelper.postJsonObjectWithSessionID(MainActivity.SERVER_URL + MainActivity.MESSAGE,
                                        jsonObject, sessionID);

                                /* Handler lets us update UI elements from new Thread  */
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(success){
                                            arsic.aleksa.chatapplication.Message message = new Message(Message.getText().toString(), parseUsernameFromSessionID(), contactName, 1, 0 );

                                            messageAdapter.addMessage(message);
                                            listViewMessages.setSelection(listViewMessages.getAdapter().getCount() - 1);

                                            Toast.makeText(getApplicationContext(), getString(R.string.sentMsg), Toast.LENGTH_SHORT).show();
                                            Message.setText(null);
                                        }else{
                                            Message.setText(null);
                                            Toast.makeText(MessageActivity.this, getString(R.string.message_failed), Toast.LENGTH_SHORT).show();
                                        }
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
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final JSONObject jsonObject = new JSONObject();

                        try{
                            jsonObject.put("sessionid", sessionID);

                            final boolean success = httpHelper.postJsonObjectWithSessionID(MainActivity.SERVER_URL + MainActivity.LOGOUT,
                                    jsonObject, sessionID);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(success){
                                        Toast.makeText(MessageActivity.this, R.string.logout_successful, Toast.LENGTH_SHORT).show();
                                        toMainActivity();
                                    }else Toast.makeText(MessageActivity.this, httpHelper.responseMessage, Toast.LENGTH_SHORT).show();
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

    private void getMessages(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                JSONArray jsonArray = new JSONArray();

                try{
                    jsonArray = httpHelper.getJSONArrayFromURL(MainActivity.SERVER_URL + MainActivity.MESSAGE +
                            "/" +  contactName, sessionID);


                    if(jsonArray.length() != 0) {

                        messages = new Message[jsonArray.length()];

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            arsic.aleksa.chatapplication.Message message;

                            if(loggedUser.equals(jsonObject.getString("sender"))){
                                messages[i] = new Message(jsonObject.getString("data"), parseUsernameFromSessionID(), contactName, 1, 0);
                            }else{
                                messages[i] = new Message(jsonObject.getString("data"), contactName, parseUsernameFromSessionID(),  0, 1);
                            }

                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                for(int i = 0; i < messages.length; i++){
                                    messageAdapter.addMessage(messages[i]);
                                    listViewMessages.setAdapter(messageAdapter);
                                }
                            }
                        });

                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private String parseUsernameFromSessionID(){
        return sessionID.substring(0, sessionID.indexOf('-'));
    }

    private boolean messageIsEmpty(){
        String message = this.Message.getText().toString();

        if(message.isEmpty()) return true;
        else return false;
    }

    private void toMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
