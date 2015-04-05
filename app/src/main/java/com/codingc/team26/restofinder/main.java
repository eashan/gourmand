package com.codingc.team26.restofinder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.app.TabActivity;

public class main extends TabActivity {

    SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Third tab");

        tab1.setIndicator("Home");
        tab1.setContent(new Intent(this, Home.class));

        tab2.setIndicator("Nearby");
        tab2.setContent(new Intent(this, near.class));

        tab3.setIndicator("Profile");
        tab3.setContent(new Intent(this, Profile.class));

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);

        session = new SessionManagement(getApplicationContext());
        session.checkLogin();
    }


}
