package com.hanna.test.cookieinformation.javasample;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.cookieinformation.mobileconsents.CallListener;
import com.cookieinformation.mobileconsents.GetConsents;
import com.cookieinformation.mobileconsents.storage.ConsentWithType;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ActivityResultLauncher<Bundle> listener = registerForActivityResult(new GetConsents(getApplication()), result -> {
      for (Map.Entry<UUID, ? extends ConsentWithType> entry : result.entrySet()) {
        Log.d("Show Entry", entry.getKey() + ": "+entry.getValue().getType()+" : "+entry.getValue().getConsented());
      }
    });

    findViewById(R.id.display_always).setOnClickListener(v -> ((MyApplication) getApplication()).getSdk()
        .displayConsents(listener));

    findViewById(R.id.display_if_needed).setOnClickListener(v -> ((MyApplication) getApplication()).getSdk()
        .displayConsentsIfNeeded(listener, new Function1<Exception, Unit>() {
          @Override public Unit invoke(Exception e) {
            Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            return null;
          }
        }));

    findViewById(R.id.reset_all_consents).setOnClickListener(v -> ((MyApplication) getApplication()).getSdk()
        .resetAllConsentChoices(
            new Continuation<Unit>() {
              @NonNull @Override public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
              }

              @Override public void resumeWith(@NonNull Object o) {

              }
            }));

    findViewById(R.id.get_with_type).setOnClickListener(v -> ((MyApplication) getApplication()).getSdk()
        .getSavedConsentsWithType(
            new CallListener<Map<UUID, ConsentWithType>>() {
              @Override public void onSuccess(Map<UUID, ConsentWithType> result) {
                Object[] array = result.values().toArray();
                for (int i = 0; i < array.length; i++) {
                  Log.d("TAG", "onSuccess: " + array[i]);
                }
              }

              @Override public void onFailure(@NonNull IOException error) {

              }
            }));
  }
}