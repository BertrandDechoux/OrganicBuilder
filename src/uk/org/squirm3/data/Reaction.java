package uk.org.squirm3.data;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Iterator;


public class Reaction
{
	private int a_type,b_type,a_state,b_state; // for type: 0=a..5=f,6=x,7=y
	private boolean bonded_before,bonded_after;
	private int future_a_state,future_b_state;
	
	public Reaction(int a_type,int a_state,boolean bonded_before,int b_type,int b_state,int future_a_state,boolean bonded_after,int future_b_state)
	{
		this.a_type = a_type;
		this.a_state = a_state;
		this.bonded_before = bonded_before;
		this.b_type = b_type;
		this.b_state = b_state;
		this.future_a_state = future_a_state;
		this.bonded_after = bonded_after;
		this.future_b_state = future_b_state;
	}
	
	public String getString() {
		String s = new String("error!");
		// need to check that charAt isn't going to break on us
		if(a_type>=0 && a_type<Atom.type_code.length() && b_type>=0 && b_type<Atom.type_code.length()) {
			s = Atom.type_code.charAt(a_type)+String.valueOf(a_state)+(bonded_before?"":" + ")+
				Atom.type_code.charAt(b_type)+String.valueOf(b_state)+" => "+
				Atom.type_code.charAt(a_type)+String.valueOf(future_a_state)+(bonded_after?"":" + ")+
				Atom.type_code.charAt(b_type)+String.valueOf(future_b_state);
		}
		return s;
	}
	
	public String toString() {
		return getString();
	}
	
	
	public static String parse(String text, Vector reactions_found) {
		//System.out.println("Input: "+text); // DEBUG
		
		// for each line in the text			
		StringTokenizer lines = new StringTokenizer(text,"\n",true);
		String s = new String();
		String line = new String();
		while(lines.hasMoreTokens()) 
		{
			line = lines.nextToken();
			line = line.trim(); // remove leading and trailing whitespace and control chars
			if(line.length()==0) continue; // nothing doing
			
			if(line.length()>2 && line.charAt(0)=='/' && line.charAt(1)=='/')
				continue; // this line contains a comment, skip it
			
			//System.out.println("Parsing line: "+line+" (length "+String.valueOf(line.length())+")");
			
			Reaction r = new Reaction(0,0,false,0,0,0,false,0); // the target
			
			boolean error_found=false; // is there an error on this line?
			
			// using abcdefxy characters as the tokens, work through the line
			StringTokenizer reactants = new StringTokenizer(line,Atom.type_code,true);
			int element=0;
			while(reactants.hasMoreTokens()) 
			{
				s = reactants.nextToken();
				
				//System.out.println("Token "+String.valueOf(element)+": "+s+"\n"); // DEBUG
				
				if(s.length()==0) error_found=true;
				
				try // (parseInt may throw an exception)
				{ 
					switch(element++)
					{
					case 0:  // deal with the first character of the first reactant
						r.a_type = Atom.type_code.indexOf(s.charAt(0)); 
						break;
					case 1: // deal with the character up until the next type (state + optional "+")
						{
							int nd = findNDigits(s);
							if(nd>0)
								r.a_state = Integer.parseInt(s.substring(0,nd));
							else 
								error_found=true;
							r.bonded_before = (s.indexOf('+')==-1);
							break;
						}
					case 2:  // deal with the first character of the second reactant
						r.b_type = Atom.type_code.indexOf(s.charAt(0)); break;
					case 3: // deal with the character up until the next type (state + "=>")
						{
							int nd = findNDigits(s);
							if(nd>0)
								r.b_state = Integer.parseInt(s.substring(0,nd));
							else 
								error_found=true;
							break;
						}
					case 4:  // a_type after (should validate on this)
						break;
					case 5: // deal with the character up until the next type (state + optional "+")
						{
							int nd = findNDigits(s);
							if(nd>0)
								r.future_a_state = Integer.parseInt(s.substring(0,nd));
							else 
								error_found=true;
							r.bonded_after = (s.indexOf('+')==-1);
							break;
						}
					case 6:  // b_type after (should validate on this)
						break;
					case 7: // deal with the character up until the next type (state + "=>")
						{
							int nd = findNDigits(s);
							if(nd>0)
								r.future_b_state = Integer.parseInt(s.substring(0,nd));
							else 
								error_found=true;
							break;
						}
					}
				}
				catch(NumberFormatException nfe)
				{
					error_found=true;
				}
				if(error_found)
					return "\nParse error on line:\n\""+line+"\"";
			}
			
			// were sufficient tokens parsed on this line? (more than 8 we ignore)
			if(element<8)
				return "\nParse error on line:\n\""+line+"\"";

			
			//System.out.println(r.getString()+"\n"); // DEBUG
			
			reactions_found.add(r);
		}
		return null;
	}
	
	private static int findNDigits(String s){
		// how many digits 0-9 are at the start of this string?
		int i=0;
		while(i<s.length() && "0123456789".indexOf(s.charAt(i))!=-1)
			i++;
		return i;
	}
	
	public static void tryReaction(Atom a, Atom b, Vector reactions) {
		if(!a.killer && !b.killer)
		{
			for(int twice=0;twice<2 && !a.has_reacted && !b.has_reacted;twice++)
			{
				// try each reaction in turn
				Iterator it = reactions.listIterator();
				while(it.hasNext() && !a.has_reacted && !b.has_reacted)
				{
					Reaction r = (Reaction)it.next();
					// is the type for 'a' specified and correct?
					if(r.a_type<6 && a.type!=r.a_type) continue;
					// is the type for 'b' specified and correct?
					if(r.b_type<6 && b.type!=r.b_type) continue;
					// is the type for 'b' specified as matching that of 'a' and correct?
					if(r.b_type>5 && r.b_type==r.a_type && b.type!=a.type) continue; // both x or both y
					// is the state of 'a' and 'b' correct?
					if(r.a_state!=a.state || r.b_state!=b.state) continue;
					// is the bonded/not status correct?
					if( (r.bonded_before && !a.hasBondWith(b)) || (!r.bonded_before && a.hasBondWith(b)) ) continue;
					
					// ok, we can now apply the reaction		
					if(!r.bonded_before && r.bonded_after) a.bondWith(b);
					else if(r.bonded_before && !r.bonded_after) a.breakBondWith(b);
					a.state=r.future_a_state;
					b.state=r.future_b_state;
					
					a.has_reacted=true; // (only want one reaction per atom per timestep)
					b.has_reacted=true;
				}
				// now swap a and b and try again
				Atom temp=a; a=b; b=temp;
			}
		}
		else {
			// the killer atom breaks the other atoms bonds (unless other is an 'a' atom)
			if(a.killer) { if(b.type!=0) b.breakAllBonds();}
			else { if(a.type!=0) a.breakAllBonds(); }
		}
	}
}
