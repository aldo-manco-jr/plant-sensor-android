package org.aldomanco.plantsensor.plant_state;

public class PlantStateModel {

    private String nameState;
    private int iconStatePath;
    private double valueState;
    private String infoState;

    public PlantStateModel(String nameState, int iconStatePath, int valueState, String infoState) {
        this.nameState = nameState;
        this.iconStatePath = iconStatePath;
        this.valueState = valueState;
        this.infoState = infoState;
    }

    public PlantStateModel(String nameState, int iconStatePath, double valueState, String infoState) {
        this.nameState = nameState;
        this.iconStatePath = iconStatePath;
        this.valueState = valueState;
        this.infoState = infoState;
    }

    public String getNameState() {
        return nameState;
    }

    public int getIconStatePath() {
        return iconStatePath;
    }

    public String getInfoState() {
        return infoState;
    }

    public double getValueState() {
        return valueState;
    }

    public void setValueState(int valueState) {
        this.valueState = valueState;
    }

    public void setIconStatePath(int iconStatePath) {
        this.iconStatePath = iconStatePath;
    }
}
