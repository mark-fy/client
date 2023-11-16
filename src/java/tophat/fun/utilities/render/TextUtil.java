package tophat.fun.utilities.render;

import tophat.fun.modules.Module;
import tophat.fun.utilities.Methods;

public class TextUtil implements Methods {

    public static String getCategoryLetter(Module.Category category) {
        if(category == Module.Category.COMBAT) {
            return "a";
        } else if(category == Module.Category.MOVEMENT) {
            return "c";
        } else if(category == Module.Category.RENDER) {
            return "f";
        } else if(category == Module.Category.PLAYER) {
            return "e";
        } else if(category == Module.Category.OTHERS) {
            return "b";
        } else {
            return "d";
        }
    }

}
