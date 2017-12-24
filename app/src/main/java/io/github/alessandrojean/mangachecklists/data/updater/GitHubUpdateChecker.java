package io.github.alessandrojean.mangachecklists.data.updater;

import io.github.alessandrojean.mangachecklists.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Class copied from Tachiyomi repository.
 * Available at: https://github.com/inorichi/tachiyomi
 */
public class GitHubUpdateChecker {
    private GitHubService service;

    public GitHubUpdateChecker() {
        createService();
    }

    private void createService() {
        Retrofit restAdapter = new Retrofit.Builder()
                                    .baseUrl("https://api.github.com")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                    .build();

        service = restAdapter.create(GitHubService.class);
    }

    /**
     * Returns observable containing release information.
     */
    public Observable<GitHubUpdateResult> checkForUpdate() {
        return service.getLatestVersion().map(gitHubRelease -> {
            String newVersion = gitHubRelease.getVersion();

            // Check if latest version is different from current version.
            if (newVersion != BuildConfig.VERSION_NAME) {
                return new GitHubUpdateResult.NewUpdate(gitHubRelease);
            }
            else {
                return new GitHubUpdateResult.NoNewUpdate();
            }
        });
    }
}
