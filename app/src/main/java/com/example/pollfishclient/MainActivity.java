package com.example.pollfishclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.pollfish.poll.PollListener;
import com.pollfish.poll.PollOverlay;

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

    // Button callback
    public void callLibrary(View view) {
        // Create a poll overlay.
        final PollOverlay pollOverlay = new PollOverlay(this);

        // Show Pollfish and listen for events.
        pollOverlay.show(new PollListener() {
            @Override
            public void onPollFailedToLoad(int errorCode) {
                //ERROR_CODE_NETWORK_ERROR;
                switch (errorCode) {
                    default:
                        break;
                }
            }
            @Override
            public void onPollOpened() {
                Log.d(TAG, "Poll Opened");
            }
            @Override
            public void onPollClosed(String param3, String param4, String param5) {
                Log.d(TAG, "Poll Closed");
                Log.d(TAG, "Param 3: " + param3);
                Log.d(TAG, "Param 4: " + param4);
                Log.d(TAG, "Param 5: " + param5);
                Snackbar.make(
                        findViewById(R.id.mainCoordinatorLayout),
                        String.format("Param3: %s, Param4: %s, Param5: %s", param3, param4, param5),
                        Snackbar.LENGTH_LONG)
                    .show();
            }
        }, "mypar1", "mypar2", "mypar3", "mypar4", "mypar5");
    }
}
