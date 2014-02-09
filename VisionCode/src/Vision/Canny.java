package Vision;

import org.opencv.core.Mat;

import edu.battlefield.vision.AbstractNotifier;
import edu.battlefield.vision.AerialAssist;
import edu.battlefield.vision.FrameListener;
import edu.battlefield.vision.VideoFrame;

public class Canny extends AbstractNotifier implements FrameListener
{
	private double mRho;
	private double mTheta;
	private int mCannyLow;
	private int mCannyHigh;
	
	
	public Canny()
	{
		this(0, 0, 0.0, 0.0);
	}

	public Canny(int pCannyLow, int pCannyHigh, double pRho, double pTheta) 
	{
		super();
		mRho = pRho;
		mTheta = pTheta;
		mCannyLow = pCannyLow;
		mCannyHigh = pCannyHigh;
	}
	
	public void detectBlobs(Mat pInput)
	{
		Mat lines = new Mat();
		AerialAssist.detectBlobs(pInput, lines, mCannyLow, mCannyHigh, mRho, mTheta);
		AerialAssist.displayImg(lines, "Lines");
		
		notifyListeners(new VideoFrame(lines, System.currentTimeMillis()));
	}

	@Override
	public void newFrame(VideoFrame pVideoFrame) 
	{
		detectBlobs(pVideoFrame.getMat());
	}
}
