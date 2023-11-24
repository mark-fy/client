package tophat.fun.menu.alt;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import tophat.fun.utilities.Methods;

public class AltThread extends Thread implements Methods {

    private final String password, username;
    private String status, error;

    public AltThread(String username, String password) {
        super("Alt Thread");
        this.username = username;
        this.password = password;
        this.status = EnumChatFormatting.GRAY + "Waiting...";
    }

    private Session createSession(String username, String password) {
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        try {
            MicrosoftAuthResult result = authenticator.loginWithCredentials(username, password);
            error = "";
            return new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "mojang");
        } catch (MicrosoftAuthenticationException e) {
            error = e.getMessage();
            return null;
        }
    }

    public String getStatus() { return this.status; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public void run() {
        if (password.equals("")) {
            mc.session = new Session(username, "", "", "mojang");
            setStatus("§aSuccess: §rLogged in cracked as §6" + username);
            return;
        }
        setStatus("§eInfo: §rTrying to log in...");
        Session auth = createSession(username, password);
        if (auth == null) {
            setStatus("§cError: §r" + error);
        } else {
            setStatus("§aSuccess: §rLogged in as §6" + auth.getUsername());
            mc.session = auth;
        }
    }
}
