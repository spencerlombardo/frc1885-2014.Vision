package Vision;

import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class DistanceDetector extends AbstractNotifier implements FrameListener
{
	public DistanceDetector()
	{
		super();
	}
	
	public void detectDistance(Mat pInput, List<Rect> pBlobs)
	{
		
		notifyListeners(new VideoFrame(pInput, System.currentTimeMillis(), pBlobs));
	}

	@Override
	public void newFrame(VideoFrame pVideoFrame)
	{
		
	}
	
}
