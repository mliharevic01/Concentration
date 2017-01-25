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
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SearchActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> productsList;

    // url to get all products list
    private String url_all_products;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "Liquids";
    private static final String TAG_PID = "L_PK";
    private static final String TAG_NAME = "Liquid_Name";

    private android.support.v7.widget.SearchView searchView;
    private BaseAdapter adapter;
    ListView list;

    // products JSONArray
    JSONArray products = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_products);

        list = (ListView) findViewById(R.id.searchlistview) ;

        url_all_products = "http://www.ezbar.nyc/android_connect/get_all_productsSearchtest.php?Name=";

        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        //new LoadAllProducts().execute();

/*        adapter = new SimpleAdapter(
                SearchActivity.this, productsList,
                R.layout.list_item, new String[]{
                TAG_NAME},
                new int[]{ R.id.name});
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);*/

        handleIntent(getIntent());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);



        final MenuItem searchItem = menu.findItem(R.id.search);
        //searchItem.expandActionView();
        searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.search).getActionView();
        searchView.onActionViewExpanded();
        searchView.clearFocus();
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = query.replace(" ", "%20");
                url_all_products = "http://www.ezbar.nyc/android_connect/get_all_productsSearchtest.php?Name=" + query;
/*                adapter = new SimpleAdapter(
                        SearchActivity.this, productsList,
                        R.layout.list_item, new String[]{
                        ""},
                        new int[]{ R.id.name});
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                //list.setAdapter(null);
                list.clearChoices();*/
                productsList.clear();
                new LoadAllProducts().execute();
/*                adapter.notifyDataSetChanged();
                list.setAdapter(adapter);*/
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productsList.clear();
                return true;
            }

        });

        super.openOptionsMenu();
        return true;

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

/**
     * Background Async Task to Load all product by making HTTP Request*/


    class LoadAllProducts extends AsyncTask<String, String, String> {

/**
         * Before starting background thread Show Progress Dialog*/


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SearchActivity.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

/**
         * getting All products from url*/


        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
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
                        //String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
                        String abv = c.getString("ABV");

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        //map.put(TAG_PID, id);
                        map.put(TAG_NAME, name);
                        map.put("ABV", abv);

                        // adding HashList to ArrayList
                        productsList.add(map);
                    }
                } /*else {
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
         *
*/

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            //list.invalidateViews();

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
/*
*
                     * Updating parsed JSON data into ListView
                     *
*/
/*                    if(adapter != null){
                        adapter = null;
                        list.setAdapter(adapter);
                       // adapter.notifyDataSetChanged();
                    }*/


                    adapter = new SimpleAdapter(
                            SearchActivity.this, productsList,
                            R.layout.list_item, new String[]{
                            TAG_NAME, "ABV"},
                            new int[]{ R.id.name, R.id.abv});
                    // updating listview
                    //list.invalidateViews();
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    //setListAdapter(adapter);
                }
            });

        }

    }
}
