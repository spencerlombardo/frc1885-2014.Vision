package utils.visiontools.transformations;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import Vision.AerialAssist;

public class ThresholdColorTransform extends AbstractTransformation implements ChangeListener {
	private JSlider mLowThresh;
	private JSlider mHighThresh;
	private JLabel mLowVal = new JLabel("000");
	private JLabel mHighVal = new JLabel("000");
	private JComboBox<EHSVChannels> mChannelPicker;
	public ThresholdColorTransform() {
		super("Threshold Color");
		JLabel low = new JLabel("LowThresh");
		mLowThresh = new JSlider(1, 255);
		JLabel high = new JLabel("HIGH");
		mHighThresh = new JSlider(1, 255);
		
		mView.add(low);
		mView.add(mLowThresh);
		mView.add(mLowVal);
		mView.add(high);
		mView.add(mHighThresh);
		mView.add(mHighVal);
		mLowThresh.addChangeListener(this);
		mHighThresh.addChangeListener(this);
		
		mLowVal.setText(Integer.toString(mLowThresh.getValue()));
		mHighVal.setText(Integer.toString(mHighThresh.getValue()));
		
		mChannelPicker = new JComboBox<>(EHSVChannels.values());
		mChannelPicker.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent pArg0) {
				notifyControl();
				
			}
		});
		mView.add(mChannelPicker);
		
		
	}

	@Override
	public void stateChanged(ChangeEvent pE) {
		Object aSource = pE.getSource();
		if(aSource instanceof JSlider) {
			if(!((JSlider) aSource).getValueIsAdjusting()) {
				mLowVal.setText(Integer.toString(mLowThresh.getValue()));
				mHighVal.setText(Integer.toString(mHighThresh.getValue()));

				notifyControl();
			}
		}
		
	}

	@Override
	public Image applyTransformation(Image pImage) {
		
		
		Image returnVal = pImage;
		if(pImage instanceof BufferedImage) {
			
			BufferedImage image = (BufferedImage)pImage;

			System.out.println("Start Threshold: Image type= " + image.getType());
			
			Mat imageAsMat = AerialAssist.toMatrix(image);

			Mat tmpMat1 = new Mat();
			ArrayList<Mat> hsvPlanes = new ArrayList<Mat>();
			
			Imgproc.cvtColor(imageAsMat, tmpMat1, Imgproc.COLOR_BGR2HSV);
			Core.split(tmpMat1, hsvPlanes);
			
			int aSelectedIndex = mChannelPicker.getSelectedIndex();
			
			Mat t = AerialAssist.thresholdColor(hsvPlanes.get(aSelectedIndex), mLowThresh.getValue(), mHighThresh.getValue());
			System.out.println(t.type());
			System.out.println(t.channels());
			
			Imgproc.cvtColor(t, t, Imgproc.COLOR_GRAY2BGR);
			
			//Returns a grayscale image
			returnVal = AerialAssist.toBufferedImage(t);
			
			System.out.println("End Threshold: Image type= " + ((BufferedImage)returnVal).getType());
		
		}
		
		return returnVal;
	}
	
	
	@Override
	public void specificAction() {
		mLowThresh.setEnabled(isEnabled());
		mHighThresh.setEnabled(isEnabled());
	}

}
