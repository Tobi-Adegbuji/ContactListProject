package com.example.mycontactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextThemeWrapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;


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
            //test
            initialValues.put("bestFriendForever", c.getBestFriendForever());
            //test

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
            updateValues.put("bestFriendForever", c.getBestFriendForever());

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


    public boolean updateContactAddress(Contact c, ContactAddress ca) {
        boolean didSucceed = false;

        try {
            Long rowID = (long) c.getContactID();

            ContentValues updateAddress = new ContentValues();

            updateAddress.put("streetadress", c.getStreetAddress());
            didSucceed = database.update("contact", updateAddress, "_id=" + rowID, null) > 0;


        }catch (Exception e) {
            //do nothing - will return false if there is an exception
        }

        return  didSucceed;

    }

    public ArrayList<String> getContactName(){
        ArrayList<String> contactNames = new ArrayList<>();
        try{
            String query = "Select contactname from contact";
            Cursor cursor = database.rawQuery(query, null); //holds the results from the query

            cursor.moveToFirst(); //moves to the first record held in the cursor
            while (!cursor.isAfterLast()) { //continues the loop while the cursor is not after the last record
                contactNames.add(cursor.getString(0)); //query result is added to contactName
                cursor.moveToNext(); //moves to the next record held in the cursor
            }
            cursor.close();
        }
        catch (Exception e) {
            contactNames = new ArrayList<String>(); //ArrayList is set to a new empty ArrayList in case it crashes while running
        }
        return contactNames;
    }

    public ArrayList<Contact> getContacts(String sortField, String sortOrder) {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        try{
            String query = "SELECT * FROM contact ORDER BY " + sortField + " " + sortOrder;
            Cursor cursor = database.rawQuery(query, null); //holds the results from the query

            Contact newContact; //new Contact object is declared
            cursor.moveToFirst(); //moves to the first record held in the cursor
            while(!cursor.isAfterLast()){ //continues the loop while the cursor is not after the last record
                newContact = new Contact(); //new contact object initialized
                newContact.setContactID(cursor.getInt(0));
                newContact.setContactName(cursor.getString(1));
                newContact.setStreetAddress(cursor.getString(2));
                newContact.setCity(cursor.getString(3));
                newContact.setState(cursor.getString(4));
                newContact.setZipCode(cursor.getString(5));
                newContact.setPhoneNumber(cursor.getString(6));
                newContact.setCellNumber(cursor.getString(7));
                newContact.setEMail(cursor.getString(8));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.valueOf(cursor.getString(9)));
                newContact.setBirthday(calendar);
                newContact.setBestFriendForever(cursor.getInt(10));
                contacts.add(newContact); //newContact object reference is added to the contacts ArrayList<Contact>
                cursor.moveToNext();  //moves to the next record held in the cursor

            }
            cursor.close();
        }
        catch (Exception e) {
            contacts = new ArrayList<Contact>(); //ArrayList is set to a new empty ArrayList in case it crashes while running
        }
        return contacts;
    }


    public Contact getSpecificContact(int contactId) {
        Contact contact = new Contact();
        String query = "SELECT * FROM contact WHERE _id =" + contactId;
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            contact.setContactID(cursor.getInt(0));
            contact.setContactName(cursor.getString(1));
            contact.setStreetAddress(cursor.getString(2));
            contact.setCity(cursor.getString(3));
            contact.setState(cursor.getString(4));
            contact.setZipCode(cursor.getString(5));
            contact.setPhoneNumber(cursor.getString(6));
            contact.setCellNumber(cursor.getString(7));
            contact.setEMail(cursor.getString(8));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(cursor.getString(9)));
            contact.setBirthday(calendar);
            contact.setBestFriendForever(cursor.getInt(10));
            cursor.close();
        }
        return contact;
    }

    public boolean deleteContact(int contactId) {
        boolean didDelete = false;
        try {
            //Three parameters for the .delete methood. 1. Name of the Table | 2. Where clause to determine the records to delete | 3. String Array of criteria for deletion (May be Null)
            didDelete = database.delete("contact", "_id=" + contactId, null) > 0;
        } catch (Exception e) {
            //Do nothing -return value already set to false
        }
        return didDelete;
    }


}
