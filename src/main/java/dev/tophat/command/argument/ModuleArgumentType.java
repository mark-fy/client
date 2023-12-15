package dev.tophat.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.tophat.TopHat;
import dev.tophat.command.suggestor.Suggestor;
import dev.tophat.module.base.Module;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class ModuleArgumentType implements ArgumentType<Module> {

    private final static DynamicCommandExceptionType DOES_NOT_EXIST = new DynamicCommandExceptionType(
            name -> Text.of("The module " + name + " does not exist.")
    );

    @Override
    public Module parse(final StringReader reader) throws CommandSyntaxException {
        final String name = reader.readString();
        final Module module = TopHat.INSTANCE.getModuleRegistry().getModule(name);
        if (module == null) throw DOES_NOT_EXIST.createWithContext(reader, name);

        return module;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return new Suggestor(builder).addAll(TopHat.INSTANCE.getModuleRegistry().getObjects().stream().map(Module::getName)).buildFuture();
    }

    public static Module getModule(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Module.class);
    }

    public static ModuleArgumentType module() {
        return new ModuleArgumentType();
    }
}
