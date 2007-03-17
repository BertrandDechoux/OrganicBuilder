
package uk.org.squirm3.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.org.squirm3.Application;
import uk.org.squirm3.data.Atom;
import uk.org.squirm3.data.DraggingPointData;
import uk.org.squirm3.engine.EngineListenerAdapter;
import uk.org.squirm3.engine.IApplicationEngine;

public class AtomsListener extends EngineListenerAdapter {
	private IApplicationEngine iApplicationEngine;
	
	private DraggingPointData draggingPointData;
	private Atom[] latestAtomsCopy;
	
	private int simulationWidth;
	private int simulationHeight;
	
	private BufferedImage bimg;
	private boolean needRepaint = true;
	private byte scale;
	
	private JCheckBox auto;
	private JSlider scaleSlider;
	
	private JPanel imagePanel;
	private JPanel controlsPanel;
	
	private final JComponent collisionsPanel;
	private JScrollPane scrollPane;
	

	public AtomsListener(IApplicationEngine iApplicationEngine) {
		needRepaint = true;
		scale = 100;
		collisionsPanel = createCollisionsPanel();
		setApplicationEngine(iApplicationEngine);
	}
	
	public JComponent getCollisionsPanel() {
		return collisionsPanel;
	}
	
	private JComponent createCollisionsPanel() {
		imagePanel = new ImagePanel();
		scrollPane = new JScrollPane(imagePanel);
		scrollPane.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent arg0) {
				imageSizeHasChanged();
			}
			public void componentHidden(ComponentEvent arg0) {}
			public void componentMoved(ComponentEvent arg0) {}
			public void componentShown(ComponentEvent arg0) {}
		});
		
		controlsPanel = new JPanel();
		controlsPanel.add(new JLabel(Application.localize(new String[] {"interface","scale"})));
		auto = new JCheckBox(Application.localize(new String[] {"interface","scale","auto"}));
		auto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				scaleSlider.setEnabled(!auto.isSelected());
			    imageSizeHasChanged();
			}
		});
		auto.setSelected(true);
		controlsPanel.add(auto);
		scaleSlider = new JSlider(30, 100, scale);
		scaleSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
			    if (!scaleSlider.getValueIsAdjusting()) {
			        scale = (byte) scaleSlider.getValue();
			        imageSizeHasChanged();
			    }
			}
		});
		scaleSlider.setToolTipText(Application.localize(new String[] {"interface","scale","manual"}));
		scaleSlider.setEnabled(false);
		controlsPanel.add(scaleSlider);

		return scrollPane;
	}
	
	public JPanel getControlsPanel() {
		return controlsPanel;
	}

	public void setApplicationEngine(IApplicationEngine iApplicationEngine) {
		this.iApplicationEngine = iApplicationEngine;
		simulationSizeHasChanged();
		atomsHaveChanged();
		draggingPointHasChanged();
	}
	
	public void atomsHaveChanged() {
		Collection c = iApplicationEngine.getAtoms();
		Iterator it = c.iterator();
		latestAtomsCopy = new Atom[c.size()];
		int i = 0;
		while(it.hasNext()) {
			latestAtomsCopy[i] = (Atom)it.next();
			i++;
		}
		imageHasChanged();
	}

	public void draggingPointHasChanged() {
		draggingPointData = iApplicationEngine.getCurrentDraggingPoint();
		imageHasChanged();
	}

	public void simulationSizeHasChanged() {
		simulationHeight = iApplicationEngine.getSimulationHeight();
		simulationWidth = iApplicationEngine.getSimulationWidth();
		imageSizeHasChanged();
	}
	
	private void imageSizeHasChanged() {
		if(auto!=null) {
			if(auto.isSelected()) {
				if(imagePanel!=null) {
					Dimension d = scrollPane.getSize();
					int pseudoScale = (int) (Math.min((double)((double)d.height*0.99/(double)simulationHeight),(double)((double)d.width*0.99/(double)simulationWidth))*100);
					scale = (pseudoScale>=100)? 100 : (byte)pseudoScale;
				} 
			} else {
				scale = (byte) scaleSlider.getValue();
			}
		}
		float zoom = ((float)scale)/100;
		imagePanel.setPreferredSize(new Dimension((int)(simulationWidth*zoom), (int)(simulationHeight*zoom)));
		SwingUtilities.updateComponentTreeUI(scrollPane);
		imageHasChanged();
	}
	
	private void imageHasChanged() {
		needRepaint = true;
		if(imagePanel== null || !imagePanel.isDisplayable()) return;
		imagePanel.repaint();
	}
	
	private void updateImage() {
		if(!needRepaint) return;
		needRepaint = false;
		int R = (int)Atom.getAtomSize();
		 //to avoid the array to be changed (multithreading issue maybe later)
		Atom[] atoms = latestAtomsCopy;
		
		// get the dimensions
		int w = simulationWidth;
		int h = simulationHeight;
		
		// do we have a correct bimg ?
		if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h)
			bimg = (BufferedImage) collisionsPanel.createImage(w,h);
		if(bimg==null) return;// collisionsPanel is not displayable
		// create graphics	
		Graphics2D g2 = bimg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, w, h);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);
			
		// draw the atoms themselves
		g2.setStroke(new BasicStroke(4));
		g2.setFont(new Font("Arial", Font.BOLD, R));
		g2.setPaint(Color.black);
		Atom.draw(atoms, g2, R, collisionsPanel);
		
		// draw the bonds 
		g2.setPaint(new Color(0,0,0,50));
		Atom.drawBonds(atoms, g2, R, collisionsPanel);
		
		// draw the dragging line if currently dragging
		if(draggingPointData!=null) {
			g2.setStroke(new BasicStroke(5));
			g2.setPaint(new Color(0,0,0,100));
			g2.drawLine((int)draggingPointData.getX(),(int)draggingPointData.getY(),(int)(atoms[draggingPointData.getWhichBeingDragging()].pos.x),
				(int)(atoms[draggingPointData.getWhichBeingDragging()].pos.y));
			g2.setStroke(new BasicStroke(4)); // else the stroke would have been changed
				// when outlining the collider area
		}
		
		// draw the dragging point used
		if(iApplicationEngine.getLastUsedDraggingPoint()!=null) {
			DraggingPointData lastUsedDraggingPoint = iApplicationEngine.getLastUsedDraggingPoint();
			g2.setStroke(new BasicStroke(1));
			g2.setPaint(new Color(200,0,0,100));
			g2.drawLine((int)lastUsedDraggingPoint.getX(),(int)lastUsedDraggingPoint.getY(),(int)(atoms[lastUsedDraggingPoint.getWhichBeingDragging()].pos.x),
				(int)(atoms[lastUsedDraggingPoint.getWhichBeingDragging()].pos.y));
			g2.setStroke(new BasicStroke(4)); // else the stroke would have been changed
				// when outlining the collider area
		}
		
		// outline the collider area
		g2.setPaint(new Color(100,100,200));
		g2.drawRoundRect(2,1,simulationWidth-4,simulationHeight-4,9,9);
		g2.dispose();
	}

	class ImagePanel extends JPanel implements Scrollable{

		public ImagePanel() {
			addMouseListener(
					new MouseListener() {
						public void mouseClicked(MouseEvent event) {}
						public void mouseEntered(MouseEvent event) {}
						public void mouseExited(MouseEvent event) {}
						public void mousePressed(MouseEvent event) {
							// who did we click on?
							Point p = event.getPoint();
							int R = (int) Atom.getAtomSize();
							float zoom = ((float)scale)/100;
							Point2D.Float p2 = new Point2D.Float(p.x/zoom,p.y/zoom);
							for(int i=0;i<latestAtomsCopy.length;i++){
								if(p2.distanceSq(latestAtomsCopy[i].pos)<R*R) {
									iApplicationEngine.setDraggingPoint(
											new DraggingPointData((long)p2.x,
													(long)p2.y, i));
									break;
								}
							}
						}
						public void mouseReleased(MouseEvent event) {
							iApplicationEngine.setDraggingPoint(null);
						}
					});
			addMouseMotionListener(
					new MouseMotionListener() {
						public void mouseDragged(MouseEvent event) {
							if(draggingPointData!=null) {
								float zoom = ((float)scale)/100;
								iApplicationEngine.setDraggingPoint(
										new DraggingPointData((long)(event.getPoint().x/zoom),
												(long)(event.getPoint().y/zoom), 
												draggingPointData.getWhichBeingDragging()));
							}
						}
						public void mouseMoved(MouseEvent event) {
							if(draggingPointData!=null) {
								float zoom = ((float)scale)/100;
								iApplicationEngine.setDraggingPoint(
										new DraggingPointData((long)(event.getPoint().x/zoom),
												(long)(event.getPoint().y/zoom), 
												draggingPointData.getWhichBeingDragging()));
							}
						}
					});
		}
		
		public void paint(Graphics g) {
			super.paint(g);
			updateImage();
			float zoom = ((float)scale)/100;
			g.drawImage(bimg, 0, 0, (int)(bimg.getWidth()*zoom), (int)(bimg.getHeight()*zoom), this);
		}

		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
			return 1;	//TODO
		}

		public boolean getScrollableTracksViewportHeight() {
			return false;
		}

		public boolean getScrollableTracksViewportWidth() {
			return false;
		}

		public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
			return 1; //TODO
		}
	
	}

}

