package susbsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;



public class TurretSubsystem {

    private Limelight3A limelight3A;
    private DcMotor turret;

    //ganhos do controlador
    private double kP = 0.0001;
    private double kD = 0.0000;
  //posição desejada do alvo
    private double goalX = 0;
    //erro da última leitura
    private double lastError = 0;
    //tolerancia para considerar que esta alinhado
    private double angleTolerance = 0.2;
    //limite máximo de potencia
    private final double MAX_POWER = 0.6;
    private double power = 0;
    private final ElapsedTime timer = new ElapsedTime();
    public void init(HardwareMap hwMap) {

        limelight3A = hwMap.get(limelight3A.getClass(), "limelight");
        turret = hwMap.get(DcMotor.class, "turret");
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

       limelight3A.pipelineSwitch(0);
       limelight3A.start();

       lastError = 0;

    }

    public void update() {
        LLResult llResult = limelight3A.getLatestResult();

        //verifica se a limeligt encontrou o alvo válido

        if (llResult == null || !llResult.isValid()) {
            turret.setPower(0);
            return;
        }

        // tx = deslocamento horizontal da AprilTag
        double tx = llResult.getTx();

        //calcula o erro em relação a posição desejada
        double error = goalX - tx;

        //se tiver suficientemente linhado, para a torreta

       if(Math.abs(error) <= angleTolerance) {
           turret.setPower(0);
           lastError = error;
           return;
       }

       //parte proporcional
        double proportional = kP * error;

       //parte derivativa
        double derivative = error - lastError;
        double derivativePower = kD * derivative;

        //potencia final
        double power = proportional + derivativePower;

        //limitaa a potencia da torreta
        power = Range.clip(power, -MAX_POWER,MAX_POWER);

        turret.setPower(power);

        // guarda o erro atual para a próxima leitura
        lastError = error;


    }

    public void stop() {
        turret.setPower(0);
        limelight3A.stop();

    }

    public void setkP(double kP) {
        this.kP = kP;
    }
    public double getkP() {
        return kP;
    }

    public void resetTimer() {
        lastError = 0;
    }

    // se o tx aumentar quando a torreta deveria diminuir, provavelmente a direçao do motor
    //ta invertida.
    // ent eu testo, trocando double error = goalX - tx;
    //por double error = tx - goalX;

    //dudaao, nao altera o kp, tu precisa confirmar a direçao do movimento
    //dps ajusta o kp pra torreta alinhar rapido sem oscilamento.


}

