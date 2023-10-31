package wtf.config.base;

import wtf.tophat.utilities.Methods;

public class Config implements Methods {
    public String name = this.getClass().getAnnotation(ConfigInfo.class).name();
    public String description = this.getClass().getAnnotation(ConfigInfo.class).description();

    public String getName() { return name; }
    public String getDescription() { return description; }
}
