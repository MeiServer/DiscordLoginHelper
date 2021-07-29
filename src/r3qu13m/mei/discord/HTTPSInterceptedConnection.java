package r3qu13m.mei.discord;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Permission;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;

import com.sun.net.ssl.HttpsURLConnection;
import com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl;

@SuppressWarnings("deprecation")
public class HTTPSInterceptedConnection extends HttpsURLConnection {
	private final HttpsURLConnectionOldImpl impl;

	public HTTPSInterceptedConnection(final HttpsURLConnectionOldImpl impl) throws IOException {
		super(impl.getURL());
		this.impl = impl;
	}

	@Override
	public String getCipherSuite() {
		return this.impl.getCipherSuite();
	}

	@Override
	public X509Certificate[] getServerCertificateChain() {
		return this.impl.getServerCertificateChain();
	}

	@Override
	public void disconnect() {
		this.impl.disconnect();
	}

	@Override
	public boolean usingProxy() {
		return this.impl.usingProxy();
	}

	@Override
	public void connect() throws IOException {
		this.impl.connect();
	}

	public Certificate[] getLocalCertificates() {
		return this.impl.getLocalCertificates();
	}

	public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
		return this.impl.getServerCertificates();
	}

	@Override
	public synchronized OutputStream getOutputStream() throws IOException {
		return this.impl.getOutputStream();
	}

	@Override
	public synchronized InputStream getInputStream() throws IOException {
		return this.impl.getInputStream();
	}

	@Override
	public InputStream getErrorStream() {
		return this.impl.getErrorStream();
	}

	@Override
	public Map<String, List<String>> getHeaderFields() {
		return this.impl.getHeaderFields();
	}

	@Override
	public String getHeaderField(final String name) {
		return this.impl.getHeaderField(name);
	}

	@Override
	public String getHeaderField(final int ordinal) {
		return this.impl.getHeaderField(ordinal);
	}

	@Override
	public String getHeaderFieldKey(final int ordinal) {
		return this.impl.getHeaderField(ordinal);
	}

	@Override
	public void setRequestProperty(final String key, final String value) {
		this.impl.setRequestProperty(key, value);
	}

	@Override
	public void addRequestProperty(final String key, final String value) {
		this.impl.addRequestProperty(key, value);
	}

	@Override
	public int getResponseCode() throws IOException {
		return this.impl.getResponseCode();
	}

	@Override
	public String getRequestProperty(final String key) {
		return this.impl.getRequestProperty(key);
	}

	@Override
	public Map<String, List<String>> getRequestProperties() {
		return this.impl.getRequestProperties();
	}

	@Override
	public void setInstanceFollowRedirects(final boolean b) {
		this.impl.setInstanceFollowRedirects(b);
	}

	@Override
	public boolean getInstanceFollowRedirects() {
		return this.impl.getInstanceFollowRedirects();
	}

	@Override
	public void setRequestMethod(final String method) throws ProtocolException {
		this.impl.setRequestMethod(method);
	}

	@Override
	public String getRequestMethod() {
		return this.impl.getRequestMethod();
	}

	@Override
	public String getResponseMessage() throws IOException {
		return this.impl.getResponseMessage();
	}

	@Override
	public long getHeaderFieldDate(final String key, final long value) {
		return this.impl.getHeaderFieldDate(key, value);
	}

	@Override
	public Permission getPermission() throws IOException {
		return this.impl.getPermission();
	}

	@Override
	public URL getURL() {
		return this.impl.getURL();
	}

	@Override
	public int getContentLength() {
		return this.impl.getContentLength();
	}

	@Override
	public long getContentLengthLong() {
		return this.impl.getContentLengthLong();
	}

	@Override
	public String getContentType() {
		return this.impl.getContentType();
	}

	@Override
	public String getContentEncoding() {
		return this.impl.getContentEncoding();
	}

	@Override
	public long getExpiration() {
		return this.impl.getExpiration();
	}

	@Override
	public long getDate() {
		return this.impl.getDate();
	}

	@Override
	public long getLastModified() {
		return this.impl.getLastModified();
	}

	@Override
	public int getHeaderFieldInt(final String key, final int value) {
		return this.impl.getHeaderFieldInt(key, value);
	}

	@Override
	public long getHeaderFieldLong(final String key, final long value) {
		return this.impl.getHeaderFieldLong(key, value);
	}

	@Override
	public Object getContent() throws IOException {
		return this.impl.getContent();
	}

	@Override
	public String toString() {
		return this.impl.toString();
	}

	@Override
	public void setDoInput(final boolean b) {
		this.impl.setDoInput(b);
	}

	@Override
	public boolean getDoInput() {
		return this.impl.getDoInput();
	}

	@Override
	public void setDoOutput(final boolean b) {
		this.impl.setDoOutput(b);
	}

	@Override
	public boolean getDoOutput() {
		return this.impl.getDoOutput();
	}

	@Override
	public void setAllowUserInteraction(final boolean b) {
		this.impl.setAllowUserInteraction(b);
	}

	@Override
	public boolean getAllowUserInteraction() {
		return this.impl.getAllowUserInteraction();
	}

	@Override
	public void setUseCaches(final boolean b) {
		this.impl.setUseCaches(b);
	}

	@Override
	public boolean getUseCaches() {
		return this.impl.getUseCaches();
	}

	@Override
	public void setIfModifiedSince(final long date) {
		this.impl.setIfModifiedSince(date);
	}

	@Override
	public long getIfModifiedSince() {
		return this.impl.getIfModifiedSince();
	}

	@Override
	public boolean getDefaultUseCaches() {
		return this.impl.getDefaultUseCaches();
	}

	@Override
	public void setDefaultUseCaches(final boolean b) {
		this.impl.setDefaultUseCaches(b);
	}

	@Override
	public boolean equals(final Object obj) {
		return this.impl.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.impl.hashCode();
	}

	@Override
	public void setConnectTimeout(final int time) {
		this.impl.setConnectTimeout(time);
	}

	@Override
	public int getConnectTimeout() {
		return this.impl.getConnectTimeout();
	}

	@Override
	public void setReadTimeout(final int time) {
		this.impl.setReadTimeout(time);
	}

	@Override
	public int getReadTimeout() {
		return this.impl.getReadTimeout();
	}

	@Override
	public void setFixedLengthStreamingMode(final int len) {
		this.impl.setFixedLengthStreamingMode(len);
	}

	@Override
	public void setFixedLengthStreamingMode(final long len) {
		this.impl.setFixedLengthStreamingMode(len);
	}

	@Override
	public void setChunkedStreamingMode(final int mode) {
		this.impl.setChunkedStreamingMode(mode);
	}

}
