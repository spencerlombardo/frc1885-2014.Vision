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

public class ImageProcessing 
{	
	private static int MAX_KERNAL_LENGTH = 4;
	
	private static double heightRatio = .55;
	private static double widthRatio = .75;
	private static double calibrationX = 59.5;
	private double calibrationY = 26.25 + 3.5 - 1.25;
	private double calArea = 384.0;
	private double calHeight = 12;
	private double calWidth = 32;
	private static double inchPerPixel = 0.2151639344262295;
	
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
	private static List<MatOfPoint> mainProcessing(Mat imread, Mat pOutput) 
	{
		Mat thresh = applyThreshold(imread, 169, 179);
		displayImg(thresh, "Thresholded");
		
		Mat morph = morph(thresh);
		if(morph.empty())
		{
			morph = thresh;
		}
		displayImg(morph, "Morph");
		
		List <MatOfPoint> contours = blobsarecool(morph, morph);
		displayImg(morph, "BLOBS ARE COOLIO YO");
		morph.copyTo(pOutput);
		return contours;
	}
	public static Mat applyThreshold(Mat imread, int pMin, int pMax) 
	{
		ArrayList<Mat> hsvPlanes = new ArrayList<Mat>();
		
		Imgproc.cvtColor(imread, imread, Imgproc.COLOR_BGR2HSV);
		Core.split(imread, hsvPlanes);
		
		Mat thresh = thresholdColor(hsvPlanes.get(0), pMin, pMax);
		return thresh;
	}
	public static Mat morph(Mat pMat)
	{
		int mErosionSizeX1 = 1;
		int mErosionSizeY1 = 1;
		
		Mat mElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(mErosionSizeX1 + 1, mErosionSizeY1 + 1), new Point(mErosionSizeX1, mErosionSizeY1));
		
		Mat tmp3 = pMat.clone();
		Mat tmp2 = new Mat();
		
		Imgproc.erode(tmp3, tmp2, mElement);
		Imgproc.dilate(tmp2, tmp3, mElement);

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
	public static void lowFrequencyFilterChannel(Mat pInput, Mat pOutput, int pChannel)
	{
		//Extract the Channel requested
		Mat grayImg = new Mat(pInput.rows(), pInput.cols(), CvType.CV_8UC1);
		Core.extractChannel(pInput, grayImg, pChannel);

		//Apply Filter
		lowFrequencyFilter(grayImg, pOutput);
	}
	public static void lowFrequencyFilter(Mat pInput, Mat pOutput)
	{
		//Apply the Gaussian Filter
		for ( int i = 1; i < MAX_KERNAL_LENGTH; i = i + 2 )
		{ 
			Imgproc.GaussianBlur( pInput, pOutput, new Size( i, i ), 0, 0 );
		}	
	}
	public static List<MatOfPoint> blobsarecool(Mat pInput, Mat pOutput)
	{
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();

		Imgproc.findContours(pInput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		Imgproc.cvtColor(pOutput, pOutput, Imgproc.COLOR_GRAY2BGR);
	
		MatOfPoint largest = largestContour(contours);
		
		Rect tmp = Imgproc.boundingRect(largest);
		Core.rectangle(pOutput, tmp.tl(), tmp.br(), new Scalar(0, 0,231));
		System.out.println("Largest's Pixels: " + (pOutput.height() - ((tmp.br().y + tmp.tl().y)/2)));
		System.out.println("Area: " + tmp.height*tmp.width);
		System.out.println("Height: " + tmp.height + ", Width: " + tmp.width);
		
		contours.clear();
		contours.add(largest);
		return contours;
	}
	public static MatOfPoint largestContour(List<MatOfPoint> pInput)
	{
		MatOfPoint ans = pInput.get(0);
		for(int i = 1; i < pInput.size(); i++)
		{
			if(pInput.get(i).size().area() > ans.size().area())
			{
				ans = pInput.get(i);
			}
		}
		return ans;
	}
	public static Rect cannyAndHough(Mat pInput, Mat pOutput, int pLowThresh, int pHighThresh, double pRho, double pTheta)
	{
		Imgproc.Canny(pInput, pInput, pLowThresh, pHighThresh);
		
		Mat lines = new Mat();
		Imgproc.HoughLinesP(pInput, lines, pRho, pTheta, 30, 20, 10);
		//Imgproc.HoughLinesP(pInput, lines, pRho, pTheta, 30);
		Imgproc.cvtColor(pInput, pOutput, Imgproc.COLOR_GRAY2BGR);
		ArrayList<Double> yVals = new ArrayList<Double>();
		displayImg(pInput, "Canny");
		
		List<Point> points = new ArrayList<Point>();
		
		drawLines(pInput, pOutput, lines, yVals, points);

		sortArray(yVals);
		
		changeIndices(yVals);
		MatOfPoint pnts = new MatOfPoint();
		pnts.fromList(points);
		
		Rect tmp = Imgproc.boundingRect(pnts);
		return tmp;
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
		}		
		for(int i = 0; i < contours.size(); i++)
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