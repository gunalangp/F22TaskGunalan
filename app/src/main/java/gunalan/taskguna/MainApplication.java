package gunalan.taskguna;

import android.support.multidex.MultiDexApplication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import gunalan.taskguna.service.Api;
import gunalan.taskguna.util.AppConstants;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainApplication extends MultiDexApplication {

    Retrofit restClient;
    int cacheSize = 10 * 1024 * 1024; // 10 MB

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Gson gson = new GsonBuilder().setLenient().create();

            Cache cache = new Cache(getCacheDir(), cacheSize);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .build();

            restClient = new Retrofit.Builder()
                    .baseUrl(AppConstants.BASE_URl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Api getClient() {
        return restClient.create(Api.class);
    }
}
