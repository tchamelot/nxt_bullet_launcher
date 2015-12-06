package com.chamelot.motor;

import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;

public class Motor extends NXTMotor{
	private boolean stalled;
	private boolean stallTest;
	private boolean moving;
	private Thread stallTester;
	
	private int defaultPower;
	
	private Thread rotateTo;
	private boolean rotating;
	private int targetAngle;
	
	public Motor(MotorPort port){
		super(port);
		defaultPower = 100; 
		stallTest = false;
		moving = false;
		rotating = false;
	}
	
	public Motor(MotorPort port, int power){
		super(port);
		defaultPower = power; 
		stallTest = false;
		moving = false;
		rotating = false;
	}
	
	
	public void forward(){
		super.forward();
		this.setPower(defaultPower);
		moving = true;
	}
	
	public void forward(int power){
		super.forward();
		moving = true;
		this.setPower(power);
	}

	public void backward(){
		super.backward();
		moving = true;
		this.setPower(defaultPower);
	}
	
	public void backward(int power){
		super.backward();
		moving = true;
		this.setPower(power);
	}
	
	public void stop(){
		super.stop();
		moving = false;
	}
	
	
	public void waitForStall(){
		if(stallTest)
			while(stalled != true){	
			}
	}
	
	public boolean isStalled(){
		return (stalled && stallTest);
	}
	
	
	public void startStallTest(){
		if(stallTest != true){
			stallTest = true;
			stallTester = new Thread(new StallTester());
			stallTester.start();
		}
	}
	
	public void stopStallTest(){
		stallTest = false;
	}
	
	private class StallTester implements Runnable{

		public void run() {
			long timeStalled = 0;
			int angle = Motor.this.getTachoCount();
			
			while(stallTest){
				if(moving){
					int temp = Motor.this.getTachoCount();
					if(temp == angle){
						if(timeStalled == 0)
							timeStalled = System.currentTimeMillis();
						if((System.currentTimeMillis() - timeStalled) > 50){
							stalled = true;
						}
						else
							stalled = false;
					}
					else{
						timeStalled = 0;
						stalled = false;
					}
					angle = temp;
				}
				else
					stalled = false;
			}
		}
	}
	
	
	private class RotateTo implements Runnable{

		public void run() {
			if(targetAngle > Motor.this.getTachoCount()){
				Motor.this.forward();
			}
			
			if(targetAngle < Motor.this.getTachoCount()){
				Motor.this.backward();
			}
			
			while(targetAngle != Motor.this.getTachoCount()){
				LCD.drawInt(targetAngle, 0, 0);
				LCD.drawInt(Motor.this.getTachoCount(), 0, 1);
			}
			Motor.this.stop();
			rotating =false;
			rotateTo = null;
		}
	}
	
	public void rotateTo(int angle){
		if(rotating ==  false){
			rotating = true;
			targetAngle = angle;
			rotateTo = new Thread(new RotateTo());
			rotateTo.start();
		}
	}

	public boolean rotationDone(){
		return rotating;
	}
}
