package com.bobhenz.simplediscgolf;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

/**
 * Created by bhenz on 8/27/2015.
 */
public class DialogCourseName extends DialogFragment {
    public interface Listener {
        void dialogCourseNameListener(String value);
    }

    private EditText mEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mEditText = new EditText(getActivity());
        if (savedInstanceState != null) {
            String savedValue = savedInstanceState.getString("value");
            mEditText.setText(savedValue);
        } else {
            Bundle args = getArguments();
            String defaultValue = args.getString("default");
            mEditText.setText(defaultValue);
        }
        mEditText.requestFocus();
        mEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mEditText);
        builder.setTitle("Course Name");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Listener listener = (Listener)getActivity();
                listener.dialogCourseNameListener(mEditText.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", null);

        Dialog dialog = builder.create();
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putString("value", mEditText.getText().toString());
    }
}
