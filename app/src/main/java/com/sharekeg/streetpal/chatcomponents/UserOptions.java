package com.sharekeg.streetpal.chatcomponents;

/**
 * Created by Khalid on 8/16/2017.
 */

public class UserOptions {
    private String positiveButtonText, negativeButtonText, neutralButtonText;
    private int optionsCount, positiveButtonId, negativeButtonId, neutralButtonId;

    public UserOptions(String positiveButtonText, String negativeButtonText, String neutralButtonText, int positiveButtonId, int negativeButtonId, int neutralButtonId) {
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.neutralButtonText = neutralButtonText;
        this.optionsCount = 3;
        this.positiveButtonId = positiveButtonId;
        this.negativeButtonId = negativeButtonId;
        this.neutralButtonId = neutralButtonId;
    }

    public UserOptions(String positiveButtonText, String negativeButtonText, int positiveButtonId, int negativeButtonId) {
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.optionsCount = 2;
        this.positiveButtonId = positiveButtonId;
        this.negativeButtonId = negativeButtonId;
    }

    public UserOptions(String positiveButtonText, int positiveButtonId) {
        this.positiveButtonText = positiveButtonText;
        this.optionsCount = 1;
        this.positiveButtonId = positiveButtonId;
    }

    public String getPositiveButtonText() {
        return positiveButtonText;
    }

    public void setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
    }

    public String getNegativeButtonText() {
        return negativeButtonText;
    }

    public void setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
    }

    public String getNeutralButtonText() {
        return neutralButtonText;
    }

    public void setNeutralButtonText(String neutralButtonText) {
        this.neutralButtonText = neutralButtonText;
    }

    public int getOptionsCount() {
        return optionsCount;
    }

    public void setOptionsCount(int optionsCount) {
        this.optionsCount = optionsCount;
    }

    public int getPositiveButtonId() {
        return positiveButtonId;
    }

    public void setPositiveButtonId(int positiveButtonId) {
        this.positiveButtonId = positiveButtonId;
    }

    public int getNegativeButtonId() {
        return negativeButtonId;
    }

    public void setNegativeButtonId(int negativeButtonId) {
        this.negativeButtonId = negativeButtonId;
    }

    public int getNeutralButtonId() {
        return neutralButtonId;
    }

    public void setNeutralButtonId(int neutralButtonId) {
        this.neutralButtonId = neutralButtonId;
    }
}
