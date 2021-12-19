package org.aldomanco.plantsensor.models;

public class PlantStateModel {

    private String nameState;
    private int iconStatePath;
    private Object valueState;
    private String infoState;

    private Object minValueState;
    private Object maxValueState;

    private Object startingYellowValueState;
    private Object endingYellowValueState;

    private Object startingGreenValueState;
    private Object endingGreenValueState;

    public PlantStateModel(String nameState, int iconStatePath, int valueState, String infoState, int minValueState, Object maxValueState, Object startingYellowValueState, int endingYellowValueState, int startingGreenValueState, int endingGreenValueState) {
        this.nameState = nameState;
        this.iconStatePath = iconStatePath;
        this.valueState = (int) valueState;
        this.infoState = infoState;
        this.minValueState = (int) minValueState;
        this.maxValueState = (int) maxValueState;
        this.startingYellowValueState = (int) startingYellowValueState;
        this.endingYellowValueState = (int) endingYellowValueState;
        this.startingGreenValueState = (int) startingGreenValueState;
        this.endingGreenValueState = (int) endingGreenValueState;
    }

    public PlantStateModel(String nameState, int iconStatePath, double valueState, String infoState, double minValueState, double maxValueState, double startingYellowValueState, double endingYellowValueState, Object startingGreenValueState, Object endingGreenValueState) {
        this.nameState = nameState;
        this.iconStatePath = iconStatePath;
        this.valueState = (double) valueState;
        this.infoState = infoState;
        this.minValueState = (double) minValueState;
        this.maxValueState = (double) maxValueState;
        this.startingYellowValueState = (double) startingYellowValueState;
        this.endingYellowValueState = (double) endingYellowValueState;
        this.startingGreenValueState = (double) startingGreenValueState;
        this.endingGreenValueState = (double) endingGreenValueState;
    }

    public PlantStateModel(String nameState, int iconStatePath, String valueState, String infoState) {
        this.nameState = nameState;
        this.iconStatePath = iconStatePath;
        this.valueState = (String) valueState;
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

    public Object getValueState() {
        return valueState;
    }

    public void setValueState(int valueState) {
        this.valueState = valueState;
    }

    public void setValueState(double valueState) {
        this.valueState = valueState;
    }

    public void setValueState(String valueState) {
        this.valueState = valueState;
    }

    public void setIconStatePath(int iconStatePath) {
        this.iconStatePath = iconStatePath;
    }

    public Object getMinValueState() {
        return minValueState;
    }

    public void setMinValueState(Object minValueState) {
        this.minValueState = minValueState;
    }

    public Object getMaxValueState() {
        return maxValueState;
    }

    public void setMaxValueState(Object maxValueState) {
        this.maxValueState = maxValueState;
    }

    public Object getStartingYellowValueState() {
        return startingYellowValueState;
    }

    public void setStartingYellowValueState(Object startingYellowValueState) {
        this.startingYellowValueState = startingYellowValueState;
    }

    public Object getEndingYellowValueState() {
        return endingYellowValueState;
    }

    public void setEndingYellowValueState(Object endingYellowValueState) {
        this.endingYellowValueState = endingYellowValueState;
    }

    public Object getStartingGreenValueState() {
        return startingGreenValueState;
    }

    public void setStartingGreenValueState(Object startingGreenValueState) {
        this.startingGreenValueState = startingGreenValueState;
    }

    public Object getEndingGreenValueState() {
        return endingGreenValueState;
    }

    public void setEndingGreenValueState(Object endingGreenValueState) {
        this.endingGreenValueState = endingGreenValueState;
    }
}
