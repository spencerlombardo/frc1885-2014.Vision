package utils.visiontools.transformations;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import Vision.AerialAssist;

public class HoughTransformation extends AbstractTransformation implements ChangeListener {



	private static final int DEFAULT_THRESHOLD = 30;
	private static final int MAX_THRESHOLD = 300;
	private static final int MIN_THRESHOLD = 1;
	private static final int MAX_ANGLE = 359;
	private static final int MIN_ANGLE = 0;
	private static final double RHO_STEP_SIZE = .05;
	private static final int MAX_RHO = 100;
	private static final int MIN_RHO = 1;
	private static final int DEFAULT_RHO = 1;
	private static final Scalar HOUGH_CIRCLE_COLOR = new Scalar(0, 255, 0);
	private final JSpinner mRho;
	private final JSlider mAngle;
	private final JSlider mThreshold;
	private JLabel mAngleLabel = new JLabel("00000");
	private JLabel mThreshLabel = new JLabel("0000");

	public HoughTransformation() {
		super("HOUGH", false);
		mRho = new JSpinner(new SpinnerNumberModel(DEFAULT_RHO, MIN_RHO, MAX_RHO, RHO_STEP_SIZE));
		mRho.addChangeListener(this);
		mRho.setBorder(BorderFactory.createTitledBorder("RHO"));
		mView.add(mRho);

		JPanel anglePanel = new JPanel();
		mAngle = new JSlider(MIN_ANGLE, MAX_ANGLE);
		anglePanel.setBorder(BorderFactory.createTitledBorder("Angle"));

		double degs = Math.toDegrees(Math.PI/180d);
		mAngle.setValue((int)degs);
		mAngle.addChangeListener(this);
		anglePanel.add(mAngle);
		anglePanel.add(mAngleLabel);
		mView.add(anglePanel);


		JPanel threshPanel = new JPanel();
		mThreshold = new JSlider(MIN_THRESHOLD, MAX_THRESHOLD);
		mThreshold.setValue(DEFAULT_THRESHOLD);
		threshPanel.setBorder(BorderFactory.createTitledBorder("Threshold"));
		mThreshold.addChangeListener(this);
		threshPanel.add(mThreshold);
		threshPanel.add(mThreshLabel);
		mView.add(threshPanel);
	}

	@Override
	public void specificAction() {
		mRho.setEnabled(isEnabled());
		mAngle.setEnabled(isEnabled());
		mThreshold.setEnabled(isEnabled());

	}

	@Override
	public Image applyTransformation(Image pImage) {
		Image returnedImage = pImage;
		
		if(pImage instanceof BufferedImage) {

			BufferedImage aImage = (BufferedImage)pImage;
			System.out.println("Start HOUGH: Image Type= " + aImage.getType());

			Mat imageAsMat = AerialAssist.toMatrix(aImage);
			Imgproc.cvtColor(imageAsMat, imageAsMat, Imgproc.COLOR_BGR2GRAY);
			
			Mat lines = new Mat();
			
			Double rho = (Double)mRho.getValue();
			int angleAsDegs = mAngle.getValue();
			double angleAsRads = Math.toRadians(angleAsDegs);
			int theshold = mThreshold.getValue();
			Imgproc.HoughLinesP(imageAsMat, lines, rho, angleAsRads, theshold);

			Imgproc.cvtColor(imageAsMat, imageAsMat, Imgproc.COLOR_GRAY2BGR);
			
//			List<Point>pPoints = new ArrayList<Point>();
			for(int i = 0; i < lines.size().area(); i++)
			{
				double [] tmp = lines.get(0, i);

				//Error Checking
				if(Math.abs((double)((tmp[3] - tmp[1])/(tmp[2]- tmp[0]))) > .5 || tmp[1] > imageAsMat.height()-75)
				{
					continue;
				}

//				pPoints.add(new Point(tmp[0], tmp[1]));
//				pPoints.add(new Point(tmp[2], tmp[3]));
//				pPoints.add(new Point(tmp[2] - tmp[0], tmp[3] - tmp[1]));

				Core.line(imageAsMat, new Point(tmp[0], tmp[1]), new Point(tmp[2], tmp[3]), HOUGH_CIRCLE_COLOR, 2);
			}

			System.out.println(imageAsMat.channels());
			returnedImage = AerialAssist.toBufferedImage(imageAsMat);
			
			System.out.println("End HOUGH: Image Type= " + ((BufferedImage)returnedImage).getType());
		}

		return returnedImage;
	}

	@Override
	public void stateChanged(ChangeEvent pE) {
		
		Object aSource = pE.getSource();
		boolean shouldUpdate = false;
		
		if(aSource instanceof JSlider) {
			JSlider aSlider = (JSlider)aSource;
			if(!aSlider.getValueIsAdjusting()){
				shouldUpdate = true;
				
			}
		} else if(aSource instanceof JSpinner) {
			shouldUpdate = true;
		}
		
		
		if(shouldUpdate) {
			mAngleLabel.setText(Integer.toString(mAngle.getValue()));
			mThreshLabel.setText(Integer.toString(mThreshold.getValue()));
			actionPerformed(null);
		}
		
		
	}

}
