package gunalan.taskguna.service;

import gunalan.taskguna.data.model.MovieModel;
import gunalan.taskguna.data.model.TrailerModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Api {

    @GET("movie/popular")
    @Headers("Accept: application/json")
    Call<MovieModel> getPapulorMovie(@Query("api_key") String key);

    @GET
    @Headers("Accept: application/json")
    public Call<TrailerModel> getTrailer(@Url String url, @Query("api_key") String key);

}
