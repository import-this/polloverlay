package com.example.pollfishclient.view;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.collection.ArrayMap;

import com.example.pollfishclient.R;
import com.google.android.material.snackbar.Snackbar;
import com.pollfish.poll.PollListener;
import com.pollfish.poll.PollLoadListener;
import com.pollfish.poll.PollOverlay;

import java.util.Collections;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final Map<String, String> params;
    static {
        final Map<String, String> map = new ArrayMap<>(5);
        map.put(PollOverlay.PARAM1, "mypar1");
        map.put(PollOverlay.PARAM2, "mypar2");
        map.put(PollOverlay.PARAM3, "mypar3");
        map.put(PollOverlay.PARAM4, "mypar4");
        map.put(PollOverlay.PARAM5, "mypar5");
        params = Collections.unmodifiableMap(map);
    }

    // Button callback
    public void callLibrary(View view) {
        // Create a poll overlay.
        final PollOverlay pollOverlay = new PollOverlay(this);

        // Show Pollfish and listen for events.
        pollOverlay.show(this, new PollLoadListener() {
            @Override
            public void onPollLoaded() {
                Log.d(TAG, "Poll loaded");
            }
            @Override
            public void onPollFailedToLoad(int errorCode) {
                Log.d(TAG, "Poll failed to load");
                //ERROR_CODE_NETWORK_ERROR;
                switch (errorCode) {
                    default:
                        break;
                }
            }
        }, new PollListener() {
            @Override
            public void onPollOpened() {
                Log.d(TAG, "Poll opened");
            }

            @Override
            public void onPollClosed(@NonNull Map<String, String> returnVals) {
                final String param3 = returnVals.get(PollOverlay.PARAM3);
                final String param4 = returnVals.get(PollOverlay.PARAM4);
                final String param5 = returnVals.get(PollOverlay.PARAM5);

                Log.d(TAG, "Poll closed");
                Log.d(TAG, "Param 3: " + param3);
                Log.d(TAG, "Param 4: " + param4);
                Log.d(TAG, "Param 5: " + param5);
                Snackbar.make(
                        findViewById(R.id.mainCoordinatorLayout),
                        String.format("Param3: %s, Param4: %s, Param5: %s", param3, param4, param5),
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        }, params);
    }
}
