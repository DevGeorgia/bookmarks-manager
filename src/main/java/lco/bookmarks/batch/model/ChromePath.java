package lco.bookmarks.batch.model;

import java.nio.file.Paths;

public class ChromePath {

    public static final String CHROME_BOOKMARK_PATH = String.valueOf(Paths.get(System.getProperty("user.home"), "AppData", "Local", "Google", "Chrome", "User Data", "Default", "Bookmarks"));

    private String chromeBookmarkPath;

    public ChromePath() {
        this.chromeBookmarkPath = CHROME_BOOKMARK_PATH;
    }

    public String getChromeBookmarkPath() {
        return chromeBookmarkPath;
    }

    public void setChromeBookmarkPath(String chromeBookmarkPath) {
        this.chromeBookmarkPath = chromeBookmarkPath;
    }
}
