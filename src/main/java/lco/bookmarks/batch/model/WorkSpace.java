package lco.bookmarks.batch.model;

import java.nio.file.Paths;

public class WorkSpace {

    public static final String WORK_DESTINATION = String.valueOf(Paths.get(System.getProperty("java.io.tmpdir"), "BookmarksManager"));

    private String workDestinationPath;

    public WorkSpace() {
        this.workDestinationPath = WORK_DESTINATION;
    }

    public String getWorkDestinationPath() {
        return workDestinationPath;
    }

    public void setWorkDestinationPath(String workDestinationPath) {
        this.workDestinationPath = workDestinationPath;
    }
}
