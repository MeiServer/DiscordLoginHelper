package r3qu13m.mei.discord;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.sun.net.ssl.internal.www.protocol.https.Handler;
import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl;

public class HTTPSIntercepter extends Handler {
	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		System.err.println(url);
		return new HTTPSInterceptedConnection((HttpsURLConnectionOldImpl) super.openConnection(url));
	}

}
