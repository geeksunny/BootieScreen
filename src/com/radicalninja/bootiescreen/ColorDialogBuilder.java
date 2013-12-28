package com.radicalninja.bootiescreen;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;

public class ColorDialogBuilder extends Builder {
	
	private static final String LOG_TAG = "ColorDialogBuilder";
	
	private Context mContext;
	
	private ColorPicker picker;
	private SVBar svBar;
	private OpacityBar opacityBar;

	public ColorDialogBuilder(Context context) {
		super(context);
		mContext = context;
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View cpv = inflater.inflate(R.layout.color_picker, null);
		
		picker = (ColorPicker) cpv.findViewById(R.id.picker);
		svBar = (SVBar) cpv.findViewById(R.id.svbar);
		opacityBar = (OpacityBar) cpv.findViewById(R.id.opacitybar);
		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		
		this.setView(cpv);
		
		// Setting the default OnClickListeners.
		this.setPositiveButton("Confirm", defaultButtonActions)
			.setNegativeButton("Cancel", defaultButtonActions);
	}
	
	/**
	 * Returns the active ColorPicker object.
	 * @return Returns the active ColorPicker object.
	 */
	public ColorPicker getColorPicker() {
		return picker;
	}
	
	/**
	 * Initializes the color picker to use the given color value on all applicable spots.
	 * @param color An integer of the given color value.
	 */
	public void setColor(int color) {
		picker.setOldCenterColor(color);
		picker.setNewCenterColor(color);
		picker.setColor(color);
	}
	
	/**
	 * Default generic button actions. To be overwritten by class interfacing with this object.
	 */
	public OnClickListener defaultButtonActions = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				break;
			case DialogInterface.BUTTON_NEUTRAL:
				break;
			}
		}
	};

}
