package testes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class Teleop extends LinearOpMode {

    //ajustes
    private static final double DEADZONE = 0.15 ;
    private static final double SLOW_MODE_FACTOR = 0.3;


//função para auxiliar
    private double applyDeadzone(double value ) {
        return Math.abs(value) < DEADZONE ? 0.0 : value;


    }


    // motores

    DcMotor frenteESQ;
    DcMotor frenteDIR;
    DcMotor trasESQ;
    DcMotor trasDIR;



    //comandos do piloto

    private double forward;
    private double strafe;
    private double turn;

    @Override
    public void runOpMode() {

        frenteESQ = hardwareMap.get(DcMotor.class, "frenteESQ");
        frenteDIR = hardwareMap.get(DcMotor.class, "frenteDIR");
        trasESQ = hardwareMap.get(DcMotor.class, "trasESQ");
        trasDIR = hardwareMap.get(DcMotor.class,"trasDIR");

        frenteESQ.setDirection(DcMotorEx.Direction.REVERSE);
        frenteDIR.setDirection(DcMotorSimple.Direction.FORWARD);
        trasESQ.setDirection(DcMotorEx.Direction.REVERSE);
        trasDIR.setDirection(DcMotorSimple.Direction.FORWARD);

        waitForStart();

        while (opModeIsActive()) {

            double forward = applyDeadzone(gamepad1.right_trigger - gamepad1.left_trigger);
            double strafe = applyDeadzone(gamepad1.left_stick_x);
            double turn = applyDeadzone(gamepad1.right_stick_x);

            double frontLeftPower = forward + strafe + turn;
            double frontRightPower = forward - strafe - turn;
            double backLeftPower = forward - strafe + turn;
            double backRightPOwer = forward + strafe - turn;

            //se algum valor passar de 1.0, reduz todos juntos pra manter a direção
            double largest = Math.max(1.0, Math.max(
                    Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower)), Math.max(Math.abs(frontRightPower), Math.abs(backRightPOwer))
            ));

            //enviar para os motores

            frenteESQ.setPower(frontLeftPower / largest);
            frenteDIR.setPower(frontRightPower / largest);
            trasESQ.setPower(backLeftPower / largest);
            trasDIR.setPower(backRightPOwer / largest);


            // telemetria neles
            telemetry.addData("Frente", "%.2f", forward);
            telemetry.addData("Lateral", "%.2f", strafe);
            telemetry.addData("Giro", "%.2f", turn);
        }





    }



}








