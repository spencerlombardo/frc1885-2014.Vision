package Vision;

import ilite.util.lang.IProvider;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;

import org.opencv.core.Core;

public class VisionMain 
{
	private ImageUpdater updater;
	
	
	public VisionMain()
	{
		updater = new ImageUpdater();
	}
	private void setupApplication()
	{
		System.out.println("Happy");
		IPVideoCapture capture = new IPVideoCapture("10.18.85.21");
		ThresholdImg thresh = new ThresholdImg(169, 179);
		Morphology morphImg = new Morphology();
		ApplyCanny canny = new ApplyCanny(1, 75, 1, Math.PI/180);
		DistanceDetector distance = new DistanceDetector();
		printStuffs p = new printStuffs();
		
		capture.add(updater);
		capture.add(thresh);
		
		thresh.add(morphImg);
		
		morphImg.add(canny);
		
		canny.add(distance);
		
		distance.add(p);
		
		try 
		{
			capture.getCameraReadHandler().get();
		} 
		catch (InterruptedException | ExecutionException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void startMain ()
	{
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
		
		setupApplication();
	}
	
	public IProvider<BufferedImage> getUpdater()
	{
		return updater;
	}
}
