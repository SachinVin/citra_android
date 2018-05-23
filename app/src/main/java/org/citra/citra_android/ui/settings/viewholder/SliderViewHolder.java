package org.citra.citra_android.ui.settings.viewholder;

import android.view.View;
import android.widget.TextView;

import org.citra.citra_android.R;
import org.citra.citra_android.model.settings.view.SettingsItem;
import org.citra.citra_android.model.settings.view.SliderSetting;
import org.citra.citra_android.ui.settings.SettingsAdapter;

public final class SliderViewHolder extends SettingViewHolder
{
	private SliderSetting mItem;

	private TextView mTextSettingName;
	private TextView mTextSettingDescription;

	public SliderViewHolder(View itemView, SettingsAdapter adapter)
	{
		super(itemView, adapter);
	}

	@Override
	protected void findViews(View root)
	{
		mTextSettingName = root.findViewById(R.id.text_setting_name);
		mTextSettingDescription = root.findViewById(R.id.text_setting_description);
	}

	@Override
	public void bind(SettingsItem item)
	{
		mItem = (SliderSetting) item;

		mTextSettingName.setText(item.getNameId());

		if (item.getDescriptionId() > 0)
		{
			mTextSettingDescription.setText(item.getDescriptionId());
		}
	}

	@Override
	public void onClick(View clicked)
	{
		getAdapter().onSliderClick(mItem);
	}
}

