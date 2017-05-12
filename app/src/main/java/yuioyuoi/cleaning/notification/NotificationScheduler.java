package yuioyuoi.cleaning.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import yuioyuoi.cleaning.R;
import yuioyuoi.cleaning.formatters.NotificationFormatter;

public class NotificationScheduler
{
    private static final String TAG = "NotificationScheduler";

    private NotificationScheduler() {}

    private static NotificationScheduler INSTANCE = new NotificationScheduler();

    public static NotificationScheduler getInstance()
    {
        return INSTANCE;
    }

    public int i = 0;

    public void scheduleNotification( Context context, String roomName, Date utcReminder )
    {
        Intent notificationIntent = new Intent( context, NotificationPublisher.class );
        String message = NotificationFormatter.formatNotification( roomName );

        int notificationId = message.hashCode();
        Notification notification = NotificationPublisher.getNotification( context, message );
        notificationIntent.putExtra( NotificationPublisher.NOTIFICATION_ID, notificationId );
        notificationIntent.putExtra( NotificationPublisher.NOTIFICATION, notification );
        PendingIntent pendingIntent = PendingIntent.getBroadcast( context, notificationId, notificationIntent, PendingIntent.FLAG_ONE_SHOT );

        Date utcNow = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) ).getTime();
        if( utcReminder.after( utcNow ) )
        {
            AlarmManager alarmManager = ( AlarmManager ) context.getSystemService( Context.ALARM_SERVICE );
            alarmManager.set( AlarmManager.RTC_WAKEUP, utcReminder.getTime(), pendingIntent );

            Log.i( TAG, "we have scheduled " + roomName + " at " + utcReminder.toString() + " UTC" );
        }
        else
        {
            Log.i( TAG, "reminder already passed! we nag the user" );
            // immediately publish the notification
            NotificationPublisher.publishNotification( context, notificationId, notification );
        }
    }
}
