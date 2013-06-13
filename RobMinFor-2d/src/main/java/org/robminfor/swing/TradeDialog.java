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
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import org.robminfor.engine.Landscape;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.EntityManager;
import org.robminfor.engine.entities.Home;
import org.robminfor.engine.entities.IStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradeDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JDialog newdialog;
	private final List<JLabel> counts = new ArrayList<JLabel>(); 
	private final List<JButton> buys = new ArrayList<JButton>(); 
	private final List<JButton> sells = new ArrayList<JButton>(); 

    private Logger log = LoggerFactory.getLogger(getClass());
	private Timer refreshTimer;
	protected SwingWorker<Void, Void> refreshworker;
	
	private static final int WORKERCOST = 100;
	
	
	private List<String> values = EntityManager.getEntityManager().getEntityNames();
	private final Landscape landscape;
	
	private int getCount(String entityName)  {
		//TODO implement this in a generic fashion over all storage sites
		Home home = (Home) landscape.getHomeSite().getEntity();
		return home.getCount(entityName);
	}
	
	private void addEntity(String entityName) { 
		//TODO implement this in a generic fashion over all storage sites
		Home home = (Home) landscape.getHomeSite().getEntity();
		home.addEntity(entityName);
	}
	
	private void removeEntity(String entityName) { 
		//TODO implement this in a generic fashion over all storage sites
		Home home = (Home) landscape.getHomeSite().getEntity();
		home.removeEntity(entityName);
	}
	
	private int getBuyCost(String entityName) {
		return EntityManager.getEntityManager().getEntity(entityName).getBuyValue();
	}
	
	private int getSellValue(String entityName) {
		return EntityManager.getEntityManager().getEntity(entityName).getSellValue();
	}
	
	private void addItem(String name, int count, Integer buyCost, Integer sellValue, int pos) {

		JLabel nameLabel = new JLabel(name);
		
		GridBagConstraints gbc_name = new GridBagConstraints();
		gbc_name.fill = GridBagConstraints.BOTH;
		gbc_name.gridx = 0;
		gbc_name.gridy = pos;
		contentPanel.add(nameLabel, gbc_name);
		
		//TODO implement as images
		JLabel lblCount = new JLabel(""+count);
		GridBagConstraints gbc_count = new GridBagConstraints();
		gbc_count.gridx = 1;
		gbc_count.gridy = pos;
		gbc_count.ipadx = 10; //some padding
		contentPanel.add(lblCount, gbc_count);
		counts.add(lblCount);
		
		if (buyCost != null) {
			JButton btnBuy = new JButton("Buy ($"+buyCost+")");
			GridBagConstraints gbc_buy = new GridBagConstraints();
			gbc_buy.gridx = 2;
			gbc_buy.gridy = pos;
			contentPanel.add(btnBuy, gbc_buy);
			buys.add(btnBuy);
			btnBuy.addActionListener(new BuyListener(name));
		}

		if (sellValue != null) {
			JButton btnSell = new JButton("Sell ($"+sellValue+")");
			GridBagConstraints gbc_sell = new GridBagConstraints();
			gbc_sell.gridx = 3;
			gbc_sell.gridy = pos;
			contentPanel.add(btnSell, gbc_sell);
			sells.add(btnSell);
			btnSell.addActionListener(new SellListener(name));
		}
	}
	
	/**
	 * Create the dialog.
	 * @param parent 
	 */
	public TradeDialog(Frame parent, final Landscape landscape) {
		super(parent, true);
		newdialog = this;
		this.landscape = landscape;
		
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
			for (int i = 0 ; i < values.size(); i++) {
				String name = values.get(i);
				int count = getCount(name);
				Integer buyCost = getBuyCost(name);
				Integer sellValue = getSellValue(name); 
				
				addItem(name, count, buyCost, sellValue, i+1);
			}
		}
		
		{
			String name = "Worker";
			int count = landscape.getAgentCount();
			Integer buyCost = 10000; //manually sync with BuyListener
			
			addItem(name, count, buyCost, null, values.size()+1);
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

		//refresh information
		ActionListener refreshListener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				//only start a new worker if the last one has finished
				if (refreshworker == null 
						|| refreshworker.isDone()) {
					refreshworker = new SwingWorker<Void, Void>() {
						protected Void doInBackground() throws Exception {
							
							//update counts
							for (int i = 0 ; i < values.size(); i++) {
								int count = getCount(values.get(i));
								counts.get(i).setText(""+count);
								//if no counts, can't sell
								if (count == 0) {
									sells.get(i).setEnabled(false);
								} else {
									sells.get(i).setEnabled(true);
								}
							}
							//update count of workers
							counts.get(values.size()).setText(""+landscape.getAgentCount());
							
							
							//update if we can afford to buy
							int cash = landscape.getMoney();
							for (int i = 0 ; i < values.size(); i++) {
								if (getBuyCost(values.get(i)) > cash) {
									buys.get(i).setEnabled(false);
								} else {
									buys.get(i).setEnabled(true);
								}
							}
							//update worker buy button
							if (WORKERCOST > cash) {
								buys.get(values.size()).setEnabled(false);
							} else {
								buys.get(values.size()).setEnabled(true);
							}
							
							return null;
						}	
					};
					refreshworker.execute();
				}
			}
		};
		refreshTimer = new Timer(1, refreshListener);
		refreshTimer.start();
	}
	
	private class BuyListener implements ActionListener {
		private final String type;
		
		public BuyListener(String type) {
			this.type = type;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			if ("Worker".equals(this.type)) {
				Agent agent = new Agent(landscape.getHomeSite());
				landscape.addAgent(agent);
				landscape.changeMoney(-10000); //manually sync with dialog
			} else {
				int value = getBuyCost(type);
				addEntity(type);
				landscape.changeMoney(-value);
			}
		}
	}
	
	private class SellListener implements ActionListener {
		private final String type;
		
		public SellListener(String type) {
			this.type = type;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			int value = getSellValue(type);
			removeEntity(type);
			landscape.changeMoney(value);
		}
	}
}
