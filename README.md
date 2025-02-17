# Nov Open Reader

A simple application to read data from NFC insulin pens from Novo Nordisk : NovoPen 6 and NovoPen Echo Plus.

The application is developed using Kotlin and Compose Multiplatform, with an Android version and a
desktop version. For now, only the Android version is capable of reading pen using NFC. The desktop
version is used only for testing and can import plain raw data instead of NFC reading.

## NvpLib

Communication with the pen is done using a small library that can be used in other Kotlin projects.

Two components are used :
- *nvplib-core*: contains the data structure and the protocol implementation to send requests and read
  results. This library is plain JVM Kotlin and doesn't require any dependency.
- *nvplib-nfc*: A wrapper for the core library that add support for Android NFC API.

The library is available on Maven Central. To import it, use the following dependencies :
```kotlin
repositories {
  mavenCentral()
}

dependencies {
  implementation("net.cacheux.nvplib:nvplib-core:0.1.1")
  implementation("net.cacheux.nvplib:nvplib-nfc:0.1.1")
}
```

Thanks to [JamOrHam](https://github.com/jamorham) for providing 
[a comprehensive implementation](https://github.com/NightscoutFoundation/xDrip/tree/master/app/src/main/java/com/eveningoutpost/dexdrip/insulin/opennov)
of the NovoPen protocol for xDrip+ that has been very helpful in order to develop NvpLib.

## Disclaimer

This application is not developed or endorsed by Novo Nordisk.

This application is intended for informational purposes only and should not be used as a substitute
for professional medical advice, diagnosis, or treatment. Always seek the advice of your physician
or other qualified health provider with any questions you may have regarding the use of insulin pens
or any other medical condition.

[<img src="https://f-droid.org/badge/get-it-on.png"
    alt="Get it on F-Droid"
    height="80">](https://f-droid.org/packages/net.cacheux.nvp.app)
