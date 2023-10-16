package com.ahmadzein.contacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    //Data base version
    public static final int DATABASE_VERSION = 1;

    //Data base name
    public static final String DATABASE_NAME= "contactsManager";

    //Contacts table name
    public static final String TABLE_CONTACTS= "contacts";

    //contacts table columns names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PH_NO = "phoneNumber";
    public static final String KEY_EMAIL = "email";

    public DatabaseHandler(Context c){
        super(c,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) { //w2t lcreate lal database 2wl marra r7 y3mal lonCreate fa byi5la2 ltable
    String CREATE_CONTACTS_TABLE = "CREATE TABLE "+TABLE_CONTACTS+" ("+
            KEY_ID+" INTEGER PRIMARY KEY,"+
            KEY_NAME+" TEXT,"+
            KEY_PH_NO+" TEXT,"+
            KEY_EMAIL+" TEXT)";

    db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
     //bas badi 2i3mal shi t3dil 3l database b7otoh hwn wb8ayer lversion fw2, w3l run lal obj min hal DatabaseHandler
     // bshof lconstructor bil super 2ino t8yrt lversion fa b3yt lal onUpgrade
    }

    public void addContact(Contact c){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        //ma b7ot l id la2inha by default auto increment
        value.put(KEY_NAME,c.getName());
        value.put(KEY_PH_NO,c.getPhoneNumber());
        value.put(KEY_EMAIL,c.getEmail());
        //inserting in database
        db.insert(TABLE_CONTACTS,null,value);
        db.close();
    }

    //search in contacts
    public ArrayList<Contact> queryContacts(String s){
        SQLiteDatabase db = this.getReadableDatabase();
        String condition = KEY_NAME+" LIKE ? OR "+KEY_PH_NO+" LIKE ? ";
        Cursor table = db.query(TABLE_CONTACTS,new String[]{KEY_ID,KEY_NAME,KEY_PH_NO,KEY_EMAIL},condition,new String[]{"%"+s+"%","%"+s+"%"},null,null,null);
        ArrayList<Contact> contacts = new ArrayList<>();
        if (table != null && table.getCount()>0){
            table.moveToFirst();
            do {
                contacts.add(
                        new Contact(
                        Integer.parseInt(table.getString(0)), //id
                        table.getString(1), //name
                        table.getString(2), //phone
                        table.getString(3)  //email
                        )
                );

            }while(table.moveToNext());
        }
        db.close();
        return contacts;

    };

    //get all contacts
    public ArrayList<Contact> getAllContacts(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_CONTACTS;
        Cursor table = db.rawQuery(query,null);
        ArrayList<Contact> contacts = new ArrayList<>();
        if (table != null && table.getCount()>0){

            table.moveToFirst();
            do {
                        if(table.getColumnCount()==4) {
                            contacts.add(
                                    new Contact(
                                    Integer.parseInt(table.getString(0)), //id
                                    table.getString(1), //name
                                    table.getString(2), //phone
                                    table.getString(3)  //email
                                    )
                            );
                        }
                        else {
                            System.out.println("HEYYYYYYYYYY count = "+table.getColumnCount());
                        }

            }while(table.moveToNext());
        }
        db.close();
        return contacts;
    }

    //get contact with id
    public Contact getContact(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String condition= "id=?";
        Cursor table = db.query(TABLE_CONTACTS,new String[]{KEY_ID,KEY_NAME,KEY_PH_NO,KEY_EMAIL},condition,new String[]{""+id},null,null,null);
        if(table != null && table.getCount()>0){
            table.moveToFirst();
            Contact c= new Contact(
                    Integer.parseInt(table.getString(0)), //id
                    table.getString(1), //name
                    table.getString(2), //phone
                    table.getString(3)  //email
            );
            db.close();
            return c;
        }
        db.close();
        return null;
    };


    //delete contacts
    public int deleteContacts(int id){ //return nb of deleted rows
        SQLiteDatabase db = this.getWritableDatabase();
        String condition = KEY_ID+"=?";
        int affected= db.delete(TABLE_CONTACTS,condition,new String[]{""+id});
        db.close();
        return affected;
    };

    //edit contacts
    public int editContact (Contact c){ //fiha l new values
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME,c.getName());
        values.put(KEY_EMAIL,c.getEmail());
        values.put(KEY_PH_NO,c.getPhoneNumber());
        int affected = db.update(TABLE_CONTACTS,values,KEY_ID+"=?",new String[]{""+c.getId()});
        db.close();
        return affected;
    };

}
