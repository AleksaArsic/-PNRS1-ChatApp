package arsic.aleksa.chatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btnLogIn, btnRegister;
    private EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogIn = findViewById(R.id.LoginBtn);
        btnRegister = findViewById(R.id.RegisterBtn);

        username = findViewById(R.id.LoginUsername);
        password = findViewById(R.id.LoginPassword);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!requiredFieldsLogInCheck()){
                    username.setHint(Html.fromHtml(getString(R.string.username) + " <font color=\"#ff0000\">" + "* " + "</font>"));
                    password.setHint(Html.fromHtml(getString(R.string.password) + " <font color=\"#ff0000\">" + "* " + "</font>"));
                    Toast.makeText(getApplicationContext(), getString(R.string.errorMsgLogIn), Toast.LENGTH_LONG).show();
                }else{
                    launchContactsActivity();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchRegisterActivity();
            }
        });

        // Extra for exiting application from any activity
        if(getIntent().getBooleanExtra("EXTRA", false)){
            finish();
            return;
        }

    }

    private void launchContactsActivity(){
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    private void launchRegisterActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private boolean requiredFieldsLogInCheck(){
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();

        if((username.length() < 3) || (password.length() < 6)) return false;
        else return true;
    }
}
