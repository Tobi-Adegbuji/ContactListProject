package com.example.mycontactlist;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private ArrayList<Contact> items;
    private Context adapterContext;

    public ContactAdapter(Context context, ArrayList<Contact> items) {
        super(context, R.layout.list_item, items);
        adapterContext = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        try{
            Contact contact = items.get(position);

            if(v == null) { //if there isn't an existing view to be reused, the LayoutInflater service is called to instantiate the list_item layout previously used
                LayoutInflater vi = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }

            TextView contactName = (TextView) v.findViewById(R.id.textContactName);
            TextView contactNumber = (TextView) v.findViewById(R.id.textPhoneNumber);
            TextView contactCell = (TextView) v.findViewById(R.id.textCellNumber);
            TextView contactStreetAddress = (TextView) v.findViewById(R.id.textStreetAddress);
            TextView contactCityStateZip = (TextView) v.findViewById(R.id.textCityStateZip);
            Button b = (Button) v.findViewById(R.id.buttonDeleteContact);
            ImageView starBFF = (ImageView) v.findViewById(R.id.imageBFFStar);
            ImageView starBBFF = (ImageView) v.findViewById(R.id.imageBBFFStar);
            contactName.setText(contact.getContactName());
            contactNumber.setText("Home: " + contact.getPhoneNumber());
            contactCell.setText("Cell: " + contact.getCellNumber());
            contactStreetAddress.setText(contact.getStreetAddress());
            contactCityStateZip.setText(contact.getCity() + ", " + contact.getState() + " " + contact.getZipCode());
            b.setVisibility(View.INVISIBLE);


            try{
                if(contact.getBestFriendForever() == 1) {
                    starBFF.setVisibility(View.VISIBLE);
                    starBBFF.setVisibility(View.INVISIBLE);
                }
                else if (contact.getBestFriendForever() == 2) {
                    starBFF.setVisibility(View.VISIBLE);
                    starBBFF.setVisibility(View.VISIBLE);
                }
                else {
                    starBFF.setVisibility(View.INVISIBLE);
                    starBBFF.setVisibility(View.INVISIBLE);

                }

            }
            catch (Exception e){

            }
        }
        catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return v;
    }



//            if(position % 2 == 0) {
//                contactName.setTextColor(Color.RED);
//            }else {
//                contactName.setTextColor(Color.BLUE);
//            }


    public void showDelete(final int position, final View convertView, final Context context, final Contact contact) {
        View v = convertView;
        final Button b = (Button) v.findViewById(R.id.buttonDeleteContact);
        if (b.getVisibility() == View.INVISIBLE) {
            b.setVisibility(View.VISIBLE);
            b.setOnClickListener(new View.OnClickListener() { //Page 111 OnClickListener does not have View. before the method
                @Override
                public void onClick(View v) {
                    hideDelete(position, convertView, context);
                    items.remove(contact);
                    deleteOption(contact.getContactID(), context);
                }
            });
        }
        else {
            hideDelete(position, convertView, context);
        }
    }

    private void deleteOption(int contactToDelete, Context context) {
        ContactDataSource db = new ContactDataSource(context);
        try {
            db.open();
            db.deleteContact(contactToDelete);
            db.close();
        } catch (Exception e) {
            Toast.makeText(adapterContext, "Delete Contact Failed", Toast.LENGTH_LONG).show();
        }
        this.notifyDataSetChanged(); //tells the adapter that the underlying data source has changed, so that the list displayed will be changed to reflect the deletion
    }

    public void hideDelete(int position, View convertView, Context context) {
        View v = convertView;
        final Button b = (Button) v.findViewById(R.id.buttonDeleteContact);
        b.setVisibility(View.INVISIBLE);
        b.setOnClickListener(null);
    }







}
