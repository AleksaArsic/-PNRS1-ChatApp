package arsic.aleksa.chatapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLData;
import java.util.Calendar;

public  class RegisterActivity extends AppCompatActivity {

    /* Layout representatives */
    private Button btnRegister, btnBirthDate;
    private EditText username, password, email;
    private EditText firstName, lastName;
    private TextView birthDate;

    /* DatePicker popup variables */
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Calendar c;
    private int mYear, mMonth, mDay;

    /* DatabaseHelper initialization */
    private mDataBaseHelper mDataBaseH;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDataBaseH = new mDataBaseHelper(this);

        btnRegister = findViewById(R.id.RegisterRegBtn);
        btnBirthDate = findViewById(R.id.BirthDateRegBtn);

        username = findViewById(R.id.RegUsername);
        password = findViewById(R.id.RegPassword);
        email = findViewById(R.id.RegEmail);

        firstName = findViewById(R.id.FirstNameReg);
        lastName = findViewById(R.id.LastNameReg);
        birthDate = findViewById(R.id.TextViewBirthDate);

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        btnBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, android.R.style.Theme_Holo_Dialog,
                        dateSetListener, mYear, mMonth, mDay);

                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if(year < 1970){
                    Toast.makeText(getApplicationContext(), getString(R.string.ancientMaster), Toast.LENGTH_LONG).show();
                }else if(year  > 2010){
                    Toast.makeText(getApplicationContext(), getString(R.string.youngMaster), Toast.LENGTH_LONG).show();
                }

                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                birthDate.setText(date);
            }
        };

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!requiredFieldsRegisterCheck()){
                    username.setHint(Html.fromHtml (getString(R.string.username) + " <font color=\"#ff0000\">" + "* " + "</font>"));
                    password.setHint(Html.fromHtml (getString(R.string.password) + " <font color=\"#ff0000\">" + "* " + "</font>"));
                    email.setHint(Html.fromHtml (getString(R.string.email)+ " <font color=\"#ff0000\">" + "* " + "</font>"));
                    Toast.makeText(getApplicationContext(), getString(R.string.errorMsgReg), Toast.LENGTH_LONG).show();
                }else{
                    /* Check if user exists in database */
                    if(uniqueUsername(username.getText().toString())) {
                        mDataBaseH.insertContact(new Contact(username.getText().toString(),
                                firstName.getText().toString(),
                                lastName.getText().toString()));
                        Toast.makeText(getApplicationContext(), getString(R.string.reg_successful), Toast.LENGTH_SHORT).show();
                        launchLoginActivity();
                    }else{
                        username.setText("");
                        username.setHint(Html.fromHtml (getString(R.string.username) + " <font color=\"#ff0000\">" + "* " + "</font>"));
                        Toast.makeText(getApplicationContext(), getString(R.string.username_exists), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean uniqueUsername(String username){
        SQLiteDatabase db = mDataBaseH.getReadableDatabase();
        String[] columns = new String[]{mDataBaseHelper.CONTACTS_COLUMN_USERNAME};

        Cursor cursor = db.query(mDataBaseHelper.CONTACTS_TABLE_NAME, columns,
                null, null, null, null, null);

        if(cursor.getCount() <= 0){
            cursor.close();
            return true;
        }

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            if(username.equals(cursor.getString(cursor.getColumnIndex(columns[0])))){
                cursor.close();
                return false;
            }
        }

        cursor.close();
        return true;
    }

    private boolean requiredFieldsRegisterCheck(){
        String username, password, email;

        username = this.username.getText().toString();
        password = this.password.getText().toString();
        email = this.email.getText().toString();

        if((username.length() < 3) || (password.length() < 6) || (email.length() < 11)) return false;
        else return true;
    }

    private void launchLoginActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
