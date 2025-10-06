# MyBookings

[![Android](https://img.shields.io/badge/platform-Android-brightgreen)]()
[![Language](https://img.shields.io/badge/language-Java-orange)]()
[![License](https://img.shields.io/badge/license-MIT-blue)]()

**MyBookings** is an m-commerce hotel booking Android app built with Java and Android Studio. It uses Firebase Firestore for backend data storage, integrates Google Maps & Places for location and search, uses PayHere for payments, generates booking QR codes with ZXing, and supports QR scanning with Google ML Kit. Real-time updates and push notifications are powered by Firebase Cloud Messaging (FCM).

---

## Table of Contents


---

## Features

- Search hotels by location (Google Places / Maps)  
- View hotel listings, details, images and amenities  
- Book rooms and manage bookings (Firestore)  
- Secure payments via **PayHere** integration  
- Generate booking **QR code** (ZXing) for quick check-in  
- Scan QR codes using **Google ML Kit** for check-in  
- Real-time updates (Firestore listeners)  
- Push notifications with **Firebase Cloud Messaging (FCM)**  
- Offline-safe reads (Firestore caching) and basic error handling

---


## Tech Stack

- Android (Java)  
- Android Studio  
- Firebase Firestore (database)  
- Firebase Auth (if used for sign-in) *(optional)*  
- Firebase Cloud Messaging (notifications)  
- Google Maps SDK for Android  
- Google Places API / Places SDK for Android  
- PayHere Android SDK (or Web integration)  
- ZXing (QR code generation)  
- Google ML Kit (barcode scanning / QR scanning)

---

## App Architecture (brief)

Typical layers:
- **UI / Activities & Fragments** — screens for listing, details, booking, profile  
- **ViewModels / Controllers** (simple Java classes) — UI logic  
- **Repository** — Firestore read/write + caching + payment calls  
- **Services** — background workers for notifications, FCM handlers  
- **Utilities** — QR generation (ZXing), QR scanning (ML Kit), network utils

---

## Prerequisites

- Android Studio (Arctic Fox or later recommended)  
- Java 8+ (project configured for Java)  
- Android SDK (matching project `compileSdkVersion`)  
- A Firebase project with Firestore & FCM enabled  
- Google Cloud API key with **Maps SDK for Android** & **Places API** enabled  
- PayHere merchant account and keys (sandbox & production)  
- (Optional) Device or emulator with Google Play services

---

## Installation & Setup

### Clone repo
```bash
git clone https://github.com/yourusername/MyBookings.git
cd MyBookings

````

### Firebase Implementation

- https://console.firebase.google.com create a firebase projects
- Google Analytics for your firebase project (Disable)
- Download the `google-services.json` file and add into your module(app-level) root directory.

- Ensure your `build.gradle.kts` (Project) includes:

```bash
plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false
}
```
- Ensure your `build.gradle.kts` (Module) includes:

```bash
plugins {
    id("com.google.gms.google-services")
}

dependencies {
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    implementation("com.google.firebase:firebase-firestore")    
}
````
Note: Update versions to the latest stable ones in your build.gradle

// Google Maps & Places
    implementation 'com.google.android.gms:play-services-maps:18.1.0'
    implementation 'com.google.android.libraries.places:places:3.1.0'

    // ZXing for QR generation
    implementation 'com.google.zxing:core:3.5.0'
    implementation 'com.journeyapps:zxing-android-embedded:4.3.0' // if used

    // ML Kit barcode scanning
    implementation 'com.google.mlkit:barcode-scanning:17.0.2'

    // PayHere (check their SDK version)
    // implementation '.lk.payhere:android:VERSION' or follow PayHere docs

    // Other common libs...

### Google APIs (Maps & Places)

- Get an API key from Google Cloud Console.
- Enable Maps SDK for Android and Places API for your API key.
- Add the API key to AndroidManifest.xml (or res/values/strings.xml and reference it):

 ````bash
  <meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="@string/google_maps_key" />
````
- Ensure your `build.gradle.kts` (Module) includes:
  ````bash
  dependencies {
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation ("com.google.android.gms:play-services-location:21.3.0")
    implementation ("com.google.android.libraries.places:places:2.5.0")
}

### PayHere configuration

- Follow PayHere documentation to integrate their Android SDK or web checkout.
- Add your merchant API keys (sandbox for testing) into a secure config — do not commit keys. Use a local keystore.properties or environment variable replacement.
- Example (do not commit real keys):

````bash
PAYHERE_MERCHANT_ID=XXXXXXXX
PAYHERE_SECRET=YYYYYYYY
````
- Load them at runtime from a secure, ignored file/build config.
  

### Configure ZXing & ML Kit (QR)

- ZXing: used for generating QR images.
- ML Kit: to scan barcodes/QR codes from camera input. Follow official docs for setup and camera permission handling.

- Ensure your `build.gradle.kts` (Module) includes:
  ````bash
  dependencies {
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.google.mlkit:barcode-scanning:17.2.0")
}

## Running the app

- Make sure `google-services.json` is in app/.
- Add your API keys (Maps, Places, PayHere) in local config (ignored by git).
- Run on a physical device or emulator with Google Play services.
- Use Firebase console and FCM to test push notifications.

## Testing
- Test bookings with PayHere sandbox credentials.
- Test QR generation: create a booking and view generated QR (ZXing).
- Test scanning: use ML Kit scanner activity to scan the booking QR and verify check-in flow.
- Test FCM: send test notifications from Firebase Console to verify handlers.

## 👩‍💻 Author
**Savindi Duleesha**  
 - 📧 savindiduleesga@gmail.com
 - 🌐 [Portfolio](https://savindi2003.github.io/my-portfolio/)

