package Vision;

public class printStuffs extends AbstractNotifier implements FrameListener 
{
	public printStuffs()
	{
		super();
	}
	
	public void debug(VideoFrame pInput)
	{
		ImageProcessing.displayImg(pInput.getMat(), "Final Image");
		System.out.println("Calibration Values: " + Calibration.getInstance().toString());
		System.out.println("Calculated Values: " + pInput.toString());
	}
	
	@Override
	public void newFrame(VideoFrame pVideoFrame) 
	{
		debug(pVideoFrame);
	}
	
}
