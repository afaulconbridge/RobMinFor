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

import org.robminfor.engine.Landscape;
import org.robminfor.engine.Site;
import org.robminfor.engine.actions.AbstractAction;
import org.robminfor.engine.actions.Deploy;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.EntityManager;
import org.robminfor.engine.entities.IStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;

import javax.swing.JList;
import javax.swing.AbstractListModel;

public class CreateEntityDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JDialog newdialog;
	private final List<Site> selected;
	private final Landscape landscape;

    private Logger log = LoggerFactory.getLogger(getClass());
	
	private class OkActionListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			for (Site site : selected) {
				//TODO allow for buying of things rather than magically creating it
				//TODO allow for selecting of different things
				AbstractEntity thing = EntityManager.getEntityManager().getEntity("Stonemasonry");
				
				Site source = landscape.findNearestStorageFor(site, thing);
				IStorage sourceStorage = (IStorage) source.getEntity();
				sourceStorage.addEntity(thing.getName());
				
				AbstractAction action = new Deploy(source, site, thing);
				landscape.addAction(action);
				log.info("creating Deploy of Stonemasonry");
			}
			newdialog.dispose();
		}
	}
	
	/**
	 * Create the dialog.
	 * @param parent 
	 */
	public CreateEntityDialog(Frame parent, List<Site> selectedTmp, Landscape landscapeTmp) {
		super(parent, true);
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
			//add other items here
			//TODO load this from some external file
			JList list = new JList();
			list.setModel(new AbstractListModel() {
				String[] values = new String[] {"Stonemasonry"};
				public int getSize() {
					return values.length;
				}
				public Object getElementAt(int index) {
					return values[index];
				}
			});
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
				okButton.addActionListener(new OkActionListener());
				//TODO disable OK button until something is selected
				//okButton.setEnabled(false);
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
