package r3qu13m.mei.discord;

public class DiscordLoginHelperMain {
	public static void main(final String args[]) {
		try {
			new LoginWebView().getToken();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
}
