public class MaterialHelper {
    public static double materialVolume(Material material){
        if(material == null) return 0;
        if(material.getMaterialState() != Material.MATERIAL_STATE.GAS){
            return material.getMass()/ material.getDensity();
        }else{
            return (material.getMass() * material.getMaterialType().getGasConstant() * material.getTemperature()) / material.getPressure();
        }
    }

    public static double materialMassFromVolume(Material material, double volume){
        if(material.getMaterialState() != Material.MATERIAL_STATE.GAS){
            return volume * material.getDensity();
        }else{
            return (material.getPressure() * volume) / (material.getMaterialType().getGasConstant() * material.getTemperature());
        }
    }

    public static Material addMaterials(Material material1, Material material2){
        if(material1.getMaterialType() == material2.getMaterialType() && material1.getMaterialState() == material2.getMaterialState()){
            double volume = materialVolume(material1) + materialVolume(material2);
            double tMass1 = material1.getTemperature() * material1.getMass();
            double tMass2 = material2.getTemperature() * material2.getMass();
            double totalMass = material1.getMass() + material2.getMass();
            double temperature = (tMass1 + tMass2)/totalMass;
            if(material1.getMaterialState() == Material.MATERIAL_STATE.GAS){
                double pressure = (totalMass * material1.getMaterialType().getGasConstant() * temperature) / volume;
                return new Material(material1.getMaterialType(), totalMass, temperature, pressure);
            }else{
                return new Material(material1.getMaterialType(),totalMass,temperature);
            }
        }
        return material1;
    }

    public static Material createMaterialCopy(Material material, double mass){
        Material mat = new Material(material.getMaterialType(), mass, material.getTemperature(), material.getPressure());
        return mat;
    }

    public static double getHeatTransfer(Material material1, Material material2, double area){
        double effectiveThermalConductivity = Math.min(material1.getMaterialType().getThermalConductivity(), material2.getMaterialType().getThermalConductivity());
        return effectiveThermalConductivity * area * (material2.getTemperature() - material1.getTemperature());
    }

    public static void setMaterialTemperaturesFromHeatTransfer(Material material1, Material material2, double area, double time){
        double heat = getHeatTransfer(material1, material2, area) * time;
        double temperature1 = material1.getTemperature() + (heat/(material1.getMass() * material1.getMaterialType().getSpecificHeat()));
        double temperature2 = material2.getTemperature() + ((-heat)/(material2.getMass() * material2.getMaterialType().getSpecificHeat()));
        material1.setTemperature(temperature1);
        material2.setTemperature(temperature2);
    }

    public static double getPressure(double initialPressure, double initialVolume, double finalVolume){
        return initialPressure * Math.pow((initialVolume/finalVolume), Simulator.GAMMA);
    }

    public static double getVolume(double initialVolume, double initialPressure, double finalPressure){
        return initialVolume / Math.pow((finalPressure/initialPressure),(1/Simulator.GAMMA));
    }

    public static double getTemperature(double initialTemperature, double initialPressure, double initialVolume, double finalPressure, double finalVolume){
        return ((finalPressure*finalVolume) / (initialPressure*initialVolume)) * initialTemperature;
    }

    public static void materialPressureFromTemperature(Material material, double volume){
        double pressure = (material.getMass() * material.getMaterialType().getGasConstant() * material.getTemperature()) / volume;
        material.setPressure(pressure);
    }
}
