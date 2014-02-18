package utils.visiontools;

import java.awt.Image;
import java.lang.reflect.Field;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import utils.visiontools.transformations.CannyTransformation;
import utils.visiontools.transformations.HoughTransformation;
import utils.visiontools.transformations.ThresholdColorTransform;
import Vision.AerialAssist;

public class VisionToolsMain {
	
	private static boolean isLoaded = false;
	
	//Grab Library
			static {
				try {
					System.out.println("Old lib path: " + System.getProperty("java.library.path"));
					String homeDir = System.getProperty("user.home");
					String libPath =homeDir+"/Downloads/opencv/build/java/x64;"+System.getProperty("java.library.path");
					System.setProperty( "java.library.path", libPath);

					Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
					fieldSysPath.setAccessible( true );
					fieldSysPath.set( null, null );


					System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
					isLoaded = true;
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
	
	public static void createAndShowGUI() {
		String fileName = "./2014SampleImagePartialLightUp.png";
		Mat aImread = Highgui.imread(fileName);
		Image aBufferedImage = AerialAssist.toBufferedImage(aImread);
		VisionToolsModel aModel = new VisionToolsModel(aBufferedImage);
		VisionToolsControl aControl = new VisionToolsControl(aModel);
		aControl.registerTransformation(new ThresholdColorTransform());
		aControl.registerTransformation(new CannyTransformation());
		aControl.registerTransformation(new HoughTransformation());
		VisionToolsView aView = new VisionToolsView(aControl);
		
		JFrame aFrame = new JFrame();
		aFrame.setContentPane(aView.getView());
		aFrame.pack();
		aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		aFrame.setVisible(true);
		
	}
	
	public static void main(String[] args) {
		
		if(!isLoaded) {
			System.err.println("Unable to start app because OPENCV Lib not loaded!");
			return;
		}
		
		
		
		SwingUtilities.invokeLater(new  Runnable() {
			
			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
