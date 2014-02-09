package Vision;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import edu.battlefield.vision.AbstractNotifier;
import edu.battlefield.vision.AerialAssist;
import edu.battlefield.vision.FrameListener;
import edu.battlefield.vision.VideoFrame;

public class ExperimentLight extends AbstractNotifier implements FrameListener
{
	private int mColor;
	public ExperimentLight(int pColor)
	{
		super();
		mColor = pColor;
	}
	
	public void onlyOneColor(Mat pInput)
	{
		AerialAssist.cancelLight(pInput, pInput);
		
		ArrayList<Mat> channels = new ArrayList<Mat>();
		channels.add(new Mat());
		channels.add(new Mat());
		channels.add(new Mat());
		
		//Extract channels
		Core.extractChannel(pInput, channels.get(0), 0);
		Core.extractChannel(pInput, channels.get(1), 1);
		Core.extractChannel(pInput, channels.get(2), 2);
		
		if(mColor == 1)
		{
			channels.set(0, Mat.zeros(channels.get(0).rows(), channels.get(0).cols(), channels.get(0).depth()));
		}
		else if(mColor == 2)
		{
			channels.set(2, Mat.zeros(channels.get(0).rows(), channels.get(0).cols(), channels.get(0).depth()));
		}
		
		//Merge the channels into the output Matrix
		Core.merge(channels, pInput);
	}
	
	public void newFrame(VideoFrame pVideoFrame) 
	{
		onlyOneColor(pVideoFrame.getMat());
	}
}
