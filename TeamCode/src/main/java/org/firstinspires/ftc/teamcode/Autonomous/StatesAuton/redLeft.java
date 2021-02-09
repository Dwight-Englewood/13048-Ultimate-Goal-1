package org.firstinspires.ftc.teamcode.Autonomous.StatesAuton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Autonomous.Methods.NewAutonMethods;
import org.firstinspires.ftc.teamcode.Autonomous.Methods.ZoneDetect;
import org.firstinspires.ftc.teamcode.Hardware.Movement;

@Autonomous(name = "Red Left", group = "Autonomous")
public class redLeft extends OpMode {
    NewAutonMethods robot = new NewAutonMethods();
  //  ZoneDetect detector = new ZoneDetect();
    public boolean ZoneA = true, ZoneB = false, ZoneC = false;

    public void init() {
        robot.initNew(hardwareMap, telemetry); // init all ur motors and crap (NOTE: DO NOT INIT GYRO OR VISION IN THIS METHOD)

        new Thread()  {
            public void run() {
                robot.initGyro();
                robot.isGyroInit();// whatever ur init gyro method is on robot
            }
        }.start();

        /*new Thread(){
            public void run() {
                detector.init(hardwareMap, telemetry);
                detector.isInit();// whatever ur vision init method is
                //        detector.start();
            }
        }.start();
        robot.initClaws();
        robot.runtime.reset();

         */
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
   /*     detector.detectionLoopBlue();
        if (robot.runtime.milliseconds() >= 1000){
            detector.resetValues();
            robot.runtime.reset();
        }
        telemetry.addData("rightConf", detector.rightConf);
        telemetry.addData("midConf", detector.midConf);
        telemetry.addData("leftConf", detector.leftConf);

    */
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
  //      detector.blockFinder();
        robot.runtime.reset();
        robot.changeRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        switch (robot.command) {
            case 0:
                if (ZoneA) {
                    robot.command = 1;
                } else if (ZoneB) {
                    robot.command = 100;
                } else if (ZoneC) {
                    robot.command = 1000;
                }
//Zone A
            case 1:
                robot.setTarget(Movement.LEFTSTRAFE, 180);
                break;

            case 2:
                robot.finishDrive();
                break;

            case 3:
                robot.lowerWobble();
                if (robot.runtime.milliseconds() > 500) {
                    robot.openWobbleServo();
                    robot.command++;
                }
                break;

            case 4:
                robot.raiseWobble();
                if (robot.runtime.milliseconds() > 500) {
                    robot.command++;
                }
                break;

            case 5:
                robot.setTarget(Movement.LEFTSTRAFE, 30);
                break;

            case 6:
                robot.finishDrive();
                break;
//Zone B
            case 101:
                robot.setTarget(Movement.LEFTSTRAFE, 210);
                break;

            case 102:
                robot.finishDrive();
                break;

            case 103:
                robot.gyroTurn(-45);
                robot.lowerWobble();
                if (robot.runtime.milliseconds() > 500) {
                    robot.openWobbleServo();
                    robot.command++;
                }
                break;

            case 104:
                robot.gyroTurn(45);
                robot.raiseWobble();
                if (robot.runtime.milliseconds() > 500) {
                    robot.command++;
                }
                break;

            case 105:
                robot.setTarget(Movement.RIGHTSTRAFE, 60);
                break;

            case 106:
                robot.finishDrive();
                break;
//Zone C
            case 1001:
                robot.setTarget(Movement.LEFTSTRAFE, 240);
                break;

            case 1002:
                robot.finishDrive();
                break;

            case 1003:
            robot.lowerWobble();
            if (robot.runtime.milliseconds() > 500) {
                robot.openWobbleServo();
                robot.command++;
            }
            break;

            case 1004:
                robot.raiseWobble();
                if (robot.runtime.milliseconds() > 500) {
                    robot.command++;
                }
                break;

            case 1005:
                robot.setTarget(Movement.RIGHTSTRAFE, 90);
                break;

            case 1006:
                robot.finishDrive();
                break;
        }

        telemetry.addData("Case:", robot.command);

        telemetry.addData("Failsafe:", robot.timerFailSafe());
        telemetry.addData("runtime", robot.runtime.milliseconds());

        telemetry.addData("Powersafe:", robot.tinyPowerValue());
        telemetry.addData("power", robot.FL.getPower());
        telemetry.update();
    }
}
