public class Wire {
    private Material material;
    private double radius;
    private double mass;
    private double length;
    private double resistance;
    private double crossArea;
    private double temperature;

    public Wire(Material material, double radius, double length) {
        this.material = material;
        this.radius = radius;
        this.length = length;

        calculateProps();
    }

    public void calculateProps(){
        crossArea = radius * radius * Simulator.PI;
        resistance = (material.getMaterialType().getResistivity() / crossArea) * length;
        mass = (length * crossArea) * material.getDensity();
        material.setMass(mass);
        temperature = Simulator.AMBIENT_TEMP;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getResistance() {
        return resistance;
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    public double getCrossArea() {
        return crossArea;
    }

    public void setCrossArea(double crossArea) {
        this.crossArea = crossArea;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }
}
