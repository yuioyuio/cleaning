package yuioyuoi.cleaning.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import yuioyuoi.cleaning.R;
import yuioyuoi.cleaning.activity.Dashboard;

/**
 * Created by Jean on 20/11/2016.
 */

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive( Context context, Intent intent )
    {
        System.out.println( "ALARM!" );

        // extract the notification and its ID from the intent
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        System.out.println( "notifying with id " + id + " " + notification.toString());

        publishNotification( context, id, notification );

        Intent refreshIntent = new Intent( Dashboard.REFRESH_DASHBOARD );
        context.sendBroadcast( refreshIntent );
    }

    public static void publishNotification( Context context, int id, Notification notification )
    {
        NotificationManager notificationManager = ( NotificationManager ) context.getSystemService( Context.NOTIFICATION_SERVICE );
        notificationManager.notify( id, notification );
    }

    public static Notification getNotification( Context context, String content )
    {
        Notification.Builder builder = new Notification.Builder( context );
        builder.setContentTitle( "Scheduled Notification" );
        builder.setContentText( content );
        builder.setSmallIcon( R.mipmap.ic_launcher );
        return builder.build();
    }
}
