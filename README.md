# Citra for Android
An (Unofficial) Android frontend for the Citra Emulator

This is an unofficial port for the [Citra emulator](https://github.com/citra-emu) based on the [Dolphin emulator's](https://github.com/dolphin-emu) Android frontend.
So, huge props to the guys over there.

Since this code is almost entirely a copy of the Dolphin frontend,there are a lot of useless segments in here that aren't being used ... or are useless.

**Download** apk files from [here](https://github.com/SachinVin/citra_android/releases)

### Building
You can follow [this guide](https://github.com/dolphin-emu/dolphin/blob/master/AndroidSetup.md) if haven't set up your Android environment.
Once that is done, follow [this](https://developer.android.com/studio/projects/add-native-code#vanilla_cmake) to setup and use Cmake 3.8(min. for Citra) or higher.

First, for the frontend, you can clone/download this repo.

You can find the Citra backend [here](https://github.com/SachinVin/citra) - either download a snapshot archive from GitHub or clone the repository.

Edit the CMakeLists path in the build.gradle to point to the location of the CMakeLists.txt of the Citra backend.

You're all set, now hit build.

### Device Requirements
* OS
  * Android (5.0 Lollipop or higher).
* Processor
  * A processor with support for either ARMv8 or x86-64; 32-bit processors (ARMv7, x86) are not supported by Citra.
* Graphics
  * A graphics processor that supports OpenGL ES 3.2 or higher.
    
### Known Issues
Since this is in very early stages of development a lot of things don't quite work properly yet:
* Colors in games (since GLES doesn't support BGR color ordering, the color channels may be inverted);
* Since this is based on an older version of Citra, bugs solved on newer versions of Citra may still be present.

