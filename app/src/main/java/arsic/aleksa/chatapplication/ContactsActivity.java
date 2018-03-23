package arsic.aleksa.chatapplication;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsActivity extends AppCompatActivity {


    private Button btnLogOut;
    private TextView firstContact;

    private boolean backPressedOnce = false;

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
        firstContact = findViewById(R.id.FirstContact);


        firstContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMessageActivity();
            }
        });

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
}
