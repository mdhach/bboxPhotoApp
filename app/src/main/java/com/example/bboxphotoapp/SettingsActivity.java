package com.example.bboxphotoapp;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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
        infoMsg = "Current Save Location:\n\n";
        
        Button settingsSaveLocation = findViewById(R.id.settings_save_location);
        Button settingsInitJSON = findViewById(R.id.settings_init_json);
        Button settingsZipFiles = findViewById(R.id.settings_zip_files);
        settingsInfo = findViewById(R.id.settings_info);
        
        String currSaveLoc = infoMsg + PrefsManager.getValue(PrefsManager.saveLocKey);
        
        settingsInfo.setText(currSaveLoc);
        settingsInfo.setVisibility(View.VISIBLE);
        
        settingsSaveLocation.setOnClickListener(view -> addSaveLocDialogFrag());
        
        settingsZipFiles.setOnClickListener(view -> {
            String message = getString(R.string.cdm_zip_files);
            Runnable r = this::zipFiles;
            addConfirmDialog(title, message, r);
        });
        
        settingsInitJSON.setOnClickListener(view -> {
//            String message = getString(R.string.cdm_reinit_json);
//            Runnable r = () -> JSONManager.initJSON(this);
//            addConfirmDialog(title, message, r);
            Toast.makeText(this, "Feature temporarily disabled!", Toast.LENGTH_SHORT).show();
        });
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
                        PrefsManager.setValue(PrefsManager.saveLocKey, bundle.getString(bundleKey));
                        String currSaveLoc = infoMsg + PrefsManager.getValue(PrefsManager.saveLocKey);
                        settingsInfo.setText(currSaveLoc);
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

            Log.d(TAG, "M/zipFiles: failed to save zip; refer to ZipManager.class");
        }
    }

    /**
     * Creates a confirmation dialog box.
     * 
     * @param title name of the dialog pop-up box
     * @param message display text
     */
    private void addConfirmDialog(String title, String message, Runnable func) {
        ConfirmDialogFrag dialogFrag = new ConfirmDialogFrag(title, message);
        dialogFrag.show(getSupportFragmentManager(), "ConfirmDialogFrag");

        String requestKey = getString(R.string.rq_confirmation);
        String bundleKey = getString(R.string.bn_confirmation);
        
        getSupportFragmentManager()
                .setFragmentResultListener(requestKey, this, (key, bundle) -> {
                    if(key.equals(requestKey)) {
                        if(bundle.getBoolean(bundleKey)) {
                            func.run();
                            Log.d(TAG, "M/addConfirmDialog: request success!");
                        } else {
                            Log.d(TAG, "M/addConfirmDialog: incorrect bundleKey");
                        }
                    } else {
                        Log.d(TAG, "M/addConfirmDialog: incorrect requestKey");
                    }
                    getSupportFragmentManager().clearFragmentResultListener(requestKey);
                });
    }
    
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // finishes activity if the phone 'back' button is pressed (not the app 'back' button)
        Log.d(TAG, "M/onBackPressed: return to main...");
        Log.d(TAG, "M/onBackPressed: isMainNull? " + JSONManager.isMainNull());
        finish();
    }
    
}
