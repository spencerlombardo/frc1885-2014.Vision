package edu.battlefield.vision;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class ThreshTool {
	
	static {

		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//			isLibLoaded = true;
		} catch(UnsatisfiedLinkError e) {
			e.printStackTrace();
		}
	}
	
	private final Mat mOrigImage;
	private final JPanel mPanel = new JPanel();

	public ThreshTool(Mat pOriginalImage) {
		mOrigImage = pOriginalImage;
		final Image bufferedImage = toBufferedImage(mOrigImage);
		ImageIcon anIcon = new ImageIcon(bufferedImage);
		mPanel.setLayout(new BorderLayout());
		final JLabel imageLabel = new JLabel(anIcon);
		
		JPanel imagePanel = new JPanel();
		imagePanel.add(imageLabel);
		JLabel originalImagePanel = new JLabel(new ImageIcon(bufferedImage));
		originalImagePanel.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				Point point = e.getPoint();
				
				BufferedImage origImag = (BufferedImage)bufferedImage;
				
				int rgb = origImag.getRGB(e.getPoint().x, e.getPoint().y);
				int []pixelColors = new int[4];
				pixelColors[0] = ((rgb & 0xFF000000) >> 24);
				pixelColors[1] = ((rgb & 0x00FF0000) >> 16);
				pixelColors[2]= ((rgb & 0x0000FF00) >> 8);
				pixelColors[3] = ((rgb & 0x000000FF) >> 0);
				JOptionPane.showConfirmDialog(mPanel, "Got the pixel values["+pixelColors[0]+", "+pixelColors[1] +", " + pixelColors[2]+", " + pixelColors[3]+"]");
			}
			
		});
		imagePanel.add(originalImagePanel);
		mPanel.add(imagePanel, BorderLayout.CENTER);
		
		JPanel sliderPanel = new JPanel();
		final JSlider aSLider = new JSlider(0, 255);
		aSLider.setValue(0);
		sliderPanel.add(aSLider);
		final JLabel sliderValue = new JLabel("SLIDER VALUE: XXX");
		
		final JComboBox<Integer>channel = new JComboBox<>(new Integer[]{0,1,2});
		
		channel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateImage(imageLabel, aSLider, sliderValue, (Integer)channel.getSelectedItem());
				
			}
		});

		aSLider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
			
				
				if(aSLider.getValueIsAdjusting()) 
					return;
				
				updateImage(imageLabel, aSLider, sliderValue, (Integer)channel.getSelectedItem());
				
				
				
			}
		});
		
		sliderPanel.add(sliderValue);
		sliderPanel.add(channel);
		mPanel.add(sliderPanel, BorderLayout.SOUTH);
		
	}
	
	public JPanel getView() {
		return mPanel;
	}
	private void updateImage(final JLabel imageLabel, final JSlider aSLider,
			final JLabel sliderValue, final Integer pChannel) {
		final int value = aSLider.getValue();
		sliderValue.setText("SLIDER VALUE: " + value);
		
		SwingWorker<Image, Void>aWorker =new SwingWorker<Image, Void>() {

			@Override
			protected Image doInBackground() throws Exception {
				Mat newImage = new Mat(mOrigImage.rows(), mOrigImage.cols(), CvType.CV_8UC1);
				AerialAssist.lowFrequencyFilter(mOrigImage, newImage);
				AerialAssist.threshChannel(newImage, newImage, pChannel.intValue(), value);
				return toBufferedImage(newImage);
			}
			
			@Override
			protected void done() {
				
				Image newImage;
				try {
					newImage = get();

					ImageIcon anIcon = new ImageIcon(newImage);
					
					imageLabel.setIcon(anIcon);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		};
		
		aWorker.execute();
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
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				createAndShowGUI();	
			}
		});
	}

	protected static void createAndShowGUI() {
		
		JFrame aFrame = new JFrame();
		File selectedFile = 
				new File("C:/Users/spenc_000/Documents/School/Robotics/frc1885-2014.Vision/Test Image/2013SampleImagePartialLightUp.jpg");
		Mat imread = Highgui.imread(selectedFile.getAbsolutePath());
		
		ThreshTool aTool = new ThreshTool(imread);
		aFrame.setContentPane(aTool.getView());
		aFrame.pack();
		aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		aFrame.setVisible(true);
	}
}
