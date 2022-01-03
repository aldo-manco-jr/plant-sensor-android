package org.aldomanco.plantsensor.models;

import org.aldomanco.plantsensor.R;
import org.aldomanco.plantsensor.home.LoggedUserActivity;

public class PlantStateModel {

    private String nameState;
    private int iconStatePath;
    private double valueState;
    private String infoState;

    private double minValueState;
    private double maxValueState;

    private double startingYellowValueState;
    private double endingYellowValueState;

    private double startingGreenValueState;
    private double endingGreenValueState;

    private Color colorPlantState;

    public PlantStateModel(String nameState, int iconStatePath, double valueState, String infoState, double minValueState, double maxValueState, double startingYellowValueState, double endingYellowValueState, double startingGreenValueState, double endingGreenValueState) {
        this.nameState = nameState;
        this.iconStatePath = iconStatePath;
        this.valueState = valueState;
        this.infoState = infoState;
        this.minValueState = minValueState;
        this.maxValueState = maxValueState;
        this.startingYellowValueState = startingYellowValueState;
        this.endingYellowValueState = endingYellowValueState;
        this.startingGreenValueState = startingGreenValueState;
        this.endingGreenValueState = endingGreenValueState;
        this.colorPlantState = Color.NO_COLOR;
        setColorPlantState();
    }

    public String getNameState() {
        return nameState;
    }

    public void setNameState(String nameState) {
        this.nameState = nameState;
    }

    public int getIconStatePath() {
        return iconStatePath;
    }

    public void setIconStatePath(int iconStatePath) {
        this.iconStatePath = iconStatePath;
    }

    public double getValueState() {
        return valueState;
    }

    public void setValueState(double valueState) {
        this.valueState = valueState;
    }

    public String getInfoState() {
        return infoState;
    }

    public void setInfoState(String infoState) {
        this.infoState = infoState;
    }

    public double getMinValueState() {
        return minValueState;
    }

    public void setMinValueState(double minValueState) {
        this.minValueState = minValueState;
    }

    public double getMaxValueState() {
        return maxValueState;
    }

    public void setMaxValueState(double maxValueState) {
        this.maxValueState = maxValueState;
    }

    public double getStartingYellowValueState() {
        return startingYellowValueState;
    }

    public void setStartingYellowValueState(double startingYellowValueState) {
        this.startingYellowValueState = startingYellowValueState;
    }

    public double getEndingYellowValueState() {
        return endingYellowValueState;
    }

    public void setEndingYellowValueState(double endingYellowValueState) {
        this.endingYellowValueState = endingYellowValueState;
    }

    public double getStartingGreenValueState() {
        return startingGreenValueState;
    }

    public void setStartingGreenValueState(double startingGreenValueState) {
        this.startingGreenValueState = startingGreenValueState;
    }

    public double getEndingGreenValueState() {
        return endingGreenValueState;
    }

    public void setEndingGreenValueState(double endingGreenValueState) {
        this.endingGreenValueState = endingGreenValueState;
    }

    public Color getColorPlantState() {
        return colorPlantState;
    }

    public void setColorPlantState() {

        if (valueState >= startingGreenValueState
                && valueState <= endingGreenValueState) {

            this.colorPlantState = Color.GREEN;

        } else if (valueState >= startingYellowValueState
                && valueState < startingGreenValueState) {

            this.colorPlantState = Color.YELLOW_NEGATIVE;

        } else if (valueState > endingGreenValueState
                && valueState <= endingYellowValueState) {

            this.colorPlantState = Color.YELLOW_POSITIVE;

        } else if (valueState > endingYellowValueState) {

            this.colorPlantState = Color.RED_POSITIVE;

        } else if (valueState < startingYellowValueState) {

            this.colorPlantState = Color.RED_NEGATIVE;

        }
    }
}
