package r3qu13m.mei.discord;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class DiscordTokenCatcher {
	private static BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);
	private static Scene scene;
	private static JFrame frame;
	private static WebView webView;

	public static Optional<String> getToken() throws InterruptedException {
		DiscordTokenCatcher.initURLStreamHandler();

		SwingUtilities.invokeLater(() -> {
			DiscordTokenCatcher.initAndShowGUI();
		});

		final String res = DiscordTokenCatcher.queue.take();
		if (res.equals("")) {
			return Optional.empty();
		}

		DiscordTokenCatcher.frame.dispose();
		return Optional.of(res);
	}

	private static void initURLStreamHandler() {
		URL.setURLStreamHandlerFactory(protocol -> {
			if ("https".equals(protocol)) {
				return new HTTPSIntercepter<>(url -> url.contains("/api/v"), DiscordTokenCatcher.queue,
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

		DiscordTokenCatcher.frame.getContentPane().setPreferredSize(new Dimension(1024, 768));
		DiscordTokenCatcher.frame.pack();

		Platform.runLater(() -> {
			DiscordTokenCatcher.initFX(fxPanel);
		});
	}

	private static void initFX(final JFXPanel fxPanel) {
		final Group group = new Group();
		DiscordTokenCatcher.scene = new Scene(group);
		fxPanel.setScene(DiscordTokenCatcher.scene);

		DiscordTokenCatcher.webView = new WebView();

		group.getChildren().add(DiscordTokenCatcher.webView);

		final WebEngine webEngine = DiscordTokenCatcher.webView.getEngine();
		webEngine.onResizedProperty();
		webEngine.load("https://discord.com/channels/@me");
	}
}
