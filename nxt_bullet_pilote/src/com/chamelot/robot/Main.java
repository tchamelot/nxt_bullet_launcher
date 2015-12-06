package com.chamelot.robot;

import lejos.nxt.MotorPort;
import lejos.nxt.Sound;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;
import lejos.nxt.Button;
import lejos.nxt.LCD;

import java.io.IOException;
import java.io.InputStream;

import com.chamelot.motor.*;

public class Main {
	public static void main(String[] args) {
		BulletLauncher launcher = new BulletLauncher();
		//testCom();
	}

	static public void 	testAim(){
		int h = 0,  v = 0;
		boolean modif = false;
		boolean select = true;
		AimMotors motors = new AimMotors();
		motors.calibrate();
		motors.startAim();
		do{
			if(Button.RIGHT.isDown() == true && modif != true){
				if(select == true) 
					h += 10;
				else
					v += 10;
				modif = true;
			}	
			if(Button.LEFT.isDown() == true && modif != true){
				if(select == true) 
					h -= 10;
				else
					v -= 10;
				modif = true;
			}
			if(Button.ENTER.isDown() == true && modif != true){
				select = !select;
				modif = true;
			}
			if(Button.RIGHT.isDown() == false && Button.LEFT.isDown() == false && Button.ENTER.isDown() == false)
				modif = false;
				
			motors.setTarget(h, v);
			LCD.drawInt(h, 4, 0 , 0);
			LCD.drawInt(v, 4, 0 , 1);
		}while(Button.ESCAPE.isDown() == false);
		motors.stopAimMotors();
	}
	
	static public void testAngle(){
		do{
			if(Button.ENTER.isDown() == true){
				MotorPort.A.resetTachoCount();
				MotorPort.C.resetTachoCount();
				LCD.clear();
			}
			LCD.drawInt(MotorPort.A.getTachoCount(), 0, 0);
			LCD.drawInt(MotorPort.C.getTachoCount(), 0, 1);
		}while(Button.ESCAPE.isDown() == false);
	}
	
	static public void testThread(){
		Thread a = new Thread(){
			public void run(){
				while(true)
					LCD.drawString("A", 0, 0);
			}
		};
		Thread b = new Thread(){
			public void run(){
				while(true)
					LCD.drawString(" B", 0, 0);
			}
		};
		a.start();
		b.start();
	}

	public static void testCom(){
		NXTConnection conn;
		InputStream iS;
		byte[] data = {0};
		conn = USB.waitForConnection(30000, NXTConnection.PACKET);
		iS = conn.openInputStream();
		while(Button.ESCAPE.isDown() == false){
			try {
				iS.read(data);
				for(int i = 0; i < data.length; i++)
					LCD.drawInt(data[i], 0, i);
			} catch (IOException e) {
			}
			Sound.beep();
			Button.waitForAnyPress();
		}
		
		conn.close();
	}

}
