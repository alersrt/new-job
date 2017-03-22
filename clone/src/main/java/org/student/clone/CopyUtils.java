package org.student.clone;

import java.io.*;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * It is util class which provide functions of object
 * copying. It works with serializable objects
 */
public class CopyUtils {
	/**
	 * Return copies of object.
	 * @param obj the copied object
	 * @param <T> type of copied object
	 * @return {@code (T) object} clone of copied object
	 */
	public static <T> T deepClone(T obj) throws IOException, ClassNotFoundException {
		// Serializing input object in to byte stream
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(obj);

		// Reading object from byte stream
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

		// Closing streams
		byteArrayInputStream.close();
		byteArrayOutputStream.close();
		objectInputStream.close();
		objectOutputStream.close();

		return (T) objectInputStream.readObject();
	}

}
