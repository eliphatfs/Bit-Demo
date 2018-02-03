package com.gix.bit.demo;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothDeviceManager {
	public static BluetoothDevice remote;
	public static BluetoothAdapter local;
	private static Set<BluetoothSocket> mSocketSet = new HashSet<BluetoothSocket>();
	static {
		local = BluetoothAdapter.getDefaultAdapter();
		remote = null;
	}
	public static BluetoothSocket connectDevice(String id) {
		if (remote != null)
			disconnect();
		remote = local.getRemoteDevice(id);
		final BluetoothSocket socket;
		try {
			socket = remote.createRfcommSocketToServiceRecord(remote.getUuids()[0].getUuid());
			socket.connect();
			//Toast.makeText(MainActivity.instance, socket.toString(), Toast.LENGTH_LONG).show();
			mSocketSet.add(socket);
			return socket;
		} catch (IOException e) {
			e.printStackTrace();
			//Toast.makeText(MainActivity.instance, "Error opening socket, returning null...", Toast.LENGTH_LONG).show();
		}
		return null;
	}
	
	public static void disconnect() {
		remote = null;
		/*for (BluetoothSocket bluetoothSocket : mSocketSet) {
			try {
				bluetoothSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		mSocketSet.clear();
	}
}
