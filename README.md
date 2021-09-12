Sary Android App Catalog Screen
===============================
Design and implementation of Sary app catalog screen.

##### Screenshots
<img src="screenshots/store.gif" />

<br/>

### Implementation
- - - - - - - - - - - - - - - - - - - -
The implementation is written in __Kotlin__. The app uses two APIs [Banner API](https://staging.sary.co/api/v2.5.1/baskets/76097/banners) & [Catalog API](https://staging.sary.co/api/v2.5.1/baskets/76097/catalog/) to render the store screen. The design of the store screen has small differences from the real app's screen. The Authorization token is embedded in the source code only for presentation purposes. The catalog section `ui_type` (*type*: __slider__) is implemented as a horizontal scrollable list.

##### Architecture
This sample app uses MVVM Architecture, a well known architecture for Android, the app's components are less dependents and easier to test.

##### Libraries Used
* ViewModel & Livedata.
* ViewBinding.
* Hilt for dependency injection.
* [Retrofit](http://square.github.io/retrofit) for REST api communication.
* [Gson](https://github.com/google/gson) for parsing JSON.
* Glide for image fetching.

##### Resources
1. APK link: <https://drive.google.com/file/d/1OfxD55_M5-TEDfm3eZGQggL1SpoWnAK5/view?usp=sharing>.
2. Video link: <https://drive.google.com/file/d/16U9SsZOCdQvnDgQQpCpr0LeDTIUr8jnp/view?usp=sharing>.

<br/>

### Jetpack Compose Implementation
- - - - - - - - - - - - - - - - - - - -
The project has a different implementation using *Jetpack Compose* implemented in a different branch [jetpack-compose-implementation](https://github.com/mahmoud-adel-sayed/sary-android-catalog-screen/tree/jetpack-compose-implementation), to run it you need to use [Android Studio Arctic Fox or Bumblebee Canary](https://developer.android.com/studio).

##### Implementation Details
* The project uses Kotlin instead of Groovy in the Gradle Scripts.
* The `minSdkVersion` is __21__ instead of 19.
* The additional libraries used are __Accompanist (Insets & Coil)__, __Kotlin Coroutines__, and __the Navigation Component__.
* The app theme relays on the Material Theme to model Colors, Shapes, and Typography.
* __Montserrat Typeface__ is used to show case how to use custom fonts in the app.
* The app uses `AndroidView` Composable Function to render the View-Based `BannerView`.
* `LocalElevations` & `LocalImages` are used to associate different Elevations & Images with the app themes.
* *ViewBinding* is used to bind banner slides.

##### Other Considerations
* We could add an Interceptor to `Coil` and request sized images from the server.
* We could use __Coroutines Suspending Functions__ in the data layer.
* We could use __Compose ConstraintLayout__ to construct complex layouts.
