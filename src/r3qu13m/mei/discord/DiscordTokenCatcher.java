package r3qu13m.mei.discord;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.filechooser.FileSystemView;

public class DiscordTokenCatcher {
	private static BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);

	public static Optional<String> getToken() throws IOException, InterruptedException {
		DiscordTokenCatcher.runBrowser();

		final Thread catcherThread = new Thread(() -> {
			while (true) {
				final StringBuilder sb = new StringBuilder();
				try {
					try (InputStream is = new URL("http://127.0.0.1:2525/").openStream();
							InputStreamReader isr = new InputStreamReader(is);
							BufferedReader br = new BufferedReader(isr)) {
						while (br.ready()) {
							sb.append(br.readLine());
						}
					}
					final String res = sb.toString();
					if (res.length() != 0) {
						DiscordTokenCatcher.queue.put(res);

						try (InputStream is = new URL("http://127.0.0.1:2525/stop").openStream()) {
							is.read();
						} catch (final IOException e) {
							// ignore
						}

						break;
					}
					Thread.sleep(500);
				} catch (IOException | InterruptedException e) {
					// do nothing
				}
			}
		});

		catcherThread.start();
		catcherThread.join();

		return Optional.ofNullable(DiscordTokenCatcher.queue.poll());
	}

	private static void runBrowser() throws IOException {
		System.out.println("Downloading browser...");
		final String name = System.getProperty("os.name").toLowerCase();
		String arch;

		if (name.startsWith("win")) {
			arch = "win32";
		} else if (name.startsWith("mac")) {
			arch = "darwin";
		} else if (name.startsWith("linux")) {
			arch = "linux";
		} else {
			throw new RuntimeException(String.format("Unknown environment: %s is not supported!", name));
		}

		final URLConnection conn = new URL(String.format(
				"https://github.com/MeiServer/DiscordLoginHelper/releases/download/v1.1.2/discordbrowser-%s-x64.zip",
				arch)).openConnection();
		conn.setRequestProperty("User-Agent", "DiscordTokenCatcher");
		conn.connect();

		final File baseDir = new File(FileSystemView.getFileSystemView().getDefaultDirectory(), "MeiServer");
		if (!baseDir.exists()) {
			baseDir.mkdirs();
		}

		final File browserDestFile = new File(baseDir, "DiscordBrowser.zip");
		if (!browserDestFile.exists()) {
			final InputStream is = new BufferedInputStream(conn.getInputStream());
			final OutputStream os = new FileOutputStream(browserDestFile);

			is.close();
			os.close();
		}

		System.out.println("Extracting browser module");
		File executableFile = null;
		try (final ZipFile zf = new ZipFile(browserDestFile)) {
			final Enumeration<? extends ZipEntry> entries = zf.entries();
			while (entries.hasMoreElements()) {
				final ZipEntry entry = entries.nextElement();
				if (entry.getName().contains("../")) {
					// https://snyk.io/research/zip-slip-vulnerability
					throw new RuntimeException(
							String.format("Unsupported Path Name: %s (maybe Zip Slip exploit?)", entry.getName()));
				}
				final File dest = new File(baseDir, entry.getName());
				if (entry.isDirectory()) {
					dest.mkdirs();
				} else {
					try (final OutputStream os = new FileOutputStream(dest);
							final InputStream is = zf.getInputStream(entry)) {
						DiscordTokenCatcher.copyToStream(is, os);
					}
				}
				if (entry.getName().replace(".exe", "").endsWith("discordbrowser")) {
					executableFile = dest;
				}

				Files.setPosixFilePermissions(dest.toPath(),
						EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE,
								PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_READ,
								PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_EXECUTE,
								PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_WRITE,
								PosixFilePermission.OTHERS_EXECUTE));
			}
		}

		System.err.println("Run browser");
		Runtime.getRuntime().exec(executableFile.getAbsolutePath());
	}

	private static void copyToStream(final InputStream is, final OutputStream os) throws IOException {
		final byte buf[] = new byte[1024];
		while (true) {
			final int readCount = is.read(buf, 0, 1024);
			if (readCount == -1) {
				break;
			}
			os.write(buf, 0, readCount);
		}
		os.flush();
	}
}
