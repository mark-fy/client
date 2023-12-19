package dev.tophat.command.impl;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.tophat.TopHat;
import dev.tophat.command.Command;
import dev.tophat.command.argument.ModuleArgumentType;
import dev.tophat.module.ModuleRegistry;
import dev.tophat.module.base.Module;
import dev.tophat.util.MessageUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ToggleCommand extends Command {

    public ToggleCommand() {
        super("toggle");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
                argument("module", ModuleArgumentType.module())
                        .executes(context -> {
                            final Module module = ModuleArgumentType.getModule(context, "module");
                            module.toggle();

                            return 1;
                        })
        );
    }
}
