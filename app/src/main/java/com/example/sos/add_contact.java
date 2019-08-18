package com.example.sos;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.sos.modal.Contact;
import com.example.sos.sql.DatabaseHelper;

public class add_contact extends AppCompatActivity implements View.OnClickListener {

    private final AppCompatActivity activity = add_contact.this;

    private EditText textInputEditTextContactName;
    private EditText textInputEditTextContactNumber;

    private Button appCompatButtonSave;

    private DatabaseHelper databaseHelper;
    private Contact contact;
    String email;
    Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email=extras.getString("email");
        }

        initViews();
        initListeners();
        initObjects();

    }

    private void initViews() {

        textInputEditTextContactName = (EditText) findViewById(R.id.editContactName);
        textInputEditTextContactNumber = (EditText) findViewById(R.id.editContactNumber);

        appCompatButtonSave = (Button) findViewById(R.id.buttonSave);
    }

    private void initListeners() {
        appCompatButtonSave.setOnClickListener(this);
    }

    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);
        contact = new Contact();
    }

    @Override
    public void onClick(View v) {
        postDataToSQLite();
        Intent intent = new Intent(this,contacts.class);
        intent.putExtra("email",email);
        startActivity(intent);
    }

    private void postDataToSQLite() {

        if (!databaseHelper.checkContact(textInputEditTextContactNumber.getText().toString().trim()) &&
                textInputEditTextContactNumber.getText().toString().length() >= 10 &&
                textInputEditTextContactNumber.getText().toString().length() < 12) {

            contact.setContactName(textInputEditTextContactName.getText().toString().trim());
            contact.setContactNumber(textInputEditTextContactNumber.getText().toString().trim());
            contact.setContactEmail(email);

            databaseHelper.addContact(contact);

            Toast.makeText(add_contact.this, "Saved!", Toast.LENGTH_LONG).show();
            emptyInputEditText();
        }

        else if(textInputEditTextContactNumber.getText().toString().length() < 10){
            Toast.makeText(add_contact.this, "Phone number only consists 10 or 11 character", Toast.LENGTH_LONG).show();
        }

        else if(textInputEditTextContactNumber.getText().toString().length() > 11){
            Toast.makeText(add_contact.this, "Phone number only consists 10 or 11 character", Toast.LENGTH_LONG).show();
        }

        else {
            Toast.makeText(add_contact.this, "Number already exists!", Toast.LENGTH_LONG).show();
        }

    }

    private void emptyInputEditText() {
        textInputEditTextContactName.setText(null);
        textInputEditTextContactNumber.setText(null);
    }

}

