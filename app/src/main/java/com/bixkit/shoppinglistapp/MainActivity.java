package com.bixkit.shoppinglistapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText addedItem;
    EditText quantityItem;
    Spinner dropdownAmount;
    ListView listView;
    Firebase ref;
    FirebaseListAdapter<Product> adapter;

    public FirebaseListAdapter<Product> getMyAdapter() { return adapter; }
    public Product getItem(int index){
        return getMyAdapter().getItem(index);
    }





    private static final String TAG = "com.bixkit.myfirstapp";


//    ArrayAdapter<Product> adapter;

//    ArrayList<Product> bag = new ArrayList<Product>();



    Product lastDeletedProduct;
    int lastDeletedPosition;

    public void saveCopy(){
        lastDeletedPosition = listView.getCheckedItemPosition();
        lastDeletedProduct = getItem(lastDeletedPosition);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getting our listiew - you can check the ID in the xml to see that it
        //is indeed specified as "list"
        listView = (ListView) findViewById(R.id.list);
        //here we create a new adapter linking the bag and the
        //listview

        ref = new Firebase("https://shoppinglistserkan.firebaseio.com/items");


        adapter =  new FirebaseListAdapter<Product>(
                this,
                Product.class,
                android.R.layout.simple_list_item_checked, ref ){
            @Override
            protected void populateView(View v, Product product, int i) {
                TextView text = (TextView) v.findViewById(android.R.id.text1);
                text.setText(product.toString());
            }
        };



        //setting the adapter on the listview
        listView.setAdapter(adapter);
        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        getPreferences();

        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        boolean inputSwitch = prefs.getBoolean("inputTypeSwitch", true);

        final EditText quantityItem = (EditText) findViewById(R.id.insertedItemQuantity);
        final Spinner dropdownAmount = (Spinner) findViewById(R.id.dropDownAmount);

        if(inputSwitch) {
            dropdownAmount.setVisibility(View.GONE);
            quantityItem.setVisibility(View.VISIBLE);
        } else {
            dropdownAmount.setVisibility(View.VISIBLE);
            quantityItem.setVisibility(View.GONE);
        }

        Button addButton = (Button) findViewById(R.id.addButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        final EditText addedItem = (EditText) findViewById(R.id.insertedItem);
        final Map<String, String> productDetails = new HashMap<>();



        int position = -1;
        if (savedInstanceState!=null)
        {
            Log.d(TAG,"on create");
            position = savedInstanceState.getInt("position");
        }
        if(position != -1){
            listView.setSelection(position);
        }



        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = addedItem.getText().toString();
                String listQuantity = (String) dropdownAmount.getSelectedItem();
                int quantity = Integer.valueOf(quantityItem.getText().toString());


                Product addedProduct = new Product(name, quantity, listQuantity);

                //add products to list
                ref.push().setValue(addedProduct);

                //The next line is needed in order to say to the ListView
                //that the data has changed - we have added stuff now!
                getMyAdapter().notifyDataSetChanged();

            }
        });

        //delete button for a checked item
        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                saveCopy(); //save a copy of the item selected before you delete it
                int index = listView.getCheckedItemPosition();

                // delete product
                getMyAdapter().getRef(index).setValue(null);
                getMyAdapter().notifyDataSetChanged();

                final View parent = listView;
                Snackbar snackbar = Snackbar
                        .make(listView, "Item Deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //add products to list
                                ref.push().setValue(lastDeletedProduct);
                                getMyAdapter().notifyDataSetChanged();
                                Snackbar snackbar = Snackbar.make(listView, "Item restored!", Snackbar
                                        .LENGTH_SHORT);
                                snackbar.show();
                            }
                        });
                snackbar.show();

            }
        });

//        clearButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                getMyAdapter().notifyDataSetChanged();
//                //showing dialog
//                MyDialogFragment dialog = new MyDialogFragment(){
//                    @Override
//                    protected void positiveClick(){
//                        bag.clear();
//                        getMyAdapter().notifyDataSetChanged();
//                    }
//
//                    @Override
//                    protected void negativeClick(){
//                        Toast toast = Toast.makeText(getApplicationContext(), "List not cleared", Toast
//                                .LENGTH_LONG);
//                        toast.show();
//                    }
//
//                };
//                dialog.show(getFragmentManager(), "MyFragment");
//
//            }
//        });


    }






    //This method is called before our activity is destroyed
    protected void onSaveInstanceState(Bundle outState) {
        //ALWAYS CALL THE SUPER METHOD
        super.onSaveInstanceState(outState);
        Log.d(TAG, "on save");
        outState.putInt("position", listView.getCheckedItemPosition());
    }




//    //this is called when our activity is recreated, but
//    //AFTER our onCreate method has been called
//    //EXTREMELY IMPORTANT DETAIL
//    //This is an alternative place to restore the data
//    //This can also be done in the onCreate method like in the book.
//    protected void onRestoreInstanceState(Bundle savedState) {
//        //MOST UI elements will automatically store the information
//        //if we call the super.onRestoreInstaceState
//        //but other data will be lost.
//        super.onRestoreInstanceState(savedState);
//
//        bag = savedState.getParcelableArrayList("savedList");
//        Log.d(TAG,"on restore");
//        listView = (ListView) findViewById(R.id.list);
//        //here we create a new adapter linking the bag and the
//        //listview
//        adapter =  new ArrayAdapter<Product>(this,
//                android.R.layout.simple_list_item_checked,bag );
//
//        //setting the adapter on the listview
//        listView.setAdapter(adapter);
//        //here we set the choice mode - meaning in this case we can
//        //only select one item at a time.
//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//
//    }

    //This will be called when other activities in our application
    //are finished.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1) //exited our preference screen
        {
//            Toast toast =
//                    Toast.makeText(getApplicationContext(), "back from preferences", Toast.LENGTH_LONG);
//            toast.setText("back from our preferences");
//            toast.show();

            SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
            boolean inputSwitch = prefs.getBoolean("inputTypeSwitch", false);
            final EditText quantityItem = (EditText) findViewById(R.id.insertedItemQuantity);
            final Spinner dropdownAmount = (Spinner) findViewById(R.id.dropDownAmount);

            if(inputSwitch) {
                dropdownAmount.setVisibility(View.GONE);
                quantityItem.setVisibility(View.VISIBLE);
            } else {
                dropdownAmount.setVisibility(View.VISIBLE);
                quantityItem.setVisibility(View.GONE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setPreferences() {
        //Here we create a new activity and we instruct the
        //Android system to start it
        Intent intent = new Intent(this, SettingsActivity.class);
        //startActivity(intent); //this we can use if we DONT CARE ABOUT RESULT

        //we can use this, if we need to know when the user exists our preference screens
        startActivityForResult(intent, 1);
    }

    public void getPreferences() {

        //We read the shared preferences from the
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");


        Toast.makeText(
                this,
                "Welcome back " + username + " !", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()){
            case R.id.clear_all:
                //showing dialog before anything gets deleted
                MyDialogFragment dialog = new MyDialogFragment(){
                    @Override
                    protected void positiveClick(){
                        ref.setValue(null);
                        getMyAdapter().notifyDataSetChanged();
                    }

                    @Override
                    protected void negativeClick(){
                        Toast toast = Toast.makeText(getApplicationContext(), "List not cleared", Toast
                                .LENGTH_LONG);
                        toast.show();
                    }
                };
                dialog.show(getFragmentManager(), "MyFragment");
                return true;

            case R.id.action_settings:
                setPreferences();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
