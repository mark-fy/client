package tophat.fun.utilities.render;

import tophat.fun.modules.Module;
import tophat.fun.utilities.Methods;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String createAsteriskString(String input) {
        StringBuilder asterisks = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            asterisks.append('*');
        }
        return asterisks.toString();
    }

    public static boolean isPrintableChar(char c) {
        return c >= 32 && c <= 126;
    }

    public static int isColorCode(String input) {
        Pattern pattern = Pattern.compile("§[a-zA-Z]");
        Matcher matcher = pattern.matcher(input);

        int count = 0;
        while (matcher.find()) {
            count++;
        }

        return count;
    }

}
