package org.aldomanco.plantsensor.plant_state;

public class PlantStateModel {

    private String nameState;
    private String iconStatePath;
    private double valueState;
    private String infoState;

    public PlantStateModel(String nameState, String iconStatePath, int valueState, String infoState) {
        this.nameState = nameState;
        this.iconStatePath = iconStatePath;
        this.valueState = valueState;
        this.infoState = infoState;
    }

    public PlantStateModel(String nameState, String iconStatePath, double valueState, String infoState) {
        this.nameState = nameState;
        this.iconStatePath = iconStatePath;
        this.valueState = valueState;
        this.infoState = infoState;
    }

    public String getNameState() {
        return nameState;
    }

    public String getIconStatePath() {
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
}
