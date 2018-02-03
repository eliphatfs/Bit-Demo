package com.gix.bit.demo;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class ChooseDeviceDialog {
	public static interface Callback {
		public void call(String id);
	}
	public static AlertDialog show(final Context context, final String title, final Callback callback) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		
		final List<String> items = new ArrayList<String>();
		final List<String> idStrings = new ArrayList<String>();
		
		for (BluetoothDevice device : BluetoothDeviceManager.local.getBondedDevices()) {
			items.add(device.getName());
			idStrings.add(device.getAddress());
		}
		
		builder.setItems(items.toArray(new String[items.size()]), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which >= 0)
					callback.call(idStrings.get(which));
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		return dialog;
	}
}
