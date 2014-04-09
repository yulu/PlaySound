package com.example.playsound;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity{

	private Button playButton;
	private Button stopButton;
	private MediaPlayerLoop mediaPlayer;
	private TextView loopDisplay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		playButton = (Button)findViewById(R.id.play_button);
		stopButton = (Button)findViewById(R.id.stop_button);
		loopDisplay = (TextView)findViewById(R.id.loop_display);
		
		mediaPlayer = new MediaPlayerLoop(this, 4, R.raw.test1);		
		mediaPlayer.setLoopNotifier(new MediaPlayerLoop.LoopNotifier(){

			@Override
			public void onLoopUpdated(int loopNumber) {
				final int display = loopNumber;

				runOnUiThread(new Runnable(){
			        public void run() {
			    		loopDisplay.setText(String.valueOf(display));
			        }
			    });
				
			}

			@Override
			public void onLoopFinished(int loopNumber) {
				final int display = loopNumber;
				runOnUiThread(new Runnable(){
			        public void run() {
			    		loopDisplay.setText("complete at " + String.valueOf(display));
			        }
			    });
			}
		});
		
					
		playButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				playButton.setVisibility(View.INVISIBLE);
				mediaPlayer.startPlay();
				
			}
			
		});

		stopButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				mediaPlayer.stopPlay();
				
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onPause(){
		super.onPause();
		mediaPlayer.stopPlay();
	}

}
