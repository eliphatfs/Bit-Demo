package com.gix.bit.demo;

import java.io.File;
import java.util.concurrent.TimeUnit;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;

public class MediaManager {
	static MediaPlayer mediaPlayer = null;
	public static String currentPlaying = null;
	public static String next = null;
	public static final int LEAST_TIME_PLAY = 30000;
	/**
	 * Try to update the playing music.
	 * @param path Path of music file
	 * @return Whether music is updated.
	 * Either the previous music is not played long enough
	 * or it is the same as the inputting one
	 * or in work mode, it will return false.
	 * Otherwise, it will return true.
	 */
	public static boolean tryUpdateMusic(final String path) {
		if (MainActivity.isWorkMode) return false;
		if (mediaPlayer != null) {
			if (currentPlaying.equals(path) || mediaPlayer.getCurrentPosition() <= LEAST_TIME_PLAY)
				return false;
			stop();
		}
		MainActivity.instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				System.err.println(new File(path).exists());
				mediaPlayer = MediaPlayer.create(MainActivity.instance, Uri.fromFile(new File(path)));
				currentPlaying = path;
				mediaPlayer.start();
				mediaPlayer.setLooping(false);
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						MainActivity.executor.schedule(new Runnable() {
							@Override
							public void run() {
								currentPlaying = null;
								tryUpdateMusic(next);
								File file = Controller.pickMusic(Controller.status);
								next = file.getAbsolutePath();
								MainActivity.instance.changeMusicString(file.getName());
							}
						}, 400, TimeUnit.MICROSECONDS);
					}
				});
			}
		});
		return true;
	}
	public static void stop() {
		if (mediaPlayer == null) return;
		mediaPlayer.stop();
		mediaPlayer.release();
		mediaPlayer = null;
		currentPlaying = null;
	}
}
