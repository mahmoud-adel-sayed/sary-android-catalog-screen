# Sary Android App Catalog Screen
Design and implementation of Sary app (flagship) Catalog screen.

### Screenshots
<img src="screenshots/store.gif" />

### Implementation
The implementation is written in __Kotlin__. The app uses two APIs [Banner API](https://staging.sary.co/api/v2.5.1/baskets/76097/banners) & [Catalog API](https://staging.sary.co/api/v2.5.1/baskets/76097/catalog/) to render the store screen. The design of the store screen has small differences from the real app's screen (I do not have access to the Zeplin Design). The Authorization token is embedded in the source code only for presentation purposes. The catalog section `ui_type` (*type*: __slide__) is implemented as a horizontal scrollable list (the task does not describe or show the design).

### Implementation Optimizations
* we could replace the `NestedScrollView` with a parent `RecyclerView` that contains the banner viewType & the child `RecyclerView`s (one for each catalog section) ViewTypes to avoid the nested scrolling issues.

### APK & Video Links
1. APK link: <https://drive.google.com/file/d/1ESzi3oclEa1VBnqj-k9ONPBpi_0Rlzac/view?usp=sharing>.
2. Video link: <https://drive.google.com/file/d/16U9SsZOCdQvnDgQQpCpr0LeDTIUr8jnp/view?usp=sharing>.

### Architecture
This sample app uses MVVM Architecture, a well known architecture for Android, the app's components are less dependents and easier to test.

### Libraries Used
* ViewModel & Livedata.
* [Dagger 2][dagger2] for dependency injection.
* [Retrofit][retrofit] for REST api communication.
* [Gson][gson] for parsing JSON.
* [ButterKnife][butterKnife] for view binding.
* Glide for image fetching.

[dagger2]: https://google.github.io/dagger
[retrofit]: http://square.github.io/retrofit
[gson]: https://github.com/google/gson
[butterKnife]: https://github.com/JakeWharton/butterknife