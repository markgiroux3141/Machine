public class RotationalForce {
    private Machine inputMachine;
    private Machine outputMachine;
    private double torque;
    private double angularVelocity;
    private double leverArm;

    public RotationalForce(double leverArm) {
        torque = 0;
        angularVelocity = 0;
        this.leverArm = leverArm;
    }

    public RotationalForce(double torque, double angularVelocity) {
        this.torque = torque;
        this.angularVelocity = angularVelocity;
    }

    public double getTorque() {
        return torque;
    }

    public void setTorque(double torque) {
        this.torque = torque;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public Machine getInputMachine() {
        return inputMachine;
    }

    public void setInputMachine(Machine inputMachine) {
        this.inputMachine = inputMachine;
    }

    public Machine getOutputMachine() {
        return outputMachine;
    }

    public void setOutputMachine(Machine outputMachine) {
        this.outputMachine = outputMachine;
    }

    public double getLeverArm() {
        return leverArm;
    }

    public void setLeverArm(double leverArm) {
        this.leverArm = leverArm;
    }
}
