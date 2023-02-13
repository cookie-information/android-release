package com.hanna.test.cookieinformation.javasample;

import android.view.View;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.cookieinformation.mobileconsents.GetConsents;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ActivityResultLauncher<Bundle> listener = registerForActivityResult(new GetConsents(getApplication()), result -> {

    });

    ((Button) findViewById(R.id.display_always)).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ((MyApplication) getApplication()).getSdk().displayConsents(listener);
      }
    });
  }
}