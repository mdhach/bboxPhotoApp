package com.example.bboxphotoapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    
    private TextView settingsInfo;
    
    private final String infoMsg = "Current Save Location:\n\n";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        Button settingsSaveLocation = findViewById(R.id.settings_save_location);
        Button settingsInitJSON = findViewById(R.id.settings_init_json);
        Button settingsZipFiles = findViewById(R.id.settings_zip_files);
        settingsInfo = findViewById(R.id.settings_info);
        
        String currSaveLoc = infoMsg + PrefsManager.getValue(PrefsManager.saveLocKey);
        
        settingsInfo.setText(currSaveLoc);
        settingsInfo.setVisibility(View.VISIBLE);
        
        settingsSaveLocation.setOnClickListener(view -> addSaveLocDialogFrag());
        settingsZipFiles.setOnClickListener(view -> {
            String title = getString(R.string.cdt_zip_files);
            String message = getString(R.string.cdm_zip_files);
            addConfirmDialog(title, message);
        });

        
        settingsInitJSON.setOnClickListener(view -> {
            JSONManager.reinitializeJSON(this);

            Toast.makeText(this, "JSON has been reinitialized!", Toast.LENGTH_SHORT).show();
        });
        
//        Log.d(TAG, "M/onCreate: getFilesDir() " + getFilesDir().toString());
//        Log.d(TAG, "M/onCreate: MediaStore " + MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        Log.d(TAG, "M/onCreate: MediaStore getPath() " + MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
//        Log.d(TAG, "M/onCreate: External" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
    }

    private void addSaveLocDialogFrag() {
        SaveLocDialogFrag dialogFrag = new SaveLocDialogFrag();
        dialogFrag.show(getSupportFragmentManager(), "SaveLocDialogFrag");

        String requestKey = getString(R.string.rq_save_location);
        String bundleKey = getString(R.string.bn_save_location);

        getSupportFragmentManager()
                .setFragmentResultListener(requestKey, this, (key, bundle) -> {
                    if(key.equals(requestKey)) {
                        PrefsManager.setValue(PrefsManager.saveLocKey, bundle.getString(bundleKey));
                        String currSaveLoc = infoMsg + PrefsManager.getValue(PrefsManager.saveLocKey);
                        settingsInfo.setText(currSaveLoc);
                        getSupportFragmentManager().clearFragmentResultListener(requestKey);
                    }
                });
    }

    private void zipFiles() {
        String sourcePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        String saveLocation = PrefsManager.getValue(PrefsManager.saveLocKey);

        // manager object to zip files at arg0 (sourcePath) and save to arg1 (saveLocation)
        ZipManager zipManager = new ZipManager(sourcePath, saveLocation);

        // zip function returns a boolean for callback
        if(zipManager.zip()) {
            String lastPathComponent = saveLocation.substring(saveLocation.lastIndexOf("/") + 1);
            Toast.makeText(this, "Success! Zip saved to: " + lastPathComponent, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "M/zipFiles: file saved as " + PrefsManager.getValue(PrefsManager.saveNameKey));
            Log.d(TAG, "M/zipFiles: file saved at " + saveLocation);
        } else {
            // NOTE: a file will still be created even if the zip function fails to execute properly
            Toast.makeText(this, "Failed to save images to: " + saveLocation, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates a confirmation pop-up and returns a boolean.
     * 
     * @param title name of the dialog pop-up box
     * @param message shown text
     * @return boolean true if user confirms, false if user cancels
     */
    private void addConfirmDialog(String title, String message) {
        ConfirmDialogFrag dialogFrag = new ConfirmDialogFrag(title, message);
        dialogFrag.show(getSupportFragmentManager(), "ConfirmDialogFrag");

        String requestKey = getString(R.string.rq_confirmation);
        String bundleKey = getString(R.string.bn_confirmation);

        getSupportFragmentManager()
                .setFragmentResultListener(requestKey, this, (key, bundle) -> {
                    if(key.equals(requestKey)) {
                        if(bundle.getBoolean(bundleKey)) {
                            zipFiles();
                        }
                    }
                    getSupportFragmentManager().clearFragmentResultListener(requestKey);
                });

    }
    
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // finishes activity if the phone 'back' button is pressed (not the app 'back' button)
        finish();
    }
    
}
