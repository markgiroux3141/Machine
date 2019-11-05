import java.awt.Graphics;
import java.awt.Color;

public class Motor implements Machine{
    private static final double ROTOR_FRICTION = 0.995;
    private static final double WIRE_TEMPERATURE_RATING = 1000;
    private static final double RESTART_TEMPERATURE = 303;

    public enum STATE{
        RUNNING,
        STOPPED,
        STOPPED_EXTERNAL
    }

    private Battery battery;
    private RotationalForce outputValue;
    private STATE state;
    private Coil coil;
    private Magnet magnet;
    private Rotor rotor;
    private double turns;
    private double resistance;
    private double inpVoltage;
    private double maxVoltage;
    private double magneticField;
    private double coilArea;
    private double rotorMass;
    private double rotorRadius;
    private double ambientTemperature;

    private double rotorMoment;
    private double backEmf = 0;
    private double torque;
    private double current;
    private double rpm = 0;
    private double maxRads;
    private double rads = 0;
    private double radAngle = 0;
    private double loadTorque = 0;
    private double wireLength;
    private double wireRadius;
    private double wireCrossArea;
    private double wireMass;
    private double wireTemperature;

    public Motor(Wire wire, Magnet magnet, Rotor rotor, Battery battery, double numTurns, double coilArea, double ambientTemperature) {
        coil = new Coil(wire, numTurns, coilArea);
        this.turns = coil.getNumTurns();
        this.resistance = wire.getResistance() + battery.getInternalResistance();
        this.inpVoltage = battery.getVoltage();
        this.maxVoltage = battery.getVoltage();
        this.magneticField = magnet.getMaterial().getMaterialType().getMagnetism();
        this.coilArea = coil.getCoilArea();
        this.rotor = rotor;
        this.rotorMass = rotor.getMass();
        this.rotorRadius = rotor.getRadius();
        this.ambientTemperature = ambientTemperature;

        state = STATE.RUNNING;
        calculateWireProps();
        calculateRotorMoment();
        calculateCurrent();
        calculateMaxRads();
    }

    public void calculateWireProps(){
        wireLength = coil.getWire().getLength();
        wireCrossArea = coil.getWire().getCrossArea();
        wireRadius = coil.getWire().getRadius();
        wireMass = coil.getWire().getMass();
        wireTemperature = ambientTemperature;
    }

    @Override
    public void stepMachine(){
        if(state == STATE.RUNNING){
            if(wireTemperature > WIRE_TEMPERATURE_RATING){
                killMotor(false);
            }
        }else if(state == STATE.STOPPED){
            if(wireTemperature <= RESTART_TEMPERATURE){
                restartMotor();
            }
        }
        calculateMotor();
    }

    public void killMotor(boolean external){
        if(external){
            state = STATE.STOPPED_EXTERNAL;
        }else{
            state = STATE.STOPPED;
        }
        inpVoltage = 0;
    }

    public void restartMotor(){
        state = STATE.RUNNING;
        inpVoltage = maxVoltage;
    }

    public void calculateMotor(){
        calculateCurrent();
        calculateWireTemperature();
        calculateTorque();
        calculateSpeed();
        calculateBackEmf();
    }

    public void calculateCurrent(){
        current =  (inpVoltage - backEmf) / resistance;
    }

    public void calculateMaxRads(){
        maxRads = inpVoltage/(magneticField * coilArea * turns);
    }

    public double calculateMaxCurrent(){
        return maxVoltage / resistance;
    }

    public double calculateMaxTorque(){
        return turns * calculateMaxCurrent() * coilArea * magneticField;
    }

    public void calculateWireTemperature(){
        double heat = current * current * resistance * Simulator.TIME_CONST;
        double wireSurfaceArea = (wireRadius * 2 * Simulator.PI) * wireLength;
        double heatLoss = (Simulator.SOLID_AIR_THERMAL_CONDUCTIVITY * wireSurfaceArea * (wireTemperature - ambientTemperature)) * Simulator.TIME_CONST;
        double specificHeat = coil.getWire().getMaterial().getMaterialType().getSpecificHeat();
        wireTemperature += (heat - heatLoss)/(wireMass * specificHeat);
    }

    public void calculateRotorMoment(){
        rotorMoment = rotor.getMoment();
    }

    public void calculateTorque(){
        torque = turns * current * coilArea * magneticField;
    }

    public void calculateSpeed(){
        double angularAccel = (torque - outputValue.getTorque()) / rotorMoment;
        rads += angularAccel * Simulator.TIME_CONST;
        if(rads > maxRads) rads = maxRads;
        rads *= ROTOR_FRICTION;
        radAngle += rads * Simulator.TIME_CONST;
        outputValue.setAngularVelocity(rads);
        rpm = rads * Simulator.RADS_TO_RPM;
    }

    public void calculateBackEmf(){
        backEmf = magneticField * coilArea * turns * rads;
    }

    public void drawMotor(Graphics g, int xPos, int yPos, int scale){
        double coilRadius = Math.sqrt(coilArea);
        double scaledMotorRadius = coilRadius * scale;
        int tempCol = (int)Math.min((wireTemperature - Simulator.AMBIENT_TEMP) * (255/(WIRE_TEMPERATURE_RATING - Simulator.AMBIENT_TEMP)),255);
        GraphicsHelper.drawCircle(g, xPos, yPos, (int)scaledMotorRadius);
        GraphicsHelper.fillCircle(g, xPos, yPos, (int)scaledMotorRadius - 1, new Color(255, 255 - tempCol, 255 - tempCol));
        g.drawLine(xPos,yPos,(int)(Math.cos(radAngle) * scaledMotorRadius) + xPos, (int)(Math.sin(radAngle) * scaledMotorRadius) + yPos);
        g.drawString((float)rpm + " RPM", xPos - (int)scaledMotorRadius, (yPos - (int)scaledMotorRadius) - 60);
        g.drawString("Temperature " + (float)(wireTemperature - 273.13) + " C", xPos - (int)scaledMotorRadius, (yPos - (int)scaledMotorRadius) - 45);
        g.drawString("Torque " + (float)torque + " Nm", xPos - (int)scaledMotorRadius, (yPos - (int)scaledMotorRadius) - 30);
        g.drawString("Back Emf " + (float)backEmf + " V", xPos - (int)scaledMotorRadius, (yPos - (int)scaledMotorRadius) - 15);
    }

    public void displayMotorInfo(double seconds){
        System.out.println("Motor: " + "RPM " + rpm +  "     Current " + current + " amps " + "     Back Emf " + backEmf +  " volts " + "     Torque " + torque + " n/m " + "    Temperature " + (wireTemperature - 273) + " C" + "    Time " + seconds + " s");
    }

    public double getTurns() {
        return turns;
    }

    public void setTurns(double turns) {
        this.turns = turns;
    }

    public double getResistance() {
        return resistance;
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    public double getInpVoltage() {
        return inpVoltage;
    }

    public void setInpVoltage(double inpVoltage) {
        this.inpVoltage = inpVoltage;
    }

    public double getMagneticField() {
        return magneticField;
    }

    public void setMagneticField(double magneticField) {
        this.magneticField = magneticField;
    }

    public double getCoilArea() {
        return coilArea;
    }

    public void setCoilArea(double coilArea) {
        this.coilArea = coilArea;
    }

    public double getRotorMoment() {
        return rotorMoment;
    }

    public void setRotorMoment(double rotorMoment) {
        this.rotorMoment = rotorMoment;
    }

    public double getTorque() {
        return torque;
    }

    public void setTorque(double torque) {
        this.torque = torque;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getRpm() {
        return rpm;
    }

    public void setRpm(double rpm) {
        this.rpm = rpm;
    }

    public double getRads() {
        return rads;
    }

    public void setRads(double rads) {
        this.rads = rads;
        this.rpm = rads * Simulator.RADS_TO_RPM;
    }

    public double getLoadTorque() {
        return loadTorque;
    }

    public void setLoadTorque(double loadTorque) {
        this.loadTorque = loadTorque;
    }

    public double getBackEmf() {
        return backEmf;
    }

    public void setBackEmf(double backEmf) {
        this.backEmf = backEmf;
    }

    public double getRotorMass() {
        return rotorMass;
    }

    public void setRotorMass(double rotorMass) {
        this.rotorMass = rotorMass;
    }

    public double getRotorRadius() {
        return rotorRadius;
    }

    public void setRotorRadius(double rotorRadius) {
        this.rotorRadius = rotorRadius;
    }

    public double getWireLength() {
        return wireLength;
    }

    public void setWireLength(double wireLength) {
        this.wireLength = wireLength;
    }

    public double getWireCrossArea() {
        return wireCrossArea;
    }

    public void setWireCrossArea(double wireCrossArea) {
        this.wireCrossArea = wireCrossArea;
    }

    public double getWireMass() {
        return wireMass;
    }

    public void setWireMass(double wireMass) {
        this.wireMass = wireMass;
    }

    public double getAmbientTemperature() {
        return ambientTemperature;
    }

    public void setAmbientTemperature(double ambientTemperature) {
        this.ambientTemperature = ambientTemperature;
    }

    public double getWireTemperature() {
        return wireTemperature;
    }

    public void setWireTemperature(double wireTemperature) {
        this.wireTemperature = wireTemperature;
    }

    public RotationalForce getOutputValue() {
        return outputValue;
    }

    public void setOutputValue(RotationalForce outputValue) {
        this.outputValue = outputValue;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public Coil getCoil() {
        return coil;
    }

    public void setCoil(Coil coil) {
        this.coil = coil;
    }

    public Magnet getMagnet() {
        return magnet;
    }

    public void setMagnet(Magnet magnet) {
        this.magnet = magnet;
    }

    public Rotor getRotor() {
        return rotor;
    }

    public void setRotor(Rotor rotor) {
        this.rotor = rotor;
    }

    public double getMaxRads() {
        return maxRads;
    }

    public void setMaxRads(double maxRads) {
        this.maxRads = maxRads;
    }

    public double getWireRadius() {
        return wireRadius;
    }

    public void setWireRadius(double wireRadius) {
        this.wireRadius = wireRadius;
    }

    public Battery getBattery() {
        return battery;
    }

    public void setBattery(Battery battery) {
        this.battery = battery;
    }

    public double getMaxVoltage() {
        return maxVoltage;
    }

    public void setMaxVoltage(double maxVoltage) {
        this.maxVoltage = maxVoltage;
    }

    public double getRadAngle() {
        return radAngle;
    }

    public void setRadAngle(double radAngle) {
        this.radAngle = radAngle;
    }
}
