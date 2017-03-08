package example.vlado.photoframe;

/**
 * Created by vlado on 28/01/2017.
 */

public class Settings {

    private int delay;
    private String photosFolderPath;
    private boolean includeSubdirectories;

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getPhotosFolderPath() {
        return photosFolderPath;
    }

    public void setPhotosFolderPath(String photosFolderUrl) {
        this.photosFolderPath = photosFolderUrl;
    }

    public boolean isIncludeSubdirectories() {
        return includeSubdirectories;
    }

    public void setIncludeSubdirectories(boolean includeSubdirectories) {
        this.includeSubdirectories = includeSubdirectories;
    }

    public int getDelayInMs() {
        return delay * 1000;
    }

    public boolean equals(Settings settings) {
        return  delay == settings.delay &&
                ((photosFolderPath == settings.photosFolderPath) ||
                (photosFolderPath != null && photosFolderPath.equals(settings.photosFolderPath))) &&
                includeSubdirectories == settings.includeSubdirectories;
    }
}
