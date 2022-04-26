package com.example.imagegallery;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Methods {
    @GET("?method=flickr.photos.getRecent&per_page=15&api_key=6f102c62f41998d151e5a1b48713cf13&safe_search=1&format=json&nojsoncallback=1&extras=url_s")
    Call<ImageList> getAllData(@Query("page")Integer pageNumber);

    @GET("?method=flickr.photos.search&per_page=15&api_key=6f102c62f41998d151e5a1b48713cf13&safe_search=1&format=json&nojsoncallback=1&extras=url_s")
    Call<ImageList> getAllDataByQuery(@Query("text") String query);
}
