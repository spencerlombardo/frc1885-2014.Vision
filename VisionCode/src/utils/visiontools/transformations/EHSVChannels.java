package utils.visiontools.transformations;

public enum EHSVChannels {
	HUE("H"),
	SATURATION("S"),
	VALUE("V");
	
	private String mText;

	private EHSVChannels(String pText) {
		mText = pText;
	}
	
	public String getText() {
		return mText;
	}

}
