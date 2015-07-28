package com.bobhenz.simplediscgolf;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class DiscGolfHoleActivity extends Activity {
    private DiscGolfLocation mDgLocation;
    private DiscGolfHole mHole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDgLocation = new DiscGolfLocation(DiscGolfHoleActivity.this);
        Intent intent = getIntent();
        boolean bNewGame = intent.getBooleanExtra(MainActivity.EXTRA_GAME_NEW, true);
        setContentView(R.layout.activity_disc_golf_hole);
        if (bNewGame) {
            mHole = new DiscGolfHole("1", 3, DiscGolfHole.TeeCategory.ADVANCED, mDgLocation, (ViewGroup)findViewById(android.R.id.content));
        }
    }

    public void onButtonAddThrow (View view) {
        mHole.addThrow();
    }

    @Override
    protected void onResume() {
        Log.d("main", "onResume");
        super.onResume();
        mDgLocation.start();
    }

    @Override
    protected void onPause() {
        Log.d("main", "onPause");
        mDgLocation.stop();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_disc_golf_hole, menu);
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
}
