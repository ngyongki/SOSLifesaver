package com.example.sos;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sos.modal.Contact;
import com.example.sos.sql.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener  {
    Dialog myDialog;
    Button helpButton;
    boolean check_siren,check_location;
    String fire_number,police_number,ambulance_number,Emergency_message,email;
    private List<Contact> list;
    DatabaseHelper databaseHelper ;
    private ContactAdapter contactAdapter;
    private AppCompatActivity activity = MainActivity.this;
    TextView textViewPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view );
        BottomNavigationView topView = findViewById(R.id.nav_view_top) ;

        navView.setOnNavigationItemSelectedListener(this);
        topView.setOnNavigationItemSelectedListener(this);
        myDialog = new Dialog(this );

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fire_number = extras.getString("fire");
            police_number = extras.getString("police");
            ambulance_number = extras.getString("ambulance");
            Emergency_message=extras.getString("message");
            check_siren=extras.getBoolean("check_siren");
            check_location=extras.getBoolean("check_location");
            email=extras.getString("email");
        }

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.siren);
        helpButton = (Button) findViewById(R.id.helpButton);


        helpButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                float percent = 1.0f;
                int seventyVolume = (int) (maxVolume*percent);
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, seventyVolume, 0);
                if(check_location==true)
                {
                    sendSMS();
                }

                if (check_siren == true) {
                    if (mp.isPlaying()) {
                        mp.pause();
                    } else

                        mp.start();

                }
            }

        });
    }

    private void sendSMS() {


        // create class object
        GPStracker gps = new GPStracker(MainActivity.this);
        double longitude = 0.0, latitude = 0.0;
        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            // \n is for new line
            String message = "My Location is - "+"https://www.google.com/maps/search/?api=1&query=" + latitude+"," +longitude + "\nMessage :" +Emergency_message;
            list = new ArrayList<>();
            contactAdapter = new ContactAdapter(list);
            databaseHelper = new DatabaseHelper(activity  );
            list.addAll(databaseHelper.getAllContact(email));


            int contact_counts = databaseHelper.getContactCount(email);

            int size=list.size();


               final StringBuffer buffer = new StringBuffer("smsto:");

              for (int i = 0; i < size; i++) {

                 buffer.append(list.get(i).getContactNumber());

                 buffer.append(";");
              }


            Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse(buffer.toString()));
            smsIntent.putExtra("sms_body", message);
            startActivity(smsIntent);


        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }




    public void openMainActivity()
    {  Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("email",email);
        startActivity(intent);
    }
    public void openContact()
    {  Intent intent = new Intent(this,contacts.class);
        intent.putExtra("email",email);
        startActivity(intent);
    }
    public void openSetting()
    {  Intent intent = new Intent(this,Setting.class);
        intent.putExtra("email",email);
        startActivity(intent);
    }

    public void openRescueFragment()

    {  Intent intent = new Intent(this,rescuefragment.class);
        intent.putExtra("fire",fire_number);
        intent.putExtra("police",police_number);
        intent.putExtra("ambulance",fire_number);
        intent.putExtra("email",email);
        startActivity(intent);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId())
        {
            case R.id.navigation_home :

                openMainActivity();
                break;
            case R.id.navigation_rescue:
                openRescueFragment();
                break;
            case R.id.navigation_contacts:
               openContact();
                break;
            case R.id.navigation_setting:
                openSetting();
                break;


        }
        return true;
}


}
