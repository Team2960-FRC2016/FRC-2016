package org.usfirst.frc.team2960.robot.subsystems;

import org.usfirst.frc.team2960.robot.PeriodicUpdate;
import org.usfirst.frc.team2960.robot.RobotMap;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DriveTrain extends Subsystem implements PeriodicUpdate {
    
	
	Victor LtDriveMt1;
	Victor LtDriveMt2;
	Victor RtDriveMt1;
	Victor RtDriveMt2;
	AnalogGyro gyro;
	Encoder RightDriveEnc;
	TurnControl turn; 
	LinearDriveControl linear;
	PIDController turning;
	PIDController move;
	public boolean moveStop;
	public boolean linearStop;
	boolean isEnadled;
	double angleSetpoint = 0;
	double lengthSetPoint = 0;
	final int tolerance = 2;
	final double angleSlowDown = 50;
	final int slowDown = 10;
	final double rateTolerance = 5;
	int RateSetPoint = 50;
	int angleDirection;
	Double Rate;
	final double FINALRATE = 200;
	public boolean TurnOnTheTurn =  false;
	Camera camera;
	
	public DriveTrain(Camera Cam)
	{
		LtDriveMt1 = new Victor(RobotMap.LtDriveMt1);
		LtDriveMt2 = new Victor(RobotMap.LtDriveMt2);
		RtDriveMt1 = new Victor(RobotMap.RtDriveMt1);
		RtDriveMt2 = new Victor(RobotMap.RtDriveMt2);
		gyro = new AnalogGyro(RobotMap.gyro);
		turn = new TurnControl(this);
		turning = new PIDController(RobotMap.turnControlP, RobotMap.turnControlI, RobotMap.turnControlD, gyro, turn);
		gyro.setPIDSourceType(PIDSourceType.kRate);
		RightDriveEnc = new Encoder(RobotMap.RtDriveEncA, RobotMap.RtDriveEncB);
		RightDriveEnc.setDistancePerPulse(.1); 
		camera = Cam;
		//move = new PIDController(RobotMap.moveP, RobotMap.moveI, RobotMap.moveD, RightDriveEnc, linear);
	}

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	RtDriveMt1.set(0);
    	RtDriveMt2.set(0);
    	LtDriveMt1.set(0);
    	LtDriveMt2.set(0);
    }
    
    public void displayGyroValue() {
    	SmartDashboard.putString("gyroAngle", Double.toString(gyro.getAngle()));
    	SmartDashboard.putString("gyroRate", Double.toString(gyro.getRate()));
    }
    
    public void calibrateGyro()
    {
    	gyro.calibrate();
    }
    
    public void resetGyro()
    {
    	gyro.reset();
    }
    public void resetEncoder()
    {
    	RightDriveEnc.reset();
    }
    
    
    public void setSpeed(Double left, Double right){
    	System.out.print("Left: ");
    	System.out.print(left);
    	System.out.print(", Right: ");
    	System.out.print(right);
    	System.out.print("\n");
    	
    	LtDriveMt1.set(left);
    	LtDriveMt2.set(left);
    	RtDriveMt1.set(-right);
    	RtDriveMt2.set(-right);
    }
    /*
    public void move()
    {
    	lengthSetPoint = 10;
    	RightDriveEnc.reset();
    	move.enable();
    	move.setSetpoint(50);
    	linearStop = true;
    } 
    */
    public void gotoAngle(double angle){
    	angleSetpoint = angle;
    	turning.enable();
    	moveStop = true;
    	
    }
    public void addAngle(double angle){
    	angleSetpoint = gyro.getAngle() + angle;
    	turning.enable();
    	moveStop = true;
    }
    
    public void disablePIDAngle(){
    	if(turning.isEnabled()){
    	turning.disable();
    	moveStop = false;
    	
    	}
    }
    /*
    public void disablePIDForward()
    {
    	if(move.isEnabled()){
    		move.disable();
    		linearStop = false;
    	}
    	
    }
    */
    public void checkAngle(){
    	
	    if(turning.isEnabled()){
		Double error = angleSetpoint - gyro.getAngle(); 	
			
	    	if((gyro.getAngle() >= (angleSetpoint - tolerance)) && (gyro.getAngle() <= (angleSetpoint + tolerance))){
	    		turning.setSetpoint(0);
	    		
	    		
	    		if((gyro.getRate() >= (-rateTolerance)) && (gyro.getRate() <= (rateTolerance)) ){
	    			disablePIDAngle();
	    			}
	    	
	    		
	    		
	    	}
	    	else if(error > -angleSlowDown && error < angleSlowDown){
	    		Rate = error/angleSlowDown * FINALRATE;
	    		turning.setSetpoint(Rate);
	    	}
	    	else if(gyro.getAngle() > angleSetpoint){
	    		turning.setSetpoint(-FINALRATE);
	    	}
	    	else if(gyro.getAngle() < angleSetpoint){
	    		turning.setSetpoint(FINALRATE);
	    	}
	    	
	    	
	    		
	    }
	    
	    }
    
    
    
     public BuiltInAccelerometer accel = new BuiltInAccelerometer();
	@Override
	public void update() {
		
		if(TurnOnTheTurn){
			addAngle(camera.getAngle());
		}
		
		
		
		
		SmartDashboard.putBoolean("Encoder direction", RightDriveEnc.getDirection());
		SmartDashboard.putString("Encoder Distance", Double.toString(RightDriveEnc.getDistance()));
		SmartDashboard.putString("Encoder count", Double.toString(RightDriveEnc.get()));
		SmartDashboard.putString("Encoder Rate", Double.toString(RightDriveEnc.getRate()));
		SmartDashboard.putString("x", Double.toString(accel.getX()));
    	SmartDashboard.putString("y", Double.toString(accel.getY()));
    	SmartDashboard.putString("z", Double.toString(accel.getZ()));
    	SmartDashboard.putNumber("setpont", turning.getSetpoint());
		SmartDashboard.putNumber("error", turning.getError());
    
    	this.displayGyroValue();
    	checkAngle();
    	
    	
	}

	@Override
	public void start() {
		this.calibrateGyro();
		
	}
}

