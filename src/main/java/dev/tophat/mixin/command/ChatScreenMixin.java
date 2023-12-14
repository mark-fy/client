package dev.tophat.mixin.command;

import dev.tophat.TopHat;
import dev.tophat.command.CommandRegistry;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    public ChatScreenMixin(final Text title) {
        super(title);
    }

    @Shadow
    public abstract boolean sendMessage(final String text, final boolean addToHistory);

    @Redirect(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ChatScreen;sendMessage(Ljava/lang/String;Z)Z"
            )
    )
    public boolean onSendMessage(final ChatScreen instance, final String text, final boolean addToHistory) {
        final String command = text.trim();

        if (command.startsWith(Character.toString(CommandRegistry.PREFIX))) {
            client.inGameHud.getChatHud().addToMessageHistory(command);
            TopHat.INSTANCE.getCommandRegistry().handle(command);

            return true;
        }

        return this.sendMessage(text, addToHistory);
    }
}
