package wtf.tophat.client.commands.impl;

import wtf.tophat.client.commands.base.Command;
import wtf.tophat.client.commands.base.CommandInfo;


@CommandInfo(name = "Clear", description = "clears the chat", command = ".clear")
public class Clear extends Command {

    @Override
    public void onCommand(String[] args, String command) {
        Thread clearChatThread = new Thread(() -> {
            sendChat("Clearing the chat!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mc.ingameGUI.getChatGUI().clearChatMessages();
        });
        clearChatThread.start();
    }

}
