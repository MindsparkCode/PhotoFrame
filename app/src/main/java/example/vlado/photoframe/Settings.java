package example.vlado.photoframe;

/**
 * Created by vlado on 28/01/2017.
 */

public class Settings {

    private int delay;
    private String photosFolderUrl;

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getPhotosFolderPath() {
        return photosFolderUrl;
    }

    public void setPhotosFolderPath(String photosFolderUrl) {
        this.photosFolderUrl = photosFolderUrl;
    }
}
