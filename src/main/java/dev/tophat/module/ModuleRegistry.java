package dev.tophat.module;

import com.google.common.eventbus.Subscribe;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import dev.tophat.event.KeyListener;
import dev.tophat.event.Render2DListener;
import dev.tophat.module.base.Category;
import dev.tophat.module.base.Module;
import dev.tophat.module.impl.combat.Aura;
import dev.tophat.module.impl.render.ClickGUI;
import dev.tophat.structure.Registry;

import java.util.List;
import java.util.stream.Collectors;

public class ModuleRegistry extends Registry<Module> {

    public ModuleRegistry() {
        DietrichEvents2.global().subscribe(this);
        this.register(new Aura());
        this.register(new ClickGUI());
    }

    public <M extends Module> M getModule(String input) {
        return (M) this.getObjects().stream().filter(module -> module.getName().equalsIgnoreCase(input)).findFirst().orElse(null);
    }

    public List<Module> getModulesByCategory(Category input) {
        return this.getObjects().stream().filter(module -> module.getCategory().equals(input)).collect(Collectors.toList());
    }

    @Subscribe
    public void onKeyboard(KeyListener.KeyEvent keyListener) {
        this.getObjects().forEach(module -> {
            if (module.getKey() == keyListener.getKey()) {
                module.toggle();
            }
        });
    }

    @Subscribe
    public void onRender2D(Render2DListener.Render2DEvent event) {
        this.getObjects().forEach(module -> {
            if (module != null) {
                if (!module.isEnabled()) {
                    //
                }
            }
        });
    }

}
