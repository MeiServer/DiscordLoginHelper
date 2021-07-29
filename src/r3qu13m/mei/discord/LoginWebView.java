package r3qu13m.mei.discord;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import r3qu13m.mei.lib.Discord;

public class LoginWebView {
	private static Discord discord;
	private static BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
	private static Scene scene;
	private static JFrame frame;
	private static WebView webView;

	public void login() throws InterruptedException {
		discord = new Discord();
		this.initURLStreamHandler();
		SwingUtilities.invokeLater(() -> {
			this.initAndShowGUI();
		});

		final String res = queue.take();
		if (res.equals("")) {
			System.exit(0);
		}
		discord.authorize(res);
	}

	private void initURLStreamHandler() {
		URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
			public URLStreamHandler createURLStreamHandler(String protocol) {
				if ("https".equals(protocol)) {
					return new HTTPSIntercepter();
				}
				return null;
			}
		});
	}

	private void initAndShowGUI() {
		// This method is invoked on Swing thread
		frame = new JFrame("Discord Login");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		final JFXPanel fxPanel = new JFXPanel();
		fxPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				Platform.runLater(() -> {
					final WebEngine webEngine = webView.getEngine();
					webView.setPrefSize(scene.getWidth(), scene.getHeight());
					webEngine.onResizedProperty();
				});
			}
		});

		frame.add(fxPanel, BorderLayout.CENTER);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent event) {
				try {
					queue.put("");
					Thread.currentThread().destroy();
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		frame.getContentPane().setPreferredSize(new Dimension(1024, 768));
		frame.pack();

		Platform.runLater(() -> {
			this.initFX(fxPanel);
		});
	}

	private void initFX(final JFXPanel fxPanel) {
		final Group group = new Group();
		scene = new Scene(group);
		fxPanel.setScene(scene);

		webView = new WebView();

		group.getChildren().add(webView);

		final WebEngine webEngine = webView.getEngine();
		webEngine.onResizedProperty();
		webEngine.setJavaScriptEnabled(true);
		webEngine.getLoadWorker().stateProperty()
				.addListener((final ObservableValue<State> ov, final State oldState, final State newState) -> {
					if (newState == State.SCHEDULED) {
						if (webEngine.getLocation().contains("example.com")) {
							try {
								final String[] args = webEngine.getLocation().split("code=");
								if (args.length == 2) {
									queue.put(args[1]);
									frame.dispose();
									Thread.currentThread().destroy();
								}
							} catch (final InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});
		webEngine.load(discord.getAuthorizationURL());
	}
}
