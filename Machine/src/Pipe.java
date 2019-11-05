public class Pipe {
    private Material material;
    private double diameter;
    private double thickness;
    private double length;

    private double mass;
    private double innerSurfaceArea;
    private double outerSurfaceArea;
    private double volumeCapacity;
    private double maxPressure;

    public Pipe(Material material, double diameter, double thickness, double length) {
        this.material = material;
        this.diameter = diameter;
        this.thickness = thickness;
        this.length = length;

        calculateProps();
    }

    public void calculateProps(){
        double outerRadius = diameter / 2;
        double innerRadius = (diameter/2) - thickness;
        double innerCrossArea = (innerRadius * innerRadius) * Simulator.PI;
        volumeCapacity = innerCrossArea * length;

        double innerCircumference = innerRadius * 2 * Simulator.PI;
        double outerCircumference = diameter * Simulator.PI;
        innerSurfaceArea = innerCircumference * length;
        outerSurfaceArea = outerCircumference * length;

        double materialVolume = (((outerRadius * outerRadius) * Simulator.PI) - innerCrossArea) * length;
        mass = material.getDensity() * materialVolume;
        material.setMass(mass);

        maxPressure = (material.getMaterialType().getStrength() * thickness) / diameter;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public double getDiameter() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public double getThickness() {
        return thickness;
    }

    public void setThickness(double thickness) {
        this.thickness = thickness;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getInnerSurfaceArea() {
        return innerSurfaceArea;
    }

    public void setInnerSurfaceArea(double innerSurfaceArea) {
        this.innerSurfaceArea = innerSurfaceArea;
    }

    public double getOuterSurfaceArea() {
        return outerSurfaceArea;
    }

    public void setOuterSurfaceArea(double outerSurfaceArea) {
        this.outerSurfaceArea = outerSurfaceArea;
    }

    public double getMaxPressure() {
        return maxPressure;
    }

    public void setMaxPressure(double maxPressure) {
        this.maxPressure = maxPressure;
    }

    public double getVolumeCapacity() {
        return volumeCapacity;
    }

    public void setVolumeCapacity(double volumeCapacity) {
        this.volumeCapacity = volumeCapacity;
    }
}
