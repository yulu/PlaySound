package com.example.playsound;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * This is a class to play a sound clip in a specified number of loops
 * To create an object of the class, acitivity context, number of loops and the source of the sound clip is required.
 * 
 * A LoopNotifier interface is provided to update the current state of the loop and the completion status
 * @author yulu
 *
 */

public class MediaPlayerLoop{
	/**
	 * Buffer time between two cycles, cannot be too small, otherwise some cycles will be skipped.
	 * TODO: will test this setting later
	 */
	private static int mBuffer = 1000;
	
	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> playHandle;
	
	private Context mContext;
	private MediaPlayer mediaPlayer;
	private int mLoops;
	private int mSingleDuration;
	
	private int currentLoop = 0;
	
	/**
	 * LoopNotifier Interface:
	 * onLoopUpdate: update the currentLoopNumber
	 * onLoopFinished: return the LoopNumber when stopping the mediaplayer
	 */
	private LoopNotifier mLoopNotifier;
	
	public interface LoopNotifier{
		public void onLoopUpdated(int loopNumber);
		public void onLoopFinished(int loopNumber);
	}

	public void setLoopNotifier(LoopNotifier l){
		this.mLoopNotifier = l;
	}
	
	/**
	 * Constructor
	 * @param context
	 * @param loops
	 * @param sourceId
	 */
	public MediaPlayerLoop(Context context, int loops, int sourceId) {
		mContext = context;
		mLoops = loops;
		
		//get the media and media durition
		mediaPlayer = MediaPlayer.create(context, sourceId);
		mSingleDuration = mediaPlayer.getDuration() + mBuffer;
		
		//setup the scheduler
		scheduler = Executors.newScheduledThreadPool(1);
				
	}

	/**
	 * to start the mediaplayer
	 */
	public void startPlay(){
		
		final Runnable playTask = new Runnable(){
			@Override
			public void run() {
				mediaPlayer.start();
				currentLoop++;
				
				//notify the subscriber the status change
				if(mLoopNotifier != null){
					mLoopNotifier.onLoopUpdated(currentLoop);
				}
				
			}		
		};
		
		playHandle = scheduler.scheduleAtFixedRate(playTask, 0, mSingleDuration, TimeUnit.MILLISECONDS);
		
		scheduler.schedule(new Runnable(){
			public void run(){
				if(!playHandle.isCancelled()){
					playHandle.cancel(true);
					if(mLoopNotifier != null){
						mLoopNotifier.onLoopFinished(currentLoop);
					}
				}
			}
		}, mSingleDuration*(mLoops-1), TimeUnit.MILLISECONDS);
		
	}
	
	/**
	 * to stop the mediaplayer
	 */
	public void stopPlay(){
		if(mediaPlayer.isPlaying())
			mediaPlayer.stop();
		
		if(playHandle != null && !playHandle.isCancelled()){
			playHandle.cancel(true);			
		}
		
		//notify the subscriber the status change
		if(mLoopNotifier != null){
			mLoopNotifier.onLoopFinished(currentLoop);
		}

	}

}
