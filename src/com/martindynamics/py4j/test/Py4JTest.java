package com.martindynamics.py4j.test;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import image.tools.IViewer;
import img.ImageManipulation;


public class Py4JTest extends Thread{
	
	private ImageProcessor proc;
	
	private ServerSocket server;
	private Socket sock;
	
	private ObjectInputStream  input;
	private ObjectOutputStream output;
	
	int portNumber = 1586;
	
	volatile BufferedImage img;
	volatile boolean imgReady = false;
	
	//IViewer view = null;
		
	public Py4JTest(){
		long t0;
        //byte[][][] img = ImageManipulation.loadImage("E:/School/Research/CNN-Models/MaskRCNN/images/office2.jpg");
        proc = new ImageProcessor();
		this.start();
        
		boolean running = true;
		while(running) {
			if(imgReady) {
//				if(view==null) {
//					view = new IViewer(img);
//				}else {
//					view.setImage(img);
//				}
		        t0 = System.currentTimeMillis();
//		        ArrayList<ImageObject> objects = new ArrayList<>(proc.processImage(ImageManipulation.getRGBAArray(img)));
		        ArrayList<ImageObject> objects = new ArrayList<>(proc.processImage(
		        		ImageProcessor.flatten(ImageManipulation.getRGBAArray(img)),img.getWidth(),img.getHeight(),true));
		        System.out.println("Took: "+(System.currentTimeMillis()-t0)/1000f+" seconds to find "+objects.size()+" objects");
		        t0 = System.currentTimeMillis();
		        for(ImageObject obj: objects) {
		        	System.out.println(obj.label+": "+obj.score);
		        	System.out.println("\t"+obj.coordinates[0]+", "+obj.coordinates[1]+", "+obj.coordinates[2]+", "+obj.coordinates[3]);
		        }
		        System.out.println("Took: "+(System.currentTimeMillis()-t0)/1000f+" seconds to retrieve objects");
			}else {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
        
        proc.close();
	}
	
	public void reconnet() {
		try {
			if(server==null || server.isClosed()) {
				System.out.println("Creating server socket at: "+portNumber);
				server = new ServerSocket(portNumber);
			}
			System.out.println("Listening on port "+portNumber);
			sock   = server.accept();
			System.out.println("Connected to: "+sock.getInetAddress());
			input  = new ObjectInputStream(sock.getInputStream());
			output = new ObjectOutputStream(sock.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized boolean sendMessage(Object msg){
		try {
			output.reset();
			output.writeObject(msg);
			output.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void run() {
		while(true) {
			if(sock==null || sock.isClosed() || sock.isOutputShutdown() || sock.isInputShutdown()){
				reconnet();
			}
			try {
				Object obj = input.readObject();
				if(obj.getClass().equals(BufferedImage.class)) {
					if(!imgReady) {
						img = (BufferedImage) obj;
						imgReady = true;
					}
			        
				}else if(obj.getClass() == (byte[].class)){
					img = new BufferedImage(640,480, BufferedImage.TYPE_3BYTE_BGR);
					byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
					byte[] in   = (byte[])obj; 
					for(int i=0; i<in.length; i++) {
						data[i]=in[i];
					}
					imgReady = true;
				}else {
					System.out.println("Uknown object: "+obj.getClass());
				}
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				//e.printStackTrace();
				try {
					sock.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				sock=null;
			}
		}
	}

	public static void main(String[] args) {
		new Py4JTest();
	}
}
