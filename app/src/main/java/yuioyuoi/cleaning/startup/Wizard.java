package yuioyuoi.cleaning.startup;

import android.content.Context;

import java.util.Calendar;

import yuioyuoi.cleaning.data.RoomDbHelper;
import yuioyuoi.cleaning.model.Actions;

/**
 * Created by Jean on 12/03/2017.
 */

public class Wizard
{
    public void create( Context context )
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( calendar.getTimeInMillis() + 5000 );

        RoomDbHelper roomDbHelper = new RoomDbHelper( context );
        roomDbHelper.newRoom( "Flat", null, null, Actions.HOOVER, calendar.getTime().toString(), "2-WEEKS" );
        roomDbHelper.newRoom( "Flat", null, null, Actions.SWEEP, calendar.getTime().toString(), "2-WEEKS" );
        roomDbHelper.newRoom( "Flat", null, null, Actions.MOP, calendar.getTime().toString(), "1-MONTH" );
        roomDbHelper.newRoom( "Flat", "Surfaces", null, Actions.DUST, calendar.getTime().toString(), "2-WEEKS" );
        roomDbHelper.newRoom( "Flat", "Windows", null, Actions.WIPE, calendar.getTime().toString(), "6-MONTHS" );
        roomDbHelper.newRoom( "Bathroom", "Toilet", null, Actions.LIMESCALE, calendar.getTime().toString(), "2-WEEKS" );
        roomDbHelper.newRoom( "Bathroom", "Toilet", null, Actions.WIPE, calendar.getTime().toString(), "1-MONTH" );
        roomDbHelper.newRoom( "Bathroom", "Surfaces", null, Actions.DUST, calendar.getTime().toString(), "2-WEEKS" );
        roomDbHelper.newRoom( "Bathroom", "Mirrors", null, Actions.WIPE, calendar.getTime().toString(), "1-MONTH" );
        roomDbHelper.newRoom( "Bathroom", "Sink", null, Actions.LIMESCALE, calendar.getTime().toString(), "1-MONTH" );
        roomDbHelper.newRoom( "Bathroom", "Shower", null, Actions.BLEACH, calendar.getTime().toString(), "1-MONTH" );
        roomDbHelper.newRoom( "Bathroom", "Shower", "Glass", Actions.WIPE, calendar.getTime().toString(), "3-MONTH" );
        roomDbHelper.newRoom( "Bathroom", "Shower", "Shower Head", Actions.LIMESCALE, calendar.getTime().toString(), "1-MONTH" );
        roomDbHelper.newRoom( "Kitchen", "Oven", null, Actions.WIPE, calendar.getTime().toString(), "3-MONTHS" );
        roomDbHelper.newRoom( "Kitchen", "Toaster", null, Actions.WIPE, calendar.getTime().toString(), "1-MONTH" );
        roomDbHelper.newRoom( "Kitchen", "Kettle", null, Actions.WIPE, calendar.getTime().toString(), "1-MONTH" );
        roomDbHelper.newRoom( "Kitchen", "Drying Rack", null, Actions.LIMESCALE, calendar.getTime().toString(), "3-WEEKS" );
        roomDbHelper.newRoom( "Kitchen", "Hood", null, Actions.WIPE, calendar.getTime().toString(), "6-MONTHS" );
        roomDbHelper.newRoom( "Kitchen", "Rice Cooker", null, Actions.WIPE, calendar.getTime().toString(), "1-MONTH" );
        roomDbHelper.newRoom( "Kitchen", "Surfaces", null, Actions.WIPE, calendar.getTime().toString(), "1-MONTH" );
        roomDbHelper.newRoom( "Kitchen", "Mirrors", null, Actions.WIPE, calendar.getTime().toString(), "1-MONTH" );
    }
}
