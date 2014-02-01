package edu.battlefield.vision;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class LightCancellization extends AbstractNotifier implements FrameListener
{
	
	public LightCancellization()
	{
		super();
	}
	
	public void cancelLight(Mat pInput)
	{
		//Switch to YUV Space
		Mat yuv = new Mat();
		Imgproc.cvtColor(pInput, yuv, Imgproc.COLOR_BGR2YCrCb);
		
		//Zero Out the light Space, Y
		AerialAssist.cancelLight(yuv, yuv);
		
		//Convert Back to BGR Space
		Imgproc.cvtColor(yuv, pInput, Imgproc.COLOR_YCrCb2BGR);
		
		notifyListeners(new VideoFrame(pInput, System.currentTimeMillis()));
		
	}
	
	@Override
	public void newFrame(VideoFrame pVideoFrame) 
	{
		cancelLight(pVideoFrame.getMat());
	}
}
