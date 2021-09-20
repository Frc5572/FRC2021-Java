// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.lang.Math;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Servo;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

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


  // public var for shooter PID
  double v1;
  double v2;
  int PCM1 = 0;
  int PCM2 = 1;
  double servoPos = 0;


  // initialize timer
  Timer timer = new Timer();

  // init compressor
  Compressor compressor = new Compressor();

  DoubleSolenoid intakeSol = new DoubleSolenoid(PCM1, 6, 1);
  DoubleSolenoid climberSol2 = new DoubleSolenoid(PCM1, 7, 0);
  DoubleSolenoid climberSol1 = new DoubleSolenoid(PCM2, 4, 3);
  DoubleSolenoid hopperSol = new DoubleSolenoid(PCM1, 5, 2);


  //                         not right ?????????????????????????????????????????????????????????????
  Servo leftServo = new Servo(1);

  // initialize  talon motors
  WPI_TalonSRX m_frontLeft = new WPI_TalonSRX(4);
  // WPI_TalonSRX m_middleLeft = new WPI_TalonSRX(6);
  WPI_TalonSRX m_backLeft = new WPI_TalonSRX(8);
  SpeedControllerGroup leftDriveMotors = new SpeedControllerGroup(m_frontLeft, m_backLeft);
  WPI_TalonSRX m_frontRight = new WPI_TalonSRX(3);
  // WPI_TalonSRX m_middleRight = new WPI_TalonSRX(3);
  WPI_TalonSRX m_backRight = new WPI_TalonSRX(2);
  SpeedControllerGroup rightDriveMotors = new SpeedControllerGroup(m_frontRight, m_backRight);

  WPI_TalonSRX m_shooterLeft = new WPI_TalonSRX(12);
  WPI_TalonSRX m_shooterRight = new WPI_TalonSRX(14);

  // initialize neo motors
  CANSparkMax m_TurretMotor = new CANSparkMax(13, MotorType.kBrushless);

  // init speed controller groups
  SpeedControllerGroup shooterMotors = new SpeedControllerGroup(m_shooterLeft, m_shooterRight);

  // PID object
  PIDController pid = new PIDController(.0045, .0, 0, 100); 

  CANSparkMax m_Climber1 = new CANSparkMax(16, MotorType.kBrushless);
  CANSparkMax m_Climber2 = new CANSparkMax(15, MotorType.kBrushless);
  SpeedControllerGroup climberMotors = new SpeedControllerGroup(m_Climber1, m_Climber2);

  // controllers
  Controller driver = new Controller(0);
  Controller operator = new Controller(1);


  @Override
  public void robotInit() {

    // smart dashboard put PID values
    // SmartDashboard.putNumber("P Gain", 0);
    // SmartDashboard.putNumber("I Gain", 0);
    // SmartDashboard.putNumber("D Gain", 0);
    // SmartDashboard.putNumber("SetPoint", 0);

    // set motors to 0 at beginning
    m_frontLeft.set(ControlMode.PercentOutput, 0);
    m_frontRight.set(ControlMode.PercentOutput, 0);
    // m_middleLeft.set(ControlMode.PercentOutput, 0);
    // m_middleRight.set(ControlMode.PercentOutput, 0);
    m_backLeft.set(ControlMode.PercentOutput, 0);
    m_backRight.set(ControlMode.PercentOutput, 0);

    m_shooterLeft.set(ControlMode.PercentOutput, 0);
    m_shooterRight.set(ControlMode.PercentOutput, 0);
    

    leftServo.set(0);
    leftServo.setBounds(2.0, 1.8, 1.5, 1.2, 1.0);

    // set neutral mode
    m_frontLeft.setNeutralMode(NeutralMode.Coast);
    m_frontRight.setNeutralMode(NeutralMode.Coast);
    // m_middleLeft.setNeutralMode(NeutralMode.Brake);
    // m_middleRight.setNeutralMode(NeutralMode.Brake);
    m_backLeft.setNeutralMode(NeutralMode.Coast);
    m_backRight.setNeutralMode(NeutralMode.Coast);

    m_shooterLeft.setNeutralMode(NeutralMode.Coast);
    m_shooterRight.setNeutralMode(NeutralMode.Coast);

    // invert motors
    m_shooterLeft.setInverted(true);
    rightDriveMotors.setInverted(true);

    // start compressor
    compressor.setClosedLoopControl(true);
    compressor.start();

    // set solenoids to start position
    intakeSol.set(Value.kForward);
    hopperSol.set(Value.kForward);
    climberSol1.set(Value.kReverse);
    climberSol2.set(Value.kReverse);

    m_Climber1.setInverted(true);

    // frontLeftSpeed.set(ControlMode.Follower, 5);

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  @Override
  public void robotPeriodic() {
    // assign variable to shooter motor speed
    double s1 = m_shooterLeft.getSelectedSensorVelocity();
    double s2 = m_shooterRight.getSelectedSensorVelocity();
  
    // average speed between two shooter motors
    double speed = (s1 + s2) / 2;

    // output shooter motor speed to smartdashboard
    SmartDashboard.putNumber("RPM", speed);
  }

  @Override
  public void autonomousInit() {

    // start timer
    timer.start();

    m_autoSelected = m_chooser.getSelected();
    m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

  }

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

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {

    // servo to .5 on driver.X
    // if(operator.getRawButton(X_BUTTON)){
    //   leftServo.set(servoPos);
    //   servoPos = servoPos + 0.01;
    // } 
    // else if (operator.getRawButton(Y_BUTTON)){
    //   leftServo.set(servoPos);
    //   servoPos = servoPos - 0.01;
    // }
    // shooter on driver right trigger
    // if(driver.getRawAxis(RIGHT_Z) > .4){
    //   m_shooterLeft.set(ControlMode.PercentOutput, .7);
    //   m_shooterRight.set(ControlMode.PercentOutput, .7);
    // } else {
    //   m_shooterLeft.set(ControlMode.PercentOutput, 0);
    //   m_shooterRight.set(ControlMode.PercentOutput, 0);
    // }
    // intake on B
    if(driver.B()){
      intakeSol.set(Value.kReverse);
    }
    else{
      intakeSol.set(Value.kForward);
    }
    // hopper on A
    if(driver.A()){
      hopperSol.set(Value.kReverse);
    }
    else{
      hopperSol.set(Value.kForward);
    }
    // climber 1 on X
    if(driver.X()){
      climberSol1.set(Value.kForward);
      System.out.println("Pressed X");
    }
    else{
      climberSol1.set(Value.kReverse);
    }
    // climber 2 on Y
    if(driver.Y()){
      climberSol2.set(Value.kForward);
      System.out.println("Pressed Y");
    }
    else{
      climberSol2.set(Value.kReverse);
    }
    if(operator.RB()){
      m_TurretMotor.set(.1);
    }
    else if(operator.LB()){
      m_TurretMotor.set(-.1);
    }
    else {
      m_TurretMotor.set(0);
    }

    if(driver.POVDown()){
      climberMotors.set(.6);
      System.out.println("down");
    }
    else{
      climberMotors.set(0);
    }
    if(Math.abs(driver.L()) > .2){
      leftDriveMotors.set(-driver.L() / 2);
    } else {
      leftDriveMotors.set(0);
      rightDriveMotors.set(0);
    }
    if(Math.abs(driver.R()) > .2){
      rightDriveMotors.set(-driver.R() / 2);
    } else {
      leftDriveMotors.set(0);
      rightDriveMotors.set(0);
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