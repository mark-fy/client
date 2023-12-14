package dev.tophat.mixin.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import dev.tophat.TopHat;
import dev.tophat.command.CommandRegistry;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatInputSuggestor.class)
public class ChatInputSuggestorMixin {

    @Unique
    private boolean command;

    @Redirect(
            method = "refresh",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/brigadier/StringReader;peek()C"
            )
    )
    public char onRefresh(final StringReader instance) {
        final char peek = instance.peek();

        this.command = peek == CommandRegistry.PREFIX;
        if (command) return '/';

        return peek;
    }

    @Redirect(
            method = "refresh",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;getCommandDispatcher()Lcom/mojang/brigadier/CommandDispatcher;")
    )
    public CommandDispatcher<CommandSource> onParseCommandDispatcher(final ClientPlayNetworkHandler instance) {
        if (this.command) return TopHat.INSTANCE.getCommandRegistry().getDispatcher();
        return instance.getCommandDispatcher();
    }

    @Redirect(
            method = "showUsages",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;getCommandDispatcher()Lcom/mojang/brigadier/CommandDispatcher;")
    )
    public CommandDispatcher<CommandSource> onUsageCommandDispatcher(final ClientPlayNetworkHandler instance) {
        if (this.command) return TopHat.INSTANCE.getCommandRegistry().getDispatcher();
        return instance.getCommandDispatcher();
    }
}
