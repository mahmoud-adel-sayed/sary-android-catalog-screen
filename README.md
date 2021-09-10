# Sary Android App Catalog Screen
Design and implementation of Sary app Catalog screen.

### Screenshots
<img src="screenshots/store.gif" />

### Implementation
The implementation is written in __Kotlin__. The app uses two APIs [Banner API](https://staging.sary.co/api/v2.5.1/baskets/76097/banners) & [Catalog API](https://staging.sary.co/api/v2.5.1/baskets/76097/catalog/) to render the store screen. The design of the store screen has small differences from the real app's screen. The Authorization token is embedded in the source code only for presentation purposes. The catalog section `ui_type` (*type*: __slider__) is implemented as a horizontal scrollable list.

### Jetpack Compose Implementation
The project has a different implementation using *Jetpack Compose* implemented in a different branch [jetpack-compose-implementation](https://github.com/mahmoud-adel-sayed/sary-android-catalog-screen/tree/jetpack-compose-implementation), to run it you need to use [Android Studio Arctic Fox or Bumblebee Canary](https://developer.android.com/studio).

##### Compose Implementation Details
* The project uses Kotlin instead of Groovy in the Gradle Scripts.
* The minSdkVersion is 21 instead of 19.
* The additional libraries used are Accompanist Libs (Insets & Coil), Kotlin Coroutines, and the Navigation Component.
* The app theme relays on the Material Theme to model Colors, Shapes, and Typography.
* Montserrat typeface is used to show case how to use custom fonts in the app.
* The app uses `AndroidView` Composable function to render the View-Based `BannerView`.

### APK & Video Links
1. APK link: <https://drive.google.com/file/d/1OfxD55_M5-TEDfm3eZGQggL1SpoWnAK5/view?usp=sharing>.
2. Video link: <https://drive.google.com/file/d/16U9SsZOCdQvnDgQQpCpr0LeDTIUr8jnp/view?usp=sharing>.

### Architecture
This sample app uses MVVM Architecture, a well known architecture for Android, the app's components are less dependents and easier to test.

### Libraries Used
* ViewModel & Livedata.
* ViewBinding.
* Hilt for dependency injection.
* [Retrofit][retrofit] for REST api communication.
* [Gson][gson] for parsing JSON.
* Glide for image fetching.

[retrofit]: http://square.github.io/retrofit
[gson]: https://github.com/google/gson