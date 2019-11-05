public class Rotor {
    private Material material;
    private double mass;
    private double radius;
    private double length;
    private double moment;

    public Rotor(Material material, double radius, double length) {
        this.material = material;
        this.radius = radius;
        this.length = length;

        calculateProps();
    }

    public void calculateProps(){
        this.mass = ((radius * radius) * Simulator.PI * length) * material.getDensity();
        this.moment = (mass/2) * (radius * radius);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
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

    public double getMoment() {
        return moment;
    }

    public void setMoment(double moment) {
        this.moment = moment;
    }
}
