package example.vlado.photoframe.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wefika.horizontalpicker.HorizontalPicker;

import org.greenrobot.eventbus.EventBus;

import example.vlado.photoframe.R;
import example.vlado.photoframe.Settings;
import example.vlado.photoframe.util.SharedPrefHelper;
import example.vlado.photoframe.util.FilesUtil;

/**
 * Created by vlado on 28/01/2017.
 */

public class SettingsDialogFragment extends AppCompatDialogFragment implements DirectoryChooser.DirectorySelectedListener {

    private Settings settings;
    private AlertDialog dialog;

    private HorizontalPicker numberPicker;
    private TextView photosFolderTextView;
    private TextView choosePhotoFolderTextview;

    private String[] pickerValues = new String[]{"0", "5", "10", "15", "20", "30", "50", "100", "200", "500", "1000", "2000", "5000"};

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_settings, null);

        builder.setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        settings.setDelay(Integer.parseInt(pickerValues[numberPicker.getSelectedItem()]));
                        settings.setPhotosFolderPath(photosFolderTextView.getText().toString());
                        EventBus.getDefault().post(settings);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SettingsDialogFragment.this.getDialog().cancel();
                    }
                })
                .setTitle(R.string.settings);

        dialog = builder.create();

        settings = SharedPrefHelper.getSettings(getContext());

        numberPicker = (HorizontalPicker) view.findViewById(R.id.delay_number_picker);
        numberPicker.setValues(pickerValues);
        String currentDelay = settings.getDelay() + "";
        int currentDelayIndex = java.util.Arrays.binarySearch(pickerValues, currentDelay);
        if (currentDelayIndex > -1) {
            numberPicker.setSelectedItem(currentDelayIndex);
        }

        photosFolderTextView = (TextView) view.findViewById(R.id.photos_folder_text_view);
        photosFolderTextView.setText(settings.getPhotosFolderPath());
        photosFolderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDirectoryChooserDialog();
            }
        });
        photosFolderTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean canCancel = FilesUtil.loadImageList(settings.getPhotosFolderPath()) != null;
                checkIfPhotoPathValid(editable.toString(), canCancel);
            }
        });

        choosePhotoFolderTextview = (TextView) view.findViewById(R.id.choose_photo_folder_text_view);
        choosePhotoFolderTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDirectoryChooserDialog();
            }
        });

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean canCancel = FilesUtil.loadImageList(settings.getPhotosFolderPath()) != null;
        checkIfPhotoPathValid(photosFolderTextView.getText().toString(), canCancel);
    }

    private void checkIfPhotoPathValid(String path, boolean canCancel) {
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        if (FilesUtil.loadImageList(path) == null) {
            photosFolderTextView.setError(getString(R.string.no_photos_to_load));
            positiveButton.setEnabled(false);
        } else {
            photosFolderTextView.setError(null);
            positiveButton.setEnabled(true);
        }
        negativeButton.setEnabled(canCancel);
        dialog.setCancelable(canCancel);
    }

    private void showDirectoryChooserDialog() {
        DirectoryChooser directoryChooser = new DirectoryChooser(getContext(), this);
        directoryChooser.showDialog();
    }

    @Override
    public void directorySelected(String folderPath) {
        photosFolderTextView.setText(folderPath);
    }

//    private void removeDividerFromPicker(NumberPicker picker) {
//
//        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
//        for (java.lang.reflect.Field selectionDivider : pickerFields) {
//            if (selectionDivider.getName().equals("mSelectionDivider")) {
//                try {
//                    selectionDivider.setAccessible(true);
//                    selectionDivider.set(picker, null);
//                } catch (IllegalArgumentException | IllegalAccessException | Resources.NotFoundException e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
//        }
//    }
}
