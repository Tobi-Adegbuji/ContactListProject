package com.example.mycontactlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {

    ArrayList<Contact> contacts;
    boolean isDeleting = false;
    ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initListButton();
        initMapButton();
        initSettingsButton();
        initItemClick();
        initAddContactButton();
        initDeleteButton();
        initBackButton();
        initAddOrDelButton();

        String sortBy = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).getString("sortfield", "contactname");
        String sortOrder = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).getString("sortorder", "ASC");

        ContactDataSource ds = new ContactDataSource(this);

        try{
            ds.open();
            contacts = ds.getContacts(sortBy, sortOrder);
            ds.close();
            ListView listView = (ListView) findViewById(R.id.lvContacts);
            adapter = new ContactAdapter(this, contacts);
            listView.setAdapter(adapter);
        }catch (Exception e){
            Toast.makeText(this, "Error retrieving contacts", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        String sortBy = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).getString("sortfield", "contactname");
        String sortOrder = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).getString("sortorder", "ASC");

        ContactDataSource ds = new ContactDataSource(this);

        try{
            ds.open();
            contacts = ds.getContacts(sortBy, sortOrder);
            ds.close();
            if(contacts.size() > 0) {
                ListView listView = (ListView) findViewById(R.id.lvContacts);
                adapter = new ContactAdapter(this, contacts);
                listView.setAdapter(adapter);
            }else {
                Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }catch (Exception e){
            Toast.makeText(this, "Error retrieving contacts", Toast.LENGTH_LONG).show();
        }
    }


private void initAddOrDelButton(){
    final TextView addOrDel = (TextView) findViewById(R.id.addOrDelete);
    final Button newContact = (Button) findViewById(R.id.buttonAdd);
    final Button deleteButton = (Button) findViewById(R.id.buttonDelete);
    final Button backButton = (Button) findViewById(R.id.back);
    final Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
    addOrDel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            addOrDel.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            newContact.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
            deleteButton.startAnimation(slideUp);
            newContact.startAnimation(slideUp);
            backButton.startAnimation(slideUp);
        }
    });
}

    private void initBackButton(){
        final TextView addOrDel = (TextView) findViewById(R.id.addOrDelete);
        final Button newContact = (Button) findViewById(R.id.buttonAdd);
        final Button deleteButton = (Button) findViewById(R.id.buttonDelete);
        final Button backButton = (Button) findViewById(R.id.back);
        final Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteButton.setVisibility(View.INVISIBLE);
                newContact.setVisibility(View.INVISIBLE);
                backButton.setVisibility(View.INVISIBLE);
                deleteButton.startAnimation(slideDown);
                newContact.startAnimation(slideDown);
                backButton.startAnimation(slideDown);
                addOrDel.setVisibility(View.VISIBLE);


            }
        });
    }

    private void initListButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonList);
        ibList.setEnabled(false);

    }

    private void initMapButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonMap);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactListActivity.this, ContactMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingsButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonSettings);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactListActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initItemClick(){
        ListView listView = (ListView) findViewById(R.id.lvContacts);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                Contact selectedContact = contacts.get(position); //creates Contact object from the ArrayList using the position value
                if (isDeleting) {
                    adapter.showDelete(position, itemClicked, ContactListActivity.this, selectedContact);

                } else {

                Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
                intent.putExtra("contactid", selectedContact.getContactID()); //places the contact ID in the Bundle that is passed to the Contact Activity
                startActivity(intent);                                                  //a BUNDLE is an object used in Android to pass data between Activites
            }
            }
        });
    }

    private void initAddContactButton() {
        Button newContact = (Button) findViewById(R.id.buttonAdd);
        newContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initDeleteButton() {
        final Button deleteButton = (Button) findViewById(R.id.buttonDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isDeleting) { //checks to see if user has deleting enabled when user
                    deleteButton.setText("Delete");
                    isDeleting = false;
                    adapter.notifyDataSetChanged(); //tells the adapter to update itself; sets the list back to the not deleting mode
                } else {
                    deleteButton.setText("Done Deleting");
                    isDeleting = true;
                }
            }
        });
    }




}
