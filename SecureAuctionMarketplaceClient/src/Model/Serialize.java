package Model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serialize {
	public static String convertObjectToString(Object obj) {
		String serializedObject = "";
		try {
		     ByteArrayOutputStream bo = new ByteArrayOutputStream();
		     ObjectOutputStream so = new ObjectOutputStream(bo);
		     so.writeObject(obj);
		     so.flush();
		     serializedObject = bo.toString();
		 } catch (Exception e) {
		     System.out.println(e);
		 }
		return serializedObject;
	}
	
	public static Object convertStringToOjbect(String str) {
		Object obj = null;
		try {
		     byte b[] = str.getBytes(); 
		     ByteArrayInputStream bi = new ByteArrayInputStream(b);
		     ObjectInputStream si = new ObjectInputStream(bi);
		     obj = si.readObject();
		 } catch (Exception e) {
		     System.out.println(e);
		 }
		return obj;
	}
}
