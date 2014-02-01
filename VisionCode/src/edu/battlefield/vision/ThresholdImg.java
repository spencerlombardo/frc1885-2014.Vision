package edu.battlefield.vision;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class ThresholdImg extends AbstractNotifier implements FrameListener
{
	//Blue Thresholds
	private static Scalar mMinB = new Scalar(90, 0, 0);
	private static Scalar mMaxB = new Scalar(240, 240, 240);
	//Red Thresholds
	private static Scalar mMinR = new Scalar(0, 0, 70);
	private static Scalar mMaxR = new Scalar(240, 240, 240);
	
	public ThresholdImg()
	{
		super();
	}

	public void applyThreshold(Mat pInput)
	{
		AerialAssist.threshImg(pInput, pInput, mMinB, mMaxB);
		
		notifyListeners(new VideoFrame(pInput, System.currentTimeMillis()));
	}
	
	@Override
	public void newFrame(VideoFrame pVideoFrame) 
	{
		applyThreshold(pVideoFrame.getMat());
	}
}
