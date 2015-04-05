package com.codingc.team26.restofinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;


public class filter extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        final CheckBox barPresent = (CheckBox)findViewById(R.id.barcheckbox);
        final CheckBox barNotPresent = (CheckBox)findViewById(R.id.notbarcheckbox);
        barPresent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    barNotPresent.setChecked(false);
                }
            }
        });
        barNotPresent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    barPresent.setChecked(false);
                }
            }
        });
        setTitle("Filter");
        try {
            String cuisine = getIntent().getExtras().getString("cuisine");
            ((Button)findViewById(R.id.cuisinebtn)).setText(cuisine);
        }catch(Exception e){
            ((Button)findViewById(R.id.cuisinebtn)).setText("All Cuisines");
        }
        ((Button)findViewById(R.id.cuisinebtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cuisineList = new Intent(filter.this, cuisine.class);
                startActivityForResult(cuisineList, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            String cuisine = data.getExtras().getString("cuisine");
            ((Button)findViewById(R.id.cuisinebtn)).setText(cuisine);
            Log.w("Gourmand", cuisine);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String cost = "";
        if (id == R.id.done) {
            RadioGroup costGroup = (RadioGroup)findViewById(R.id.costGroup);
            int radioButtonId = costGroup.getCheckedRadioButtonId();
            if(radioButtonId == -1){
                cost = "no";
            }else{
                if(radioButtonId == R.id.radio1){
                    cost="0,500";
                }else if(radioButtonId == R.id.radio2){
                    cost="500,1000";
                }else if(radioButtonId == R.id.radio3){
                    cost="1000,2000";
                }else if(radioButtonId == R.id.radio4){
                    cost="2000,20000";
                }
            }
            Log.w("Gourmand", "cost===>"+cost);
            boolean barPresent = ((CheckBox)findViewById(R.id.barcheckbox)).isChecked();
            boolean barNotPresent = ((CheckBox)findViewById(R.id.notbarcheckbox)).isChecked();
            String bar = "no";
            if(barPresent){
                bar = "barPresent";
            }else if(barNotPresent){
                bar = "barNotPresent";
            }
            Log.w("Gourmand", "bar==>"+bar);
            String veg = "no";
            boolean pureVeg = ((CheckBox)findViewById(R.id.vegcheckbox)).isChecked();
            if(pureVeg){
                veg = "pureVeg";
            }
            Log.w("Gourmand", "veg==>"+veg);
            String cuisine = ((Button)findViewById(R.id.cuisinebtn)).getText().toString();
            Log.w("Gourmand", "cuisine==>"+cuisine);
            boolean homeDelivery = ((CheckBox)findViewById(R.id.homedelivery)).isChecked();
            String homedelivery = "no";
            if(homeDelivery){
                homedelivery = "yes";
            }
            setResult(Activity.RESULT_OK, new Intent().putExtra("cost", cost).putExtra("bar", bar).putExtra("veg", veg).putExtra("cuisine", cuisine).putExtra("homedelivery", homedelivery));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
