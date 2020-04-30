package com.example.moviestest.data;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.moviestest.R;
import com.example.moviestest.data.helper.MoviesDB;
import com.example.moviestest.data.helper.Utils;
import com.example.moviestest.services.MainResponse;
import com.example.moviestest.services.WebService;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements com.example.moviestest.data.Adapter.OnFilmClicked {

    private static final String TAG ="ASDA";
    public static String BASE_URL = "https://api.themoviedb.org";
    public static int PAGE = 1;
    public static String API_KEY = "675236176baaaafd2ea29287a326d89b";
    public static String CATEGORY = "popular";
    public static String LANGUAGE = "it";

    private MoviesDB mDatabase;

    Adapter Adapter;
    RecyclerView recyclerView;
    List<MainResponse.Movie> listOfMovie;
    Button getHelpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getHelpButton = findViewById(R.id.buttonGetHelp);

        getHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://www.themoviedb.org/faq/general"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        getSupportActionBar().setTitle("Movies");

       if(Utils.isNetworkAvailable(getApplicationContext())) {
           getFeed();
       } else {
           getFeedFromDatabase();
           Toast.makeText(this, "Controlla la tua connessione e riprovi", Toast.LENGTH_LONG).show();
       }
    }

    @Override
    public void onBackPressed(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You are leaving the app, are you sure about this?");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Yes, close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i){
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getFeedFromDatabase() {
        mDatabase = new MoviesDB(this);
        List<MainResponse.Movie> movieList = mDatabase.getFilms();
        Log.d(TAG, "getting from db");

    }


    @Override
    public void onFilmId(long id) {
         //boh però non lo faceva andare sincermente meglio non farsi domande

    }


    public void getFeed(){

        recyclerView = findViewById(R.id.listFilm);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WebService service = retrofit.create(WebService.class);
        Call<MainResponse> call = service.getMovies(CATEGORY, API_KEY, LANGUAGE, PAGE);
        call.enqueue(new Callback<MainResponse>() {
            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {
                MainResponse results = response.body();
                listOfMovie = results.getResults();
                Adapter = new Adapter(getApplicationContext(), (ArrayList<MainResponse.Movie>) listOfMovie, MainActivity.this);
                recyclerView.setAdapter(Adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));

            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                Log.d(TAG, "an error occurred");
            }
        });
    }
}
