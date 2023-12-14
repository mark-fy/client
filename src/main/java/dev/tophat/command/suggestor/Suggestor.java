package dev.tophat.command.suggestor;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class Suggestor {

    private final SuggestionsBuilder builder;

    public Suggestor(final SuggestionsBuilder builder) {
        this.builder = builder;
    }

    public Suggestor add(final String str) {
        if (str.toLowerCase().startsWith(this.builder.getRemainingLowerCase()))
            this.builder.suggest(str);
        return this;
    }

    public Suggestor addAll(final Iterable<String> list) {
        list.forEach(this::add);
        return this;
    }

    public Suggestor addAll(final Stream<String> stream) {
        stream.forEach(this::add);
        return this;
    }

    public CompletableFuture<Suggestions> buildFuture() {
        return this.builder.buildFuture();
    }
}