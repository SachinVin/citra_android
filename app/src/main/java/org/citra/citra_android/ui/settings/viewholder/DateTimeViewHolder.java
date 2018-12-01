package org.citra.citra_android.ui.settings.viewholder;

import android.view.View;
import android.widget.TextView;

import org.citra.citra_android.R;
import org.citra.citra_android.model.settings.view.DateTimeSetting;
import org.citra.citra_android.model.settings.view.SettingsItem;
import org.citra.citra_android.ui.settings.SettingsAdapter;
import org.citra.citra_android.utils.Log;

public final class DateTimeViewHolder extends SettingViewHolder
{
    private DateTimeSetting mItem;

    private TextView mTextSettingName;
    private TextView mTextSettingDescription;

    public DateTimeViewHolder(View itemView, SettingsAdapter adapter)
    {
        super(itemView, adapter);
    }

    @Override
    protected void findViews(View root)
    {
        mTextSettingName = root.findViewById(R.id.text_setting_name);
        Log.error("test " + mTextSettingName);
        mTextSettingDescription = root.findViewById(R.id.text_setting_description);
        Log.error("test " + mTextSettingDescription);
    }

    @Override
    public void bind(SettingsItem item)
    {
        mItem = (DateTimeSetting) item;
        mTextSettingName.setText(item.getNameId());
        if (item.getDescriptionId() > 0)
        {
            mTextSettingDescription.setText(item.getDescriptionId());
        }
    }

    @Override
    public void onClick(View clicked)
    {
        getAdapter().onDateTimeClick(mItem);
    }
}