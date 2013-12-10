package com.radicalninja.mybootx;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
		//
		ColorChangedListener colorListener = new ColorChangedListener();
		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		//picker.setOnColorChangedListener(colorListener);
		
		this.setView(cpv);
		
		// Setting the default OnClickListeners.
		this.setPositiveButton("Confirm", yesButtonPressed);
	}
	
	public OnClickListener yesButtonPressed = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			//Log.i(LOG_TAG, "YES PRESSED! Passing the color back!");
			//Log.i(LOG_TAG, "Color: "+picker.getColor());
			//mColorInteger = String.format("%d", picker.getColor());
		}
	};

	/*
	 * Implementing a quick OnColorChangedListener for the color picker.
	 * 
	 * All this code will probably move into its own class later.
	 */
	class ColorChangedListener extends java.lang.Object implements OnColorChangedListener {

		@Override
		public void onColorChanged(int color) {
			// TODO Auto-generated method stub
			//Log.i("ColorPicker", "Color: " + color);
		}
		
	}

}
