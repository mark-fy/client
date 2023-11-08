package wtf.tophat.config;

import wtf.tophat.client.utilities.Methods;

public class ConfigManager implements Methods {

    public void init() {
        Methods.createFolder("tophat/configs");
    }

}
