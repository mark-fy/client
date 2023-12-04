package tophat.fun.modules;

import de.florianmichael.rclasses.storage.Storage;
import io.github.nevalackin.radbus.Listen;
import org.reflections.Reflections;
import tophat.fun.Client;
import tophat.fun.events.impl.game.KeyboardEvent;
import tophat.fun.events.impl.render.Render2DEvent;
import tophat.fun.utilities.Methods;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager extends Storage<Module> implements Methods {

    public void init() {
        Client.INSTANCE.eventManager.subscribe(this);
        Reflections reflections = new Reflections("tophat.fun.modules.impl");
        reflections.getTypesAnnotatedWith(ModuleInfo.class).forEach(aClass -> {
            try {
                this.add((Module) aClass.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public <V extends Module> V getByClass(final Class<V> clazz) {
        final Module feature = this.getList().stream().filter(m -> m.getClass().equals(clazz)).findFirst().orElse(null);
        if (feature == null) return null;
        return clazz.cast(feature);
    }

    public <M extends Module> M getModule(String input) {
        return (M) this.getList().stream().filter(module -> module.getName().equalsIgnoreCase(input)).findFirst().orElse(null);
    }

    public List<Module> getModulesByCategory(Module.Category input) {
        return this.getList().stream().filter(module -> module.getCategory().equals(input)).collect(Collectors.toList());
    }

    public List<Module> getEnabledModules() {
        return this.getList().stream().filter(Module::isEnabled).collect(Collectors.toList());
    }

    @Listen
    public void onKeyboard(KeyboardEvent event) {
        this.getList().forEach(module -> {
            if (module.getKeyCode() == event.getKeyCode()) {
                module.toggle();
            }
        });
    }

    @Listen
    public void onRender2D(Render2DEvent event) {
        this.getList().forEach(module -> {
            if (module != null) {
                if (!mc.gameSettings.showDebugInfo && module.isEnabled()) {
                    module.renderIngame();
                }
            }
        });
    }

}
