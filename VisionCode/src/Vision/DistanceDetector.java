package Vision;

import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class DistanceDetector extends AbstractNotifier implements FrameListener
{
	public DistanceDetector()
	{
		super();
	}
	
	public void detectDistance(Mat pInput, Rect pRect)
	{
		Calibration c = Calibration.getInstance();
		
		double midHeight = (pInput.height()-((pRect.br().y + pRect.tl().y)/2));
		double calHeightInches = midHeight * c.getInchPerPixel();
		double angle = Math.atan(calHeightInches/c.getCalibrationX());
		double calDistance = c.getCalibrationY()/Math.tan(angle);
		
		notifyListeners(new VideoFrame(pInput, System.currentTimeMillis(), pRect, calHeightInches, angle, calDistance));
	}

	@Override
	public void newFrame(VideoFrame pVideoFrame)
	{
		detectDistance(pVideoFrame.getMat(), pVideoFrame.getBlobs());
	}
	
}
