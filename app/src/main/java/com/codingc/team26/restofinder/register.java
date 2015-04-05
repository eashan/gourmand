package com.codingc.team26.restofinder;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

//import com.daimajia.androidanimations.library.Techniques;
//import com.daimajia.androidanimations.library.YoYo;


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
        final EditText nameText=(EditText)findViewById(R.id.nameh);
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


    private class registerUser extends AsyncTask<String, Integer, JSONObject>{

        @Override
        protected JSONObject doInBackground(String... params) {
            // TODO Auto-generated method stub
            return postData(params[0],params[1],params[2]);
        }

        @Override
        protected void onPostExecute(JSONObject json){
            pb.setVisibility(View.GONE);
            Boolean result = null;
            String message = null;
            try {
                if(json != null) {
                    result = json.getInt("status") == 200;
                    message = json.getString("message");
                    Log.w("Gourmand", "status==>" + result + "&message==>" + message);
                    if(result){
                        Toast.makeText(register.this, message, Toast.LENGTH_LONG).show();
                        session.createLoginSession(name, email, password);
                        finish();
                    }else{
                        Toast.makeText(register.this, message, Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(register.this, "Cannot connect to the server", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.w("Gourmand", "failed to parse json");
            }
        }
        protected void onProgressUpdate(Integer... progress){
            pb.setProgress(progress[0]);
        }

        private JSONObject postData(String name,String email,String password) {
            // Create a new HttpClient and Post Header

            String url = "http://104.131.56.170/register";

            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("name", name));
            nameValuePairs.add(new BasicNameValuePair("email",email));
            nameValuePairs.add(new BasicNameValuePair("password",password));
            JSONParser jsonParser = new JSONParser();
            JSONObject json = jsonParser.postUrl(url, nameValuePairs);
            return json;
        }

    }
}


