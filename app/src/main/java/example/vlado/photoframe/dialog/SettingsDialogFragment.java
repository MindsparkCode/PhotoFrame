package example.vlado.photoframe.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.wefika.horizontalpicker.HorizontalPicker;

import org.greenrobot.eventbus.EventBus;

import example.vlado.photoframe.R;
import example.vlado.photoframe.Settings;
import example.vlado.photoframe.util.FilesUtil;
import example.vlado.photoframe.util.SharedPrefHelper;

/**
 * Created by vlado on 28/01/2017.
 */

public class SettingsDialogFragment extends AppCompatDialogFragment implements DirectoryChooser.DirectorySelectedListener {

    private Settings settings;
    private AlertDialog dialog;

    private HorizontalPicker delayNumberPicker;
    private EditText delayEditText;

    private TextView photosFolderTextView;
    private TextView choosePhotoFolderTextview;

    private String[] pickerValues = new String[]{"0", "5", "10", "15", "20", "30", "50", "100", "200", "500", "1000", "2000", "5000"};
    private CheckBox includeSubdirectoriesCheckBox;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            view = inflater.inflate(R.layout.dialog_settings, null);
        } else {
            view = inflater.inflate(R.layout.dialog_settings_pre_ics, null);
        }

        settings = SharedPrefHelper.getSettings(getContext());

        builder.setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            settings.setDelay(Integer.parseInt(pickerValues[delayNumberPicker.getSelectedItem()]));
                        } else {
                            settings.setDelay(Integer.parseInt(delayEditText.getText().toString()));
                        }
                        settings.setPhotosFolderPath(photosFolderTextView.getText().toString());
                        settings.setIncludeSubdirectories(includeSubdirectoriesCheckBox.isChecked());
                        EventBus.getDefault().post(settings);
                    }
                })
                .setTitle(R.string.settings);

        boolean canCancel = FilesUtil.isPhotoPathValid(settings.getPhotosFolderPath(), settings.isIncludeSubdirectories());
        if (canCancel) {
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SettingsDialogFragment.this.getDialog().cancel();
                }
            });
        }

        dialog = builder.create();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            delayNumberPicker = (HorizontalPicker) view.findViewById(R.id.delay_number_picker);
            delayNumberPicker.setValues(pickerValues);
            String currentDelay = settings.getDelay() + "";
            int currentDelayIndex = java.util.Arrays.binarySearch(pickerValues, currentDelay);
            if (currentDelayIndex > -1) {
                delayNumberPicker.setSelectedItem(currentDelayIndex);
            }
        } else {
            delayEditText = (EditText) view.findViewById(R.id.delay_edit_text);
            delayEditText.setText(settings.getDelay() + "");
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
                checkIfPhotoPathValid(editable.toString(), includeSubdirectoriesCheckBox.isChecked());
            }
        });

        choosePhotoFolderTextview = (TextView) view.findViewById(R.id.choose_photo_folder_text_view);
        choosePhotoFolderTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDirectoryChooserDialog();
            }
        });

        includeSubdirectoriesCheckBox = (CheckBox) view.findViewById(R.id.include_subdirectories_checkbox);
        includeSubdirectoriesCheckBox.setChecked(settings.isIncludeSubdirectories());
        includeSubdirectoriesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkIfPhotoPathValid(photosFolderTextView.getText().toString(), includeSubdirectoriesCheckBox.isChecked());
            }
        });

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        checkIfPhotoPathValid(photosFolderTextView.getText().toString(), includeSubdirectoriesCheckBox.isChecked());
    }

    private void checkIfPhotoPathValid(String path, boolean includeSubdirectories) {
        boolean isPathValid = FilesUtil.isPhotoPathValid(path, includeSubdirectories);

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (isPathValid) {
            photosFolderTextView.setTextColor(includeSubdirectoriesCheckBox.getCurrentTextColor());
            choosePhotoFolderTextview.setText(R.string.select_photo_folder);
            positiveButton.setEnabled(true);
        } else {
            photosFolderTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.material_red));
            choosePhotoFolderTextview.setText(R.string.select_valid_photo_folder);
            positiveButton.setEnabled(false);
        }
    }

    private void showDirectoryChooserDialog() {
        DirectoryChooser directoryChooser = new DirectoryChooser(getContext(), this);
        directoryChooser.showDialog();
    }

    @Override
    public void directorySelected(String folderPath) {
        photosFolderTextView.setText(folderPath);
    }

}
