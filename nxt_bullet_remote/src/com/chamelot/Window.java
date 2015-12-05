package com.chamelot;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class Window extends JFrame implements KeyListener{
	private Communication comm;
	private JPanel panel = new JPanel();
		
	public Window() {
		this.setTitle("NXT Interface");
		this.setSize(500, 200);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
			
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void  windowClosing(WindowEvent e){
				if(comm != null)
					comm.closeCommunication();
				e.getWindow().dispose();
			}
		});
		this.setUndecorated(false);

		this.setContentPane(panel);
		this.setVisible(true);
		
		this.addKeyListener(this);
		
		try{
			comm= new Communication("usb://");
			comm.addReadListener(new ReadListener(){

				@Override
				public void readNew(byte[] data) {
					for(byte i : data)
						System.out.println("Données reçues : " + i);
					System.out.println("");
				}

				@Override
				public void readEOC() {
					comm.closeCommunication();
					System.exit(0);
				}
			});
		}
		catch(IOException e){
		}
	}

	public void keyPressed(KeyEvent e) {
		byte[] data = {0, 0, 0, 0};
		int idx  = 0;
				
		if((e.getKeyCode()==KeyEvent.VK_UP)){
			data[idx] = 117;
			idx++;
		}			
		if((e.getKeyCode()==KeyEvent.VK_DOWN)){
			data[idx] = 100;
			idx++;
		}	
		if((e.getKeyCode()==KeyEvent.VK_RIGHT)){
			data[idx] = 114;
			idx++;
		}	
		if((e.getKeyCode()==KeyEvent.VK_LEFT)){
			data[idx] = 108;
			idx++;
		}
        System.out.println("Sending");
		comm.sendData(data);
        System.out.println("Send");
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
	}
		
}

