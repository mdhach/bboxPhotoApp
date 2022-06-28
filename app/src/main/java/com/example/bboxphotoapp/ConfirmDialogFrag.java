package com.example.bboxphotoapp;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

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
    private String inputKey;
    
    private Bundle bundle;
    
    private boolean requireInput;
    private EditText input;
    
    public ConfirmDialogFrag(String title, String message) {
        this.title = title;
        this.message = message;
        this.bundle = new Bundle();
    }
    
    public ConfirmDialogFrag(String title, String message, boolean requireInput) {
        this.title = title;
        this.message = message;
        this.bundle = new Bundle();
        this.requireInput = requireInput;
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        requestKey = getString(R.string.rq_confirmation);
        bundleKey = getString(R.string.bn_confirmation);
        inputKey = getString(R.string.bn_dialog_input);
        
        builder.setTitle(title);
        builder.setMessage(message);
        
        if(requireInput) {
            input = new EditText(requireContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
        }
        
        builder.setNegativeButton("Cancel", ((dialogInterface, i) -> {
            bundle.putBoolean(bundleKey, false);
            getParentFragmentManager().setFragmentResult(requestKey, bundle);
            
            Log.d(TAG, "M/onCreateDialog: action cancel");
        }));
        
        builder.setPositiveButton("Confirm", ((dialogInterface, i) -> {
            bundle.putBoolean(bundleKey, true);
            
            if(requireInput) { bundle.putString(inputKey, input.getText().toString()); }
            
            getParentFragmentManager().setFragmentResult(requestKey, bundle);
            
            Log.d(TAG, "M/onCreateDialog: action confirm");
        }));
        
        return builder.create();
    }
}
