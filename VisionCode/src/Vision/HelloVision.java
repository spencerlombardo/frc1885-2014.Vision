package Vision;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class HelloVision {

	private static boolean isLibLoaded = false;

	static {

		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			isLibLoaded = true;
		} catch(UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {

		System.out.println("OPENCV Version= " + Core.NATIVE_LIBRARY_NAME);

		if(isLibLoaded) {
			Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
			System.out.println("OpenCV Mat: " + m);
			Mat mr1 = m.row(1);
			mr1.setTo(new Scalar(1));
			Mat mc5 = m.col(5);
			mc5.setTo(new Scalar(5));
			System.out.println("OpenCV Mat data:\n" + m.dump());

			JFileChooser aChooser = new JFileChooser();
			int showOpenDialog = aChooser.showOpenDialog(null);

			switch(showOpenDialog) {
			case JFileChooser.APPROVE_OPTION:
				File selectedFile = aChooser.getSelectedFile();

				Mat imread = Highgui.imread(selectedFile.getAbsolutePath());
				final Image bufferedImage = toBufferedImage(imread);
				
				ImageIcon anIcon = new ImageIcon(bufferedImage);
				JFrame aFrame = new JFrame();
				aFrame.setContentPane(new JLabel(anIcon));
				aFrame.pack();
				aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				aFrame.setVisible(true);
			}
		}

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
