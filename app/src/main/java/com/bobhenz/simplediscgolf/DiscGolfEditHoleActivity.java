package com.bobhenz.simplediscgolf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class DiscGolfEditHoleActivity extends Activity
    implements
        DialogHoleNumber.Listener {
    //private DiscGolfCourseInfo mCourseInfo;
    private DiscGolfHoleInfo mHoleInfo;
    private DiscGolfDatabase mDgDatabase;
    //private ArrayList<DiscGolfCourseInfo> mTouchedCourseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disc_golf_edit_hole);
        mDgDatabase = new DiscGolfDatabase(getApplicationContext());
        Intent intent = getIntent();
        mHoleInfo = intent.getParcelableExtra("hole-info");
        //mCourseInfo = mDgDatabase.readCourseFromHoleInfoDbId(holeInfoDbId);
        //mTouchedCourseList.add(mCourseInfo);
        //mHoleInfo = mCourseInfo.findHoleByDbId(holeInfoDbId);
        updateGui();
    }

    private void updateGui() {
        //TextView courseNameView = (TextView)findViewById(R.id.text_course_name);
        //courseNameView.setText(mCourseInfo.getName());
        TextView holeNumberView = (TextView)findViewById(R.id.text_hole_number);
        holeNumberView.setText(mHoleInfo.getName());
        TextView parView = (TextView)findViewById(R.id.text_par);
        parView.setText(String.valueOf(mHoleInfo.getPar()));
    }

    public void onClickHoleNumber(View view) {
        new DialogHoleNumber().show(getFragmentManager(), "hole-number");
    }
    public void dialogHoleNumberListener(String value) {
        mHoleInfo.setName(value);
        updateGui();
    }

    public void onClickPar(View view) {
        //new DialogPar().show(getFragmentManager(), "course-name");
    }
    public void dialogParListener(int value) {
        mHoleInfo.setPar(value);
        updateGui();
    }

/*
        // Setup the name picker.
        NumberPicker namePicker = (NumberPicker)findViewById(R.id.id_picker_name);
        int maxHoleNumberNames = 4*9;
        int maxHoleCharacterNames = 18;
        List<String> values = new ArrayList<String>();
        for (int index = 0; index < maxHoleNumberNames; index++) {
            values.add(String.valueOf(index+1));
        }
        for (int count = 0; count < maxHoleCharacterNames; count++) {
            values.add(String.valueOf((char)('A' + maxHoleCharacterNames - count - 1)));
        }
        namePicker.setMinValue(0);
        namePicker.setMaxValue(values.size() - 1);

        // This is some wacked-out conversion from an array list to an array.
        String[] stringArray = new String[values.size()];
        stringArray = values.toArray(stringArray);
        namePicker.setDisplayedValues(stringArray);

        namePicker.setWrapSelectorWheel(true);
        namePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        namePicker.setValue(values.indexOf(mHoleInfo.getName()));
        SetNumberPickerTextColor(namePicker, Color.parseColor("#FFFFFF"));

        // Setup the par picker.
        NumberPicker parPicker = (NumberPicker)findViewById(R.id.id_picker_par);
        parPicker.setMinValue(1);
        parPicker.setMaxValue(7);
        parPicker.setWrapSelectorWheel(false);
        parPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        parPicker.setValue(mHoleInfo.getPar());
        SetNumberPickerTextColor(parPicker, Color.parseColor("#FFFFFF"));
*/


    private static boolean SetNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        //I got this code from stack overflow.
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    Log.w("setNumPickTextColor", e);
                }
                catch(IllegalAccessException e){
                    Log.w("setNumPickTextColor", e);
                }
                catch(IllegalArgumentException e){
                    Log.w("setNumPickTextColor", e);
                }
            }
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_disc_golf_edit_hole, menu);
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

    public void onClickSave(View view) {
        // Pass back the (possibly modified) hole information.
        Intent intent = getIntent();
        intent.putExtra("hole-info", mHoleInfo);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onClickCancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
