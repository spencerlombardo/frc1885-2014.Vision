package utils.visiontools;

import java.awt.Image;

import javax.swing.JPanel;

public interface ITransformation {
	
	public JPanel getView();
	
	public boolean isEnabled();
	
	public Image applyTransformation(Image pImage);
	
	public void registerControl(VisionToolsControl pControl);

}
