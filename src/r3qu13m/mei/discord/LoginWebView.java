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

public class LoginWebView {
	private static BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);
	private static Scene scene;
	private static JFrame frame;
	private static WebView webView;

	public Optional<String> getToken() throws InterruptedException {
		this.initURLStreamHandler();

		SwingUtilities.invokeLater(() -> {
			this.initAndShowGUI();
		});

		final String res = LoginWebView.queue.take();
		if (res.equals("")) {
			return Optional.empty();
		}

		LoginWebView.frame.dispose();
		return Optional.of(res);
	}

	private void initURLStreamHandler() {
		URL.setURLStreamHandlerFactory(protocol -> {
			if ("https".equals(protocol)) {
				return new HTTPSIntercepter<>(url -> url.contains("/api/v"), LoginWebView.queue, TokenIntercepter.class);
			}
			return null;
		});
	}

	private void initAndShowGUI() {
		// This method is invoked on Swing thread
		LoginWebView.frame = new JFrame("Discord Login");
		LoginWebView.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		final JFXPanel fxPanel = new JFXPanel();
		fxPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				Platform.runLater(() -> {
					final WebEngine webEngine = LoginWebView.webView.getEngine();
					LoginWebView.webView.setPrefSize(LoginWebView.scene.getWidth(), LoginWebView.scene.getHeight());
					webEngine.onResizedProperty();
				});
			}
		});

		LoginWebView.frame.add(fxPanel, BorderLayout.CENTER);
		LoginWebView.frame.setVisible(true);

		LoginWebView.frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent event) {
				try {
					LoginWebView.queue.put("");
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		LoginWebView.frame.getContentPane().setPreferredSize(new Dimension(1024, 768));
		LoginWebView.frame.pack();

		Platform.runLater(() -> {
			this.initFX(fxPanel);
		});
	}

	private void initFX(final JFXPanel fxPanel) {
		final Group group = new Group();
		LoginWebView.scene = new Scene(group);
		fxPanel.setScene(LoginWebView.scene);

		LoginWebView.webView = new WebView();

		group.getChildren().add(LoginWebView.webView);

		final WebEngine webEngine = LoginWebView.webView.getEngine();
		webEngine.onResizedProperty();
		webEngine.load("https://discord.com/channels/@me");
	}
}
