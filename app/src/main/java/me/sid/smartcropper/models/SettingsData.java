package me.sid.smartcropper.models;

public class SettingsData {

    int icon;
    String itemName;
    int goToIcon;

    public SettingsData(int icon, String itemName, int goToIcon) {
        this.icon = icon;
        this.itemName = itemName;
        this.goToIcon = goToIcon;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getGoToIcon() {
        return goToIcon;
    }

    public void setGoToIcon(int goToIcon) {
        this.goToIcon = goToIcon;
    }


}
