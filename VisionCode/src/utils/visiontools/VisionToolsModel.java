package utils.visiontools;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public class VisionToolsModel {
	
	public VisionToolsModel(Image pOriginalImage) {
		mOriginalImage = pOriginalImage;
	}
	private final  Image mOriginalImage;
	public Image getModifiedImage() {
		return mModifiedImage;
	}
	
	public void addTransformation(ITransformation pTransformation) {
		mTransformations.add(pTransformation);
	}
	
	public List<ITransformation>getTransformations() {
		return new ArrayList<ITransformation>(mTransformations);
	}

	private Image mModifiedImage = null;
	private List<ITransformation>mTransformations = new ArrayList<ITransformation>();
	private Image mTransformedImage;

	public Image getImage() {
		return mOriginalImage;
	}

	public void setTransformedImage(Image pImage) {
		mTransformedImage = pImage;
		
	}
	
	public Image getTransformedImage() {
		return mTransformedImage;
	}

}
