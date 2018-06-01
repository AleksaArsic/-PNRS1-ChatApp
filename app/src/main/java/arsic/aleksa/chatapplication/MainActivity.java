package arsic.aleksa.chatapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    /* Shared Preference id key*/
    public static final String ID_SHARED_PREF_KEY = "contact_id";
    public static final String SESSION_ID = "sessionid";

    /* HTTP Server information */
    public static final String SERVER_URL = "http://18.205.194.168:80";
    public static final String REGISTRATION = "/register";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String CONTACTS = "/contacts";
    public static final String MESSAGE = "/message";
    public static final String FROMSERVICE = "/getfromservice";

    private Handler handler;
    private HttpHelper httpHelper;

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

        /* Used for handling UI components in seperate thread */
        handler = new Handler();

        /* HTTP helper class */
        httpHelper = new HttpHelper();




        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!requiredFieldsLogInCheck()){
                    username.setHint(Html.fromHtml(getString(R.string.username) + " <font color=\"#ff0000\">" + "* " + "</font>"));
                    password.setHint(Html.fromHtml(getString(R.string.password) + " <font color=\"#ff0000\">" + "* " + "</font>"));
                    Toast.makeText(getApplicationContext(), getString(R.string.errorMsgLogIn), Toast.LENGTH_LONG).show();
                }else {
                    /* Database handling */
                    /*
                    if (userExists(username.getText().toString())) {
                        contact = mDataBaseH.readContact(username.getText().toString());

                        // Put logged user ID to Shared Preferences
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

                    */

                    // Put logged user ID to Shared Preferences
                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("arsic.aleksa.chatapplication",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(ID_SHARED_PREF_KEY, 1);
                    editor.apply();

                    /* HTTP server POST username, password for login */
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject jsonObject = new JSONObject();

                            try{
                                jsonObject.put("username", username.getText().toString());
                                jsonObject.put("password", password.getText().toString());

                                final boolean success = httpHelper.postJsonObject(SERVER_URL + LOGIN, jsonObject);

                                final String responseMessage = httpHelper.responseMessage;

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(success){
                                            Toast.makeText(MainActivity.this, R.string.login_successful, Toast.LENGTH_SHORT).show();

                                            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("aleksa.arsic.chatapplication",
                                                    Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString(SESSION_ID, httpHelper.sessionId);
                                            editor.apply();

                                            launchContactsActivity();
                                        }
                                        else{
                                            Toast.makeText(MainActivity.this, responseMessage, Toast.LENGTH_SHORT).show();
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
