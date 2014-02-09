package Vision;

import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import edu.battlefield.vision.AbstractNotifier;
import edu.battlefield.vision.AerialAssist;
import edu.battlefield.vision.FrameListener;
import edu.battlefield.vision.VideoFrame;

public class DetectAuto extends AbstractNotifier implements FrameListener
{
	public DetectAuto()
	{
		super();
	}

	public void detectBlobs(Mat pInput)
	{
		List <Rect> blobs = AerialAssist.detectAutonomous(pInput, 10);

		//Draw the Blobs on the Original Image
		for(int i = 0; i < blobs.size(); i++)
		{
			Core.rectangle(pInput, blobs.get(i).tl(), blobs.get(i).br(), new Scalar(255, 255, 255));
			System.out.println("#" + i + ": " + blobs.get(i).tl());
		}
		notifyListeners(new VideoFrame(pInput, System.currentTimeMillis(), 0, 0, blobs));
	}
	
	@Override
	public void newFrame(VideoFrame pVideoFrame) 
	{
		detectBlobs(pVideoFrame.getMat());
	}
	
}
