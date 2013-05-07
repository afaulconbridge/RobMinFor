package org.robminfor.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.robminfor.engine.Landscape;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.Home;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradeDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JDialog newdialog;
	private final List<JLabel> counts = new ArrayList<JLabel>(); 
	private final List<JButton> buys = new ArrayList<JButton>(); 
	private final List<JButton> sells = new ArrayList<JButton>(); 

    private Logger log = LoggerFactory.getLogger(getClass());
	
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
			JLabel title = new JLabel("Performing trading");
			GridBagConstraints gbc_title = new GridBagConstraints();
			gbc_title.insets = new Insets(0, 0, 5, 0);
			gbc_title.anchor = GridBagConstraints.WEST;
			gbc_title.gridx = 0;
			gbc_title.gridy = 0;
			contentPanel.add(title, gbc_title);
		}
		{
			//add other items here
			//TODO load this from some external file
			String[] values = new String[] {"Stone", "Ore", "Crystal", "Stonemasonry"};
			
			for (int i = 0 ; i < values.length; i++) {
				
				String clsName = "org.robminfor.engine.entities."+values[i];
				Class cls = null;
				try {
					cls = Class.forName(clsName);
				} catch (ClassNotFoundException e) {
					log.error("Unable to find class "+clsName);
					continue;
				}
				
				JLabel name = new JLabel(values[i]);
				GridBagConstraints gbc_name = new GridBagConstraints();
				gbc_name.fill = GridBagConstraints.BOTH;
				gbc_name.gridx = 0;
				gbc_name.gridy = i+1;
				contentPanel.add(name, gbc_name);

				int count = 0;
				//TODO implement this in a generic fashion over all storage sites
				Home home = (Home) landscape.getHomeSite().getEntity();
				for(AbstractEntity thing : home.getContent()){
					if (cls.isInstance(thing)) {
						count += 1;
					}
				}
				
				int buyCost = 200; //TODO implement for real
				int sellValue = 10; //TODO implement for real
				
				JLabel lblCount = new JLabel(""+count);
				GridBagConstraints gbc_count = new GridBagConstraints();
				gbc_count.gridx = 1;
				gbc_count.gridy = i+1;
				gbc_count.ipadx = 10; //some padding
				contentPanel.add(lblCount, gbc_count);
				counts.add(lblCount);
				
				JButton btnBuy = new JButton("Buy ($"+buyCost+")");
				//TODO create action
				//TODO disable when not applicable
				GridBagConstraints gbc_buy = new GridBagConstraints();
				gbc_buy.gridx = 2;
				gbc_buy.gridy = i+1;
				contentPanel.add(btnBuy, gbc_buy);
				buys.add(btnBuy);

				JButton btnSell = new JButton("Sell ($"+sellValue+")");
				//TODO create action
				//TODO disable when not applicable
				GridBagConstraints gbc_sell = new GridBagConstraints();
				gbc_sell.gridx = 3;
				gbc_sell.gridy = i+1;
				contentPanel.add(btnSell, gbc_sell);
				sells.add(btnSell);
			}
		}

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
