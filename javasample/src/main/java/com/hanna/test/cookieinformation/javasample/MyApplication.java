package com.hanna.test.cookieinformation.javasample;

import android.app.Application;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cookieinformation.mobileconsents.CallListener;
import com.cookieinformation.mobileconsents.ConsentItem;
import com.cookieinformation.mobileconsents.Consentable;
import com.cookieinformation.mobileconsents.MobileConsentSdk;
import com.cookieinformation.mobileconsents.MobileConsents;
import com.cookieinformation.mobileconsents.models.MobileConsentCredentials;
import com.cookieinformation.mobileconsents.models.MobileConsentCustomUI;

import java.io.IOException;
import java.util.Map;

import kotlin.coroutines.Continuation;
import kotlinx.coroutines.Dispatchers;

public class MyApplication extends Application implements Consentable {

    @NonNull
    @Override
    public MobileConsents getSdk() {
        return new MobileConsents(provideConsentSdk(), Dispatchers.getMain());
    }


    @Nullable
    @Override
    public Object getSavedConsents(@NonNull Continuation<? super Map<ConsentItem.Type, Boolean>> continuation) {
        return getSdk().getSavedConsents(new CallListener<Map<ConsentItem.Type, Boolean>>() {
            @Override
            public void onSuccess(Map<ConsentItem.Type, Boolean> typeBooleanMap) {

            }

            @Override
            public void onFailure(@NonNull IOException e) {

            }
        });
    }

    @NonNull
    @Override
    public MobileConsentSdk provideConsentSdk() {
        return MobileConsentSdk.Builder(this)
            .setClientCredentials(provideCredentials())
            .setMobileConsentCustomUI(new MobileConsentCustomUI(Color.parseColor("#ff0000")))
            .setLanguage("da")
            .build();
    }

    @NonNull
    @Override
    public MobileConsentCredentials provideCredentials() {
        return new MobileConsentCredentials(
            "40dbe5a7-1c01-463a-bb08-a76970c0efa0",
            "68cbf024407a20b8df4aecc3d9937f43c6e83169dafcb38b8d18296b515cc0d5f8bca8165d615caa4d12e236192851e9c5852a07319428562af8f920293bc1db",
            "4113ab88-4980-4429-b2d1-3454cc81197b"
        );
    }
}