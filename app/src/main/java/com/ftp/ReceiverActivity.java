package com.ftp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.ftp.databinding.ActivityReceiverBinding;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiverActivity extends AppCompatActivity {

    private ActivityReceiverBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView statusText = binding.textStatus;
        TextView ipText = binding.texIp;

        String ipAddress = getLocalIpAddress();
        ipText.setText("IP: " + ipAddress);
        generateQRCode(ipAddress);

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(8988)) {
                runOnUiThread(() -> statusText.setText("Waiting for sender..."));

                while (true) {
                    Socket socket = serverSocket.accept();
                    runOnUiThread(() -> statusText.setText("Connection accepted..."));

                    new Thread(() -> {
                        try {
                            DataInputStream dis = new DataInputStream(socket.getInputStream());

                            String fileName = dis.readUTF();
                            long fileSize = dis.readLong();

                            runOnUiThread(() -> {
                                binding.progressCard.setVisibility(View.VISIBLE);
                                binding.tvFileName.setText(fileName);
                                binding.progressBar.setProgress(0);
                                binding.tvProgress.setText("0%");
                                binding.textStatus.setText("Receiving " + fileName + " (" + fileSize / 1024 + " KB)");
                            });

                            long received = 0;
                            File file = new File(getExternalFilesDir(null), fileName);
                            try (FileOutputStream fos = new FileOutputStream(file)) {
                                byte[] buffer = new byte[4096];
                                int len;
                                while (received < fileSize && (len = dis.read(buffer)) > 0) {
                                    fos.write(buffer, 0, len);
                                    received += len;

                                    int progress = (int) ((received * 100) / fileSize);
                                    runOnUiThread(() -> {
                                        binding.progressBar.setProgress(progress);
                                        binding.tvProgress.setText(progress + "%");
                                    });
                                }
                            }

                            runOnUiThread(() -> {
                                binding.progressCard.setVisibility(View.GONE);
                                statusText.setText("File received: " + file.getAbsolutePath());
                            });

                            dis.close();
                            socket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> statusText.setText("Error: " + e.getMessage()));
                        }
                    }).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> statusText.setText("Server error: " + e.getMessage()));
            }
        }).start();
    }

    private String getLocalIpAddress() {
        try {
            for (java.util.Enumeration<java.net.NetworkInterface> en = java.net.NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                java.net.NetworkInterface intf = en.nextElement();
                if (intf.getName().equalsIgnoreCase("wlan0")) {
                    for (java.util.Enumeration<java.net.InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        java.net.InetAddress inetAddress = enumIpAddr.nextElement();
                        if (inetAddress instanceof java.net.Inet4Address && !inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "Unavailable";
    }

    private void generateQRCode(String ip) {
        try {
            int size = 500;
            com.google.zxing.Writer writer = new com.google.zxing.qrcode.QRCodeWriter();
            com.google.zxing.common.BitMatrix bitMatrix = writer.encode(ip, com.google.zxing.BarcodeFormat.QR_CODE, size, size);

            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            runOnUiThread(() -> binding.qrImageView.setImageBitmap(bitmap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
