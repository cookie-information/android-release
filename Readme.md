# Android SDK
[![Maven Central](https://img.shields.io/maven-central/v/com.cookieinformation/mobileconsents.svg?label=latest%20release)](https://search.maven.org/artifact/com.cookieinformation/mobileconsents)


## Installation
MobileConsentsSDK is available through Gradle, by adding te dependency to your project..
Kotlin DSL


```kotlin
implementation("com.cookieinformation:mobileconsents:<latest_release>")
```


Groovy
```kotlin
implementation 'com.cookieinformation:mobileconsents:<latest_release>'
```
## Initializing
Create an instance of the sdk, and initialize prior to accessing the sdk at any point.
You can initialize with or without theming and styling (for theming and styling its required to have compose dependencies.)
The SDK credentials can be fetched from the platform here: https://go.cookieinformation.com/login
This SDK requires you to ensure the language used for the sdk, is fully set on the platform, and all data exists.
So if you choose to use the sdk in German, please ensure all fields are set on the platform.


The recommended implementation would be having the sdk invoking (most likely) the showPrivacyPopUpIfNeeded,
when needed, so just initialize prior to calling the method


The minimum required data for initializing the SDK would be the following:


```kotlin
   ConsentsUISDK.init(
       clientID = "<CLIENT_ID>",
       clientSecret = "<CLIENT_SECRET>",
       solutionId = "<SOLUTION ID>",
       context = this@MainActivity
   )
```


Here is an example of all the arguments and data that support the SKD.


```kotlin
   ConsentsUISDK.init(
   clientID = "<CLIENT_ID>",
   clientSecret = "<CLIENT_SECRET>",
   solutionId = "<SOLUTION ID>",
   languageCode = "FR",//Can be null, in this case it will take the devices configured language.
   typography = defaultTypography.copy(bodyMedium = TextStyle.Default),
   customDarkColorScheme = darkColorScheme(
       primary = Color.Yellow,
       secondary = Color.Green,
       tertiary = Color.Black
   ),
   customLightColorScheme = lightColorScheme(
       primary = Color.Red,
       secondary = Color.Yellow,
       tertiary = Pink40
   ),
   context = this@MainActivity
)
```
You will also need to add the Internet permission to your AndroidManifiest file
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
### UI language
The language set for the sdk, will be the language used for all components, and will ignore the systems language.
In case no language has been set, (in the init function), the sdk will use the devices configured language.
Its important to note, it is the developers responsibility to ensure that any potential languages to be used are set on CookieInformation's platform.
Ensure data is provided in the selected language within the panel. Otherwise, the popup will not be displayed.
# Using built-in mobile consents UI


SDK contains built-in screens for managing consents.
Please ensure you set the correct language code you expect the consents to use, and that has been
fully set on the platform.


## Consents Screen
<img src="https://github.com/cookie-information/android-ui-sdk/assets/19759748/dfde5431-4068-4ecc-a23c-42441ff020e9" width="300">


## Standard flows
## Presenting the privacy pop-up
To show the Privacy Pop Up screen, whatever the state is use `showPrivacyPopUp` (typically used in settings to
allow for modification of the consent)
or to show the Privacy Pop Up screen in case the user has not consented to the latest version use `showPrivacyPopUpIfNeeded` (typically used at startup to
present the privacy screen conditionally. See more below) method:


```kotlin
   ConsentsUISDK.showPrivacyPopup(callingActivity).collect {
       it.fold(
           onSuccess = {
               it.forEachIndexed { index, consentItem ->
                   Log.d("TAG", "onCreate: ${consentItem.title}  ${consentItem.accepted}")
               }
           },
           onFailure = { Log.d("TAG", it.message ?: "no message") }
       )
   }
```


The above function takes an optional completion block argument that should be used to react to the
users consent and start or block various third-party SDKs.


## Presenting the privacy pop-up conditionally


The `showPrivacyPopUpIfNeeded` method is typically used to present the popup after app start (or at
a point the developer deems appropriate).
The method checks if a valid consent is already saved locally on the device and also checks if there
are any updates on the Cookie Information server. In case there is no consent saved or the consent
version is different from the one available on the server, the popup will be presented, otherwise
only the completion closure is called. Using the `ignoreVersionChanges` parameter allows the
developer to turn off the version checking mechanism and ignore consent version changes coming from
the server.


```kotlin
ConsentsUISDK.showPrivacyPopupIfNeeded(callingActivity).collect {
       it.fold(
           onSuccess = {
               it.forEachIndexed { index, consentItem ->
                   Log.d("TAG", "onCreate: ${consentItem.title}  ${consentItem.accepted}")
               }
           },
           onFailure = {
               Log.d("TAG", it.message ?: "no message")
           }


       )
   }
```


### Tracking the changes of the accepted consents


```kotlin
ConsentsUISDK.getSavedItemsAsFlow().collect {
   //collects emitted Result<List<ConsentItem>> objects, so can track the changes
}
```
### Managing multiple users


This SDK supports multiple users, if you would like to identify you users, and ask each user to consents, just pass the userId into the methods, `showPrivacyPopUp` etc.
The argument userId is by default null which represents an anonymous user.
Please note, user management is applicable per device, and not across multiple devices, so each user will need to interact on each device used.


Example for displaying for an anonymous user:
```kotlin
ConsentsUISDK.showPrivacyPopUpIfNeeded(callingActivity, null).collect { consents ->
   //returns the result for an anonymous user
}
```
Example for displaying for a user identified as "user_email":
```kotlin
ConsentsUISDK.showPrivacyPopUpIfNeeded(callingActivity, "user_email").collect { consents ->
   //returns the result for a user identified as user_email.
}
```


### Handling errors


Both the `showPrivacyPopUp` and `showPrivacyPopUpIfNeeded`, return Flow<Result<List<ConsentItem>>>,
so whenever any item is collected,
you as a developer should handle the state according to your needs, see Kotlin.Result class for further
information on the Result class [here](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/).


```kotlin
ConsentsUISDK.showPrivacyPopUpIfNeeded(callingActivity, userId).collect { consents ->
   if (consent.isSuccessful()) {
       consents.forEach { consent ->
           // handle user defined consent items such as age consent
           print("Consent given for: ${consentItem.title}  ${consentItem.accepted}")
       }
   } else { //failure.
       //handle error
   }
}
```
Error logs are also available. If the popup does not appear, check the logs and verify that data has been provided from the panel.


### Optional settings
### Theming & Styling


The UI theme and font can be customized in the SDKs initializer, make sure you have [Jetpack Compose](https://developer.android.com/develop/ui/compose/documentation) dependencies in you project:
```kotlin
   ConsentsUISDK.init(
       clientID = "<CLIENT_ID>",
       clientSecret = "<CLIENT_SECRET>",
       solutionId = "<SOLUTION ID>",
       languageCode = "FR",
       typography = defaultTypography.copy(bodyMedium = TextStyle.Default),
       customDarkColorScheme = darkColorScheme(
           primary = Color.Yellow,
           secondary = Color.Green,
           tertiary = Color.Black
       ),
       customLightColorScheme = lightColorScheme(
           primary = Color.Red,
           secondary = Color.Yellow,
           tertiary = Pink40
       ),
       context = this@MainActivity
)
```


Consent solution description and consent item texts can leverage HTML tags for basic text styling.
Supported tags include:


- `<b>` for bolding text
- `<i>` and `<em>` for emphasizing text
- `<br>` for line breaking
- `<ul>` and `<li>` for creating lists
- `<a href>` for embedding links


## Displaying the device identifier


<img src="https://github.com/cookie-information/android-ui-sdk/assets/19759748/806d65cf-54e4-412a-9056-8e9b1f1df2ba" width="300">


All consents sent to the Cookie Information servers are identified by a unique device identifier
that is generated randomly after opening the privacy popup for the first time. This ID is necessary
for Cookie Information to retrieve consents saved by the end user.


During normal operation the identifier is not required, however in case the end user wants to access
their saved consents, it is only possible if they provide the above mentioned identifier. When using
the default user interface, the device identifier can be located at the bottom of the privacy policy
page (after tapping "read more"). It can be copied to the clipboard by tapping the text.


## Java / XML support
The library is written in Kotlin using Compose, so you need to enable Kotlin and Compose support. However, you can continue using XMLs in your app. Refer to the exampleXML in the repository for guidance.


You can also keep some of your code in Java. Therefore, consider updating at least the parts related to this library to Kotlin.


## Migrate from version 2.x to 3.x
Remove all code from version 2.x an follow this guide to integrate version 3.x. Use the same clientID, clientSecret, clientSecret.


If you need any additional features, please do not hesitate to reach out to our support team.


## Custom view using core sdk
[![Maven Central](https://img.shields.io/maven-central/v/com.cookieinformation/mobileconsents.svg?label=latest%20core%20release)](https://search.maven.org/artifact/com.cookieinformation/core)


The core SDK does not include a UI component but offers a convenient way for you to implement your own user interface.
## Installation
Mobile Consents core SDK is available through Gradle, by adding te dependency to your project.


Kotlin DSL


```kotlin
implementation("com.cookieinformation:core:<latest_core_release>")
```


Groovy
```kotlin
implementation 'com.cookieinformation:core:<latest_core_release>'
```
## Initializing
```kotlin
   ConsentSDK.init(
       clientID = "<CLIENT_ID>",
       clientSecret = "<CLIENT_SECRET>",
       solutionId = "<SOLUTION ID>",
       languageCode = "en"
   )
```
## Available methods
### CacheLatestConsentSolution
fetches the available consent solution on the server and saves it to the local database.
```kotlin
suspend fun cacheLatestConsentSolution(): Result<Unit>
```
### SaveConsents
Saves consent items with acceptance response in the local database and on the server for the provided user id.
```kotlin
suspend fun saveConsents(
   userId: String? = null,
   consentItems: List<ConsentItemOption>
): Result<List<ConsentItem>>
```
### DeleteUserData
Wipes all the data associated with a user for the provided user id.
```kotlin
fun deleteUserData(userId: String?): Result<Unit>
```
### GetLatestSavedUserConsents
Gives a list of the latest localized consent items from the local database
```kotlin
getLatestSavedUserConsents(userId: String? = null): Result<List<ConsentItem>>
```
### HasUserRespondedToLatestSavedSolution
Checks if the specified user has responded to the latest version of consent in the local database
```kotlin
fun hasUserRespondedToLatestSavedSolution(userId: String? = null): Result<Boolean>
```
### ResendAllFailedSaveConsentsRequests
Resends all the locally saved failed post consents requests to the consent solution server
```kotlin
suspend fun resendAllFailedSaveConsentsRequests()
```
## Implementation
Refer to the MobileConsentsSDK implementation for a more detailed example. For additional customization options within MobileConsentsSDK, please contact our support team.
