package Vision;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class CancelColor extends AbstractNotifier implements FrameListener
{
	private int mCOI;
	public CancelColor(int pCOI)
	{
		super();
		mCOI = pCOI;
	}
	
	public void onlyOneColor(Mat pInput)
	{
		AerialAssist.zeroOutChannel(pInput, pInput, 0);
		
		ArrayList<Mat> channels = new ArrayList<Mat>();
		channels.add(new Mat());
		channels.add(new Mat());
		channels.add(new Mat());
		
		//Extract channels
		Core.extractChannel(pInput, channels.get(0), 0);
		Core.extractChannel(pInput, channels.get(1), 1);
		Core.extractChannel(pInput, channels.get(2), 2);
		
		channels.set(mCOI, Mat.zeros(channels.get(0).rows(), channels.get(0).cols(), channels.get(0).depth()));
		
		//Merge the channels into the output Matrix
		Core.merge(channels, pInput);
		
		notifyListeners(new VideoFrame(pInput, System.currentTimeMillis()));
	}
	
	public void newFrame(VideoFrame pVideoFrame) 
	{
		onlyOneColor(pVideoFrame.getMat());
	}
}
