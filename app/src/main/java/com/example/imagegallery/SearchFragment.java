package com.example.imagegallery;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private ImageList list;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private LinearLayoutManager layoutManager;
    private ProgressBar pb;
    private int pageCount = 1;
    private NestedScrollView nestedScrollView;
    String query = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        query = getArguments().getString("query");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.idRV);
        pb = view.findViewById(R.id.idPB);
        nestedScrollView = view.findViewById(R.id.idNestedScrollView);

        if (query != null && !query.isEmpty()){
            getImagesByQuery(query);
        }
        setUpPagination(true);
        return view;
    }

    private void setUpPagination(boolean isPaginationAllowed) {
        if (isPaginationAllowed){
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                    (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                        if (scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                            pb.setVisibility(View.VISIBLE);
                            getImages(++pageCount);

                            //Toast.makeText(this, "page number: "+pageCount, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                    (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    });
        }

    }

    private void getImages(Integer pageCount) {
        Methods methods = RetrofitClient.getRetrofitInstance().create(Methods.class);
        Call<ImageList> call = methods.getAllData(pageCount);

        call.enqueue(new Callback<ImageList>() {
            @Override
            public void onResponse(Call<ImageList> call, Response<ImageList> response) {
                if (response.isSuccessful() && response.body() != null){
                    recyclerView.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);

                    list =  response.body();
                    layoutManager = new LinearLayoutManager(getContext());
                    imageAdapter = new ImageAdapter(getContext(),list);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(imageAdapter);
                    imageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ImageList> call, Throwable t) {
                Snackbar snackbar = Snackbar
                        .make(null,"Something went wrong",Snackbar.LENGTH_LONG)
                        .setAction("RETRY", v -> {
                            Toast.makeText(getContext(), "retrying....", Toast.LENGTH_SHORT).show();

                            getImages(pageCount);
                        });
                snackbar.show();
                pb.setVisibility(View.GONE);
            }
        });
    }
    private void getImagesByQuery(String query) {
        Methods methods = RetrofitClient.getRetrofitInstance().create(Methods.class);
        Call<ImageList> call = methods.getAllDataByQuery(query);

        call.enqueue(new Callback<ImageList>() {
            @Override
            public void onResponse(Call<ImageList> call, Response<ImageList> response) {
                if (response.isSuccessful() && response.body() != null){
                    recyclerView.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);

                    list =  response.body();
                    layoutManager = new LinearLayoutManager(getContext());
                    imageAdapter = new ImageAdapter(getContext(),list);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(imageAdapter);
                    imageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ImageList> call, Throwable t) {
                Snackbar snackbar = Snackbar
                        .make(null,"Something went wrong",Snackbar.LENGTH_LONG)
                        .setAction("RETRY", v -> {
                            Toast.makeText(getContext(), "retrying....", Toast.LENGTH_SHORT).show();
                            getImagesByQuery(query);
                        });
                snackbar.show();
            }
        });
    }
}