package Vision;

import Vision.FrameListener;

public interface Observable 
{
	public void add(FrameListener pListener);
	public void remove(FrameListener pListener);
}
