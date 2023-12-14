package dev.tophat.mixin.imgui;

import dev.misuyaka.ImGuiImpl;
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
                ImGui.end();
            }

            ImGui.showDemoWindow();
        });
    }
}
