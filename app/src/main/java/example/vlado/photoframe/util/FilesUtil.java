package example.vlado.photoframe.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by vlado on 30/01/2017.
 */

public class FilesUtil {

    private static final String TAG = FilesUtil.class.getSimpleName();

    public static List<File> loadImageList(String photosFolderPath) {
        File photosFolder;
        photosFolder = new File(photosFolderPath);

        File[] photosArray = photosFolder.listFiles(new FilenameFilter() {

            String[] imageFileExtensions = new String[]{"jpg", "png", "jpeg"};

            @Override
            public boolean accept(File file, String s) {
                for (String extension : imageFileExtensions) {
                    if (s.toLowerCase().endsWith(extension)) {
                        return true;
                    }
                }
                return false;
            }
        });

        if (photosArray == null || photosArray.length < 1) {
            return null;
        }

        List<File> imageList = Arrays.asList(photosArray);
        Collections.shuffle(imageList);
        return imageList;
    }
}
