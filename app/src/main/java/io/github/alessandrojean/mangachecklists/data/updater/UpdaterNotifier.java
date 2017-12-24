package io.github.alessandrojean.mangachecklists.data.updater;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import io.github.alessandrojean.mangachecklists.R;
import io.github.alessandrojean.mangachecklists.data.notification.NotificationHandler;
import io.github.alessandrojean.mangachecklists.data.notification.NotificationReceiver;
import io.github.alessandrojean.mangachecklists.data.notification.Notifications;

/**
 * Class copied from Tachiyomi repository.
 * Available at: https://github.com/inorichi/tachiyomi
 */
public class UpdaterNotifier {
    private Context context;
    private NotificationCompat.Builder notification;

    public UpdaterNotifier(Context context) {
        this.context = context;
        notification = new NotificationCompat.Builder(context, Notifications.CHANNEL_COMMON);
    }

    private void showNotification() {
        String notificationService = Context.NOTIFICATION_SERVICE;
        NotificationManager manager = (NotificationManager) context.getSystemService(notificationService);

        manager.notify(Notifications.ID_UPDATER, notification.build());
    }

    /**
     * Call when apk download starts.
     *
     * @param title tile of notification.
     */
    public void onDownloadStarted(String title) {
        notification.setContentTitle(title);
        notification.setContentText(context.getString(R.string.update_check_notification_download_in_progress));
        notification.setSmallIcon(android.R.drawable.stat_sys_download);
        notification.setOngoing(true);

        showNotification();
    }

    /**
     * Call when apk download progress changes.
     *
     * @param progress progress of download (xx%/100).
     */
    public void onProgressChange(int progress) {
        notification.setProgress(100, progress, false);
        notification.setOnlyAlertOnce(true);

        showNotification();
    }

    /**
     * Call when apk download is finished.
     *
     * @param uri path location of apk.
     */
    public void onDownloadFinished(Uri uri) {
        notification.setContentText(context.getString(R.string.update_check_notification_download_complete));
        notification.setSmallIcon(android.R.drawable.stat_sys_download_done);
        notification.setOnlyAlertOnce(false);
        notification.setProgress(0, 0, false);
        // Install action
        notification.setContentIntent(NotificationHandler.installApkPendingActivity(context, uri));
        notification.addAction(R.drawable.ic_arrow_down_bold_box_outline_grey600_24dp,
                context.getString(R.string.action_install),
                NotificationHandler.installApkPendingActivity(context, uri));
        notification.addAction(R.drawable.ic_close_grey600_24dp,
                context.getString(R.string.action_cancel),
                NotificationReceiver.dismissNotificationPendingBroadcast(context, Notifications.ID_UPDATER));

        showNotification();
    }

    /**
     * Call when apk download throws a error.
     *
     * @param url web location of apk to download.
     */
    public void onDownloadError(String url) {
        notification.setContentText(context.getString(R.string.update_check_notification_download_error));
        notification.setSmallIcon(android.R.drawable.stat_sys_warning);
        notification.setOnlyAlertOnce(false);
        notification.setProgress(0, 0, false);
        // Retry action.
        notification.addAction(R.drawable.ic_refresh_grey600_24dp,
                context.getString(R.string.action_try_again),
                UpdaterService.downloadApkPendingService(context, url));
        // Cancel action.
        notification.addAction(R.drawable.ic_close_grey600_24dp,
                context.getString(R.string.action_cancel),
                NotificationReceiver.dismissNotificationPendingBroadcast(context, Notifications.ID_UPDATER));

        showNotification();
    }

}
