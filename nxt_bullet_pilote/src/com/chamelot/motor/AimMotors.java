package com.chamelot.motor;

import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.Sound;

public class AimMotors {
	protected Motor leftMotor;
	protected Motor rightMotor;
	
	private Thread aim;
	private boolean run;
	private int leftTarget;
	private boolean leftForward;
	private int rightTarget;
	private boolean rightForward;
	
	public AimMotors(){
		leftMotor = new Motor(MotorPort.A, 30);
		rightMotor = new Motor(MotorPort.C, 30);
		
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
		
		leftMotor.startStallTest();
		rightMotor.startStallTest();

		run = false;
	}
	
	public void calibrate(){
		boolean leftDone = false, rightDone = false;
		leftMotor.forward();
		rightMotor.backward();
		while((!leftDone) || (!rightDone)){
			if(leftMotor.isStalled() == true){
				leftMotor.stop();
				leftDone = true;
			}
			if(rightMotor.isStalled() == true){
				rightMotor.stop();
				rightDone = true;
			}
		}
		
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
		leftMotor.rotateTo(-180);
		rightMotor.rotateTo(90);
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
	}

	public void stopAimMotors(){
		leftMotor.stopStallTest();
		rightMotor.stopStallTest();
		run = false;
	}
	
	public void startAim(){
		if(run != true){
			run = true;
			aim = new Thread(new Aim());
			aim.start();
		}
	}
	
	public void setTarget(int h, int v){
		if(h > 100) h = 100;
		if(h < -100) h = -100;
		if(v > 100) v = 100;
		if(v < 0) v = 0;
		leftTarget = (v + h) / 2;
		rightTarget = (v - h) / 2;
	}
	
	public class Aim implements Runnable{
		public void run() {
			int errLeft = 0;
			int errRight = 0;
			int errLeftPrev, errRightPrev;
			int powerLeft, powerRight;
			byte idx = 0;
			
			while(run){
				errLeftPrev = errLeft;
				errRightPrev = errRight;
					
				errLeft = leftTarget - leftMotor.getTachoCount();
				errRight = rightTarget - rightMotor.getTachoCount();
					
				powerLeft = errLeft * 2 + (errLeft - errLeftPrev) * 5;
				powerRight = errRight * 2 + (errRight - errRightPrev) * 5;
					
				if(powerLeft >= 0){
					leftForward = true;
					if(powerLeft > 40) powerLeft = 40;
				}
				else{
					leftForward = false;
					powerLeft = -powerLeft;
					if(powerLeft > 40) powerLeft = 40;
				}
				
				if(powerRight >= 0){
					rightForward = true;
					if(powerRight > 40) powerRight = 40;
				}
				else{
					rightForward = false;
					powerRight = -powerRight;
					if(powerRight > 40) powerRight = 40;
				}
				
				if(leftForward == true)
					leftMotor.forward(powerLeft);
				else
					leftMotor.backward(powerLeft);
				
				if(rightForward == true)
					rightMotor.forward(powerRight);
				else
					rightMotor.backward(powerRight);
			}
		}	
	}	
}
