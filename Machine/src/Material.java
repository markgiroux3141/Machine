public class Material{
    public enum MATERIAL_STATE{
        SOLID,
        LIQUID,
        GAS
    }

    private MaterialType materialType;
    private MATERIAL_STATE materialState;
    private double mass;
    private double temperature;
    private double pressure;
    private double density;

    public Material(MaterialType materialType){
        this.temperature = Simulator.AMBIENT_TEMP;
        this.pressure = Simulator.AMBIENT_PRESSURE;
        this.materialType = materialType;

        calculateProps();
    }

    public Material(MaterialType materialType, double mass, double temperature) {
        this.materialType = materialType;
        this.mass = mass;
        this.temperature = temperature;
        this.pressure = Simulator.AMBIENT_PRESSURE;

        calculateProps();
    }

    public Material(MaterialType materialType, double mass, double temperature, double pressure) {
        this.materialType = materialType;
        this.mass = mass;
        this.temperature = temperature;
        this.pressure = pressure;

        calculateProps();
    }

    public void calculateProps(){
        if(temperature < materialType.getMeltingPoint()){
            materialState = MATERIAL_STATE.SOLID;
            density = materialType.getSolidDensity();
        }else if(temperature < materialType.getBoilingPoint()){
            materialState = MATERIAL_STATE.LIQUID;
            density = materialType.getLiquidDensity();
        }else{
            materialState = MATERIAL_STATE.GAS;
            density = materialType.getGasDensity();
        }
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    public MATERIAL_STATE getMaterialState() {
        return materialState;
    }

    public void setMaterialState(MATERIAL_STATE materialState) {
        this.materialState = materialState;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }
}
