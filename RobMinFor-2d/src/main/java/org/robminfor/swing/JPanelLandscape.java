package org.robminfor.swing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.Scrollable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.robminfor.engine.Landscape;
import org.robminfor.engine.Site;
import org.robminfor.engine.actions.Dig;
import org.robminfor.engine.entities.AbstractEntity;
import org.robminfor.engine.entities.Air;
import org.robminfor.util.Vect;

public class JPanelLandscape extends JComponent implements Scrollable, MouseListener, MouseMotionListener {

	public static final int TILESIZE = 32;
	private static final String UNKNOWN = "unknown"; //image name for unknown sites
	
	private static final long serialVersionUID = 8391342856037568970L;

	public Landscape landscape = null;
	
	private int visiblez = 0;
	private List<Vect> oldagentposs = new ArrayList<Vect>();
	
	private Integer highlightedx = null;
	private Integer highlightedy = null;
	private final Stroke highlightstroke = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
	private final Color highlightedcolor = Color.cyan;

	private final List<Site> selected = new ArrayList<Site>();
	private Integer selectedstartx = null;
	private Integer selectedstarty = null;
	private Integer selectedcurrentx = null;
	private Integer selectedcurrenty = null;
	private final Stroke selectedstroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
	private final Color selectedcolor = Color.blue;

	private Image darktile; 
	
    private Logger log = LoggerFactory.getLogger(getClass());

	private float updatefraction;

	public JPanelLandscape() {
		super();
		setPreferredSize(new Dimension(32*32, 32*32));
		revalidate();
        addMouseListener(this);
        addMouseMotionListener(this);
	}
	
	public JPanelLandscape(Landscape landscape) {
		super();
		setLandscape(landscape);
        addMouseListener(this);
        addMouseMotionListener(this);
	}

	private Image getDarkTile() {
		if (darktile == null){
			darktile = createImage(TILESIZE, TILESIZE);
			Graphics gtile = darktile.getGraphics();
			gtile.setColor(Color.BLACK);
			gtile.fillRect(0, 0, TILESIZE, TILESIZE);
			gtile.dispose();
		}
		return darktile;
		
	}
	
	public void setLandscape(Landscape landscape) {
		log.info("Setting new landscape");
		this.landscape = landscape;
		
		setPreferredSize(new Dimension(landscape.getSizeX()*32, landscape.getSizeY()*32));
		
		
		revalidate();
		repaint();
		
		//store old agent positions AFTER we have drawn them
		oldagentposs = new ArrayList<Vect>(landscape.getAgentCount());
		for (int i = 0; i < landscape.getAgentCount(); i++) {
			oldagentposs.add(landscape.getAgents(i).getPosition());	
		}
	}
	
	public List<Site> getSelected() {
		return Collections.unmodifiableList(selected);
	}
	
	@Override
    protected void paintComponent(Graphics gg) {
        super.paintComponent(gg);

		//this cast *should* always work...
		Graphics2D g = (Graphics2D) gg;
        
        if (landscape == null) {
        	return;
        }
                
        Rectangle visible = getVisibleRect();
        //precalc what tiles we can see
        int visibletileleft = visible.x/TILESIZE;
        int visibletileright = visibletileleft+(visible.width/TILESIZE)+2;
        visibletileright = Math.min(landscape.getSizeX(), visibletileright);
        int visibletiletop = visible.y/TILESIZE;
        int visibletilebottom = visibletiletop+(visible.height/TILESIZE)+2;
        visibletilebottom = Math.min(landscape.getSizeY(), visibletilebottom);
        
        for (int y = visibletiletop; y < visibletilebottom; y++) {
        	int pixely = y*TILESIZE;
	        for (int x = visibletileleft; x < visibletileright; x++) {
	        	int pixelx = x*TILESIZE;
	        	Rectangle tilerect = new Rectangle(pixelx, pixely, TILESIZE, TILESIZE);
	        	if (visible.intersects(tilerect)) {
	        		Site site = landscape.getSite(x, y, getVisibleZ());
	        		AbstractEntity entity = site.getEntity();
	        		if (entity == Air.getInstance()) {
	        			//air is see-through, so draw the tile underneath
	        			int depth = 1;
        				AbstractEntity deepentity  = null;
        				Site deepsite = null;
	        			while (depth < 4) {
	        				deepsite = landscape.getSite(x, y, getVisibleZ()+depth);
	        				deepentity = deepsite.getEntity();
	        				if (deepentity == Air.getInstance()) {
		        				depth += 1;
	        				} else {
	        					break;
	        				}
	        			}
	        			if (depth >= 4) {
	        				//nothing found in time, draw darkness
		        			g.setColor(Color.BLACK);
		        			g.drawRect(pixelx, pixely, TILESIZE, TILESIZE);
		        			g.fillRect(pixelx, pixely, TILESIZE, TILESIZE);
	        			} else {
	        				//found something
	        				//shade it and draw it
			        		Image tileimage = null;
			        		float darkness = 0.25f * depth;
			        		String imageName = UNKNOWN;
			        		if (deepsite.isVisible()) {
			        			imageName = deepentity.getName();
			        		}
			        		try {
			        			tileimage = ImageLoader.getImage(imageName);
			        		} catch (IOException e) {
			        			log.error("Problem loading "+imageName);
			        			throw new RuntimeException(e);
			        		}
			        		if (tileimage != null) {
			        			  
			        			g.drawImage(tileimage, pixelx, pixely, null);		        			
			        			
			        			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, darkness));
			        			g.drawImage(getDarkTile(), pixelx, pixely, null);
			        			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			        			//may be faster if pre-darkened images are cached and blitted without composite
			        		}
	        			}
	        		} else {
		        		Image tileimage = null;
		        		String imageName = UNKNOWN;
		        		if (site.isVisible()) {
		        			imageName = entity.getName();
		        		}
		                try {
		                	tileimage = ImageLoader.getImage(imageName);
		        		} catch (IOException e) {
		        			log.error("Problem loading "+imageName);
		        			throw new RuntimeException(e);
		        		}
		        		if (tileimage != null) {
		        			g.drawImage(tileimage, pixelx, pixely, null);
		        		}
	        		}
		        }
	        } 
        }
        
        for (int i = 0; i < landscape.getAgentCount(); i++){
        	Vect agentposition = landscape.getAgents(i).getPosition();
        	if (agentposition.getZ() == getVisibleZ() 
        			&& agentposition.getX() >= visibletileleft-1 && agentposition.getX() <= visibletileright+1
        			&& agentposition.getY() >= visibletiletop-1 && agentposition.getY() <= visibletilebottom+1){

        		Image tileimage = null;
                try {
                	tileimage = ImageLoader.getImage("Worker");
        		} catch (IOException e) {
        			log.error("Problem loading Worker");
        			throw new RuntimeException(e);
        		}
        		if (tileimage != null){
        			
        			int newpixelx = agentposition.getX()*TILESIZE;
        			int newpixely = agentposition.getY()*TILESIZE;
        			int oldpixelx = oldagentposs.get(i).getX()*TILESIZE;
        			int oldpixely = oldagentposs.get(i).getY()*TILESIZE;
        			
        			int pixelx = (int)(((1.0-updatefraction)*oldpixelx)+((updatefraction)*newpixelx));
        			int pixely = (int)(((1.0-updatefraction)*oldpixely)+((updatefraction)*newpixely));
        			
        			g.drawImage(tileimage, pixelx, pixely, null);
        			//something about this is off - it flickers and twitches a bit
        			//good enough for the moment though
        		}
        	}
        }
        //draw selected sites
    	g.setStroke(selectedstroke);
    	g.setColor(selectedcolor);
        for (Site site : selected){
        	if (site.getZ() == getVisibleZ()
        			&& site.getX() >= visibletileleft-1 && site.getX() <= visibletileright+1
        			&& site.getY() >= visibletiletop-1 && site.getY() <= visibletilebottom+1) {
		        g.drawRect(site.getX()*TILESIZE, site.getY()*TILESIZE, TILESIZE, TILESIZE);
        	}
        }
        if (selectedstartx != null && selectedstarty != null && selectedcurrentx != null && selectedcurrenty != null) {
			for (int x = Math.min(selectedstartx, selectedcurrentx); x <= Math.max(selectedstartx, selectedcurrentx); x++) {
				for (int y = Math.min(selectedstarty, selectedcurrenty); y <= Math.max(selectedstarty, selectedcurrenty); y++) {
					Site site = landscape.getSite(x,y,getVisibleZ());
		        	if (site.getZ() == getVisibleZ()
		        			&& site.getX() >= visibletileleft-1 && site.getX() <= visibletileright+1
		        			&& site.getY() >= visibletiletop-1 && site.getY() <= visibletilebottom+1){
				        g.drawRect(site.getX()*TILESIZE, site.getY()*TILESIZE, TILESIZE, TILESIZE);
		        	}
				}	
			}
        }
        
        
        //draw highlighted sites
        if (highlightedx != null && highlightedy != null){
        	g.setStroke(highlightstroke);
        	g.setColor(highlightedcolor);
	        g.drawRect(highlightedx*TILESIZE, highlightedy*TILESIZE, TILESIZE, TILESIZE);
        }
        
        //TODO draw action pending sites
    }  


	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return 1;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		return 1;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public int getVisibleZ() {
		return visiblez;
	}

	public void setVisibleZ(int visiblez) {
		if (visiblez < 0){
			throw new IndexOutOfBoundsException("visiblez must be greater than zero");
		} else if (visiblez >= landscape.getSizeZ()){
			throw new IndexOutOfBoundsException("visiblez must be less than landscapez");
		}
		
		if (visiblez != this.visiblez){
			this.visiblez = visiblez;
			repaint();
		}
	}
	
	public void update(){
		oldagentposs = new ArrayList<Vect>(landscape.getAgentCount());
		for (int i = 0; i < landscape.getAgentCount(); i++){
			oldagentposs.add(landscape.getAgents(i).getPosition());	
		}
		landscape.update();
	}

	public void setUpdateFraction(float updatefraction) {
		if (updatefraction >= 1.0){
			updatefraction = 1.0f;
		} else if (updatefraction <= 0.0){
			updatefraction = 0.0f;
		}
		this.updatefraction = updatefraction;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (landscape != null){
			highlightedx = e.getX() / TILESIZE;
			highlightedy = e.getY() / TILESIZE;
			if (selectedstartx != null && selectedstarty != null){
				selectedcurrentx = e.getX() / TILESIZE;
				selectedcurrenty = e.getY() / TILESIZE;	
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		//track highlighted square
		if (landscape != null){
			highlightedx = e.getX() / TILESIZE;
			highlightedy = e.getY() / TILESIZE;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		highlightedx = null;
		highlightedy = null;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (landscape != null){
			if (e.getButton() == MouseEvent.BUTTON1){
				//if not shift-selecting, then clear old selection
				if (!e.isShiftDown()){
					selected.clear();
				}
				selectedstartx = e.getX() / TILESIZE;
				selectedstarty = e.getY() / TILESIZE;
				selectedcurrentx = selectedstartx;
				selectedcurrenty = selectedstarty;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (landscape != null){
			if (e.getButton() == MouseEvent.BUTTON1){
				for (int x = Math.min(selectedstartx, selectedcurrentx); x <= Math.max(selectedstartx, selectedcurrentx); x++){
					for (int y = Math.min(selectedstarty, selectedcurrenty); y <= Math.max(selectedstarty, selectedcurrenty); y++){
						Site site = landscape.getSite(x,y,getVisibleZ());
						if(site != null && !selected.contains(site)){
							selected.add(site);
						}
					}	
				}
				selectedstartx = null;
				selectedstarty = null;
				selectedcurrentx = null;
				selectedcurrenty = null;
			}
		}
	}

	public void dig() {
		log.info("Dig assigned");
		for (Site site : selected){
			landscape.addAction(new Dig(site));
		}
		selected.clear();
	}
}