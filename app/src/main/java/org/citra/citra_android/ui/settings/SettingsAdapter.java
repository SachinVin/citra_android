package org.citra.citra_android.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v17.leanback.widget.picker.TimePicker;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;

import org.citra.citra_android.R;
import org.citra.citra_android.dialogs.MotionAlertDialog;
import org.citra.citra_android.model.settings.FloatSetting;
import org.citra.citra_android.model.settings.IntSetting;
import org.citra.citra_android.model.settings.StringSetting;
import org.citra.citra_android.model.settings.view.CheckBoxSetting;
import org.citra.citra_android.model.settings.view.DateTimeSetting;
import org.citra.citra_android.model.settings.view.InputBindingSetting;
import org.citra.citra_android.model.settings.view.SettingsItem;
import org.citra.citra_android.model.settings.view.SingleChoiceSetting;
import org.citra.citra_android.model.settings.view.SliderSetting;
import org.citra.citra_android.model.settings.view.SubmenuSetting;
import org.citra.citra_android.ui.settings.viewholder.CheckBoxSettingViewHolder;
import org.citra.citra_android.ui.settings.viewholder.DateTimeViewHolder;
import org.citra.citra_android.ui.settings.viewholder.HeaderViewHolder;
import org.citra.citra_android.ui.settings.viewholder.InputBindingSettingViewHolder;
import org.citra.citra_android.ui.settings.viewholder.SettingViewHolder;
import org.citra.citra_android.ui.settings.viewholder.SingleChoiceViewHolder;
import org.citra.citra_android.ui.settings.viewholder.SliderViewHolder;
import org.citra.citra_android.ui.settings.viewholder.SubmenuViewHolder;
import org.citra.citra_android.utils.Log;
import org.citra.citra_android.utils.SettingsFile;

import java.util.ArrayList;

public final class SettingsAdapter extends RecyclerView.Adapter<SettingViewHolder>
		implements DialogInterface.OnClickListener, SeekBar.OnSeekBarChangeListener
{
	private SettingsFragmentView mView;
	private Context mContext;
	private ArrayList<SettingsItem> mSettings;

	private SettingsItem mClickedItem;
	private int mSeekbarProgress;

	private AlertDialog mDialog;
	private TextView mTextSliderValue;

	public SettingsAdapter(SettingsFragmentView view, Context context)
	{
		mView = view;
		mContext = context;
	}

	@Override
	public SettingViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view;
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		switch (viewType)
		{
			case SettingsItem.TYPE_HEADER:
				view = inflater.inflate(R.layout.list_item_settings_header, parent, false);
				return new HeaderViewHolder(view, this);

			case SettingsItem.TYPE_CHECKBOX:
				view = inflater.inflate(R.layout.list_item_setting_checkbox, parent, false);
				return new CheckBoxSettingViewHolder(view, this);

			case SettingsItem.TYPE_SINGLE_CHOICE:
				view = inflater.inflate(R.layout.list_item_setting, parent, false);
				return new SingleChoiceViewHolder(view, this);

			case SettingsItem.TYPE_SLIDER:
				view = inflater.inflate(R.layout.list_item_setting, parent, false);
				return new SliderViewHolder(view, this);

			case SettingsItem.TYPE_SUBMENU:
				view = inflater.inflate(R.layout.list_item_setting, parent, false);
				return new SubmenuViewHolder(view, this);

			case SettingsItem.TYPE_INPUT_BINDING:
				view = inflater.inflate(R.layout.list_item_setting, parent, false);
				return new InputBindingSettingViewHolder(view, this, mContext);

			case SettingsItem.TYPE_DATETIME_SETTING:
                		view = inflater.inflate(R.layout.list_item_setting, parent, false);
                		return new DateTimeViewHolder(view, this);


			default:
				Log.error("[SettingsAdapter] Invalid view type: " + viewType);
				return null;
		}
	}

	@Override
	public void onBindViewHolder(SettingViewHolder holder, int position)
	{
		holder.bind(getItem(position));
	}

	private SettingsItem getItem(int position)
	{
		return mSettings.get(position);
	}

	@Override
	public int getItemCount()
	{
		if (mSettings != null)
		{
			return mSettings.size();
		}
		else
		{
			return 0;
		}
	}

	@Override
	public int getItemViewType(int position)
	{
		return getItem(position).getType();
	}

	public void setSettings(ArrayList<SettingsItem> settings)
	{
		mSettings = settings;
		notifyDataSetChanged();
	}

	public void onBooleanClick(CheckBoxSetting item, int position, boolean checked)
	{
		IntSetting setting = item.setChecked(checked);
		notifyItemChanged(position);

		if (setting != null)
		{
			mView.putSetting(setting);
		}

		mView.onSettingChanged();
	}

	public void onSingleChoiceClick(SingleChoiceSetting item)
	{
		mClickedItem = item;

		int value = getSelectionForSingleChoiceValue(item);

		AlertDialog.Builder builder = new AlertDialog.Builder(mView.getActivity());

		builder.setTitle(item.getNameId());
		builder.setSingleChoiceItems(item.getChoicesId(), value, this);

		mDialog = builder.show();
	}

	public void onDateTimeClick(DateTimeSetting item){
	    mClickedItem = item;

	    AlertDialog.Builder builder = new AlertDialog.Builder(mView.getActivity());

	    LayoutInflater inflater = LayoutInflater.from(mView.getActivity());
	    View view = inflater.inflate(R.layout.sysclock_datetime_picker, null);

        DatePicker dp = (DatePicker) view.findViewById(R.id.date_picker);
        TimePicker tp = (TimePicker) view.findViewById(R.id.time_picker);

        //set date and time to substrings of settingValue; format = 2018-12-24 04:20:69 (alright maybe not that 69)
        String settingValue = item.getValue();
        dp.updateDate(Integer.parseInt(settingValue.substring(0, 4)), Integer.parseInt(settingValue.substring(5, 7)) - 1, Integer.parseInt(settingValue.substring(8, 10)));

        tp.setIs24Hour(true);
        tp.setHour(Integer.parseInt(settingValue.substring(11, 12)));
        tp.setMinute(Integer.parseInt(settingValue.substring(14, 15)));

        DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //set it
                int year = dp.getYear();
                if (year < 2000){
                    year = 2000;
                }
                String month = ("00" + (dp.getMonth() + 1)).substring(String.valueOf(dp.getMonth() + 1).length());
                String day = ("00" + dp.getDayOfMonth()).substring(String.valueOf(dp.getDayOfMonth()).length());
                String hr = ("00" + tp.getHour()).substring(String.valueOf(tp.getHour()).length());
                String min = ("00" + tp.getMinute()).substring(String.valueOf(tp.getMinute()).length());
                String datetime = year + "-" + month + "-" + day + " " + hr + ":" + min + ":01";
                mView.putSetting(new StringSetting(item.getKey(), item.getSection(), item.getFile(), datetime));
                mView.onSettingChanged();
                mClickedItem = null;
                mSeekbarProgress = -1;
                closeDialog();
            }
        };
	    DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeDialog();
            }
        };
	    builder.setView(view);
	    builder.setPositiveButton("Set", ok);
	    builder.setNegativeButton("Cancel", cancel);
	    mDialog = builder.show();
    }
	public void onSliderClick(SliderSetting item)
	{
		mClickedItem = item;
		mSeekbarProgress = item.getSelectedValue();
		AlertDialog.Builder builder = new AlertDialog.Builder(mView.getActivity());

		LayoutInflater inflater = LayoutInflater.from(mView.getActivity());
		View view = inflater.inflate(R.layout.dialog_seekbar, null);

		builder.setTitle(item.getNameId());
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, this);
		builder.setNegativeButton(R.string.cancel, this);
		mDialog = builder.show();

		mTextSliderValue = view.findViewById(R.id.text_value);
		mTextSliderValue.setText(String.valueOf(mSeekbarProgress));

		TextView units = view.findViewById(R.id.text_units);
		units.setText(item.getUnits());

		SeekBar seekbar = view.findViewById(R.id.seekbar);

		seekbar.setMax(item.getMax());
		seekbar.setProgress(mSeekbarProgress);

		seekbar.setOnSeekBarChangeListener(this);
	}

	public void onSubmenuClick(SubmenuSetting item)
	{
		mView.loadSubMenu(item.getMenuKey());
	}

	public void onInputBindingClick(final InputBindingSetting item, final int position)
	{
		final MotionAlertDialog dialog = new MotionAlertDialog(mContext, item);
		dialog.setTitle(R.string.input_binding);
		dialog.setMessage(String.format(mContext.getString(R.string.input_binding_descrip), mContext.getString(item.getNameId())));
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, mContext.getString(R.string.cancel), this);
		dialog.setButton(AlertDialog.BUTTON_NEUTRAL, mContext.getString(R.string.clear), (dialogInterface, i) ->
		{
			item.setValue("");

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.remove(item.getKey());
			editor.apply();
		});
		dialog.setOnDismissListener(dialog1 ->
		{
			StringSetting setting = new StringSetting(item.getKey(), item.getSection(), item.getFile(), item.getValue());
			notifyItemChanged(position);

			if (setting != null)
			{
				mView.putSetting(setting);
			}

			mView.onSettingChanged();
		});
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if (mClickedItem instanceof SingleChoiceSetting)
		{
			SingleChoiceSetting scSetting = (SingleChoiceSetting) mClickedItem;

			int value = getValueForSingleChoiceSelection(scSetting, which);

			if (scSetting.getKey().startsWith(SettingsFile.KEY_GCPAD_TYPE))
			{
				mView.onGcPadSettingChanged(scSetting.getKey(), value);
			}

			if (scSetting.getKey().equals(SettingsFile.KEY_WIIMOTE_TYPE))
			{
				mView.onWiimoteSettingChanged(scSetting.getSection(), value);
			}

			if (scSetting.getKey().equals(SettingsFile.KEY_WIIMOTE_EXTENSION))
			{
				mView.onExtensionSettingChanged(scSetting.getKey() + Character.getNumericValue(scSetting.getSection().charAt(scSetting.getSection().length() - 1)), value);
			}

			// Get the backing Setting, which may be null (if for example it was missing from the file)
			IntSetting setting = scSetting.setSelectedValue(value);
			if (setting != null)
			{
				mView.putSetting(setting);
			}

			closeDialog();
		}
		else if (mClickedItem instanceof SliderSetting)
		{
			SliderSetting sliderSetting = (SliderSetting) mClickedItem;
			if (sliderSetting.getSetting() instanceof FloatSetting)
			{
				float value;

				if (sliderSetting.getKey().equals(SettingsFile.KEY_FRAME_LIMIT))
				{
					value = mSeekbarProgress / 100.0f;
				}
				else
				{
					value = (float) mSeekbarProgress;
				}

				FloatSetting setting = sliderSetting.setSelectedValue(value);
				if (setting != null)
				{
					mView.putSetting(setting);
				}
			}
			else
			{
				IntSetting setting = sliderSetting.setSelectedValue(mSeekbarProgress);
				if (setting != null)
				{
					mView.putSetting(setting);
				}
			}
		}

		mView.onSettingChanged();
		mClickedItem = null;
		mSeekbarProgress = -1;
	}

	public void closeDialog()
	{
		if (mDialog != null)
		{
			mDialog.dismiss();
			mDialog = null;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		mSeekbarProgress = progress;
		mTextSliderValue.setText(String.valueOf(mSeekbarProgress));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
	}

	private int getValueForSingleChoiceSelection(SingleChoiceSetting item, int which)
	{
		int valuesId = item.getValuesId();

		if (valuesId > 0)
		{
			int[] valuesArray = mContext.getResources().getIntArray(valuesId);
			return valuesArray[which];
		}
		else
		{
			return which;
		}
	}

	private int getSelectionForSingleChoiceValue(SingleChoiceSetting item)
	{
		int value = item.getSelectedValue();
		int valuesId = item.getValuesId();

		if (valuesId > 0)
		{
			int[] valuesArray = mContext.getResources().getIntArray(valuesId);
			for (int index = 0; index < valuesArray.length; index++)
			{
				int current = valuesArray[index];
				if (current == value)
				{
					return index;
				}
			}
		}
		else
		{
			return value;
		}

		return -1;
	}
}
