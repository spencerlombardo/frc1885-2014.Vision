package Vision;

import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class VideoFrame 
{
	private final Mat mMat;
	
	private final long mTimeInMicroSeconds;
	
	private final int mCameraIndex;

	private final double distance;
	
	private final List<Rect> blobs;
	
	public VideoFrame(Mat pMat, long pTime, int pCameraIndex, double  pDistance, List<Rect> pBlobs) 
	{
		mMat = pMat.clone();
		mTimeInMicroSeconds = pTime;
		mCameraIndex = pCameraIndex;
		distance = pDistance;
		blobs = pBlobs;
	}
	
	public VideoFrame(Mat pMat, long pTime, List<Rect> pBlobs) 
	{
		this(pMat, pTime, 0, 0.0, pBlobs);
	}
	
	public VideoFrame(Mat pMat, long pTime, int pCameraIndex, double  pDistance) 
	{
		mMat = pMat.clone();
		mTimeInMicroSeconds = pTime;
		mCameraIndex = pCameraIndex;
		distance = pDistance;
		blobs = null;
	}

	public VideoFrame(Mat pMat, long pTime) 
	{
		this(pMat, pTime, 0, 0.0);
		
	}
	
	public VideoFrame(Mat pMat)
	{
		this(pMat, 0, 0, 0.0);
	}
	
	public VideoFrame()
	{
		this(new Mat(), 0, 0, 0.0);
	}
	
	public VideoFrame(Mat pMat, double pDistance)
	{
		this(pMat, 0, 0, pDistance);
	}

	public VideoFrame(Mat pMat, long pTime, double pDistance, List<Rect> pBlobs) 
	{
		this(pMat, pTime, 0, pDistance, pBlobs);
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

	public double getDistance() 
	{
		return distance;
	}

	public List<Rect> getBlobs() 
	{
		return blobs;
	}
}
