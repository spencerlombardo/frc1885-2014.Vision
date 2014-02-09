package Vision;

import edu.battlefield.vision.FrameListener;

public interface Observable 
{
	public void add(FrameListener pListener);
	public void remove(FrameListener pListener);
}
