package edu.battlefield.vision;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class IPVideoCapture extends AbstractNotifier
{
	private ImageNetworkConnection capture = null;
	
	private final int NUM_THREADS = 1;
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(NUM_THREADS);
	
	private int mFPS;
	
	private ScheduledFuture<?> mCameraReadHandler = null;
	
	private FrameReader mFrameReader = new FrameReader();
	
	private final int mCameraIndex;
	
	public IPVideoCapture()
	{
		super();
		mFPS = 20;
		mCameraIndex= 0;
		
		try 
		{
			setupParameters();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}	
	}
	
	private void setupParameters() throws IOException
	{
		capture = new ImageNetworkConnection("http://10.18.85.21/axis-cgi/jpg/image.cgi");
		
		mCameraReadHandler = scheduler.scheduleAtFixedRate(mFrameReader, 1000, (int)((1.0/mFPS)*1000), TimeUnit.MILLISECONDS);	
	}
	
	public ScheduledFuture<?> getCameraReadHandler()
	{
		return mCameraReadHandler;
	}
	
	private class FrameReader implements Runnable 
	{
		/**
		 * @brief the input frame from the specified source
		 */
		private Mat mReceivedFrame = new Mat();
    	
		/**
		 * @brief the last time the function was called...
		 */
		private long mLastTime = 0;
       	
		/**
		 * @brief run function for the threading...
		 */
    	public void run() 
    	{ 	    	   	
    	   	// read the desired frame...
    		try 
    		{
				mReceivedFrame = AerialAssist.toMatrix(capture.grabImage());
			} 
    		catch (IOException e) 
    		{
				e.printStackTrace();
			}
    		
    		// flip the frame 
    		Core.flip(mReceivedFrame, mReceivedFrame, 1);
			
    		// notify all the listeners
    		notifyListeners(new VideoFrame(mReceivedFrame, System.currentTimeMillis(), mCameraIndex));
    		
    		// simple timing
		    long tTimeNow = System.nanoTime();
    	   	mLastTime = tTimeNow;
    	}
	}
	
}
