package wtf.viaversion;

import wtf.viaversion.viamcp.ViaMCP;

public class VersionManager {

    public void init() {
        try {
            ViaMCP.create();

            ViaMCP.INSTANCE.initAsyncSlider();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
