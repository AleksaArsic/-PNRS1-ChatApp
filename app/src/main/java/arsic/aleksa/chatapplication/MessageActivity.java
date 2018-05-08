package arsic.aleksa.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MessageActivity extends AppCompatActivity {

    /* Shared Preference id key*/
    public static final String ID_SHARED_PREF_KEY = "contact_id";

    /* Layout representatives */
    private Button btnLogOut, btnSend;
    private EditText Message;
    private TextView textViewContactName;

    private ListView listViewMessages;
    private MessageAdapter messageAdapter = new MessageAdapter(this);

    /* Variables for getting Contact Name from ContactsActivity */
    private Bundle bundle = null;
    private String contactName = null;
    private int contactID = 0;

    /* Shared Preference to read logged user ID */
    SharedPreferences sharedPref;
    int userID = 0;

    /* Database initialization */
    mDataBaseHelper mDataBaseH = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mDataBaseH = new mDataBaseHelper(this);

        btnLogOut = findViewById(R.id.MessageLogOutBtn);
        btnSend = findViewById(R.id.MessageSendBtn);
        Message = findViewById(R.id.MessageEditText);
        textViewContactName = findViewById(R.id.MessageContactName);

        listViewMessages = findViewById(R.id.MessageListView);
        listViewMessages.setAdapter(messageAdapter);

        /* Get contact name and contact id from ContactsActivity Bundle Extra */
        bundle = getIntent().getExtras();
        contactName = bundle.getString("ContactName");
        contactID = bundle.getInt("ContactID");

        /* Get user id from shared preferences */
        sharedPref = getApplicationContext().getSharedPreferences("arsic.aleksa.chatapplication", Context.MODE_PRIVATE);
        userID = sharedPref.getInt(ID_SHARED_PREF_KEY, 0);

        textViewContactName.setText(contactName);

        Message[] messages;
        if((messages = mDataBaseH.readMessages(userID, contactID)) != null){
            for (int i = 0; i < messages.length; i++){
                messageAdapter.addMessage(messages[i]);
            }
        }
        listViewMessages.setSelection(listViewMessages.getAdapter().getCount() - 1);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageIsEmpty()){
                    Toast.makeText(getApplicationContext(), getString(R.string.emptyMsg), Toast.LENGTH_LONG).show();
                }else{
                    Message message = new Message(Message.getText().toString(), userID, contactID);

                    mDataBaseH.insertMessage(message);
                    messageAdapter.addMessage(message);
                    listViewMessages.setSelection(listViewMessages.getAdapter().getCount() - 1);

                    Toast.makeText(getApplicationContext(), getString(R.string.sentMsg), Toast.LENGTH_SHORT).show();
                    Message.setText(null);
                }
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainActivity();
            }
        });
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
