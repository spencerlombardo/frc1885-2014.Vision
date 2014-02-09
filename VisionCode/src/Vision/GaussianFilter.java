package Vision;

import org.opencv.core.Mat;

import edu.battlefield.vision.AbstractNotifier;
import edu.battlefield.vision.AerialAssist;
import edu.battlefield.vision.FrameListener;
import edu.battlefield.vision.VideoFrame;

public class GaussianFilter extends AbstractNotifier implements FrameListener
{
	public GaussianFilter()
	{
		super();
	}
	
	public void applyGaussian(Mat pInput)
	{
		//Apply Gaussian Filter to Image
		AerialAssist.lowFrequencyFilter(pInput, pInput);
		
		notifyListeners(new VideoFrame(pInput, System.currentTimeMillis()));
	}
	
	@Override
	public void newFrame(VideoFrame pVideoFrame) 
	{
		applyGaussian(pVideoFrame.getMat());		
	}

}
