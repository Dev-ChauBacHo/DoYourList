package com.chaubacho.doyourlist2.data.model;

public class ColorItem {
    private String ColorCode;
    private int ColorImage;

    public ColorItem(String colorCode, int colorImage) {
        ColorCode = colorCode;
        ColorImage = colorImage;
    }

    public int getColorImage() {
        return ColorImage;
    }

    public void setColorImage(int colorImage) {
        ColorImage = colorImage;
    }

    public String getColorCode() {
        return ColorCode;
    }

    public void setColorCode(String colorCode) {
        ColorCode = colorCode;
    }
}
