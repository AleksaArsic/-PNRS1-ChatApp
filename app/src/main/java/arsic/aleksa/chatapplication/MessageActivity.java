package arsic.aleksa.chatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MessageActivity extends AppCompatActivity {

    private Button btnLogOut, btnSend;
    private EditText Message;
    private TextView textViewContactName;

    private ListView listViewMessages;
    private MessageAdapter messageAdapter = new MessageAdapter(this);

    /* Variables for getting Contact Name from ContactsActivity */
    private Bundle bundle = null;
    private String contactName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        btnLogOut = findViewById(R.id.MessageLogOutBtn);
        btnSend = findViewById(R.id.MessageSendBtn);
        Message = findViewById(R.id.MessageEditText);
        textViewContactName = findViewById(R.id.MessageContactName);

        listViewMessages = findViewById(R.id.MessageListView);
        listViewMessages.setAdapter(messageAdapter);

        /* Get contact name from ContactsActivity Bundle Extra */
        bundle = getIntent().getExtras();
        contactName = bundle.getString("ContactName");

        textViewContactName.setText(contactName);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageIsEmpty()){
                    Toast.makeText(getApplicationContext(), getString(R.string.emptyMsg), Toast.LENGTH_LONG).show();
                }else{
                    messageAdapter.addMessage(new MessageRow(Message.getText().toString()));

                    listViewMessages.setSelection(listViewMessages.getAdapter().getCount() - 1);

                    Toast.makeText(getApplicationContext(), getString(R.string.sentMsg), Toast.LENGTH_LONG).show();
                    Message.setText(null);
                }
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainAcitvity();
            }
        });
    }

    private boolean messageIsEmpty(){
        String message = this.Message.getText().toString();

        if(message.isEmpty()) return true;
        else return false;
    }

    private void toMainAcitvity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
