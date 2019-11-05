import java.awt.*;

public class Expander implements Machine {
    public static final double EQUALIZATION_THRESHOLD_RATIO = 0.2;
    public static final double MAX_EXPANDER_DRAW_SIZE = 0.3;
    public static final double LIQUID_THRESHOLD = 0.00001;
    public enum STATE{
        READY,
        EXPANDING_GAS,
        EQUALIZING_BEFORE,
        CONDENSING,
        EQUALIZING_AFTER,
        DONE
    }

    private STATE state;
    private Material gas;
    private Material liquid;
    private MaterialType containerType;
    private Material container;
    private Chamber inputChamber;
    private Chamber outputGasChamber;
    private Chamber outputLiquidChamber;
    private double containerThickness;
    private double containerArea;
    private double containerHeight;
    private double volumeCapacity;
    private double volumeToPull;
    private double gasTemperature;
    private double initialGasTemperaturePull;
    private double initialContainerTemperaturePull;
    private double initialContainerTemperatureCondense;

    public Expander(MaterialType containerType, double containerThickness, double volumeCapacity) {
        this.containerType = containerType;
        this.containerThickness = containerThickness;
        this.volumeCapacity = volumeCapacity;

        initExpander();
    }

    public void initExpander(){
        state = STATE.READY;

        double oneThird = (double)1/(double)3;
        double containerInnerRadius = Math.pow(volumeCapacity / Simulator.PI,oneThird);
        double containerOuterRadius = containerInnerRadius + containerThickness;
        double containerOuterVolume = ((containerOuterRadius * containerOuterRadius * containerOuterRadius) * Simulator.PI);
        double containerVolume = containerOuterVolume - volumeCapacity;
        container = new Material(containerType);
        container.setMass(containerVolume * container.getDensity());
        container.setTemperature(Simulator.AMBIENT_TEMP);

        double containerPerimeter = containerInnerRadius * 2 * Simulator.PI;
        containerArea = containerPerimeter * containerInnerRadius;
        containerHeight = containerOuterRadius * 2;
    }

    @Override
    public void stepMachine(){
        if(state == STATE.READY) {
            pullFromChamber();
        } else if(state == STATE.EXPANDING_GAS){
            calculateGasExpansion();
        } else if(state == STATE.EQUALIZING_BEFORE){
            calculateEqualizationBefore();
        }else if(state == STATE.CONDENSING) {
            calculateCondensation();
        }else if(state == STATE.EQUALIZING_AFTER){
            calculateEqualizationAfter();
        }else if(state == STATE.DONE){
            boolean pushedLiquid = pushToLiquidChamber();
            if(pushedLiquid) {
                state = STATE.READY;
                liquid = null;
            }
        }
    }

    public void calculateGasExpansion(){
        gasTemperature = MaterialHelper.getTemperature(gas.getTemperature(), gas.getPressure(), volumeToPull, Simulator.AMBIENT_PRESSURE, volumeCapacity);
        gas.setPressure(Simulator.AMBIENT_PRESSURE);
        gas.setTemperature(gasTemperature);
        initialGasTemperaturePull = gas.getTemperature();
        initialContainerTemperaturePull = container.getTemperature();
        state = STATE.EQUALIZING_BEFORE;
    }

    public void calculateCondensation(){
        if(container.getTemperature() < gas.getMaterialType().getBoilingPoint()){
            double maxHeatFromLatent = gas.getMaterialType().getSpecificHeat() * gas.getMass() * (gas.getMaterialType().getBoilingPoint() - gasTemperature);
            double liquidMass = maxHeatFromLatent/gas.getMaterialType().getLatentHeatVaporization();
            if(liquidMass > 0 ){
                liquid = new Material(gas.getMaterialType(),liquidMass, gas.getMaterialType().getBoilingPoint() - LIQUID_THRESHOLD);
                gas.setTemperature(gas.getMaterialType().getBoilingPoint());
                initialContainerTemperatureCondense = container.getTemperature();
            }
            state = STATE.EQUALIZING_AFTER;
        }else{
            state = STATE.DONE;
        }
        if(pushToGasChamber()){
            gas = null;
        }
    }

    public void calculateEqualizationBefore(){
        MaterialHelper.setMaterialTemperaturesFromHeatTransfer(container, gas, containerArea, Simulator.TIME_CONST);
        gasTemperature = gas.getTemperature();
        if(Math.abs(container.getTemperature() - gas.getTemperature()) < ((initialContainerTemperaturePull - initialGasTemperaturePull)*EQUALIZATION_THRESHOLD_RATIO)
                && (gas.getTemperature() > gas.getMaterialType().getBoilingPoint() || container.getTemperature() < gas.getMaterialType().getBoilingPoint())){
            state = STATE.CONDENSING;
        }
    }

    public void calculateEqualizationAfter(){
        if(liquid != null){
            MaterialHelper.setMaterialTemperaturesFromHeatTransfer(container, liquid, containerArea, Simulator.TIME_CONST);
        }
        if(Math.abs(liquid.getTemperature() - container.getTemperature()) < ((liquid.getMaterialType().getBoilingPoint() - initialContainerTemperatureCondense)*EQUALIZATION_THRESHOLD_RATIO)){
            state = STATE.DONE;
        }
    }

    public void pullFromChamber(){
        volumeToPull = calculateVolumeToPull();
        if(inputChamber.getContents() != null){
            gas = MachineHelper.pullFromChamber(inputChamber, volumeToPull);
        }
        if(gas != null){
            state = STATE.EXPANDING_GAS;
        }
    }

    public boolean pushToGasChamber(){
        return MachineHelper.pushToChamber(outputGasChamber, MaterialHelper.materialVolume(gas), gas);
    }

    public boolean pushToLiquidChamber(){
        boolean pushed;
        if(liquid != null){
            pushed = MachineHelper.pushToChamber(outputLiquidChamber, MaterialHelper.materialVolume(liquid), liquid);
        }else{
            pushed = true;
        }
        return pushed;
    }

    public double calculateVolumeToPull(){
        if(inputChamber.getContents() != null){
            double gasPressure = inputChamber.getContents().getPressure();
            return MaterialHelper.getVolume(volumeCapacity, Simulator.AMBIENT_PRESSURE, gasPressure);
        }else{
            return 0;
        }
    }

    public void drawExpander(Graphics g, int xPos, int yPos, int scale){
        if(containerHeight > MAX_EXPANDER_DRAW_SIZE){
            scale *= (MAX_EXPANDER_DRAW_SIZE/containerHeight);
        }
        double currentVolume = MaterialHelper.materialVolume(liquid);
        double gasLevel = currentVolume / (containerHeight * containerHeight);
        double gasPosition = containerHeight - gasLevel;
        int scaledChamberSide = (int)(containerHeight * scale);
        g.drawRect(xPos,yPos,scaledChamberSide, scaledChamberSide);
        g.drawString("Chamber Temperature: " + (float)(container.getTemperature() - 273) + " C ", xPos, yPos - 30);
        if(gas != null){
            g.drawString("Gas Temperature: " + (float)(gas.getTemperature() - 273) + " C ", xPos, yPos - 45);
        }
        if(liquid != null){
            g.drawLine(xPos, yPos + (int)(gasPosition * scale), xPos + scaledChamberSide, yPos + (int)(gasPosition * scale));
            double colorScale = 5;
            g.setColor(new Color(0,255, 0));
            g.fillRect(xPos + 1, yPos + (int)(gasPosition * scale) + 1,scaledChamberSide - 1, scaledChamberSide - (int)(gasPosition * scale) - 1);
            g.setColor(new Color(0,0,0));
            g.drawString((float)MaterialHelper.materialVolume(liquid) + " m3 of liquid" + liquid.getMaterialType().getName() + " at " + (float)(liquid.getTemperature() - 273.13) + " C", xPos, yPos - 15);
        }
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public Material getGas() {
        return gas;
    }

    public void setGas(Material gas) {
        this.gas = gas;
    }

    public MaterialType getContainerType() {
        return containerType;
    }

    public void setContainerType(MaterialType containerType) {
        this.containerType = containerType;
    }

    public Material getContainer() {
        return container;
    }

    public void setContainer(Material container) {
        this.container = container;
    }

    public Chamber getInputChamber() {
        return inputChamber;
    }

    public void setInputChamber(Chamber inputChamber) {
        this.inputChamber = inputChamber;
    }

    public Chamber getOutputGasChamber() {
        return outputGasChamber;
    }

    public void setOutputGasChamber(Chamber outputGasChamber) {
        this.outputGasChamber = outputGasChamber;
    }

    public Chamber getOutputLiquidChamber() {
        return outputLiquidChamber;
    }

    public void setOutputLiquidChamber(Chamber outputLiquidChamber) {
        this.outputLiquidChamber = outputLiquidChamber;
    }

    public double getContainerThickness() {
        return containerThickness;
    }

    public void setContainerThickness(double containerThickness) {
        this.containerThickness = containerThickness;
    }

    public double getVolumeCapacity() {
        return volumeCapacity;
    }

    public void setVolumeCapacity(double volumeCapacity) {
        this.volumeCapacity = volumeCapacity;
    }

    public double getGasTemperature() {
        return gasTemperature;
    }

    public void setGasTemperature(double gasTemperature) {
        this.gasTemperature = gasTemperature;
    }
}
