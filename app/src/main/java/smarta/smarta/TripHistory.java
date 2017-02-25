package smarta.smarta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TripHistory extends AppCompatActivity {

    RecyclerView mRecycler;
    private RVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.d("ferk", String.valueOf(tripManager.getTripListHistory().size()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);

        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        List<Trip> trips = TripManager.getInstance().getTripListHistory();

        adapter = new RVAdapter(TripHistory.this,trips);
        mRecycler.setAdapter(adapter);
    }
}
