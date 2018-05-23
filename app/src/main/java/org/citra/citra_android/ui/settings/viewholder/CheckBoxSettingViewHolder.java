package org.citra.citra_android.ui.settings.viewholder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.citra.citra_android.R;
import org.citra.citra_android.model.settings.view.CheckBoxSetting;
import org.citra.citra_android.model.settings.view.SettingsItem;
import org.citra.citra_android.ui.settings.SettingsAdapter;

public final class CheckBoxSettingViewHolder extends SettingViewHolder
{
	private CheckBoxSetting mItem;

	private TextView mTextSettingName;
	private TextView mTextSettingDescription;

	private CheckBox mCheckbox;

	public CheckBoxSettingViewHolder(View itemView, SettingsAdapter adapter)
	{
		super(itemView, adapter);
	}

	@Override
	protected void findViews(View root)
	{
		mTextSettingName = root.findViewById(R.id.text_setting_name);
		mTextSettingDescription = root.findViewById(R.id.text_setting_description);
		mCheckbox = root.findViewById(R.id.checkbox);
	}

	@Override
	public void bind(SettingsItem item)
	{
		mItem = (CheckBoxSetting) item;

		mTextSettingName.setText(item.getNameId());

		if (item.getDescriptionId() > 0)
		{
			mTextSettingDescription.setText(item.getDescriptionId());
		}

		mCheckbox.setChecked(mItem.isChecked());
	}

	@Override
	public void onClick(View clicked)
	{
		mCheckbox.toggle();

		getAdapter().onBooleanClick(mItem, getAdapterPosition(), mCheckbox.isChecked());
	}
}
