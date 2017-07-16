<img src="/screenshots/header.gif" width="400">
<img src="/screenshots/samples.gif" align="right" width="120">

[![Release](https://jitpack.io/v/tarek360/RichPath.svg)](https://jitpack.io/#tarek360/RichPath) ![API](https://img.shields.io/badge/API-11%2B-brightgreen.svg?style=flat) [![Twitter URL](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=https://github.com/tarek360/RichPath)

💪 Rich Android Path.     🤡 Draw as you want.    🎉 Animate much as you can.

### Download sample app:
[![](http://s25.postimg.org/l7tmfhxy7/get_it_on_google_play.jpg)](https://play.google.com/store/apps/details?id=com.pathanimator.sample)

### Features

- **Full Animation Control on Paths and VectorDrawables:**
Animate any attribute in a specific path in the VectorDrawable

`fillColor`, `strokeColor`, `strokeAlpha`, `fillAlpha`, `size`, `width`, `height`, `scale`, `scaleX`, `scaleY`, `rotation`, `translationX`, `translationY`, `trimPathStart`, `trimPathEnd`, `trimPathOffset`.

- **Path morphing on API +11 :** 💪

<img src="/screenshots/animal_path_morphing.gif" width="250">

```Java
RichPathAnimator.animate(richPath)
       .pathData(pathData1, pathData2, ...)
       .start();
```

## Just 3 Steps to Animate any path.

#### 1. In your layout.
```xml
    <com.richpath.RichPathView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:vector="@drawable/vector_drawable" />
```

#### 2. Find your richPath.
 ```java
RichPath richPath = richPathView.findRichPathByName("path_name");

```

#### 3. Use the RichPathAnimator to animate your richPath.
 ```java
RichPathAnimator.animate(richPath)
        .trimPathEnd(value1, value2, ...)
        .fillColor(value1, value2, ...)
        .start();
```

## Example

#### notification icon vector drawable
<img src="/screenshots/ic_notifications.png" align="right" width="120">

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="32dp"
    android:height="32dp"
    android:viewportHeight="32.0"
    android:viewportWidth="32.0">

    <group
        android:pivotX="16"
        android:pivotY="6.25">
        <path
            android:name="top"
            android:fillColor="#FFF7F7F7"
            android:pathData="M22,19.8v-5c0-3.1-1.6-5.6-4.5-6.3V7.8c0-0.8-0.7-1.5-1.5-1.5s-1.5,0.7-1.5,1.5v0.7c-2.9,0.7-4.5,3.2-4.5,6.3v5l-2,2v1h16v-1L22,19.8z" />

        <path
            android:name="bottom"
            android:fillColor="#FFF7F7F7"
            android:pathData="M16,25.8c1.1,0,2-0.9,2-2h-4C14,24.9,14.9,25.8,16,25.8z" />
    </group>
</vector>
</vector>
```

#### XML
```xml
    <com.richpath.RichPathView
        android:id="@+id/ic_notifications"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:vector="@drawable/ic_notifications" />
```

#### Java
<img src="/screenshots/ic_notifications.gif" align="right" width="120">

```java
RichPath top = notificationsRichPathView.findRichPathByName("top");
RichPath bottom = notificationsRichPathView.findRichPathByName("bottom");

RichPathAnimator.animate(top)
        .interpolator(new DecelerateInterpolator())
        .rotation(0, 20, -20, 10, -10, 5, -5, 2, -2, 0)
        .duration(4000)
        .andAnimate(bottom)
        .interpolator(new DecelerateInterpolator())
        .rotation(0, 10, -10, 5, -5, 2, -2, 0)
        .startDelay(50)
        .duration(4000)
        .start();
```


### Installation

Add the following dependency to your module `build.gradle` file:
```gradle
dependencies {
	...
	compile 'com.github.tarek360.RichPath:animator:0.0.5'
}
```

Add this to your root `build.gradle` file (**not** your module `build.gradle` file) :
```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

### More Control by the RichPathAnimator

 - **Animate multiple paths sequentially or at the same time**
 ```java
RichPathAnimator
        .animate(richPath1, richPath2)
        .rotation(value1, value2, ...)

        //Animate the same path or another with different animated attributes.
        .andAnimate(richPath3)
        .scale(value1, value2, ...)

        //Animate after the end of the last animation.
        .thenAnimate(richPath4)
        .strokeColor(value1, value2, ...)

        // start your animation 🎉
        .start();
```


## Credits

- [florent37](https://github.com/florent37) He is the creator of [ViewAnimator](https://github.com/florent37/ViewAnimator) which gave me the idea of this project. Some core concepts and ideas were reused, but everything is written from scratch.
- [Android](https://android.com/) Some code is reused form the android source code and the VectorDrawableCompat support library.
- [Alex Lockwood](https://github.com/alexjlockwood) The paths of the morphing sample is extracted from the [Shape Shifter](https://github.com/alexjlockwood/ShapeShifter) demo.

## Developed By

* Ahmed Tarek
 * [tarek360.github.io](http://tarek360.github.io/)
 * [Twitter](https://twitter.com/a_tarek360)


## License

>Copyright 2017 Tarek360

>Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

>   http://www.apache.org/licenses/LICENSE-2.0

>Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
