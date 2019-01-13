# NestedScrollingHistory

<img src="./resource/title.jpg" width="90%"/>

What's NestedScrolling?

What's different between `NestedScrollingChild` / `NestedScrollingChild2` / `NestedScrollingChild3` ?

Why newest(`androidx.core 1.1.0-alpha01`) `NestedScrollView` sometimes out of control?

<img src="./resource/bug3_1.gif" width="20%"/>

You can see all details [here]() ([中文]()).

You also can get better `NestedScrollView4` instead of `NestedScrollView` bellow.

---

`NestedScrollView4` use like

<img src="./resource/bug3_2.gif" width="20%"/>

For Gradle

Step 1. Add the JitPack repository in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency

```groovy
dependencies {
        implementation 'com.github.RubiTree:NestedScrollingHistory:v1.0.0'
}
```