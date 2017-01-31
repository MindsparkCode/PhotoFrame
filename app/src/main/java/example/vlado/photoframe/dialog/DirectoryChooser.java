package example.vlado.photoframe.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import example.vlado.photoframe.R;

public class DirectoryChooser {
    private static final String PARENT_DIR = "..";

    private final Context context;
    private ListView listView;
    private Dialog dialog;
    private File currentPath;
    private DirectorySelectedListener listener;

    public interface DirectorySelectedListener {
        void directorySelected(String directory);
    }

    public DirectoryChooser(final Context context, final DirectorySelectedListener listener) {
        this.context = context;
        this.listener = listener;

        listView = new ListView(context);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int which, long id) {
                String directoryChosen = (String) listView.getItemAtPosition(which);
                File chosenDirectory = getChosenDirectory(directoryChosen);
                if (chosenDirectory.isDirectory()) {
                    refresh(chosenDirectory);
                }
            }
        });

        int paddingDp = 8;
        final float scale = context.getResources().getDisplayMetrics().density;
        int paddingPx = (int) (paddingDp * scale + 0.5f);

        listView.setPadding(paddingPx, 2 * paddingPx, paddingPx, 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(listView)
                .setTitle(context.getString(R.string.choose_photo_folder))
                .setPositiveButton(R.string.choose_current_folder, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listener != null) {
                            listener.directorySelected(currentPath.getAbsolutePath());
                            dialog.dismiss();
                        }
                    }
                });

        dialog = builder.create();

        refresh(Environment.getExternalStorageDirectory());
    }

    public void showDialog() {
        dialog.show();
    }

    public Dialog getDialog() {
        return dialog;
    }

    /**
     * Sort, filter and display the files for the given path.
     */
    private void refresh(File path) {
        this.currentPath = path;
        if (path.exists()) {
            File[] dirs = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return (file.isDirectory() && file.canRead() && !file.getName().toLowerCase().startsWith("."));
                }
            });
            File[] files = path.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (!file.isDirectory()) {
                        if (!file.canRead()) {
                            return false;
                        }
                        return !file.getName().toLowerCase().startsWith(".");
                    } else {
                        return false;
                    }
                }
            });

            // convert to an array
            int i = 0;
            String[] fileList;
            if (path.equals(Environment.getExternalStorageDirectory())) {
                fileList = new String[dirs.length + files.length];
            } else {
                fileList = new String[dirs.length + files.length + 1];
                fileList[i++] = PARENT_DIR;
            }
            Arrays.sort(dirs);
            Arrays.sort(files);
            for (File dir : dirs) {
                fileList[i++] = dir.getName();
            }
            for (File file : files) {
                fileList[i++] = file.getName();
            }

            listView.setAdapter(new ArrayAdapter(context,
                    android.R.layout.simple_list_item_1, fileList) {
                @Override
                public View getView(int pos, View view, ViewGroup parent) {
                    view = super.getView(pos, view, parent);
                    ((TextView) view).setSingleLine(true);
                    return view;
                }
            });
        }
    }


    /**
     * Convert a relative filename into an actual File object.
     */
    private File getChosenDirectory(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) {
            return currentPath.getParentFile();
        } else {
            return new File(currentPath, fileChosen);
        }
    }
}

