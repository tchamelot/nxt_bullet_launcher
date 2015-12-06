package com.chamelot.communication;

import java.io.OutputStream;
import java.util.ArrayList;

import com.chamelot.communication.ReadListener;

import java.io.IOException;
import java.io.InputStream;

import lejos.nxt.Sound;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.USB;
import lejos.nxt.comm.NXTConnection;

public class Communication extends Thread{
	
	private NXTConnection conn;
	private InputStream iS;
	private OutputStream oS;
	private byte[] data = {0};
	private boolean read;
	
	private ArrayList<ReadListener> listenerList = new ArrayList<ReadListener>();

	public final static int USB_MODE = 0;
	public final static int BLUETOOTH_MODE = 1;	
		
	public Communication(int mode)  throws NXTCommException {
		switch(mode){
		case USB_MODE:
			conn = USB.waitForConnection(9000, NXTConnection.PACKET);
			break;
		case BLUETOOTH_MODE:
			conn = Bluetooth.waitForConnection(9000, NXTConnection.PACKET);
			break;
		default:
			throw new NXTCommException();
		}
		
		if(conn == null){
			System.exit(1);
		}

		iS = conn.openInputStream();
		
		oS = conn.openOutputStream();
			
		if( confirmation() != true){
			conn.close();
			throw new NXTCommException();
		}
		read = true;
		Sound.beep();
		this.start();
	}
	
	private boolean confirmation() {
		byte receipt[] = {0};
	    boolean validation = false;
	    
		try {
	        iS.read(receipt);
	        
	        if(receipt[0] != 1) {
	        	validation = false;
	        }
	        else {
	        	validation = true;
	        	oS.write(receipt);
	        	oS.flush();
	    		Sound.beep();
	        }
	    }
		catch (Exception e){
		}
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
			oS.close();
			iS.close();
		}
		catch(IOException e){
		}
		Sound.twoBeeps();
	}
	
	public void run(){
		try {
			while(read){
				if(iS.available() > 0){
					iS.read(data);
					if(data[0] == 4)
						updateReadEOC();
					updateReadNew();
				}
			}
		} 
		catch (IOException e) {
		}
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
