package com.example.mycontactlist;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarContextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<Contact> contacts;
    boolean isDeleting = false;
    ContactAdapter adapter;
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
//        initListButton();
        initItemClick();
        initAddContactButton();
        initDeleteButton();
        initBackButton();
        initAddOrDelButton();

    androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar_header);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,R.string.navigataion_drawer_open,R.string.navigataion_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

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

//    public void initNavigationDrawer() {
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//
//                int id = menuItem.getItemId();
//
//
//                    default:
//                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                        drawer.closeDrawer(GravityCompat.START);
//                        return true;
//
//                }
//                return true;
//            }
//        });
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(toggle.onOptionsItemSelected(item)){
//            return true;
//        }}

//    private void initListButton() {
//        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonList);
//        ibList.setEnabled(false);
//
//    }


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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.contactitem:
                break;
            case R.id.mapitem:
                Intent intent2 = new Intent(ContactListActivity.this, ContactMapActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                break;
            case R.id.settingsitem:
                Intent intent3 = new Intent(ContactListActivity.this, ContactSettingsActivity.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent3);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
    return true;
    }



}
