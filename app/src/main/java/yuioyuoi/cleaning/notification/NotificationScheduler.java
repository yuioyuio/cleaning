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

    public void scheduleNotification( Context context, String content, Date utcReminder )
    {
        Intent notificationIntent = new Intent( context, NotificationPublisher.class );
        notificationIntent.putExtra( NotificationPublisher.NOTIFICATION_ID, content.hashCode() );
        notificationIntent.putExtra( NotificationPublisher.NOTIFICATION, getNotification( context, content ) );
        PendingIntent pendingIntent = PendingIntent.getBroadcast( context, content.hashCode(), notificationIntent, PendingIntent.FLAG_ONE_SHOT );

        Date utcNow = Calendar.getInstance( TimeZone.getTimeZone( "UTC" ) ).getTime();
        if( utcReminder.after( utcNow ) )
        {
            AlarmManager alarmManager = ( AlarmManager ) context.getSystemService( Context.ALARM_SERVICE );
            alarmManager.set( AlarmManager.RTC_WAKEUP, utcReminder.getTime(), pendingIntent );
        }
        else
        {
            // TODO if nag mode turned on, immediately inform the user to get their shit together and clean their house
            Log.i( TAG, "reminder already passed! we nag the user" );
        }
    }

    private Notification getNotification( Context context, String content ) {
        Notification.Builder builder = new Notification.Builder( context );
        builder.setContentTitle( "Scheduled Notification" );
        builder.setContentText( content );
        builder.setSmallIcon( R.mipmap.ic_launcher );
        return builder.build();
    }
}
