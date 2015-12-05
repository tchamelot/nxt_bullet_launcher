package com.chamelot;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import lejos.pc.comm.NXTConnector;

public class Communication extends Thread{
	
	private NXTConnector conn;
	private InputStream iS;
	private OutputStream oS;
	private byte[] data = {0, 0, 0, 0};
	private boolean read;
	
	private Thread reader;
	
	private ArrayList<ReadListener>listenerList = new ArrayList<ReadListener>();
	
	public Communication(String URL) throws IOException{
		conn = new NXTConnector();
		if (!conn.connectTo(URL)) {
			System.err.println("No NXT found");
			System.exit(1);
		}
		System.out.println("Connection start");
		iS  = conn.getInputStream();
		oS = conn.getOutputStream();
		
		System.out.println("Confirmation");
		if(confirmation() != true){
			System.out.println("Connection failed");
			this.conn.close();
			throw new IOException();
		}
		System.out.println("Connection done");
		read = true;
		this.start();
	}
	
	private boolean confirmation() {
		byte[] receipt = {0};
	    boolean validation = false;
	    
		try {
			oS.write(1);
			oS.flush();
			System.out.println("Wait answer");
	        this.iS.read(receipt);
	        System.out.println("Answer received");
	        if(receipt[0] != 1) 
	        	validation = false;
	        else 
	        	validation = true;
	    }
		catch (Exception e){
	        System.out.println("Confirmation exception");
		}
        System.out.println("Confirmation done");
		return validation;

	}

	public void sendData(byte[] data){
		try {
			oS.write(data);
			oS.flush();
		} 
		catch (IOException e) {
		}
	}
	
	public void closeCommunication(){
		try{
			oS.write(4);
			oS.flush();
			read = false;
			iS.close();
			oS.close();
		}
		catch(IOException e){
			System.out.print("closeStream IOException");
		}
	}
	
	public void run(){
		try{
			while(read){
				if(iS.available() > 0){
					System.out.print("Start read");
					iS.read(data);
					if(data[0] == 4)
						updateReadEOC();
					updateReadNew();
				}
			}
		}	
		catch(IOException e){
			System.out.print("read IOException");
		}
		System.out.print("read finished");
	}
	
	public void addReadListener(ReadListener listener){
		listenerList.add(listener);
	}
	
	public void clearListener(){
		listenerList.clear();
	}
	
	public void updateReadNew(){
		for(ReadListener listener : this.listenerList){
			listener.readNew(data);
		}
	}
	
	public void updateReadEOC(){
		for(ReadListener listener : this.listenerList){
			listener.readEOC();
		}
	}
}