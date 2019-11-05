public class Coil {
    private Wire wire;
    private double numTurns;
    private double coilArea;

    public Coil(Wire wire, double numTurns, double coilArea) {
        this.wire = wire;
        this.numTurns = numTurns;
        this.coilArea = coilArea;

        if(numTurns == -1){
            double coilPerimeter = Math.sqrt(coilArea) * 4;
            this.numTurns = wire.getLength() / coilPerimeter;
        }
        if(coilArea == -1){
            double coilPerimeter = wire.getLength()/numTurns;
            double coilSideLength = coilPerimeter / 4;
            this.coilArea = coilSideLength * coilSideLength;
        }
    }

    public Wire getWire() {
        return wire;
    }

    public void setWire(Wire wire) {
        this.wire = wire;
    }

    public double getNumTurns() {
        return numTurns;
    }

    public void setNumTurns(double numTurns) {
        this.numTurns = numTurns;
    }

    public double getCoilArea() {
        return coilArea;
    }

    public void setCoilArea(double coilArea) {
        this.coilArea = coilArea;
    }
}
