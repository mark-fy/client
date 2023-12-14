package dev.tophat;

import dev.tophat.command.CommandRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;

import java.util.stream.Collectors;

public class TopHat implements ClientModInitializer{

    private final String name;
    private final String version;
    private final String authors;

    public static TopHat INSTANCE;

    public TopHat() {
        final ModMetadata metadata = FabricLoader.getInstance().getModContainer("tophat").orElseThrow().getMetadata();

        this.name = metadata.getName();
        this.version = metadata.getVersion().getFriendlyString();
        this.authors = metadata.getAuthors().stream().map(Person::getName).collect(Collectors.joining(", "));
    }

    private CommandRegistry commandRegistry;


    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        this.commandRegistry = new CommandRegistry();
    }

    public CommandRegistry getCommandRegistry() {
        return this.commandRegistry;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getAuthors() {
        return this.authors;
    }

}
