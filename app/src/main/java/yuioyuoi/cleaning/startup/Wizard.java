package yuioyuoi.cleaning.startup;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

import yuioyuoi.cleaning.data.RoomDbHelper;
import yuioyuoi.cleaning.model.Actions;
import yuioyuoi.cleaning.notification.NotificationScheduler;

/**
 * Created by Jean on 12/03/2017.
 */

public class Wizard
{
    public void create( Context context )
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( calendar.getTimeInMillis() + 5000 );

        Date reminder = calendar.getTime();
        String reminderString = RoomDbHelper.ISO_8601_FORMAT.format( reminder );

        RoomDbHelper roomDbHelper = new RoomDbHelper( context );

        roomDbHelper.newRoom( "Flat", null, null, Actions.HOOVER, reminderString, "2-WEEKS" );
        roomDbHelper.newRoom( "Flat", null, null, Actions.SWEEP, reminderString, "2-WEEKS" );
        roomDbHelper.newRoom( "Flat", null, null, Actions.MOP, reminderString, "1-MONTH" );
        roomDbHelper.newRoom( "Flat", "Surfaces", null, Actions.DUST, reminderString, "2-WEEKS" );
        roomDbHelper.newRoom( "Flat", "Windows", null, Actions.WIPE, reminderString, "6-MONTHS" );
        roomDbHelper.newRoom( "Bathroom", "Toilet", null, Actions.LIMESCALE, reminderString, "2-WEEKS" );
        roomDbHelper.newRoom( "Bathroom", "Toilet", null, Actions.WIPE, reminderString, "1-MONTH" );
        roomDbHelper.newRoom( "Bathroom", "Surfaces", null, Actions.DUST, reminderString, "2-WEEKS" );
        roomDbHelper.newRoom( "Bathroom", "Mirrors", null, Actions.WIPE, reminderString, "1-MONTH" );
        roomDbHelper.newRoom( "Bathroom", "Sink", null, Actions.LIMESCALE, reminderString, "1-MONTH" );
        roomDbHelper.newRoom( "Bathroom", "Shower", null, Actions.BLEACH, reminderString, "1-MONTH" );
        roomDbHelper.newRoom( "Bathroom", "Shower", "Glass", Actions.WIPE, reminderString, "3-MONTH" );
        roomDbHelper.newRoom( "Bathroom", "Shower", "Shower Head", Actions.LIMESCALE, reminderString, "1-MONTH" );
        roomDbHelper.newRoom( "Kitchen", "Oven", null, Actions.WIPE, reminderString, "3-MONTHS" );
        roomDbHelper.newRoom( "Kitchen", "Toaster", null, Actions.WIPE, reminderString, "1-MONTH" );
        roomDbHelper.newRoom( "Kitchen", "Kettle", null, Actions.WIPE, reminderString, "1-MONTH" );
        roomDbHelper.newRoom( "Kitchen", "Drying Rack", null, Actions.LIMESCALE, reminderString, "3-WEEKS" );
        roomDbHelper.newRoom( "Kitchen", "Hood", null, Actions.WIPE, reminderString, "6-MONTHS" );
        roomDbHelper.newRoom( "Kitchen", "Rice Cooker", null, Actions.WIPE, reminderString, "1-MONTH" );
        roomDbHelper.newRoom( "Kitchen", "Surfaces", null, Actions.WIPE, reminderString, "1-MONTH" );
        roomDbHelper.newRoom( "Kitchen", "Mirrors", null, Actions.WIPE, reminderString, "1-MONTH" );

        NotificationScheduler.getInstance().scheduleNotification( context, "Flat", reminder );
        NotificationScheduler.getInstance().scheduleNotification( context, "Bathroom", reminder );
        NotificationScheduler.getInstance().scheduleNotification( context, "Kitchen", reminder );
    }
}
