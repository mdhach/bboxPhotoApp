package com.example.bboxphotoapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendEmailActivity extends AppCompatActivity {

    private static final String TAG = "SendEmailActivity";

    private EditText emailTo;
    private EditText emailSubject;
    private EditText emailMessage;

    private TextView emailTxtViewAttachment;
    
    private Uri uri;
    
    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                            Intent resultIntent = result.getData();
                            if (resultIntent != null) {
                                uri = resultIntent.getData();
                                emailTxtViewAttachment.setText(uri.getLastPathSegment());
                                emailTxtViewAttachment.setVisibility(View.VISIBLE);
                            } else {
                                Log.d(TAG, "V/resultLauncher: Intent or uri is invalid");
                            }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        emailTo = findViewById(R.id.emailTo);
        emailSubject = findViewById(R.id.emailSubject);
        emailMessage = findViewById(R.id.emailMessage);

        Button emailSend = findViewById(R.id.emailSend);
        Button emailAttachment = findViewById(R.id.emailAttachment);

        emailTxtViewAttachment = findViewById(R.id.emailTxtViewAttachment);

        emailSend.setOnClickListener(v -> sendEmail());
        emailAttachment.setOnClickListener(v -> addAttachment());
        
    }

    private void sendEmail() {
        try {
            String stringTo = emailTo.getText().toString();
            String stringSubject = emailSubject.getText().toString();
            String stringMessage = emailMessage.getText().toString();
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{stringTo});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, stringSubject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, stringMessage);
            if(uri != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            }
            this.startActivity(Intent.createChooser(emailIntent, "Launching email provider..."));
        } catch(Throwable t) {
            Toast.makeText(this, "Request failed, try again...", Toast.LENGTH_SHORT).show();
        }

    }

    private void addAttachment() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra("return-data", true);
        resultLauncher.launch(Intent.createChooser(intent, "Choose File "));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // finishes activity if the phone 'back' button is pressed (not the app 'back' button)
        finish();
    }
}