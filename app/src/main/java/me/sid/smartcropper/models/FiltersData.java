package me.sid.smartcropper.models;

public class FiltersData {

    int filterIcon;
    int filterId;
    String filterName;

    public FiltersData(int filterIcon, String filterName,int filterId) {
        this.filterIcon = filterIcon;
        this.filterName = filterName;
        this.filterId = filterId;
    }

    public int getFilterIcon() {
        return filterIcon;
    }

    public void setFilterIcon(int filterIcon) {
        this.filterIcon = filterIcon;
    }

    public int getFilterId() {
        return filterId;
    }

    public void setFilterId(int filterId) {
        this.filterId = filterId;
    }
    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }
}
