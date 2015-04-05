package com.codingc.team26.restofinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class restaurant_menu extends Activity {

    SessionManagement session;
    String id;

    DisplayImageOptions display;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);

        configureDefaultImageLoader(getApplicationContext());

        session = new SessionManagement(getApplicationContext());

        id = getParent().getIntent().getExtras().getString("id");

        new getMenu().execute(id);

    }

    public static void configureDefaultImageLoader(Context context) {

        ImageLoaderConfiguration defaultConfiguration
                = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(defaultConfiguration);
    }

    static class ViewHolder {
        ImageView imageView;
    }

    private class ImageAdapter extends BaseAdapter {
        ArrayList<String> urls = new ArrayList<String>();

        public ImageAdapter(ArrayList<String> urls){
            this.urls = urls;
        }

        @Override
        public int getCount() {
            return urls.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {
            View view = convertView;
            final ViewHolder gridViewImageHolder;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.menuitem, parent, false);
                gridViewImageHolder = new ViewHolder();
                gridViewImageHolder.imageView = (ImageView) view.findViewById(R.id.image);
                gridViewImageHolder.imageView.setMaxHeight(80);
                gridViewImageHolder.imageView.setMaxWidth(80);
                view.setTag(gridViewImageHolder);
            } else {
                gridViewImageHolder = (ViewHolder) view.getTag();
            }
            imageLoader.displayImage(urls.get(position)
                    ,gridViewImageHolder.imageView
                    ,display);

            return view;
        }
    }


    private class getMenu extends AsyncTask<String, Integer, JSONObject >{

        @Override
        protected JSONObject doInBackground(String...params){
            String url = "http://104.131.56.170/get-menu";
            HashMap<String, String> user = session.getUserDetails();
            String email = user.get("email");
            String password = user.get("password");
            String basicAuth = "Basic " + new String(Base64.encode((email + ":" + password).getBytes(), Base64.NO_WRAP));
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id", params[0]));
            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
            JSONParser jParser = new JSONParser();
            JSONObject json = jParser.getJSONFromUrl(url+"?"+paramsString, basicAuth);
            return json;
        }

        protected void onPostExecute(JSONObject json){

            final ArrayList<String> list = new ArrayList<String>();
            try {
                JSONArray data = json.getJSONArray("data");
                for(int i = 0; i < data.length(); i++){
                    list.add(data.getString(i));
                    Log.w("Gourmand", "imageurl==>"+data.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            imageLoader = ImageLoader.getInstance();

            display = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.place_holder)
                    .showImageForEmptyUri(R.drawable.hand)
                    .showImageOnFail(R.drawable.big_problem)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();

            final GridView gridview = (GridView) findViewById(R.id.gridview);
            gridview.setAdapter(new ImageAdapter(list));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    gridview.getAdapter().getItem(position);
                    Intent intent = new Intent(restaurant_menu.this, fullScreen.class);
                    intent.putExtra("position", position);
                    intent.putExtra("url", list.get(position));
                    startActivity(intent);
                }
            });
        }

    }

}
