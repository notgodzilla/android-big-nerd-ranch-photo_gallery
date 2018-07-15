package com.notgodzilla.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView photoRecyclerView;
    private List<GalleryItem> photoGalleryItems = new ArrayList<>();

    //newInstance constructor
    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //Starts AsyncTask, starts bg thread and calls doInBackground
        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        photoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        //Sets up appropriate adapter every time onCreateView is called
        setUpAdapter();
        return v;
    }

    private void setUpAdapter() {

        //First checks if fragment has been attached to an activity
        if(isAdded()) {
            photoRecyclerView.setAdapter(new PhotoAdapter(this.photoGalleryItems) );
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            try {
                return new FlickrFetcher().fetchItems();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            photoGalleryItems = galleryItems;
            setUpAdapter();
        }
    }



    public class PhotoHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public PhotoHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }

        public void bindGalleryItem(GalleryItem item) {
            textView.setText(item.toString());
        }

    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> galleryItems;

        public PhotoAdapter(List<GalleryItem> items) {
            this.galleryItems = items;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder photoHolder, int i) {
            GalleryItem galleryItem = this.galleryItems.get(i);
            photoHolder.bindGalleryItem(galleryItem);

        }

        @Override
        public int getItemCount() {
            return galleryItems.size();
        }
    }
}
