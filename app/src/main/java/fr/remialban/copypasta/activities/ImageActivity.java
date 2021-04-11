package fr.remialban.copypasta.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.io.InputStream;

import fr.remialban.copypasta.R;
import fr.remialban.copypasta.tools.ScanHelper;

public class ImageActivity extends AppCompatActivity {

    TextView textResult;
    Button saveButton;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        init();
    }

    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textResult = findViewById(R.id.text_result);
        saveButton = findViewById(R.id.save_btn);
        imageView = findViewById(R.id.image_view);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent().putExtra("content", textResult.getText());
                setResult(1, intent);

                //setIntent(intent);
                finish();
            }
        });
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            imageView.setImageURI(imageUri);
            this.scan(imageUri);

        } else {
            finish();
        }
    }

    private void scan(Uri imageUri) {

        try {
            InputImage inputImage = InputImage.fromFilePath(getApplicationContext(), imageUri);

            ScanHelper scan = new ScanHelper(inputImage, getIntent().getIntExtra("request", -1), (Vibrator) getSystemService(Context.VIBRATOR_SERVICE)) {
                @Override
                public void onSuccess(String text) {
                    textResult.setText(text);
                    if (getIntent().getIntExtra("request", -1) == ScanHelper.CODE_SCAN_BARCODE)
                    {
                        Intent intent = getIntent().putExtra("content", textResult.getText());
                        setResult(1, intent);

                        //setIntent(intent);
                        finish();
                    }
                }

                @Override
                public void onComplete(String text) {
                    //textResult.setText(text);

                }
            };
        } catch (IOException e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
    }
}