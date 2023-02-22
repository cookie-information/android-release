package com.hanna.test.cookieinformation.javasample;

import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.cookieinformation.mobileconsents.GetConsents;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ActivityResultLauncher<Bundle> listener = registerForActivityResult(new GetConsents(getApplication()), result -> {

    });

    findViewById(R.id.display_always).setOnClickListener(v -> ((MyApplication) getApplication()).getSdk().displayConsents(listener));

    findViewById(R.id.display_if_needed).setOnClickListener(v -> ((MyApplication) getApplication()).getSdk().displayConsentsIfNeeded(listener));
  }
}