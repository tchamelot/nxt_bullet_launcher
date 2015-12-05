package com.chamelot.motor;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;

public class Motor extends NXTMotor{
	private boolean stalled;
	private boolean stallTest;
	private Thread stallTester;
	private int defaultPower;
	
	public Motor(MotorPort port){
		super(port);
		defaultPower = 100; 
		stallTest = false;
	}
	
	public Motor(MotorPort port, int power){
		super(port);
		defaultPower = power; 
		stallTest = false;
	}
	
	public void forward(){
		super.forward();
		this.setPower(defaultPower);
	}
	
	public void forward(int power){
		super.forward();
		this.setPower(power);
	}

	public void backward(){
		super.backward();
		this.setPower(defaultPower);
	}
	
	public void backward(int power){
		super.backward();
		this.setPower(power);
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
				if(Motor.this.isMoving()){
					int temp = Motor.this.getTachoCount();
					if(temp == angle){
						if(timeStalled == 0)
							timeStalled = System.currentTimeMillis();
						if((System.currentTimeMillis() - timeStalled) > 50){
							//Sound.beep();
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

	public void rotateTo(int angle){
		if(angle > this.getTachoCount()){
			this.forward();
			this.setPower(40);
		}
		if(angle < this.getTachoCount()){
			this.backward();
			this.setPower(40);
		}
		while(angle != this.getTachoCount()){
			
		}
		this.stop();
	}
}
