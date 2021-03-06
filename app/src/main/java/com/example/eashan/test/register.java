package com.example.eashan.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

//import com.daimajia.androidanimations.library.Techniques;
//import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class register extends Activity {
    String name;
    String email;
    String password;
    private ProgressBar pb;
    SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //SharedPreferences pref = getApplicationContext().getSharedPreferences("Pref", 0); // 0 - for private mode
        //final SharedPreferences.Editor editor = pref.edit();
        final EditText nameText=(EditText)findViewById(R.id.name);
        final EditText emailText=(EditText)findViewById(R.id.email);
        final  EditText passwordText=(EditText)findViewById(R.id.password);
        pb=(ProgressBar)findViewById(R.id.progressBar1);
        pb.setVisibility(View.GONE);


        Button button = (Button) findViewById(R.id.register);

        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

        session = new SessionManagement(getApplicationContext());


                // TODO Auto-generated method stub


                if (!isNetworkConnected())
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Network error";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }


                else if (emailText.toString().equals(""))
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Please enter emailId";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }
                else if ( emailText.toString().contains("@"))
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Invalid Email Id";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                } else
                {

                    name = nameText.getText().toString();
                    email = emailText.getText().toString();
                    password = passwordText.getText().toString();
                    Log.w("Gourmand", "name=" + name + "&email=" + email + "&password=" + password);
                    // new RegisterUser().execute(null,null,null);
                    pb.setVisibility(View.VISIBLE);
                    new registerUser().execute(name,email,password);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private boolean isNetworkConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            for (NetworkInfo info : networkInfo) {
                if (info.getState() == NetworkInfo.State.CONNECTED)
                    return true;
            }
        }

        return false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private class registerUser extends AsyncTask<String, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return postData(params[0],params[1],params[2]);
        }

        @Override
        protected void onPostExecute(Boolean result){
            pb.setVisibility(View.GONE);
            if(result){
                Log.w("Gourmand", "inside onPostExecute method ===> true");
                Toast.makeText(register.this, "Registeration successful", Toast.LENGTH_LONG).show();
                session.createLoginSession(name, email, password);
                finish();
            }else{
                Log.w("Gourmand", "inside onPostExecute method ===> false");
                Toast.makeText(register.this, "Registeration unsuccessful", Toast.LENGTH_LONG).show();
            }
        }
        protected void onProgressUpdate(Integer... progress){
            pb.setProgress(progress[0]);
        }

        private Boolean postData(String name,String email,String password) {
            // Create a new HttpClient and Post Header
            Log.w("inpost", "name=" + name + "&email=" + email + "&password=" + password);


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://14.97.169.176/gourmand/register");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("name", name));
                nameValuePairs.add(new BasicNameValuePair("email",email));
                nameValuePairs.add(new BasicNameValuePair("password",password));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int status = response.getStatusLine().getStatusCode();
                Log.w("Gourmand", "status====>" + status);
                return status == 200;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }

        }

    }
}


