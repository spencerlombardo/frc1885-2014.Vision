package Vision;

import java.awt.image.BufferedImage;

import edu.battlefield.vision.AbstractNotifier;
import edu.battlefield.vision.AerialAssist;
import edu.battlefield.vision.FrameListener;
import edu.battlefield.vision.VideoFrame;

public class ImageUpdater extends AbstractNotifier implements FrameListener
{
	public ImageUpdater()
	{
		super();
	}
	public void updateFrame(VideoFrame pInput)
	{
		//Convert to Buffered Image
		BufferedImage tmp = (BufferedImage) AerialAssist.toBufferedImage(pInput.getMat());
		
		//Anything else you want done
		
		//Update the image
		
		
	}
	//Place in here what should be done continuously
	public void newFrame(VideoFrame pVideoFrame) 
	{
		updateFrame(pVideoFrame);
	}
	
}
