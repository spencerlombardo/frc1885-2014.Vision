package edu.battlefield.vision;

import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class DetectBlobs extends AbstractNotifier implements FrameListener
{
	public DetectBlobs()
	{
		super();
	}

	public void detectBlobs(Mat pInput)
	{
		List <Rect> blobs = AerialAssist.detectBlobs(pInput, 10);

		//Draw the Blobs on the Original Image
		for(int i = 0; i < blobs.size(); i++)
		{
			Core.rectangle(pInput, blobs.get(i).tl(), blobs.get(i).br(), new Scalar(0, 255, 255));
			System.out.println("#" + i + ": " + blobs.get(i).tl());
		}
		AerialAssist.displayImg(pInput, "Image");
		notifyListeners(new VideoFrame(pInput, System.currentTimeMillis()));
	}
	
	@Override
	public void newFrame(VideoFrame pVideoFrame) 
	{
		detectBlobs(pVideoFrame.getMat());
	}
	
}
