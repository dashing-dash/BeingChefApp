package com.example.android.beingchef;

/**
 * Created by Pallav on 7/18/2017.
 */

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddItemListActivity extends AppCompatActivity implements OnItemSelectedListener{

    private TextView category;
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView txtDetails;
    private EditText inputItemName, inputDesc,inputPrice;
    private Button btnSave;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String itemCategory;

    private String userId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_list);
        category=(TextView) findViewById(R.id.category);
        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Mad Max: Fury Road");
        categories.add("Inside Out");
        categories.add("Star Wars: Episode VII - The Force Awakens");
        categories.add("Shaun the Sheep");
        categories.add("The Martian");
        categories.add("Mission: Impossible Rogue Nation");
        categories.add("Up");
        categories.add("Star Trek");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);



        // Displaying toolbar icon
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        txtDetails = (TextView) findViewById(R.id.txt_user);
        inputItemName = (EditText) findViewById(R.id.itemName);
        inputDesc = (EditText) findViewById(R.id.desc);
        inputPrice = (EditText) findViewById(R.id.price);
        btnSave = (Button) findViewById(R.id.btn_save);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("category");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Realtime Database");

        // app_title change listener
        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);

                 //update toolbar title
                getSupportActionBar().setTitle(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        // Save / update the item
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = inputItemName.getText().toString();
                String desc = inputDesc.getText().toString();
                String price = inputPrice.getText().toString();
                
                // Check for already existed userId
                if (TextUtils.isEmpty(userId)) {
                    createItemList(itemName,desc,price);
                } else {
                    updateItemList(itemName,desc,price);
                }
            }
        });

        toggleButton();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner itemCategory
        itemCategory = parent.getItemAtPosition(position).toString();
        category.setText(itemCategory);
        // Showing selected spinner itemCategory
        Toast.makeText(parent.getContext(), "Selected: " + itemCategory, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    private void toggleButton() {
        if (TextUtils.isEmpty(userId)) {
            btnSave.setText("Save");
        } else {
            btnSave.setText("Update");
        }
    }

    /**
     * Creating new item node under 'users'
     */
    private void createItemList(String itemName, String desc, String price) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        mFirebaseDatabase = mFirebaseInstance.getReference(itemCategory);
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }

        ItemList item = new ItemList(itemName, desc,price);

        mFirebaseDatabase.child(userId).setValue(item);

        addItemListChangeListener();
    }

    /**
     * ItemList data change listener
     */
    private void addItemListChangeListener() {

        mFirebaseDatabase = mFirebaseInstance.getReference(itemCategory);
        // ItemList data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ItemList item = dataSnapshot.getValue(ItemList.class);

                // Check for null
                if (item == null) {
                    Log.e(TAG, "ItemList data is null!");
                    return;
                }

                Log.e(TAG, "ItemList data is changed!" + item.itemName + ", " + item.itemDesc + ", " + item.itemPrice);

                // Display newly updated name and email
                txtDetails.setText(item.itemName + ", " + item.itemDesc + ", " + item.itemPrice);

                // clear edit text
                inputDesc.setText("");
                inputItemName.setText("");
                inputPrice.setText("");
                toggleButton();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read item", error.toException());
            }
        });
    }

    private void updateItemList(String itemName, String itemDesc, String itemPrice) {

        mFirebaseDatabase = mFirebaseInstance.getReference(itemCategory);
        // updating the item via child nodes
        userId = mFirebaseDatabase.push().getKey();
        if (!TextUtils.isEmpty(itemName))
            mFirebaseDatabase.child(userId).child("itemName").setValue(itemName);

        if (!TextUtils.isEmpty(itemDesc))
            mFirebaseDatabase.child(userId).child("itemDesc").setValue(itemDesc);

        if (!TextUtils.isEmpty(itemPrice))
            mFirebaseDatabase.child(userId).child("itemPrice").setValue(itemPrice);
    }
}