package com.example.bboxphotoapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class SaveLocDialogFrag extends AppCompatDialogFragment {

    private static final String TAG = "SaveLocDialogFrag";
    
    private Button saveLocation;
    private String requestKey;
    private String bundleKey;

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent resultIntent = result.getData();
                        if (resultIntent != null) {
                            Uri uri = resultIntent.getData();
                            saveLocation.setText(String.valueOf(uri));
                            Log.d(TAG, "V/resultLauncher: new path " + uri);
                        } else {
                            Log.d(TAG, "V/resultLauncher: Intent or uri is invalid");
                        }
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
        saveLocation.setText(PrefsManager.getValue(PrefsManager.saveNameKey));

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
                    Log.i(TAG, "M/onCreateDialog: cancel");
                }))
                
                .setPositiveButton("Confirm", ((dialogInterface, i) -> {
                    // init bundle with new save location
                    Bundle bundle = new Bundle();
                    bundle.putString(bundleKey, saveLocation.getText().toString());
                    
                    // pass bundle to parent activity
                    getParentFragmentManager().setFragmentResult(requestKey, bundle);
                    
                    // confirmation toast
                    Toast.makeText(requireActivity(), "New location set!", Toast.LENGTH_SHORT).show();

                    Log.i(TAG, "M/onCreateDialog: confirm");
                }));
        
        return builder.create();
    }
}
