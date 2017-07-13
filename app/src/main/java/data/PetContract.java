package data;

import android.provider.BaseColumns;

/**
 * Created by JR on 7/13/2017.
 */

// make it final so it can't be extended
public final class PetContract {


    private PetContract(){}


    /* Inner class that defines the table contents of the pets table */
    public static final class PetEntry implements BaseColumns {

        // table name
        public static final String TABLE_NAME = "pets";


        /**
         * Columns in the table
         */
        // id
        public static final String _ID = BaseColumns._ID;

        // name
        public static final String COLUMN_PET_NAME = "name";

        // breed
        public static final String COLUMN_PET_BREED = "breed";

        // gender
        public static final String COLUMN_PET_GENDER = "gender";

        // weight
        public static final String COLUMN_PET_WEIGHT = "weight";

        /**
         * Constant integer values for genders
         */
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

    }

}
