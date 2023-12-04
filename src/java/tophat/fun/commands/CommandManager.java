package tophat.fun.commands;

import de.florianmichael.rclasses.storage.Storage;
import io.github.nevalackin.radbus.Listen;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Keyboard;
import org.reflections.Reflections;
import tophat.fun.Client;
import tophat.fun.events.impl.game.KeyboardEvent;
import tophat.fun.events.impl.network.ChatEvent;
import tophat.fun.utilities.Methods;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

public class CommandManager extends Storage<Command> implements Methods {

    public void init() {
        Client.INSTANCE.eventManager.subscribe(this);
        Reflections reflections = new Reflections("tophat.fun.commands.impl");
        reflections.getTypesAnnotatedWith(CommandInfo.class).forEach(aClass -> {
            try {
                this.add((Command) aClass.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    public Command getCommandByName(String input) {
        return this.getList().stream().filter(command -> command.getName().equalsIgnoreCase(input)).findFirst().orElse(null);
    }

    @Override
    public <V extends Command> V getByClass(final Class<V> clazz) {
        final Command feature = this.getList().stream().filter(c -> c.getClass().equals(clazz)).findFirst().orElse(null);
        if (feature == null) return null;
        return clazz.cast(feature);
    }

    @Listen
    public void onCommand(ChatEvent event) {
        if(event.isCancelled()) {
            return;
        }

        String message = event.getMessage();
        String[] arguments = message.split(" ");

        if (message.startsWith(".")) {
            event.setCancelled(true);
        } else {
            return;
        }

        String name = arguments[0].substring(1);

        Command command = getCommandByName(name);

        if (command != null) {
            command.onCommand(arguments, message);
        } else {
            sendChat(String.format("Could not find command .%s", name.toLowerCase(Locale.ROOT)), true);
        }
    }

    @Listen
    public void onKeyboard(KeyboardEvent event) {
        if(event.getKeyCode() == Keyboard.KEY_PERIOD) {
            mc.displayGuiScreen(new GuiChat("."));
        }
    }

}
