package Vision;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class AerialAssist 
{	
	private static int MAX_KERNAL_LENGTH = 4;
	
	private static double heightRatio = .55;
	private static double widthRatio = .75;
	
	public static void loadLib() {
		
	}

	//Grab Library
	static {
		try {
			System.out.println("Old lib path: " + System.getProperty("java.library.path"));
			String homeDir = System.getProperty("user.home");
			String libPath =homeDir+"/opencv/build/java/x64;"+System.getProperty("java.library.path");
			System.setProperty( "java.library.path", libPath);

			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );


			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		} catch(UnsatisfiedLinkError e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String [] args)
	{
		//Select Temporary Image
		String defaultPath = "C:/Users/spenc_000/Documents/School/Robotics/frc1885-2014.Vision";
		File selectedFile = new File(defaultPath + "/Test Images/2014SampleImagePartialLightUp.png");

		//Set up the camera
		//First parameter is the Web Server Address
		//IP Address is for the camera
		//Second is the username. Always root
		//Third is the password. the password for this camera is team1885

		long start_time = System.currentTimeMillis();
		long grabImage = 0;
		long convertYUV = 0;
		long zerolight = 0;
		long convertBGR = 0;
		
		System.out.println("Start Time: " + start_time);
		
		for(int c = 0; c < 5; c++)
		{
			start_time = System.currentTimeMillis();
			System.out.println("Start Time: " + start_time);
			//Grab Image
			Mat imread = Highgui.imread(selectedFile.getAbsolutePath());
			grabImage = System.currentTimeMillis();
			displayImg(imread, "Original Image");		//Display Image



			//Switch to YUV Space
			Mat yuv = new Mat();
			Imgproc.cvtColor(imread, yuv, Imgproc.COLOR_BGR2YCrCb);
			convertYUV = System.currentTimeMillis();
			
			ArrayList<Mat> hsvPlanes = new ArrayList<Mat>();
			
			Mat tmpMat = imread.clone();
			Mat tmpMat1 = new Mat();
			
			Imgproc.cvtColor(tmpMat, tmpMat1, Imgproc.COLOR_BGR2HSV);
			Core.split(tmpMat1, hsvPlanes);
			
			Mat t = thresholdColor(hsvPlanes.get(0), 174, 180);
			displayImg(t, "Thresholded");
			
			Mat b = morph(t);
			displayImg(b, "Morph");
			
			Mat lines = new Mat();
			
			cannyAndHough(b, lines, 1, 75, 1, Math.PI/180);
			displayImg(lines, "canny and ohugh");
//			
//			//Zero Out the light Space, Y
//			zeroOutChannel(yuv, yuv, 0);
//			zerolight = System.currentTimeMillis();
//			//Convert Back to BGR Space
//			Imgproc.cvtColor(yuv, imread, Imgproc.COLOR_YCrCb2BGR);
//			convertBGR = System.currentTimeMillis();
//			displayImg(imread, "NO Light BGR");
			
//			lowFrequencyFilter(imread, imread);
//			//displayImg(threshImg, "Gaussian Filter");
			
//			
//			Mat lines = new Mat();
//			
//			Mat thresh = new Mat();
//			
//			
//			threshImg(imread, thresh, new Scalar( 0, 0, 50), new Scalar(255, 255, 255));
//			displayImg(thresh, "Threshold");
//			
//			cannyAndHough(thresh, lines, 1, 75, 1, Math.PI/180);
//			displayImg(lines, "canny and ohugh");
			
//			//Create 
//			Mat threshImg = new Mat(imread.rows(), imread.cols(), CvType.CV_8UC1);
//
//			//Apply Gaussian Filter to Image
//			lowFrequencyFilter(imread, threshImg);
//			//displayImg(threshImg, "Gaussian Filter");
//			gaussian = System.currentTimeMillis();
//			
//			//Threshold Image
//			//threshChannel(threshImg, threshImg, 2, 200);
//			threshImg(threshImg, threshImg, mMinR, mMaxR);
//			//displayImg(threshImg, "Threshold");
//			threshold = System.currentTimeMillis();
//
//			//Detect Blobs on the Image
//			Mat tmp = new Mat();
//			threshImg.copyTo(tmp);
//			List <Rect> blobs = detectAutonomous(tmp, 10);
//			detectblob = System.currentTimeMillis();
//
//			//Draw the Blobs on the Original Image
//			for(int i = 0; i < blobs.size(); i++)
//			{
//				Core.rectangle(imread, blobs.get(i).tl(), blobs.get(i).br(), new Scalar(255, 255, 255));
//				System.out.println("#" + i + ": " + blobs.get(i).tl());
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				displayImg(imread, "With Blobs");
//			}
			System.out.println("Grabbing Image: " + (grabImage - start_time));
			System.out.println("Convert to YUV: " + (convertYUV - grabImage));
			System.out.println("Zero out Light: " + (zerolight - convertYUV));
			System.out.println("Convert to BGR: " + (convertBGR - zerolight));			
		}

	}
	public static Mat morph(Mat pMat)
	{
		int mErosionSize = 2;
		Mat mElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2*mErosionSize + 1, 2*mErosionSize +1), new Point(mErosionSize, mErosionSize));
		
		Mat tmp3 = pMat.clone();
		Mat tmp2 = new Mat();
		
		Imgproc.erode(tmp3, tmp2, mElement);
		Imgproc.dilate(tmp2, tmp3, mElement);
		
		for(int i = 0; i < 500; i++)
		{
			Imgproc.erode(tmp3, tmp2, mElement);
			Imgproc.dilate(tmp2, tmp3, mElement);
		}
		return tmp3;
	}
	public static Mat thresholdColor(final Mat aChannel, final int aLow, final int aHigh)
	{
		Mat tTemp1 = new Mat();
		Mat tTemp2 = new Mat();
		// BLUE
		if (aLow < aHigh)
		{
			Imgproc.threshold(aChannel, tTemp1, aHigh, 256, Imgproc.THRESH_TOZERO_INV);
			Imgproc.threshold(tTemp1, tTemp2, aLow, 256, Imgproc.THRESH_BINARY);
		}
		else
		{
			Mat A = thresholdColor(aChannel,0,aHigh);
			Mat B = thresholdColor(aChannel,aLow,180);
			Core.add(A, B, tTemp2);
		}
		return tTemp2;
	}
	public static void zeroOutChannel(Mat pInput, Mat pOutput, int coi)
	{
		//Create Channels
		ArrayList<Mat> channels = new ArrayList<Mat>();
		channels.add(new Mat());
		channels.add(new Mat());
		channels.add(new Mat());
				
		//Extract channels
		Core.extractChannel(pInput, channels.get(0), 0);
		Core.extractChannel(pInput, channels.get(1), 1);
		Core.extractChannel(pInput, channels.get(2), 2);
				
		//Change y to 0
		channels.set(coi, Mat.zeros(channels.get(coi).rows(), channels.get(coi).cols(), channels.get(coi).depth()));
		
		
		//Merge the channels into the output Matrix
		Core.merge(channels, pOutput);
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
	public static void cannyAndHough(Mat pInput, Mat pOutput, int pLowThresh, int pHighThresh, double pRho, double pTheta)
	{
		Imgproc.Canny(pInput, pInput, pLowThresh, pHighThresh);
		Imgproc.cvtColor(pInput, pOutput, Imgproc.COLOR_GRAY2BGR);
		Mat lines = new Mat();
		Imgproc.HoughLinesP(pInput, lines, pRho, pTheta, 30);
		ArrayList<Double> yVals = new ArrayList<Double>();
		displayImg(pInput, "Canny");
		
		List<Point> points = new ArrayList<Point>();
		drawLines(pInput, pOutput, lines, yVals, points);
		
		for(int i = 0; i < points.size(); i++)
		{
			Core.circle(pOutput, points.get(i), 2, new Scalar(255, 0, 255), -1, 8, 0);
		}
		
		sortArray(yVals);
		
		changeIndices(yVals);
		
		double midpoint = averageValues(yVals);
		Core.line(pOutput, new Point(0, midpoint), new Point(320, midpoint), new Scalar(255, 0, 255), 2);
	}
	private static void changeIndices(ArrayList<Double> yVals) 
	{
		int [] tmp = findIndices(yVals);

		for(int i = 0; i < yVals.size(); i++)
		{
			if(i <= tmp[0])
			{
				yVals.set(i, yVals.get(tmp[0]));
			}
			if(i >= tmp[1])
			{
				yVals.set(i, yVals.get(tmp[1]));
			}
		}
	}
	private static double averageValues(ArrayList<Double> yVals) {
		double ave = 0.0;
		for(int i = 0; i < yVals.size(); i++)
		{
			ave += yVals.get(i);
		}
		return ave/yVals.size();
	}
	private static void sortArray(ArrayList<Double> yVals) 
	{
		//Sort Array
		for(int i = 0; i < yVals.size(); i++)
		{
			for(int c = i; c < yVals.size(); c++)
			{
				if(yVals.get(i) > yVals.get(c))
				{
					double tmp = yVals.get(i);
					yVals.set(i, yVals.get(c));
					yVals.set(c, tmp);
				}
			}
		}
	}
	private static void drawLines(Mat pInput, Mat pOutput, Mat lines, ArrayList<Double> yVals,  List<Point> pPoints) 
	{
		moments(pInput, pOutput, pPoints);
		for(int i = 0; i < lines.size().area(); i++)
		{
			double [] tmp = lines.get(0, i);
			
			//Error Checking
			if(Math.abs((double)((tmp[3] - tmp[1])/(tmp[2]- tmp[0]))) > .5 || tmp[1] > pInput.height()-75)
			{
				continue;
			}
			
			pPoints.add(new Point(tmp[0], tmp[1]));
			pPoints.add(new Point(tmp[2], tmp[3]));
			pPoints.add(new Point(tmp[2] - tmp[0], tmp[3] - tmp[1]));

			yVals.add((tmp[3] + tmp[1])/2);
			
			Core.line(pOutput, new Point(tmp[0], tmp[1]), new Point(tmp[2], tmp[3]), new Scalar(0, 255, 0), 2);
		}
	}
	private static void moments(Mat pInput, Mat pOutput, List<Point> pCenterPoints) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(pInput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		List<Moments> mu = new ArrayList<Moments>();
		for(int i = 0; i < contours.size(); i++)
		{
			mu.add(Imgproc.moments(contours.get(i), true));
		}		for(int i = 0; i < contours.size(); i++)
		{
			pCenterPoints.add(new Point(mu.get(i).get_m10()/mu.get(i).get_m00(), mu.get(i).get_m01()/mu.get(i).get_m00()));
		}
		
		for(int i = 0; i < contours.size(); i++)
		{
			Scalar color = new Scalar(0, 255, 255);
			Imgproc.drawContours(pOutput, contours, i, color);
		}
	}
	private static int [] findIndices(ArrayList<Double> xVals) 
	{
		int [] ans = new int [2];
		
		for(int i = 0; i < xVals.size(); i++)
		{
			double begin = xVals.get(i);
			double end = xVals.get(xVals.size()-(i+1));
			if(end - begin < 200)
			{
				 ans[0] = i;
				 ans[1] = xVals.size()-(i+1);
				 return ans;
			}
		}
		return ans;
	}
	public static List <Rect> detectAutonomous(Mat pInput, int pMaxArea)
	{
		//Get the Contours
		List <MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(pInput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		//Create the list for accepted bounded rects
		List<Rect> boundRects = new ArrayList <Rect>();

		//Check to see if there are any contours.
		if(contours.isEmpty())
		{
			return boundRects;
		}
		//Order the Contours
		TreeMap<Double, MatOfPoint> orderedContour = new TreeMap<Double, MatOfPoint>();

		for(int i = 0; i < contours.size(); i++)
		{
			
			double tmp = contours.get(i).size().area();
			if(orderedContour.containsKey(tmp))
			{
				tmp += 0.01;
			}
			orderedContour.put(tmp, contours.get(i));
		}

		//Check if they are accepted as a true blob
		if(checkRatio(orderedContour, 10))
		{
			boundRects.add(Imgproc.boundingRect(getLargestContour(orderedContour)));
		}

		//Return accepted Rectangles/Blobs
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
	public static boolean checkRatio(TreeMap<Double, MatOfPoint> pMap, int minArea)
	{
		//Get the largest Contours
		Set<Double> keys =  pMap.descendingKeySet();
		ArrayList <Double> slots = new ArrayList<Double>();

		for(Double key: keys)
		{
			if(key > minArea)
			{
				slots.add(key);
			}
		}

		//Make them into Rectangles
		ArrayList<Rectangle> rects = new ArrayList<Rectangle>();

		//Get Largest Rectangles
		Rectangle largest = null;
		try
		{
			largest = convertToRectangle(Imgproc.boundingRect(pMap.get(slots.get(0))));
		}
		catch(Exception e)
		{
			return false;
		}

		for(int i = 1; i < slots.size(); i++)
		{
			rects.add(convertToRectangle(Imgproc.boundingRect(pMap.get(slots.get(i)))));
		}

		//remove the rectangles that are not in the biggest one
		for(int i = 0; i < rects.size(); i++)
		{
			if(!largest.contains(rects.get(i)))
			{
				rects.remove(i);
			}
		}

		//Get the complete width and average Height
		double sumWidth = 0;
		double aveHeight = 0;
		for(int i = 0; i < rects.size(); i++)
		{
			sumWidth += rects.get(i).getWidth();
			aveHeight += rects.get(i).getHeight();
		}
		aveHeight /= rects.size();

		double wRatio = sumWidth/largest.getWidth();
		//System.out.println("wRatio: " + wRatio);
		double hRatio = aveHeight/largest.getHeight();
		//System.out.println("hRation: " + hRatio);

		return wRatio > widthRatio && hRatio > heightRatio;
	}
	private static Rectangle convertToRectangle(Rect largest) 
	{
		return new Rectangle(largest.x, largest.y, largest.width, largest.height);
	}
	private static class ImageDlg {
		private final JFrame mFrame = new JFrame();
		private JLabel mLabel = new JLabel();
		public ImageDlg(String pTitle) {
			mFrame.setTitle(pTitle);
			mFrame.setContentPane(mLabel);
			mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mFrame.setVisible(true);
		}
		
		public void updateImage(final Image pImage) {

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					ImageIcon anIcon = new ImageIcon(pImage);
					mLabel.setIcon(anIcon);
					mLabel.repaint();
					mFrame.pack();
				}
			});

		}
	}
	private static Map<String, ImageDlg> mDialogIcons = new HashMap<>();
	public static void displayImg(Mat pInput, String pTitle)
	{ 
		ImageDlg dlgToUpdate = mDialogIcons.get(pTitle);
		if(dlgToUpdate == null) {
			dlgToUpdate = new ImageDlg(pTitle);
			mDialogIcons.put(pTitle, dlgToUpdate);
		}
		
		final Image bufferedImage = toBufferedImage(pInput);
		dlgToUpdate.updateImage(bufferedImage);
		
		
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
	public static Mat toMatrix(BufferedImage img)
	{
		byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		Mat tmp = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC3);
		tmp.put(0, 0, pixels);
		return tmp;
	}
}