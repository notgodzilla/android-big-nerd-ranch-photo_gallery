package com.notgodzilla.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetcher {

    public static final String TAG = "FlickrFetcher";
    private static final String API_KEY = "fe9f068fdacd27c046785841a4756e9c";
    private static final String BASE_FLICKR_QUERY = "https://api.flickr.com/services/rest";

    public List<GalleryItem> fetchItems() throws IOException, JSONException {
        List<GalleryItem> items = new ArrayList<>();


        try {
            String url = Uri.parse(BASE_FLICKR_QUERY)
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG,"Received JSON" + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        } catch (IOException e) {
            throw e;
        } catch (JSONException je) {
            throw je;
        }

        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws IOException, JSONException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for(int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            //Only get gallery items with small link
            if(!photoJsonObject.has("url_s")) {
                continue;
            }

            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }

    }

    /*Fetch raw data from URL and returns as array of bytes
      Creates URL from string, opens connection to URL, then
      calls read() until connection is out of data
      Close connection then return ByteArrayOutputStream
    */
    public byte[] getUrlByBytes(String urlSpec) throws IOException{

        //Creates URL from string
        URL url = new URL(urlSpec);

        //Opens connection to URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            return out.toByteArray();

        } catch(IOException e) {
            Log.e(TAG, "Error fetching URL");
            e.printStackTrace();
            throw e;

        } finally {
            connection.disconnect();
        }
    }

    //Converts result form getUrlByBytes to a String
    public String getUrlString(String urlSpecs) throws IOException {
        return new String(getUrlByBytes(urlSpecs));
    }
}
