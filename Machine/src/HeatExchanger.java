import java.awt.Graphics;
import java.awt.Color;

public class HeatExchanger implements Machine {
    public static final double FINAL_TEMPERATURE_DIFFERENCE = 10;
    public static final double CRITICAL_COOLANT_TEMPERATURE = 303;
    public static final double MIN_TEMPERATURE = 294;
    public static final double MAX_TEMPERATURE = 1000;

    public enum MATERIAL_STATE{
        EMPTY,
        TRANSFERRING_HEAT
    }

    private MATERIAL_STATE contentState;
    private MATERIAL_STATE coolantState;

    private Pipe pipe;
    private Chamber inputContentChamber;
    private Chamber inputCoolantChamber;
    private Chamber outputContentChamber;
    private Chamber outputCoolantChamber;
    private Material contents;
    private Material coolant;
    private double contentVolume;
    private double coolantVolume;
    private double initialCoolantTemperature;
    private double contentTemperature;
    private double coolantTemperature;
    private double pressure;

    public HeatExchanger(Pipe pipe, double coolantVolume){
        this.coolantVolume = coolantVolume;
        this.pipe = pipe;

        calculateProps();
    }

    public void calculateProps(){
        contentState = MATERIAL_STATE.EMPTY;
        coolantState = MATERIAL_STATE.EMPTY;

        contentVolume = pipe.getVolumeCapacity();
    }

    @Override
    public void stepMachine(){
        if(contentState == MATERIAL_STATE.EMPTY){
            pullFromContentChamber();
        }
        if(coolantState == MATERIAL_STATE.EMPTY){
            pullFromCoolantChamber();
        }

        if(contentState == MATERIAL_STATE.TRANSFERRING_HEAT && coolantState == MATERIAL_STATE.TRANSFERRING_HEAT){
            calculateHeatExchanger();
            if(contents.getTemperature() <= (initialCoolantTemperature + FINAL_TEMPERATURE_DIFFERENCE)){
                pushToContentChamber();
            }
            if(coolant.getTemperature() >= CRITICAL_COOLANT_TEMPERATURE){
                pushToCoolantChamber();
            }
        }
    }

    public void pullFromContentChamber(){
        contents = MachineHelper.pullFromChamber(inputContentChamber, contentVolume);
        if(contents != null){
            contentState = MATERIAL_STATE.TRANSFERRING_HEAT;
            pressure = contents.getPressure();
            contentTemperature = contents.getTemperature();
        }
    }

    public void pullFromCoolantChamber(){
        coolant = MachineHelper.pullFromChamber(inputCoolantChamber, coolantVolume);
        if(coolant != null){
            initialCoolantTemperature = coolant.getTemperature();
            coolantState = MATERIAL_STATE.TRANSFERRING_HEAT;
            coolantTemperature = coolant.getTemperature();
        }
    }

    public void pushToContentChamber(){
        contents.setTemperature(contentTemperature);
        if(MachineHelper.pushToChamber(outputContentChamber, contentVolume, contents)){
            contents = null;
            contentState = MATERIAL_STATE.EMPTY;
        }
    }

    public void pushToCoolantChamber(){
        coolant.setTemperature(coolantTemperature);
        if(MachineHelper.pushToChamber(outputCoolantChamber, coolantVolume, coolant)){
            coolant = null;
            coolantState = MATERIAL_STATE.EMPTY;
        }
    }

    public void calculateHeatExchanger(){
        calculateContentPipeExchange();
        calculatePipeCoolantExchange();
    }

    public void calculateContentPipeExchange(){
        MaterialHelper.setMaterialTemperaturesFromHeatTransfer(pipe.getMaterial(), contents, pipe.getInnerSurfaceArea(), Simulator.TIME_CONST);
        MaterialHelper.materialPressureFromTemperature(contents, contentVolume);
        pressure = contents.getPressure();
        contentTemperature = contents.getTemperature();
    }

    public void calculatePipeCoolantExchange(){
        MaterialHelper.setMaterialTemperaturesFromHeatTransfer(coolant, pipe.getMaterial(), pipe.getOuterSurfaceArea(), Simulator.TIME_CONST);
        coolantTemperature = coolant.getTemperature();
    }

    public void drawHeatExchanger(Graphics g, int xPos, int yPos, int scale){
        Color contentColor = new Color(200,200,200);
        Color coolantColor = new Color(255,255,255);

        if(contents != null) contentColor = GraphicsHelper.getTemperatureGradient(MIN_TEMPERATURE, MAX_TEMPERATURE, contentTemperature);
        if(coolant != null) coolantColor = GraphicsHelper.getTemperatureGradient(MIN_TEMPERATURE, MAX_TEMPERATURE, coolantTemperature);

        GraphicsHelper.fillCoil(g, xPos, yPos, contentColor,coolantColor, 0.02,3,scale);
        g.drawString("Pipe Temperature " + (float)(pipe.getMaterial().getTemperature() - 273) + " C",xPos,yPos - 15);
        g.drawString("Pipe Max Pressure " + (float)pipe.getMaxPressure() + " Atm", xPos, yPos - 30);
        if(contents != null){
            g.drawString("Content Volume" + (float)MaterialHelper.materialVolume(contents) + " m3",xPos, yPos - 75);
            g.drawString("Content Pressure " + (float)pressure + " Atm",xPos,yPos - 60);
            g.drawString("Content Temperature " + (float)(contentTemperature - 273) + " C",xPos,yPos - 45);
        }
        if(coolant != null){
            g.drawString("Coolant Temperature " + (float)(coolantTemperature - 273) + " C", xPos, yPos - 105);
            g.drawString("Coolant Volume " + (float)MaterialHelper.materialVolume(coolant) + " m3",xPos,yPos - 90);
        }
    }

    public MATERIAL_STATE getContentState() {
        return contentState;
    }

    public void setContentState(MATERIAL_STATE contentState) {
        this.contentState = contentState;
    }

    public MATERIAL_STATE getCoolantState() {
        return coolantState;
    }

    public void setCoolantState(MATERIAL_STATE coolantState) {
        this.coolantState = coolantState;
    }

    public Pipe getPipe() {
        return pipe;
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }

    public Chamber getInputContentChamber() {
        return inputContentChamber;
    }

    public void setInputContentChamber(Chamber inputContentChamber) {
        this.inputContentChamber = inputContentChamber;
    }

    public Chamber getInputCoolantChamber() {
        return inputCoolantChamber;
    }

    public void setInputCoolantChamber(Chamber inputCoolantChamber) {
        this.inputCoolantChamber = inputCoolantChamber;
    }

    public Chamber getOutputContentChamber() {
        return outputContentChamber;
    }

    public void setOutputContentChamber(Chamber outputContentChamber) {
        this.outputContentChamber = outputContentChamber;
    }

    public Chamber getOutputCoolantChamber() {
        return outputCoolantChamber;
    }

    public void setOutputCoolantChamber(Chamber outputCoolantChamber) {
        this.outputCoolantChamber = outputCoolantChamber;
    }

    public Material getContents() {
        return contents;
    }

    public void setContents(Material contents) {
        this.contents = contents;
    }

    public Material getCoolant() {
        return coolant;
    }

    public void setCoolant(Material coolant) {
        this.coolant = coolant;
    }

    public double getContentVolume() {
        return contentVolume;
    }

    public void setContentVolume(double contentVolume) {
        this.contentVolume = contentVolume;
    }

    public double getCoolantVolume() {
        return coolantVolume;
    }

    public void setCoolantVolume(double coolantVolume) {
        this.coolantVolume = coolantVolume;
    }
}
