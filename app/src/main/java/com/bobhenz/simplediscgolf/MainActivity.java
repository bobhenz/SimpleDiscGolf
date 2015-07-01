package com.bobhenz.simplediscgolf;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;


public class MainActivity extends Activity {

    private DiscGolfLocation mDgLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDgLocation = new DiscGolfLocation(MainActivity.this);
        final DiscGolfLocationButton button_tee = new DiscGolfLocationButton((Button)findViewById(R.id.id_button_tee), mDgLocation);
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
}
