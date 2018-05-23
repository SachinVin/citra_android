package org.citra.citra_android.ui.settings.viewholder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import org.citra.citra_android.R;
import org.citra.citra_android.model.settings.view.InputBindingSetting;
import org.citra.citra_android.model.settings.view.SettingsItem;
import org.citra.citra_android.ui.settings.SettingsAdapter;

public final class InputBindingSettingViewHolder extends SettingViewHolder
{
	private InputBindingSetting mItem;

	private TextView mTextSettingName;
	private TextView mTextSettingDescription;

	private Context mContext;

	public InputBindingSettingViewHolder(View itemView, SettingsAdapter adapter, Context context)
	{
		super(itemView, adapter);

		mContext = context;
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
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

		mItem = (InputBindingSetting) item;

		mTextSettingName.setText(item.getNameId());
		mTextSettingDescription.setText(sharedPreferences.getString(mItem.getKey(), ""));
	}

	@Override
	public void onClick(View clicked)
	{
		getAdapter().onInputBindingClick(mItem, getAdapterPosition());
	}
}
