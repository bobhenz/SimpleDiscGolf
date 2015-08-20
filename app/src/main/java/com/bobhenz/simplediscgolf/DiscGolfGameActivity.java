package com.bobhenz.simplediscgolf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;


public class DiscGolfGameActivity extends Activity {
    private DiscGolfLocation mDgLocation;
    private DiscGolfDatabase mDgDatabase;
    private DiscGolfGameData mGame;
    private DiscGolfLocationButton mTeeButton;
    private DiscGolfLocationButton mBasketButton;
    private ViewGroup mThrowGroup;
    private StrokeListManager mStrokeListManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDgLocation = new DiscGolfLocation(DiscGolfGameActivity.this);
        mDgDatabase = new DiscGolfDatabase(getApplicationContext());
        setContentView(R.layout.activity_disc_golf_game);
        //mTeeButton = new DiscGolfLocationButton((Button)findViewById(R.id.button_tee), "tee", "Tee", mDgLocation);
        //mBasketButton = new DiscGolfLocationButton((Button)findViewById(R.id.button_basket), "basket", "Basket", mDgLocation);
        mThrowGroup = (ViewGroup) findViewById(R.id.throw_group);
        mStrokeListManager = new StrokeListManager(mThrowGroup);
        Intent intent = getIntent();
        boolean bNewGame = intent.getBooleanExtra(MainActivity.EXTRA_GAME_NEW, true);
        if (bNewGame) {
            Location location = mDgLocation.getCurrentLocation();
            mGame = new DiscGolfGameData(mDgDatabase, location);
            DiscGolfHoleInfo holeInfo = mGame.getCourse().guessHole(location);
            mGame.addHole(holeInfo);
            for (int stroke = 0; stroke < holeInfo.getPar(); stroke++) {
                mGame.getHole().addStroke(null);
            }
            updateGui();
        } else {
            /*
            Location location = mDgLocation.getCurrentLocation();
            String gameId = intent.getStringExtra(MainActivity.EXTRA_GAME_ID, "");
            DiscGolfGame game = mDgDatabase.getGame(gameId);
            DiscGolfCourseInfo course = mDgDatabase.getLocalCourse(game);
            DiscGolfHoleInfo whichhole = mDgDatabase.getLocalGuessHole(location, course);
            DiscGolfHoleInfo hole = game.getHole(whichhole.getName());
            if (hole == null) {
                // If the hole wasn't created yet for this game, then
                // just use the "guess" to initialize the hole.
                hole = whichhole;
            }
            */
        }
    }

    private class StrokeListManager implements View.OnClickListener {
        private ViewGroup mParentView;
        private List<Button> mMarkButtonArray = new ArrayList<Button>();
        private List<Button> mRemoveButtonArray = new ArrayList<Button>();
        private List<View> mLayoutArray = new ArrayList<View>();

        StrokeListManager(ViewGroup parentView) {
            mParentView = parentView;
        }

        public void prepareObjects(DiscGolfHoleData hole) {
            int guiRowCount = mLayoutArray.size();
            // Increase the number of rows if we don't have enough.
            while (guiRowCount < hole.getStrokeCount()) {
                Context context = mParentView.getContext();
                View layout = LayoutInflater.from(context).inflate(R.layout.throwgroup, mParentView, false);
                Button markButton = (Button)layout.findViewById(R.id.id_button_throw);
                markButton.setOnClickListener(this);
                mMarkButtonArray.add(markButton);
                Button removeButton = (Button)layout.findViewById(R.id.id_button_rmthrow);
                removeButton.setOnClickListener(this);
                mRemoveButtonArray.add(removeButton);
                mLayoutArray.add(layout);
                mParentView.addView(layout);
                guiRowCount++;
            } // while

            // Reduce the number of rows if we have too many.
            while ((guiRowCount > hole.getStrokeCount()) && (guiRowCount > 0)) {
                mParentView.removeView(mLayoutArray.get(guiRowCount - 1));
                mLayoutArray.remove(guiRowCount - 1);
                mMarkButtonArray.remove(guiRowCount - 1);
                mRemoveButtonArray.remove(guiRowCount - 1);
                guiRowCount--;
            } // while
        }

        private void updateStrokeText(int stroke, Button button, Location startLocation, Location markedLocation) {
            String buttonText;
            if ((startLocation != null) && (markedLocation != null)) {
                float distance = startLocation.distanceTo(markedLocation);
                buttonText = String.format("%-3d  |  %.2f ft", stroke, (distance * 100.0) / 2.54 / 12.0);
            } else {
                buttonText = String.format("%-3d  |  Unknown", stroke);
            }
            button.setText(buttonText);
        }

        public void update(DiscGolfHoleData hole) {
            Location startLocation = hole.getStartLocation();
            for (int stroke = 1; stroke <= mLayoutArray.size(); stroke++) {
                Location location = hole.getStrokeLocation(stroke);
                updateStrokeText(stroke, mMarkButtonArray.get(stroke-1), startLocation, location);
                startLocation = location;
            } // for row
        }

        public void onClick(View button) {
            if (mMarkButtonArray.contains(button)) {
                Log.d("onClick mark", "HERE");
                int stroke = mMarkButtonArray.indexOf(button) + 1;
                Location location = mDgLocation.getCurrentLocation();
                mGame.getHole().setStrokeLocation(stroke, location);
                // updateGui() will update all the buttons
                updateGui();
            } else if (mRemoveButtonArray.contains(button)) {
                Log.d("onClick remove", "HERE");
                int stroke = mRemoveButtonArray.indexOf(button) + 1;
                mGame.getHole().removeStroke(stroke);
                // updateGui() will remove the objects we no longer need.
                updateGui();
            }
        }
    }

    private void updateGui() {
        DiscGolfHoleData hole = mGame.getHole();
        mStrokeListManager.prepareObjects(hole);
        mStrokeListManager.update(hole);
    }

    public void onButtonAddThrow (View view) {
        mGame.getHole().addStroke(null);
        updateGui();
    }

    public void onButtonEditHoleInfo(View v) {
        // Open a dialog giving the user the option of which hole info they
        // want to use.
        // TODO: do it here.
        /*
        boolean bUserCancelled = false;
        boolean bUserWantsToEdit = true;
        if (!bUserCancelled) {
            DiscGolfHoleInfo holeInfo = mGame.getCourse().getHole(selectedIndex);
            mGame.getHole().setInfo(holeInfo);
        }
        if (bUserWantsToEdit) {
            Intent intent = new Intent(this, DiscGolfEditHoleActivity.class);
            intent.putExtra("HOLE_INFO", (Parcelable) mGame.getHole().getInfo());
            startActivityForResult(intent, 1);
            DiscGolfHoleInfo holeInfo = intent.getParcelableExtra("HOLE_INFO");
            boolean bUserWantsToSave = true;
            if (bUserWantsToSave) {
                mGame.getHole().setInfo(holeInfo);
            }
        }
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                DiscGolfHoleInfo holeInfo = data.getParcelableExtra("HOLE_INFO");
                if (holeInfo != null) {
                    //mDgDatabase.write(newInfo);
                    mGame.getHole().setInfo(holeInfo);
                    updateGui();
                }
            }
        }
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
