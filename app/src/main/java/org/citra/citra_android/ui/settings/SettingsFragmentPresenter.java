package org.citra.citra_android.ui.settings;

import android.text.TextUtils;

import org.citra.citra_android.R;
import org.citra.citra_android.model.settings.IntSetting;
import org.citra.citra_android.model.settings.Setting;
import org.citra.citra_android.model.settings.SettingSection;
import org.citra.citra_android.model.settings.view.CheckBoxSetting;
import org.citra.citra_android.model.settings.view.DateTimeSetting;
import org.citra.citra_android.model.settings.view.HeaderSetting;
import org.citra.citra_android.model.settings.view.SettingsItem;
import org.citra.citra_android.model.settings.view.SingleChoiceSetting;
import org.citra.citra_android.model.settings.view.SliderSetting;
import org.citra.citra_android.utils.EGLHelper;
import org.citra.citra_android.utils.SettingsFile;

import java.util.ArrayList;
import java.util.HashMap;

public final class SettingsFragmentPresenter
{
	private SettingsFragmentView mView;

	private String mMenuTag;
	private String mGameID;

	private ArrayList<HashMap<String, SettingSection>> mSettings;
	private ArrayList<SettingsItem> mSettingsList;

	private int mControllerNumber;
	private int mControllerType;

	public SettingsFragmentPresenter(SettingsFragmentView view)
	{
		mView = view;
	}

	public void onCreate(String menuTag, String gameId)
	{
		mGameID = gameId;
		mMenuTag = menuTag;

	}

	public void onViewCreated(ArrayList<HashMap<String, SettingSection>> settings)
	{
		setSettings(settings);
	}

	/**
	 * If the screen is rotated, the Activity will forget the settings map. This fragment
	 * won't, though; so rather than have the Activity reload from disk, have the fragment pass
	 * the settings map back to the Activity.
	 */
	public void onAttach()
	{
		if (mSettings != null)
		{
			mView.passSettingsToActivity(mSettings);
		}
	}

	public void putSetting(Setting setting)
	{
		mSettings.get(setting.getFile()).get(setting.getSection()).putSetting(setting);
	}

	public void loadDefaultSettings()
	{
		loadSettingsList();
	}

	public void setSettings(ArrayList<HashMap<String, SettingSection>> settings)
	{
		if (mSettingsList == null && settings != null)
		{
			mSettings = settings;

			loadSettingsList();
		}
		else
		{
			mView.showSettingsList(mSettingsList);
		}
	}

	private void loadSettingsList()
	{
		if (!TextUtils.isEmpty(mGameID))
		{
			mView.getActivity().setTitle("Game Settings: " + mGameID);
		}
		ArrayList<SettingsItem> sl = new ArrayList<>();

		switch (mMenuTag)
		{
			case SettingsFile.FILE_NAME_CONFIG:
				addCoreSettings(sl);
				sl.add(new HeaderSetting(null, null, R.string.video_backend, 0));
				addGraphicsSettings(sl);
				break;
			default:
				mView.showToastMessage("Unimplemented menu");
				return;
		}

		mSettingsList = sl;
		mView.showSettingsList(mSettingsList);
	}

	private void addCoreSettings(ArrayList<SettingsItem> sl)
	{
		Setting useCpuJit = null;
		Setting audioStretch = null;
		Setting region = null;
		Setting systemClock = null;
		Setting dateTime = null;

		if (!mSettings.get(SettingsFile.SETTINGS_DOLPHIN).isEmpty())
		{
			useCpuJit = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_CORE).getSetting(SettingsFile.KEY_CPU_JIT);
			audioStretch = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_AUDIO).getSetting(SettingsFile.KEY_ENABLE_AUDIO_STRETCHING);
			region = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_SYSTEM).getSetting(SettingsFile.KEY_REGION_VALUE);
			systemClock = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_SYSTEM).getSetting(SettingsFile.KEY_INIT_CLOCK);
			dateTime = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_SYSTEM).getSetting(SettingsFile.KEY_INIT_TIME);
		}
		else
		{
			mView.passSettingsToActivity(mSettings);
		}

		String defaultCpuCore = System.getProperty("os.arch");
		switch (defaultCpuCore)
		{
			case "x86_64":
				sl.add(new CheckBoxSetting(SettingsFile.KEY_CPU_JIT, SettingsFile.SECTION_CORE,
								SettingsFile.SETTINGS_DOLPHIN, R.string.cpu_jit, 0, true, useCpuJit));
				break;
			case "aarch64":
			default:
				break;
		}
		sl.add(new SingleChoiceSetting(SettingsFile.KEY_REGION_VALUE, SettingsFile.SECTION_SYSTEM,SettingsFile.SETTINGS_DOLPHIN, R.string.region, 0, R.array.regionNames, R.array.regionValues, -1, region));
		sl.add(new CheckBoxSetting(SettingsFile.KEY_ENABLE_AUDIO_STRETCHING, SettingsFile.SECTION_AUDIO, SettingsFile.SETTINGS_DOLPHIN, R.string.audio_stretch, R.string.audio_stretch_description, false, audioStretch));
		sl.add(new SingleChoiceSetting(SettingsFile.KEY_INIT_CLOCK, SettingsFile.SECTION_SYSTEM, SettingsFile.SETTINGS_DOLPHIN, R.string.init_clock, R.string.init_clock_descrip, R.array.systemClockNames, R.array.systemClockValues, 0, systemClock));
		sl.add(new DateTimeSetting(SettingsFile.KEY_INIT_TIME, SettingsFile.SECTION_SYSTEM, SettingsFile.SETTINGS_DOLPHIN, R.string.init_time, R.string.init_time_descrip, "2000-01-01 00:00:01", dateTime));
	}

	private void addGraphicsSettings(ArrayList<SettingsItem> sl)
	{
		Setting hardwareRenderer = null;
		Setting hardwareShader = null;
		Setting shadersAccurateMul = null;
		Setting shadersAccurateGs = null;
		Setting shaderJitEnable = null;
		Setting resolutionFactor = null;
		Setting vsyncEnable = null;
		Setting frameLimitEnable = null;
		Setting frameLimitValue = null;
		Setting stereoscopyEnable = null;
		Setting stereoscopyDepth = null;

		if (!mSettings.get(SettingsFile.SETTINGS_DOLPHIN).isEmpty())
		{
			hardwareRenderer = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_RENDERER).getSetting(SettingsFile.KEY_HW_RENDERER);
			hardwareShader = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_RENDERER).getSetting(SettingsFile.KEY_HW_SHADER);
			shadersAccurateMul = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_RENDERER).getSetting(SettingsFile.KEY_SHADERS_ACCURATE_MUL);
			shadersAccurateGs = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_RENDERER).getSetting(SettingsFile.KEY_SHADERS_ACCURATE_GS);
			shaderJitEnable = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_RENDERER).getSetting(SettingsFile.KEY_USE_SHADER_JIT);
			resolutionFactor = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_RENDERER).getSetting(SettingsFile.KEY_RESOLUTION_FACTOR);
			vsyncEnable = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_RENDERER).getSetting(SettingsFile.KEY_USE_VSYNC);
			frameLimitEnable = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_RENDERER).getSetting(SettingsFile.KEY_FRAME_LIMIT_ENABLED);
			frameLimitValue = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_RENDERER).getSetting(SettingsFile.KEY_FRAME_LIMIT);
			stereoscopyEnable = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_RENDERER).getSetting(SettingsFile.KEY_STEREOSCOPY);
			stereoscopyDepth = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_RENDERER).getSetting(SettingsFile.KEY_FACTOR_3D);
		}
		else
		{
			mView.passSettingsToActivity(mSettings);
		}

		if (mSettings.get(SettingsFile.SETTINGS_DOLPHIN).isEmpty())
		{
			mView.passSettingsToActivity(mSettings);
		}

		sl.add(new CheckBoxSetting(SettingsFile.KEY_HW_RENDERER, SettingsFile.SECTION_RENDERER, SettingsFile.SETTINGS_DOLPHIN, R.string.hw_renderer, 0, true, hardwareRenderer));
		sl.add(new CheckBoxSetting(SettingsFile.KEY_HW_SHADER, SettingsFile.SECTION_RENDERER, SettingsFile.SETTINGS_DOLPHIN, R.string.hw_shaders, R.string.hw_shaders_descrip, true, hardwareShader));
		sl.add(new CheckBoxSetting(SettingsFile.KEY_SHADERS_ACCURATE_MUL, SettingsFile.SECTION_RENDERER, SettingsFile.SETTINGS_DOLPHIN, R.string.shaders_accurate_mul, 0, false, shadersAccurateMul));
		sl.add(new CheckBoxSetting(SettingsFile.KEY_SHADERS_ACCURATE_GS, SettingsFile.SECTION_RENDERER, SettingsFile.SETTINGS_DOLPHIN, R.string.shaders_accurate_gs , 0, false, shadersAccurateGs));
		sl.add(new CheckBoxSetting(SettingsFile.KEY_USE_SHADER_JIT, SettingsFile.SECTION_RENDERER, SettingsFile.SETTINGS_DOLPHIN, R.string.use_shader_jit, 0, true, shaderJitEnable));
		sl.add(new SliderSetting(SettingsFile.KEY_RESOLUTION_FACTOR, SettingsFile.SECTION_RENDERER, SettingsFile.SETTINGS_DOLPHIN, R.string.internal_resolution, R.string.internal_resolution_descrip, 10, "x", 0, resolutionFactor));
		sl.add(new CheckBoxSetting(SettingsFile.KEY_USE_VSYNC, SettingsFile.SECTION_RENDERER, SettingsFile.SETTINGS_DOLPHIN, R.string.vsync	, 0, false, vsyncEnable));
		sl.add(new CheckBoxSetting(SettingsFile.KEY_FRAME_LIMIT_ENABLED, SettingsFile.SECTION_RENDERER, SettingsFile.SETTINGS_DOLPHIN, R.string.overclock_enable, 0, false, frameLimitEnable));
		sl.add(new SliderSetting(SettingsFile.KEY_FRAME_LIMIT, SettingsFile.SECTION_RENDERER, SettingsFile.SETTINGS_DOLPHIN, R.string.overclock_title, R.string.overclock_enable_description, 500, "%", 100, frameLimitValue));

		// Todo: Implement ColorPickerSetting
		// sl.add(new ColorPickerSetting(SettingsFile.KEY_BACKGROUND_RED,SettingsFile.KEY_BACKGROUND_GREEN,SettingsFile.KEY_BACKGROUND_BLUE, SettingsFile.SECTION_RENDERER, SettingsFile.SETTINGS_DOLPHIN, R.string., R.string., Color.BLACK, backgroundColor));

		/*
		 Check if we support stereo
		 If we support desktop GL then we must support at least OpenGL 3.2
		 If we only support OpenGLES then we need both OpenGLES 3.1 and AEP
		 */
		EGLHelper helper = new EGLHelper(EGLHelper.EGL_OPENGL_ES2_BIT);

		if ((helper.supportsOpenGL() && helper.GetVersion() >= 320) ||
						(helper.supportsGLES3() && helper.GetVersion() >= 310 && helper.SupportsExtension("GL_ANDROID_extension_pack_es31a")))
		{
			sl.add(new CheckBoxSetting(SettingsFile.KEY_STEREOSCOPY, SettingsFile.SECTION_RENDERER, SettingsFile.SETTINGS_DOLPHIN, R.string.stereoscopy, R.string.stereoscopy_descrip, false, stereoscopyEnable));
			sl.add(new SliderSetting(SettingsFile.KEY_FACTOR_3D, SettingsFile.SECTION_RENDERER, SettingsFile.SETTINGS_DOLPHIN, R.string.sterescopy_depth,R.string.sterescopy_depth_descrip,100,"%",0, stereoscopyDepth));
		}

	}


	private void addGcPadSubSettings(ArrayList<SettingsItem> sl, int gcPadNumber, int gcPadType)
	{
	/*
		if (gcPadType == 1) // Emulated
		{
			Setting bindA = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_A + gcPadNumber);
			Setting bindB = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_B + gcPadNumber);
			Setting bindX = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_X + gcPadNumber);
			Setting bindY = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_Y + gcPadNumber);
			Setting bindZ = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_Z + gcPadNumber);
			Setting bindStart = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_START + gcPadNumber);
			Setting bindControlUp = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_CONTROL_UP + gcPadNumber);
			Setting bindControlDown = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_CONTROL_DOWN + gcPadNumber);
			Setting bindControlLeft = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_CONTROL_LEFT + gcPadNumber);
			Setting bindControlRight = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_CONTROL_RIGHT + gcPadNumber);
			Setting bindCUp = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_C_UP + gcPadNumber);
			Setting bindCDown = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_C_DOWN + gcPadNumber);
			Setting bindCLeft = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_C_LEFT + gcPadNumber);
			Setting bindCRight = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_C_RIGHT + gcPadNumber);
			Setting bindTriggerL = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_TRIGGER_L + gcPadNumber);
			Setting bindTriggerR = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_TRIGGER_R + gcPadNumber);
			Setting bindDPadUp = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_DPAD_UP + gcPadNumber);
			Setting bindDPadDown = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_DPAD_DOWN + gcPadNumber);
			Setting bindDPadLeft = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_DPAD_LEFT + gcPadNumber);
			Setting bindDPadRight = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_BINDINGS).getSetting(SettingsFile.KEY_GCBIND_DPAD_RIGHT + gcPadNumber);

			sl.add(new HeaderSetting(null, null, R.string.generic_buttons, 0));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_A + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.button_a, bindA));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_B + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.button_b, bindB));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_X + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.button_x, bindX));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_Y + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.button_y, bindY));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_Z + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.button_z, bindZ));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_START + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.button_start, bindStart));

			sl.add(new HeaderSetting(null, null, R.string.controller_control, 0));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_CONTROL_UP + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.generic_up, bindControlUp));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_CONTROL_DOWN + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.generic_down, bindControlDown));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_CONTROL_LEFT + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.generic_left, bindControlLeft));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_CONTROL_RIGHT + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.generic_right, bindControlRight));

			sl.add(new HeaderSetting(null, null, R.string.controller_c, 0));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_C_UP + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.generic_up, bindCUp));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_C_DOWN + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.generic_down, bindCDown));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_C_LEFT + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.generic_left, bindCLeft));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_C_RIGHT + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.generic_right, bindCRight));

			sl.add(new HeaderSetting(null, null, R.string.controller_trig, 0));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_TRIGGER_L + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.trigger_left, bindTriggerL));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_TRIGGER_R + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.trigger_right, bindTriggerR));

			sl.add(new HeaderSetting(null, null, R.string.controller_dpad, 0));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_DPAD_UP + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.generic_up, bindDPadUp));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_DPAD_DOWN + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.generic_down, bindDPadDown));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_DPAD_LEFT + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.generic_left, bindDPadLeft));
			sl.add(new InputBindingSetting(SettingsFile.KEY_GCBIND_DPAD_RIGHT + gcPadNumber, SettingsFile.SECTION_BINDINGS, SettingsFile.SETTINGS_DOLPHIN, R.string.generic_right, bindDPadRight));
		}
		else // Adapter
		{
			Setting rumble = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_CORE).getSetting(SettingsFile.KEY_GCADAPTER_RUMBLE + gcPadNumber);
			Setting bongos = mSettings.get(SettingsFile.SETTINGS_DOLPHIN).get(SettingsFile.SECTION_CORE).getSetting(SettingsFile.KEY_GCADAPTER_BONGOS + gcPadNumber);

			sl.add(new CheckBoxSetting(SettingsFile.KEY_GCADAPTER_RUMBLE + gcPadNumber, SettingsFile.SECTION_CORE, SettingsFile.SETTINGS_DOLPHIN, R.string.gc_adapter_rumble, R.string.gc_adapter_rumble_description, false, rumble));
			sl.add(new CheckBoxSetting(SettingsFile.KEY_GCADAPTER_BONGOS + gcPadNumber, SettingsFile.SECTION_CORE, SettingsFile.SETTINGS_DOLPHIN, R.string.gc_adapter_bongos, R.string.gc_adapter_bongos_description, false, bongos));
		}
		*/
	}

	private boolean getInvertedBooleanValue(int file, String section, String key, boolean defaultValue)
	{
		try
		{
			return ((IntSetting) mSettings.get(file).get(section).getSetting(key)).getValue() != 1;
		}
		catch (NullPointerException ex)
		{
			return defaultValue;
		}
	}

}