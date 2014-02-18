package Vision;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Calibration
{

	private static final int minBlobArea = 200;
	private static final double calibrationX = 216;
	private static final double cameraOffset = 12;
	private static final double calibrationY = 101.25 - cameraOffset;
	private double calArea;
	private double calHeight;
	private double calWidth;
	private double calHeightInches;
	private double inchPerPixel;
	private double calPixelCount;
	private double calAngle;
	private double calDistance;
	private boolean isCalibrated;
	
	private Mat calMat;
	
	private static Calibration myCal;
	
	public static Calibration getInstance() {
		if(myCal == null) {
			myCal = new Calibration();
		}
		return myCal;
	}
	
	private Calibration()
	{
		isCalibrated = false;
	}

	public void doCalibration(Mat pMat)
	{
		calMat = pMat;
		
		preProcess(calMat, calMat);
		
		Rect blob = calPixelCount();
		ImageProcessing.displayImg(calMat, "Le Blobs");
		inchPerPixel = calibrationY/calPixelCount;
		getCalibration(blob);
		if(blob.area() > this.minBlobArea)
		{
			isCalibrated = true;
		}
		else
		{
			System.out.println("Bad Image. Still Calibrating");
		}
	}
	private void preProcess(Mat pInput, Mat pOutput)
	{
		ArrayList<Mat> hsvPlanes = new ArrayList<Mat>();
		
		Imgproc.cvtColor(pInput, pInput, Imgproc.COLOR_BGR2HSV);
		Core.split(pInput, hsvPlanes);
		
		Mat thresh = ImageProcessing.thresholdColor(hsvPlanes.get(0), 169, 179);
		ImageProcessing.displayImg(thresh, "LE THRESH");
		Mat morph = ImageProcessing.morph(thresh);
		if(morph.empty())
		{
			morph = thresh;
		}
		ImageProcessing.displayImg(morph, "Le Morph");
		morph.copyTo(pOutput);
	}
	private Rect calPixelCount()
	{
		Rect tmp = ImageProcessing.cannyAndHough(calMat, calMat, 1, 75, 1, Math.PI/180);
		
		Core.rectangle(calMat, tmp.tl(), tmp.br(), new Scalar(0, 0, 255));
		calPixelCount = (calMat.height() - ((tmp.br().y + tmp.tl().y)/2));
		calArea = tmp.height*tmp.width;
		calHeight = tmp.height;
		calWidth = tmp.width;
		
		return tmp;
	}
	private void getCalibration(Rect pRect)
	{
		calHeightInches = (calMat.height() - (pRect.br().y +pRect.tl().y)/2)*inchPerPixel;
			
		calAngle = Math.atan(calHeightInches/calibrationX);
			
		calDistance = calHeightInches/Math.tan(calAngle);
		
	}
	public double getInchPerPixel() 
	{
		return inchPerPixel;
	}
	public double getCalWidth() 
	{
		return calWidth;
	}
	public double getCalHeight() 
	{
		return calHeight;
	}
	public double getCalArea() 
	{
		return calArea;
	}
	public double getCalibrationY() 
	{
		return calibrationY;
	}
	public double getCalibrationX() 
	{
		return calibrationX;
	}
	public double getCalPixelCount() {
		return calPixelCount;
	}
	public Mat getCalMat() {
		return calMat;
	}
	public double getCalAngle() 
	{
		return calAngle;
	}
	public double getCalYInches() 
	{
		return calHeightInches;
	}
	public double getCalDistance()
	{
		return calDistance;
	}
	public boolean isCalibrated() 
	{
		return isCalibrated;
	}
	public String toString() 
	{
		return "Calibration [calArea=" + calArea + ", calHeight=" + calHeight
				+ ", calWidth=" + calWidth + ", calYInches=" + calHeightInches
				+ ", inchPerPixel=" + inchPerPixel + ", calPixelCount="
				+ calPixelCount + ", calAngle=" + calAngle + ", calDistance="
				+ calDistance + ", isCalibrated=" + isCalibrated + ", calMat="
				+ calMat + "]";
	}
}
