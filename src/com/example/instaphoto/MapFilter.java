package com.example.instaphoto;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * 
 * @author Timur Priymak
 * Filter used for flipping image and making image black and white
 */
public class MapFilter {

	/**
	 * amount to shift to get G
	 */
	private static final int SHIFT_G = 8;
	/**
	 * amount to shift to get R
	 */
	private static final int SHIFT_R = 16;
	/**
	 * amount to shift to get alpha
	 */
	private static final int SHIFT_ALPHA = 24;
	/**
	 * flips the image horizontally
	 * @param the_bitmap user inputed bitmap
	 * @return the flipped bitmap
	 */
	public Bitmap flipHorizontal(Bitmap the_bitmap)
	{
		Bitmap my_bitmap = Bitmap.createBitmap(the_bitmap.getWidth(), the_bitmap.getHeight(), the_bitmap.getConfig());
		for(int i = 0; i < the_bitmap.getWidth(); i++)
		{
			for(int j = 0; j < the_bitmap.getHeight(); j++)
			{
				my_bitmap.setPixel(i, j, the_bitmap.getPixel((the_bitmap.getWidth() - 1) - i, j));
			}
		}
		return my_bitmap;
		
	}
	
	/**
	 * Flips a bitmap vertically
	 * @param the_bitmap user inputted bitmap
	 * @return the flipped bitmap
	 */
	public Bitmap flipVertical(Bitmap the_bitmap)
	{
		Bitmap my_bitmap = Bitmap.createBitmap(the_bitmap.getWidth(), the_bitmap.getHeight(), the_bitmap.getConfig());
		for(int i = 0; i < the_bitmap.getWidth(); i++)
		{
			for(int j = 0; j < the_bitmap.getHeight(); j++)
			{
				my_bitmap.setPixel(i, j, the_bitmap.getPixel(i, (the_bitmap.getHeight() - 1) - j));
			}
		}
		return my_bitmap;
	}
	
	/**
	 * Makes the bitmap black and white
	 * @param the_bitmap user inputted bitmap
	 * @return black and white bitmap
	 */
	public Bitmap blackAndWhite(Bitmap the_bitmap)
	{
		Bitmap my_bitmap = Bitmap.createBitmap(the_bitmap.getWidth(), the_bitmap.getHeight(), the_bitmap.getConfig());
		int p;
		int a;
		int r;
		int g;
		int b;
		int new_color;
		int new_value;
		for(int i = 0; i < my_bitmap.getWidth(); i++)
		{
			for(int j = 0; j < my_bitmap.getHeight(); j++)
			{
				//Extract ARGB values by shifting and &ing
				p = the_bitmap.getPixel(i, j);
				a = (p >> SHIFT_ALPHA) & 0xff;
				r = (p >> SHIFT_R) & 0xff;
				g = (p >> SHIFT_G) & 0xff;
				b = p & 0xff;
				//Average out rgb values
				new_color = (r + g + b) / 3;
				new_value = a << 24 | new_color << 16 | new_color << 8 | new_color;
				my_bitmap.setPixel(i, j, new_value);
			}
		}
		return my_bitmap;
	}

}
