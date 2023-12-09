package tophat.fun.commands;

import tophat.fun.utilities.Methods;

public class Command implements Methods {

    private final String name = this.getClass().getAnnotation(CommandInfo.class).name();
    private final String desc = this.getClass().getAnnotation(CommandInfo.class).desc();
    private final String command = this.getClass().getAnnotation(CommandInfo.class).command();
    private final String alias = this.getClass().getAnnotation(CommandAlias.class).alias();

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

    public String getAlias() {
        return alias;
    }
}
