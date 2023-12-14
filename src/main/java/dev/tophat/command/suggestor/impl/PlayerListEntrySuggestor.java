package dev.tophat.command.suggestor.impl;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.tophat.command.suggestor.Suggestor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class PlayerListEntrySuggestor implements SuggestionProvider<CommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        MinecraftClient mc = MinecraftClient.getInstance();
        return new Suggestor(builder)
                .addAll(Objects.requireNonNull(mc.getNetworkHandler()).getPlayerList().stream()
                        .filter(player -> !player.getProfile().getId().equals(Objects.requireNonNull(mc.player).getUuid()))
                        .map(player -> player.getProfile().getName()))
                .buildFuture();
    }
}