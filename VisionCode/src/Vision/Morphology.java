package Vision;

import org.opencv.core.Mat;

public class Morphology extends AbstractNotifier implements FrameListener
{
	
	public Morphology()
	{
		super();
	}
	
	public void applyMorph(Mat pInput)
	{
		ImageProcessing.morph(pInput);
		
		notifyListeners(new VideoFrame(pInput, System.currentTimeMillis()));
	}

	public void newFrame(VideoFrame pVideoFrame) 
	{
		applyMorph(pVideoFrame.getMat());	
	}
}
