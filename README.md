# University of Alacalá Indoor Positioning APP

### Overview 

This is a functional Android application built using Mapbox.

### Disclaimer

1. It contains [Mapbox SDK](https://www.mapbox.com/) and it has its own support Mapping applications.

2. This application is just another way to build an Indoor App by using different libraries to develop an application on the fly. Be aware this is not meant to be a blueprint, the correct way, even worse, the best way to build an IPS. On the contrary, it was built purely for fun and curiosity. Indeed, there are plenty of different ways to create a similar project using OSM, Google Maps, ARCGIS, among others. However, I hope this can be useful offering some guidance for those trying to build Indoor APPs.
 
### Requirements for running it locally

* **Virtualized environment**

	This project has been using the following versions of the tools aforementioned:

	- Android Studio 4.0.1

* **On your own**

	Currently, the project has the following dependencies:
  
  Add the following dependencies to build.gradle (Module:app)
	- 'com.google.android.material:material:1.2.1'
	- 'com.mapbox.mapboxsdk:mapbox-android-sdk:9.5.0'
	- 'com.mapbox.mapboxsdk:mapbox-sdk-turf:5.6.0'
	- 'com.mapbox.mapboxsdk:mapbox-android-plugin-markerview-v9:0.4.0'
	- 'com.android.support:design:27.1.1'
  - 'com.mapbox.mapboxsdk:mapbox-android-plugin-annotation-v9:0.9.0'
  
  ```javascript
  allprojects {
    repositories {
        maven {
            url 'https://api.mapbox.com/downloads/v2/releases/maven'
            authentication {
                basic(BasicAuthentication)
            }
            credentials {
                // Do not change the username below.
                // This should always be `mapbox` (not your username).
                username = 'mapbox'
                // Use the secret token you stored in gradle.properties as the password
                password = project.properties['MAPBOX_DOWNLOADS_TOKEN'] ?: ""
            }
        }
    } 

### Useful links

* [QGIS](https://www.qgis.org/en/site/)
* [Georeference QGIS](https://docs.qgis.org/2.8/en/docs/user_manual/plugins/plugins_georeferencer.html)
* [MAPBOX DSK for Android](https://docs.mapbox.com/android/maps/guides/)

