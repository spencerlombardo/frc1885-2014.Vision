package edu.battlefield.vision;

public interface Observable 
{
	public void add(FrameListener pListener);
	public void remove(FrameListener pListener);
}
