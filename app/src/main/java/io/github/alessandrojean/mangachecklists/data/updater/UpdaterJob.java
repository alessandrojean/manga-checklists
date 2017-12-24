package io.github.alessandrojean.mangachecklists.data.updater;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import io.github.alessandrojean.mangachecklists.R;
import io.github.alessandrojean.mangachecklists.data.notification.Notifications;

/**
 * Class copied from Tachiyomi repository.
 * Available at: https://github.com/inorichi/tachiyomi
 */
public class UpdaterJob extends Job {
    public static final String TAG = "UpdateChecker";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        return new GitHubUpdateChecker()
                    .checkForUpdate()
                    .map(gitHubUpdateResult -> {
                        if (gitHubUpdateResult instanceof GitHubUpdateResult.NewUpdate) {
                            GitHubUpdateResult.NewUpdate update = (GitHubUpdateResult.NewUpdate) gitHubUpdateResult;
                            String url = update.getRelease().getDownloadLink();

                            Intent intent = new Intent(getContext(), UpdaterService.class);
                            intent.putExtra(UpdaterService.EXTRA_DOWNLOAD_URL, url);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), Notifications.CHANNEL_COMMON);
                            builder.setContentTitle(getContext().getString(R.string.app_name));
                            builder.setContentText(getContext().getString(R.string.update_check_notification_update_available));
                            builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
                            // Download action
                            builder.addAction(android.R.drawable.stat_sys_download_done,
                                    getContext().getString(R.string.action_download),
                                    PendingIntent.getService(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));

                            String notificationService = Context.NOTIFICATION_SERVICE;
                            NotificationManager manager = (NotificationManager) getContext().getSystemService(notificationService);
                            manager.notify(Notifications.ID_UPDATER, builder.build());
                        }

                        return Result.SUCCESS;
                    })
                    .onErrorReturn(t -> Result.FAILURE)
                    .toBlocking()
                    .single();
    }

    public static void setupTask() {
        new JobRequest.Builder(TAG)
                .setPeriodic(TimeUnit.DAYS.toMillis(1), TimeUnit.HOURS.toMillis(1))
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    public static void cancelTask() {
        JobManager.instance().cancelAllForTag(TAG);
    }
}
