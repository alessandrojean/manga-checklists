package io.github.alessandrojean.mangachecklists.data.notification;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import java.util.Arrays;
import java.util.List;

import io.github.alessandrojean.mangachecklists.R;

/**
 * Class copied from Tachiyomi repository.
 * Available at: https://github.com/inorichi/tachiyomi
 */
public class Notifications {
    public static final String CHANNEL_COMMON = "common_channel";
    // One of the Beta World Lines from Steins;Gate ;)
    public static final int ID_UPDATER = 1048596;

    /**
     * Creates the notification channels introduced in Android Oreo.
     *
     * @param context The application context.
     */
    public static void createChannels(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        List<NotificationChannel> channels = Arrays.asList(
            new NotificationChannel(CHANNEL_COMMON, context.getString(R.string.channel_common),
                  NotificationManager.IMPORTANCE_LOW)
        );

        String notificationService = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(notificationService);
        notificationManager.createNotificationChannels(channels);
    }
}
