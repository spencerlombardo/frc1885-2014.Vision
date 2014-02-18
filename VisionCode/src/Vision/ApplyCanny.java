package Vision;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class ApplyCanny extends AbstractNotifier implements FrameListener
{
	private double mRho;
	private double mTheta;
	private int mCannyLow;
	private int mCannyHigh;
	
	
	public ApplyCanny()
	{
		this(0, 0, 0.0, 0.0);
	}

	public ApplyCanny(int pCannyLow, int pCannyHigh, double pRho, double pTheta) 
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
		Rect tmp = ImageProcessing.cannyAndHough(pInput, lines, mCannyLow, mCannyHigh, mRho, mTheta);
		
		Core.rectangle(lines, tmp.tl(), tmp.br(), new Scalar(0,0,255));
		
		notifyListeners(new VideoFrame(lines, System.currentTimeMillis(), tmp));
	}

	@Override
	public void newFrame(VideoFrame pVideoFrame) 
	{
		detectBlobs(pVideoFrame.getMat());
	}
}
