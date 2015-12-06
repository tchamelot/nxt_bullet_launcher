package com.chamelot.robot;

import com.chamelot.communication.Communication;
import com.chamelot.communication.NXTCommException;
import com.chamelot.communication.ReadListener;
import com.chamelot.motor.AimMotors;

import lejos.nxt.LCD;

public class BulletLauncher {
	private AimMotors motors;
	private Communication comm;
	private int hTarget;
	private int vTarget;
	
	public BulletLauncher(){
		motors = new AimMotors();
		motors.calibrate();
		hTarget = 0;
		vTarget = 0;
		motors.startAim();
		try {
			comm = new Communication(Communication.USB_MODE);
			comm.addReadListener(new ReadListener(){

				@Override
				public void readNew(byte[] data) {
					perform(data);
					//for(int i = 0; i < data.length; i++)
						//LCD.drawInt(data[i], 3, 0, i);
				}

				@Override
				public void readEOC() {
					comm.closeCommunication();
					motors.stopAimMotors();
				}
				
			});
			
		} 
		catch (NXTCommException e) {
		}
	}
	
	private void perform(byte[] data){
		for(byte i : data){
			switch(i){
			case 117:
				hTarget+=5;
				break;
			case 100:
				hTarget-=5;
				break;
			case  108:
				vTarget-=5;
				break;
			case 114:
				vTarget+=5;
				break;
			}
			motors.setTarget(vTarget, hTarget);
			LCD.drawInt(hTarget, 3, 0, 0);
			LCD.drawInt(vTarget, 3, 0, 1);
		}
	}
}
