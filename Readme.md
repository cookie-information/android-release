[![Maven Central](https://img.shields.io/maven-central/v/com.cookieinformation/mobileconsents.svg?label=latest%20release)](https://search.maven.org/artifact/com.cookieinformation/mobileconsents)
### Using the SDK:
### Integration: 
To add SDK to your app add dependency in build.gradle(.kts) file:

Kotlin DSL
```kotlin
implementation("com.cookieinformation:mobileconsents:<latest_release>")
```
#### Setup

Join our partner program for free at [Cookie Information](https://cookieinformation.com/)
You will then receive credentials, that will need to be provided for initializing the sdk.
-----------------------------------------
This library is provided to you, to integrate mobile consents in an easy way.
Lets get started.

#Here are the update for release: 0.2.6:
-Enable resetting consents, either all or by Type.
-Support multiple language, and enforce selecting at least one language. Please note, these languages must be 
    synchronized with the consents dashboard.
-Consent List UI, has updated to a clearer separation of required and optional consents.


#Here are the main objects you should be familiar with:
```kotlin
class MobileConsentSdk
class MobileConsentCredentials
class MobileConsentCustomUI
```

The above objects are required in order to initialize the sdk.
MobileConsentSdk - is the object that handles all the consents info, and state. This is init using the builder pattern.
    -it is also dependant on the MobileConsentCredentials and the MobileConsentCustomUI.

MobileConsentCredentials - The object that contains all credentials required for fetching the relevant data for your company/application.

MobileConsentCustomUI - The color theme used for various components that are included in the library.

Steps to implement:
-Extend the application class. (Dont forget to add to manifest)
-Make you Extended Application class 'Consentable' - (This is a defined interface, defined in the library).
-Implement the required methods.
Here is a sample:

#### Kotlin:
```kotlin
class App : Application(), Consentable {

  override val sdk: MobileConsents by lazy { MobileConsents(provideConsentSdk()) }
  
  override fun provideConsentSdk() = MobileConsentSdk.Builder(this)
    .setClientCredentials(provideCredentials())
    .setMobileConsentCustomUI(MobileConsentCustomUI(Color.parseColor("any hexcode color string")))
    .setLanguage("A list of locales that the consents support.") //please ensure your consents are set to have the the corresponding translation on the dashboard.
    .build()

  override fun provideCredentials(): MobileConsentCredentials {
    return MobileConsentCredentials(
      clientId = "Client ID provided XXXXX",
      solutionId = "Solution ID provided XXXXXXX",
      clientSecret = "Client Secret ID provided XXXXX"
    )
  }
}
```
#### Java:
```java
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
        .setMobileConsentCustomUI(MobileConsentCustomUI(Color.parseColor("any hexcode color string")))
        .setLanguage("A string that represent the language, wanted") //please ensure your consents are set to have the the corresponding translation on the dashboard.
        .build();
  }

  @NonNull
  @Override
  public MobileConsentCredentials provideCredentials() {
    return new MobileConsentCredentials(
        "Client ID provided XXXXX",
        "Client Secret ID provided XXXXX",
        "Solution ID provided XXXXXXX"
    );
  }
}
```


Once this is implemented, your application is ready to handle consents out of the box.
There are 2 main functions in the sdk, that are responsible for navigating to the mobile consents component
These methods take an activity listener, so the component calling them can know how to handle the updated consents settings.
```kotlin
fun displayConsents(listener)
fun displayConsentsIfNeeded(listener)
```

Here is a sample:
```kotlin
class MainActivity : AppCompatActivity() {

  private val listener: ActivityResultLauncher<Bundle?> = registerForActivityResult(
    GetConsents(this),
  ) {
    //observes and returns a map of the consent and the values.
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById<Button>(R.id.display_always).setOnClickListener {
      (applicationContext as App).sdk.displayConsents(listener)
    }

    findViewById<Button>(R.id.display_if_needed).setOnClickListener {
      (applicationContext as App).sdk.displayConsentsIfNeeded(listener)
    }
  }
}
```

This is all required to get this to work!
Good luck:)




