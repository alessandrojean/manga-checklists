package io.github.alessandrojean.mangachecklists.data.updater;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Class copied from Tachiyomi repository.
 * Available at: https://github.com/inorichi/tachiyomi
 */
public class GitHubRelease {
    @SerializedName("tag_name")
    private String version;

    @SerializedName("body")
    private String changelog;

    @SerializedName("assets")
    private List<Asset> assets;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public String getDownloadLink() {
        return assets.get(0).getDownloadLink();
    }

    public class Asset {
        @SerializedName("browser_download_url")
        private String downloadLink;

        public String getDownloadLink() {
            return downloadLink;
        }

        public void setDownloadLink(String downloadLink) {
            this.downloadLink = downloadLink;
        }
    }
}
