package com.example.mycontactlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

public class ContactSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initListButton();
        initMapButton();
        initSettingsButton();
        initSettings();
        initSortByClick();
        initSortOrderClick();


        initBackgroundClick();


    }
    private void initListButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactSettingsActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    private void initMapButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonMap);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactSettingsActivity.this, ContactMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingsButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonSettings);
        ibList.setEnabled(false);
    }

    private void initSettings() {

        String sortBy = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).getString("sortfield","contactname");
        String sortOrder = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).getString("sortorder","ASC");

        RadioButton rbName = (RadioButton) findViewById(R.id.radioName);
        RadioButton rbCity = (RadioButton) findViewById(R.id.radioCity);
        RadioButton rbBirthDay = (RadioButton) findViewById(R.id.radioBirthday);
        if (sortBy.equalsIgnoreCase("contactname")) {
            rbName.setChecked(true);
        }
        else if (sortBy.equalsIgnoreCase("city")) {
            rbCity.setChecked(true);
        }
        else {
            rbBirthDay.setChecked(true);
        }

        RadioButton rbAscending = (RadioButton) findViewById(R.id.radioAscending);
        RadioButton rbDescending = (RadioButton) findViewById(R.id.radioDescending);
        if (sortOrder.equalsIgnoreCase("ASC")) {
            rbAscending.setChecked(true);
        }
        else {
            rbDescending.setChecked(true);
        }
        /*color change code below */


        String back = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).getString("background","blue");

        RadioButton def = (RadioButton) findViewById(R.id.bgDefault);
        RadioButton red = (RadioButton) findViewById(R.id.bgRed);
        RadioButton blue = (RadioButton) findViewById(R.id.bgBlue);
        RadioButton black = (RadioButton) findViewById(R.id.bgBlack);
        ScrollView settingsSV = (ScrollView) findViewById(R.id.settingsScrollView);


        if(back.equalsIgnoreCase("default")) {
            def.setChecked(true);
            settingsSV.setBackgroundColor(getResources().getColor(R.color.new_background_color));

        }else if (back.equalsIgnoreCase("red")) {
            red.setChecked(true);
            settingsSV.setBackgroundColor(getResources().getColor(R.color.background_red));

        }else if(back.equalsIgnoreCase("black")){
            black.setChecked(true);
            settingsSV.setBackgroundColor(getResources().getColor(R.color.background_black));
        }else {
            blue.setChecked(true);
            settingsSV.setBackgroundColor(getResources().getColor(R.color.background_blue));

        }
    }


    private void initSortByClick() {
        RadioGroup rgSortBy = (RadioGroup) findViewById(R.id.radioGroupSortBy);
        rgSortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                RadioButton rbName = (RadioButton) findViewById(R.id.radioName);
                RadioButton rbCity = (RadioButton) findViewById(R.id.radioCity);
                if (rbName.isChecked()) {
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit() .putString("sortfield", "contactname").commit();
                }
                else if (rbCity.isChecked()) {
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit().putString("sortfield", "city").commit();
                }
                else {
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit().putString("sortfield", "birthday").commit();
                }
            }
        });
    }

    private void initSortOrderClick() {
        RadioGroup rgSortOrder = (RadioGroup) findViewById(R.id.radioGroupSortOrder);
        rgSortOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                RadioButton rbAscending = (RadioButton) findViewById(R.id.radioAscending);
                if (rbAscending.isChecked()) {
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit().putString("sortorder", "ASC").commit();
                }
                else {
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit().putString("sortorder", "DESC").commit();
                }
            }
        });
    }



    /* private void initBackgroundColor() {
        String back = getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).getString("background","default");

        RadioButton def = (RadioButton) findViewById(R.id.bgDefault);
        RadioButton red = (RadioButton) findViewById(R.id.bgRed);
        RadioButton blue = (RadioButton) findViewById(R.id.bgBlue);
        ScrollView settingsSV = (ScrollView) findViewById(R.id.settingsScrollView);
        ScrollView mainSV = (ScrollView) findViewById(R.id.scrollView1);
        if(back.equalsIgnoreCase("default")) {
            def.setChecked(true);
            settingsSV.setBackgroundColor(getResources().getColor(R.color.new_background_color));
            mainSV.setBackgroundColor(getResources().getColor(R.color.new_background_color));
        }else if (back.equalsIgnoreCase("red")) {
            red.setChecked(true);
            settingsSV.setBackgroundColor(getResources().getColor(R.color.background_red));
            mainSV.setBackgroundColor(getResources().getColor(R.color.background_red));
        }else {
            blue.setChecked(true);
            settingsSV.setBackgroundColor(getResources().getColor(R.color.background_blue));
            mainSV.setBackgroundColor(getResources().getColor(R.color.background_blue));
        }


    } */


    private void initBackgroundClick(){
        RadioGroup background = (RadioGroup) findViewById(R.id.radioGroupBackgroundColor);

        background.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                RadioButton def = (RadioButton) findViewById(R.id.bgDefault);
                RadioButton red = (RadioButton) findViewById(R.id.bgRed);
                RadioButton black = (RadioButton) findViewById(R.id.bgBlack);
                ScrollView settingsSV = (ScrollView) findViewById(R.id.settingsScrollView);
                if(def.isChecked()) {
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit().putString("background", "default").commit();
                    settingsSV.setBackgroundColor(getResources().getColor(R.color.new_background_color));
                }else if(red.isChecked()) {
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit().putString("background", "red").commit();
                    settingsSV.setBackgroundColor(getResources().getColor(R.color.background_red));
                }else if(black.isChecked()){
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit().putString("background", "black").commit();
                    settingsSV.setBackgroundColor(getResources().getColor(R.color.background_black));
                }
                else {
                    getSharedPreferences("MyContactListPreferences", Context.MODE_PRIVATE).edit().putString("background", "blue").commit();
                    settingsSV.setBackgroundColor(getResources().getColor(R.color.background_blue));
                }

            }
        });


    }



}
