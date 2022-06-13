package com.example.bboxphotoapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.io.File;
import java.io.IOException;

/**
 * Creates a dialog fragment that changes the save location of the zip and JSON file.
 * 
 */
public class SaveLocDialogFrag extends AppCompatDialogFragment {

    private static final String TAG = "SaveLocDialogFrag";
    
    private Button saveLocation;
    private String requestKey;
    private String bundleKey;
    private String newPath = null;

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "V/resultLauncher: action confirm");
                        
                        Intent resultIntent = result.getData();
                        if (resultIntent != null) {
                            // convert uri to document uri
                            Uri uri = resultIntent.getData();
                            String id = DocumentsContract.getTreeDocumentId(uri);
                            Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri, id);
                            
                            // get real path
                            newPath = Utils.getPath(requireContext(), docUri);
                            
                            // set button text to relative path
                            String relativePath = Utils.getRelativePath(newPath);
                            saveLocation.setText(relativePath);
                            
                            // logs for checking old and new path
                            Log.d(TAG, "V/resultLauncher: old path " + PrefsManager.getValue(PrefsManager.saveLocKey));
                            Log.d(TAG, "V/resultLauncher: new path " + newPath);
                        } else {
                            Log.d(TAG, "V/resultLauncher: Invalid intent...");
                        }
                    } else {
                        Log.d(TAG, "V/resultLauncher: action deny");
                    }
                }
            }
    );

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.save_location_dialog_fragment, null);
        
        saveLocation = view.findViewById(R.id.changeSaveLocation);

        String currentPath = Utils.getRelativePath(PrefsManager.getValue(PrefsManager.saveLocKey));
        
        saveLocation.setText(currentPath);

        requestKey = getString(R.string.rq_save_location);
        bundleKey = getString(R.string.bn_save_location);
        
        // open folder explorer
        saveLocation.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            resultLauncher.launch(intent);
        });
        
        builder.setView(view)
                .setTitle("Change save location")
                
                .setNegativeButton("Cancel", ((dialogInterface, i) -> {
                    Log.d(TAG, "M/onCreateDialog: action cancel");
                }))
                
                .setPositiveButton("Confirm", ((dialogInterface, i) -> {
                    Bundle bundle = new Bundle();
                    
                    if(newPath != null) {
                        // add new path to bundle
                        Log.d(TAG, "M/onCreateDialog: added newPath to bundle " + newPath);
                        bundle.putString(bundleKey, newPath);
                    } else {
                        // else add current path if no new path was set
                        Log.d(TAG, "M/onCreateDialog: newPath null; added current path");
                        bundle.putString(bundleKey, currentPath);
                    }
                    // pass bundle to parent activity
                    getParentFragmentManager().setFragmentResult(requestKey, bundle);

                    Log.d(TAG, "M/onCreateDialog: action confirm");
                }));
        
        return builder.create();
    }
}
