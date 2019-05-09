package com.woosa.toilet;

public class DeviceData {

    private String text;
    private int ImageID;

    public DeviceData(String text, int imageID) {
        this.ImageID = imageID;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImageID() {
        return ImageID;
    }

    public void setImageID(int imageID) {
        ImageID = imageID;
    }

}
