package gunalan.taskguna.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import gunalan.taskguna.R;
import gunalan.taskguna.data.model.FavoriteModel;
import gunalan.taskguna.data.model.MovieDetailModel;
import gunalan.taskguna.util.AppConstants;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    private ArrayList<MovieDetailModel> moviesList;
    private ArrayList<FavoriteModel> favoriteModelArrayList;
    private Context context;
    private ImageListener imageListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.iv_image);

        }
    }


    public MovieAdapter(Context context, ArrayList<MovieDetailModel> moviesList,
                        ArrayList<FavoriteModel> favoriteModelArrayList, ImageListener imageListener) {
        this.moviesList = moviesList;
        this.context = context;
        this.favoriteModelArrayList = favoriteModelArrayList;
        this.imageListener = imageListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        MovieDetailModel movie = moviesList.get(position);
        if (movie.getPoster_path() != null) {
            Glide.with(context).load(AppConstants.IMAGE_BASE_URL + movie.getPoster_path())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Glide.with(context).load(R.mipmap.ic_launcher).into(holder.imageView);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);
        } else {
            Glide.with(context).load(R.mipmap.ic_launcher).into(holder.imageView);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageListener.image(holder.getAdapterPosition(), holder.imageView);
            }
        });

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
