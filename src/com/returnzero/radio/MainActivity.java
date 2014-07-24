package com.returnzero.radio;

import io.vov.vitamio.*;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	final String SUGGESTED_URL = "rtmp://liveradio.irib.ir/livepkgr/javan";
	ImageButton mPlayButton;
	boolean isThisActivityCreatedBefore = false;
	boolean isOnPlay = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
			return;

		isThisActivityCreatedBefore = true;

		setContentView(R.layout.activity_main);

		Typeface face = Typeface
				.createFromAsset(getAssets(), "Font/hemmat.ttf");
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setTypeface(face);
		tv.setText(PersianReshape.reshape((String) tv.getText()));

		mPlayButton = (ImageButton) findViewById(R.id.buttonPlay);
		mPlayButton.setOnClickListener(this);
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
		if (isThisActivityCreatedBefore) {
			startService(new Intent(RadioService.ACTION_STOP));
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(RadioService.ACTION_TOGGLE_PLAYBACK);
		Uri uri = Uri.parse(SUGGESTED_URL);
		intent.setData(uri);
		startService(intent);

		if (isOnPlay) {
			mPlayButton.setBackgroundResource(R.drawable.play_white);

			isOnPlay = false;
		} else {
			mPlayButton.setBackgroundResource(R.drawable.pause_white);
			isOnPlay = true;
		}

	}

}
