package org.robminfor.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.robminfor.engine.Landscape;

public class TradeDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JDialog newdialog;
	
	/**
	 * Create the dialog.
	 * @param parent 
	 */
	public TradeDialog(Frame parent, Landscape landscape) {
		super(parent, true);
		newdialog = this;
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{151, 0};
		gbl_contentPanel.rowHeights = new int[]{15, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblSelectParametersFor = new JLabel("Execute trades");
			GridBagConstraints gbc_lblSelectParametersFor = new GridBagConstraints();
			gbc_lblSelectParametersFor.insets = new Insets(0, 0, 5, 0);
			gbc_lblSelectParametersFor.anchor = GridBagConstraints.WEST;
			gbc_lblSelectParametersFor.gridx = 0;
			gbc_lblSelectParametersFor.gridy = 0;
			contentPanel.add(lblSelectParametersFor, gbc_lblSelectParametersFor);
		}
		//add other items here
		//TODO load this from some external file

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Close");
				cancelButton.setActionCommand("Close");
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
