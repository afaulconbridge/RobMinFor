package org.robminfor.swing;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JScrollBar;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;

import org.robminfor.engine.Landscape;
import org.robminfor.engine.LandscapeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Calendar;
import javax.swing.JButton;

public class Display {

	private Landscape landscape = null;
	
	private JFrame frame;
	private JPanelLandscape jpanellandscape;
	private JScrollBar depthBar;
	private JLabel landscapetime;
	private JLabel landscapedate;
	private final ButtonGroup buttonGroupSpeed = new ButtonGroup();

	private long lastupdate;
	
	private SwingWorker<Void, Void> updateworker = null; 
	private Timer updatetimer;
	
	private SwingWorker<Void, Void> refreshworker = null; 
	private Timer refreshtimer;
	
    private Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Display window = new Display();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Display() {
		initialize();
	}

	/**
	 * Wrapper around JPanelLandscape to ensure other controls are updated.
	 */
	private void setLandscape(Landscape landscape){
		log.info("Setting new landscape");
		this.landscape = landscape;
		jpanellandscape.setLandscape(landscape);
		depthBar.setMinimum(0);
		depthBar.setMaximum(landscape.getSizeZ()-1);
		depthBar.setValue(0);
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 575, 553);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{71, 8, 0, 0};
		gridBagLayout.rowHeights = new int[]{528, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		

		jpanellandscape = new JPanelLandscape();
		
		JScrollPane scrollPane = new JScrollPane(jpanellandscape);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		frame.getContentPane().add(scrollPane, gbc_scrollPane);
		
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New...");
		mntmNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//TODO prompt for parameters
				LandscapeFactory factory = LandscapeFactory.getInstance();
				landscape = factory.generate(64, 64, 23);
				setLandscape(landscape);
			}
		});
		mnFile.add(mntmNew);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setEnabled(false);
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save As...");
		mntmSaveAs.setEnabled(false);
		mnFile.add(mntmSaveAs);
		
		JMenuItem mntmLoad = new JMenuItem("Load...");
		mntmLoad.setEnabled(false);
		mnFile.add(mntmLoad);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//note you will need to kill any other existing threads here too
				//or wait for them to end
				//TODO prompt to confirm
	            frame.dispose();
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnSpeed = new JMenu("Speed");
		menuBar.add(mnSpeed);
		
		JRadioButtonMenuItem rdbtnmntmPause = new JRadioButtonMenuItem("Pause");
		mntmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updatetimer.stop();
			}
		});
		buttonGroupSpeed.add(rdbtnmntmPause);
		mnSpeed.add(rdbtnmntmPause);
		
		JRadioButtonMenuItem rdbtnmntmX = new JRadioButtonMenuItem("x1");
		rdbtnmntmX.setSelected(true);
		rdbtnmntmX.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updatetimer.setDelay(1000);
				updatetimer.setInitialDelay(1000);
				updatetimer.restart();
			}
		});
		buttonGroupSpeed.add(rdbtnmntmX);
		mnSpeed.add(rdbtnmntmX);
		
		JRadioButtonMenuItem rdbtnmntmX_1 = new JRadioButtonMenuItem("x2");
		rdbtnmntmX_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updatetimer.setDelay(500);
				updatetimer.setInitialDelay(500);
				updatetimer.restart();
			}
		});
		buttonGroupSpeed.add(rdbtnmntmX_1);
		mnSpeed.add(rdbtnmntmX_1);
		
		JRadioButtonMenuItem rdbtnmntmX_2 = new JRadioButtonMenuItem("x10");
		rdbtnmntmX_2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updatetimer.setDelay(100);
				updatetimer.setInitialDelay(100);
				updatetimer.restart();
			}
		});
		buttonGroupSpeed.add(rdbtnmntmX_2);
		mnSpeed.add(rdbtnmntmX_2);
		
		depthBar = new JScrollBar();
		depthBar.setBlockIncrement(1);
		depthBar.setToolTipText("depth");
		depthBar.setMinimum(0);
		depthBar.setMaximum(0);
		depthBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				jpanellandscape.setVisibleZ(e.getValue());
				//TODO consider not setting it if part of an ongoing value change (slide)
			}
		});
		GridBagConstraints gbc_scrollBar = new GridBagConstraints();
		gbc_scrollBar.fill = GridBagConstraints.VERTICAL;
		gbc_scrollBar.insets = new Insets(0, 0, 5, 5);
		gbc_scrollBar.gridx = 1;
		gbc_scrollBar.gridy = 0;
		frame.getContentPane().add(depthBar, gbc_scrollBar);
		
		
		JPanel controlpanel = new JPanel();
		controlpanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GridBagConstraints gbc_controlpanel = new GridBagConstraints();
		gbc_controlpanel.insets = new Insets(0, 0, 5, 0);
		gbc_controlpanel.fill = GridBagConstraints.BOTH;
		gbc_controlpanel.gridx = 2;
		gbc_controlpanel.gridy = 0;
		frame.getContentPane().add(controlpanel, gbc_controlpanel);
		GridBagLayout gbl_controlpanel = new GridBagLayout();
		gbl_controlpanel.columnWidths = new int[]{53, 0};
		gbl_controlpanel.rowHeights = new int[]{15, 15, 16, 0};
		gbl_controlpanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_controlpanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		controlpanel.setLayout(gbl_controlpanel);
		
		landscapetime = new JLabel("00:00");
		GridBagConstraints gbc_time = new GridBagConstraints();
		gbc_time.insets = new Insets(0, 0, 5, 0);
		gbc_time.gridx = 0;
		gbc_time.gridy = 0;
		controlpanel.add(landscapetime, gbc_time);
				
		landscapedate = new JLabel("00 / 00 / 0000");
		GridBagConstraints gbc_date = new GridBagConstraints();
		gbc_date.insets = new Insets(0, 0, 5, 0);
		gbc_date.gridx = 0;
		gbc_date.gridy = 1;
		controlpanel.add(landscapedate, gbc_date);
		
		JButton btnDig = new JButton("Dig");
		btnDig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.info("Dig clicked");
				jpanellandscape.dig();
			}
		});
		GridBagConstraints gbc_btnDig = new GridBagConstraints();
		gbc_btnDig.gridx = 0;
		gbc_btnDig.gridy = 2;
		controlpanel.add(btnDig, gbc_btnDig);
		
		//landscape update timer
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (landscape != null){
					//only start a new worker if the last one has finished
					if (updateworker == null){
						updateworker = new SwingWorker<Void, Void>() {
							protected Void doInBackground() throws Exception {
								//log.info("================updateworker started================");
								try {
									jpanellandscape.update();
									//TODO update other components
									Calendar cal = landscape.getCalendar();
									Integer hour = cal.get(Calendar.HOUR_OF_DAY);
									Integer minute = cal.get(Calendar.MINUTE);
									Integer day = cal.get(Calendar.DAY_OF_MONTH);
									Integer month = cal.get(Calendar.MONTH);
									Integer year = cal.get(Calendar.YEAR);
									landscapetime.setText(""+hour+":"+minute);
									landscapedate.setText(""+day+" / "+month+" / "+year);
									lastupdate = System.currentTimeMillis();
								} catch (Throwable e){
									log.error("Error caught", e);
									System.exit(1);
								}
								//log.info("================updateworker finished================");
								updateworker = null;
								return null;
							}	
						};
						updateworker.execute();
					}
				}
			}
		};
		updatetimer = new Timer(1000, taskPerformer);
		updatetimer.start();
		
		//landscape draw timer
		taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				//only start a new worker if the last one has finished
				if (refreshworker == null 
						|| refreshworker.isDone()){
					refreshworker = new SwingWorker<Void, Void>() {
						protected Void doInBackground() throws Exception {
							long updatetime = System.currentTimeMillis();
							long updateinterval = updatetime - lastupdate;
							float updatefraction = ((float)updateinterval) / ((float)updatetimer.getDelay());
							jpanellandscape.setUpdateFraction(updatefraction);
							jpanellandscape.repaint();
							return null;
						}	
					};
					refreshworker.execute();
				}
			}
		};
		refreshtimer = new Timer(1, taskPerformer);
		refreshtimer.start();
	
	}
}
