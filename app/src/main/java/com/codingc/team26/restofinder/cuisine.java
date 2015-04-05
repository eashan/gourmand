package com.codingc.team26.restofinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class cuisine extends Activity {

    private ArrayList<String> selectedCuisines = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisine);
        displayCuisineList();
    }

    private HashMap<String, String> getCuisineMap (String name){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", name);
        return map;
    }

    private void displayCuisineList(){
        final ArrayList<HashMap<String, String>> cuisines = new ArrayList<HashMap<String, String>>();
        cuisines.add(getCuisineMap("Afghani"));
        cuisines.add(getCuisineMap("African"));
        cuisines.add(getCuisineMap("American"));
        cuisines.add(getCuisineMap("Andhra"));
        cuisines.add(getCuisineMap("Arabian"));
        cuisines.add(getCuisineMap("Asian"));
        cuisines.add(getCuisineMap("Awadhi"));
        cuisines.add(getCuisineMap("Bakery"));
        cuisines.add(getCuisineMap("Bengali"));
        cuisines.add(getCuisineMap("Beverages"));
        cuisines.add(getCuisineMap("Biryani"));
        cuisines.add(getCuisineMap("British"));
        cuisines.add(getCuisineMap("Burmese"));
        cuisines.add(getCuisineMap("Cafe"));
        cuisines.add(getCuisineMap("Chettinad"));
        cuisines.add(getCuisineMap("Chinese"));
        cuisines.add(getCuisineMap("Coffee and Tea"));
        cuisines.add(getCuisineMap("Continental"));
        cuisines.add(getCuisineMap("Desserts"));
        cuisines.add(getCuisineMap("European"));
        cuisines.add(getCuisineMap("Fast Food"));
        cuisines.add(getCuisineMap("Finger Food"));
        cuisines.add(getCuisineMap("French"));
        cuisines.add(getCuisineMap("German"));
        cuisines.add(getCuisineMap("Goan"));
        cuisines.add(getCuisineMap("Greek"));
        cuisines.add(getCuisineMap("Gujarati"));
        cuisines.add(getCuisineMap("Healthy Food"));
        cuisines.add(getCuisineMap("Hyderabadi"));
        cuisines.add(getCuisineMap("Ice Cream"));
        cuisines.add(getCuisineMap("Indian"));
        cuisines.add(getCuisineMap("Indonesian"));
        cuisines.add(getCuisineMap("Italian"));
        cuisines.add(getCuisineMap("Japanese"));
        cuisines.add(getCuisineMap("Juices"));
        cuisines.add(getCuisineMap("Kashmiri"));
        cuisines.add(getCuisineMap("Kerala"));
        cuisines.add(getCuisineMap("Konkan"));
        cuisines.add(getCuisineMap("Korean"));
        cuisines.add(getCuisineMap("Lebanese"));
        cuisines.add(getCuisineMap("Lucknowi"));
        cuisines.add(getCuisineMap("Maharashtrian"));
        cuisines.add(getCuisineMap("Malaysian"));
        cuisines.add(getCuisineMap("Malwani"));
        cuisines.add(getCuisineMap("Mangalorean"));
        cuisines.add(getCuisineMap("Mediterranean"));
        cuisines.add(getCuisineMap("Mexican"));
        cuisines.add(getCuisineMap("Middle Eastern"));
        cuisines.add(getCuisineMap("Mughlai"));
        cuisines.add(getCuisineMap("North Indian"));
        cuisines.add(getCuisineMap("Parsi"));
        cuisines.add(getCuisineMap("Pizza"));
        cuisines.add(getCuisineMap("Rajasthani"));
        cuisines.add(getCuisineMap("Raw Meats"));
        cuisines.add(getCuisineMap("Seafood"));
        cuisines.add(getCuisineMap("Singaporean"));
        cuisines.add(getCuisineMap("South Indian"));
        cuisines.add(getCuisineMap("Spanish"));
        cuisines.add(getCuisineMap("Street Food"));
        cuisines.add(getCuisineMap("Sushi"));
        cuisines.add(getCuisineMap("Tex-Mex"));
        cuisines.add(getCuisineMap("Thai"));
        cuisines.add(getCuisineMap("Tibetan"));
        cuisines.add(getCuisineMap("Vietnamese"));

        ListView listView = (ListView)findViewById(R.id.cuisineList);
        ListAdapter adapter=new SimpleAdapter(cuisine.this,cuisines,R.layout.cuisineitem,new String[]{"name"},new int[]{R.id.cuisinelistitem});
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {
                if(selectedCuisines.contains(cuisines.get(position).get("name"))){
                    selectedCuisines.remove(cuisines.get(position).get("name"));
                }else{
                    selectedCuisines.add(cuisines.get(position).get("name"));
                }
            }

        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cuisine, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.done) {
            String cuisines = "";
            for(int i = 0; i < selectedCuisines.size(); i++){
                cuisines += selectedCuisines.get(i) + ",";
            }
            if(cuisines != ""){
                cuisines = cuisines.substring(0, cuisines.length()-1);
            }else{
                cuisines = "All Cuisines";
            }
            setResult(Activity.RESULT_OK, new Intent().putExtra("cuisine", cuisines));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
