package wtf.tophat.modules.impl.render;

import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import wtf.spotify.ColorUtil;
import wtf.spotify.ScissorUtil;
import wtf.spotify.SpotifyAPI;
import wtf.tophat.TopHat;
import wtf.tophat.events.impl.Render2DEvent;
import wtf.tophat.modules.base.Module;
import wtf.tophat.modules.base.ModuleInfo;

import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import wtf.tophat.settings.impl.NumberSetting;
import wtf.tophat.utilities.render.shaders.RoundedUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

@ModuleInfo(name = "Spotify Player",desc = "use spotify in minecraft", category = Module.Category.RENDER)
public class SpotifyPlayer extends Module {

    private final NumberSetting posX, posY;

    public SpotifyPlayer() {
        TopHat.settingManager.add(
                posX = new NumberSetting(this, "X Position", 0, 1000, 50, 0),
                posY = new NumberSetting(this, "Y Position", 0, 1000, 50, 0)
        );
    }

    private boolean downloadedCover;
    private int imageColor = -1;
    private ResourceLocation currentAlbumCover;

    private final SpotifyAPI spotifyAPI = new SpotifyAPI();
    private Track currentTrack;
    private CurrentlyPlayingContext currentPlayingContext;

    @Listen
    public void onRender2D(Render2DEvent event) {
        renderEvent();
    }

    @Override
    public void onEnable() {
        if (getPlayer() == null) {
            toggle();
            return;
        }
        spotifyAPI.init();
        super.onEnable();
    }

    public void renderEvent() {

        //If the user is not playing anything or if the user is not authenticated yet
        if (mc.player == null || spotifyAPI.currentTrack == null || spotifyAPI.currentPlayingContext == null) {
            return;
        }
        //If the current track does not equal the track that is playing on spotify then it sets the variable to the current track
        if (currentTrack != spotifyAPI.currentTrack || currentPlayingContext != spotifyAPI.currentPlayingContext) {
            this.currentTrack = spotifyAPI.currentTrack;
            this.currentPlayingContext = spotifyAPI.currentPlayingContext;
        }

        // You can make these two customizable.
        final int albumCoverSize = 55;
        final int playerWidth = 150;

        final int diff = currentTrack.getDurationMs() - currentPlayingContext.getProgress_ms();
        final long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60;
        final long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;
        final String trackRemaining = String.format("-%s:%s", diffMinutes < 10 ? "0" + diffMinutes : diffMinutes, diffSeconds < 10 ? "0" + diffSeconds : diffSeconds);

        try {
            // The rect methods that have WH at the end means they use width & height instead of x2 and y2

            //Gradient Rect behind the text
            RoundedUtil.drawRound(posX.get().floatValue() - 5, posY.get().floatValue(), playerWidth + albumCoverSize + 10, albumCoverSize,8, new Color(imageColor));

            //We scissor the text to be inside the box
            ScissorUtil.scissor(posX.get().floatValue() + albumCoverSize, posY.get().floatValue(), playerWidth, albumCoverSize);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);

            // Display the current track name
            // TODO: make the text of the current track and artist scroll back and forth, with a pause at each end.
            mc.fontRenderer.drawString("§l" + currentTrack.getName(), posX.get().intValue() + albumCoverSize + 4, posY.get().intValue() + 6, -1);

            /*For every artist, append them to a string builder to make them into a single string
            They are separated by commas unless there is only one Or if its the last one, then its a dot.*/
            final StringBuilder artistsDisplay = new StringBuilder();
            for (int artistIndex = 0; artistIndex < currentTrack.getArtists().length; artistIndex++) {
                final ArtistSimplified artist = currentTrack.getArtists()[artistIndex];
                artistsDisplay.append(artist.getName()).append(artistIndex + 1 == currentTrack.getArtists().length ? '.' : ", ");
            }

            mc.fontRenderer.drawString(artistsDisplay.toString(), posX.get().intValue() + albumCoverSize + 4, posY.get().intValue() + 17, -1);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            // Draw how much time until the track ends
            mc.fontRenderer.drawString(trackRemaining, posX.get().intValue() + playerWidth + 8, posY.get().intValue() + albumCoverSize - 11, -1);

            //This is where we draw the progress bar
            final int progressBarWidth = ((playerWidth - albumCoverSize) * currentPlayingContext.getProgress_ms()) / currentTrack.getDurationMs();
            RoundedUtil.drawRound(posX.get().floatValue() + albumCoverSize + 5, posY.get().floatValue() + albumCoverSize - 9, playerWidth - albumCoverSize, 4, 2, new Color(50, 50, 50));
            RoundedUtil.drawRound(posX.get().floatValue() + albumCoverSize + 5, posY.get().floatValue() + albumCoverSize - 9, progressBarWidth, 4, 2, new Color(255,255,255));

            if (currentAlbumCover != null && downloadedCover) {
                mc.getTextureManager().bindTexture(currentAlbumCover);
                GlStateManager.color(1,1,1);
                Gui.drawModalRectWithCustomSizedTexture(posX.get().intValue(), posY.get().intValue(), 0, 0, albumCoverSize, albumCoverSize, albumCoverSize, albumCoverSize);
            }
            if ((currentAlbumCover == null || !currentAlbumCover.getResourcePath().contains(currentTrack.getAlbum().getId()))) {
                downloadedCover = false;
                final ThreadDownloadImageData albumCover = new ThreadDownloadImageData(null, currentTrack.getAlbum().getImages()[1].getUrl(), null, new IImageBuffer() {

                    public BufferedImage parseUserSkin(BufferedImage image) {
                        imageColor = ColorUtil.averageColor(image, image.getWidth(), image.getHeight(), 1).getRGB();
                        downloadedCover = true;
                        return image;
                    }


                    public void skinAvailable() {
                    }
                });
                GlStateManager.color(1, 1, 1);
                mc.getTextureManager().loadTexture(currentAlbumCover = new ResourceLocation("spotifyAlbums/" + currentTrack.getAlbum().getId()), albumCover);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
