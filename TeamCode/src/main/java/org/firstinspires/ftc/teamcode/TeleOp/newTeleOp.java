package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.teamcode.Hardware.DeuxBoot;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="Tele-Op",group="TeleOp")
public class newTeleOp extends OpMode {
    DeuxBoot robot = new DeuxBoot();
    double speed;
    boolean buttonAheld = false;
    boolean grabberClosed = true;

    @Override
    public void init() {
        robot.initNew(hardwareMap);

        new Thread()  {
            public void run() {
                robot.initGyro();
                telemetry.addData("Gyro Initialized", robot.isGyroInit());
            }
        }.start();

    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        robot.runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the r hits PLAY but before they hit STOP
     */

    @Override
    public void loop() {
        robot.tankDrive(gamepad1.left_stick_x,
                gamepad1.left_stick_y,
                gamepad1.right_stick_x,
                gamepad1.left_trigger,
                gamepad1.right_trigger,
                speed);

        robot.wobbleMotor.setPower(gamepad2.left_stick_y * 0.5);
        robot.intake.setPower(gamepad2.right_stick_y);
        robot.outtake.setPower(gamepad2.right_trigger);

        if (gamepad1.b) {
            speed = 0.25;
        } else if (gamepad1.a) {
            speed = 1;
        } else if (gamepad1.y) {
            speed = -1;
        }
        telemetry.addData("Speed", speed);

        if (gamepad2.a && !buttonAheld) {
            buttonAheld = true;
            if (grabberClosed) {
                grabberClosed = false;
                robot.wobbleServo.setPosition(1);
            } else {
                grabberClosed = true;
                robot.wobbleServo.setPosition(0);
            }
        }

        if (!gamepad2.a) {
            buttonAheld = false;
        }
        telemetry.addData("FL Power", robot.FL.getPower());
        telemetry.addData("FR Current", robot.FR.getPower());
        telemetry.addData("BL Current", robot.BL.getPower());
        telemetry.addData("BR Current", robot.BR.getPower());
        telemetry.update();
    }
        /*
         * Code to run ONCE after the driver hits STOP
         */

        @Override
        public void stop () {
        }
}

