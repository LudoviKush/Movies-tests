package com.example.moviestest.data;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.moviestest.R;
import com.example.moviestest.services.MainResponse;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<MainResponse.Movie> mFilm;
    private ArrayList<MainResponse.Movie> searchFilmList;
    private boolean isSearching;



    private OnFilmClicked mOnFilmClicked;
    private String TAG = "ASDA";

    public void setSearching(boolean searching) {
        isSearching = searching;
        if(!isSearching){
            searchFilmList.clear();
        }
    }

    public Adapter(Context context, ArrayList<MainResponse.Movie> mFilm, OnFilmClicked onFilmListener) {
        this.context = context;
        this.mFilm = mFilm;
        this.mOnFilmClicked = onFilmListener;

        this.searchFilmList = new ArrayList<>();
        isSearching = false;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cell_film, parent, false);
        return new ViewHolder(view, new OnFilmListener() {
            @Override
            public void onFilmClick( int position ) {

                if(isSearching){
                    mOnFilmClicked.onFilmId(searchFilmList.get(position).getId());
                }else {
                    mOnFilmClicked.onFilmId(mFilm.get(position).getId());
                }


            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MainResponse.Movie currentMovie;
        if(isSearching){
            currentMovie = searchFilmList.get(position);
        }
        else {
            currentMovie = mFilm.get(position);
        }
        String image = "https://image.tmdb.org/t/p/w500/" + currentMovie.getPoster_path();

        holder.titleFilm.setText(currentMovie.getTitle());
        Glide.with(context)
                .load(image)
                .centerCrop()
                .into(holder.poster);
    }


    @Override
    public int getItemCount() {
        if(isSearching){
            return searchFilmList.size();
        }
        else{
            return mFilm.size();
        }
    }

    @Override
    public Filter getFilter() {


        return filter;

    }
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering( CharSequence constraint ) {
            ArrayList<MainResponse.Movie> filteredList = new ArrayList<>();


            if(constraint == null || constraint.length()==0){

                filteredList.addAll(mFilm);

            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (MainResponse.Movie movie : mFilm){
                    if(movie.getTitle().toLowerCase().contains(filterPattern)){
                        filteredList.add(movie);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults( CharSequence constraint, FilterResults results ) {

            searchFilmList.clear();
            searchFilmList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        ImageView poster;
        TextView titleFilm;
        OnFilmListener onFilmListener;

        public ViewHolder(@NonNull View itemView, OnFilmListener onFilmListener) {
            super(itemView);
            poster = itemView.findViewById(R.id.poster);
            titleFilm = itemView.findViewById(R.id.filmTitle);


          this.onFilmListener = onFilmListener;
          itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onFilmListener.onFilmClick(getAdapterPosition());
            Log.d(TAG, "onClicked: ");
            int pos = getAdapterPosition();
            Intent intent = new Intent(context, DetailActivity.class);
            MainResponse.Movie currentMovie;
            if(isSearching){
                currentMovie = searchFilmList.get(pos);
            }
            else {
                currentMovie = mFilm.get(pos);
            }
            intent.putExtra("original_title", currentMovie.getOriginal_title());
            intent.putExtra("backdrop_path", currentMovie.getBackdrop_path());
            intent.putExtra("overview", currentMovie.getOverview());
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    public interface OnFilmListener{
        void onFilmClick(int position);
    }
    public interface OnFilmClicked{
        void onFilmId(long id);
    }


}