package com.example.mycontactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextThemeWrapper;

import java.sql.SQLException;


public class ContactDataSource {

    private SQLiteDatabase database;
    private ContactDBHelper dbHelper;

    public ContactDataSource (Context context) {
        dbHelper = new ContactDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean insertContact(Contact c) {
        boolean didSucceed = false;
        try {

            ContentValues initialValues = new ContentValues(); //Object used to store a set of keys/value pairs that are used to assign contact data to the correct field in the table

            initialValues.put("contactname", c.getContactName()); //value retrieved from the Contact object and place into the ContentValues Object
            initialValues.put("streetaddress", c.getStreetAddress()); //value retrieved from the Contact object and place into the ContentValues Object
            initialValues.put("city", c.getCity()); //value retrieved from the Contact object and place into the ContentValues Object
            initialValues.put("state", c.getState()); //value retrieved from the Contact object and place into the ContentValues Object
            initialValues.put("zipcode", c.getZipCode()); //value retrieved from the Contact object and place into the ContentValues Object
            initialValues.put("phonenumber", c.getPhoneNumber()); //value retrieved from the Contact object and place into the ContentValues Object
            initialValues.put("cellnumber",c.getCellNumber()); //value retrieved from the Contact object and place into the ContentValues Object
            initialValues.put("email", c.getEMail()); //value retrieved from the Contact object and place into the ContentValues Object
            initialValues.put("birthday", String.valueOf(c.getBirthday().getTimeInMillis())); //SQLit does not support storing data as dates directly; value retrieved from the Contact object and place into the ContentValues Object

            didSucceed = database.insert("contact", null, initialValues) > 0; //insert method returns the number of records successfully inserted

        } catch (Exception e) {
            //do nothing -will return false if there is an exception
        }
        return didSucceed;
    }

    public boolean updateContact(Contact c) {
        boolean didSucceed = false;
        try{
            //update procedure needs the contact's ID to crrectly update the able
            Long rowID = (long) c.getContactID();
            ContentValues updateValues = new ContentValues();

            updateValues.put("contactname", c.getContactName()); //same as InsertContact Method but updates the record stored instead of creating a new record
            updateValues.put("streetaddress", c.getStreetAddress());
            updateValues.put("city", c.getCity());
            updateValues.put("state", c.getState());
            updateValues.put("zipcode", c.getZipCode());
            updateValues.put("phonenumber", c.getPhoneNumber());
            updateValues.put("cellnumber",c.getCellNumber());
            updateValues.put("email", c.getEMail());
            updateValues.put("birthday", String.valueOf(c.getBirthday().getTimeInMillis()));

            didSucceed = database.update("contact", updateValues, "_id=" + rowID, null) > 0; //update method returns the number of records successfully updated

        }catch (Exception e) {
            //do nothing -will return false if there is an exception
        }


        return didSucceed;
    }

    public int getLastContactID() {
        int lastID = -1;
        try{
            String query = "Select MAX(_id) from contact"; //query to get the maximum value for the _id field in the contact table
            Cursor cursor = database.rawQuery(query, null); //cursor declared and assigned to hold the results of the execution of the query
                                                                        //cursor is an object that is used to hold and move through the results of a query
            cursor.moveToFirst(); //cursor is told to move to the first record in the returned data
            lastID = cursor.getInt(0); //max id is retrieved from the record set
            cursor.close(); //cursor is closed
        }catch (Exception e) {
            lastID = -1;
        }
        return lastID;
    }



}
