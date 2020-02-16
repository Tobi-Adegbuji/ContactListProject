package com.example.mycontactlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.example.mycontactlist.DatePickerDialog.SaveDateListener;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SaveDateListener {

    boolean isBFF;

    private Contact currentContact; //creates the association between the this MainActivity class (ContactActivity) and a Contact object


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initListButton();
        initMapButton();
        initToggleButton();
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            initContact(extras.getInt("contactid"));
        } else {
            currentContact = new Contact();
        }
        setForEditing(false);
        initSettingsButton();
        initChangeDateButton();

        initTextChangedEvents();
        initSaveButton();




    }


    private void initListButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    private void initMapButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonMap);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingsButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonSettings);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }



    private void initToggleButton() {
        final ToggleButton editToggle = (ToggleButton)findViewById(R.id.toggleButtonEdit);
        editToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setForEditing(editToggle.isChecked());
            }
        });
    }



    private void setForEditing(boolean enabled) {
        EditText editName = (EditText) findViewById(R.id.editName);
        EditText editAddress = (EditText) findViewById(R.id.editAddress);
        EditText editCity = (EditText) findViewById(R.id.editCity);
        EditText editState = (EditText) findViewById(R.id.editState);
        EditText editZipCode = (EditText) findViewById(R.id.editZipcode);
        EditText editPhone = (EditText) findViewById(R.id.editHome);
        EditText editCell = (EditText) findViewById(R.id.editCell);
        EditText editEmail = (EditText) findViewById(R.id.editEMail);
        Button buttonChange = (Button) findViewById(R.id.btnBirthday);
        Button buttonSave = (Button) findViewById(R.id.buttonSave);
        CheckBox bff = (CheckBox) findViewById(R.id.checkBoxBFF);
        CheckBox bbff = (CheckBox) findViewById(R.id.checkBoxBBFF);

        editName.setEnabled(enabled);
        editAddress.setEnabled(enabled);
        editCity.setEnabled(enabled);
        editState.setEnabled(enabled);
        editZipCode.setEnabled(enabled);
        editPhone.setEnabled(enabled);
        editCell.setEnabled(enabled);
        editEmail.setEnabled(enabled);
        buttonChange.setEnabled(enabled);
        buttonSave.setEnabled(enabled);
        bff.setEnabled(enabled);
        bbff.setEnabled(enabled);

        if (enabled) {
            editName.requestFocus();
        } else {
            ScrollView s = (ScrollView) findViewById(R.id.scrollView1);
            s.fullScroll(ScrollView.FOCUS_UP);
        }

        if(bff.isChecked()){
            bbff.setEnabled(false);
        }else if(bbff.isChecked()) {
            bff.setEnabled(false);
        }



    }


    @Override
    public void didFinishDatePickerDialog(Calendar selectedTime) {
        TextView birthday = (TextView) findViewById(R.id.textBirthday);
        birthday.setText(DateFormat.format("MM/dd/yyyy", selectedTime.getTimeInMillis()).toString());

        currentContact.setBirthday(selectedTime); /* uses the Contact Class's setBirthday method to assign the date *
                                                   * selected in the custom dialog to the currentContact object     */
    }

    private void initChangeDateButton() {
        Button changeDate = (Button) findViewById(R.id.btnBirthday);
        changeDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DatePickerDialog datePickerDialog = new DatePickerDialog();
                datePickerDialog.show(fm, "DatePick");
            }

        });


    }


    private void initTextChangedEvents() {
        final EditText etContactName = (EditText) findViewById(R.id.editName); //declared Final because it is used inside the event code

        etContactName.addTextChangedListener(new TextWatcher() { /*  TextWatcher is an Object that, when attached to a widget that allows editing, *
                                                                  *  will execute its methods when the text in the widget is changed.              *
                                                                  *  REQUIRES the three methods below to be implemented even if only one is used   */
            @Override
            public void afterTextChanged(Editable s) { /*  Method called after the user completes editing the data and leaves the EditText.  *
                                                        *  This is the event that this app uses to capture the data the user entered         */
                currentContact.setContactName(etContactName.getText().toString()); /* Code executed when the user ends editing of the EditText.   *
                                                                                    * Gets the text in EditText, converts it to a string, and     *
                                                                                    * sets the contactName attribute of the currentContact object *
                                                                                    * to that value                                               */
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { /* Required for the TextWatcher Object. Executed when           *
                                                                                              * the user presses down on a key to enter it into an EditText  *
                                                                                              * but before the value in the EditText is actually changed     */
                //Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { /* Required for the TextWatcher Object. Executed after *
                                                                                           * each and every character change in an EditText      */
                //Auto-generated method stub
            }
        });



        //BFF CheckBox Test Code Start
        final CheckBox etBFF = (CheckBox) findViewById(R.id.checkBoxBFF);
        final CheckBox etBFFF = (CheckBox) findViewById(R.id.checkBoxBBFF);
        etBFF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    currentContact.setBestFriendForever(1);
                    etBFFF.setEnabled(false);
                } else {
                    currentContact.setBestFriendForever(0);
                    etBFFF.setEnabled(true);
                }


            }
        });
        //End of BFF CheckBox Test Code */


        etBFFF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    currentContact.setBestFriendForever(2);
                    etBFF.setEnabled(false);
                } else if(!etBFF.isChecked() && !isChecked) {
                    currentContact.setBestFriendForever(0);
                    etBFF.setEnabled(true);
                }
            }
        });




        final EditText etStreetAddress = (EditText) findViewById(R.id.editAddress);
        etStreetAddress.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setStreetAddress(etStreetAddress.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Auto-generated method stub
            }
        });

        final EditText etCity = (EditText) findViewById(R.id.editCity);
        etCity.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setCity(etCity.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Auto-generated method stub
            }
        });

        final EditText etState = (EditText) findViewById(R.id.editState);
        etState.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setState(etState.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Auto-generated method stub
            }
        });

        final EditText etZipcode = (EditText) findViewById(R.id.editZipcode);
        etZipcode.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setZipCode(etZipcode.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Auto-generated method stub
            }
        });

        final EditText etHomePhone = (EditText) findViewById(R.id.editHome);
        etHomePhone.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setPhoneNumber(etHomePhone.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //  Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  Auto-generated method stub
            }
        });


        final EditText etCellPhone = (EditText) findViewById(R.id.editCell);
        etCellPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setCellNumber(etCellPhone.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //  Auto-generated method stub
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  Auto-generated method stub
            }
        });

        final EditText etEmail = (EditText) findViewById(R.id.editEMail);
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setEMail(etEmail.getText().toString());
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //  Auto-generated method stub
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  Auto-generated method stub
            }
        });

        etHomePhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher()); // code adds a listener to to the Phone Number editTexts that calls
        etCellPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher()); // the PhoneNumberFormattingTextWatcher object, which in turn
                                                                                    // adds the appropriate formatting as the user types

    }


    private void initSaveButton() {
        Button saveButton = (Button) findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard();
                boolean wasSuccessful = false; //captures the return value of ContactDataSource Methods and used to determine the operations that should be performed
                ContactDataSource ds = new ContactDataSource(MainActivity.this); //new ContactDataSource object is instantiated
                try {
                    ds.open(); //Opens the database

                    if (currentContact.getContactID() == -1)  { //only new Contacts will have a -1 value
                        wasSuccessful = ds.insertContact(currentContact); //if method runs successfully, insertContact returns True
                    }
                    if(wasSuccessful) { //checks to see if, IF statement above was run successfully (new Contact)
                        int newID = ds.getLastContactID(); //uses retrieval method on ContactDataSource to get the newly inserted contact's ID
                        currentContact.setContactID(newID); //Sets the currentContact object's ID to the retrieved value from the database
                    }
                    else{
                        wasSuccessful = ds.updateContact(currentContact);
                    }

                    ds.close(); //closes the database

                } catch (Exception e) {
                    wasSuccessful = false; //if an exception is found, wasSuccessful stays false;
                }

                if (wasSuccessful) { //if it runs succesfully the edit Toggle button is turned off and the screen is set to Viewing
                    ToggleButton editToggle = (ToggleButton) findViewById(R.id.toggleButtonEdit);
                    editToggle.toggle();
                    setForEditing(false);
                }

            }
        });

    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); //gets a system service that manages user input
        EditText editName = (EditText) findViewById(R.id.editName); //gets a reference to an EditText
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0); // closes the keyboard
        EditText editAddress = (EditText) findViewById(R.id.editAddress);
        imm.hideSoftInputFromWindow(editAddress.getWindowToken(), 0);
        EditText editCity = (EditText) findViewById(R.id.editCity);
        imm.hideSoftInputFromWindow(editCity.getWindowToken(), 0);
        EditText editState= (EditText) findViewById(R.id.editState);
        imm.hideSoftInputFromWindow(editState.getWindowToken(), 0);
        EditText editZip = (EditText) findViewById(R.id.editZipcode);
        imm.hideSoftInputFromWindow(editZip.getWindowToken(), 0);
        EditText editHome = (EditText) findViewById(R.id.editHome);
        imm.hideSoftInputFromWindow(editHome.getWindowToken(), 0);
        EditText editCell = (EditText) findViewById(R.id.editCell);
        imm.hideSoftInputFromWindow(editCell.getWindowToken(), 0);
        EditText editEMail = (EditText) findViewById(R.id.editEMail);
        imm.hideSoftInputFromWindow(editEMail.getWindowToken(), 0);
    }

    private void initContact(int id) {

        ContactDataSource ds = new ContactDataSource(MainActivity.this);
        try {
            ds.open();
            currentContact = ds.getSpecificContact(id);
            ds.close();
        } catch (Exception e) {
            Toast.makeText(this, "Load Contact Failed", Toast.LENGTH_LONG).show();
        }

        EditText editName = (EditText) findViewById(R.id.editName);
        EditText editAddress = (EditText) findViewById(R.id.editAddress);
        EditText editCity = (EditText) findViewById(R.id.editCity);
        EditText editState= (EditText) findViewById(R.id.editState);
        EditText editZip = (EditText) findViewById(R.id.editZipcode);
        EditText editHome = (EditText) findViewById(R.id.editHome);
        EditText editCell = (EditText) findViewById(R.id.editCell);
        EditText editEMail = (EditText) findViewById(R.id.editEMail);
        TextView birthDay = (TextView) findViewById(R.id.textBirthday);


        //Test
        CheckBox editBFF = (CheckBox) findViewById(R.id.checkBoxBFF);
        //test

        CheckBox editBBFF = (CheckBox) findViewById(R.id.checkBoxBBFF);

        editName.setText(currentContact.getContactName());
        editAddress.setText(currentContact.getStreetAddress());
        editCity.setText(currentContact.getCity());
        editState.setText(currentContact.getState());
        editZip.setText(currentContact.getZipCode());
        editHome.setText(currentContact.getPhoneNumber());
        editCell.setText(currentContact.getCellNumber());
        editEMail.setText(currentContact.getEMail());
        birthDay.setText(DateFormat.format("MM/dd/yyyy", currentContact.getBirthday().getTimeInMillis()).toString());

        try{
            if(currentContact.getBestFriendForever() == 1) {
                editBFF.setChecked(true);
                editBBFF.setChecked(false);
            }
            else if (currentContact.getBestFriendForever() == 2){
                editBBFF.setChecked(true);
                editBFF.setChecked(false);
            }
            else {
                editBFF.setChecked(false);
                editBBFF.setChecked(false);

            }

        }catch (Exception e) {

        }

    }


 /*
    private void initCheckBoxBFFByClick() {
        int bff = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).getInt("BFF", 0);
        CheckBox checkBoxBFF = (CheckBox) findViewById(R.id.checkBoxBFF);
        checkBoxBFF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit().putInt("BFF", 1).commit();
                }else {
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit().putInt("BFF", 0).commit();
                }
            }
        });





    }

    private void initCheckBoxBFF() {
        int bff = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).getInt("BFF", 0);
        CheckBox checkBoxBFF = (CheckBox) findViewById(R.id.checkBoxBFF);
        if(bff == 1) {
            checkBoxBFF.setChecked(true);
        } else {
            checkBoxBFF.setChecked(false);
        }

    }

 */



}
