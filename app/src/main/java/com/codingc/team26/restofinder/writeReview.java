package com.codingc.team26.restofinder;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class writeReview extends Activity {

    SessionManagement session;
    String rid, email, review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        session = new SessionManagement(getApplicationContext());
        setTitle("Write A Review");
        rid = getIntent().getExtras().getString("rid");
        email = session.getUserDetails().get("email");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.write_review, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.done) {
            review = ((EditText)findViewById(R.id.addReview)).getText().toString();
            if(!review.isEmpty()) {
                new addReview().execute(review, email, rid);
                finish();
            }else{
                Toast.makeText(getApplicationContext(), "Field is empty", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class addReview extends AsyncTask<String, Integer, Void>{

        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://104.131.56.170/add-review");

            try {
                // Add your data
                HashMap<String, String> user = session.getUserDetails();
                String email = user.get("email");
                String password = user.get("password");
                String basicAuth = "Basic " + new String(Base64.encode((email + ":" + password).getBytes(), Base64.NO_WRAP));
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("review", params[0]));
                nameValuePairs.add(new BasicNameValuePair("email",params[1]));
                nameValuePairs.add(new BasicNameValuePair("rid",params[2]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httppost.setHeader("Authorization", basicAuth);
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                Log.w("Gourmand", "status====>" + status);
                return null;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return null;
            }
        }
    }

}
