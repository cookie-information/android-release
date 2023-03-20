package com.hanna.test.cookieinformation.javasample;

import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.cookieinformation.mobileconsents.ConsentItem;
import com.cookieinformation.mobileconsents.GetConsents;
import java.util.UUID;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ActivityResultLauncher<Bundle> listener = registerForActivityResult(new GetConsents(getApplication()), result -> {

    });

    findViewById(R.id.display_always).setOnClickListener(v -> ((MyApplication) getApplication()).getSdk()
        .displayConsents(listener));

    findViewById(R.id.display_if_needed).setOnClickListener(v -> ((MyApplication) getApplication()).getSdk()
        .displayConsentsIfNeeded(listener));

    findViewById(R.id.reset_all_consents).setOnClickListener(v -> ((MyApplication) getApplication()).getSdk()
        .resetAllConsentChoices(
            new Continuation<Unit>() {
              @NonNull @Override public CoroutineContext getContext() {
                return null;
              }

              @Override public void resumeWith(@NonNull Object o) {

              }
            }));

    findViewById(R.id.reset_all_consents).setOnClickListener(v ->
        ((MyApplication) getApplication()).getSdk().resetConsentChoice(
            UUID.randomUUID(),
            new Continuation<Unit>() {
              @NonNull @Override public CoroutineContext getContext() {
                return null;
              }

              @Override public void resumeWith(@NonNull Object o) {

              }
            }
        )
    );
  }
}