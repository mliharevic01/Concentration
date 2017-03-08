package nyc.ezbar.ce_concentration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
   private  JSONParser jParser = new JSONParser();

   private  ArrayList<HashMap<String, String>> productsList;


    // url to get all products list

    private HashMap<String, String> map;

    private int[] myIntArray = {R.id.location1,R.id.location2, R.id.location3, R.id.location4,R.id.location5, R.id.location6, R.id.location7, R.id.location8, R.id.location9, R.id.location10};


    private String url_all_products;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "Inventory";
    private static final String TAG_LOCATION = "LocationNumber";
    private static final String TAG_NAME = "Liquid_Name";
    private static final String TAG_QOH = "QoH";
    private static final String TAG_TQ = "TQ";
    private ImageButton bt1,bt2,bt3,bt4,bt5,bt6,bt7,bt8,bt9,bt10;
    private TextView location, qty;
    // products JSONArray
    JSONArray products = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hashmap for ListView


        url_all_products = "http://www.ezbar.nyc/android_connect/PullFromInventory.php";


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        bt1 = (ImageButton) findViewById(R.id.location1);
        bt2 = (ImageButton) findViewById(R.id.location2);
        bt3 = (ImageButton) findViewById(R.id.location3);
        bt4 = (ImageButton) findViewById(R.id.location4);
        bt5 = (ImageButton) findViewById(R.id.location5);
        bt6 = (ImageButton) findViewById(R.id.location6);
        bt7 = (ImageButton) findViewById(R.id.location7);
        bt8 = (ImageButton) findViewById(R.id.location8);
        bt9 = (ImageButton) findViewById(R.id.location9);
        bt10 = (ImageButton) findViewById(R.id.location10);
        location = (TextView)  findViewById(R.id.liqinput);
        qty = (TextView) findViewById(R.id.qtyinput);


        new LoadAllProducts().execute();


        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setText(productsList.get(0).get(TAG_NAME));
                qty.setText(" " +productsList.get(0).get(TAG_QOH)+"mL");
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setText(productsList.get(1).get(TAG_NAME));
                qty.setText(" " +productsList.get(1).get(TAG_QOH)+"mL");
            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setText(productsList.get(2).get(TAG_NAME));
                qty.setText(" " +productsList.get(2).get(TAG_QOH)+"mL");
            }
        });

        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setText(productsList.get(3).get(TAG_NAME));
                qty.setText(" " +productsList.get(3).get(TAG_QOH)+"mL");
            }
        });

        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setText(productsList.get(4).get(TAG_NAME));
                qty.setText(" " +productsList.get(4).get(TAG_QOH)+"mL");
            }
        });

        bt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setText(productsList.get(5).get(TAG_NAME));
                qty.setText(" " +productsList.get(5).get(TAG_QOH)+"mL");
            }
        });

        bt7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setText(productsList.get(6).get(TAG_NAME));
                qty.setText(" " +productsList.get(6).get(TAG_QOH)+"mL");
            }
        });
        bt8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setText(productsList.get(7).get(TAG_NAME));
                qty.setText(" " +productsList.get(7).get(TAG_QOH)+"mL");
            }
        });

        bt9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setText(productsList.get(8).get(TAG_NAME));
                qty.setText(" " +productsList.get(8).get(TAG_QOH)+"mL");
            }
        });

        bt10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setText(productsList.get(9).get(TAG_NAME));
                qty.setText(" " +productsList.get(9).get(TAG_QOH)+"mL");
            }
        });




    }

    class LoadAllProducts extends AsyncTask<String, String, String> {
/*
*
         * Before starting background thread Show Progress Dialog
         */


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url*/

        @Override
        protected String doInBackground(String... args)  {
            // Building Parameters
            HashMap<String,String > params = new HashMap<>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_products, "POST", params);

            productsList = new ArrayList<HashMap<String, String>>();

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_PRODUCTS);

                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable
//                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
                        String location = c.getString(TAG_LOCATION) + ".";
                        String qoh = c.getString(TAG_QOH);
                        String tq = c.getString(TAG_TQ);
                        //String abv = c.getString("Genre");

                        // creating new HashMap
                        map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        //  map.put(TAG_PID, id);
                        map.put(TAG_NAME, name);
                        map.put(TAG_LOCATION,location);
                        map.put(TAG_QOH, qoh);
                        map.put(TAG_TQ, tq);

                        // adding HashList to ArrayList
                        productsList.add(map);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



            return null;
        }
/*
*
         * After completing background task Dismiss the progress dialog
         **/

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread

            runOnUiThread(new Runnable() {
                public void run() {
/**
 * Updating parsed JSON data into ListView
 *
 * */
                    for (int i = 0; i < productsList.size(); i++){
                        String current = productsList.get(i).get(TAG_QOH);
                        System.out.println("Current:" + current);
                        String total = productsList.get(i).get(TAG_TQ);
                        System.out.println("Total: " + total);
                        System.out.println("Value:" + Integer.valueOf(current)/Integer.valueOf(total));
                        if(Double.valueOf(current)/Double.valueOf(total) >= .5){
                            Drawable replacer = getResources().getDrawable(R.drawable.half_full);
                            ImageButton loc = (ImageButton) findViewById(myIntArray[i]);
                            loc.setImageDrawable(replacer);
                        }
                        else if(Double.valueOf(current)/Double.valueOf(total) >= .25){
                            Drawable replacer = getResources().getDrawable(R.drawable.quarter_half);
                            ImageButton loc = (ImageButton) findViewById(myIntArray[i]);
                            loc.setImageDrawable(replacer);
                        }
                        else if(Double.valueOf(current)/Double.valueOf(total) > 0){
                            Drawable replacer = getResources().getDrawable(R.drawable.lessthanaquarter);
                            ImageButton loc = (ImageButton) findViewById(myIntArray[i]);
                            loc.setImageDrawable(replacer);
                        }
                        else{
                            Drawable replacer = getResources().getDrawable(R.drawable.empty);
                            ImageButton loc = (ImageButton) findViewById(myIntArray[i]);
                            loc.setImageDrawable(replacer);
                        }

                    }
                }
            });


        }

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


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
            qty.setText("");
            location.setText("");
            new LoadAllProducts().execute();

            return true;
        }
        if(id == R.id.addBottle){
            Intent insert_intent = new Intent(this,InsertActivity.class);;
            this.startActivity(insert_intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent recipies_intent = new Intent(this, RecipesActivity.class);;
            this.startActivity(recipies_intent);
        } else if (id == R.id.nav_stats) {
            Intent inventory_intent = new Intent(this, AllProductsActivity.class);;
            this.startActivity(inventory_intent);
        }else if (id == R.id.nav_search){
            Intent search_intent = new Intent(this, SearchActivity.class);
            this.startActivity(search_intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
