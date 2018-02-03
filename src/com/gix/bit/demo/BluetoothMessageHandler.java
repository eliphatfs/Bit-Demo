package com.gix.bit.demo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import android.bluetooth.BluetoothSocket;

public class BluetoothMessageHandler {
	public InputStream inputStream;
	public OutputStream outputStream;
	
	public BluetoothSocket device1Socket;
	
	public static BluetoothMessageHandler listenOnNew(BluetoothSocket socket) {
		try {
			return new BluetoothMessageHandler(socket);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private BluetoothMessageHandler(BluetoothSocket socket) throws IOException {
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		device1Socket = socket;
		MainActivity.executor.schedule(listen, 100, TimeUnit.MILLISECONDS);
	}
	
	int lightCD = 100;
	
	public Runnable listen = new Runnable() {
		@Override
		public void run() {
			try {
				lightCD--;
				if (toSend != null) {
					sendCommand(toSend);
					toSend = null;
				}
				int oldstat = Controller.status;
				Controller.ColorMusicPair result = null;
				while (inputStream.available() > 0) {
					final int data = inputStream.read();
					if (data <= 251)
						result = Controller.determination(data);
					else if (data == 255) {
						while (inputStream.available() <= 0)
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
							}
						MainActivity.instance.changeBeatString(String.valueOf(inputStream.read()));
					}
				}
				if (lightCD > 0) {
					Controller.status = oldstat;
					MainActivity.executor.schedule(listen, 100, TimeUnit.MILLISECONDS);
					return;
				}
				if (result != null) {
					lightCD = 100;
					toSend = result;
					System.err.println(Controller.status);
					if (Controller.status == 2) {
						MainActivity.instance.changeAdviceString("You are in good status, keep on!");
				    }
					else if (Controller.status == 1) {
						MainActivity.instance.changeAdviceString("Come on! Just think of something pleasant..?");
					}
					else if (Controller.status == 3) {
						MainActivity.instance.changeAdviceString("Worked too long? Maybe you can take a walk around.");
				    }
					else if (Controller.status == 4) {
				        MainActivity.instance.changeAdviceString("You need to have a rest.");
					}
					if (Controller.status != oldstat) {
						if (!MediaManager.tryUpdateMusic(Controller.pickMusic(Controller.status).getAbsolutePath()))
							Controller.status = oldstat;
						File file = Controller.pickMusic(Controller.status);
						MediaManager.next = file.getAbsolutePath();
						MainActivity.instance.changeMusicString(file.getName());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				//Toast.makeText(MainActivity.instance, "Read Socket Error!", Toast.LENGTH_LONG).show();
			}
			MainActivity.executor.schedule(listen, 100, TimeUnit.MILLISECONDS);
		}
	};
	public volatile Controller.ColorMusicPair toSend = null;
	public Controller.ColorMusicPair lastSent = null;
	public void sendCommand(Controller.ColorMusicPair data) throws IOException {
		lastSent = data;
    	System.err.println(1);
		device1Socket.close();
		long begin = System.currentTimeMillis();
		long cnt = 0;
		while (System.currentTimeMillis() - begin < 1000) {
			cnt++;
		}
    	System.err.println(2);
		BluetoothSocket socket = BluetoothDeviceManager.connectDevice(MainActivity.device2);
    	System.err.println(3);
		socket.getOutputStream().write(data.r);
		socket.getOutputStream().write(data.g);
		socket.getOutputStream().write(data.b);
		socket.getOutputStream().flush();
    	System.err.println(4);
		begin = System.currentTimeMillis();
		while (System.currentTimeMillis() - begin < 1000) {
			cnt++;
		}
    	System.err.println(5);
		begin = cnt;
		socket.close();
		begin = System.currentTimeMillis();
		while (System.currentTimeMillis() - begin < 1000) {
			cnt++;
		}
    	System.err.println(6);
		device1Socket = BluetoothDeviceManager.connectDevice(MainActivity.device1);
    	System.err.println(7);
		inputStream = device1Socket.getInputStream();
		outputStream = device1Socket.getOutputStream();
	}
}
