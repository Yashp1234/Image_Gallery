package com.example.imagegallery;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ImageList list;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private ProgressBar pb;
    private int pageCount = 1;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout layout;
    private NavigationView navigationView;
    private NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.idRV);
        pb = findViewById(R.id.idPB);
        layout = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.idNavView);
        nestedScrollView = findViewById(R.id.idNestedScrollView);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setUpPagination(true);

        //function for loading images from internet using glide and retrofit
        getImages(pageCount);

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpPagination(boolean isPaginationAllowed) {
        if (isPaginationAllowed){
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                    (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY == v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                    pb.setVisibility(View.VISIBLE);
                    getImages(++pageCount);

//                    Toast.makeText(this, "page number: "+pageCount, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                    (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            });
        }

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);

        MenuItem searchItem = menu.findItem(R.id.idSearch);
        MenuItem refreshItem = menu.findItem(R.id.idRefresh);
        MenuItem homeItem = navigationView.getMenu().findItem(R.id.idHome);

        homeItem.setOnMenuItemClickListener(item -> {
            getImages(pageCount);
            return true;
        });

        refreshItem.setOnMenuItemClickListener(item -> {
            getImages(pageCount);
            return true;
        });

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Type here to search..");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                getImagesByQuery(searchView.getQuery().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
                    layoutManager = new LinearLayoutManager(MainActivity.this);
                    imageAdapter = new ImageAdapter(MainActivity.this,list);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(imageAdapter);
                    imageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ImageList> call, Throwable t) {
                Snackbar snackbar = Snackbar
                        .make(layout,"Something went wrong",Snackbar.LENGTH_LONG)
                        .setAction("RETRY", v -> {
                            Toast.makeText(MainActivity.this, "retrying....", Toast.LENGTH_SHORT).show();

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
                    layoutManager = new LinearLayoutManager(MainActivity.this);
                    imageAdapter = new ImageAdapter(MainActivity.this,list);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(imageAdapter);
                    imageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ImageList> call, Throwable t) {
                Snackbar snackbar = Snackbar
                        .make(layout,"Something went wrong",Snackbar.LENGTH_LONG)
                        .setAction("RETRY", v -> {
                            Toast.makeText(MainActivity.this, "retrying....", Toast.LENGTH_SHORT).show();
                            getImagesByQuery(query);
                        });
                snackbar.show();
            }
        });
    }
}

