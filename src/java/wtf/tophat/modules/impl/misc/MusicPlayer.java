package wtf.tophat.modules.impl.misc;

import wtf.tophat.TopHat;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.settings.impl.StringSetting;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

@ModuleInfo(name = "Music Player", desc = "playing music simply", category = Module.Category.MISC)
public class MusicPlayer extends Module {

    private Clip clip;

    private final StringSetting track;
    private final NumberSetting volume;

    public MusicPlayer(){
        TopHat.settingManager.add(
                track = new StringSetting(this, "Track", "Art of Scratch (Intro)", "Art of Scratch (Intro)", "Shook Ones, Pt. II", "Deep Cover", "Switchez", "Constant Elevation", "N.Y. State of Mind", "California Love", "Big Poppa", "No Mores 's"),
                volume = new NumberSetting(this, "Volume", 0, 100, 100, 0)
        );
    }

    @Override
    public void onEnable() {
        String filepath = "C:\\Users\\Public\\musics\\" + track.get() + ".wav"; //make the thing like config / script folder !!
        playMusic(filepath);
        setVolume(volume.get().intValue() * 2);
    }

    @Override
    public void onDisable() {
        stopMusic();
    }

    public void playMusic(String location) {
        if(getPlayer() == null || getWorld() == null)
            return;
        try {
            File musicPath = new File(location);

            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
            } else {
                System.out.println("Can't find file");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void setVolume(int volume) {
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume / 100.0f) + gainControl.getMinimum();
            gainControl.setValue(gain);
        }
    }

}
