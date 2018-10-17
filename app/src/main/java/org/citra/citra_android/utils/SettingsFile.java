package org.citra.citra_android.utils;

import android.support.annotation.NonNull;

import org.citra.citra_android.model.settings.FloatSetting;
import org.citra.citra_android.model.settings.IntSetting;
import org.citra.citra_android.model.settings.Setting;
import org.citra.citra_android.model.settings.SettingSection;
import org.citra.citra_android.model.settings.StringSetting;
import org.citra.citra_android.services.DirectoryInitializationService;
import org.citra.citra_android.ui.settings.SettingsActivityView;
import org.ini4j.Wini;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * A HashMap<String, SettingSection> that constructs a new SettingSection instead of returning null
 * when getting a key not already in the map
 */
final class SettingsSectionMap extends HashMap<String, SettingSection>
{
  @Override
  public SettingSection get(Object key)
  {
    if (!(key instanceof String))
    {
      return null;
    }

    String stringKey = (String) key;

    if (!super.containsKey(stringKey))
    {
      SettingSection section = new SettingSection(stringKey);
      super.put(stringKey, section);
      return section;
    }
    return super.get(key);
  }
}

/**
 * Contains static methods for interacting with .ini files in which settings are stored.
 */
public final class SettingsFile
{
  public static final int SETTINGS_DOLPHIN = 0;

  public static final String FILE_NAME_CONFIG = "config";

  public static final String SECTION_CONTROLS = "Controls";
  public static final String SECTION_CORE = "Core";
  public static final String SECTION_RENDERER = "Renderer";
  public static final String SECTION_LAYOUT = "Layout";
  public static final String SECTION_AUDIO = "Audio";
  public static final String SECTION_SYSTEM = "System";
  public static final String SECTION_CAMERA = "Camera";
  public static final String SECTION_MISC = "Miscellaneous";
  public static final String SECTION_DEBUGGING = "Debugging";
  public static final String SECTION_WEBSERVICE = "WebService";

  public static final String KEY_CPU_JIT = "use_cpu_jit";

  public static final String KEY_HW_RENDERER = "use_hw_renderer";
  public static final String KEY_HW_SHADER = "use_hw_shader";
  public static final String KEY_SHADERS_ACCURATE_MUL = "shaders_accurate_mul";
  public static final String KEY_SHADERS_ACCURATE_GS = "shaders_accurate_gs";
  public static final String KEY_USE_SHADER_JIT = "use_shader_jit";
  public static final String KEY_USE_VSYNC = "use_vsync";
  public static final String KEY_RESOLUTION_FACTOR = "resolution_factor";
  public static final String KEY_FRAME_LIMIT_ENABLED = "use_frame_limit";
  public static final String KEY_FRAME_LIMIT = "frame_limit";
  public static final String KEY_BACKGROUND_RED = "bg_red";
  public static final String KEY_BACKGROUND_BLUE = "bg_blue";
  public static final String KEY_BACKGROUND_GREEN = "bg_green";
  public static final String KEY_STEREOSCOPY = "toggle_3d";
  public static final String KEY_FACTOR_3D = "factor_3d";

  public static final String KEY_LAYOUT_OPTION = "layout_option";
  public static final String KEY_SWAP_SCREEN = "swap_screen";

  public static final String KEY_AUDIO_OUTPUT_ENGINE = "output_engine";
  public static final String KEY_ENABLE_AUDIO_STRETCHING = "enable_audio_stretching";
  public static final String KEY_VOLUME = "volume";

  public static final String KEY_USE_VIRTUAL_SD = "use_virtual_sd";

  public static final String KEY_IS_NEW_3DS = "is_new_3ds";
  public static final String KEY_REGION_VALUE = "region_value";
  public static final String KEY_INIT_CLOCK = "init_clock";
  public static final String KEY_INIT_TIME = "init_time";

  public static final String KEY_CAMERA_OUTER_RIGHT_NAME = "camera_outer_right_name";
  public static final String KEY_CAMERA_OUTER_RIGHT_CONFIG = "camera_outer_right_config";
  public static final String KEY_CAMERA_OUTER_RIGHT_FLIP = "camera_outer_right_flip";
  public static final String KEY_CAMERA_OUTER_LEFT_FLIP = "camera_outer_left_flip";
  public static final String KEY_CAMERA_INNER_NAME = "camera_inner_name";
  public static final String KEY_CAMERA_INNER_CONFIG = "camera_inner_config";
  public static final String KEY_CAMERA_INNER_FLIP = "camera_inner_flip";

  public static final String KEY_LOG_FILTER = "log_filter";

  public static final String KEY_GCPAD_TYPE = "SIDevice";

  public static final String KEY_GCBIND_A = "InputA_";
  public static final String KEY_GCBIND_B = "InputB_";
  public static final String KEY_GCBIND_X = "InputX_";
  public static final String KEY_GCBIND_Y = "InputY_";
  public static final String KEY_GCBIND_Z = "InputZ_";
  public static final String KEY_GCBIND_START = "InputStart_";
  public static final String KEY_GCBIND_CONTROL_UP = "MainUp_";
  public static final String KEY_GCBIND_CONTROL_DOWN = "MainDown_";
  public static final String KEY_GCBIND_CONTROL_LEFT = "MainLeft_";
  public static final String KEY_GCBIND_CONTROL_RIGHT = "MainRight_";
  public static final String KEY_GCBIND_C_UP = "CStickUp_";
  public static final String KEY_GCBIND_C_DOWN = "CStickDown_";
  public static final String KEY_GCBIND_C_LEFT = "CStickLeft_";
  public static final String KEY_GCBIND_C_RIGHT = "CStickRight_";
  public static final String KEY_GCBIND_TRIGGER_L = "InputL_";
  public static final String KEY_GCBIND_TRIGGER_R = "InputR_";
  public static final String KEY_GCBIND_DPAD_UP = "DPadUp_";
  public static final String KEY_GCBIND_DPAD_DOWN = "DPadDown_";
  public static final String KEY_GCBIND_DPAD_LEFT = "DPadLeft_";
  public static final String KEY_GCBIND_DPAD_RIGHT = "DPadRight_";

  public static final String KEY_GCADAPTER_RUMBLE = "AdapterRumble";
  public static final String KEY_GCADAPTER_BONGOS = "SimulateKonga";

  public static final String KEY_WIIMOTE_TYPE = "Source";
  public static final String KEY_WIIMOTE_EXTENSION = "Extension";

  public static final String KEY_WIIBIND_A = "WiimoteA_";
  public static final String KEY_WIIBIND_B = "WiimoteB_";
  public static final String KEY_WIIBIND_1 = "Wiimote1_";
  public static final String KEY_WIIBIND_2 = "Wiimote2_";
  public static final String KEY_WIIBIND_MINUS = "WiimoteMinus_";
  public static final String KEY_WIIBIND_PLUS = "WiimotePlus_";
  public static final String KEY_WIIBIND_HOME = "WiimoteHome_";
  public static final String KEY_WIIBIND_IR_UP = "IRUp_";
  public static final String KEY_WIIBIND_IR_DOWN = "IRDown_";
  public static final String KEY_WIIBIND_IR_LEFT = "IRLeft_";
  public static final String KEY_WIIBIND_IR_RIGHT = "IRRight_";
  public static final String KEY_WIIBIND_IR_FORWARD = "IRForward_";
  public static final String KEY_WIIBIND_IR_BACKWARD = "IRBackward_";
  public static final String KEY_WIIBIND_IR_HIDE = "IRHide_";
  public static final String KEY_WIIBIND_SWING_UP = "SwingUp_";
  public static final String KEY_WIIBIND_SWING_DOWN = "SwingDown_";
  public static final String KEY_WIIBIND_SWING_LEFT = "SwingLeft_";
  public static final String KEY_WIIBIND_SWING_RIGHT = "SwingRight_";
  public static final String KEY_WIIBIND_SWING_FORWARD = "SwingForward_";
  public static final String KEY_WIIBIND_SWING_BACKWARD = "SwingBackward_";
  public static final String KEY_WIIBIND_TILT_FORWARD = "TiltForward_";
  public static final String KEY_WIIBIND_TILT_BACKWARD = "TiltBackward_";
  public static final String KEY_WIIBIND_TILT_LEFT = "TiltLeft_";
  public static final String KEY_WIIBIND_TILT_RIGHT = "TiltRight_";
  public static final String KEY_WIIBIND_TILT_MODIFIER = "TiltModifier_";
  public static final String KEY_WIIBIND_SHAKE_X = "ShakeX_";
  public static final String KEY_WIIBIND_SHAKE_Y = "ShakeY_";
  public static final String KEY_WIIBIND_SHAKE_Z = "ShakeZ_";
  public static final String KEY_WIIBIND_DPAD_UP = "WiimoteUp_";
  public static final String KEY_WIIBIND_DPAD_DOWN = "WiimoteDown_";
  public static final String KEY_WIIBIND_DPAD_LEFT = "WiimoteLeft_";
  public static final String KEY_WIIBIND_DPAD_RIGHT = "WiimoteRight_";
  public static final String KEY_WIIBIND_NUNCHUK_C = "NunchukC_";
  public static final String KEY_WIIBIND_NUNCHUK_Z = "NunchukZ_";
  public static final String KEY_WIIBIND_NUNCHUK_UP = "NunchukUp_";
  public static final String KEY_WIIBIND_NUNCHUK_DOWN = "NunchukDown_";
  public static final String KEY_WIIBIND_NUNCHUK_LEFT = "NunchukLeft_";
  public static final String KEY_WIIBIND_NUNCHUK_RIGHT = "NunchukRight_";
  public static final String KEY_WIIBIND_NUNCHUK_SWING_UP = "NunchukSwingUp_";
  public static final String KEY_WIIBIND_NUNCHUK_SWING_DOWN = "NunchukSwingDown_";
  public static final String KEY_WIIBIND_NUNCHUK_SWING_LEFT = "NunchukSwingLeft_";
  public static final String KEY_WIIBIND_NUNCHUK_SWING_RIGHT = "NunchukSwingRight_";
  public static final String KEY_WIIBIND_NUNCHUK_SWING_FORWARD = "NunchukSwingForward_";
  public static final String KEY_WIIBIND_NUNCHUK_SWING_BACKWARD = "NunchukSwingBackward_";
  public static final String KEY_WIIBIND_NUNCHUK_TILT_FORWARD = "NunchukTiltForward_";
  public static final String KEY_WIIBIND_NUNCHUK_TILT_BACKWARD = "NunchukTiltBackward_";
  public static final String KEY_WIIBIND_NUNCHUK_TILT_LEFT = "NunchukTiltLeft_";
  public static final String KEY_WIIBIND_NUNCHUK_TILT_RIGHT = "NunchukTiltRight_";
  public static final String KEY_WIIBIND_NUNCHUK_TILT_MODIFIER = "NunchukTiltModifier_";
  public static final String KEY_WIIBIND_NUNCHUK_SHAKE_X = "NunchukShakeX_";
  public static final String KEY_WIIBIND_NUNCHUK_SHAKE_Y = "NunchukShakeY_";
  public static final String KEY_WIIBIND_NUNCHUK_SHAKE_Z = "NunchukShakeZ_";
  public static final String KEY_WIIBIND_CLASSIC_A = "ClassicA_";
  public static final String KEY_WIIBIND_CLASSIC_B = "ClassicB_";
  public static final String KEY_WIIBIND_CLASSIC_X = "ClassicX_";
  public static final String KEY_WIIBIND_CLASSIC_Y = "ClassicY_";
  public static final String KEY_WIIBIND_CLASSIC_ZL = "ClassicZL_";
  public static final String KEY_WIIBIND_CLASSIC_ZR = "ClassicZR_";
  public static final String KEY_WIIBIND_CLASSIC_MINUS = "ClassicMinus_";
  public static final String KEY_WIIBIND_CLASSIC_PLUS = "ClassicPlus_";
  public static final String KEY_WIIBIND_CLASSIC_HOME = "ClassicHome_";
  public static final String KEY_WIIBIND_CLASSIC_LEFT_UP = "ClassicLeftStickUp_";
  public static final String KEY_WIIBIND_CLASSIC_LEFT_DOWN = "ClassicLeftStickDown_";
  public static final String KEY_WIIBIND_CLASSIC_LEFT_LEFT = "ClassicLeftStickLeft_";
  public static final String KEY_WIIBIND_CLASSIC_LEFT_RIGHT = "ClassicLeftStickRight_";
  public static final String KEY_WIIBIND_CLASSIC_RIGHT_UP = "ClassicRightStickUp_";
  public static final String KEY_WIIBIND_CLASSIC_RIGHT_DOWN = "ClassicRightStickDown_";
  public static final String KEY_WIIBIND_CLASSIC_RIGHT_LEFT = "ClassicRightStickLeft_";
  public static final String KEY_WIIBIND_CLASSIC_RIGHT_RIGHT = "ClassicRightStickRight_";
  public static final String KEY_WIIBIND_CLASSIC_TRIGGER_L = "ClassicTriggerL_";
  public static final String KEY_WIIBIND_CLASSIC_TRIGGER_R = "ClassicTriggerR_";
  public static final String KEY_WIIBIND_CLASSIC_DPAD_UP = "ClassicUp_";
  public static final String KEY_WIIBIND_CLASSIC_DPAD_DOWN = "ClassicDown_";
  public static final String KEY_WIIBIND_CLASSIC_DPAD_LEFT = "ClassicLeft_";
  public static final String KEY_WIIBIND_CLASSIC_DPAD_RIGHT = "ClassicRight_";
  public static final String KEY_WIIBIND_GUITAR_FRET_GREEN = "GuitarGreen_";
  public static final String KEY_WIIBIND_GUITAR_FRET_RED = "GuitarRed_";
  public static final String KEY_WIIBIND_GUITAR_FRET_YELLOW = "GuitarYellow_";
  public static final String KEY_WIIBIND_GUITAR_FRET_BLUE = "GuitarBlue_";
  public static final String KEY_WIIBIND_GUITAR_FRET_ORANGE = "GuitarOrange_";
  public static final String KEY_WIIBIND_GUITAR_STRUM_UP = "GuitarStrumUp_";
  public static final String KEY_WIIBIND_GUITAR_STRUM_DOWN = "GuitarStrumDown_";
  public static final String KEY_WIIBIND_GUITAR_MINUS = "GuitarMinus_";
  public static final String KEY_WIIBIND_GUITAR_PLUS = "GuitarPlus_";
  public static final String KEY_WIIBIND_GUITAR_STICK_UP = "GuitarUp_";
  public static final String KEY_WIIBIND_GUITAR_STICK_DOWN = "GuitarDown_";
  public static final String KEY_WIIBIND_GUITAR_STICK_LEFT = "GuitarLeft_";
  public static final String KEY_WIIBIND_GUITAR_STICK_RIGHT = "GuitarRight_";
  public static final String KEY_WIIBIND_GUITAR_WHAMMY_BAR = "GuitarWhammy_";
  public static final String KEY_WIIBIND_DRUMS_PAD_RED = "DrumsRed_";
  public static final String KEY_WIIBIND_DRUMS_PAD_YELLOW = "DrumsYellow_";
  public static final String KEY_WIIBIND_DRUMS_PAD_BLUE = "DrumsBlue_";
  public static final String KEY_WIIBIND_DRUMS_PAD_GREEN = "DrumsGreen_";
  public static final String KEY_WIIBIND_DRUMS_PAD_ORANGE = "DrumsOrange_";
  public static final String KEY_WIIBIND_DRUMS_PAD_BASS = "DrumsBass_";
  public static final String KEY_WIIBIND_DRUMS_MINUS = "DrumsMinus_";
  public static final String KEY_WIIBIND_DRUMS_PLUS = "DrumsPlus_";
  public static final String KEY_WIIBIND_DRUMS_STICK_UP = "DrumsUp_";
  public static final String KEY_WIIBIND_DRUMS_STICK_DOWN = "DrumsDown_";
  public static final String KEY_WIIBIND_DRUMS_STICK_LEFT = "DrumsLeft_";
  public static final String KEY_WIIBIND_DRUMS_STICK_RIGHT = "DrumsRight_";
  public static final String KEY_WIIBIND_TURNTABLE_GREEN_LEFT = "TurntableGreenLeft_";
  public static final String KEY_WIIBIND_TURNTABLE_RED_LEFT = "TurntableRedLeft_";
  public static final String KEY_WIIBIND_TURNTABLE_BLUE_LEFT = "TurntableBlueLeft_";
  public static final String KEY_WIIBIND_TURNTABLE_GREEN_RIGHT = "TurntableGreenRight_";
  public static final String KEY_WIIBIND_TURNTABLE_RED_RIGHT = "TurntableRedRight_";
  public static final String KEY_WIIBIND_TURNTABLE_BLUE_RIGHT = "TurntableBlueRight_";
  public static final String KEY_WIIBIND_TURNTABLE_MINUS = "TurntableMinus_";
  public static final String KEY_WIIBIND_TURNTABLE_PLUS = "TurntablePlus_";
  public static final String KEY_WIIBIND_TURNTABLE_EUPHORIA = "TurntableEuphoria_";
  public static final String KEY_WIIBIND_TURNTABLE_LEFT_LEFT = "TurntableLeftLeft_";
  public static final String KEY_WIIBIND_TURNTABLE_LEFT_RIGHT = "TurntableLeftRight_";
  public static final String KEY_WIIBIND_TURNTABLE_RIGHT_LEFT = "TurntableRightLeft_";
  public static final String KEY_WIIBIND_TURNTABLE_RIGHT_RIGHT = "TurntableRightRight_";
  public static final String KEY_WIIBIND_TURNTABLE_STICK_UP = "TurntableUp_";
  public static final String KEY_WIIBIND_TURNTABLE_STICK_DOWN = "TurntableDown_";
  public static final String KEY_WIIBIND_TURNTABLE_STICK_LEFT = "TurntableLeft_";
  public static final String KEY_WIIBIND_TURNTABLE_STICK_RIGHT = "TurntableRight_";
  public static final String KEY_WIIBIND_TURNTABLE_EFFECT_DIAL = "TurntableEffDial_";
  public static final String KEY_WIIBIND_TURNTABLE_CROSSFADE_LEFT = "TurntableCrossLeft_";
  public static final String KEY_WIIBIND_TURNTABLE_CROSSFADE_RIGHT = "TurntableCrossRight_";

  public static final String KEY_WIIMOTE_SCAN = "WiimoteContinuousScanning";
  public static final String KEY_WIIMOTE_SPEAKER = "WiimoteEnableSpeaker";

  // Internal only, not actually found in settings file.
  public static final String KEY_VIDEO_BACKEND_INDEX = "VideoBackendIndex";

  private SettingsFile()
  {
  }

  /**
   * Reads a given .ini file from disk and returns it as a HashMap of SettingSections, themselves
   * effectively a HashMap of key/value settings. If unsuccessful, outputs an error telling why it
   * failed.
   *
   * @param fileName The name of the settings file without a path or extension.
   * @param view     The current view.
   * @return An Observable that emits a HashMap of the file's contents, then completes.
   */
  public static HashMap<String, SettingSection> readFile(final String fileName,
          SettingsActivityView view)
  {
    HashMap<String, SettingSection> sections = new SettingsSectionMap();

    File ini = getSettingsFile(fileName);

    BufferedReader reader = null;

    try
    {
      reader = new BufferedReader(new FileReader(ini));

      SettingSection current = null;
      for (String line; (line = reader.readLine()) != null; )
      {
        if (line.startsWith("[") && line.endsWith("]"))
        {
          current = sectionFromLine(line);
          sections.put(current.getName(), current);
        }
        else if ((current != null))
        {
          Setting setting = settingFromLine(current, line, fileName);
          if (setting != null)
          {
            current.putSetting(setting);
          }
        }
      }
    }
    catch (FileNotFoundException e)
    {
      Log.error("[SettingsFile] File not found: " + fileName + ".ini: " + e.getMessage());
      view.onSettingsFileNotFound();
    }
    catch (IOException e)
    {
      Log.error("[SettingsFile] Error reading from: " + fileName + ".ini: " + e.getMessage());
      view.onSettingsFileNotFound();
    }
    finally
    {
      if (reader != null)
      {
        try
        {
          reader.close();
        }
        catch (IOException e)
        {
          Log.error("[SettingsFile] Error closing: " + fileName + ".ini: " + e.getMessage());
        }
      }
    }

    return sections;
  }

  /**
   * Saves a Settings HashMap to a given .ini file on disk. If unsuccessful, outputs an error
   * telling why it failed.
   *
   * @param fileName The target filename without a path or extension.
   * @param sections The HashMap containing the Settings we want to serialize.
   * @param view     The current view.
   * @return An Observable representing the operation.
   */
  public static void saveFile(final String fileName, final HashMap<String, SettingSection> sections,
          SettingsActivityView view)
  {
    File ini = getSettingsFile(fileName);

    Wini writer = null;
    try
    {
      writer = new Wini(ini);

      Set<String> keySet = sections.keySet();
      for (String key : keySet)
      {
        SettingSection section = sections.get(key);
        writeSection(writer, section);
      }
      writer.store();
    }
    catch (IOException e)
    {
      Log.error("[SettingsFile] File not found: " + fileName + ".ini: " + e.getMessage());
      view.showToastMessage("Error saving " + fileName + ".ini: " + e.getMessage());
    }
  }

  @NonNull
  private static File getSettingsFile(String fileName)
  {
    return new File(
            DirectoryInitializationService.getUserDirectory() + "/config/" + fileName + ".ini");
  }

  private static SettingSection sectionFromLine(String line)
  {
    String sectionName = line.substring(1, line.length() - 1);
    return new SettingSection(sectionName);
  }

  /**
   * For a line of text, determines what type of data is being represented, and returns
   * a Setting object containing this data.
   *
   * @param current  The section currently being parsed by the consuming method.
   * @param line     The line of text being parsed.
   * @param fileName The name of the ini file the setting is in.
   * @return A typed Setting containing the key/value contained in the line.
   */
  private static Setting settingFromLine(SettingSection current, String line, String fileName)
  {
    String[] splitLine = line.split("=");

    if (splitLine.length != 2)
    {
      Log.warning("Skipping invalid config line \"" + line + "\"");
      return null;
    }

    String key = splitLine[0].trim();
    String value = splitLine[1].trim();

    if(value.isEmpty()){
      Log.warning("Skipping null value in config line \"" + line + "\"");
      return null;
    }

    int file = SETTINGS_DOLPHIN;

    try
    {
      int valueAsInt = Integer.valueOf(value);

      return new IntSetting(key, current.getName(), file, valueAsInt);
    }
    catch (NumberFormatException ex)
    {
    }

    try
    {
      float valueAsFloat = Float.valueOf(value);

      return new FloatSetting(key, current.getName(), file, valueAsFloat);
    }
    catch (NumberFormatException ex)
    {
    }

    return new StringSetting(key, current.getName(), file, value);
  }

  /**
   * Writes the contents of a Section HashMap to disk.
   *
   * @param parser  A Wini pointed at a file on disk.
   * @param section A section containing settings to be written to the file.
   */
  private static void writeSection(Wini parser, SettingSection section)
  {
    // Write the section header.
    String header = section.getName();

    // Write this section's values.
    HashMap<String, Setting> settings = section.getSettings();
    Set<String> keySet = settings.keySet();

    for (String key : keySet)
    {
      Setting setting = settings.get(key);
      parser.put(header, setting.getKey(), setting.getValueAsString());
    }
  }
}
