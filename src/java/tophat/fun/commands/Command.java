package tophat.fun.commands;

import tophat.fun.utilities.Methods;

public class Command implements Methods {

    private final String name = this.getClass().getAnnotation(CommandInfo.class).name();
    private final String desc = this.getClass().getAnnotation(CommandInfo.class).desc();
    private final String command = this.getClass().getAnnotation(CommandInfo.class).command();

    public void onCommand(String[] args, String command) { /* */ }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getCommand() {
        return command;
    }
}
