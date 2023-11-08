package wtf.tophat.client.commands.base;

import wtf.tophat.client.utilities.Methods;

public class Command implements Methods {
    public String name = this.getClass().getAnnotation(CommandInfo.class).name();
    public String description = this.getClass().getAnnotation(CommandInfo.class).description();
    public String command = this.getClass().getAnnotation(CommandInfo.class).command();

    public void onCommand(String[] args, String command) {}

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCommand() { return command; }
}
