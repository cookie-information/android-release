package com.hanna.test.cookieinformation.javasample;

import android.app.Application;
import android.graphics.Color;

import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cookieinformation.mobileconsents.CallListener;
import com.cookieinformation.mobileconsents.ConsentItem;
import com.cookieinformation.mobileconsents.Consentable;
import com.cookieinformation.mobileconsents.MobileConsentSdk;
import com.cookieinformation.mobileconsents.MobileConsents;
import com.cookieinformation.mobileconsents.models.BodyStyle;
import com.cookieinformation.mobileconsents.models.MobileConsentCredentials;
import com.cookieinformation.mobileconsents.models.MobileConsentCustomUI;

import com.cookieinformation.mobileconsents.models.SdkTextStyle;
import com.cookieinformation.mobileconsents.models.SubtitleStyle;
import com.cookieinformation.mobileconsents.models.TitleStyle;
import com.cookieinformation.mobileconsents.storage.ConsentWithType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.UUID;
import kotlin.ParameterName;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.Dispatchers;

public class MyApplication extends Application implements Consentable {

    @Override public void initSDKWithCompletion(
        @NonNull Function2<? super Boolean, ? super IOException, Unit> onComplete
    ) {
        getSdk().initSDKWithCompletion(onComplete);
    }

    @NonNull
    @Override
    public MobileConsents getSdk() {
        return new MobileConsents(provideConsentSdk(), Dispatchers.getMain());
    }

    @Nullable
    @Override
    public Object getSavedConsents(@NonNull Continuation<? super Map<UUID, Boolean>> continuation) {
        return getSdk().getSavedConsents(new CallListener<Map<UUID, Boolean>>() {
            @Override
            public void onSuccess(Map<UUID, Boolean> typeBooleanMap) {

            }

            @Override
            public void onFailure(@NonNull IOException e) {

            }
        });
    }

    @Nullable
    @Override
    public Object getSavedConsentsWithType(@NonNull Continuation<? super Map<UUID, ConsentWithType>> continuation) {
        return getSdk().getSavedConsentsWithType(new CallListener<Map<UUID, ConsentWithType>>() {
            @Override
            public void onSuccess(Map<UUID, ConsentWithType> typeBooleanMap) {

            }

            @Override
            public void onFailure(@NonNull IOException e) {

            }
        });
    }

    @NonNull
    @Override
    public MobileConsentSdk provideConsentSdk() {
        List<Locale> localList = new ArrayList();
        localList.add(new Locale("da"));
        localList.add(Locale.FRENCH);
        return MobileConsentSdk.Builder(this)
            .setClientCredentials(provideCredentials())
            .setMobileConsentCustomUI(
                new MobileConsentCustomUI(Color.parseColor("#ff0000"),
                    new SdkTextStyle(
                    new TitleStyle(null),
                    new SubtitleStyle(Typeface.MONOSPACE),
                    new BodyStyle(null)
                )))
            .setLanguages(localList)
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