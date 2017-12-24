package io.github.alessandrojean.mangachecklists.data.updater;

/**
 * Class copied from Tachiyomi repository.
 * Available at: https://github.com/inorichi/tachiyomi
 */
public class GitHubUpdateResult {
    public static class NewUpdate extends GitHubUpdateResult {
        private GitHubRelease release;

        public NewUpdate(GitHubRelease release) {
            this.release = release;
        }

        public GitHubRelease getRelease() {
            return release;
        }

        public void setRelease(GitHubRelease release) {
            this.release = release;
        }
    }

    public static class NoNewUpdate extends GitHubUpdateResult {

    }
}
