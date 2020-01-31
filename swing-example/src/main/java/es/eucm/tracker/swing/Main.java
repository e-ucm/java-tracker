/**
 * Copyright © 2019-20 e-UCM (http://www.e-ucm.es/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.eucm.tracker.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import es.eucm.tracker.AccessibleTracker;
import es.eucm.tracker.AlternativeTracker;
import es.eucm.tracker.CompletableTracker;
import es.eucm.tracker.TrackerAsset;
import es.eucm.tracker.TrackerAssetSettings;
import eu.rageproject.asset.manager.Severity;

public class Main {

	private static final float SETTING_LABEL_WIDTH = 0.5f;

	private static final float SETTING_FIELD_WIDTH = 1.0f;

	private static final int SETTING_PADDING_X = 10;

	public static void main(String[] args) {
		Main app = new Main();
		app.launch();
	}

	private TrackerAsset tracker;

	private int infoCount = 0;

	private JFrame window;

	private JTextArea log;

	private JTextField hostField;

	private JTextField trackingCodeField;

	private JTextField usernameField;

	private JTextField passwordField;

	private JRadioButton checkRand;

	private JRadioButton checkSelected;

	private JRadioButton checkAccessed;

	private JRadioButton checkCompleted;

	private JRadioButton checkManual;

	private JTextField verbField;

	private JTextField objectTypeField;

	private JTextField objectIdField;

	private JPanel customTraceOptionsPanel;

	public Main() {
		tracker = new TrackerAsset();
		tracker.setBridge(new JavaBridge() {
			@Override
			public void log(Severity severity, String msg) {
				super.log(severity, msg);
				appendLogMessage(severity, msg);
			}
		});

	}

	private void launch() {
		SwingUtilities.invokeLater(() -> {
			createAndShowGUI();
		});
	}

	private void appendLogMessage(Severity severity, String msg) {
		SwingUtilities.invokeLater(() -> {
			log.setText(log.getText() + "\n\n " + (++infoCount) + " -> "
					+ severity + ": " + msg);
		});
	}

	public void createAndShowGUI() {
		window = new JFrame();
		window.setTitle("Tracker GUI Example");

		Container root = window.getContentPane();

		root.setLayout(new BorderLayout());

		JPanel mainNorthPanel = new JPanel();
		mainNorthPanel
				.setLayout(new BoxLayout(mainNorthPanel, BoxLayout.PAGE_AXIS));

		// Settings and Login & Start panel
		JPanel settingsAndStartPanel = new JPanel(new FlowLayout());
		settingsAndStartPanel.setBorder(
				BorderFactory.createTitledBorder("1.- Settings & Start"));
		settingsAndStartPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainNorthPanel.add(settingsAndStartPanel);

		hostField = new JTextField(30);
		hostField.setText("analytics.example.com");

		trackingCodeField = new JTextField(30);
		trackingCodeField.setText("5bef140d35d17e0082ae3279a1z1noomz1");

		usernameField = new JTextField(20);
		passwordField = new JTextField(20);

		settingsAndStartPanel.add(createSettingsPanel(new Component[][] {
				new Component[] { new JLabel("Host:"), hostField },
				new Component[] { new JLabel("Tracking Code:"),
						trackingCodeField },
				new Component[] { new JLabel("Username:"), usernameField },
				new Component[] { new JLabel("Password:"), passwordField } }));

		// Buttons panel
		JButton startButton = new JButton("Login & Start");
		startButton.addActionListener(new LoginAndStartAction());
		settingsAndStartPanel.add(startButton);

		// Other options

		JPanel verbPanel = new JPanel(new BorderLayout());
		verbPanel.setBorder(BorderFactory.createTitledBorder("2.- Verb"));
		verbPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainNorthPanel.add(verbPanel);

		JPanel verbOptionsPanel = new JPanel(new FlowLayout());
		verbOptionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		verbPanel.add(verbOptionsPanel, BorderLayout.NORTH);

		ButtonGroup optsGroup = new ButtonGroup();

		checkSelected = new JRadioButton("Selected");
		checkSelected.addActionListener(new EnableCustomPanelAction(false));
		optsGroup.add(checkSelected);
		verbOptionsPanel.add(checkSelected);

		checkAccessed = new JRadioButton("Accessed");
		checkAccessed.addActionListener(new EnableCustomPanelAction(false));
		optsGroup.add(checkAccessed);
		verbOptionsPanel.add(checkAccessed);

		checkCompleted = new JRadioButton("Completed");
		checkCompleted.addActionListener(new EnableCustomPanelAction(false));
		optsGroup.add(checkCompleted);
		verbOptionsPanel.add(checkCompleted);

		checkRand = new JRadioButton("Random");
		checkRand.addActionListener(new EnableCustomPanelAction(false));
		checkRand.setSelected(true);
		optsGroup.add(checkRand);
		verbOptionsPanel.add(checkRand);

		checkManual = new JRadioButton("Custom");
		checkManual.addActionListener(new EnableCustomPanelAction(true));
		optsGroup.add(checkManual);
		verbOptionsPanel.add(checkManual);

		verbField = new JTextField(30);
		verbField.setText("accessed");
		verbField.setEnabled(false);

		objectTypeField = new JTextField(30);
		objectTypeField.setText("scene");
		objectTypeField.setEnabled(false);

		objectIdField = new JTextField(30);
		objectIdField.setText("mainMenu");
		objectIdField.setEnabled(false);

		customTraceOptionsPanel = createSettingsPanel(new Component[][] {
				new Component[] { new JLabel("Verb:"), verbField },
				new Component[] { new JLabel("Object Type:"), objectTypeField },
				new Component[] { new JLabel("Object Id:"), objectIdField } });
		customTraceOptionsPanel
				.setBorder(BorderFactory.createTitledBorder("Custom trace"));
		customTraceOptionsPanel.setEnabled(false);
		verbPanel.add(customTraceOptionsPanel, BorderLayout.CENTER);

		root.add(mainNorthPanel, BorderLayout.NORTH);
		root.add(createLogPanel(), BorderLayout.CENTER);
		root.add(createSendPanel(), BorderLayout.SOUTH);

		// Recalculate mininum / preferred size
		window.pack();

		// Center

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - window.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - window.getHeight()) / 2);
		window.setLocation(x, y);

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		window.setVisible(true);

	}

	private JPanel createSettingsPanel(Component[][] settings) {

		JPanel settingsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		for (int i = 0; i < settings.length; i++) {
			c.weightx = SETTING_LABEL_WIDTH;
			c.gridx = 0;
			c.gridy = i;
			c.ipadx = SETTING_PADDING_X;
			c.anchor = GridBagConstraints.LINE_END;
			c.insets = new Insets(0, 0, 0, 10);
			settingsPanel.add(settings[i][0], c);

			c.weightx = SETTING_FIELD_WIDTH;
			c.gridx = 1;
			c.gridy = i;
			c.ipadx = SETTING_PADDING_X;
			c.anchor = GridBagConstraints.LINE_START;
			c.insets = new Insets(0, 0, 0, 10);
			settingsPanel.add(settings[i][1], c);
		}

		return settingsPanel;
	}

	private JComponent createLogPanel() {
		log = new JTextArea(60, 90);
		log.setEditable(false);
		log.setLineWrap(true);

		JScrollPane scrollPane = new JScrollPane(log);
		scrollPane.setPreferredSize(new Dimension(880, 250));
		scrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		return scrollPane;
	}

	private JComponent createSendPanel() {
		JPanel sendPanel = new JPanel(new FlowLayout());

		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendTraceAction());

		sendPanel.add(sendButton);

		return sendPanel;
	}

	private void randomTrace() throws Exception {
		int rand9 = rand9();

		if (rand9 <= 3) {
			selected();
		} else if (rand9 <= 6) {
			accessed();
		} else {
			completed();
		}
	}

	private static int rand9() {
		return (int) ((Math.random() * 9) + 1);
	}

	private void selected() throws Exception {
		AlternativeTracker.Alternative[] values = AlternativeTracker.Alternative
				.values();
		tracker.getAlternative().selected("alternativeId_" + rand9(),
				"alternativeOptionId_" + rand9(),
				values[new Random().nextInt(values.length)]);
	}

	private void accessed() throws Exception {
		AccessibleTracker.Accessible[] values = AccessibleTracker.Accessible
				.values();
		tracker.getAccessible().accessed("accessedId_" + rand9(),
				values[new Random().nextInt(values.length)]);
	}

	private void completed() throws Exception {
		CompletableTracker.Completable[] values = CompletableTracker.Completable
				.values();
		tracker.getCompletable().completed("completableId_" + rand9(),
				values[new Random().nextInt(values.length)]);
	}

	private class LoginAndStartAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			TrackerAssetSettings settings = new TrackerAssetSettings();

			settings.setHost(hostField.getText());
			settings.setPort(443);
			settings.setSecure(true);
			settings.setTraceFormat(TrackerAssetSettings.TraceFormats.XAPI);
			settings.setBasePath("/api/");

			tracker.setSettings(settings);

			String username = usernameField.getText();
			String password = passwordField.getText();
			if (!username.isEmpty() && !password.isEmpty()) {
				tracker.login(username, password);
			}

			tracker.start(trackingCodeField.getText());
		}
	}

	private class SendTraceAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				if (checkRand.isSelected()) {
					randomTrace();
				} else if (checkSelected.isSelected()) {
					selected();
				} else if (checkAccessed.isSelected()) {
					accessed();
				} else if (checkCompleted.isSelected()) {
					completed();
				} else {
					// Manual
					String verb = verbField.getText();
					String objectType = objectTypeField.getText();
					String objectId = objectIdField.getText();
					tracker.trace(verb, objectType, objectId);
				}

				tracker.flush();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private class EnableCustomPanelAction implements ActionListener {

		private boolean enable;

		public EnableCustomPanelAction() {
			this.enable = false;
		}

		public EnableCustomPanelAction(boolean enable) {
			this.enable = enable;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			customTraceOptionsPanel.setEnabled(enable);
			for (Component c : customTraceOptionsPanel.getComponents()) {
				c.setEnabled(enable);
			}
		}

	}
}
