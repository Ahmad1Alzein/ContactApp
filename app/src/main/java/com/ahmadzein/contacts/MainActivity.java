package com.ahmadzein.contacts;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import com.ahmadzein.contacts.databinding.ActivityMainBinding;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.security.Permission;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ListView contactsView;
    private EditText searchInput;
    ArrayList<String> contacts; //bas l2ism bo3rdoh
    ArrayList<Contact> archive; //2o7tofith bil contact kilha 3alad l id ydal me3i
    ArrayAdapter<String> adapter;
    private boolean doubleBackToExitPressedOnce;
    DatabaseHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        dbHandler= new DatabaseHandler(this);


        /**************change color of  task description when clicking the app switching button*****************/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String title = getString(R.string.app_name);
                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_ic_foreground);
            int color = getResources().getColor(R.color.primary_color);
            setTaskDescription(new ActivityManager.TaskDescription(title, icon, color));
        }
         /**********/


        //initializing
        contactsView= findViewById(R.id.contacts_view);
        searchInput=findViewById(R.id.search_input);
        contacts = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,contacts);
        contactsView.setAdapter(adapter);
        doubleBackToExitPressedOnce = false;

        //fill array and update list
        refresh();

        //list view listener
        contactsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent viewContact = new Intent(MainActivity.this, MainActivity2.class);
                viewContact.putExtra("contactId",archive.get(position).getId()); //ba3atet l id
                startActivity(viewContact);
            }
        });

        //search listener
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            contacts.clear();
            archive = dbHandler.queryContacts(s.toString()); //lcontact kemli hwn
            for(int i=0;i<archive.size();i++){
                contacts.add(archive.get(i).getName()); //bizid bas lname hwn
            }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_search){
            if(searchInput.getVisibility()==EditText.GONE){
                searchInput.setVisibility(EditText.VISIBLE);
            }
            else{
                searchInput.setVisibility(EditText.GONE);

            }
        }else if(item.getItemId()==R.id.action_add){
            Intent go = new Intent(this,MainActivity3.class);
            startActivity(go);
            //yraje3 llist view 2ino 3lyha kil lcontacts
        }

        return super.onOptionsItemSelected(item);
    }

    public void refresh(){
        //fill array and update list
        archive = dbHandler.getAllContacts(); //me3i kill lcontacts hwn
        contacts.clear();
        for(int i=0;i<archive.size();i++){
          contacts.add(archive.get(i).getName());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRestart(){ //bas yerja3 lahawn
        super.onRestart();
        refresh();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "click BACK twice to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}