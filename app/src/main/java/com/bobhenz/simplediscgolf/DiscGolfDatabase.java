package com.bobhenz.simplediscgolf;

import android.location.Location;

/**
 * Created by bhenz on 7/29/2015.
 */
public class DiscGolfDatabase {
    DiscGolfDatabase() {

    }

    DiscGolfCourseInfo guessCourse(Location location) {
        /**
         * Returns a course within a reasonable distance from
         * the current location. If location is unknown, or
         */
        if (location == null) {
            // TODO: Get the last course played and return that.
            // Else, return null.
            return null;
        }

        // TODO: Cycle through all the courses in the database and
        // look for a match within a "reasonable" radius of the
        // current location. (e.g. 5 miles?) Pick the closest
        // and return it. Else, return null.
        return null;
    }

    public void refresh() {
        // TODO: Reload data from storage.
    }

    public void flush() {
        //TODO: Flush (merge?) data to storage.
    }
}
