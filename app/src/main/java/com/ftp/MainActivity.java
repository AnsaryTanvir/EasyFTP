package com.ftp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.ftp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSender.setOnClickListener(v -> startActivity(new Intent(this, SenderActivity.class)));
        binding.btnReceiver.setOnClickListener(v -> startActivity(new Intent(this, ReceiverActivity.class)));
        binding.githubLinkTextView.setOnClickListener(v->{
            String url = "https://github.com/AnsaryTanvir/EasyFTP";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            v.getContext().startActivity(intent);
        });
    }


}