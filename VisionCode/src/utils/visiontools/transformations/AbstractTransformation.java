package utils.visiontools.transformations;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import utils.visiontools.ITransformation;
import utils.visiontools.VisionToolsControl;

public abstract class AbstractTransformation implements ITransformation, ActionListener{

	protected JPanel mView = new JPanel();
	protected final JCheckBox mEnabledCheckBox;
	protected VisionToolsControl mControl;

	public AbstractTransformation(String pString) {
		this(pString, true);
	}
	public AbstractTransformation(String pString, boolean pIsEnabled) {
		super();

		mEnabledCheckBox = new JCheckBox("Enabled");
		mEnabledCheckBox.setSelected(pIsEnabled);
		mEnabledCheckBox.addActionListener(this);
		mView.add(mEnabledCheckBox);
		mView.setBorder(BorderFactory.createTitledBorder(pString));
	}

	@Override
	public JPanel getView() {
		return mView;
	}

	@Override
	public boolean isEnabled() {
		return mEnabledCheckBox.isSelected();
	}

	@Override
	public Image applyTransformation(Image pImage) {
		
		Image returnedImage = pImage;

		return returnedImage;
	}

	@Override
	public void registerControl(VisionToolsControl pControl) {
		mControl = pControl;
	
	}
	
	@Override
	public void actionPerformed(ActionEvent pE) {
		specificAction();
		mControl.updateView();
	}
	
	public abstract void specificAction();

	protected void notifyControl() {
		if(mControl != null) {
			mControl.updateView();
		}
	}

}