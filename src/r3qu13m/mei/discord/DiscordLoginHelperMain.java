package r3qu13m.mei.discord;

public class DiscordLoginHelperMain {
	public static void main(String args[]) {
		try {
			new LoginWebView().login();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
