package com.ftp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.ftp.databinding.ActivitySenderBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class SenderActivity extends AppCompatActivity {

    private ActivitySenderBinding binding;
    private static final int PICK_FILE = 1;
    private Uri fileUri;
    private long totalBytes = 0;
    private long sentBytes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySenderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        binding.btnChooseFile.setOnClickListener(v -> pickFile());
        binding.btnSendFile.setOnClickListener(v -> {
            if (fileUri != null && !binding.edittextIp.getText().toString().isEmpty()) {
                sendFile(binding.edittextIp.getText().toString());
            } else {
                Toast.makeText(this, "Select file and enter IP", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnScanQr.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(SenderActivity.this);
            integrator.setPrompt("Scan receiver's IP QR code");
            integrator.setOrientationLocked(false);
            integrator.initiateScan();
        });

        binding.topAppBar.setNavigationOnClickListener(v -> finish());
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                binding.edittextIp.setText(result.getContents());
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_FILE && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            String fileName = getFileName(fileUri);
            if (fileName != null) {
                binding.btnChooseFile.setText(fileName);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void sendFile(String ip) {
        new Thread(() -> {
            try {
                runOnUiThread(() -> binding.progressCard.setVisibility(View.VISIBLE));

                totalBytes = getContentResolver().openFileDescriptor(fileUri, "r").getStatSize();
                sentBytes = 0;
                updateProgress(0);

                Socket socket = new Socket(ip, 8988);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                InputStream fis = getContentResolver().openInputStream(fileUri);
                String fileName = getFileName(fileUri);

                runOnUiThread(() -> binding.tvFileName.setText(fileName));

                // Send metadata
                dos.writeUTF(fileName);
                dos.writeLong(totalBytes);

                // Send file data
                byte[] buffer = new byte[4096];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    dos.write(buffer, 0, len);
                    sentBytes += len;
                    int progress = (int) ((sentBytes * 100) / totalBytes);
                    updateProgress(progress);
                }

                fis.close();
                dos.close();
                socket.close();

                runOnUiThread(() -> {
                    Toast.makeText(this, "File sent!", Toast.LENGTH_SHORT).show();
                    binding.progressCard.setVisibility(View.GONE);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    binding.progressCard.setVisibility(View.GONE);
                });
            }
        }).start();
    }

    private void updateProgress(int progress) {
        runOnUiThread(() -> {
            binding.progressBar.setProgress(progress);
            binding.tvProgress.setText(progress + "%");
        });
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) result = cursor.getString(index);
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}
