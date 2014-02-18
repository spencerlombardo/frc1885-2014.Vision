package Vision;

import org.opencv.core.Mat;

public class ThresholdImg extends AbstractNotifier implements FrameListener
{
	private int mMin;
	private int mMax;
	
	public ThresholdImg(int pMin, int pMax)
	{
		super();
		mMin = pMin;
		mMax = pMax;
	}

	public void applyThreshold(Mat pInput)
	{
		Mat threshold = ImageProcessing.applyThreshold(pInput, mMin, mMax);
		
		notifyListeners(new VideoFrame(threshold, System.currentTimeMillis()));
	}
	
	@Override
	public void newFrame(VideoFrame pVideoFrame) 
	{
		applyThreshold(pVideoFrame.getMat());
	}
}
