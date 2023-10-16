package com.ahmadzein.contacts;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity3 extends AppCompatActivity {

    ImageButton back, save;
    EditText fname, lname, phoneNb, email;
    SharedPreferences data;
    SharedPreferences.Editor editor;
    LinearLayout container;
    TextView title;
    ImageView img;
    int accessType;
    DatabaseHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        fname = findViewById(R.id.firstNameInput);
        lname = findViewById(R.id.lastNameInput);
        phoneNb = findViewById(R.id.numberInput);
        email = findViewById(R.id.emailInput);
        back = findViewById(R.id.add_back_btn);
        save = findViewById(R.id.saveContactBtn);
        data = getSharedPreferences(getString(R.string.data), Context.MODE_PRIVATE);
        editor = data.edit();
        container = findViewById(R.id.container);
        title = findViewById(R.id.access_type);
        img = findViewById(R.id.iconImg);
        dbHandler= new DatabaseHandler(this);

        /**************change color of  task description when clicking the app switching button*****************/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String title = getString(R.string.app_name);
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_ic_foreground);
            int color = getResources().getColor(R.color.primary_color);
            setTaskDescription(new ActivityManager.TaskDescription(title, icon, color));
        }
        /**********/

        Intent edit = getIntent();
        int contactID = edit.getIntExtra("contactID", -1);
        if (contactID != -1) {
            accessType = 1; //Zero: new Contact One: edit Contact
            title.setText(getString(R.string.accessEdit));
            img.setImageResource(R.drawable.ic_editting_logo);
            Contact c = dbHandler.getContact(contactID);
            String[] name = c.getName().split(" ");
            fname.setText(name[0]);
            if (name.length == 2) {
                lname.setText(name[1]);
            }
            phoneNb.setText(c.getPhoneNumber());
            email.setText(c.getEmail());
        } else {

            accessType = 0; // bkon feyit 3br intent l main screen
            title.setText(getString(R.string.accessAdd));
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((fname.getText().toString().equals("") && lname.getText().toString().equals("")) || phoneNb.getText().toString().equals("")) {
                    Toast.makeText(getBaseContext(), "Required name and phone Nb", Toast.LENGTH_SHORT).show();
                } else {
                    String name=fname.getText().toString() + " " + lname.getText().toString();
                    Contact c = new Contact(name, phoneNb.getText().toString(),email.getText().toString());
                    if (accessType == 0) { //new contact
                        dbHandler.addContact(c);
                    } else {
                        c.setId(contactID);
                        dbHandler.editContact(c);
                    }

                }
                save.setClickable(false);
                save.setImageResource(R.drawable.ic_save_btn_disabled);
                for (int i = 0; i < container.getChildCount(); i++) {
                    if (container.getChildAt(i) instanceof EditText) {
                        EditText b = (EditText) container.getChildAt(i);
                        b.setEnabled(false);
                        b.setTextColor(getResources().getColor(R.color.savedColor));
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

