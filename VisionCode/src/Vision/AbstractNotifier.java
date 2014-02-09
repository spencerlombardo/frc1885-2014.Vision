package Vision;

import java.util.ArrayList;
import java.util.List;

import edu.battlefield.vision.FrameListener;
import edu.battlefield.vision.Observable;
import edu.battlefield.vision.VideoFrame;

public abstract class AbstractNotifier implements Observable
{
	protected List<FrameListener> mListeners = new ArrayList<FrameListener>();
	
	public void notifyListeners(final VideoFrame aTimeStampedMat)
	{
		for(FrameListener tFrameListener: mListeners)
		{
			tFrameListener.newFrame(aTimeStampedMat);
		}
	}
	
	public void add(FrameListener pListener)
	{
		mListeners.add(pListener);
	}
	
	public void remove(FrameListener pListener)
	{
		mListeners.remove(pListener);
	}
}
