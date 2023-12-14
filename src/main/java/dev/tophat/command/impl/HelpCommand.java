package dev.tophat.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.tophat.TopHat;
import dev.tophat.command.Command;
import dev.tophat.util.MessageUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.CharUtils;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> list(context, 1))
                .then(
                        argument("page", IntegerArgumentType.integer())
                                .executes(context -> list(context, IntegerArgumentType.getInteger(context, "page")))
                );
    }

    private int list(final CommandContext<CommandSource> context, final int page) {
        final CommandDispatcher<CommandSource> dispatcher = TopHat.INSTANCE.getCommandRegistry().getDispatcher();
        final String[] commands = dispatcher.getSmartUsage(dispatcher.getRoot(), context.getSource()).values().toArray(new String[0]);
        final int pages = MathHelper.ceil(commands.length / 5D);

        if (page <= 0 || page > pages) {
            MessageUtil.sendMessage(
                    Text.literal("Page ").formatted(Formatting.GRAY)
                            .append(String.valueOf(page)).formatted(Formatting.BLUE)
                            .append(" does not exist.").formatted(Formatting.GRAY)
            );
            return 1;
        }

        final int pageStart = (page - 1) * 5;
        MessageUtil.sendMessage(Text.literal("Usages:").formatted(Formatting.GRAY));

        for (int i = pageStart; i < Math.min(pageStart + 5, commands.length); i++) {
            final String command = commands[i];
            MessageUtil.sendMessage(Text.literal("• ").formatted(Formatting.GRAY).append(command).formatted(Formatting.BLUE));
        }

        MessageUtil.sendMessage(Text.literal(String.format("Page %s/%s.", page, pages)).formatted(Formatting.GRAY));

        return 1;
    }
}
