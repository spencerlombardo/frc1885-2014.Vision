package Vision;

import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class VideoFrame 
{
	private final Mat mMat;
	
	private final long mTimeInMicroSeconds;
	
	private final int mCameraIndex;
	
	private final Rect blob;
	
	private final double calHeightInches;
	
	private final double calDistance;
	
	private final double calAngle;
	
	public VideoFrame(Mat pMat, long pTime, int pCameraIndex, Rect pBlob, double pCalHeightInches, double pCalDistance, double pCalAngle) 
	{
		mMat = pMat.clone();
		mTimeInMicroSeconds = pTime;
		mCameraIndex = pCameraIndex;
		blob = pBlob;
		calDistance = pCalDistance;
		calHeightInches = pCalHeightInches;
		calAngle = pCalAngle;
	}
	
	public VideoFrame(Mat pMat, long pTime, Rect pBlob) 
	{
		this(pMat, pTime, 0, pBlob, 0.0, 0.0, 0.0);
	}
	
	public VideoFrame(Mat pMat, long pTime, int pCameraIndex) 
	{
		this(pMat, pTime, pCameraIndex, null, 0.0, 0.0, 0.0);
	}

	public VideoFrame(Mat pMat, long pTime) 
	{
		this(pMat, pTime, 0, null, 0.0, 0.0, 0.0);
	}
	
	public VideoFrame(Mat pMat)
	{
		this(pMat, 0, 0, null, 0.0, 0.0, 0.0);
	}
	
	public VideoFrame()
	{
		this(new Mat(), 0, 0, null, 0.0, 0.0, 0.0);
	}
	
	public VideoFrame(Mat pMat, long pTime, Rect pBlob, double pCalHeightInches, double pAngle, double pCalDistance)
	{
		this(pMat, pTime, 0, pBlob, pCalHeightInches, pAngle, pCalDistance);
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
	
	public Rect getBlobs() 
	{
		return blob;
	}

	@Override
	public String toString() 
	{
		return "VideoFrame [mTimeInMicroSeconds=" + mTimeInMicroSeconds
				+ ", calHeightInches=" + calHeightInches + ", calDistance="
				+ calDistance + ", calAngle=" + calAngle + "]";
	}
	
}
