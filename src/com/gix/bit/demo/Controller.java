package com.gix.bit.demo;

import java.io.File;

public class Controller {
	public static final int TOOLOW = 50;
	public static final int LOWER_LIMIT = 98;
	public static final int UPPER_LIMIT = 180;
	public static final int TOOHIGH = 250;
	public static int[] X = new int[20];
	public static int last = 0, T = 0;
	public static double pj = 0, la = 0;
	public static int tt = 0;
	public static int s = 0;
	public static class ColorMusicPair {
		public int r, g, b;
		public String music;
		public ColorMusicPair(int r, int g, int b) {
			this(r, g, b, null);
		}
		public ColorMusicPair(int r, int g, int b, String music) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.music = music;
		}
	}
	public static final String Stat4 = "/storage/sdcard1/催眠曲/";
	public static final String Stat3 = "/storage/sdcard1/平静/";
	public static final String Stat2 = "/storage/sdcard1/MusicNormal/";
	public static final String Stat1 = "/storage/sdcard1/亢奋/";
	public static int status = 0; //歌曲状态
	/**
	 * @param x SDNN value
	 */
	public static ColorMusicPair determination(int x) {
	    if (x <= TOOLOW) return new ColorMusicPair(255, 157, 255); //立刻报警 R255 G157 B255
	    if (MainActivity.isWorkMode) return null;
	    if (x != last) {
	        if (T == 20) {
	            T = 0;
	            if (tt == 0) {pj = s / 20; tt = 1;}
	        }
	        if (tt != 0) { pj = (s - X[T] + x) / 20.0; s = s - X[T] + x; }
	        else s = s + x;
	        X[T++] = x;
	    }
	    last = x;
	    if (tt == 0) return null;
	    if (pj <= LOWER_LIMIT){
	        if (la <= LOWER_LIMIT) return null;
	        status = 1;
	        return new ColorMusicPair(255, 167, 79);
	        //亮（红）灯 R255 G167 B79
	        //放激动的歌
	    }

	    if (pj > LOWER_LIMIT && pj < UPPER_LIMIT){
	        if (la > LOWER_LIMIT && la < UPPER_LIMIT) return null;
	        status = 2;
	        return new ColorMusicPair(255, 244, 147);
	        //亮（橙）色灯 继续放原来的歌 R255 G244 B147
	    }
	    if (pj >= UPPER_LIMIT && pj < TOOHIGH){
	        if (la >= UPPER_LIMIT && la < TOOHIGH) return null;
	        status = 3;
	        return new ColorMusicPair(185, 255, 255);
	        //亮（蓝）灯 R185 G255 B255
	        //放舒缓的歌
	    }
	    if (pj >= TOOHIGH) {
	        if (la >= TOOHIGH) return null;
	        status = 4;
	        return new ColorMusicPair(186, 255, 80);
	        //绿 R186 G255 B80
	        //催眠曲
	    }
	    la = pj;
	    return null;
	}
	
	public static File pickMusic(int stat) {
		String dir = "";
		switch (stat) {
		case 1:
			dir = Stat1;
			break;
		case 2:
			dir = Stat2;
			break;
		case 3:
			dir = Stat3;
			break;
		case 4:
			dir = Stat4;
			break;
		default:
			throw new AssertionError();
		}
		File group = new File(dir);
		File[] list = group.listFiles();
		return list[(int) (Math.random() * list.length)];
	}
}
