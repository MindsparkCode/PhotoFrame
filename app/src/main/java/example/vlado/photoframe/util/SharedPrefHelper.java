package example.vlado.photoframe.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import example.vlado.photoframe.R;
import example.vlado.photoframe.Settings;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by vlado on 28/01/2017.
 */

public class SharedPrefHelper {

    public static void saveSettings(Settings settings, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.key_default_shared_preferences), MODE_PRIVATE);
        SharedPreferences.Editor edit= preferences.edit();

        edit.putInt(context.getString(R.string.key_delay), settings.getDelay());
        edit.putString(context.getString(R.string.key_photos_folder_path), settings.getPhotosFolderPath());

        edit.apply();
    }

    public static Settings getSettings(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.key_default_shared_preferences), MODE_PRIVATE);

        Settings settings = new Settings();

        settings.setDelay(preferences.getInt(context.getString(R.string.key_delay), 30));
        settings.setPhotosFolderPath(preferences.getString(context.getString(R.string.key_photos_folder_path),
                                Environment.getExternalStorageDirectory().getAbsolutePath() + "/PhotoFrame"));

        return settings;
    }
}
