package com.example.sos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sos.sql.DatabaseHelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = LoginActivity.this;

    private EditText textInputEditTextEmail;
    private EditText textInputEditTextPassword;

    private Button appCompatButtonLogin;
    private Button appCompatButtonGoSignUp;

    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin,logout =false;

    private String username,password;

    private DatabaseHelper databaseHelper;
    static final Integer LOCATION = 0x1;
    static final Integer CALL = 0x2;


    public LoginActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        initViews();
        initListeners();
        initObjects();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            logout = extras.getBoolean("logout");

        }
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

if(logout)
{
    loginPrefsEditor.clear();
    loginPrefsEditor.commit();
}
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {

                  textInputEditTextEmail.setText(loginPreferences.getString("username", ""));
                  textInputEditTextPassword.setText(loginPreferences.getString("password", ""));
                  saveLoginCheckBox.setChecked(true);
                  verifyFromSQLite();

        }




    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        }
    }

    //This method is to initialize views
    private void initViews() {

        textInputEditTextEmail = (EditText) findViewById(R.id.enterEmail);
        textInputEditTextPassword = (EditText) findViewById(R.id.enterPassword);

        appCompatButtonLogin = (Button) findViewById(R.id.buttonLogin);
        appCompatButtonGoSignUp = (Button) findViewById(R.id.buttonGoSignUp);

        saveLoginCheckBox = (CheckBox) findViewById(R.id.checkLogin);

    }

    //This method is to initialize listeners
    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(this);
        appCompatButtonGoSignUp.setOnClickListener(this);

    }

    //This method is to initialize objects to be used
    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textInputEditTextEmail.getWindowToken(), 0);

                username = textInputEditTextEmail.getText().toString();
                password = textInputEditTextPassword.getText().toString();

                if (saveLoginCheckBox.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("username", username);
                    loginPrefsEditor.putString("password", password);
                    loginPrefsEditor.commit();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }
                verifyFromSQLite();
                break;
            case R.id.buttonGoSignUp:
                // Navigate to RegisterActivity
                Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentRegister);
                break;
        }
    }


     //This method is to validate the input text fields and verify login credentials from SQLite
    private void verifyFromSQLite() {

        if (databaseHelper.checkUser(textInputEditTextEmail.getText().toString().trim()
                , textInputEditTextPassword.getText().toString().trim())) {
            String email= textInputEditTextEmail.getText().toString();
            String password=textInputEditTextPassword.getText().toString();
            logout=false;
            Intent accountIntent = new Intent(this,MainActivity.class);
            accountIntent.putExtra("email",email);
            startActivity(accountIntent);
            finish();

        } else {
            Toast.makeText(LoginActivity.this,getString(R.string.email_or_password_is_invalid), Toast.LENGTH_LONG).show();
        }
    }


}
