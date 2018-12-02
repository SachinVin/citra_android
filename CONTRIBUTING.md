# SachinVin/citra_android contribution guidelines

First of all, thanks for reading this document and, more importantly, contributing to this project!

Here's a few things you should know before you open an issue or a pull request.

## Github basics

If you are familiar with how open-source repo maintenance works, you can skip this section, otherwise continue onward.

### The issue tracker

citra_android's issue tracker is no different from any other on Github and should be treated as one. This means that it SHOULD be used for:

- Letting us know when you stumble upon a bug that is not yet documented (check the [known issues](https://github.com/SachinVin/citra_android/wiki/Known-issues) wiki page for what already is.

- Suggesting new features for the project. If you want to suggest an improvement and know how it should be done, you can open a new issue with thorough description included. If someone likes your idea and knows how to code, they can implement it into a future release. Please make sure you know something on the topic of your issue. Two-lines long issues saying "when new update i want savestates in update also optimize more also work on mali gpu's's also optimize" it will most possibly be ignored. Please be mature.

It should NOT be used for:

- Chatting or asking irrelevant questions (check *What not to ask for* for more).

- Social-networky stuff. No memes, no "Check out how it runs" videos, no screenshots "Just completed <game>, so proud :)". It's nice, but it does not belong here.
  
- Piracy. See *Piracy clause* for more.

- Self-advertising.

- Bloating/spamming/shitposting/trolling.

### Pull requests

Again, pull requests work pretty much the same Github-wide. They are a convenient function for developers who want to submit their own code into someone's project as a contribution. Use it for:

- Proposing a merge from your own fork of *citra_android*.

- Editing docs like README or this one (though that's essentially the same as above).

Other than the rules listed in the **Issues** section, it should NOT be used for:

- Asking about anything. Relevant questions should go to **Issues**.

- Anything other than submitting code. It doesn't get any more complicated.

## Things to know before opening an issue

- Check the list of [Known Issues](https://github.com/SachinVin/citra_android/wiki/Known-issues) and don't make unnecessary duplicates.

- Read the README and see if your device is compatible, if not, you're wasting your time opening an issue about Citra crashing. Feel free to open something device-independent though. If you need help checking the compatibility of your device, scroll down to *How to know if my device is compatible*.

## Things to do before submitting a Pull Request

- Review the indentation of your code.

- Test your branch on a device to see if it does not crash somewhere.

- Be sure that your code is compilable.

- If you added any new classes in Java, edit your .gitignore not to exclude app/

## Appendix

### What not to ask for.

- ETA on new updates. If you want bleeding-edge builds, get them from [Appveyor](https://ci.appveyor.com/project/SachinVin/citra-android).

- Help with an unsupported device.

- ROMs. See *Piracy clause*.

- Anything not in English.

### How to know if my device is compatible

- Visit [GSMArena](https://www.gsmarena.com) and search for your device.

- Write down the values in the CPU and GPU rows (you don't need values like Quad-core or 4.20 GHz, just the stuff after that, f.e. Cortex-A69 for CPU identification, for GPU, you'll need everything present in the row, f.e. Adreno 666).

- Search Google for the CPU model number. Find a page that has information about architecture (Wikipedia usually does, unbelievably, the ARM developer page does not). If your architecture is arm64 or armv8, your CPU is compatible.

- Again, Google your GPU model. Wikipedia is your friend again. Under "Supported APIs", search for "OpenGL ES". If there's a value of 3.2, you can use the hardware renderer, otherwise you'll have to switch to software.

### Piracy clause

Emulation is a long-living tech gimmick that started in the last century and has been since hated by about every console publisher. Why? Because many, if not most of the people using emulators are not getting their games legally, some of them do not even own the consoles. We understand that some people either can't afford the hardware/software to play legally or don't want to support bad companies (which console manufacturers nowadays undoubtedly are), but to keep our emulator happy and not-taken-down, we have (and want) to stay legal. If you are a video game pirate (shame on you by the way), we do not care as long as you don't ask us for support with pirated copies of games or discuss them on the issue tracker. That way, we can continue doing what we love without getting sued. If you are pirating games, because you don't own the console, there's actually a legal way to acquire the games - buy them physically and ask a friend to dump them for you.
