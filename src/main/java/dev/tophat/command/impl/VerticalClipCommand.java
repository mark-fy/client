package dev.tophat.command.impl;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.tophat.command.Command;
import dev.tophat.util.MessageUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class VerticalClipCommand extends Command {

    public VerticalClipCommand() {
        super("vclip");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
                argument("blocks", DoubleArgumentType.doubleArg())
                        .executes(context -> {
                            final double blocks = DoubleArgumentType.getDouble(context, "blocks");

                            assert mc.player != null;
                            mc.player.setPosition(
                                    mc.player.getX(),
                                    mc.player.getY() + blocks,
                                    mc.player.getZ()
                            );

                            MessageUtil.sendMessage(
                                    Text.empty()
                                            .append(Text.literal("Clipped you ")).formatted(Formatting.GRAY)
                                            .append(Text.literal(blocks < 0 ? "down" : "up")).formatted(Formatting.BLUE)
                            );

                            return 1;
                        })
        );
    }
}
