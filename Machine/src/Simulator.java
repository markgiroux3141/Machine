import java.util.List;
import java.util.ArrayList;

public class Simulator {
    public static final double TIME_CONST = 0.0001; //seconds
    public static final double PI = 3.14159265358;
    public static final double RADS_TO_RPM = 9.549296586;
    public static final double AMBIENT_TEMP = 294;
    public static final double AMBIENT_PRESSURE = 1;
    public static final double SOLID_AIR_THERMAL_CONDUCTIVITY = 13.1;
    public static final double AMBIENT_TEMPERATURE = 294;
    public static final double GAMMA = 1.66f;

    private MaterialType copper;
    private MaterialType iron;
    private MaterialType nitrogen;
    private MaterialType water;

    private Material gas;
    private Material coolant;

    private Wire copperWire;
    private Rotor rotor;
    private Magnet magnet;
    private Battery nineVoltBattery;
    private Battery carBattery;
    private Battery powerSupply;
    private Battery deadBattery;

    private Motor motor;
    private Compressor compressor;

    //Compressor Chambers
    private Chamber chamber;
    private Chamber outputChamber;

    //Gate
    private Chamber heatExchangerGateChamber;

    //Heat Exchanger Chambers
    private HeatExchanger heatExchanger;
    private Chamber inputCoolantChamber;
    private Chamber outputCoolantChamber;
    private Chamber outputContentChamber;
    private Pipe pipe;

    //Expander
    private Expander expander;
    private Chamber expanderOutputGasChamber;
    private Chamber expanderOutputLiquidChamber;

    private RotationalForce rotationalForce;

    private List<Machine> machines;

    public Simulator(){
        nitrogen = new MaterialType("Nitrogen",0,100, 1200, 1.165, 807,1000,1,94,30, 0, 696,25300,199000);
        gas = new Material(nitrogen, 1, 294,1);

        chamber = new Chamber(10);
        outputChamber = new Chamber(.1);
        heatExchangerGateChamber = new Chamber(.1);

        chamber.addToContents(gas);
        copper = new MaterialType("Copper",1920,1.68E-8, 385, 1.3,7000,8290,400, 2862,1538,0,800,400000,1000000);
        iron = new MaterialType("Iron",1920,1.68E-7, 385, 1.2,6500,7874,400, 2562,1085,0.2,800,300000,1000000);

        water = new MaterialType("Water",0,1,4.1, 1.3,1000,970, 200, 372,273,0,800, 330000,2260000);

        copperWire = new Wire(new Material(copper),0.001,60);
        rotor = new Rotor(new Material(iron), .02,.05);
        magnet = new Magnet(new Material(iron));
        nineVoltBattery = new Battery(12,300);
        carBattery = new Battery(8, 0.04);
        powerSupply = new Battery(60, 0.04);
        deadBattery = new Battery(0,1);

        pipe = new Pipe(new Material(copper,0,AMBIENT_TEMP), .01,.003,100);

        motor = new Motor(copperWire, magnet, rotor, carBattery,-1,.004, AMBIENT_TEMP);
        compressor = new Compressor(0.02,1,294,.02,1,1);

        heatExchanger = new HeatExchanger(pipe,1);
        coolant = new Material(water,820000, AMBIENT_TEMP);
        inputCoolantChamber = new Chamber(1000);
        outputCoolantChamber = new Chamber(1000);
        outputContentChamber = new Chamber(.1);
        inputCoolantChamber.addToContents(coolant);
        heatExchanger.setInputContentChamber(outputChamber);
        heatExchanger.setInputCoolantChamber(inputCoolantChamber);
        heatExchanger.setOutputContentChamber(heatExchangerGateChamber);
        heatExchanger.setOutputCoolantChamber(outputCoolantChamber);

        rotationalForce = new RotationalForce(0.00001);
        compressor.setChamber(chamber);
        compressor.setOutputChamber(outputChamber);
        motor.setOutputValue(rotationalForce);
        compressor.setInputValue(rotationalForce);
        rotationalForce.setInputMachine(motor);
        rotationalForce.setOutputMachine(compressor);

        heatExchangerGateChamber.setOutputChamber(chamber);
        heatExchangerGateChamber.setAlternateOutputChamber(outputContentChamber);

        expander = new Expander(iron, .001,.02);
        expanderOutputGasChamber = new Chamber(1);
        expanderOutputLiquidChamber = new Chamber(0.02);
        expander.setInputChamber(outputContentChamber);
        expander.setOutputGasChamber(expanderOutputGasChamber);
        expander.setOutputLiquidChamber(expanderOutputLiquidChamber);

        getExpanderOutputGasChamber().setOutputChamber(chamber);

        //Testing
        //outputContentChamber.setContents(new Material(nitrogen,5,303,300));

        machines = new ArrayList<>();
        machines.add(chamber);
        machines.add(motor);
        machines.add(compressor);
        machines.add(outputChamber);
        machines.add(heatExchanger);
        machines.add(inputCoolantChamber);
        machines.add(outputContentChamber);
        machines.add(outputCoolantChamber);
        machines.add(expander);
        machines.add(expanderOutputGasChamber);
        machines.add(expanderOutputLiquidChamber);
        machines.add(heatExchangerGateChamber);
    }

    public void runSimulation(double runTime){
        double counter = 0;
        int runTimeTicks = (int)(runTime/TIME_CONST);
        while(counter < runTimeTicks){
            machines.forEach(m -> {
                m.stepMachine();
            });
            counter ++;
        }
    }

    public Motor getMotor() {
        return motor;
    }

    public void setMotor(Motor motor) {
        this.motor = motor;
    }

    public Compressor getCompressor() {
        return compressor;
    }

    public void setCompressor(Compressor compressor) {
        this.compressor = compressor;
    }

    public MaterialType getCopper() {
        return copper;
    }

    public void setCopper(MaterialType copper) {
        this.copper = copper;
    }

    public MaterialType getIron() {
        return iron;
    }

    public void setIron(MaterialType iron) {
        this.iron = iron;
    }

    public MaterialType getNitrogen() {
        return nitrogen;
    }

    public void setNitrogen(MaterialType nitrogen) {
        this.nitrogen = nitrogen;
    }

    public Material getGas() {
        return gas;
    }

    public void setGas(Material gas) {
        this.gas = gas;
    }

    public Wire getCopperWire() {
        return copperWire;
    }

    public void setCopperWire(Wire copperWire) {
        this.copperWire = copperWire;
    }

    public Rotor getRotor() {
        return rotor;
    }

    public void setRotor(Rotor rotor) {
        this.rotor = rotor;
    }

    public Magnet getMagnet() {
        return magnet;
    }

    public void setMagnet(Magnet magnet) {
        this.magnet = magnet;
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

    public RotationalForce getRotationalForce() {
        return rotationalForce;
    }

    public void setRotationalForce(RotationalForce rotationalForce) {
        this.rotationalForce = rotationalForce;
    }

    public Battery getNineVoltBattery() {
        return nineVoltBattery;
    }

    public void setNineVoltBattery(Battery nineVoltBattery) {
        this.nineVoltBattery = nineVoltBattery;
    }

    public Battery getCarBattery() {
        return carBattery;
    }

    public void setCarBattery(Battery carBattery) {
        this.carBattery = carBattery;
    }

    public HeatExchanger getHeatExchanger() {
        return heatExchanger;
    }

    public void setHeatExchanger(HeatExchanger heatExchanger) {
        this.heatExchanger = heatExchanger;
    }

    public Chamber getInputCoolantChamber() {
        return inputCoolantChamber;
    }

    public void setInputCoolantChamber(Chamber inputCoolantChamber) {
        this.inputCoolantChamber = inputCoolantChamber;
    }

    public Chamber getOutputCoolantChamber() {
        return outputCoolantChamber;
    }

    public void setOutputCoolantChamber(Chamber outputCoolantChamber) {
        this.outputCoolantChamber = outputCoolantChamber;
    }

    public Chamber getOutputContentChamber() {
        return outputContentChamber;
    }

    public void setOutputContentChamber(Chamber outputContentChamber) {
        this.outputContentChamber = outputContentChamber;
    }

    public Pipe getPipe() {
        return pipe;
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }

    public List<Machine> getMachines() {
        return machines;
    }

    public void setMachines(List<Machine> machines) {
        this.machines = machines;
    }

    public MaterialType getWater() {
        return water;
    }

    public void setWater(MaterialType water) {
        this.water = water;
    }

    public Material getCoolant() {
        return coolant;
    }

    public void setCoolant(Material coolant) {
        this.coolant = coolant;
    }

    public Expander getExpander() {
        return expander;
    }

    public void setExpander(Expander expander) {
        this.expander = expander;
    }

    public Chamber getExpanderOutputGasChamber() {
        return expanderOutputGasChamber;
    }

    public void setExpanderOutputGasChamber(Chamber expanderOutputGasChamber) {
        this.expanderOutputGasChamber = expanderOutputGasChamber;
    }

    public Chamber getExpanderOutputLiquidChamber() {
        return expanderOutputLiquidChamber;
    }

    public void setExpanderOutputLiquidChamber(Chamber expanderOutputLiquidChamber) {
        this.expanderOutputLiquidChamber = expanderOutputLiquidChamber;
    }

    public Chamber getHeatExchangerGateChamber() {
        return heatExchangerGateChamber;
    }

    public void setHeatExchangerGateChamber(Chamber heatExchangerGateChamber) {
        this.heatExchangerGateChamber = heatExchangerGateChamber;
    }
}
