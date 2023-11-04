package wtf.accounts.threads;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import wtf.tophat.utilities.Methods;

public final class AltThreadWeb extends Thread implements Methods {

    private String status;
    boolean Mojang;

    public AltThreadWeb(boolean mojang) {
        super("Alt Thread Web");
        Mojang = mojang;
        this.status = EnumChatFormatting.GRAY + "Waiting...";
    }

    private Session createSession() {
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        try {
            MicrosoftAuthResult result = authenticator.loginWithWebview();
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
        this.status = EnumChatFormatting.YELLOW + "§8web: Logging in...";
        Session auth = this.createSession();
        if (auth == null) {
            this.status = EnumChatFormatting.RED + "§8web: Login failed!";
        } else {
            this.status = EnumChatFormatting.GREEN + "§8web: Logged in. (" + auth.getUsername() + ")";
            mc.session = auth;
        }
    }
}

