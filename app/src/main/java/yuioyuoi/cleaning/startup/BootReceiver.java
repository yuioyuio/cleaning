package yuioyuoi.cleaning.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import yuioyuoi.cleaning.data.RoomDbHelper;
import yuioyuoi.cleaning.formatters.NotificationFormatter;
import yuioyuoi.cleaning.model.Room;
import yuioyuoi.cleaning.notification.NotificationScheduler;

/**
 * Created by Jean on 20/11/2016.
 */

public class BootReceiver extends BroadcastReceiver
{
    private static final String TAG = "BootReceiver";

    /**
     * When we boot, we need to fetch all rooms and their reminders, if the reminder is in the
     * future we schedule a notification for that point in time
     * @param context
     * @param intent
     */
    @Override
    public void onReceive( Context context, Intent intent )
    {
        Log.i( TAG, "beep boop - we booted!" );

        RoomDbHelper roomDbHelper = new RoomDbHelper( context );
        List<Room> roomList = roomDbHelper.getAllRooms();

        for( Room room : roomList )
        {


            //Calendar calendar = Calendar.getInstance();
            //calendar.setTimeInMillis( calendar.getTimeInMillis() + 5000 );


            //Log.i( TAG, "we have actually scheduled " + room.name + " at " + calendar.getTime() );

            NotificationScheduler.getInstance().scheduleNotification( context, room.name, room.reminder ); //calendar.getTime() );
        }
    }
}
