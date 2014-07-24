package com.returnzero.radio;

import io.vov.vitamio.*;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	// A url to play radio from
	final String SUGGESTED_URL = "rtmp://liveradio.irib.ir/livepkgr/javan";

	ImageButton mPlayButton;

	/*
	 * This boolean checks if the Activity is created because on first time
	 * running initializing decoders from libschhedcker destroyed the activity
	 */
	boolean isThisActivityCreatedBefore = false;
	/*
	 * This is a listener for getting intents from service those intents are for
	 * updating gui
	 */
	ActivityIntentReciever activityReciever;

	/*
	 * A boolean to know if the radio is playing or not to use for changes that
	 * gui must do even if the activity is onpause
	 */
	boolean isThisPlaying = false;

	public static final String STATUS_PLAYING = "com.returzero.radio.player.status.PLAYING";
	public static final String STATUS_PAUSED = "com.returzero.radio.player.status.PAUSED";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
			return;

		isThisActivityCreatedBefore = true;

		setContentView(R.layout.activity_main);

		// Creating fonts and setting up the textview
		Typeface face = Typeface
				.createFromAsset(getAssets(), "Font/hemmat.ttf");
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setTypeface(face);
		tv.setText(PersianReshape.reshape((String) tv.getText()));

		mPlayButton = (ImageButton) findViewById(R.id.buttonPlay);
		mPlayButton.setOnClickListener(this);

		// Requesting the Service to update isThisPlaying (Believe me in some
		// cases it is required)
		Intent intent = new Intent(RadioService.ACTION_UPDATEME);
		startService(intent);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		// Unregistering the Receiver
		if (isThisActivityCreatedBefore) {
			unregisterReceiver(activityReciever);
		}
		super.onPause();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// Making sure to stop the player when it is not playing
		if (isThisActivityCreatedBefore && !isThisPlaying) {
			startService(new Intent(RadioService.ACTION_STOP));
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		// Handling the play button (it should be in if later)
		Intent intent = new Intent(RadioService.ACTION_TOGGLE_PLAYBACK);
		Uri uri = Uri.parse(SUGGESTED_URL);
		intent.setData(uri);
		startService(intent);

	}

	// This method is for handling the image change of pause and play
	public void togglePlayPause() {
		if (isThisPlaying) {
			mPlayButton.setBackgroundResource(R.drawable.pause_white);
		} else {
			mPlayButton.setBackgroundResource(R.drawable.play_white);
		}
	}

	@Override
	protected void onResume() {

		// Updating the Image of Play
		togglePlayPause();
		// Creating and Registering Reciever
		IntentFilter statusFilter;
		statusFilter = new IntentFilter();
		statusFilter.addAction(STATUS_PLAYING);
		statusFilter.addAction(STATUS_PAUSED);
		activityReciever = new ActivityIntentReciever();
		registerReceiver(activityReciever, statusFilter);

		super.onResume();
	}

	// The Reciever

	public class ActivityIntentReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context ctx, Intent intent) {
			// TODO Auto-generated method stub

			Log.d("Activity", intent.getAction());

			// Updating isThisPlaying
			if (intent.getAction() == STATUS_PAUSED) {
				isThisPlaying = false;
				togglePlayPause();
			} else if (intent.getAction() == STATUS_PLAYING) {
				isThisPlaying = true;
				togglePlayPause();
			}

		}

	}
}
