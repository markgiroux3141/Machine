import java.awt.*;

public class Compressor implements Machine{
    private static final double GAMMA = 1.66;
    private static final double ATM_TO_PASCAL = 101325;
    private static final double KG_TO_NEWTONS = 9.81;
    private static final double PISTON_DAMPENING = 0.9f;
    private static final double MAX_LOAD_TORQUE_RATIO = 0.995;

    public enum STATE{
        READY,
        RUNNING,
        DONE
    }

    private RotationalForce inputValue;
    private STATE state;
    private Chamber chamber;
    private Chamber outputChamber;
    private Material gas;
    private double initialVolume;
    private double initialPressure;
    private double initialTemperature;
    private double pistonArea;
    private double pistonMass;
    private double externalPressure;

    private double pistonVelocity;
    private double pistonDisplacement;
    private double pressure;
    private double forceFromPressure;
    private double volume;
    private double temperature;
    private double chamberDepth;

    public Compressor(double initialVolume, double initialPressure, double initialTemperature, double pistonArea, double pistonMass, double externalPressure) {
        this.initialVolume = initialVolume;
        this.initialPressure = initialPressure;
        this.initialTemperature = initialTemperature;
        this.pistonArea = pistonArea;
        this.pistonMass = pistonMass;
        this.externalPressure = externalPressure;

        state = STATE.READY;
        this.pressure = externalPressure;
        this.volume = initialVolume;
        this.temperature = initialTemperature;
        this.chamberDepth = initialVolume / pistonArea;
        this.pistonVelocity = 0;
        this.pistonDisplacement = 0;
    }

    @Override
    public void stepMachine(){
        if(state == STATE.READY){
            pullFromChamber();
        } else if(state == STATE.RUNNING){
            calculateCompressor();
            if(isMaxCompression()) {
                state = STATE.DONE;
            }
        } else if(state == STATE.DONE){
            gas.setTemperature(temperature);
            gas.setPressure(pressure);
            pushToChamber();
        }
    }

    public void pushToChamber(){
        if(MachineHelper.pushToChamber(outputChamber, volume, gas)){
            resetCompressor();
        }else{
//            if(inputValue.getInputMachine() instanceof Motor){
//                ((Motor)inputValue.getInputMachine()).killMotor(true);
//            }
        }
    }

    public void pullFromChamber(){
        gas = MachineHelper.pullFromChamber(chamber, initialVolume);
        if(gas != null){
            initialTemperature = gas.getTemperature();
            initialPressure = gas.getPressure();
            state = STATE.RUNNING;
        }else{
//            if(inputValue.getInputMachine() instanceof Motor){
//                ((Motor)inputValue.getInputMachine()).killMotor(true);
//            }
        }
    }

    public void resetCompressor(){
        pistonDisplacement = 0;
        forceFromPressure = 0;
        inputValue.setTorque(0);
        pressure = externalPressure;
        volume = initialVolume;
        temperature = initialTemperature;
        pistonVelocity = 0;
        gas = null;
        state = STATE.READY;
    }

    public boolean isMaxCompression(){
        if(inputValue.getInputMachine() instanceof Motor){
            return inputValue.getTorque() >= ((Motor)inputValue.getInputMachine()).calculateMaxTorque() * MAX_LOAD_TORQUE_RATIO;
        }
        return false;
    }

    public void calculateCompressor(){
        calculateVolume();
        calculatePressure();
        calculateTemperature();
    }

    public void calculateVolume(){
        pistonVelocity = inputValue.getAngularVelocity() * inputValue.getLeverArm();

        pistonDisplacement += pistonVelocity * Simulator.TIME_CONST;

        volume = initialVolume - (pistonDisplacement * pistonArea);

        double pressureForceOnPiston = (pressure * ATM_TO_PASCAL) * pistonArea;
        double ambientForceOnPiston = (externalPressure * ATM_TO_PASCAL) * pistonArea;
        double pistonForce = pressureForceOnPiston - ambientForceOnPiston;

        inputValue.setTorque(pistonForce * inputValue.getLeverArm());
        forceFromPressure = pressureForceOnPiston - ambientForceOnPiston;
    }

    public void calculatePressure(){
        pressure = initialPressure * Math.pow((initialVolume/volume),GAMMA);
    }

    public void calculateTemperature(){
        temperature = ((pressure*volume) / (initialPressure*initialVolume)) * initialTemperature;
    }

    public void drawCompressor(Graphics g, int xPos, int yPos, int scale){
        double chamberWidth = (Math.sqrt(pistonArea/Simulator.PI) * 2);
        int scaledChamberWidth = (int)(chamberWidth * scale);
        int scaledChamberDepth = (int)(chamberDepth * scale);
        int scaledPistonDepth = (int)(chamberDepth * 0.03 * scale);
        g.drawRect(xPos,yPos,scaledChamberWidth, scaledChamberDepth);
        g.drawRect(xPos ,yPos + (int)(pistonDisplacement * scale),scaledChamberWidth, scaledPistonDepth);
        double colorScale = 5;
        int colorVal = Math.min((int)(pressure * colorScale), 255);
        g.setColor(new Color(255 - colorVal,255 - colorVal, 255));
        g.fillRect(xPos + 1, yPos + (int)(pistonDisplacement * scale) + scaledPistonDepth + 1,(int)(chamberWidth * scale) - 1, ((int)((chamberDepth - pistonDisplacement) * scale) - scaledPistonDepth) - 1);
        g.setColor(new Color(0,0,0));
        g.drawString("Pressure " + (float)pressure + " Atm", xPos, yPos - 30);
        g.drawString("Temperature " + (float)(temperature - 273.13) + " C", xPos, yPos - 15);
        g.drawString("Torque: " + (float)inputValue.getTorque(),xPos, yPos - 45);
        g.drawString("Max Torque: " + ((Motor)inputValue.getInputMachine()).calculateMaxTorque(),xPos, yPos - 60);
    }

    public void displayCompressorInfo(double seconds){
        System.out.println("Compressor: " + "Pressure " + pressure +  " atm " + "     Volume " + volume + " m^3 " + "      Temperature " + (temperature - 273f) +  " C " + "      Piston Velocity " + pistonVelocity+ "    Time " + seconds + " s");
    }

    public double getInitialVolume() {
        return initialVolume;
    }

    public void setInitialVolume(double initialVolume) {
        this.initialVolume = initialVolume;
    }

    public double getInitialPressure() {
        return initialPressure;
    }

    public void setInitialPressure(double initialPressure) {
        this.initialPressure = initialPressure;
    }

    public double getInitialTemperature() {
        return initialTemperature;
    }

    public void setInitialTemperature(double initialTemperature) {
        this.initialTemperature = initialTemperature;
    }

    public double getPistonArea() {
        return pistonArea;
    }

    public void setPistonArea(double pistonArea) {
        this.pistonArea = pistonArea;
    }

    public double getPistonMass() {
        return pistonMass;
    }

    public void setPistonMass(double pistonMass) {
        this.pistonMass = pistonMass;
    }

    public double getExternalPressure() {
        return externalPressure;
    }

    public void setExternalPressure(double externalPressure) {
        this.externalPressure = externalPressure;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getChamberDepth() {
        return chamberDepth;
    }

    public void setChamberDepth(double chamberDepth) {
        this.chamberDepth = chamberDepth;
    }

    public double getPistonVelocity() {
        return pistonVelocity;
    }

    public void setPistonVelocity(double pistonVelocity) {
        this.pistonVelocity = pistonVelocity;
    }

    public double getPistonDisplacement() {
        return pistonDisplacement;
    }

    public void setPistonDisplacement(double pistonDisplacement) {
        this.pistonDisplacement = pistonDisplacement;
    }

    public double getForceFromPressure() {
        return forceFromPressure;
    }

    public void setForceFromPressure(double forceFromPressure) {
        this.forceFromPressure = forceFromPressure;
    }

    public RotationalForce getInputValue() {
        return inputValue;
    }

    public void setInputValue(RotationalForce inputValue) {
        this.inputValue = inputValue;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public Chamber getChamber() {
        return chamber;
    }

    public void setChamber(Chamber chamber) {
        this.chamber = chamber;
    }

    public Chamber getOutputChamber() {
        return outputChamber;
    }

    public void setOutputChamber(Chamber outputChamber) {
        this.outputChamber = outputChamber;
    }

    public Material getGas() {
        return gas;
    }

    public void setGas(Material gas) {
        this.gas = gas;
    }
}
