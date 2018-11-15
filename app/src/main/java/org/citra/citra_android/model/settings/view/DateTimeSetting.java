package org.citra.citra_android.model.settings.view;

import org.citra.citra_android.model.settings.Setting;
import org.citra.citra_android.model.settings.StringSetting;

public final class DateTimeSetting extends SettingsItem
{
    private String mDefaultValue;

    public DateTimeSetting(String key, String section, int file, int titleId, int descriptionId,
                           String defaultValue, Setting setting)
    {
        super(key, section, file, setting, titleId, descriptionId);
        mDefaultValue = defaultValue;
    }

    public String getValue()
    {
        if (getSetting() != null)
        {
            StringSetting setting = (StringSetting) getSetting();
            return setting.getValue();
        }
        else
        {
            return mDefaultValue;
        }
    }
    public StringSetting setSelectedValue(String datetime)
    {
        StringSetting setting = new StringSetting(getKey(), getSection(), getFile(), datetime);
        setSetting(setting);
        return setting;
    }

    @Override
    public int getType()
    {
        return TYPE_DATETIME_SETTING;
    }
}