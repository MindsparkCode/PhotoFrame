package example.vlado.photoframe.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by vlado on 30/01/2017.
 */

public class FilesUtil {

    private static final String TAG = FilesUtil.class.getSimpleName();
    private static String[] imageFileExtensions = new String[]{"jpg", "png", "jpeg"};

    public static List<File> loadImageList(String photosFolderPath, boolean includeSubdirectories) {
        File photosFolder = new File(photosFolderPath);

        List<File> imageList;

        if (includeSubdirectories) {
            imageList = getPhotosInSubdirectories(photosFolder);
        } else {
            imageList = getPhotosInRootOnly(photosFolder);
        }

        if (imageList != null) {
            Collections.shuffle(imageList);
        }

        return imageList;
    }

    public static boolean isPhotoPathValid(String photosFolderPath, boolean includeSubdirectories) {
        return loadImageList(photosFolderPath, includeSubdirectories).size() > 0;
    }

    private static List<File> getPhotosInRootOnly(File photosDirectory) {
        File[] photosArray = photosDirectory.listFiles(new FilenameFilter() {

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

        if (photosArray == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(photosArray);
    }

    private static List<File> getPhotosInSubdirectories(File photosDirectory) {
        List<File> imageList = new ArrayList<>();
        File[] files = photosDirectory.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File file, String s) {
                if (new File(file.getAbsolutePath() + "/" + s).isDirectory()){
                    return true;
                }
                for (String extension : imageFileExtensions) {
                    if (s.toLowerCase().endsWith(extension)) {
                        return true;
                    }
                }
                return false;
            }
        });

        if (files == null) return Collections.emptyList();

        for (File file : files) {
            if (file.isDirectory()) {
                imageList.addAll(getPhotosInSubdirectories(file));
            } else {
                imageList.add(file);
            }
        }

        return imageList;
    }
}
