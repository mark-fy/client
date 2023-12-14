package dev.tophat.module.base;

public enum Category {

    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    RENDER("Render"),
    OTHERS("Others");

    private final String name;

    Category(String name) { this.name = name; }

    public String getName() {
        return name;
    }

}
