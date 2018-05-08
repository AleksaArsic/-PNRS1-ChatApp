package arsic.aleksa.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /* Shared Preference id key*/
    public static final String ID_SHARED_PREF_KEY = "contact_id";

    /* Layout representatives */
    private Button btnLogIn, btnRegister;
    private EditText username, password;

    /* Database initialization */
    private mDataBaseHelper mDataBaseH;

    private Contact contact = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataBaseH = new mDataBaseHelper(this);

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
                }else {
                    if (userExists(username.getText().toString())) {
                        contact = mDataBaseH.readContact(username.getText().toString());

                        /* Put logged user ID to Shared Preferences */
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("arsic.aleksa.chatapplication",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(ID_SHARED_PREF_KEY, contact.getId());
                        editor.apply();

                        launchContactsActivity();
                    }else{
                        username.setText("");
                        username.setHint(Html.fromHtml(getString(R.string.username) + " <font color=\"#ff0000\">" + "* " + "</font>"));
                        Toast.makeText(getApplicationContext(), getString(R.string.username_invalid), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchRegisterActivity();
            }
        });

        /* Extra for exiting application from any activity */
        if(getIntent().getBooleanExtra("EXTRA", false)){
            finish();
            return;
        }

    }

    private boolean userExists(String username){
        SQLiteDatabase db = mDataBaseH.getReadableDatabase();
        String[] columns = new String[]{mDataBaseHelper.CONTACTS_COLUMN_USERNAME};

        Cursor cursor = db.query(mDataBaseHelper.CONTACTS_TABLE_NAME, columns,
                null, null, null, null, null);

        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            if(username.equals(cursor.getString(cursor.getColumnIndex(columns[0])))){
                cursor.close();
                return true;
            }
        }

        cursor.close();
        return false;
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
