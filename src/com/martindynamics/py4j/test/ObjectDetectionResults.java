package com.martindynamics.py4j.test;

import java.io.Serializable;
import java.util.ArrayList;

public class ObjectDetectionResults implements Serializable{

	private static final long serialVersionUID = 4091377662700797188L;

	public final long imageID;
	public final ArrayList<ImageObject> objects;
	
	public ObjectDetectionResults(long imageID, ArrayList<ImageObject> objects) {
		this.imageID = imageID;
		this.objects = objects;
	}
	
}
