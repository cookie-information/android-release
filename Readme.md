# android-ui-sdk

UI part of the new android sdk

[![Maven Central](https://img.shields.io/maven-central/v/com.cookieinformation/mobileconsents.svg?label=latest%20release)](https://search.maven.org/artifact/com.cookieinformation/mobileconsents)

# Mobile Consents SDK

## Installation

### Gradle Dependency

MobileConsentsSDK is available through Gradle, by adding te dependency to your project..
Kotlin DSL

```kotlin
implementation("com.cookieinformation:mobileconsents:<latest_release>")
```

# SDK setups

```kotlin

lifecycleScope.launch {
    val mobileConsentsSDK = ConsentsUISDK.init(
        context = context,
        clientID = "<CLIENT_ID>",
        clientSecret = "<CLIENT_SECRET>",
        solutionId = "<SOLUTION ID>",
        languageCode = "EN", //should be one of the codes that have been set up ont he platform
        typography = defaultTypography//you may want to copy and override some fields to make it blend into your app  
    )
}

```

# Using built-in mobile consents UI

SDK contains built-in screens for managing consents.
Please ensure you set the correct language code you expect the consents to use, and that has been
fully set on the platform.

## Consents Screen
<img src="https://github.com/cookie-information/android-ui-sdk/assets/19759748/dfde5431-4068-4ecc-a23c-42441ff020e9" width="300">

## Standard flows
## Presenting the privacy pop-up conditionally
To show the Privacy Pop Up screen, whatever the state is use `showPrivacyPopUp` (typically used in settings to
allow for modification of the consent) 
or to show the Privacy Pop Up screen in case the user has not consented to the latest version use `showPrivacyPopUpIfNeeded` (typically used at startup to
present the privacy screen conditionally. See more below) method:

```kotlin
ConsentsUISDK.showPrivacyPopUp(callingActivity, userId).collect { consents ->
    consents.forEach { consent ->
        // handle user defined consent items such as age consent
        print("Consent given for: ${consentItem.title}  ${consentItem.accepted}")
    }
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

### Tracking the changes of the accepted consents

```kotlin
ConsentsUISDK.getSavedItemsAsFlow().collect {
    //collects emitted Result<List<ConsentItem>> objects, so can track the changes
}
```
### Managing multiple users

This SDK supports multiple users, if you would like to identify you users, and ask each user to consents, just pass the userId into the methods, `showPrivacyPopUp` etc.
The argument userId is by default null which represents an anonymous user.
Please note, user management is applicable per device, and not accross multiple devices, so each user will need to interact on each device used.

```kotlin
ConsentsUISDK.showPrivacyPopUpIfNeeded(callingActivity, null).collect { consents ->
    //returns the result for an anonymous user
}
```

```kotlin
ConsentsUISDK.showPrivacyPopUpIfNeeded(callingActivity, "user_email").collect { consents ->
    //returns the result for a user identified as user_email.
}
```

### Handling errors

Both the `showPrivacyPopUp` and `showPrivacyPopUpIfNeeded`, return Flow<Result<List<ConsentItem>>>,
so whenever any item is collected,
you as a developer should handle the state according to your needs, see Kotlin.Result class for further
information on the Result class.

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

### optional settings
### Theming

The UI theme and font can be customized in the SDKs initializer :

```kotlin
lifecycleScope.launch {
    ConsentsUISDK.init(
        context = context,
        clientID = "<CLIENT_ID>",
        clientSecret = "<CLIENT_SECRET>",
        solutionId = "<SOLUTION ID>",
        languageCode = "EN", //should be one of the codes that have been set up ont he platform
        typography = defaultTypography//you may want to copy and override some fields to make it blend into your app  
    )
}
```

Consent solution description and consent item texts can leverage HTML tags for basic text styling.
Supported tags include:

- `<b>` for bolding text
- `<i>` and `<em>` for emphasizing text
- `<br>` for line breaking
- `<ul>` and `<li>` for creating lists
- `<a href>` for embeding links


## UI language

By default, Privacy Pop-up and Privacy Center use the language set for the SDK, at initializing.

## Displaying the device identifier

<img src="https://github.com/cookie-information/android-ui-sdk/assets/19759748/806d65cf-54e4-412a-9056-8e9b1f1df2ba" width="300">

All consents sent to the Cookie Information servers are identified by a unique device identifier
that is generated randomly after opening the privacy popup for the first time. This ID is necessary
for Cookie Information to retrieve consents saved by the end user.

During normal operation the identifier is not required, however in case the end user wants to access
their saved consents, it is only possible if they provide the above mentioned identifier. When using
the default user interface, the device identifier can be located at the bottom of the privacy policy
page (after tapping "read more"). It can be copied to the clipboard by tapping the text.

