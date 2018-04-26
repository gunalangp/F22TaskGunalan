package gunalan.taskguna.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import gunalan.taskguna.MainApplication;
import gunalan.taskguna.R;
import gunalan.taskguna.data.model.FavoriteModel;
import gunalan.taskguna.data.model.MovieModel;
import gunalan.taskguna.service.Api;
import gunalan.taskguna.ui.detailpage.DetailActivity;
import gunalan.taskguna.util.AppConstants;
import gunalan.taskguna.util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ImageListener{

    @BindView(R.id.frame)
    ConstraintLayout frame;
    @BindView(R.id.rv_list)
    RecyclerView recyclerView;
    Snackbar snackbar;
    Api restService;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedPreferences;
    private ArrayList<FavoriteModel> favoriteModelArrayList;
    private MovieModel movieModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getResources().getText(R.string.mainactivity));


        ButterKnife.bind(this);

        restService = ((MainApplication) getApplication()).getClient();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //if (new NetworkUtil().isNetworkAvailable(getApplicationContext())) {
                callApi();
                // } else {
                //       swipeRefreshLayout.setRefreshing(false);
                //         snackbar("No internet connection...");
                //       }
            }
        });
        //   if (new NetworkUtil().isNetworkAvailable(getApplicationContext())) {
        // } else {
        //    snackbar("No internet connection...");
        // }
        if (!new NetworkUtil().isNetworkAvailable(getApplicationContext())) {
            snackbar("No internet connection...");
        }
        callApi();
    }

    private void snackbar(String message) {

        snackbar = Snackbar.make(frame, message, Snackbar.LENGTH_LONG).setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        snackbar.show();

    }

    private void callApi() {
        if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(true);
        try {
            Call<MovieModel> category = restService.getPapulorMovie(AppConstants.API_KEY);
            category.enqueue(new Callback<MovieModel>() {
                @Override
                public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                    movieModel = response.body();
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                    if (movieModel != null && movieModel.getResults().size() > 0) {
                        favoriteModelArrayList = new ArrayList<FavoriteModel>();
                        sharedPreferences = getSharedPreferences("Fav_Status", MODE_PRIVATE);
                        String restoredText = sharedPreferences.getString("status", null);
                        if (restoredText != null) {
                            Gson gson = new Gson();
                            favoriteModelArrayList = gson.fromJson(
                                    restoredText,
                                    new TypeToken<ArrayList<FavoriteModel>>() {
                                    }.getType());
                            if (favoriteModelArrayList.size() != movieModel.getResults().size()) {
                                favorite();
                            }
                        } else {
                            favorite();
                        }
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemViewCacheSize(20);
                        recyclerView.setDrawingCacheEnabled(true);
                        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                        MovieAdapter adapter = new MovieAdapter(getApplicationContext(), movieModel.getResults(),
                                favoriteModelArrayList,MainActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        snackbar("No data found!");
                    }
                }

                @Override
                public void onFailure(Call<MovieModel> call, Throwable t) {
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);

                }
            });

        } catch (Exception e) {
            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
            e.printStackTrace();
        }


    }

    private void favorite(){
        for (int i = 0; i < movieModel.getResults().size(); i++) {
            FavoriteModel favoriteModel = new FavoriteModel();
            favoriteModel.setIsfavotie(false);
            favoriteModel.setId(movieModel.getResults().get(i).getId());
            favoriteModelArrayList.add(favoriteModel);
        }

        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(
                favoriteModelArrayList, new TypeToken<ArrayList<FavoriteModel>>() {
                }.getType());
        SharedPreferences.Editor editor = getSharedPreferences("Fav_Status", MODE_PRIVATE).edit();
        editor.putString("status", jsonElement.toString());
        editor.apply();
    }

    @Override
    public void image(int i, ImageView imageView) {
        Intent intent = new Intent(getBaseContext(), DetailActivity.class);
        Bundle obj = new Bundle();
        obj.putString("title", movieModel.getResults().get(i).getOriginal_title());
        obj.putString("movie_id", movieModel.getResults().get(i).getId());
        obj.putString("date", movieModel.getResults().get(i).getRelease_date());
        obj.putString("content", movieModel.getResults().get(i).getOverview());
        obj.putString("rating", movieModel.getResults().get(i).getVote_average());
        obj.putString("image", movieModel.getResults().get(i).getPoster_path());
        intent.putExtras(obj);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                MainActivity.this,
                imageView,
                ViewCompat.getTransitionName(imageView));
        startActivity(intent, options.toBundle());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
