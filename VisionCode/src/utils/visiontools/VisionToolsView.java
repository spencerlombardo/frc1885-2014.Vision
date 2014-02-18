package utils.visiontools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

public class VisionToolsView {
	
	private final JPanel mView = new JPanel();
	private final JPanel mBlankOrig = new JPanel();
	private final JPanel mBlankModified = new JPanel();
	private final JLabel mImageLabel = new JLabel();
	private final JLabel mTransformedLabel = new JLabel();
	
	private VisionToolsControl mControl;
	private JPanel mSplit;

	public VisionToolsView(VisionToolsControl pControl) {
		mControl = pControl;
		mView.setLayout(new BorderLayout());
		
		Dimension blankSize = new Dimension(500, 500);
		Image aImage = mControl.getModel().getImage();
		if(aImage != null) {
			
			int aWidth = aImage.getWidth(null);
			int aHeight = aImage.getHeight(null);
			blankSize.width = aWidth;
			blankSize.height = aHeight;
		}
		
		mBlankOrig.setBackground(Color.BLACK);
		mBlankOrig.setPreferredSize(blankSize);
		TitledBorder originalBorder = BorderFactory.createTitledBorder("ORIGINAL");
		mImageLabel.setBorder(originalBorder);
		mBlankOrig.setBorder(originalBorder);
		
		
		mBlankModified.setBackground(Color.BLACK);
		mBlankModified.setPreferredSize(blankSize);
		TitledBorder transformedBorder = BorderFactory.createTitledBorder("TRANSFORMED");
		mBlankModified.setBorder(transformedBorder);
		mTransformedLabel.setBorder(transformedBorder);
		
		mSplit = new JPanel(new GridLayout(1, 2));
		mSplit.add(mBlankOrig);
		mSplit.add(mBlankModified);
		
		mView.add(mSplit, BorderLayout.CENTER);
		
		JPanel controlsLabel = new JPanel();
		controlsLabel.setLayout(new BoxLayout(controlsLabel, BoxLayout.Y_AXIS));
		
		for(ITransformation aTransformations : mControl.getModel().getTransformations()) {
			controlsLabel.add(aTransformations.getView());
		}
		
		
		mView.add(controlsLabel, BorderLayout.NORTH);

		
		mControl.registerView(this);
	}
	
	public JPanel getView() {
		return mView;
	}
	
	public void visionModelUpdated(final VisionToolsModel pModel) {
		
		if(SwingUtilities.isEventDispatchThread()) {
			
			mSplit.removeAll();
			
			if(pModel.getImage() != null) {
				mImageLabel.setIcon(new ImageIcon(pModel.getImage()));
				mSplit.add(mImageLabel);

			} else {
				mSplit.add(mBlankOrig);
			}
			
			if(pModel.getTransformedImage() != null) {
				mTransformedLabel.setIcon(new ImageIcon(pModel.getTransformedImage()));
				mSplit.add(mTransformedLabel);
			} else {
				mSplit.add(mBlankModified);
			}
			mView.revalidate();
			mView.repaint();
			
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {

					visionModelUpdated(pModel);
				}
			});
		}
	}

}
