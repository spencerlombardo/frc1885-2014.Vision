package Vision;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class IPVideoCapture extends AbstractNotifier
{
	private AxisCameraConnection capture = null;

	private final int NUM_THREADS = 1;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(NUM_THREADS);

	private ScheduledFuture<?> mCameraReadHandler = null;

	private FrameReader mFrameReader = new FrameReader();

	private String ipAddress;

	private Calibration cal;

	public IPVideoCapture(String pIp)
	{
		super();
		ipAddress = pIp;
		setupParameters();
		cal = Calibration.getInstance();
	}
	public AxisCameraConnection getConnection()
	{
		return capture;
	}
	private void setupParameters() 
	{
		try 
		{
			capture = new AxisCameraConnection(ipAddress);
			this.capture.connect();

			(new Thread(this.capture)).start();
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCameraReadHandler = scheduler.scheduleAtFixedRate(mFrameReader, 1000, 33, TimeUnit.MILLISECONDS);
	}

	public static Mat toMatrix(BufferedImage img)
	{
		byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		Mat tmp = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC3);
		tmp.put(0, 0, pixels);
		return tmp;
	}

	private class FrameReader implements Runnable 
	{
		private Mat mReceivedFrame = new Mat();
		/**
		 * @brief run function for the threading...
		 */
		public void run() 
		{
			try
			{
				mReceivedFrame = toMatrix(capture.grabImage());
				
				if(!cal.isCalibrated())
				{
					cal.doCalibration(mReceivedFrame);
					System.out.println(cal.toString());
				}
				else
				{
					ImageProcessing.displayImg(mReceivedFrame, "OriginalImage");

					// notify all the listeners
					notifyListeners(new VideoFrame(mReceivedFrame, System.currentTimeMillis()));
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}	
	}

	public ScheduledFuture<?> getCameraReadHandler() 
	{
		return mCameraReadHandler;
	}
}
