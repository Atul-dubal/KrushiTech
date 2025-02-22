package apcoders.in.krushitech.models;

public class SettingOption {
    private String title;
    private int iconResId; // Resource ID for the icon
    private Runnable action; // Action to perform on click

    public SettingOption(String title, int iconResId, Runnable action) {
        this.title = title;
        this.iconResId = iconResId;
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public Runnable getAction() {
        return action;
    }
}
