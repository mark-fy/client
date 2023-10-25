package wtf.tophat.script;

import wtf.tophat.script.methods.Chat;
import wtf.tophat.script.methods.Player;
import wtf.tophat.utilities.Methods;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;

import java.io.File;

public class ScriptManager {

    private Globals globals;

    public void init() {
        Methods.createFolder("tophat/scripts");
    }

    public String list(String location) {
        File scriptDir = new File(location);
        File[] files = scriptDir.listFiles((dir, name) -> name.endsWith(".lua"));

        if (files == null || files.length == 0) {
            return "No scripts found.";
        }

        StringBuilder scriptNames = new StringBuilder();
        for (File file : files) {
            String scriptName = file.getName();
            scriptNames.append(scriptName).append(", ");
        }

        if (scriptNames.length() > 2) {
            scriptNames.delete(scriptNames.length() - 2, scriptNames.length());
        }

        return String.format("Available scripts: %s.", scriptNames);
    }

    public void enable(String name, String location) {
        try {
            if (globals == null) {
                globals = JsePlatform.standardGlobals();
            }

            String scriptFilePath = location + File.separator + name + ".lua";
            //System.out.println("Loading script: " + scriptFilePath);

            LuaValue chatMethods = CoerceJavaToLua.coerce(new Chat());
            globals.set("chat", chatMethods);

            LuaValue playerMethods = CoerceJavaToLua.coerce(new Player());
            globals.set("player", playerMethods);
            if (!globals.get(name).isnil()) {
                LuaValue chunk = globals.loadfile(scriptFilePath);
                chunk.call();
            }

            LuaValue onEnableFunction = globals.get("onEnable");

            if (!onEnableFunction.isnil() && onEnableFunction.isfunction()) {
                onEnableFunction.call();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disable() {
        if (globals != null) {
            LuaValue onDisableFunction = globals.get("onDisable");

            if (!onDisableFunction.isnil() && onDisableFunction.isfunction()) {
                onDisableFunction.call();
            }
            globals = null;
        }
    }
}