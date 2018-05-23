# Citra for Android
An (Unofficial) Android fronend for the Citra Emulator

This is an uofficial port for the [Citra emulator](https://github.com/citra-emu) based on the [Dolphin emulator's](https://github.com/dolphin-emu) frontend.
So, huge props to the guys over there.

Since this code almost entirely a copy of the dolphin frontend,there are a lot of useless segments in here that aren't being used ... or are useless.

### Building
You can follow [this guide](https://github.com/dolphin-emu/dolphin/blob/master/AndroidSetup.md) if haven't set up your android environment.

First the frontend, you can clone/download this repo.

You can find the citra backend [here](https://github.com/SachinVin/citra/tree/c++14). Make sure you are on the "C++14" branch. and Clone/Download the repo

Edit the CMakeLists path in the build.gradle to point to the location of the CMakeLists.txt of the citra backend

You're all set, now hit build.

### Device Requirements
* OS
  * Android (5.0 Lollipop or higher).
* Processor
  * A processor with support for either ARMv8 or x86-64. x86 32bit is not supported by Citra
* Graphics
  * A graphics processor that supports OpenGL ES 3.2 or higher.
    
### Known Issues
Since this is a early development a most of the stuff dosen't work
* Controls
* In App Settings(you can edit the settings at [internal_storage]/citra-emu/config/config.ini)
* Colors in games (since GLES dosent support BGR color scheme, the color channels may be inverted)
* Since this is based on an older version of Citra, bugs solved on newer versions of citra may still be present

