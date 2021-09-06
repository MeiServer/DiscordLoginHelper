package r3qu13m.mei.discord;

import java.io.IOException;

public class DiscordLoginHelperMain {
	public static void main(final String args[]) {
		try {
			System.err.println(DiscordTokenCatcher.getToken());
		} catch (final IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
