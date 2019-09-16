package com.martindynamics.py4j.test;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import image.tools.IViewer;
import img.ImageManipulation;


public class Py4JTestOpenCV extends Thread{
	
	private ImageProcessor proc;
	
	volatile BufferedImage img;
	volatile byte[] imgData;
	int h, w;

	VideoCapture camera;
	boolean cameraLoaded;

	volatile boolean grabFrames = false;
	volatile boolean grabbed = false;
	
	IViewer view = null;
	
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	
	public Py4JTestOpenCV(){
		long t0;
        //byte[][][] img = ImageManipulation.loadImage("E:/School/Research/CNN-Models/MaskRCNN/images/office2.jpg");
        proc = new ImageProcessor();
        camera = new VideoCapture(1);
        
        if (!camera.isOpened()) {
			System.out.println("Camera Error");
			System.exit(1);
		} else {
			System.out.println("Camera OK");
			cameraLoaded = true;
		}
        
        new FrameGrabber().start();
        grabFrames = true;
        
		boolean running = true;
		while(running) {
			while(img==null || !grabbed){}
			
			t0 = System.currentTimeMillis();
//			ArrayList<ImageObject> objects = new ArrayList<>(proc.processImage(ImageManipulation.getRGBAArray(img), false));
			
			ArrayList<ImageObject> objects = proc.processImage(imgData, w, h, true);
			System.out.println("\nTook: " + (System.currentTimeMillis() - t0) / 1000f + " seconds to find "
					+ objects.size() + " objects");
			for (ImageObject obj : objects) {
				System.out.println(obj.label + ": " + obj.score);
				System.out.println("\t" + obj.coordinates[0] + ", " + obj.coordinates[1] + ", " + obj.coordinates[2]
						+ ", " + obj.coordinates[3]);
				if(obj.label.equals("person")){
					Graphics2D g2d = img.createGraphics();
					g2d.setColor(java.awt.Color.RED);
					g2d.drawRect(obj.coordinates[1], obj.coordinates[0], 
							obj.coordinates[3]-obj.coordinates[1],
							obj.coordinates[2]-obj.coordinates[0]);
					g2d.dispose();
				}
//				if(obj.label.equals("chair")){
//					Graphics2D g2d = img.createGraphics();
//					g2d.setColor(java.awt.Color.GREEN);
//					g2d.drawRect(obj.coordinates[1], obj.coordinates[0], 
//							obj.coordinates[3]-obj.coordinates[1],
//							obj.coordinates[2]-obj.coordinates[0]);
//					g2d.dispose();
//				}
				else{
					Graphics2D g2d = img.createGraphics();
					g2d.setColor(java.awt.Color.GREEN);
					g2d.drawRect(obj.coordinates[1], obj.coordinates[0], 
							obj.coordinates[3]-obj.coordinates[1],
							obj.coordinates[2]-obj.coordinates[0]);
					g2d.setColor(java.awt.Color.RED);
					g2d.drawString(obj.label+": "+Math.round(obj.score*1000)/1000f, obj.coordinates[1]+5, obj.coordinates[0]+10);
					g2d.dispose();
				}
			}

			if (view == null) {
				view = new IViewer(img);
			} else {
				view.setImage(img);
			}
			grabbed = false;

		}
        
        proc.close();
	}
	
	public class FrameGrabber extends Thread{
		
		public void run(){
			Mat tl = new Mat();
			while (true){
				if(cameraLoaded){
					camera.read(tl);
				}
				if(grabFrames && !grabbed){
					if(img == null){
						img = new BufferedImage(tl.width(), tl.height(), BufferedImage.TYPE_3BYTE_BGR);
						imgData = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
						h = tl.height();
						w = tl.width();
					}
					tl.get(0, 0, imgData);
					grabbed = true;
				}
			}
		}
	}
	
	public static void main(String[] args) {
		new Py4JTestOpenCV();
	}
	
}