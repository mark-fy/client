package wtf.tophat.screen.alts;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import wtf.tophat.utilities.Methods;

public final class AltThread extends Thread implements Methods {

    private final String password;
    private String status;
    private final String username;
    boolean Mojang;

    public AltThread(String username, String password, boolean mojang) {
        super("Alt Thread");
        this.username = username;
        this.password = password;
        Mojang = mojang;
        this.status = EnumChatFormatting.GRAY + "Waiting...";
    }

    private Session createSession(String username, String password) {
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        try {
            MicrosoftAuthResult result = authenticator.loginWithCredentials(username, password);
            return new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "mojang");
        } catch (MicrosoftAuthenticationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getStatus() { return this.status; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public void run() {
        if (this.password.equals("")) {
            mc.session = new Session(this.username, "", "", "mojang");
            this.status = EnumChatFormatting.GREEN + "Logged in. (" + this.username + " - offline name)";
            return;
        }
        this.status = EnumChatFormatting.YELLOW + "Logging in...";
        Session auth = this.createSession(this.username, this.password);
        if (auth == null) {
            this.status = EnumChatFormatting.RED + "Login failed!";
        } else {
            this.status = EnumChatFormatting.GREEN + "Logged in. (" + auth.getUsername() + ")";
            mc.session = auth;
        }
    }
}

