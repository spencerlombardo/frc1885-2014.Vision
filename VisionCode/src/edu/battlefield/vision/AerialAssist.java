package edu.battlefield.vision;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class AerialAssist 
{
	private static boolean isLibLoaded = false;
	
	private static int MAX_KERNAL_LENGTH = 15;
	private static Scalar mMin = new Scalar(0, 190, 0);
	private static Scalar mMax = new Scalar(200, 240, 200);

	static {

		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			isLibLoaded = true;
		} catch(UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
	}
	public static void main(String [] args)
	{
		File selectedFile = new File("C:/Users/spenc_000/Documents/School/Robotics/frc1885-2014.Vision/2013SampleImage.jpg");
		
		Mat imread = Highgui.imread(selectedFile.getAbsolutePath());
		displayImg(imread);
		
		Mat newImage = new Mat(imread.rows(), imread.cols(), CvType.CV_8UC1);
		
		lowFrequencyFilter(imread, newImage);
		
		//threshChannel(newImage, newImage, 0, 150);
		threshImg(newImage, newImage, mMin, mMax);
		
		Mat tmp = new Mat();
		newImage.copyTo(tmp);
		List <Rect> blobs = detectBlobs(tmp, 10);
		for(int i = 0; i < blobs.size(); i++)
		{
			Core.rectangle(imread, blobs.get(i).tl(), blobs.get(i).br(), new Scalar(0, 255, 255));
		}
		displayImg(imread);
	}
	public static void threshImg(Mat pInput, Mat pOutput, Scalar pMin, Scalar pMax)
	{
		Core.inRange(pInput, pMin, pMax, pOutput);
	}
	public static void threshChannel(Mat pInput, Mat pOutput, int pChannel, int pMaxThresh)
	{
		Mat grayImg = new Mat(pInput.rows(), pInput.cols(), CvType.CV_8UC1);
		Core.extractChannel(pInput, grayImg, pChannel);
		
		Imgproc.threshold(grayImg, pOutput, pMaxThresh, 255, Imgproc.THRESH_BINARY);
	}
	public static void applyThresh(Mat pInput, Mat pOutput)
	{
		for(int row = 0; row < pInput.height(); row++)
		{
			for(int col = 0; col < pInput.width(); col++)
			{
				double [] curPixel = pInput.get( row, col );
				try {
				pOutput.put(row, col, checkDiff(curPixel));
				} catch(NullPointerException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void lowFrequencyFilterChannel(Mat pInput, Mat pOutput, int pChannel)
	{
		Mat grayImg = new Mat(pInput.rows(), pInput.cols(), CvType.CV_8UC1);
		Core.extractChannel(pInput, grayImg, pChannel);
		lowFrequencyFilter(grayImg, pOutput);
		displayImg(pOutput);
	}
	public static void lowFrequencyFilter(Mat pInput, Mat pOutput)
	{
		for ( int i = 1; i < MAX_KERNAL_LENGTH; i = i + 2 )
	    { 
			Imgproc.GaussianBlur( pInput, pOutput, new Size( i, i ), 0, 0 );
	    }	
	}
	private static double [] checkDiff(double [] pInput)
	{
		double[] ans = pInput;
		double min = 100;
		double max = 200;
		if(min < pInput[2] || max > pInput[2])
		{
			ans[2] = 0;
		}
		return ans;
	}
	public static void canny(Mat pInput, Mat pOutput, int pMin, int pMax)
	{
		Imgproc.Canny(pInput, pOutput, pMin, pMax);
	}
	public static List <Rect> detectBlobs(Mat pInput, int pMaxArea)
	{
		List <MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(pInput, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		List<Rect> boundRects = new ArrayList <Rect>();
		
		for(int i = 0; i < contours.size(); i++)
		{
			if(contours.get(i).rows() < pMaxArea)
			{
				continue;
			}
			boundRects.add(Imgproc.boundingRect(contours.get(i)));
			
		}
		return boundRects;
	}
	public static void displayImg(Mat pInput) {
		displayImg(pInput, "");
	}
	public static void displayImg(Mat pInput, String pTitle)
	{ 
		final Image bufferedImage = toBufferedImage(pInput);
		ImageIcon anIcon = new ImageIcon(bufferedImage);
		JFrame aFrame = new JFrame(pTitle);
		aFrame.setContentPane(new JLabel(anIcon));
		aFrame.pack();
		aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		aFrame.setVisible(true);
	}
	public static Image toBufferedImage(Mat m){
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( m.channels() > 1 ) {
			Mat m2 = new Mat();
			Imgproc.cvtColor(m,m2,Imgproc.COLOR_BGR2RGB);
			type = BufferedImage.TYPE_3BYTE_BGR;
			m = m2;
		}
		byte [] b = new byte[m.channels()*m.cols()*m.rows()];
		m.get(0,0,b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
		image.getRaster().setDataElements(0, 0, m.cols(),m.rows(), b);
		return image;
	}
}
