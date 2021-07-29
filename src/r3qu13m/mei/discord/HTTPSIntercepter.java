package r3qu13m.mei.discord;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Queue;
import java.util.function.Predicate;

import com.sun.net.ssl.internal.www.protocol.https.Handler;
import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl;

public class HTTPSIntercepter<T> extends Handler {
	private final Predicate<String> condition;
	private final Queue<T> retQueue;
	private final Class<? extends HTTPSInterceptedConnection> conCls;

	public HTTPSIntercepter(final Predicate<String> pred, final Queue<T> que,
			final Class<? extends HTTPSInterceptedConnection> cls) {
		this.condition = pred;
		this.retQueue = que;
		this.conCls = cls;
	}

	@Override
	protected URLConnection openConnection(final URL url) throws IOException {
		if (this.condition.test(url.toString())) {
			try {
				return this.conCls.getConstructor(HttpsURLConnectionOldImpl.class, Queue.class)
						.newInstance(super.openConnection(url), this.retQueue);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}
		return super.openConnection(url);
	}

}
