package io.github.alessandrojean.mangachecklists.data.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.github.alessandrojean.mangachecklists.BuildConfig;

/**
 * Class copied from Tachiyomi repository.
 * Available at: https://github.com/inorichi/tachiyomi
 */
public class NotificationReceiver extends BroadcastReceiver {
    private static String NAME = "NotificationReceiver";

    // Called to dismiss notification.
    private static String ACTION_DISMISS_NOTIFICATION = BuildConfig.APPLICATION_ID + NAME + ".ACTION_DISMISS_NOTIFICATION";

    // Value containing notification id.
    private static String EXTRA_NOTIFICATION_ID = BuildConfig.APPLICATION_ID + NAME + ".NOTIFICATION_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_DISMISS_NOTIFICATION))
            dismissNotification(context, intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1));
    }

    /**
     * Returns [PendingIntent] that starts a service which dismissed the notification.
     *
     * @param context context of application.
     * @param notificationId id of notification.
     * @return [PendingIntent]
     */
    public static PendingIntent dismissNotificationPendingBroadcast(Context context, int notificationId) {
        Intent intent = new Intent(context,NotificationReceiver.class);
        intent.setAction(ACTION_DISMISS_NOTIFICATION);
        intent.putExtra(EXTRA_NOTIFICATION_ID, notificationId);

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Dismiss the notification.
     *
     * @param notificationId the id of the notification.
     */
    private void dismissNotification(Context context, int notificationId) {
        String notificationService = Context.NOTIFICATION_SERVICE;
        NotificationManager manager = (NotificationManager) context.getSystemService(notificationService);

        manager.cancel(notificationId);
    }
}
