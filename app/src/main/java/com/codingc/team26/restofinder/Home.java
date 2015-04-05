package com.codingc.team26.restofinder;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Home extends Activity {
    String query;
    SessionManagement session;
    List<HashMap<String, String>> restaurants = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> mp = new HashMap<String, String>();

        final EditText e=(EditText) findViewById((R.id.search_restaurant));

        e.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT){
                    String query = ((EditText) findViewById(R.id.search_restaurant)).getText().toString();
                    Intent result = new Intent(Home.this, result.class);
                    result.putExtra("query", query);
                    result.putExtra("type", "restaurant");
                    startActivity(result);
                }
                return false;
            }
        });

        e.addTextChangedListener(new TextWatcher(){

            public void afterTextChanged(Editable s) { }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){
                final String query=e.getText().toString();
                if(query.length() > 3) {
                    Log.w("Gourmand", "query ==> " + query);
                    restaurants.clear();
                    new search().execute(query);
                }
            }
        });

    }


    private class search extends AsyncTask<String, Integer, JSONObject > {


        @Override
        protected JSONObject doInBackground(String...params){

            String url = "http://104.131.56.170/search";
            restaurants.clear();
            HashMap<String, String> user = session.getUserDetails();
            String email = user.get("email");
            String password = user.get("password");
            String basicAuth = "Basic " + new String(Base64.encode((email + ":" + password).getBytes(), Base64.NO_WRAP));
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("q", params[0]));
            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(url+"?"+paramsString, basicAuth);

            HttpGet httpGet = new HttpGet(url);


            return json;

        }
        protected void onPostExecute(JSONObject json){

            try {
                JSONArray data = json.getJSONArray("data");
                //  ListAdapter adapter=new SimpleAdapter(Home.this,restaurants,R.layout.hlist,new String[]{"name","type"},new int[]{R.id.nameh,R.id.type});
                for(int i = 0; i < data.length(); i++){
                    try{
                        JSONObject restaurant = data.getJSONObject(i);
                        final String name = restaurant.getString("name");
                        final String type=restaurant.getString("type");
                        HashMap<String,String> p;
                        p = new HashMap<String,String>();
                        p.put("name",name);
                        p.put("type",type);

                        restaurants.add(i,p);

                        Log.w("Gourmand", "name ==> " + name + " type ===>" + type );
                    }catch(JSONException e){
                        Log.w("Gourmand", "failed to parse jsonobject");
                    }
                }
                ListView listview = (ListView) findViewById(R.id.homelist);
                ListAdapter adapter=new SimpleAdapter(Home.this,restaurants,R.layout.hlist,new String[]{"name","type"},new int[]{R.id.nameh,R.id.type});
                listview.setAdapter(adapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {
                        HashMap<String, String> search_result = new HashMap<String, String>();
                        search_result = restaurants.get(position);
                        Log.w("Gourmand", "type===>" + search_result.get("type"));
                        Log.w("Gourmand", "name===>" + search_result.get("name"));
                        Intent result = new Intent(Home.this, result.class);
                        result.putExtra("type", search_result.get("type"));
                        result.putExtra("query", search_result.get("name"));
                        startActivity(result);
                    }

                });
            }catch (Exception JSONException){
                Log.w("Gourmand", "failed to parse json");
            }

        }

    }

}
