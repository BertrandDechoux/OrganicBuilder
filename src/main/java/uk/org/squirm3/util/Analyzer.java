package uk.org.squirm3.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Scanner;

/**  
${my.copyright}
 */

public class Analyzer {

    public static void main(String[] args) {
        String name;
        if(args.length > 0) {
            name = args[0];
        } else {
            Scanner in = new Scanner(System.in);
            System.out.println("Enter class name (e.g. java.util.Date): ");
            name = in.next();
        }
        try {
            Class cl = Class.forName(name);
            System.out.println(analyzeClass(cl));
        } catch(ClassNotFoundException e) { e.printStackTrace(); }
        System.exit(0);
    }

    public static String analyzeClass(Class cl) {
        String description = "";
        Class supercl = cl.getSuperclass();
        description += "class " + cl.getName();
        if(supercl != null && supercl != Object.class)
            description += " extends " + supercl.getName();
        description += " {\n" + printConstructors(cl) + "\n";
        description += printMethods(cl) + "\n";
        description += printFields(cl) + "}\n";
        return description;
    }

    public static String analyzeObject(Object obj) {
        ArrayList visited = new ArrayList();
        if(obj == null) return "null";
        if(visited.contains(obj)) return "...";
        visited.add(obj);
        Class cl = obj.getClass();
        if(cl == String.class) return (String)obj;
        if(cl.isArray()) {
            String r = cl.getComponentType() + "[]{";
            for(int i = 0; i < Array.getLength(obj); i++) {
                if(i>0) r += ",";
                Object val = Array.get(obj, i);
                if(cl.getComponentType().isPrimitive()) r += val;
                else r += analyzeObject(val);
            }
            return r+ "}";
        }
        String r = cl.getName();
        do {
            r += "[";
            Field[] fields = cl.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);
            for(int i = 0; i < fields.length ; i++) {
                Field f = fields[i];
                if(!Modifier.isStatic(f.getModifiers())) {
                    if(!r.endsWith("[")) r += ",";
                    r += f.getName() + "=";
                    try {
                        Class t = f.getType();
                        Object val = f.get(obj);
                        if(t.isPrimitive()) r += val;
                        else r += analyzeObject(val);
                    } catch(Exception e) { e.printStackTrace(); }
                }
            }
            r += "]";
            cl = cl.getSuperclass();
        } while(cl!=null);
        return r;
    }

    public static String printConstructors(Class cl) {
        String description = "";
        Constructor[] constructors = cl.getDeclaredConstructors();
        for( int i = 0; i < constructors.length; i++) {
            Constructor c = constructors[i];
            String name = c.getName();
            description += "\t" + Modifier.toString(c.getModifiers());
            description += " " + name;
            description += printParameters(c.getParameterTypes());
        }
        return description;
    }

    public static String printMethods(Class cl) {
        String description = "";
        Method[] methods = cl.getDeclaredMethods();
        for(int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            Class reType = m.getReturnType();
            String name = m.getName();
            description += "\t" + Modifier.toString(m.getModifiers());
            description += " " + reType.getName() + " " + name;
            description += printParameters(m.getParameterTypes());
        }
        return description;
    }

    public static String printParameters(Class[] paramTypes) {
        String description = "(";
        for( int j = 0; j < paramTypes.length ; j++) {
            if( j > 0 ) System.out.print(", ");
            description += paramTypes[j].getName();
        }
        description += ");\n";
        return description;
    }

    public static String printFields(Class cl) {
        String description = "";
        Field[] fields = cl.getDeclaredFields();
        for(int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            Class type = f.getType();
            String name = f.getName();
            description += "\t" + Modifier.toString(f.getModifiers());
            description += " " + type.getName() + " " + name + ";\n";
        }
        return description;
    }
}
