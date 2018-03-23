package arsic.aleksa.chatapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

public  class RegisterActivity extends AppCompatActivity {

    private Button btnRegister, btnBirthDate;
    private EditText username, password, email;
    private TextView birthDate;

    // DatePicker popup variables
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Calendar c;
    private int mYear, mMonth, mDay;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.RegisterRegBtn);
        btnBirthDate = findViewById(R.id.BirthDateRegBtn);

        username = findViewById(R.id.RegUsername);
        password = findViewById(R.id.RegPassword);
        email = findViewById(R.id.RegEmail);

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
                    launchContactsActivity();
                }
            }
        });
    }

    private boolean requiredFieldsRegisterCheck(){
        String username, password, email;

        username = this.username.getText().toString();
        password = this.password.getText().toString();
        email = this.email.getText().toString();

        if((username.length() < 3) || (password.length() < 6) || (email.length() < 11)) return false;
        else return true;
    }

    private void launchContactsActivity(){
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }
}
