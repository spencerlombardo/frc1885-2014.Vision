package edu.battlefield.vision;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class AerialAssist 
{	
	private static int MAX_KERNAL_LENGTH = 20;
	private static Scalar mMin = new Scalar(0, 0, 70);
	private static Scalar mMax = new Scalar(240, 240, 150);
	private static double heightRatio = .60;
	private static double widthRatio = .90;

	//Grab Library
	static {

		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		} catch(UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
	}
	public static void main(String [] args)
	{
		//Select Temporary Image
		String defaultPath = "C:/Users/spenc_000/Documents/School/Robotics/frc1885-2014.Vision";
		File selectedFile = new File(defaultPath + "/Test Images/SimplyRed.jpg");
		
		//Grab Image
		Mat imread = Highgui.imread(selectedFile.getAbsolutePath());
		displayImg(imread, "Original Image");		//Display Image
		
		//Create 
		Mat threshImg = new Mat(imread.rows(), imread.cols(), CvType.CV_8UC1);

		//Apply Gaussian Filter to Image
		lowFrequencyFilter(imread, threshImg);
		displayImg(threshImg, "Gaussian Filter");
		
		//Threshold Image
		//threshChannel(threshImg, threshImg, 2, 200);
		threshImg(threshImg, threshImg, mMin, mMax);
		displayImg(threshImg, "Threshold");
		
		//Detect Blobs on the Image
		Mat tmp = new Mat();
		threshImg.copyTo(tmp);
		List <Rect> blobs = detectBlobs(tmp, 10);
		
		//Draw the Blobs on the Original Image
		for(int i = 0; i < blobs.size(); i++)
		{
			Core.rectangle(imread, blobs.get(i).tl(), blobs.get(i).br(), new Scalar(0, 255, 255));
		}
		displayImg(imread, "Accepted Blobs");
	}
	public static float detectDepth(Mat pInput)
	{
		return 0.0f;
	}
	/**
	 * Apply a base threshold to the Image given the minimum values for the pixels and maximum value for the pixels
	 * @param pInput - Image for the Threshold to be applied.
	 * @param pOutput - Output Image.
	 * @param pMin - Minimum Range for Threshold.
	 * @param pMax - Maximum Range for Threshold.
	 */
	public static void threshImg(Mat pInput, Mat pOutput, Scalar pMin, Scalar pMax)
	{
		Core.inRange(pInput, pMin, pMax, pOutput);
	}
	/**
	 * Applys a binary threshold to a single channel of the image, returning the binary image of that single channel
	 * @param pInput - Image for the threshold to be applied to.
	 * @param pOutput - Output Image.
	 * @param pChannel - The single channel for the threshold to apply to.
	 * @param pMinThresh - The minimum value for the threshold 
	 */
	public static void threshChannel(Mat pInput, Mat pOutput, int pChannel, int pMinThresh)
	{
		//Extract the channel to a temporary image
		Mat grayImg = new Mat(pInput.rows(), pInput.cols(), CvType.CV_8UC1);
		Core.extractChannel(pInput, grayImg, pChannel);
	
		//Apply the single channel threshold
		Imgproc.threshold(grayImg, pOutput, pMinThresh, 255, Imgproc.THRESH_BINARY);
	}
	/***
	 * Apply a Gaussian Filter to a single channel of the image.
	 * @param pInput - Image for the filter to be applied to. 
	 * @param pOutput - Output image
	 * @param pChannel - Channel for the Gaussian filter to be applied to.
	 */
	public static void lowFrequencyFilterChannel(Mat pInput, Mat pOutput, int pChannel)
	{
		//Extract the Channel requested
		Mat grayImg = new Mat(pInput.rows(), pInput.cols(), CvType.CV_8UC1);
		Core.extractChannel(pInput, grayImg, pChannel);
		
		//Apply Filter
		lowFrequencyFilter(grayImg, pOutput);
	}
	/***
	 *  Apply the Gaussian Filter to the Image
	 * @param pInput - Input Image
	 * @param pOutput- Output Image
	 */
	public static void lowFrequencyFilter(Mat pInput, Mat pOutput)
	{
		//Apply the Gaussian Filter
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
		Mat hierarchy = new Mat();
		Imgproc.findContours(pInput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		List<Rect> boundRects = new ArrayList <Rect>();
		
		TreeMap<Double, MatOfPoint> orderedContour = new TreeMap<Double, MatOfPoint>();
		
		for(int i = 0; i < contours.size(); i++)
		{
			double tmp = contours.get(i).size().area();
			if(orderedContour.containsKey(tmp))
			{
				tmp += 0.01;
			}
			boundRects.add(Imgproc.boundingRect(contours.get(i)));
			orderedContour.put(tmp, contours.get(i));
		}
		if(checkRatio(orderedContour))
		{
			boundRects.add(Imgproc.boundingRect(getLargestContour(orderedContour)));
		}
		return boundRects;
	}
	
	public static MatOfPoint getLargestContour(TreeMap<Double, MatOfPoint> pMap)
	{
		MatOfPoint tmp = new MatOfPoint();
		int c = 0;
		for(Entry<Double, MatOfPoint> e: pMap.entrySet())
		{
			if(c == pMap.size()-1)
			{
				tmp = e.getValue();
			}
			c++;
		}
		return tmp;
	}
	
	public static boolean checkRatio(TreeMap<Double, MatOfPoint> pMap)
	{
		Set<Double> keys =  pMap.descendingKeySet();
		double [] slots = new double [3];
		
		int c = 0;
		for(Double key: keys)
		{
			switch(c)
			{
				case 0:			slots[c] = key;					break;
				case 1:			slots[c] = key;					break;
				case 2:			slots[c] = key;					break;
			}
			c++;
		}
		Rect largest = Imgproc.boundingRect(pMap.get(slots[0]));
		Rect second = Imgproc.boundingRect(pMap.get(slots[1]));
		Rect third = Imgproc.boundingRect(pMap.get(slots[2]));
		
		Rectangle largestRectangle = convertToRectangle(largest);
		Rectangle secondRectangle = convertToRectangle(second);
		Rectangle thirdRectangle = convertToRectangle(third);
		
		
		if(!largestRectangle.contains(secondRectangle) || !largestRectangle.contains(thirdRectangle)) {
			return false;
		}
		
		double wRatio = (second.width + third.width + 0.0)/largest.width;
		System.out.println("wRatio: " + wRatio);
		double hRatio1 = (second.height + 0.0)/largest.height;
		System.out.println("hRatio1: " + hRatio1);
		double hRatio2 = (third.height + 0.0)/largest.height;
		System.out.println("hRatio2: " + hRatio2);
		
		return wRatio > widthRatio && hRatio1 > heightRatio && hRatio2 > heightRatio;
	}
	
	private static Rectangle convertToRectangle(Rect largest) 
	{
		return new Rectangle(largest.x, largest.y, largest.width, largest.height);
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
