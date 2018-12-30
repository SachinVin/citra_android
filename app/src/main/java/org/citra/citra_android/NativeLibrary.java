/*
 * Copyright 2013 Dolphin Emulator Project
 * Licensed under GPLv2+
 * Refer to the license.txt file included.
 */

package org.citra.citra_android;

import android.app.AlertDialog;
import android.view.Surface;

import org.citra.citra_android.activities.EmulationActivity;
import org.citra.citra_android.utils.Log;

import java.lang.ref.WeakReference;

/**
 * Class which contains methods that interact
 * with the native side of the Dolphin code.
 */
public final class NativeLibrary
{
  public static WeakReference<EmulationActivity> sEmulationActivity = new WeakReference<>(null);

  /**
   * Button type for use in onTouchEvent
   */
  public static final class ButtonType
  {
    public static final int N3DS_BUTTON_A = 700;
    public static final int N3DS_BUTTON_B = 701;
    public static final int N3DS_BUTTON_X = 702;
    public static final int N3DS_BUTTON_Y = 703;
    public static final int N3DS_BUTTON_START = 704;
    public static final int N3DS_BUTTON_SELECT = 705;
    public static final int N3DS_BUTTON_HOME = 706;
    public static final int N3DS_BUTTON_ZL = 707;
    public static final int N3DS_BUTTON_ZR = 708;
    public static final int N3DS_DPAD_UP = 709;
    public static final int N3DS_DPAD_DOWN = 710;
    public static final int N3DS_DPAD_LEFT = 711;
    public static final int N3DS_DPAD_RIGHT = 712;
    public static final int N3DS_STICK_LEFT = 713;
    public static final int N3DS_STICK_LEFT_UP = 714;
    public static final int N3DS_STICK_LEFT_DOWN = 715;
    public static final int N3DS_STICK_LEFT_LEFT = 716;
    public static final int N3DS_STICK_LEFT_RIGHT = 717;
    public static final int N3DS_STICK_C = 718;
    public static final int N3DS_STICK_C_UP = 719;
    public static final int N3DS_STICK_C_DOWN = 720;
    public static final int N3DS_STICK_C_LEFT = 771;
    public static final int N3DS_STICK_C_RIGHT = 772;
    public static final int N3DS_TRIGGER_L = 773;
    public static final int N3DS_TRIGGER_R = 774;
  }

  /**
   * Button states
   */
  public static final class ButtonState
  {
    public static final int RELEASED = 0;
    public static final int PRESSED = 1;
  }

  private NativeLibrary()
  {
    // Disallows instantiation.
  }

  /**
   * Default touchscreen device
   */
  public static final String TouchScreenDevice = "Touchscreen";

  /**
   * Handles button press events for a gamepad.
   *
   * @param Device The input descriptor of the gamepad.
   * @param Button Key code identifying which button was pressed.
   * @param Action Mask identifying which action is happening (button pressed down, or button released).
   * @return If we handled the button press.
   */
  public static native boolean onGamePadEvent(String Device, int Button, int Action);

  /**
   * Handles gamepad movement events.
   *
   * @param Device The device ID of the gamepad.
   * @param Axis   The axis ID
   * @param x_axis The value of the x-axis represented by the given ID.
   * @param y_axis The value of the y-axis represented by the given ID
   */
  public static native boolean onGamePadMoveEvent(String Device, int Axis, float x_axis, float y_axis);

  /**
   * Handles gamepad movement events.
   *
   * @param Device The device ID of the gamepad.
   * @param Axis_id   The axis ID
   * @param axis_val The value of the axis represented by the given ID.
   */
  public static native boolean onGamePadAxisEvent(String Device, int Axis_id, float axis_val);

  /**
   * Handles touch events.
   *
   * @param x_axis  The value of the x-axis.
   * @param y_axis  The value of the y-axis
   * @param pressed To identify if the touch held down or released.
   */
  public static native void onTouchEvent(float x_axis, float y_axis, boolean pressed);

  /**
   * Handles touch movement.
   *
   * @param x_axis The value of the instantaneous x-axis.
   * @param y_axis The value of the instantaneous y-axis.
   */
  public static native void onTouchMoved(float x_axis, float y_axis);

  public static native String GetUserSetting(String gameID, String Section, String Key);

  public static native void SetUserSetting(String gameID, String Section, String Key, String Value);

  public static native void InitGameIni(String gameID);

  /**
   * Gets a value from a key in the given ini-based config file.
   *
   * @param configFile The ini-based config file to get the value from.
   * @param Section    The section key that the actual key is in.
   * @param Key        The key to get the value from.
   * @param Default    The value to return in the event the given key doesn't exist.
   * @return the value stored at the key, or a default value if it doesn't exist.
   */
  public static native String GetConfig(String configFile, String Section, String Key,
          String Default);

  /**
   * Sets a value to a key in the given ini config file.
   *
   * @param configFile The ini-based config file to add the value to.
   * @param Section    The section key for the ini key
   * @param Key        The actual ini key to set.
   * @param Value      The string to set the ini key to.
   */
  public static native void SetConfig(String configFile, String Section, String Key, String Value);

  /**
   * Gets the embedded banner within the given ISO/ROM.
   *
   * @param filename the file path to the ISO/ROM.
   * @return an integer array containing the color data for the banner.
   */
  public static native int[] GetBanner(String filename);

  /**
   * Gets the embedded title of the given ISO/ROM.
   *
   * @param filename The file path to the ISO/ROM.
   * @return the embedded title of the ISO/ROM.
   */
  public static native String GetTitle(String filename);

  public static native String GetDescription(String filename);

  public static native String GetGameId(String filename);

  public static native int GetCountry(String filename);

  public static native String GetCompany(String filename);

  public static native long GetFilesize(String filename);

  public static native int GetPlatform(String filename);

  /**
   * Gets the Dolphin version string.
   *
   * @return the Dolphin version string.
   */
  public static native String GetVersionString();

  public static native String GetGitRevision();

  /**
   * Saves a screen capture of the game
   */
  public static native void SaveScreenShot();

  /**
   * Saves a game state to the slot number.
   *
   * @param slot The slot location to save state to.
   * @param wait If false, returns as early as possible.
   *             If true, returns once the savestate has been written to disk.
   */
  public static native void SaveState(int slot, boolean wait);

  /**
   * Saves a game state to the specified path.
   *
   * @param path The path to save state to.
   * @param wait If false, returns as early as possible.
   *             If true, returns once the savestate has been written to disk.
   */
  public static native void SaveStateAs(String path, boolean wait);

  /**
   * Loads a game state from the slot number.
   *
   * @param slot The slot location to load state from.
   */
  public static native void LoadState(int slot);

  /**
   * Loads a game state from the specified path.
   *
   * @param path The path to load state from.
   */
  public static native void LoadStateAs(String path);

  /**
   * Sets the current working user directory
   * If not set, it auto-detects a location
   */
  public static native void SetUserDirectory(String directory);

  /**
   * Returns the current working user directory
   */
  public static native String GetUserDirectory();

  // Create the config.ini file.
  public static native void CreateConfigFile();

  public static native int DefaultCPUCore();

  /**
   * Begins emulation.
   */
  public static native void Run(String path);

  /**
   * Begins emulation from the specified savestate.
   */
  public static native void Run(String path, String savestatePath, boolean deleteSavestate);

  public static native void ChangeDisc(String path);

  // Surface Handling
  public static native void SurfaceChanged(Surface surf);

  public static native void SurfaceDestroyed();

  /**
   * Unpauses emulation from a paused state.
   */
  public static native void UnPauseEmulation();

  /**
   * Pauses emulation.
   */
  public static native void PauseEmulation();

  /**
   * Stops emulation.
   */
  public static native void StopEmulation();

  /**
   * Returns true if emulation is running (or is paused).
   */
  public static native boolean IsRunning();

  /**
   * Enables or disables CPU block profiling
   *
   * @param enable
   */
  public static native void SetProfiling(boolean enable);

  /**
   * Writes out the block profile results
   */
  public static native void WriteProfileResults();

  /**
   * Native EGL functions not exposed by Java bindings
   **/
  public static native void eglBindAPI(int api);

  /**
   * Provides a way to refresh the connections on Wiimotes
   */
  public static native void RefreshWiimotes();

  /**
   * Returns the performance stats for the current game
   **/
  public static native double[] GetPerfStats();

  /**
   * The methods C++ uses to find references to Java classes and methods
   * are really expensive. Rather than calling them every time we want to
   * run them, do it once when we load the native library.
   */
  private static native void CacheClassesAndMethods();

  static
  {
    try
    {
      System.loadLibrary("main");
    }
    catch (UnsatisfiedLinkError ex)
    {
      Log.error("[NativeLibrary] " + ex.toString());
    }

    CacheClassesAndMethods();
  }

  private static boolean alertResult = false;

  public static boolean displayAlertMsg(final String caption, final String text,
          final boolean yesNo)
  {
    Log.error("[NativeLibrary] Alert: " + text);
    final EmulationActivity emulationActivity = sEmulationActivity.get();
    boolean result = false;
    if (emulationActivity == null)
    {
      Log.warning("[NativeLibrary] EmulationActivity is null, can't do panic alert.");
    }
    else
    {
      // Create object used for waiting.
      final Object lock = new Object();
      AlertDialog.Builder builder = new AlertDialog.Builder(emulationActivity)
              .setTitle(caption)
              .setMessage(text);

      // If not yes/no dialog just have one button that dismisses modal,
      // otherwise have a yes and no button that sets alertResult accordingly.
      if (!yesNo)
      {
        builder
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, whichButton) ->
                {
                  dialog.dismiss();
                  synchronized (lock)
                  {
                    lock.notify();
                  }
                });
      }
      else
      {
        alertResult = false;

        builder
                .setPositiveButton("Yes", (dialog, whichButton) ->
                {
                  alertResult = true;
                  dialog.dismiss();
                  synchronized (lock)
                  {
                    lock.notify();
                  }
                })
                .setNegativeButton("No", (dialog, whichButton) ->
                {
                  alertResult = false;
                  dialog.dismiss();
                  synchronized (lock)
                  {
                    lock.notify();
                  }
                });
      }

      // Show the AlertDialog on the main thread.
      emulationActivity.runOnUiThread(() -> builder.show());

      // Wait for the lock to notify that it is complete.
      synchronized (lock)
      {
        try
        {
          lock.wait();
        }
        catch (Exception e)
        {
        }
      }

      if (yesNo)
        result = alertResult;
    }
    return result;
  }

  public static void setEmulationActivity(EmulationActivity emulationActivity)
  {
    Log.verbose("[NativeLibrary] Registering EmulationActivity.");
    sEmulationActivity = new WeakReference<>(emulationActivity);
  }

  public static void clearEmulationActivity()
  {
    Log.verbose("[NativeLibrary] Unregistering EmulationActivity.");

    sEmulationActivity.clear();
  }
}
