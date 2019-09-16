package com.martindynamics.py4j.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import py4j.ClientServer;

import NOMAD.vision.*;
import com.google.flatbuffers.FlatBufferBuilder;

public class ImageProcessor {
	
	ClientServer server;
	ImageProcessorListener processor;
    RandomAccessFile f;
	FlatBufferBuilder fbBuilder = new FlatBufferBuilder(1024000);
	
	public ImageProcessor(){
		server = new ClientServer(null);
	    // We get an entry point from the Python side
	    processor = (ImageProcessorListener) server.getPythonServerEntryPoint(new Class[] { ImageProcessorListener.class });
	     
	    File file = new File("/tmp/vision.mon");
		try {
			f = new RandomAccessFile(file, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		if(server!=null)
			server.shutdown();
		if(f!=null)
			try {
				f.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public List<ImageObject> processImage(byte[][][] img) {
		int w = img[0].length;
		int h = img.length;
		ArrayList<ImageObject> objects = new ArrayList<>();
		ImageObject object;
		try {
			int objectCount = processor.processImage2(w, h, 3, flatten(img));
			for (int o = 0; o < objectCount; o++) {
				object = new ImageObject(w, h, processor.getScore(o), processor.getLabel(o), 
						processor.getMaskBytes(o), processor.getObjectBytes(o));
				objects.add(object);
			}

			return objects;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public ArrayList<ImageObject> processImage(byte[] img, int w, int h, boolean simpleObject) {
		ArrayList<ImageObject> objects = new ArrayList<>();
		ImageObject object;
		long t0, t1;
		try {
			t0=System.currentTimeMillis();

			int imgOffset = Image.createImage(fbBuilder, fbBuilder.createByteVector(img), w, h);
			fbBuilder.finish(imgOffset);
			try {
				f.seek(0);
				f.write(fbBuilder.sizedByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
			fbBuilder.clear();
			
			int objectCount = processor.processImage();
			t1=System.currentTimeMillis();
			System.out.println("Took "+(t1-t0) +"ms to processImage");
//			t0=System.currentTimeMillis();
//			processor.getData();
//			t1=System.currentTimeMillis();
//			System.out.println("Took "+(t1-t0) +"ms to getData");
			for (int o = 0; o < objectCount; o++) {
				if(!simpleObject){
					t0=System.currentTimeMillis();
					object = new ImageObject(w, h, processor.getScore(o), processor.getLabel(o), 
							processor.getMaskBytes(o), processor.getObjectBytes(o));
					t1=System.currentTimeMillis();
					//System.out.println("Took "+(t1-t0) +"ms to create obj");
				}else{
					t0=System.currentTimeMillis();
					object = new ImageObject(w, h, processor.getScore(o), processor.getLabel(o), 
							null, processor.getObjectBytes(o));
					t1=System.currentTimeMillis();
					System.out.println("Took "+(t1-t0) +"ms to create obj");
				}
				objects.add(object);
			}

			return objects;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
		
	//image needs to be flattened into a 1d array such that the color is the fastest changing index, followed by x, and finally y.
	//That is, a nested array would be indexed as [y][x][c]
	public static byte[] flatten(byte[][][] img) {
		int[] shape = new int[] {img.length, img[0].length, img[0][0].length};
		byte[] out = new byte[shape[0]*shape[1]*3];
		for (int y = 0, index = 0; y < shape[0]; y++) {
			for (int x = 0; x < shape[1]; x++) {
				out[index] = img[y][x][0];
				out[index+1] = img[y][x][1];
				out[index+2] = img[y][x][2];
				index += 3;
			}
		}
		return out;
	}

}
