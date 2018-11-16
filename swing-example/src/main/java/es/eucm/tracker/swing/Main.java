/**
 * Copyright © 2016 e-UCM (http://www.e-ucm.es/)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.eucm.tracker.swing;

import es.eucm.tracker.Exceptions.XApiException;
import es.eucm.tracker.TrackerAsset;
import es.eucm.tracker.TrackerAssetSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {

    public Main() {
        super("Tracker GUI");

        final TrackerAsset tracker = new TrackerAsset();

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // Data panel
        JPanel panelData = new JPanel();
        GridLayout gl = new GridLayout(2, 2, 0, 5);
        panelData.setLayout(gl);
        panelData.add(new JLabel("Host:"));
        final JTextField hostField = new JTextField(10);
        hostField.setText("https://analytics-test.e-ucm.es/api/proxy/gleaner/collector/");
        panelData.add(hostField);
        panelData.add(new JLabel("Tracking Code:"));
        final JTextField trackingCodeButton = new JTextField(10);
        panelData.add(trackingCodeButton);

        // Buttons panel
        JPanel panelStart = new JPanel();
        panelStart.setLayout(new FlowLayout());
        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TrackerAssetSettings settings = new TrackerAssetSettings();
                String host = hostField.getText();
                String trackingCode = trackingCodeButton.getText();

                settings.setHost(host);

                tracker.setSettings(settings);

                tracker.start(trackingCode);
                //tracker.setHost(host);
                //tracker.setTrackingCode(trackingCode);
                //tracker.start();
            }
        });
        panelStart.add(startButton);

        // Buttons panel
        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new FlowLayout());
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    tracker.requestFlush();
                } catch (XApiException e1) {
                    e1.printStackTrace();
                }
            }
        });
        panelButtons.add(sendButton);

        JPanel panelTextArea = new JPanel();
        panelTextArea.setLayout(new FlowLayout());
        final JTextArea textArea = new JTextArea(60, 90);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        panelTextArea.add(scrollPane);

        // Other options

        JPanel panelOptsTop = new JPanel();
        GridLayout glOpts = new GridLayout(2, 1, 5, 5);
        panelOptsTop.setLayout(glOpts);

        JPanel panelOpts = new JPanel();
        panelOptsTop.add(panelOpts);

        panelOpts.setLayout(new FlowLayout());

        ButtonGroup optsGroup = new ButtonGroup();

        panelOpts.add(new JLabel("Random"));
        JRadioButton checkRand = new JRadioButton();
        optsGroup.add(checkRand);
        panelOpts.add(checkRand);

        panelOpts.add(new JLabel("Selected"));
        JRadioButton checkSelected = new JRadioButton();
        optsGroup.add(checkSelected);
        panelOpts.add(checkSelected);

        panelOpts.add(new JLabel("Accessed"));
        JRadioButton checkAccessed = new JRadioButton();
        optsGroup.add(checkAccessed);
        panelOpts.add(checkAccessed);

        panelOpts.add(new JLabel("Completed"));
        JRadioButton checkCompleted = new JRadioButton();
        optsGroup.add(checkCompleted);
        panelOpts.add(checkCompleted);

        panelOpts.add(new JLabel("Manual"));
        JRadioButton checkManual = new JRadioButton();
        optsGroup.add(checkManual);
        panelOpts.add(checkManual);
        optsGroup.setSelected(checkRand.getModel(), true);

        JPanel panelOptsCAS = new JPanel();
        GridLayout glOptsCAS = new GridLayout(3, 2, 5, 5);
        panelOptsCAS.setLayout(glOptsCAS);
        panelOptsTop.add(panelOptsCAS);

        panelOptsCAS.add(new JLabel("Verb:"));
        final JTextField verbField = new JTextField(10);
        verbField.setText("accessed");
        panelOptsCAS.add(verbField);
        panelOptsCAS.add(new JLabel("Object Type:"));
        final JTextField objectTypeField = new JTextField(10);
        objectTypeField.setText("scene");
        panelOptsCAS.add(objectTypeField);
        panelOptsCAS.add(new JLabel("Object Id:"));
        final JTextField objectIdField = new JTextField(10);
        objectIdField.setText("mainMenu");
        panelOptsCAS.add(objectIdField);

        Container northCont = new Container();
        northCont.setLayout(new BorderLayout());

        northCont.add(panelData, BorderLayout.NORTH);
        northCont.add(panelOptsTop, BorderLayout.CENTER);
        northCont.add(panelStart, BorderLayout.SOUTH);

        Container cp = getContentPane();
        cp.add(northCont, BorderLayout.NORTH);
        cp.add(panelTextArea, BorderLayout.CENTER);
        cp.add(panelButtons, BorderLayout.SOUTH);
    }

    private void randomTrace(TrackerAsset tracker) {
        int rand9 = (int) (Math.random() * 9) + 1;

        if (rand9 <= 3) {
            selected(tracker);
        } else if (rand9 <= 6) {
            accessed(tracker);
        } else {
            completed(tracker);
        }
    }

    private void selected(TrackerAsset tracker) {

    }

    private void accessed(TrackerAsset tracker) {

    }

    private void completed(TrackerAsset tracker) {

    }


    public static void main(String[] args) {
        JFrame f = new Main();
        f.setVisible(true);
    }
}
