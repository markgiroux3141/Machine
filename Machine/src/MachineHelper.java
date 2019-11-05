public class MachineHelper {
    public static Material pullFromChamber(Chamber chamber, double volumeToPull){
        if(volumeToPull <= chamber.getMaxVolume() && volumeToPull <= MaterialHelper.materialVolume(chamber.getContents())){
            return chamber.removeFromContents(MaterialHelper.materialMassFromVolume(chamber.getContents(),volumeToPull));
        }
        return null;
    }

    public static boolean pushToChamber(Chamber chamber, double volumeToPush, Material material){
        boolean success = false;
        if(volumeToPush <= chamber.getMaxVolume() - MaterialHelper.materialVolume(chamber.getContents())){
            chamber.addToContents(material);
            success = true;
        }
        return success;
    }
}
