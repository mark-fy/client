package dev.tophat.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.tophat.command.Command;
import net.minecraft.command.CommandSource;

public class TestCommand extends Command {

    public TestCommand() {
        super("test");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {

        /* Argument with Suggestor : .help arg <player>
        builder.then(literal("arg")
                .then(argument("player", StringArgumentType.word())
                        .suggests(new PlayerListEntrySuggestor())
                        .executes(context -> {
                            MessageUtil.sendMessage(Text.of("hi"));

                            return 1;
                        })));
         */

        /* Sends a Chat message
        builder.executes(context -> {
            MessageUtil.sendMessage(Text.of(TopHat.INSTANCE.getName()));

            return 1;
        });
         */
    }
}
