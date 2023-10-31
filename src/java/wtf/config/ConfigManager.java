package wtf.config;

import wtf.tophat.utilities.Methods;

public class ConfigManager implements Methods {

    public void init() {
        Methods.createFolder("tophat/configs");
    }

}
