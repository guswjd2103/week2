package com.example.week1_contact.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.week1_contact.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.week1_contact.ContactData;
import com.example.week1_contact.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactFragment extends Fragment {

    static final int PICK_CONTACT_REQUEST = 1;

    ArrayList<ContactData> contactList = new ArrayList<ContactData>();
    ArrayList<String> numberList = new ArrayList<String>();
    ArrayList<String> nameList = new ArrayList<String>();
    Adapter myAdapter;
    int i;

    Cursor cursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1);
            }
        }

        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        this.getContacts(getActivity(), contactList);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        myAdapter = new Adapter(getActivity(), contactList);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] projection = {ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
                cursor.moveToPosition(contactList.get(position).getId());

                Uri selectUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, (cursor.getLong(0)));
                Uri lookUp = ContactsContract.Contacts.getLookupUri(getActivity().getContentResolver(), selectUri);
                Intent intent = new Intent(Intent.ACTION_VIEW, lookUp);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addListItem(view);
            }
        });

        return view;
    }

    private void addListItem(View view) {
        Intent pickIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        pickIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        startActivityForResult(pickIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_CONTACT_REQUEST:
                switch (resultCode) {
                    case 0:
                        Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                        String[] projection = {ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
                        Cursor cursor = getContext().getContentResolver().query(contactUri, projection, null, null, null);

                        if(cursor!=null){
                            while(cursor.moveToNext()){
                                int nameidx = cursor.getColumnIndex(projection[1]);
                                int numberidx = cursor.getColumnIndex(projection[2]);

                                String name = cursor.getString(nameidx);
                                String number = cursor.getString(numberidx);

                                ContactData contactData = new ContactData(R.drawable.profile, name, number, i);
                                i+=1;
                                if(!numberList.contains(number) || !nameList.contains(name)) {
                                    contactList.add(contactData);
                                    numberList.add(number);
                                    nameList.add(name);
                                    Collections.sort(contactList);
                                    myAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        cursor.close();
                        break;
                }
                break;
        }
    }


    public List<ContactData> getContacts(Context context, List<ContactData> contactsList) {
        numberList.clear();
        nameList.clear();
        contactsList.clear();
        i=0;
        ContentResolver resolver = context.getContentResolver();
        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = {ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.Contacts.PHOTO_ID};

        Cursor cursor = resolver.query(phoneUri, projection, null, null, null);

        if(cursor!=null){
            while(cursor.moveToNext()){
                int idx = cursor.getColumnIndex(projection[0]);
                int nameidx = cursor.getColumnIndex(projection[1]);
                int numberidx = cursor.getColumnIndex(projection[2]);
                int photoidx = cursor.getInt(3);

                String name = cursor.getString(nameidx);
                String number = cursor.getString(numberidx);

                ContactData contactData = new ContactData(photoidx, name, number, i);
                i+=1;
                contactsList.add(contactData);
                numberList.add(number);
                nameList.add(name);
            }
        }

        Collections.sort(contactsList);

        cursor.close();
        return contactsList;
    }
}
