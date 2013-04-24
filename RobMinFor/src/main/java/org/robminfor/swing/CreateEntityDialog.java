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
import org.robminfor.engine.Site;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JList;

public class CreateEntityDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final CreateEntityDialog newdialog;
	private final List<Site> selected;
	private final Landscape landscape;
	
	private Display display;

	/**
	 * Create the dialog.
	 * @param parent 
	 */
	public CreateEntityDialog(Frame parent, Display displayTmp, List<Site> selectedTmp, Landscape landscapeTmp) {
		super(parent, true);
		this.display = displayTmp;
		newdialog = this;
		this.selected = selectedTmp;
		this.landscape = landscapeTmp;
		
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
			JLabel lblSelectParametersFor = new JLabel("Select entity to place");
			GridBagConstraints gbc_lblSelectParametersFor = new GridBagConstraints();
			gbc_lblSelectParametersFor.insets = new Insets(0, 0, 5, 0);
			gbc_lblSelectParametersFor.anchor = GridBagConstraints.WEST;
			gbc_lblSelectParametersFor.gridx = 0;
			gbc_lblSelectParametersFor.gridy = 0;
			contentPanel.add(lblSelectParametersFor, gbc_lblSelectParametersFor);
		}
		{
			List<String> items = new ArrayList<String>();
			items.add("Stonemasony");
			//add other items here
			//TODO load this from some external file
			JList list = new JList(items.toArray());
			GridBagConstraints gbc_list = new GridBagConstraints();
			gbc_list.fill = GridBagConstraints.BOTH;
			gbc_list.gridx = 0;
			gbc_list.gridy = 1;
			contentPanel.add(list, gbc_list);
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
						for (Site site : selected) {
							//TODO create actions
						}
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
