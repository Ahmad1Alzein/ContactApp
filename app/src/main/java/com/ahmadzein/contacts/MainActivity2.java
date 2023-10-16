package com.ahmadzein.contacts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    TextView name,phoneNb , email;
    ImageButton back,delete,phone,edit;
    int profileNb;
    Intent intent;
    DatabaseHandler dbHandler;
    Contact c;
    static final int  REQUEST_PHONE_CALL=1; //min 3indi
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        edit=findViewById(R.id.edit_btn);
        name=findViewById(R.id.namePreview);
        phoneNb=findViewById(R.id.nbPreview);
        email=findViewById(R.id.emailPreview);
        back=findViewById(R.id.info_back_btn);
        delete = findViewById(R.id.delete_btn);
        phone=findViewById(R.id.telephone_btn);
        intent = getIntent();
        dbHandler= new DatabaseHandler(this);

        /**************change color of  task description when clicking the app switching button*****************/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String title = getString(R.string.app_name);
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_ic_foreground);
            int color = getResources().getColor(R.color.primary_color);
            setTaskDescription(new ActivityManager.TaskDescription(title, icon, color));
        }
        /**********/

        fillData(); //y3abi ldata bil activity

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:"+c.getPhoneNumber());
                Intent call = new Intent(Intent.ACTION_CALL,uri);
                PackageManager manager = getPackageManager();
                List<ResolveInfo> activities= manager.queryIntentActivities(call,PackageManager.MATCH_DEFAULT_ONLY);
                if(activities.size()>0){
                    //check permission after needed app found
                    if (ContextCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) { //2ino ma me3i lpermission
                        Toast.makeText(getApplicationContext(),"Need call permission",Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);//2itha nrafadet m3sh byesma7li 2itlon
                    }
                    else{
                        startActivity(call);
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"No call app found",Toast.LENGTH_SHORT).show();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity2.this);
                alert.setMessage("This contact will be deleted.");
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHandler.deleteContacts(c.getId());
                       finish();
                    }
                });
                alert.setCancelable(false);
                alert.create().show();
            }

        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(MainActivity2.this,MainActivity3.class);
                edit.putExtra("contactID",profileNb); //beb3at ra2mo bas
                startActivity(edit);
            }
        });

    }
    public void fillData(){
        profileNb = intent.getIntExtra("contactId",0);
        c = dbHandler.getContact(profileNb);
        name.setText(c.getName());
        phoneNb.setText(c.getPhoneNumber());
        if(!c.getEmail().equals("")) {
            email.setText(c.getEmail());
        }
        else{
            email.setText(getString(R.string.noEmail));
        }
    }

    @Override
    public void onRestart(){ //2itha fitt 3l edit rj3 kb2t back
        super.onRestart();
        fillData();
    }
}