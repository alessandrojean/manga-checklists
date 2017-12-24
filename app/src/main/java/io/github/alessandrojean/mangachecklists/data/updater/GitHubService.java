package io.github.alessandrojean.mangachecklists.data.updater;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Interface copied from Tachiyomi repository.
 * Available at: https://github.com/inorichi/tachiyomi
 */
public interface GitHubService {
    @GET("/repos/alessandrojean/manga-checklists/releases/latest")
    Observable<GitHubRelease> getLatestVersion();
}
