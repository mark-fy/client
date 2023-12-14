package dev.tophat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

public abstract class Command implements Comparable<Command> {

    private final String name;

    protected MinecraftClient mc = MinecraftClient.getInstance();

    public Command(final String name) {
        this.name = name;
    }

    public abstract void build(final LiteralArgumentBuilder<CommandSource> builder);

    protected LiteralArgumentBuilder<CommandSource> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    protected <T> RequiredArgumentBuilder<CommandSource, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public void register(final CommandDispatcher<CommandSource> dispatcher) {
        final LiteralArgumentBuilder<CommandSource> builder = this.literal(name);

        this.build(builder);
        dispatcher.register(builder);
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(@NotNull Command o) {
        return name.compareTo(o.name);
    }
}
