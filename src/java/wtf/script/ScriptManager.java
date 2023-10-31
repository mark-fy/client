package wtf.script;

import wtf.script.methods.*;
import wtf.tophat.utilities.Methods;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ScriptManager {

    private Globals globals;
    private Set<String> enabledScripts = new HashSet<>();
    private Timer updateTimer = new Timer(), motionTimer = new Timer(), tickTimer = new Timer();

    public void init() {
        Methods.createFolder("tophat/scripts");
    }

    public void enable(String name, String location) {
        try {
            if (enabledScripts.contains(name)) {
                disableScript(name);
            }

            if (globals == null) {
                globals = JsePlatform.standardGlobals();
            }

            String scriptFilePath = location + File.separator + name + ".lua";

            LuaValue chatMethods = CoerceJavaToLua.coerce(new Chat());
            globals.set("chat", chatMethods);

            LuaValue playerMethods = CoerceJavaToLua.coerce(new Player());
            globals.set("player", playerMethods);

            LuaValue worldMethods = CoerceJavaToLua.coerce(new World());
            globals.set("world", worldMethods);

            LuaValue mouseMethods = CoerceJavaToLua.coerce(new Mouse());
            globals.set("mouse", mouseMethods);

            LuaValue packetMethods = CoerceJavaToLua.coerce(new Packet());
            globals.set("packet", packetMethods);

            LuaValue chunk = globals.loadfile(scriptFilePath);
            chunk.call();

            LuaValue onEnableFunc = globals.get("onEnable");
            LuaValue onUpdateFunc = globals.get("onUpdate");
            LuaValue onMotionFunc = globals.get("onMotion");
            LuaValue onTickFunc = globals.get("onTick");

            if (!onEnableFunc.isnil() && onEnableFunc.isfunction()) {
                onEnableFunc.call();
                enabledScripts.add(name);
            }

            if (!onUpdateFunc.isnil() && onUpdateFunc.isfunction()) {
                TimerTask timerTask = new TimerTask() {
                    public void run() {
                        onUpdateFunc.call();
                    }
                };
                updateTimer.scheduleAtFixedRate(timerTask, 0, 1);
                enabledScripts.add(name);
            }

            if (!onMotionFunc.isnil() && onMotionFunc.isfunction()) {
                TimerTask timerTask = new TimerTask() {
                    public void run() {
                        onMotionFunc.call();
                    }
                };
                motionTimer.scheduleAtFixedRate(timerTask, 0, 1);
                enabledScripts.add(name);
            }

            if (!onTickFunc.isnil() && onTickFunc.isfunction()) {
                TimerTask timerTask = new TimerTask() {
                    public void run() {
                        onTickFunc.call();
                    }
                };
                tickTimer.scheduleAtFixedRate(timerTask, 0, 50);
                enabledScripts.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disable() {
        for (String script : enabledScripts) {
            disableScript(script);
        }
    }

    private void disableScript(String name) {
        if (globals != null) {
            LuaValue onDisableFunction = globals.get("onDisable");

            if (!onDisableFunction.isnil() && onDisableFunction.isfunction()) {
                onDisableFunction.call();
            }
            enabledScripts.remove(name);
        }
        updateTimer.cancel();
        motionTimer.cancel();
        tickTimer.cancel();
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
}