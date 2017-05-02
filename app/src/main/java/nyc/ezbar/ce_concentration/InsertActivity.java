package nyc.ezbar.ce_concentration;

import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import junit.framework.Test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InsertActivity extends AppCompatActivity {


    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    // url to get all products list

    private String url_all_products;
    private List<String> list = new ArrayList<String>();
    private List<String> listGenre = new ArrayList<String>();
    HashMap<String, String> map;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "Inventory";
    private static final String TAG_LOCATION = "LocationNumber";
    private static final String TAG_NAME = "Liquid_Name";
    private static final String TAG_QOH = "QoH";
    private static final String TAG_TQ = "TQ";
    private Spinner spinner2;
    private EditText edittext;
    private String locationB;
    private Spinner mySpinner;
    private Spinner mySpinner2;
    private String liquorB ;
    private Spinner mySpinner3;
    private String tQuantity ;
    private Button submit;
    private String strEnteredVal;
    private String editText;
    private String urlUpdateInventory;


    // products JSONArray
    JSONArray products = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();

        //Links for products and inventory
        url_all_products = "http://www.ezbar.nyc/android_connect/PullFromInventory.php";
        urlUpdateInventory = "http://www.ezbar.nyc/android_connect/InsertItemIntoInventory.php?";


        // Loading products in Background Thread
        new LoadAllProducts().execute();

        // Creates spinner variables
        mySpinner=(Spinner) findViewById(R.id.spinner1);
        mySpinner2=(Spinner) findViewById(R.id.spinner2);
        mySpinner3=(Spinner) findViewById(R.id.spinner3);


        //Creates edittext Variables
        edittext = (EditText) findViewById(R.id.editText);
        //Sets Filter to only except certain input from User
        edittext.setFilters(new InputFilter[]{new InputFilterMinMax("0", "2000")});

        submit = (Button) findViewById(R.id.button);
        submit.setOnClickListener(new View.OnClickListener() {
            boolean isSumbittable;

            @Override
            public void onClick(View v) {
                locationB = mySpinner.getSelectedItem().toString();
                liquorB = mySpinner2.getSelectedItem().toString();
                tQuantity = mySpinner3.getSelectedItem().toString();
                editText = edittext.getText().toString();

                //Sets Text so user sees that they need to enter quantity
                if (editText.equals(""))
                {
                    Toast.makeText(InsertActivity.this, "Please enter a Quantity", Toast.LENGTH_LONG).show();
                }
                //Chacks
                else if(Integer.parseInt(editText) > Integer.parseInt(tQuantity))
                {
                    Toast.makeText(InsertActivity.this, "You cannot enter a value that is larger the Total Quantity", Toast.LENGTH_LONG).show();
                    edittext.setText("");
                   //edittext.setFilters(new InputFilter[]{new InputFilterMinMax(tQuantity, tQuantity)});
                }

                else if(locationB != "" && liquorB != "" && tQuantity != "" && tQuantity != "" && Integer.parseInt(editText) < Integer.parseInt(tQuantity))
                {
                    int length = liquorB.length();
                    char[] chars = liquorB.toCharArray();
                    int spaceCount = 0;
                    for (int i = 0; i < length; i++) {
                        if (chars[i] == ' ') {
                            spaceCount++;
                        }
                    }
                    int newLength = length + 2 * spaceCount;
                    char [] charsNew = new char [newLength];
                    for (int i = length - 1; i >= 0; i--) {
                        if (chars[i] == ' ') {
                            charsNew[newLength - 1] = '0';
                            charsNew[newLength - 2] = '2';
                            charsNew[newLength - 3] = '%';
                            newLength = newLength - 3;
                        } else {
//				System.out.println(chars[i]);
                            charsNew[newLength - 1] = chars[i];
                            newLength = newLength - 1;
                        }
                    }
                    liquorB = String.valueOf(charsNew);
                    System.out.println(liquorB);
                    urlUpdateInventory = urlUpdateInventory + "LocNum=" + locationB + "&LName=" + liquorB + "&Gen=" + listGenre.get(mySpinner2.getSelectedItemPosition()) + "&Quan=" + edittext.getText().toString() + "&TQuan=" + tQuantity;
                    System.out.println(urlUpdateInventory);

                    Thread thread = new Thread(new Runnable(){
                        public void run() {
                            try {
                                HashMap<String,String > params = new HashMap<>();

                                // getting JSON string from URL
                                JSONObject jsonFile = jParser.makeHttpRequest(urlUpdateInventory, "POST", params);

                                // Check your log cat for JSON reponse
                                Log.d("All Products: ", jsonFile.toString());

                                try {
                                    // Checking for SUCCESS TAG
                                    int success = jsonFile.getInt(TAG_SUCCESS);
                                    if (success == 1) {
                                        // products found
                                        // Getting Array of Products
                                        products = jsonFile.getJSONArray(TAG_PRODUCTS);

                                        // looping through All Products
                                        for (int i = 0; i < products.length(); i++) {
                                            JSONObject c = products.getJSONObject(i);

                                            // Storing each json item in variable
//                        String id = c.getString(TAG_PID);
                                            String name = c.getString(TAG_NAME);
                                            String location = c.getString(TAG_LOCATION) + ".";
                                            String qoh = c.getString(TAG_QOH);
                                            String tq = c.getString(TAG_TQ);
                                            String genre = c.getString("Genre");

                                            // creating new HashMap
                                            map = new HashMap<String, String>();

                                            // adding each child node to HashMap key => value
                                            //  map.put(TAG_PID, id);
                                            map.put(TAG_NAME, name);
                                            map.put(TAG_LOCATION,location);
                                            map.put(TAG_QOH, qoh);
                                            map.put(TAG_TQ, tq);
                                            list.add(i, name);
                                            listGenre.add(i,genre);

                                            // adding HashList to ArrayList
                                            productsList.add(map);
                                        }
                                    }/* else {
                    // no products found
                    // Launch Add New product Activity
Intent i = new Intent(getApplicationContext(),
                            NewProductActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                }*/
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();
                    Toast.makeText(InsertActivity.this, "Your inventory has been updated!", Toast.LENGTH_LONG).show();
                    onBackPressed();



                }
            }
        });

    }

    public void replaceSpace(String s1, int length) {
        char[] chars = s1.toCharArray();
        int spaceCount = 0;
        for (int i = 0; i < length; i++) {
            if (chars[i] == ' ') {
                spaceCount++;
            }
        }
        int newLength = length + 2 * spaceCount;
        char [] charsNew = new char [newLength];
        for (int i = length - 1; i >= 0; i--) {
            if (chars[i] == ' ') {
                charsNew[newLength - 1] = '0';
                charsNew[newLength - 2] = '2';
                charsNew[newLength - 3] = '%';
                newLength = newLength - 3;
            } else {
//				System.out.println(chars[i]);
                charsNew[newLength - 1] = chars[i];
                newLength = newLength - 1;
            }
        }
        System.out.println("Output String : " + String.valueOf(charsNew));
    }

    public class InputFilterMinMax implements InputFilter {
        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            Toast.makeText(InsertActivity.this, "You cannot enter a value over 2000mL", Toast.LENGTH_LONG).show();
            return "";


        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }

    }



    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }
/*
*
     * Background Async Task to Load all product by making HTTP Request*/


    class LoadAllProducts extends AsyncTask<String, String, String> {
/*
*
         * Before starting background thread Show Progress Dialog
         */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(InsertActivity.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url*/


        protected String doInBackground(String... args) {
            // Building Parameters
            HashMap<String,String > params = new HashMap<>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_products, "POST", params);

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
                        String genre = c.getString("Genre");

                        // creating new HashMap
                        map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        //  map.put(TAG_PID, id);
                        map.put(TAG_NAME, name);
                        map.put(TAG_LOCATION,location);
                        map.put(TAG_QOH, qoh);
                        map.put(TAG_TQ, tq);
                        list.add(i, name);
                        listGenre.add(i,genre);

                        // adding HashList to ArrayList
                        productsList.add(map);
                    }
                }/* else {
                    // no products found
                    // Launch Add New product Activity
Intent i = new Intent(getApplicationContext(),
                            NewProductActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                }*/
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
/*
*
         * After completing background task Dismiss the progress dialog
         **/

        public void addItemsOnSpinner2() {


        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
/**
 * Updating parsed JSON data into ListView
 * */
                    spinner2 = (Spinner) findViewById(R.id.spinner2);
                    //List<String> list = new ArrayList<String>();
                    ListAdapter adapter = new SimpleAdapter(
                            InsertActivity.this, productsList,
                            R.layout.list_item_inventory, new String[]{
                            TAG_LOCATION, TAG_NAME, TAG_QOH},
                            new int[]{ R.id.pid, R.id.name, R.id.abv});
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(InsertActivity.this,
                            android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner2.setAdapter(dataAdapter);

                    // updating listview

                    // setListAdapter(adapter);
                }
            });

        }

    }
}
