package gunalan.taskguna.ui.detailpage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import gunalan.taskguna.MainApplication;
import gunalan.taskguna.R;
import gunalan.taskguna.data.model.FavoriteModel;
import gunalan.taskguna.data.model.MovieModel;
import gunalan.taskguna.data.model.TrailerModel;
import gunalan.taskguna.service.Api;
import gunalan.taskguna.ui.main.MainActivity;
import gunalan.taskguna.ui.main.MovieAdapter;
import gunalan.taskguna.util.AppConstants;
import gunalan.taskguna.util.NetworkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class DetailActivity extends AppCompatActivity {

    private String title, movieId, date, content, rating, imagepath;
    Api restService;

    @BindView(R.id.tv_title)
    TextView titleText;
    @BindView(R.id.tv_date)
    TextView dateText;
    @BindView(R.id.tv_rating)
    TextView ratingText;
    @BindView(R.id.tv_favorite)
    TextView favoriteText;
    @BindView(R.id.tv_content)
    TextView contentText;
    @BindView(R.id.iv_image)
    ImageView imageView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.ll_trailers)
    LinearLayout trailerGroup;
    Snackbar snackbar;
    private String url;
    private TrailerModel trailerModel;
    SharedPreferences sharedPreferences;
    private ArrayList<FavoriteModel> favoriteModelArrayList = new ArrayList<FavoriteModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        restService = ((MainApplication) getApplication()).getClient();

        sharedPreferences = getSharedPreferences("Fav_Status", MODE_PRIVATE);
        String restoredText = sharedPreferences.getString("status", null);
        if (restoredText != null) {
            Gson gson = new Gson();
            favoriteModelArrayList = gson.fromJson(
                    restoredText,
                    new TypeToken<ArrayList<FavoriteModel>>() {
                    }.getType());
        }

        Intent obj = getIntent();
        if (obj != null) {
            title = obj.getStringExtra("title");
            movieId = obj.getStringExtra("movie_id");
            date = obj.getStringExtra("date");
            content = obj.getStringExtra("content");
            rating = obj.getStringExtra("rating");
            imagepath = obj.getStringExtra("image");
        }

        if (movieId != null && favoriteModelArrayList != null && favoriteModelArrayList.size() > 0) {
            for (int i = 0; i < favoriteModelArrayList.size(); i++) {
                if (movieId.equals(favoriteModelArrayList.get(i).getId())) {
                    if (favoriteModelArrayList.get(i).isIsfavotie()) {
                        favoriteText.setText("Favorite");
                    } else {
                        favoriteText.setText("Mark As Favorite");
                    }
                    break;
                } else {
                    favoriteText.setText("Mark As Favorite");
                }
            }
        }

        favoriteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFavotire();
            }
        });

        if (title != null)
            titleText.setText(title);
        if (date != null)
            dateText.setText(date);
        if (content != null)
            contentText.setText(content);
        if (rating != null)
            ratingText.setText(rating + "/" + "10");
        if (imagepath != null)
            Glide.with(this).load(AppConstants.IMAGE_BASE_URL + imagepath).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);

        callApi();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callApi();
            }
        });

        if (!new NetworkUtil().isNetworkAvailable(getApplicationContext())) {
            snackbar("No internet connection...");
        }
    }

    private void callApi() {
        if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(true);
        try {
            url = "movie/" + movieId + "/videos?";
            Call<TrailerModel> category = restService.getTrailer(url, AppConstants.API_KEY);
            category.enqueue(new Callback<TrailerModel>() {
                @Override
                public void onResponse(Call<TrailerModel> call, Response<TrailerModel> response) {
                    trailerModel = response.body();
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                    if (trailerModel != null && trailerModel.getResults().size() > 0) {
                        setTrailerGroup();
                    } else {
                        snackbar("No data found!");
                    }
                }

                @Override
                public void onFailure(Call<TrailerModel> call, Throwable t) {
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

    public void setTrailerGroup() {
        trailerGroup.removeAllViews();
        for (int i = 0; i < trailerModel.getResults().size(); i++) {
            View view = (View) LayoutInflater.from(
                    this).inflate(R.layout.trailer_item, null);
            TextView textView = (TextView) view.findViewById(R.id.tv_text);
            textView.setText(trailerModel.getResults().get(i).getName());
            view.setId(i);
            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (new NetworkUtil().isNetworkAvailable(getBaseContext())) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + trailerModel.getResults()
                                .get(trailerGroup.getChildAt(finalI).getId()).getKey()));
                        startActivity(intent);
                    } else {
                        snackbar("No internet connection...");
                    }
                }
            });

            trailerGroup.addView(view, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

    }

    private void addFavotire() {
        if (favoriteModelArrayList != null && favoriteModelArrayList.size() > 0) {
            for (int i = 0; i < favoriteModelArrayList.size(); i++) {
                if (movieId != null && movieId.equals(favoriteModelArrayList.get(i).getId())) {
                    if (!favoriteModelArrayList.get(i).isIsfavotie()) {
                        favoriteText.setText("Favorite");
                        FavoriteModel favoriteModel = new FavoriteModel();
                        favoriteModel.setIsfavotie(true);
                        favoriteModel.setId(movieId);
                        favoriteModelArrayList.remove(i);
                        favoriteModelArrayList.add(i, favoriteModel);
                    } else {
                        favoriteText.setText("Mark As Favorite");
                        FavoriteModel favoriteModel = new FavoriteModel();
                        favoriteModel.setIsfavotie(false);
                        favoriteModel.setId(movieId);
                        favoriteModelArrayList.remove(i);
                        favoriteModelArrayList.add(i, favoriteModel);
                    }
                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(
                            favoriteModelArrayList, new TypeToken<ArrayList<FavoriteModel>>() {
                            }.getType());
                    SharedPreferences.Editor editor = getSharedPreferences("Fav_Status", MODE_PRIVATE).edit();
                    editor.putString("status", jsonElement.toString());
                    editor.apply();
                    break;
                }
            }
        }
    }

    private void snackbar(String message) {
        snackbar = Snackbar.make(swipeRefreshLayout, message, Snackbar.LENGTH_LONG).setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //show message
        snackbar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
