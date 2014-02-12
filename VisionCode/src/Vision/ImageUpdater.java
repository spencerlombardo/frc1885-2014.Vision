package Vision;

import ilite.util.lang.Delegator;

import java.awt.image.BufferedImage;

public class ImageUpdater extends Delegator<BufferedImage> implements FrameListener
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
		
		//Notify subscribers
    update(tmp);
		
		
	}
	//Place in here what should be done continuously
	public void newFrame(VideoFrame pVideoFrame) 
	{
		updateFrame(pVideoFrame);
	}
	
}
