package com.example.bboxphotoapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    
    private TextView settingsInfo;
    
    private String title;
    private String infoMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        title = getString(R.string.cdt_title);
        infoMsg = "Current Save Location: ";
        
        // init views
        Button settingsSaveLocation = findViewById(R.id.settings_save_location);
        Button settingsCleanJSON = findViewById(R.id.settings_clean_json);
        Button settingsRenewJSON = findViewById(R.id.settings_renew_json);
        Button settingsZipFiles = findViewById(R.id.settings_zip_files);
        Button settingsReset = findViewById(R.id.settings_reset);
        settingsInfo = findViewById(R.id.settings_info);
        
        setInfoText();
        
        // allows user to change the default save location for the JSON and zip
        settingsSaveLocation.setOnClickListener(view -> addSaveLocDialogFrag());

        settingsCleanJSON.setOnClickListener(view -> {
            String message = getString(R.string.cdm_clean_json);
            String warning = getString(R.string.cdw_clean_json);

            // clean json on confirmation
            Runnable r = () -> JSONManager.cleanJSON(JSONManager.getImageKeys(this));

            // add confirmation dialog box
            Utils.addConfirmDialog(this, title, message, warning, 0, r);
        });

        settingsRenewJSON.setOnClickListener(view -> {
            String message = getString(R.string.cdm_renew_json);
            String info = getString(R.string.cdi_renew_json);

            // recreate JSONFile on confirmation
            Runnable r = JSONManager::saveJSONAsFile;

            // add confirmation dialog box
            Utils.addConfirmDialog(this, title, message, info, 1, r);
            //Toast.makeText(this, "Feature temporarily disabled!", Toast.LENGTH_SHORT).show();
        });
        
        settingsZipFiles.setOnClickListener(view -> {
            String message = getString(R.string.cdm_zip_files);
            
            // zip files on confirmation
            Runnable r = this::zipFiles;

            // add confirmation dialog box
            Utils.addConfirmDialog(this, title, message, r);
        });

        settingsReset.setOnClickListener(view -> {
            String message = getString(R.string.cdm_reset);
            String cdi = getString(R.string.cdi_reset);
            String info = cdi + "\n\nDefault: " 
                    + "\n\nSave Path: " + PrefsManager.getDefaultSaveLocation()
                    + "\n\nFile Name: " + PrefsManager.getDefaultSaveName() 
                    + "\n\nImage Directory: " + PrefsManager.getDefaultImageDir();
            // zip files on confirmation
            Runnable r = PrefsManager::reset;

            // add confirmation dialog box
            Utils.addConfirmDialog(this, title, message, info, 1, r);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setInfoText();
    }

    /**
     * Dialog fragment to change the save location of the zip file
     * 
     */
    private void addSaveLocDialogFrag() {
        SaveLocDialogFrag dialogFrag = new SaveLocDialogFrag();
        dialogFrag.show(getSupportFragmentManager(), "SaveLocDialogFrag");

        String requestKey = getString(R.string.rq_save_location);
        String bundleKey = getString(R.string.bn_save_location);

        getSupportFragmentManager()
                .setFragmentResultListener(requestKey, this, (key, bundle) -> {
                    if(key.equals(requestKey)) {
                        // get path result from dialog and set value in SharedPreferences
                        String path = bundle.getString(bundleKey);
                        PrefsManager.setValue(PrefsManager.saveLocKey, path);
                        
                        Log.d(TAG, "M/addSaveLocDialogFrag: Preferred save location set to {" + path + "}");
                        
                        // set info text view to relative path
                        setInfoText();
                        
                        getSupportFragmentManager().clearFragmentResultListener(requestKey);
                    }
                });
    }

    /**
     * Zips images file at image directory to the SharedPreferences save location
     * 
     */
    private void zipFiles() {
        String sourcePath = PrefsManager.getValue(PrefsManager.imgDirKey);
        String saveLocation = PrefsManager.getValue(PrefsManager.saveLocKey);

        // manager object to zip files at sourcePath and save to saveLocation
        ZipManager zipManager = new ZipManager(sourcePath, saveLocation);

        // zip function returns a boolean for callback
        if(zipManager.zip()) {
            // toast relative path to user
            String relativePath = Utils.getRelativePath(saveLocation);
            Toast.makeText(this, "Success! Zip saved to: " + relativePath, Toast.LENGTH_SHORT).show();
            
            // logs to verify save directory
            Log.d(TAG, "M/zipFiles: file saved as " + PrefsManager.getValue(PrefsManager.saveNameKey));
            Log.d(TAG, "M/zipFiles: file saved at " + saveLocation);
        } else {
            // NOTE: a file may still be created even if the zip function fails to complete execution
            Toast.makeText(this, "Failed to save images to: " + saveLocation, Toast.LENGTH_SHORT).show();

            Log.d(TAG, "M/zipFiles: zipManager.zip() function returned FALSE; please refer to logs in ZipManager.class");
        }
    }

    /**
     * Sets save location info text to current value stored in SharedPreferences
     */
    private void setInfoText() {
        // current zip save location
        String path = PrefsManager.getValue(PrefsManager.saveLocKey);
        String relativePath = Utils.getRelativePath(path);
        String infoText = infoMsg + relativePath;
        settingsInfo.setText(infoText);
    }
    
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // finishes activity if the phone 'back' button is pressed (not the app 'back' button)
        finish();
    }
    
}
