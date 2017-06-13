package info.papdt.blackblub.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import info.papdt.blackblub.R;
import info.papdt.blackblub.utils.NightScreenSettings;

import java.util.Locale;

public class SchedulerDialog extends Dialog implements TimePickerDialog.OnTimeSetListener {

	private TimePickerDialog sunrisePicker, sunsetPicker;
	private TextView sunriseTime, sunsetTime;

	private int hrsSunrise = 6, minSunrise = 0, hrsSunset = 22, minSunset = 0;

	private NightScreenSettings mSettings;

	public SchedulerDialog(Context context) {
		super(context);
		if (context instanceof Activity) setOwnerActivity((Activity) context);
		init();
	}

	public SchedulerDialog(Context context, int themeResId) {
		super(context, themeResId);
		if (context instanceof Activity) setOwnerActivity((Activity) context);
		init();
	}

	protected SchedulerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		if (context instanceof Activity) setOwnerActivity((Activity) context);
		init();
	}

	private void init() {
		mSettings = NightScreenSettings.getInstance(getContext());
		hrsSunrise = mSettings.getInt(NightScreenSettings.KEY_HOURS_SUNRISE, 6);
		minSunrise = mSettings.getInt(NightScreenSettings.KEY_MINUTES_SUNRISE, 0);
		hrsSunset = mSettings.getInt(NightScreenSettings.KEY_HOURS_SUNSET, 22);
		minSunset = mSettings.getInt(NightScreenSettings.KEY_MINUTES_SUNSET, 0);

		setContentView(R.layout.dialog_scheduler);

		sunriseTime = findViewById(R.id.sunrise_time);
		sunsetTime = findViewById(R.id.sunset_time);
		sunriseTime.setText(String.format(Locale.getDefault(), "%1$02d:%2$02d", hrsSunrise, minSunrise));
		sunsetTime.setText(String.format(Locale.getDefault(), "%1$02d:%2$02d", hrsSunset, minSunset));

		Switch switchView = findViewById(R.id.auto_switch);
		switchView.setChecked(mSettings.getBoolean(NightScreenSettings.KEY_AUTO_MODE, false));
		switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				mSettings.putBoolean(NightScreenSettings.KEY_AUTO_MODE, b);
			}
		});

		findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		findViewById(R.id.sunrise_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sunrisePicker = TimePickerDialog.newInstance(
						SchedulerDialog.this,
						hrsSunrise,
						minSunrise,
						true
				);
				sunrisePicker.setMinTime(4, 0, 0);
				sunrisePicker.setMaxTime(12, 0, 0);
				sunrisePicker.show(getOwnerActivity().getFragmentManager(), "sunrise_dialog");
			}
		});
		findViewById(R.id.sunset_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sunsetPicker = TimePickerDialog.newInstance(
						SchedulerDialog.this,
						hrsSunset,
						minSunset,
						true
				);
				sunsetPicker.setMinTime(18, 0,0);
				sunsetPicker.show(getOwnerActivity().getFragmentManager(), "sunset_dialog");
			}
		});
	}

	@Override
	public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
		if (view == sunrisePicker) {
			hrsSunrise = hourOfDay;
			minSunrise = minute;
			sunriseTime.setText(String.format(Locale.getDefault(), "%1$02d:%2$02d", hrsSunrise, minSunrise));
			mSettings.putInt(NightScreenSettings.KEY_HOURS_SUNRISE, hrsSunrise);
			mSettings.putInt(NightScreenSettings.KEY_MINUTES_SUNRISE, minSunrise);
		} else if (view == sunsetPicker) {
			hrsSunset = hourOfDay;
			minSunset = minute;
			sunsetTime.setText(String.format(Locale.getDefault(), "%1$02d:%2$02d", hrsSunset, minSunset));
			mSettings.putInt(NightScreenSettings.KEY_HOURS_SUNSET, hrsSunset);
			mSettings.putInt(NightScreenSettings.KEY_MINUTES_SUNSET, minSunset);
		}
	}

}