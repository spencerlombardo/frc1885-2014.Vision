package utils.visiontools;

import java.awt.Image;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

public class VisionToolsControl {

	private VisionToolsModel mModel;
	private VisionToolsView mView;

	public VisionToolsControl(VisionToolsModel pModel) {
		mModel = pModel;
	}

	public void registerView(VisionToolsView pVisionToolsView) {
		mView = pVisionToolsView;
		updateView();
	}
	
	public void registerTransformation(ITransformation pTransformation) {
		
		pTransformation.registerControl(this);
		mModel.addTransformation(pTransformation);
	}
	
	
	public VisionToolsModel getModel() {
		return mModel;
	}

	public void updateView() {
		
		SwingWorker<Image, Void>transformWorker = new SwingWorker<Image, Void>() {
			@Override
			protected Image doInBackground() throws Exception {
				
				Image image = mModel.getImage();
				
				for(ITransformation aTransformation : mModel.getTransformations()) {
					if(aTransformation.isEnabled()) {
						image = aTransformation.applyTransformation(image);
					}
				}
				
				return image;
			}
			
			@Override
			protected void done() {
				try {
					Image aImage = get();
					mModel.setTransformedImage(aImage);

					if(mView != null) {
						mView.visionModelUpdated(mModel);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		transformWorker.execute();
		
	}
	
	
}
