package com.codingc.team26.restofinder;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

public class information extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Third Tab");
        TabHost.TabSpec tab4 = tabHost.newTabSpec("Forth Tab");
        TabHost.TabSpec tab5 = tabHost.newTabSpec("Fifth Tab");

        tab1.setIndicator("Info");
        tab1.setContent(new Intent(this, restaurant_info.class));

        tab2.setIndicator("Map");
        tab2.setContent(new Intent(this, restaurant_map.class));

        tab3.setIndicator("Menu");
        tab3.setContent(new Intent(this, restaurant_menu.class));

        tab4.setIndicator("Pics");
        tab4.setContent(new Intent(this, restaurant_photos.class));

        tab5.setIndicator("Reviews");
        tab5.setContent(new Intent(this, restaurant_reviews.class));

        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        tabHost.addTab(tab4);
        tabHost.addTab(tab5);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.writereview) {
            Intent writeRestaurantReview = new Intent(information.this, writeReview.class);
            writeRestaurantReview.putExtra("rid", getIntent().getExtras().getString("id"));
            startActivity(writeRestaurantReview);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
