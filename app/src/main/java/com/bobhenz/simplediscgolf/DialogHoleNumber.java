package com.bobhenz.simplediscgolf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

/**
 * Created by bhenz on 8/27/2015.
 */
public class DialogHoleNumber extends DialogFragment {
    public interface Listener {
        void dialogHoleNumberListener(String value);
    }

    private NumberPicker mPrefixPicker;
    private NumberPicker mPostfixPicker;
    private String[] mPrefixValues;
    private String[] mPostfixValues = {
            " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    public DialogHoleNumber() {
        mPrefixValues = new String[100];
        mPrefixValues[0] = " ";
        for (int index = 1; index < mPrefixValues.length; index++) {
            mPrefixValues[index] = String.valueOf(index);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Setup the name picker.
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        mPrefixPicker = new NumberPicker(getActivity());
        mPrefixPicker.setMinValue(0);
        mPrefixPicker.setMaxValue(mPrefixValues.length - 1);
        mPrefixPicker.setDisplayedValues(mPrefixValues);
        mPrefixPicker.setWrapSelectorWheel(true);
        mPrefixPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mPrefixPicker.setValue(1);
        layout.addView(mPrefixPicker);
        
        mPostfixPicker = new NumberPicker(getActivity());
        mPostfixPicker.setMinValue(0);
        mPostfixPicker.setMaxValue(mPostfixValues.length - 1);
        mPostfixPicker.setDisplayedValues(mPostfixValues);
        mPostfixPicker.setWrapSelectorWheel(true);
        mPostfixPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mPostfixPicker.setValue(0);
        layout.addView(mPostfixPicker);
/*
        for (int count = 0; count < maxHoleCharacterNames; count++) {
            values.add(String.valueOf((char)('A' + maxHoleCharacterNames - count - 1)));
        }
*/
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);
        builder.setTitle("HoleNumber");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Listener listener = (Listener)getActivity();
                String value = mPrefixValues[mPrefixPicker.getValue()] + mPostfixValues[mPostfixPicker.getValue()];
                listener.dialogHoleNumberListener(value);
            }
        });
        builder.setNegativeButton("Cancel", null);

        Dialog dialog = builder.create();
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putInt("value-prefix", mPrefixPicker.getValue());
        state.putInt("value-postfix", mPostfixPicker.getValue());
    }
}
