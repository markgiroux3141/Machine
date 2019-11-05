public class MaterialType {
    protected String name;
    protected double strength;
    protected double resistivity;
    protected double specificHeat;
    protected double gasDensity;
    protected double liquidDensity;
    protected double solidDensity;
    protected double thermalConductivity;
    protected double boilingPoint;
    protected double meltingPoint;
    protected double magnetism;
    protected double expansionRatio;
    protected double latentHeatFusion;
    protected double latentHeatVaporization;
    protected double gasConstant;

    public MaterialType(String name, double strength, double resistivity, double specificHeat, double gasDensity, double liquidDensity, double solidDensity, double thermalConductivity, double boilingPoint, double meltingPoint, double magnetism, double expansionRatio, double latentHeatFusion, double latentHeatVaporization) {
        this.name = name;
        this.strength = strength;
        this.resistivity = resistivity;
        this.specificHeat = specificHeat;
        this.gasDensity = gasDensity;
        this.liquidDensity = liquidDensity;
        this.solidDensity = solidDensity;
        this.thermalConductivity = thermalConductivity;
        this.boilingPoint = boilingPoint;
        this.meltingPoint = meltingPoint;
        this.magnetism = magnetism;
        this.expansionRatio = expansionRatio;
        this.latentHeatFusion = latentHeatFusion;
        this.latentHeatVaporization = latentHeatVaporization;

        calculateProps();
    }

    public void calculateProps(){
        gasConstant = 1/(gasDensity*Simulator.AMBIENT_TEMP);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    public double getResistivity() {
        return resistivity;
    }

    public void setResistivity(double resistivity) {
        this.resistivity = resistivity;
    }

    public double getSpecificHeat() {
        return specificHeat;
    }

    public void setSpecificHeat(double specificHeat) {
        this.specificHeat = specificHeat;
    }

    public double getGasDensity() {
        return gasDensity;
    }

    public void setGasDensity(double gasDensity) {
        this.gasDensity = gasDensity;
    }

    public double getLiquidDensity() {
        return liquidDensity;
    }

    public void setLiquidDensity(double liquidDensity) {
        this.liquidDensity = liquidDensity;
    }

    public double getSolidDensity() {
        return solidDensity;
    }

    public void setSolidDensity(double solidDensity) {
        this.solidDensity = solidDensity;
    }

    public double getThermalConductivity() {
        return thermalConductivity;
    }

    public void setThermalConductivity(double thermalConductivity) {
        this.thermalConductivity = thermalConductivity;
    }

    public double getBoilingPoint() {
        return boilingPoint;
    }

    public void setBoilingPoint(double boilingPoint) {
        this.boilingPoint = boilingPoint;
    }

    public double getMeltingPoint() {
        return meltingPoint;
    }

    public void setMeltingPoint(double meltingPoint) {
        this.meltingPoint = meltingPoint;
    }

    public double getMagnetism() {
        return magnetism;
    }

    public void setMagnetism(double magnetism) {
        this.magnetism = magnetism;
    }

    public double getExpansionRatio() {
        return expansionRatio;
    }

    public void setExpansionRatio(double expansionRatio) {
        this.expansionRatio = expansionRatio;
    }

    public double getLatentHeatFusion() {
        return latentHeatFusion;
    }

    public void setLatentHeatFusion(double latentHeatFusion) {
        this.latentHeatFusion = latentHeatFusion;
    }

    public double getLatentHeatVaporization() {
        return latentHeatVaporization;
    }

    public void setLatentHeatVaporization(double latentHeatVaporization) {
        this.latentHeatVaporization = latentHeatVaporization;
    }

    public double getGasConstant() {
        return gasConstant;
    }

    public void setGasConstant(double gasConstant) {
        this.gasConstant = gasConstant;
    }
}
