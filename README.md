Sary Android App Catalog Screen
===============================
Design and implementation of Sary app catalog screen. The app uses two APIs [Banner API](https://staging.sary.co/api/v2.5.1/baskets/76097/banners) & [Catalog API](https://staging.sary.co/api/v2.5.1/baskets/76097/catalog/) to render the store screen. The design of the store screen has small differences from the real app's screen. The catalog section `ui_type` (*type*: __slider__) is implemented as a horizontal scrollable list.

### Screenshots
<img src="screenshots/store.gif" />

<br/>

### Jetpack Compose Implementation
- - - - - - - - - - - - - - - - - - - -
This branch contains the *Jetpack Compose* implementation, to run it you need to use [Android Studio Arctic Fox or Bumblebee Canary](https://developer.android.com/studio).

##### Implementation Details
* The project uses Kotlin instead of Groovy in the Gradle Scripts.
* The `minSdkVersion` is __21__ instead of 19.
* The app theme relays on the Material Theme to model Colors, Shapes, and Typography.
* __Montserrat Typeface__ is used to show case how to use custom fonts in the app.
* The app uses `AndroidView` Composable Function to render the View-Based `BannerView`.
* `LocalElevations` & `LocalImages` are used to associate different Elevations & Images with the app themes.

##### Architecture
This sample app uses MVVM Architecture, a well known architecture for Android, the app's components are less dependents and easier to test.

##### Libraries Used
* ViewModel & Livedata.
* ViewBinding (used to bind views in the View-Based system).
* Hilt for dependency injection.
* [Retrofit](http://square.github.io/retrofit) for REST api communication.
* [Gson](https://github.com/google/gson) for parsing JSON.
* Glide (used to fetch images in the View-Based system).
* Accompanist (Insets & Coil).
* Kotlin Coroutines.
* Navigation Component.

##### Resources
1. APK link: <https://drive.google.com/file/d/13OAckgzlqVfZRccIzt17YplbrK6fsd4G/view?usp=sharing>.

##### Other Considerations
* We could add an Interceptor to `Coil` and request sized images from the server.
* We could use __Coroutines Suspending Functions__ in the data layer.
* We could use __Compose ConstraintLayout__ to construct complex layouts.

<br/>

### Default Implementation
- - - - - - - - - - - - - - - - - - - -
The default implementation exists in the [master](https://github.com/mahmoud-adel-sayed/sary-android-catalog-screen/tree/master) branch.

##### Resources
1. APK link: <https://drive.google.com/file/d/1OfxD55_M5-TEDfm3eZGQggL1SpoWnAK5/view?usp=sharing>.
2. Video link: <https://drive.google.com/file/d/16U9SsZOCdQvnDgQQpCpr0LeDTIUr8jnp/view?usp=sharing>.