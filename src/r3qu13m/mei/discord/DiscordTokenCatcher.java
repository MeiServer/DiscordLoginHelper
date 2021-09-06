package r3qu13m.mei.discord;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileSystemView;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class DiscordTokenCatcher {
	private static BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);
	private static Scene scene;
	private static JFrame frame;
	private static WebView webView;

	public static Optional<String> getToken() throws IOException {
		/*
		 * DiscordTokenCatcher.initURLStreamHandler();
		 * 
		 * SwingUtilities.invokeLater(() -> { DiscordTokenCatcher.initAndShowGUI(); });
		 * 
		 * final String res = DiscordTokenCatcher.queue.take(); if (res.equals("")) {
		 * return Optional.empty(); }
		 * 
		 * DiscordTokenCatcher.frame.dispose();
		 * 
		 * return Optional.of(res);
		 */
		runBrowser();
		return Optional.empty();
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

		URLConnection conn = new URL(String.format(
				"https://github.com/MeiServer/DiscordLoginHelper/releases/download/v1.1.1/discordbrowser-%s-x64.zip",
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
			final byte buf[] = new byte[1024];
			while (true) {
				final int readCount = is.read(buf, 0, 1024);
				if (readCount == -1) {
					break;
				}
				os.write(buf, 0, readCount);
			}
			os.flush();
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
					final OutputStream os = new FileOutputStream(dest);
					final InputStream is = zf.getInputStream(entry);
					final byte buf[] = new byte[1024];
					while (true) {
						final int readCount = is.read(buf, 0, 1024);
						if (readCount == -1) {
							break;
						}
						os.write(buf, 0, readCount);
					}
					os.flush();
					is.close();
					os.close();
				}
				if (entry.getName().replace(".exe", "").endsWith("discordbrowser")) {
					executableFile = dest;
				}
			}

			Files.setPosixFilePermissions(executableFile.toPath(),
					EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_EXECUTE));
			Runtime.getRuntime().exec(executableFile.getAbsolutePath());
		}

		System.err.println("YEAH");
	}

	private static void initURLStreamHandler() {
		URL.setURLStreamHandlerFactory(protocol -> {
			if ("https".equals(protocol)) {
				return new HTTPSIntercepter<>(url -> url.contains("/users/@me"), DiscordTokenCatcher.queue,
						TokenIntercepter.class);
			}
			return null;
		});
	}

	private static void initAndShowGUI() {
		// This method is invoked on Swing thread
		DiscordTokenCatcher.frame = new JFrame("Discord Login");
		DiscordTokenCatcher.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		final JFXPanel fxPanel = new JFXPanel();
		fxPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				Platform.runLater(() -> {
					final WebEngine webEngine = DiscordTokenCatcher.webView.getEngine();
					DiscordTokenCatcher.webView.setPrefSize(DiscordTokenCatcher.scene.getWidth(),
							DiscordTokenCatcher.scene.getHeight());
					webEngine.onResizedProperty();
				});
			}
		});

		DiscordTokenCatcher.frame.add(fxPanel, BorderLayout.CENTER);
		DiscordTokenCatcher.frame.setVisible(true);

		DiscordTokenCatcher.frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent event) {
				try {
					DiscordTokenCatcher.queue.put("");
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		DiscordTokenCatcher.frame.getContentPane().setPreferredSize(new Dimension(400, 650));
		DiscordTokenCatcher.frame.pack();

		Platform.runLater(() -> {
			DiscordTokenCatcher.initFX(fxPanel);
		});
	}

	private static void initFX(final JFXPanel fxPanel) {
		DiscordTokenCatcher.webView = new WebView();

		DiscordTokenCatcher.scene = new Scene(DiscordTokenCatcher.webView);
		fxPanel.setScene(DiscordTokenCatcher.scene);

		final WebEngine webEngine = DiscordTokenCatcher.webView.getEngine();
		webEngine.onResizedProperty();
		webEngine.load("https://discord.com/login");
	}
}
