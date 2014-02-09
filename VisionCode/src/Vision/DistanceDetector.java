package Vision;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import edu.battlefield.vision.AbstractNotifier;
import edu.battlefield.vision.AerialAssist;
import edu.battlefield.vision.FrameListener;
import edu.battlefield.vision.VideoFrame;

public class DistanceDetector extends AbstractNotifier implements FrameListener
{
	public DistanceDetector()
	{
		super();
	}
	
	public void detectDistance(Mat pInput, List<Rect> pBlobs)
	{
		double distance = AerialAssist.distance(pInput, pBlobs);
		
		notifyListeners(new VideoFrame(pInput, System.currentTimeMillis(), distance, pBlobs));
	}

	@Override
	public void newFrame(VideoFrame pVideoFrame)
	{
		
	}
	
}
