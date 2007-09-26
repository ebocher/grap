package org.grap.lut;

import ij.io.FileInfo;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;

public class LutGenerator {

	public static int fire(byte[] reds, byte[] greens, byte[] blues) {
		int[] r = { 0, 0, 1, 25, 49, 73, 98, 122, 146, 162, 173, 184, 195, 207,
				217, 229, 240, 252, 255, 255, 255, 255, 255, 255, 255, 255,
				255, 255, 255, 255, 255, 255 };
		int[] g = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 35, 57, 79, 101,
				117, 133, 147, 161, 175, 190, 205, 219, 234, 248, 255, 255,
				255, 255 };
		int[] b = { 0, 61, 96, 130, 165, 192, 220, 227, 210, 181, 151, 122, 93,
				64, 35, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 35, 98, 160, 223,
				255 };
		for (int i = 0; i < r.length; i++) {
			reds[i] = (byte) r[i];
			greens[i] = (byte) g[i];
			blues[i] = (byte) b[i];
		}
		return r.length;
	}

	public static int grays(byte[] reds, byte[] greens, byte[] blues) {
		for (int i = 0; i < 256; i++) {
			reds[i] = (byte) i;
			greens[i] = (byte) i;
			blues[i] = (byte) i;
		}
		return 256;
	}

	public static int primaryColor(int color, byte[] reds, byte[] greens,
			byte[] blues) {
		for (int i = 0; i < 256; i++) {
			if ((color & 4) != 0)
				reds[i] = (byte) i;
			if ((color & 2) != 0)
				greens[i] = (byte) i;
			if ((color & 1) != 0)
				blues[i] = (byte) i;
		}
		return 256;
	}

	static int ice(byte[] reds, byte[] greens, byte[] blues) {
		int[] r = { 0, 0, 0, 0, 0, 0, 19, 29, 50, 48, 79, 112, 134, 158, 186,
				201, 217, 229, 242, 250, 250, 250, 250, 251, 250, 250, 250,
				250, 251, 251, 243, 230 };
		int[] g = { 156, 165, 176, 184, 190, 196, 193, 184, 171, 162, 146, 125,
				107, 93, 81, 87, 92, 97, 95, 93, 93, 90, 85, 69, 64, 54, 47,
				35, 19, 0, 4, 0 };
		int[] b = { 140, 147, 158, 166, 170, 176, 209, 220, 234, 225, 236, 246,
				250, 251, 250, 250, 245, 230, 230, 222, 202, 180, 163, 142,
				123, 114, 106, 94, 84, 64, 26, 27 };
		for (int i = 0; i < r.length; i++) {
			reds[i] = (byte) r[i];
			greens[i] = (byte) g[i];
			blues[i] = (byte) b[i];
		}
		return r.length;
	}

	public static int spectrum(byte[] reds, byte[] greens, byte[] blues) {
		Color c;
		for (int i = 0; i < 256; i++) {
			c = Color.getHSBColor(i / 255f, 1f, 1f);
			reds[i] = (byte) c.getRed();
			greens[i] = (byte) c.getGreen();
			blues[i] = (byte) c.getBlue();
		}
		return 256;
	}

	public static int rgb332(byte[] reds, byte[] greens, byte[] blues) {
		for (int i = 0; i < 256; i++) {
			reds[i] = (byte) (i & 0xe0);
			greens[i] = (byte) ((i << 3) & 0xe0);
			blues[i] = (byte) ((i << 6) & 0xc0);
		}
		return 256;
	}

	public static int redGreen(byte[] reds, byte[] greens, byte[] blues) {
		for (int i = 0; i < 128; i++) {
			reds[i] = (byte) (i * 2);
			greens[i] = (byte) 0;
			blues[i] = (byte) 0;
		}
		for (int i = 128; i < 256; i++) {
			reds[i] = (byte) 0;
			greens[i] = (byte) (i * 2);
			blues[i] = (byte) 0;
		}
		return 256;
	}

	public static void interpolate(byte[] reds, byte[] greens, byte[] blues,
			int nColors) {
		byte[] r = new byte[nColors];
		byte[] g = new byte[nColors];
		byte[] b = new byte[nColors];
		System.arraycopy(reds, 0, r, 0, nColors);
		System.arraycopy(greens, 0, g, 0, nColors);
		System.arraycopy(blues, 0, b, 0, nColors);
		double scale = nColors / 256.0;
		int i1, i2;
		double fraction;
		for (int i = 0; i < 256; i++) {
			i1 = (int) (i * scale);
			i2 = i1 + 1;
			if (i2 == nColors)
				i2 = nColors - 1;
			fraction = i * scale - i1;
			// IJ.write(i+" "+i1+" "+i2+" "+fraction);
			reds[i] = (byte) ((1.0 - fraction) * (r[i1] & 255) + fraction
					* (r[i2] & 255));
			greens[i] = (byte) ((1.0 - fraction) * (g[i1] & 255) + fraction
					* (g[i2] & 255));
			blues[i] = (byte) ((1.0 - fraction) * (b[i1] & 255) + fraction
					* (b[i2] & 255));
		}
	}

	public static ColorModel colorModel(String arg) {

		FileInfo fi = new FileInfo();
		fi.reds = new byte[256];
		fi.greens = new byte[256];
		fi.blues = new byte[256];
		fi.lutSize = 256;
		int nColors = 0;

		if (arg.equalsIgnoreCase("fire"))
			nColors = fire(fi.reds, fi.greens, fi.blues);
		else if (arg.equalsIgnoreCase("grays"))
			nColors = grays(fi.reds, fi.greens, fi.blues);
		else if (arg.equalsIgnoreCase("ice"))
			nColors = ice(fi.reds, fi.greens, fi.blues);
		else if (arg.equalsIgnoreCase("spectrum"))
			nColors = spectrum(fi.reds, fi.greens, fi.blues);
		else if (arg.equalsIgnoreCase("3-3-2 RGB"))
			nColors = rgb332(fi.reds, fi.greens, fi.blues);
		else if (arg.equalsIgnoreCase("red"))
			nColors = primaryColor(4, fi.reds, fi.greens, fi.blues);
		else if (arg.equalsIgnoreCase("green"))
			nColors = primaryColor(2, fi.reds, fi.greens, fi.blues);
		else if (arg.equalsIgnoreCase("blue"))
			nColors = primaryColor(1, fi.reds, fi.greens, fi.blues);
		else if (arg.equalsIgnoreCase("cyan"))
			nColors = primaryColor(3, fi.reds, fi.greens, fi.blues);
		else if (arg.equalsIgnoreCase("magenta"))
			nColors = primaryColor(5, fi.reds, fi.greens, fi.blues);
		else if (arg.equalsIgnoreCase("yellow"))
			nColors = primaryColor(6, fi.reds, fi.greens, fi.blues);
		else if (arg.equalsIgnoreCase("redgreen"))
			nColors = redGreen(fi.reds, fi.greens, fi.blues);
		if (nColors > 0) {
			if (nColors < 256)
				interpolate(fi.reds, fi.greens, fi.blues, nColors);
			fi.fileName = arg;
			ColorModel cm = new IndexColorModel(8, 256, fi.reds, fi.greens,
					fi.blues);

			return cm;
		}

		return null;

	}
}