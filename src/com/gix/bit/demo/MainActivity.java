package com.gix.bit.demo;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import android.app.*;
import android.os.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity
{
	public static ScheduledExecutorService executor;
	public static ToggleButton toggleButton;
	public static Button changeMusicButton;
	public static MainActivity instance;
	public static String device1, device2;
	public static boolean isWorkMode = false;
	public static BluetoothMessageHandler messageHandler = null;
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
    	instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
        toggleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				triggerWorkMode();
			}
		});
        
        changeMusicButton = (Button) findViewById(R.id.changeButton);
        changeMusicButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Controller.status >= 1 && Controller.status <= 4) {
					File file = Controller.pickMusic(Controller.status);
					MediaManager.next = file.getAbsolutePath();
					MainActivity.instance.changeMusicString(file.getName());
				}
			}
		});
        
        executor = Executors.newSingleThreadScheduledExecutor();
        showDialog1();
    }
    
    public void dialogsFinished() {
    	messageHandler = BluetoothMessageHandler.listenOnNew(BluetoothDeviceManager.connectDevice(device1));
    }
    
    public void showDialog1() {
    	ChooseDeviceDialog.show(this, "Choose Device 1...", new ChooseDeviceDialog.Callback() {
			@Override
			public void call(String id) {
				device1 = id;
				showDialog2();
			}
        });
    }
    
    public void showDialog2() {
    	ChooseDeviceDialog.show(this, "Choose Device 2...", new ChooseDeviceDialog.Callback() {
			@Override
			public void call(String id) {
				device2 = id;
				dialogsFinished();
			}
        });
    }
    
    public void triggerWorkMode() {
    	if ((Controller.last <= Controller.TOOLOW || Controller.last >= Controller.TOOHIGH) && !isWorkMode) {
    		toggleButton.toggle();
    		return;
    	}
    	isWorkMode = !isWorkMode;
    	if (isWorkMode) {
    		MediaManager.stop();
			messageHandler.toSend = new Controller.ColorMusicPair(255, 255, 255);
    	}
    }
    
    public void changeBeatString(final String n) {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
		    	((TextView) findViewById(R.id.beatLabel)).setText(n + " (Maybe VERY inaccurate sometimes...)");
			}
		});
    }
    
    public void changeMusicString(final String n) {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
		    	((TextView) findViewById(R.id.musicLabel)).setText(n);
			}
		});
    }
    
    public void changeAdviceString(final String n) {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
		    	((TextView) findViewById(R.id.adviceLabel)).setText(n);
			}
		});
    }
}
