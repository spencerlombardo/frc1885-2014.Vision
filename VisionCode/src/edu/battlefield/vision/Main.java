package edu.battlefield.vision;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;

import org.opencv.core.Core;

public class Main 
{
	private static void setupApplication()
	{
		IPVideoCapture capture = new IPVideoCapture();
		LightCancellization light = new LightCancellization();
		GaussianFilter blur = new GaussianFilter();
		ThresholdImg thresh = new ThresholdImg();
		DetectBlobs blobs = new DetectBlobs();
		
		capture.add(light);
		light.add(blur);
		blur.add(thresh);
		thresh.add(blobs);
		
		try 
		{
			capture.getCameraReadHandler().get();
		} 
		catch (InterruptedException | ExecutionException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args)
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
}
