package uk.org.squirm3.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Reaction {
    private static final String types = "abcdefxy";
    private static final Pattern pattern = Pattern.compile("([" + types
            + "])(\\d{1,})" + "(\\s*\\+\\s*|\\s*)" + "([" + types
            + "])(\\d{1,})" + "\\s*[=\\-]>\\s*" + "\\1(\\d{1,})"
            + "(\\s*\\+\\s*|\\s*)" + "\\4(\\d{1,})$");
    private final int a_type, b_type, a_state, b_state; // for type:
                                                        // 0=a..5=f,6=x,7=y
    private final boolean bonded_before, bonded_after;
    private final int future_a_state, future_b_state;

    public Reaction(final int a_type, final int a_state,
            final boolean bonded_before, final int b_type, final int b_state,
            final int future_a_state, final boolean bonded_after,
            final int future_b_state) {
        this.a_type = a_type;
        this.a_state = a_state;
        this.bonded_before = bonded_before;
        this.b_type = b_type;
        this.b_state = b_state;
        this.future_a_state = future_a_state;
        this.bonded_after = bonded_after;
        this.future_b_state = future_b_state;
    }

    @Override
    public String toString() {
        // TODO find a more secure method than type_code string
        String s = new String("error!");
        // need to check that charAt isn't going to break on us
        if (a_type >= 0 && a_type < Atom.type_code.length() && b_type >= 0
                && b_type < Atom.type_code.length()) {
            s = Atom.type_code.charAt(a_type) + String.valueOf(a_state)
                    + (bonded_before ? "" : " + ")
                    + Atom.type_code.charAt(b_type) + String.valueOf(b_state)
                    + " => " + Atom.type_code.charAt(a_type)
                    + String.valueOf(future_a_state)
                    + (bonded_after ? "" : " + ")
                    + Atom.type_code.charAt(b_type)
                    + String.valueOf(future_b_state);
        }
        return s;
    }

    public static Reaction parse(final String description) {
        final Matcher m = pattern.matcher(description);
        final boolean b = m.matches();
        if (!b) {
            return null;
        }
        final int a_type = types.indexOf(m.group(1).charAt(0));
        final int a_state = Integer.parseInt(m.group(2));
        final boolean bonded_before = !m.group(3).contains("+");
        final int b_type = types.indexOf(m.group(4).charAt(0));
        final int b_state = Integer.parseInt(m.group(5));
        final int future_a_state = Integer.parseInt(m.group(6));
        final boolean bonded_after = !m.group(7).contains("+");
        final int future_b_state = Integer.parseInt(m.group(8));
        final Reaction r = new Reaction(a_type, a_state, bonded_before, b_type,
                b_state, future_a_state, bonded_after, future_b_state);
        return r;
    }

    public boolean tryOn(final Atom a, final Atom b) { // TODO remove test of
                                                       // 'specified'
        // is the type for 'a' specified and correct?
        if (a_type < 6 && a.getType() != a_type) {
            return false;
        }
        // is the type for 'b' specified and correct?
        if (b_type < 6 && b.getType() != b_type) {
            return false;
        }
        // is the type for 'b' specified as matching that of 'a' and correct?
        if (b_type > 5 && b_type == a_type && b.getType() != a.getType()) {
            return false; // both x or both y
        }
        // is the state of 'a' and 'b' correct?
        if (a_state != a.getState() || b_state != b.getState()) {
            return false;
        }
        // is the bonded/not status correct?
        if (bonded_before && !a.hasBondWith(b) || !bonded_before
                && a.hasBondWith(b)) {
            return false;
        }
        // ok, we can now apply the reaction
        if (!bonded_before && bonded_after) {
            a.bondWith(b);
        } else if (bonded_before && !bonded_after) {
            a.breakBondWith(b);
        }
        a.setState(future_a_state);
        b.setState(future_b_state);
        return true;
    }
}
