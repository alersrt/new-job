package org.student.clone;

import org.apache.commons.lang.ObjectUtils;

import java.io.*;

/**
 * It is util class which provide functions of object
 * copying. It works with serializable objects
 */
public class CopyUtils {
	/**
	 * Return copies of object with helps of Serialization.
	 * @param obj the copied object
	 * @param <T> type of copied object
	 * @return {@code (T) object} clone of copied object
	 */
	public static <T> T cloneWithSerialization(T obj) throws IOException, ClassNotFoundException {
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

	/**
	 * Return clone of object with helps of Apache Common Library
	 * @param obj the cloned object
	 * @param <T> type of cloned object
	 * @return {@code (T) object} clone of cloned object
	 */
	public static <T> T cloneWithLibrary(T obj) {
		return (T) ObjectUtils.cloneIfPossible(obj);
	}
}
