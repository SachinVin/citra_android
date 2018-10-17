package org.citra.citra_android.model.settings.view;

import org.citra.citra_android.model.settings.IntSetting;
import org.citra.citra_android.model.settings.Setting;

public final class CheckBoxSetting extends SettingsItem
{
  private boolean mDefaultValue;

  public CheckBoxSetting(String key, String section, int file, int titleId, int descriptionId,
          boolean defaultValue, Setting setting)
  {
    super(key, section, file, setting, titleId, descriptionId);
    mDefaultValue = defaultValue;
  }

  public boolean isChecked()
  {
    if (getSetting() == null)
    {
      return mDefaultValue;
    }

    IntSetting setting = (IntSetting) getSetting();
    return setting.getValue() == 1;
  }

  /**
   * Write a value to the backing boolean. If that boolean was previously null,
   * initializes a new one and returns it, so it can be added to the Hashmap.
   *
   * @param checked Pretty self explanatory.
   * @return null if overwritten successfully; otherwise, a newly created BooleanSetting.
   */
  public IntSetting setChecked(boolean checked)
  {
    if (getSetting() == null)
    {
      IntSetting setting = new IntSetting(getKey(), getSection(), getFile(), checked ? 1 : 0);
      setSetting(setting);
      return setting;
    }
    else
    {
      IntSetting setting = (IntSetting) getSetting();
      setting.setValue(checked ? 1 : 0);
      return null;
    }
  }

  @Override
  public int getType()
  {
    return TYPE_CHECKBOX;
  }
}
