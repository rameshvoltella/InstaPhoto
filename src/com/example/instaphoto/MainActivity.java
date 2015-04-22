package com.example.instaphoto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;

/**
 * 
 * @author Timur Priymak
 * User gets to select a photo from the gallery 
 * and apply a few filters to it, undo a change, and 
 * save it to the gallery
 */
public class MainActivity extends Activity {

	/**
	 * Max amount of times to undo change
	 */
	private static final int MAX_UNDO = 3;
	/**
	 * MapFilter object
	 */
	private MapFilter my_filter;
	/**
	 * Cursor object
	 */
	private Cursor my_cursor;
	/**
	 * Image View widget of the app
	 */
	private ImageView my_image_view;
	/**
	 * current bitmap
	 */
	private Bitmap my_curr_bitmap;
	/**
	 * Stack holding max of three previous pictures
	 */
	private SimpleStack<Bitmap> undo;
	/**
	 * MediaScannerConnection object
	 */
	private MediaScannerConnection my_media_connection;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		my_filter = new MapFilter();
		my_image_view = (ImageView) findViewById(R.id.imageView1);
		undo = new SimpleStack(MAX_UNDO);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Opens up the image selection menu
	 * @param the_view the view the the pressed button
	 */
	public void nextPic(View the_view)
	{
		Intent choose_photo = new Intent(Intent.ACTION_PICK);
		choose_photo.setType("image/*");
		this.startActivityForResult(choose_photo, 1);
	}
	
	/**
	 * Either calls my_filter to flip the image or turn it black and white depending on user input
	 * @param the_view button pressed
	 */
	public void filterImage(View the_view)
	{
		try
		{
			//push image onto stack
			undo.push(my_curr_bitmap);
			if(the_view.getId() == R.id.button8)
			{
				//flip it horizontally
				my_curr_bitmap = my_filter.flipHorizontal(my_curr_bitmap);
			}
			else if(the_view.getId() == R.id.Button9)
			{
				//vertically
				my_curr_bitmap = my_filter.flipVertical(my_curr_bitmap);
			}
			else if(the_view.getId() == R.id.button5)
			{
				//turn it black and white
				my_curr_bitmap = my_filter.blackAndWhite(my_curr_bitmap);
			}
			my_image_view.setImageBitmap(my_curr_bitmap);
		}
		catch(NullPointerException e)
		{
			//Check if there is a valid image
			Toast my_toast = Toast.makeText(this, "Please select image before filtering", Toast.LENGTH_LONG);
			my_toast.show();
		}
	}
	
	/**
	 * Pops an earlier image off the stack
	 * Limited to 3 in order to conserve memory
	 * @param the_view button view
	 */
	public void undo(View the_view)
	{
		//check if stack is empty
		if(undo.getSize() > 0)
		{
			my_curr_bitmap.recycle();
			my_curr_bitmap = undo.pop();
			my_image_view.setImageBitmap(my_curr_bitmap);
		}
		else
		{
			//Check if max undos is maxed out
			Toast my_toast = Toast.makeText(this, "Sorry, only 3 undos!", Toast.LENGTH_SHORT);
			my_toast.show();
		}
	}
	
	/**
	 * Saves the image into the picture gallery
	 * giving it a generic timestamp name
	 * @param the_view button view
	 * @throws IOException
	 */
	public void save(View the_view) throws IOException
	{
		Calendar my_cal = Calendar.getInstance();
		FileOutputStream my_out = null;
		String my_string = String.valueOf(my_cal.get(Calendar.HOUR_OF_DAY))
				+
				String.valueOf(my_cal.get(Calendar.MINUTE))
				+
				String.valueOf(my_cal.get(Calendar.SECOND));
		File imageFileFolder = new File(Environment.getExternalStorageDirectory(),"instaPhoto");
		File imageFileName = new File(imageFileFolder, my_string + ".jpg");
		imageFileFolder.mkdir();
		//Give generic timestamp
			
		try
		{
			 my_out = new FileOutputStream(imageFileName);
			 my_curr_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, my_out);
			 my_out.flush();
			 my_out.close();
			 scanPhoto(imageFileName.toString());
			 my_out = null;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			Toast my_toast = Toast.makeText(this.getApplicationContext(), "Please insert SD card", Toast.LENGTH_SHORT);
			my_toast.show();
		}
	}
	/**
	 * scans image into media connection
	 * @param imageFileName
	 */
	public void scanPhoto(final String imageFileName)
	{
		my_media_connection = new MediaScannerConnection(this.getApplicationContext(), new MediaScannerConnectionClient()
		{
			public void onMediaScannerConnected()
			{
				my_media_connection.scanFile(imageFileName, null);
				Log.i("msClient obj  in Photo Utility","connection established");
			}
			public void onScanCompleted(String path, Uri uri)
			{
				my_media_connection.disconnect();
				Log.i("msClient obj in Photo Utility","scan completed");
			}
		});
		my_media_connection.connect();
	}

	/**
	 * Handles a selection of an image from the gallery dialog by 
	 * setting the selected image as a background to the image view
	 */
	@Override
	public void onActivityResult(int the_request_code, int the_result_code, Intent the_data)
	{
		super.onActivityResult(the_request_code, the_result_code, the_data);
		if(the_result_code == RESULT_OK && the_request_code == 1)
		{
				Uri my_selection = the_data.getData();
				String my_path = getPath(my_selection);
				Bitmap my_map = BitmapFactory.decodeFile(my_path);
				my_image_view.setImageBitmap(my_map);
				if(my_curr_bitmap != null)
				{
					my_curr_bitmap.recycle();
				}
				my_curr_bitmap = my_map;
		}
	}
	
	/**
	 * Gets the absolute path from the uri of the selected image
	 * @param the_uri the uri of the selected image
	 * @return a file path of the selected image
	 */
	private String getPath(Uri the_uri)
	{
		String [] my_projection = {MediaStore.Images.Media.DATA};
		my_cursor = this.getContentResolver().query(the_uri, my_projection, null, null, null);
		int my_column_index = my_cursor.getColumnIndexOrThrow(my_projection[0]);
		my_cursor.moveToFirst();
		String my_file_path = my_cursor.getString(my_column_index);
		my_cursor.close();
		return my_file_path;
	}
}