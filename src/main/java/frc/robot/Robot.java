// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

// import static java.lang.Math.abs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.SpeedControllerGroup;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  
  // create constants
  final int LEFT_Z = 2;
  final int LEFT_X = 0;
  final int LEFT_Y = 1;
  final int RIGHT_Z = 3;
  final int RIGHT_X = 4;
  final int RIGHT_Y = 5;
  final int LEFT_BUMPER = 5;
  final int RIGHT_BUMPER = 6;
  final int X_BUTTON = 3;
  final int Y_BUTTON = 4;
  final int B_BUTTON = 2;
  final int A_BUTTON = 1;
  final int START_BUTTON = 8;
  final int BACK_BUTTON = 7;
  final int LEFT_STICK_BUTTON = 9;
  final int RIGHT_STICK_BUTTON = 10;
  final int POVDown = 180;
  final int POVUp = 0;
  final int POVLeft = 270;
  final int POVRight = 90;
  int PCM1 = 0;
  int PCM2 = 1;


  // initialize timer
  Timer timer = new Timer();

  Compressor compressor = new Compressor();

  DoubleSolenoid intakeSol = new DoubleSolenoid(PCM1, 6, 1);
  DoubleSolenoid climberSol2 = new DoubleSolenoid(PCM1, 7, 0);
  DoubleSolenoid climberSol1 = new DoubleSolenoid(PCM2, 4, 3);
  DoubleSolenoid hopperSol = new DoubleSolenoid(PCM1, 5, 2);

  // initialize motor names and ID
  WPI_TalonSRX m_frontLeft = new WPI_TalonSRX(4);
  WPI_TalonSRX m_frontRight = new WPI_TalonSRX(2);
  WPI_TalonSRX m_middleLeft = new WPI_TalonSRX(6);
  WPI_TalonSRX m_middleRight = new WPI_TalonSRX(3);
  WPI_TalonSRX m_backLeft = new WPI_TalonSRX(8);
  WPI_TalonSRX m_backRight = new WPI_TalonSRX(7);

  WPI_TalonSRX m_hopperLeft = new WPI_TalonSRX(9);
  WPI_TalonSRX m_hopperRight = new WPI_TalonSRX(10);
  SpeedControllerGroup hopperMotors = new SpeedControllerGroup(m_hopperLeft, m_hopperRight);

  

  // controllers
  Joystick driver = new Joystick(0);
  Joystick operator = new Joystick(1);

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {

    // set motors to 0 at beginning
    m_frontLeft.set(ControlMode.PercentOutput, 0);
    m_frontRight.set(ControlMode.PercentOutput, 0);
    m_middleLeft.set(ControlMode.PercentOutput, 0);
    m_middleRight.set(ControlMode.PercentOutput, 0);
    m_backLeft.set(ControlMode.PercentOutput, 0);
    m_backRight.set(ControlMode.PercentOutput, 0);

    // set neutral mode
    m_frontLeft.setNeutralMode(NeutralMode.Brake);
    m_frontRight.setNeutralMode(NeutralMode.Brake);
    m_middleLeft.setNeutralMode(NeutralMode.Brake);
    m_middleRight.setNeutralMode(NeutralMode.Brake);
    m_backLeft.setNeutralMode(NeutralMode.Brake);
    m_backRight.setNeutralMode(NeutralMode.Brake);


    compressor.setClosedLoopControl(true);
    compressor.start();

    // set solenoids to start position
    intakeSol.set(Value.kForward);
    hopperSol.set(Value.kForward);
    climberSol1.set(Value.kReverse);
    climberSol2.set(Value.kReverse);

    m_hopperRight.setInverted(true);
    // frontLeftSpeed.set(ControlMode.Follower, 5);

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // if(abs(driver.getRawAxis(LEFT_X)) > 0.04){
    //   System.out.println(driver.getRawAxis(LEFT_X));
    // }
    // if(abs(driver.getRawAxis(LEFT_Y)) > 0.04){
    //   System.out.println(driver.getRawAxis(LEFT_Y));
    // }
    // if(abs(driver.getRawAxis(LEFT_Y)) > 0.04){
    //   System.out.println(driver.getRawAxis(LEFT_Y));
    // }
    // if(abs(driver.getRawAxis(RIGHT_X)) > 0.04){
    //   System.out.println(driver.getRawAxis(RIGHT_X));
    // }
    // if(abs(driver.getRawAxis(RIGHT_Y)) > 0.04){
    //   System.out.println(driver.getRawAxis(RIGHT_Y));
    // }
    // if(abs(driver.getRawAxis(RIGHT_Z)) > 0.04){
    //   System.out.println(driver.getRawAxis(RIGHT_Z));
    // }
    // if(abs(driver.getRawAxis(LEFT_Z)) > 0.04){
    //   System.out.println(driver.getRawAxis(LEFT_Z));
    // }

    // // driver buttons
    // if(abs(driver.getRawAxis(LEFT_X)) > 0.04){
    //   System.out.println("LEFT_X");
    // }
    // if(abs(driver.getRawAxis(LEFT_Y)) > 0.04){
    //   System.out.println("LEFT_Y");
    // }
    // if(abs(driver.getRawAxis(LEFT_Z)) > 0.04){
    //   System.out.println("LEFT_Z");
    // }
    // if(abs(driver.getRawAxis(RIGHT_X)) > 0.04){
    //   System.out.println("RIGHT_X");
    // }
    // if(abs(driver.getRawAxis(RIGHT_Y)) > 0.04){
    //   System.out.println("RIGHT_Y");
    // }
    // if(abs(driver.getRawAxis(RIGHT_Z)) > 0.04){
    //   System.out.println("RIGHT_Z");
    // }
    // if(driver.getRawButton(RIGHT_BUMPER)){
    //   System.out.println("RB");
    // }
    if(driver.getRawButton(LEFT_BUMPER)){
      System.out.println("LB");
    }
    // if(driver.getRawButton(X_BUTTON)){
    //   System.out.println("X");
    // }
    // if(driver.getRawButton(Y_BUTTON)){
    //   System.out.println("Y");
    // }
    // if(driver.getRawButton(A_BUTTON)){
    //   System.out.println("A");
    // }
    // if(driver.getRawButton(B_BUTTON)){
    //   System.out.println("B");
    // }
    // if(driver.getRawButton(START_BUTTON)){
    //   System.out.println("START");
    // }
    // if(driver.getRawButton(BACK_BUTTON)){
    //   System.out.println("BACK");
    // }
    // if(driver.getRawButton(LEFT_STICK_BUTTON)){
    //   System.out.println("LSTICK");
    // }
    // if(driver.getRawButton(RIGHT_STICK_BUTTON)){
    //   System.out.println("RSTICK");
    // }
    // if(driver.getPOV() == POVDown){
    //   System.out.println("POVDown");
    // }
    // if(driver.getPOV() == POVUp){
    //   System.out.println("POVUp");
    // }
    // if(driver.getPOV() == POVLeft){
    //   System.out.println("POVLeft");
    // }
    // if(driver.getPOV() == POVRight){
    //   System.out.println("POVRight");
    // }

    // // operator buttons
    // if(abs(operator.getRawAxis(LEFT_X)) > 0.04){
    //   System.out.println("LEFT_X");
    // }
    // if(abs(operator.getRawAxis(LEFT_Y)) > 0.04){
    //   System.out.println("LEFT_Y");
    // }
    // if(abs(operator.getRawAxis(LEFT_Z)) > 0.04){
    //   System.out.println("LEFT_Z");
    // }
    // if(abs(operator.getRawAxis(RIGHT_X)) > 0.04){
    //   System.out.println("RIGHT_X");
    // }
    // if(abs(operator.getRawAxis(RIGHT_Y)) > 0.04){
    //   System.out.println("RIGHT_Y");
    // }
    // if(abs(operator.getRawAxis(RIGHT_Z)) > 0.04){
    //   System.out.println("RIGHT_Z");
    // }
    // if(operator.getRawButton(RIGHT_BUMPER)){
    //   System.out.println("RB");
    // }
    // if(operator.getRawButton(LEFT_BUMPER)){
    //   System.out.println("LB");
    // }
    // if(operator.getRawButton(X_BUTTON)){
    //   System.out.println("X");
    // }
    // if(operator.getRawButton(Y_BUTTON)){
    //   System.out.println("Y");
    // }
    // if(operator.getRawButton(A_BUTTON)){
    //   System.out.println("A");
    // }
    // if(operator.getRawButton(B_BUTTON)){
    //   System.out.println("B");
    // }
    // if(operator.getRawButton(START_BUTTON)){
    //   System.out.println("START");
    // }
    // if(operator.getRawButton(BACK_BUTTON)){
    //   System.out.println("BACK");
    // }
    // if(operator.getRawButton(LEFT_STICK_BUTTON)){
    //   System.out.println("LSTICK");
    // }
    // if(operator.getRawButton(RIGHT_STICK_BUTTON)){
    //   System.out.println("RSTICK");
    // }
    // if(operator.getPOV() == POVDown){
    //   System.out.println("POVDown");
    // }
    // if(operator.getPOV() == POVUp){
    //   System.out.println("POVUp");
    // }
    // if(operator.getPOV() == POVLeft){
    //   System.out.println("POVLeft");
    // }
    // if(operator.getPOV() == POVRight){
    //   System.out.println("POVRight");
    // }
  }

  @Override
  public void autonomousInit() {

    // start timer
    timer.start();

    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    // if(driver.getRawAxis(LEFT_Y) > 0){
    //   m_frontLeft.set(ControlMode.PercentOutput, (driver.getRawAxis(LEFT_Y)));
    //   m_middleLeft.set(ControlMode.PercentOutput, (driver.getRawAxis(LEFT_Y)));
    //   m_backLeft.set(ControlMode.PercentOutput, (driver.getRawAxis(LEFT_Y)));
    // }
    // if(driver.getRawAxis(RIGHT_Y) > 0){
    //   m_frontRight.set(ControlMode.PercentOutput, (driver.getRawAxis(RIGHT_Y)));
    //   m_middleRight.set(ControlMode.PercentOutput, (driver.getRawAxis(RIGHT_Y)));
    //   m_backRight.set(ControlMode.PercentOutput, (driver.getRawAxis(RIGHT_Y)));
    // }    

    if(driver.getRawButton(LEFT_BUMPER)){
      hopperMotors.set(0.4);
    } else {
      hopperMotors.set(0);
    }

    // intake on B
    if(driver.getRawButton(B_BUTTON)){
      intakeSol.set(Value.kReverse);
    }
    else{
      intakeSol.set(Value.kForward);
    }
    // hopper on A
    if(driver.getRawButton(A_BUTTON)){
      hopperSol.set(Value.kReverse);
    }
    else{
      hopperSol.set(Value.kForward);
    }
    // climber 1 on X
    if(driver.getRawButton(X_BUTTON)){
      climberSol1.set(Value.kForward);
      System.out.println("Pressed X");
    }
    else{
      climberSol1.set(Value.kReverse);
    }
    // climber 2 on Y
    if(driver.getRawButton(Y_BUTTON)){
      climberSol2.set(Value.kForward);
      System.out.println("Pressed Y");
    }
    else{
      climberSol2.set(Value.kReverse);
    }
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
