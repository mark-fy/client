package dev.tophat.mixin.imgui;

import dev.misuyaka.ImGuiImpl;
import dev.tophat.TopHat;
import dev.tophat.module.base.Category;
import dev.tophat.module.base.Module;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ImGuiImpl.draw(io -> {
            if (ImGui.begin("ClickGUI")) {
//                for(Category category : Category.values()) {
//                    ImGui.tabItemButton(category.getName());
//                    float moduleOffset = 20;
//                    for (Module module : TopHat.INSTANCE.getModuleRegistry().getModulesByCategory(category)) {
//                        ImGui.button(module.getName(), 5, 5);
//                        moduleOffset += 10;
//                    }
//                }
                ImGui.end();
            }

            ImGui.showDemoWindow();
        });
    }
}
