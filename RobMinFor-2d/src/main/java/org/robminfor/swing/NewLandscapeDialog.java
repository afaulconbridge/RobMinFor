package org.robminfor.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.robminfor.engine.Landscape;
import org.robminfor.engine.LandscapeFactory;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class NewLandscapeDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JSpinner spinnerSize = new JSpinner();
	private final JSpinner spinnerRichness = new JSpinner();
	private final JSpinner spinnerSeed = new JSpinner();
	private final NewLandscapeDialog newdialog;
	
	private Display display;

	/**
	 * Create the dialog.
	 * @param parent 
	 */
	public NewLandscapeDialog(Frame parent, Display displayTmp) {
		super(parent, true);
		this.display = displayTmp;
		newdialog = this;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{80, 195, 0};
		gbl_contentPanel.rowHeights = new int[]{15, 20, 20, 20, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblSelectParametersFor = new JLabel("Select parameters for new landscape...");
			GridBagConstraints gbc_lblSelectParametersFor = new GridBagConstraints();
			gbc_lblSelectParametersFor.anchor = GridBagConstraints.WEST;
			gbc_lblSelectParametersFor.insets = new Insets(0, 0, 5, 0);
			gbc_lblSelectParametersFor.gridwidth = 2;
			gbc_lblSelectParametersFor.gridx = 0;
			gbc_lblSelectParametersFor.gridy = 0;
			contentPanel.add(lblSelectParametersFor, gbc_lblSelectParametersFor);
		}
		{
			spinnerSize.setModel(new SpinnerNumberModel(32, 32, 128, 1));
			GridBagConstraints gbc_spinnerSize = new GridBagConstraints();
			gbc_spinnerSize.anchor = GridBagConstraints.NORTHEAST;
			gbc_spinnerSize.insets = new Insets(0, 0, 5, 5);
			gbc_spinnerSize.gridx = 0;
			gbc_spinnerSize.gridy = 1;
			contentPanel.add(spinnerSize, gbc_spinnerSize);
		}
		{
			JLabel lblSize = new JLabel("Size");
			GridBagConstraints gbc_lblSize = new GridBagConstraints();
			gbc_lblSize.anchor = GridBagConstraints.WEST;
			gbc_lblSize.insets = new Insets(0, 0, 5, 0);
			gbc_lblSize.gridx = 1;
			gbc_lblSize.gridy = 1;
			contentPanel.add(lblSize, gbc_lblSize);
		}
		{
			spinnerRichness.setModel(new SpinnerNumberModel(new Float(0), new Float(0), new Float(1), new Float(0)));
			GridBagConstraints gbc_spinnerRichness = new GridBagConstraints();
			gbc_spinnerRichness.anchor = GridBagConstraints.NORTHEAST;
			gbc_spinnerRichness.insets = new Insets(0, 0, 5, 5);
			gbc_spinnerRichness.gridx = 0;
			gbc_spinnerRichness.gridy = 2;
			contentPanel.add(spinnerRichness, gbc_spinnerRichness);
		}
		{
			JLabel lblRichness = new JLabel("Richness");
			GridBagConstraints gbc_lblRichness = new GridBagConstraints();
			gbc_lblRichness.anchor = GridBagConstraints.WEST;
			gbc_lblRichness.insets = new Insets(0, 0, 5, 0);
			gbc_lblRichness.gridx = 1;
			gbc_lblRichness.gridy = 2;
			contentPanel.add(lblRichness, gbc_lblRichness);
		}
		{
			spinnerSeed.setModel(new SpinnerNumberModel(42, 0, 1024, 1));
			GridBagConstraints gbc_spinnerSeed = new GridBagConstraints();
			gbc_spinnerSeed.anchor = GridBagConstraints.NORTHEAST;
			gbc_spinnerSeed.insets = new Insets(0, 0, 0, 5);
			gbc_spinnerSeed.gridx = 0;
			gbc_spinnerSeed.gridy = 3;
			contentPanel.add(spinnerSeed, gbc_spinnerSeed);
		}
		{
			JLabel lblRandomSeed = new JLabel("Random seed");
			GridBagConstraints gbc_lblRandomSeed = new GridBagConstraints();
			gbc_lblRandomSeed.anchor = GridBagConstraints.WEST;
			gbc_lblRandomSeed.gridx = 1;
			gbc_lblRandomSeed.gridy = 3;
			contentPanel.add(lblRandomSeed, gbc_lblRandomSeed);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						LandscapeFactory factory = LandscapeFactory.getInstance();
						Integer size = (Integer) spinnerSize.getValue();
						Float richness = (Float) spinnerRichness.getValue();
						Integer seed = (Integer) spinnerSeed.getValue();
						Landscape landscape = factory.generate(size, size, size/2, seed);
						//TODO add other parameters - ore/crystal balance, richness, depthscale, octave count, etc
						display.setLandscape(landscape);
						newdialog.dispose();
					}
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						newdialog.dispose();
					}
				});
			}
		}
	}

}
