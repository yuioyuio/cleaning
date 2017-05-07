package yuioyuoi.cleaning.model;

import android.provider.BaseColumns;

/**
 * Created by Jean on 28/11/2016.
 */

public final class RoomContract
{
    private RoomContract() {}

    public static class RoomEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "rooms";
        public static final String COLUMN_NAME_ROOM = "room";
        public static final String COLUMN_NAME_SUBTYPE_1 = "subtype1";
        public static final String COLUMN_NAME_SUBTYPE_2 = "subtype2";
        public static final String COLUMN_NAME_ACTION = "action";
        public static final String COLUMN_NAME_REMINDER = "reminder";
        public static final String COLUMN_NAME_RECURRENCE = "recurrence";
    }
}
