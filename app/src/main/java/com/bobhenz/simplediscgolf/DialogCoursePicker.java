package com.bobhenz.simplediscgolf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by bhenz on 8/31/2015.
 */
public class DialogCoursePicker extends DialogFragment {
    public enum Action { SELECT, EDIT };
    public interface Listener {
        void dialogCoursePickerListener(Action action, long selectedDbId);
    }

    static private long[] makeLongArrayPrimitive(ArrayList<Long> longList, long[] longArray) {
        for (int index = 0; index < longList.size(); index++) {
            longArray[index] = longList.get(index);
        } // for
        return longArray;
    }

    public static DialogCoursePicker getInstance(DiscGolfCourseInfo defaultCourse, DiscGolfDatabase database, Location location) {
        int limit = 20;
        Bundle args = new Bundle();
        ArrayList<Long> courseDbIdList;
        if (location != null) {
            courseDbIdList = database.findCoursesNear(location, 50 * 1000, limit);
        } else {
            courseDbIdList = database.findRecentCourses(limit);
        }

        // Always make the first entry be the current course.
        long defaultDbId = defaultCourse.getDbId();
        if (courseDbIdList.contains(defaultDbId)) {
            courseDbIdList.remove(defaultDbId);
        } else if (courseDbIdList.size() >= limit) {
            courseDbIdList.remove(limit - 1);
        }
        courseDbIdList.add(0, defaultDbId);

        args.putLongArray("course-ids", makeLongArrayPrimitive(courseDbIdList, new long[courseDbIdList.size()]));
        ArrayList<String> courseNameList = database.getCourseNames(courseDbIdList);
        args.putStringArray("course-names", courseNameList.toArray(new String[courseNameList.size()]));
        DialogCoursePicker dialog = new DialogCoursePicker();
        dialog.setArguments(args);
        return dialog;
    }

    private String[] mNameArray;
    private long[] mIdArray;
    private int mCurrentSelection;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        mNameArray = args.getStringArray("course-names");
        mIdArray = args.getLongArray("course-ids");
        mCurrentSelection = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setSingleChoiceItems(mNameArray, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCurrentSelection = which;
                    }
                });
        builder.setTitle("Select a Course");
        builder.setCancelable(true);
        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long dbId= mIdArray[mCurrentSelection];
                Listener listener = (Listener) getActivity();
                listener.dialogCoursePickerListener(Action.SELECT, dbId);
            }
        });
        builder.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long dbId= mIdArray[mCurrentSelection];
                Listener listener = (Listener) getActivity();
                listener.dialogCoursePickerListener(Action.EDIT, dbId);
            }
        });
        builder.setNegativeButton("Cancel", null);

        Dialog dialog = builder.create();
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        // TODO
    }

}
