package com.chamelot;

import java.io.IOException;
import java.io.OutputStream;

import lejos.pc.comm.NXTConnector;

public class Main {

	public static void main(String[] args) {
		Window window = new Window();
		window.setVisible(true);
		//test();
	}
	
	public static void test(){
		NXTConnector conn = new NXTConnector();
		OutputStream oS;
		byte[] data = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		try {
			conn.connectTo("usb://");
			oS = conn.getOutputStream();
			for(int i =0; i < 10; i++){
				oS.write(data[i]);
				oS.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
