public class Battery implements Machine {

    private double voltage;
    private double internalResistance;

    public Battery(double voltage, double internalResistance) {
        this.voltage = voltage;
        this.internalResistance = internalResistance;
    }

    @Override
    public void stepMachine(){

    }

    public double getVoltage() {
        return voltage;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getInternalResistance() {
        return internalResistance;
    }

    public void setInternalResistance(double internalResistance) {
        this.internalResistance = internalResistance;
    }
}
