package com.bobhenz.simplediscgolf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class DiscGolfEditHoleActivity extends Activity {
    private DiscGolfHoleInfo mHoleInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disc_golf_edit_hole);
        Intent intent = getIntent();
        if (intent != null) {
            mHoleInfo = intent.getParcelableExtra("HOLE_INFO");
        } else {
            mHoleInfo = new DiscGolfHoleInfo("1", 3);
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
    }


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
}
