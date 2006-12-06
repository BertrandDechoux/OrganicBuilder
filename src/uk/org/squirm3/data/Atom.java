package uk.org.squirm3.data;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;
import java.util.Vector;

import uk.org.squirm3.ui.Resource;


public class Atom 
{
	static private final float R = 22.0f;
	//TODO should not be hardcoded, properties file ?
	
	//TODO remove "public" !!!
	public Point2D.Float pos,velocity,acceleration; // acceleration only used in new correct physics code
	public int type,state; // type: 0=a,..5=f

	public Vector bonds;
	
	public boolean stuck=false; // special marker for atoms that don't move
	public boolean killer=false; // special marker for atoms that have a special caustic effect
	
	public boolean has_reacted=false; // has this atom been part of a reaction this timestep?
	
	static final public String type_code = "abcdefxy";
	
	public Atom(float x,float y,int t,int s,float ms)
	{
		this.pos = new Point2D.Float(x,y);
		this.velocity = new Point2D.Float((float)(Math.random()*ms-ms/2.0),(float)(Math.random()*ms-ms/2.0));
		this.acceleration = new Point2D.Float(0,0);
		this.type = t;
		this.state = s;
		this.bonds = new Vector();
	}
	
	public void bondWith(Atom other)
	{
		// could check for existing bond if you were worried
		//if(this.bonds.contains(other) || other.bonds.contains(this)) {}

		this.bonds.add(other);
		other.bonds.add(this);
	}
	
	public boolean hasBondWith(Atom other)
	{
		for(int i=0;i<bonds.size();i++)
		{
			if(((Atom)bonds.elementAt(i))==other)
				return true;
		}
		return false;
	}
	
	public void getAllConnectedAtoms(Vector list)
	{
		// is this a new atom for this list?
		if(!list.contains(this))
		{
			list.add(this);
			// recurse
			for(int i=0;i<bonds.size();i++)
				((Atom)bonds.elementAt(i)).getAllConnectedAtoms(list);
		}
	}
	
	public void breakBondWith(Atom other)
	{
		for(int i=0;i<bonds.size();i++)
		{
			if(((Atom)bonds.elementAt(i))==other)
				bonds.removeElementAt(i);
		}
		for(int i=0;i<other.bonds.size();i++)
		{
			if(((Atom)other.bonds.elementAt(i))==this)
				other.bonds.removeElementAt(i);
		}
	}

	public void breakAllBonds()
	{
		while(!bonds.isEmpty())
		{
			breakBondWith((Atom)bonds.firstElement());
		}
	}
	
	public String toString() {
		return type_code.charAt(type) + String.valueOf(state);
	}
	
	public Image getImage() {
		return Resource.getAtomImageOfType((byte)type);		// !!! cast to be removed
	}
	
	public static void draw(Atom[] atoms, Graphics2D g2, int R, ImageObserver ob){
		if(atoms==null) return;
		int offset_x= R;
		int offset_y= R;
		int text_offset_y= (int)(R*8.0/22.0);

		for (int i = 0; i < atoms.length; i++){
			if(!atoms[i].killer) {
				// draw the normal colour atom image and label it
				g2.drawImage(atoms[i].getImage(), (int)atoms[i].pos.x-offset_x, (int)atoms[i].pos.y-offset_y, R*2, R*2, ob);
				String label = atoms[i].toString();
				int width = g2.getFontMetrics().stringWidth(label);
				g2.drawString(label, (int)atoms[i].pos.x-width/2, (int)atoms[i].pos.y+text_offset_y); 
			} else {
				// draw a special spiky image and no label
				g2.drawImage(Resource.getSpikyImage(),(int)atoms[i].pos.x-offset_x, 
					(int)atoms[i].pos.y-offset_y, R*2, R*2, ob);
			}
		}
		
	}
	
	public static void drawBonds(Atom[] atoms, Graphics2D g2, int R, ImageObserver ob) {
		if(atoms==null) return;
		for (int i = 0; i < atoms.length; i++){
			for(int j=0;j<atoms[i].bonds.size();j++) {
				float x1 = atoms[i].pos.x;
				float y1 = atoms[i].pos.y;
				float dx = ((Atom)(atoms[i].bonds.elementAt(j))).pos.x - x1;
				float dy = ((Atom)(atoms[i].bonds.elementAt(j))).pos.y - y1;
				float d = (float)Math.sqrt(dx*dx+dy*dy);
				float x_cut = dx*R*0.8f/d;
				float y_cut = dy*R*0.8f/d;
				g2.drawLine((int)(x1+x_cut),(int)(y1+y_cut),(int)(x1+dx-x_cut),(int)(y1+dy-y_cut));
			}
		}
	}
	
	public static float getAtomSize(){
		return R;
	}
	
} // class sq3Atom
