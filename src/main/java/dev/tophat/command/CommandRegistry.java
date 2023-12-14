package dev.tophat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.tophat.command.impl.TestCommand;
import dev.tophat.structure.Registry;
import dev.tophat.util.MessageUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

import java.util.Objects;

public class CommandRegistry extends Registry<Command> {

    private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
    public final static Character PREFIX = '.';

    public CommandRegistry() {
        this.register(new TestCommand());
    }

    public void handle(final String message) {
        final StringReader reader = new StringReader(message);

        if (reader.peek() == PREFIX) reader.skip();

        final ClientCommandSource source = Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getCommandSource();
        final ParseResults<CommandSource> parse = this.dispatcher.parse(reader, source);

        try {
            this.dispatcher.execute(parse);
        } catch (final CommandSyntaxException exception) {
            MessageUtil.sendMessage(
                    Text.literal("")
                            .append(Texts.toText(exception.getRawMessage())).formatted(Formatting.RED)
            );

            if (exception.getCursor() >= 0) {
                final int position = Math.min(exception.getInput().length(), exception.getCursor());
                final MutableText verbose = Text.empty().formatted(Formatting.GRAY).styled(
                        style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, message))
                );

                if (position > 10) {
                    verbose.append(ScreenTexts.ELLIPSIS);
                }

                verbose.append(exception.getInput().substring(Math.max(0, position - 10), position));

                if (position < exception.getInput().length()) {
                    final Text text = Text.literal(exception.getInput().substring(position)).formatted(Formatting.RED, Formatting.UNDERLINE);
                    verbose.append(text);
                }
                verbose.append(Text.translatable("command.context.here").formatted(Formatting.RED, Formatting.ITALIC));

                MessageUtil.sendMessage(verbose);
            }
        }
    }

    @Override
    public void register(final Command object) {
        object.register(dispatcher);
        super.register(object);
    }

    public CommandDispatcher<CommandSource> getDispatcher() {
        return this.dispatcher;
    }
}
