package nyc.ezbar.ce_concentration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AllProductsActivity extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    // url to get all products list

    private String url_all_products;

    HashMap<String, String> map;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "Inventory";
    private static final String TAG_LOCATION = "LocationNumber";
    private static final String TAG_NAME = "Liquid_Name";
    private static final String TAG_QOH = "QoH";
    private static final String TAG_TQ = "TQ";

    // products JSONArray
    JSONArray products = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory);

        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();

        url_all_products = "http://www.ezbar.nyc/android_connect/PullFromInventory.php";

        // Loading products in Background Thread
        new LoadAllProducts().execute();
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
            pDialog = new ProgressDialog(AllProductsActivity.this);
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

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
/**
                     * Updating parsed JSON data into ListView
                     * */

                    ListAdapter adapter = new SimpleAdapter(
                            AllProductsActivity.this, productsList,
                            R.layout.list_item_inventory, new String[]{
                            TAG_LOCATION, TAG_NAME, TAG_QOH},
                            new int[]{ R.id.pid, R.id.name, R.id.abv});
                    // updating listview

                    setListAdapter(adapter);
                }
            });

        }

    }
}
