package dev.tophat.util;

import dev.tophat.TopHat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MessageUtil {

    public static void sendMessage(final Text text) {
        final Text formattedText = Text.literal("").append(text).formatted(Formatting.WHITE);
        final Text line = Text.literal("").append(getPrefix()).append(" ").append(formattedText);

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(line);
    }

    public static MutableText getPrefix() {
        return Text.literal("")
                .append(Text.literal(TopHat.INSTANCE.getName())
                        .formatted(Formatting.BLUE))
                .append(Text.literal(" • ")
                        .formatted(Formatting.DARK_GRAY));
    }
}
