import java.awt.*;

public class Chamber implements Machine{
    public static final double MAX_CHAMBER_DRAW_SIZE = 0.3;
    public enum STATE{
        EMPTY,
        CONTAINS,
        FULL
    }

    private STATE state;
    private Material contents;
    private Chamber outputChamber;
    private Chamber alternateOutputChamber;
    private Chamber activeOutputChamber;
    private double maxVolume;
    private double chamberSideLength;

    public Chamber(double maxVolume) {
        this.maxVolume = maxVolume;
        state = STATE.EMPTY;
        chamberSideLength = Math.pow(maxVolume, 0.33333);
    }

    @Override
    public void stepMachine(){
        if(outputChamber != null){
            if(contents != null){
                checkAlternateChamberCriteria();
                pushToOutputChamber();
            }
        }
    }

    public void checkAlternateChamberCriteria(){
        if(contents.getPressure() > 50 && alternateOutputChamber != null){
            activeOutputChamber = alternateOutputChamber;
        }else{
            activeOutputChamber = outputChamber;
        }
    }

    public void pushToOutputChamber(){
        if(MachineHelper.pushToChamber(activeOutputChamber, MaterialHelper.materialVolume(contents), contents)){
            contents = null;
            state = STATE.EMPTY;
        }
    }

    public void addToContents(Material material){
        if(state == STATE.EMPTY){
            double volume = MaterialHelper.materialVolume(material);
            if(volume <= maxVolume){
                contents = material;
                contents.calculateProps();
                if(volume == maxVolume){
                    state = STATE.FULL;
                }else{
                    state = STATE.CONTAINS;
                }
            }
        }else if(state == STATE.CONTAINS){
            if(MaterialHelper.materialVolume(material) <= (maxVolume - MaterialHelper.materialVolume(contents))){
                contents = MaterialHelper.addMaterials(contents, material);
            }
        }
    }

    public Material removeFromContents(double mass){
        if(contents.getMass() >= mass){
            if(mass != 0 && state == STATE.FULL){
                state = STATE.CONTAINS;
            }
            Material material = MaterialHelper.createMaterialCopy(contents, mass);
            contents.setMass(contents.getMass() - mass);
            if(contents.getMass() == 0){
                state = STATE.EMPTY;
            }
            return material;
        }
        return null;
    }

    public void drawChamber(Graphics g, int xPos, int yPos, int scale){
        if(chamberSideLength > MAX_CHAMBER_DRAW_SIZE){
            scale *= (MAX_CHAMBER_DRAW_SIZE/chamberSideLength);
        }
        double currentVolume = MaterialHelper.materialVolume(contents);
        double gasLevel = currentVolume / (chamberSideLength * chamberSideLength);
        double gasPosition = chamberSideLength - gasLevel;
        int scaledChamberSide = (int)(chamberSideLength * scale);
        g.drawRect(xPos,yPos,scaledChamberSide, scaledChamberSide);
        if(contents != null){
            g.drawLine(xPos, yPos + (int)(gasPosition * scale), xPos + scaledChamberSide, yPos + (int)(gasPosition * scale));
            double colorScale = 5;
            int colorVal = Math.min((int)(contents.getPressure() * colorScale), 255);
            g.setColor(new Color(255 - colorVal,255 - colorVal, 255));
            g.fillRect(xPos + 1, yPos + (int)(gasPosition * scale) + 1,scaledChamberSide - 1, scaledChamberSide - (int)(gasPosition * scale) - 1);
            g.setColor(new Color(0,0,0));
            String materialStateString = "";
            if(contents.getMaterialState() == Material.MATERIAL_STATE.GAS) materialStateString = "gas ";
            if(contents.getMaterialState() == Material.MATERIAL_STATE.LIQUID) materialStateString = "liquid ";
            if(contents.getMaterialState() == Material.MATERIAL_STATE.SOLID) materialStateString = "solid ";
            String pressureString = (contents.getMaterialState() == Material.MATERIAL_STATE.GAS)?" at " + (float)contents.getPressure() + " atm ": "";
            g.drawString(pressureString + (float)(contents.getTemperature() - 273.13) + " C", xPos, yPos - 15);
            g.drawString((float)MaterialHelper.materialVolume(contents) + " m3 of " + materialStateString + contents.getMaterialType().getName() , xPos, yPos - 30);
        }
    }

    public Material getContents() {
        return contents;
    }

    public void setContents(Material contents) {
        state = STATE.CONTAINS;
        this.contents = contents;
    }

    public double getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(double maxVolume) {
        this.maxVolume = maxVolume;
    }

    public Chamber getOutputChamber() {
        return outputChamber;
    }

    public void setOutputChamber(Chamber outputChamber) {
        this.outputChamber = outputChamber;
    }

    public Chamber getAlternateOutputChamber() {
        return alternateOutputChamber;
    }

    public void setAlternateOutputChamber(Chamber alternateOutputChamber) {
        this.alternateOutputChamber = alternateOutputChamber;
    }

    public Chamber getActiveOutputChamber() {
        return activeOutputChamber;
    }

    public void setActiveOutputChamber(Chamber activeOutputChamber) {
        this.activeOutputChamber = activeOutputChamber;
    }
}
