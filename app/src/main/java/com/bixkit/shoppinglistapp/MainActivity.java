package com.bixkit.shoppinglistapp;

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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText addedItem;
    EditText quantityItem;
    String listQuantity;


    private static final String TAG = "com.bixkit.myfirstapp";


    ArrayAdapter<Product> adapter;
    ListView listView;
    ArrayList<Product> bag = new ArrayList<Product>();

    public ArrayAdapter getMyAdapter()
    {
        return adapter;
    }

    Product lastDeletedProduct;
    int lastDeletedPosition;

    public void saveCopy(){
        lastDeletedPosition = listView.getCheckedItemPosition();
        lastDeletedProduct = bag.get(lastDeletedPosition);
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
        adapter =  new ArrayAdapter<Product>(this,
                android.R.layout.simple_list_item_checked,bag );

        //setting the adapter on the listview
        listView.setAdapter(adapter);
        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        Button addButton = (Button) findViewById(R.id.addButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);

        int position = -1;
        if (savedInstanceState!=null)
        {
            Log.d(TAG,"on create");
            bag = savedInstanceState.getParcelableArrayList("savedList");


        }




        //add some stuff to the list
//        bag.add(new Product("bananas",10));
//        bag.add(new Product("apples",2));


        addedItem = (EditText) findViewById(R.id.insertedItem);
        quantityItem = (EditText) findViewById(R.id.insertedItemQuantity);
        listQuantity = "10";
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = addedItem.getText().toString();
                int quantity = Integer.valueOf(quantityItem.getText().toString());
//                bag.add(new Product(addedItem.toString(),quantityItem));
//                        addedItem.getText().toString() + "" + quantityItem.getText().toString());

                bag.add(new Product(name, quantity, listQuantity));

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
                bag.remove(index);
                getMyAdapter().notifyDataSetChanged();

                final View parent = listView;
                Snackbar snackbar = Snackbar
                        .make(listView, "Item Deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bag.add(lastDeletedPosition, lastDeletedProduct);
                                getMyAdapter().notifyDataSetChanged();
                                Snackbar snackbar = Snackbar.make(listView, "Item restored!", Snackbar
                                        .LENGTH_SHORT);
                                snackbar.show();
                            }
                        });
                snackbar.show();

            }
        });


    }



    //This method is called before our activity is destroyed
    protected void onSaveInstanceState(Bundle outState) {
        //ALWAYS CALL THE SUPER METHOD
        super.onSaveInstanceState(outState);
        Log.d(TAG, "on save");
        outState.putParcelableArrayList("savedList", bag);

    }









    //this is called when our activity is recreated, but
    //AFTER our onCreate method has been called
    //EXTREMELY IMPORTANT DETAIL
    //This is an alternative place to restore the data
    //This can also be done in the onCreate method like in the book.
    protected void onRestoreInstanceState(Bundle savedState) {
        //MOST UI elements will automatically store the information
        //if we call the super.onRestoreInstaceState
        //but other data will be lost.
        super.onRestoreInstanceState(savedState);

        bag = savedState.getParcelableArrayList("savedList");
        Log.d(TAG,"on restore");
        listView = (ListView) findViewById(R.id.list);
        //here we create a new adapter linking the bag and the
        //listview
        adapter =  new ArrayAdapter<Product>(this,
                android.R.layout.simple_list_item_checked,bag );

        //setting the adapter on the listview
        listView.setAdapter(adapter);
        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
