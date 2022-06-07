package com.example.bboxphotoapp;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * Generic confirmation dialog fragment.
 * 
 * Constructor changes the text of the dialog title and message via arguments.
 * 
 * Returns boolean depending on the positive (confirm) and negative (cancel) button.
 * 
 */
public class ConfirmDialogFrag extends AppCompatDialogFragment {

    private static final String TAG = "ConfirmDialogFrag";
    
    private String title;
    private String message;

    private String requestKey; 
    private String bundleKey;
    
    private Bundle bundle;
    
    public ConfirmDialogFrag(String title, String message) {
        this.title = title;
        this.message = message;
        this.bundle = new Bundle();
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        requestKey = getString(R.string.rq_confirmation);
        bundleKey = getString(R.string.bn_confirmation);
        
        builder.setTitle(title);
        builder.setMessage(message);
        
        builder.setNegativeButton("Cancel", ((dialogInterface, i) -> {
            bundle.putBoolean(bundleKey, false);
            getParentFragmentManager().setFragmentResult(requestKey, bundle);
            Log.i(TAG, "M/onCreateDialog: cancel");
        }));
        
        builder.setPositiveButton("Confirm", ((dialogInterface, i) -> {
            bundle.putBoolean(bundleKey, true);
            getParentFragmentManager().setFragmentResult(requestKey, bundle);
            
            Log.i(TAG, "M/onCreateDialog: confirm");
        }));
        
        return builder.create();
    }
}
