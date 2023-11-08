package wtf.tophat.viaversion;

import wtf.tophat.viaversion.viamcp.ViaMCP;

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
