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
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import org.robminfor.engine.Landscape;
import org.robminfor.engine.Site;
import org.robminfor.engine.actions.Buy;
import org.robminfor.engine.actions.Sell;
import org.robminfor.engine.agents.Agent;
import org.robminfor.engine.entities.EntityManager;
import org.robminfor.engine.entities.IItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradeDialog extends JDialog {

	private Logger log = LoggerFactory.getLogger(getClass());

	private final JPanel contentPanel = new JPanel();
	private final JDialog newdialog;
	private final List<JLabel> counts = new ArrayList<JLabel>();
	private final List<JButton> buys = new ArrayList<JButton>();
	private final List<JButton> sells = new ArrayList<JButton>();

	private Timer timer;

	private static final int WORKERCOST = 100;

	private final Landscape landscape;

	/**
	 * Create the dialog.
	 * 
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
		gbl_contentPanel.columnWidths = new int[] { 151, 0 };
		gbl_contentPanel.rowHeights = new int[] { 15, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
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
			// add other items here
			for (int i = 0; i < EntityManager.getEntityManager().getItemNames()
					.size(); i++) {
				String name = EntityManager.getEntityManager().getItemNames()
						.get(i);
				int count = getCount(name);
				Integer buyCost = getBuyCost(name);
				Integer sellValue = getSellValue(name);

				addItem(name, count, buyCost, sellValue, i + 1);
			}
		}

		{
			String name = "Worker";
			int count = landscape.getAgentCount();
			Integer buyCost = WORKERCOST;

			addItem(name, count, buyCost, null, EntityManager
					.getEntityManager().getItemNames().size() + 1);
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

		// refresh information
		timer = new Timer(0, new RefreshListener());
		timer.setRepeats(true);
		timer.start();

	}

	private int getCount(String name) {
		IItem item = EntityManager.getEntityManager().getItem(name);
		return getCount(item);
	}

	private int getCount(IItem item) {
		int count = 0;
		for (Site s : landscape.getStorageSites()) {
			for (IItem thing : s.getItems()) {
				if (thing.equals(item)) {
					count += 1;
				}
			}
		}
		return count;
	}

	private Integer getSellValue(String name) {
		IItem item = EntityManager.getEntityManager().getItem(name);
		return getSellValue(item);
	}

	private Integer getSellValue(IItem item) {
		return item.getSellValue();
	}

	private Integer getBuyCost(String name) {
		IItem item = EntityManager.getEntityManager().getItem(name);
		return getBuyCost(item);
	}

	private Integer getBuyCost(IItem item) {
		return item.getBuyValue();
	}

	private void addItem(String name, int count, Integer buyCost,
			Integer sellValue, int pos) {

		JLabel nameLabel = new JLabel(name);

		GridBagConstraints gbc_name = new GridBagConstraints();
		gbc_name.fill = GridBagConstraints.BOTH;
		gbc_name.gridx = 0;
		gbc_name.gridy = pos;
		contentPanel.add(nameLabel, gbc_name);

		// TODO implement as images
		JLabel lblCount = new JLabel("" + count);
		GridBagConstraints gbc_count = new GridBagConstraints();
		gbc_count.gridx = 1;
		gbc_count.gridy = pos;
		gbc_count.ipadx = 10; // some padding
		contentPanel.add(lblCount, gbc_count);
		counts.add(lblCount);

		if (buyCost != null && buyCost > 0) {
			JButton btnBuy = new JButton("Buy ($" + buyCost + ")");
			GridBagConstraints gbc_buy = new GridBagConstraints();
			gbc_buy.gridx = 2;
			gbc_buy.gridy = pos;
			contentPanel.add(btnBuy, gbc_buy);
			buys.add(btnBuy);
			btnBuy.addActionListener(new BuyListener(name));
		} else {
			buys.add(null);
		}

		if (sellValue != null && sellValue > 0) {
			JButton btnSell = new JButton("Sell ($" + sellValue + ")");
			GridBagConstraints gbc_sell = new GridBagConstraints();
			gbc_sell.gridx = 3;
			gbc_sell.gridy = pos;
			contentPanel.add(btnSell, gbc_sell);
			sells.add(btnSell);
			btnSell.addActionListener(new SellListener(name));
		} else {
			sells.add(null);
		}
	}

	private class RefreshListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent evt) {

			//log.info("TradeDialog refresh");
			// update counts
			int imax = EntityManager.getEntityManager().getItemNames().size();
			int cash = landscape.getMoney();
			for (int i = 0; i < imax; i++) {
				//log.info("i = " + i);
				//log.info("imax = " + imax);
				//log.info("sells.size() = " + sells.size());
				IItem thing = EntityManager.getEntityManager().getItem(
						EntityManager.getEntityManager().getItemNames().get(i));
				int count = getCount(thing);
				counts.get(i).setText("" + count);
				// if no counts, can't sell
				if (sells.get(i) != null) {
					if (count <= 0) {
						sells.get(i).setEnabled(false);
					} else {
						sells.get(i).setEnabled(true);
					}
				}
				// update if we can afford to buy
				if (buys.get(i) != null) {
					if (getBuyCost(thing) > cash) {
						buys.get(i).setEnabled(false);
					} else {
						buys.get(i).setEnabled(true);
					}
				}
				//log.info(EntityManager.getEntityManager().getItemNames().get(i) + " = " + count);
			}

			// update count of workers
			counts.get(imax).setText("" + landscape.getAgentCount());
			// update worker buy button
			if (WORKERCOST > cash) {
				buys.get(imax).setEnabled(false);
			} else {
				buys.get(imax).setEnabled(true);
			}
		}
	}

	private class BuyListener implements ActionListener {
		private final String type;

		public BuyListener(String type) {
			this.type = type;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			if ("Worker".equals(this.type)) {
				throw new RuntimeException("Not implemented");
			} else {
				int value = getBuyCost(type);
				if (landscape.changeMoney(-value)) {
					IItem item = EntityManager.getEntityManager().getItem(type);
					landscape.addAction(new Buy(item));
				}
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
			throw new RuntimeException("Not implemented");
		}
	}
}
