package Vision;

import java.io.IOException;

import org.opencv.core.Mat;

public class Blah 
{
	public static void main(String [] args) throws IOException
	{
		ImageNetworkConnection cap = new ImageNetworkConnection("http://10.18.85.21/axis-cgi/jpg/image.cgi");

		System.out.println("Grabbing 1000 frames at 1 frame every 100 millis");
		for(int i = 0; i < 1000; i++)
		{
			Mat tmp = AerialAssist.toMatrix(cap.grabImage());
			AerialAssist.displayImg(tmp, "Hello");
//			System.out.println("i= " + i);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
