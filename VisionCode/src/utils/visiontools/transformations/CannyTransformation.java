package utils.visiontools.transformations;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import utils.visiontools.ITransformation;
import Vision.AerialAssist;

public class CannyTransformation extends AbstractTransformation implements ITransformation, ChangeListener {
	
	private JSlider mLowThresh;
	private JSlider mHighThresh;
	private JLabel mLowVal = new JLabel("000");
	private JLabel mHighVal = new JLabel("000");

	public CannyTransformation() {
		super("CANNY");
		
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
		

		
	}
	@Override
	public void specificAction() {

		mLowThresh.setEnabled(isEnabled());
		mHighThresh.setEnabled(isEnabled());
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
		
		Image returnedImage = pImage;
		if(returnedImage instanceof BufferedImage) {
			BufferedImage bufImg = (BufferedImage)returnedImage;
			System.out.println("START CANNY: Image type= " + bufImg.getType());
			
			BufferedImage convertedImg = new BufferedImage(bufImg.getWidth(), bufImg.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		    convertedImg.getGraphics().drawImage(bufImg, 0, 0, null);
			
			Mat imageAsMat = AerialAssist.toMatrix(convertedImg);
			
			Imgproc.cvtColor(imageAsMat, imageAsMat, Imgproc.COLOR_RGB2GRAY);

			Imgproc.Canny(imageAsMat, imageAsMat, mLowThresh.getValue(), mHighThresh.getValue());
			
			Imgproc.cvtColor(imageAsMat, imageAsMat, Imgproc.COLOR_GRAY2BGR);
			returnedImage = AerialAssist.toBufferedImage(imageAsMat);
			
			System.out.println("END CANNY: Image type= " + ((BufferedImage)returnedImage).getType());
			
			
		}
		
		
			
		return returnedImage;
	}

}
