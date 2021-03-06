package com.example.ankush.rawanime.fragment;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.ankush.rawanime.R;
import com.example.ankush.rawanime.adapters.RecyclerViewAdapter;
import com.example.ankush.rawanime.fetch.fetchLatestAnimes;
import com.example.ankush.rawanime.models.AnimeModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PopularOnGoing extends Fragment {

    private final String  mainPageUrl="https://www4.gogoanime.se/";
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    List<AnimeModel> list;
    final String pagedetails="page-recent-release-ongoing.html?page=";
    ProgressBar progressBar;
    View rootView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            rootView=inflater.inflate(R.layout.anime_list,container,false);
            return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list= new ArrayList<>();
        adapter= new RecyclerViewAdapter(list,getContext());
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        progressBar=view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        MyAsyncTask task= new MyAsyncTask();
        task.execute();
    }

    //asynsc task
    private class MyAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            //since async works on different thread it cannot update the ui so we need to run the updating task on UI thread

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    // Stuff that updates the UI
                    adapter.notifyDataSetChanged();

                }
            });
        }

        @Override
        protected Void doInBackground(Void... voids) {
            fetchLatestAnimes fetchAnimes=new fetchLatestAnimes();
            int pageNumber=0;
            try {
                Document doc = Jsoup.connect(mainPageUrl).get();
                Elements container = doc.select("div.pagination.recent");
                Elements pagesContainer= container.select("li");
                for(Element pages:pagesContainer){
                    pageNumber++;
                    fetchAnimes.setList(mainPageUrl+pagedetails+pageNumber);
                    list.clear();
                    //fetchAnimes.getList();
                    list.addAll(fetchAnimes.getList());
                    onProgressUpdate();

                }

                Log.d("akd",""+pagesContainer.html());


            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
           getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    // Stuff that updates the UI
                    adapter.notifyDataSetChanged();

                }
            });

        }
    }


}
