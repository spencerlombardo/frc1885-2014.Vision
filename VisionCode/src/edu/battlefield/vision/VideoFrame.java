package edu.battlefield.vision;

import org.opencv.core.Mat;

public class VideoFrame 
{
	private final Mat mMat;
	
	private final long mTimeInMicroSeconds;
	
	private final int mCameraIndex;
	
	public VideoFrame(Mat pMat, long pTime, int pCameraIndex) 
	{
		mMat = pMat.clone();
		mTimeInMicroSeconds = pTime;
		mCameraIndex = pCameraIndex;
	}

	public VideoFrame(Mat pMat, long pTime) 
	{
		mMat = pMat.clone();
		mTimeInMicroSeconds = pTime;
		mCameraIndex = 0;
	}

	public final long getTimeInMicroSeconds()
	{
		return mTimeInMicroSeconds;
	}
	
	public final Mat getMat()
	{
		return mMat;
	}
	
	public final int getCameraIndex()
	{
		return mCameraIndex;
	}
}
