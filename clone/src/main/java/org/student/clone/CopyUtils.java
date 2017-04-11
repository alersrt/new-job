package org.student.clone;

import org.apache.commons.lang.ObjectUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * It is util class which provide functions of object
 * copying. It works with serializable objects
 */
public class CopyUtils {
	/**
	 * Return copies of object with helps of Serialization.
	 *
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
	 *
	 * @param obj the cloned object
	 * @param <T> type of cloned object
	 * @return {@code (T) object} clone of cloned object
	 */
	public static <T> T cloneWithLibrary(T obj) {
		return (T) ObjectUtils.cloneIfPossible(obj);
	}

	/**
	 * Return clone of object with helps of Reflection API.
	 * Original source code have taken from here:
	 * https://stackoverflow.com/questions/869033/how-do-i-copy-an-object-in-java
	 *
	 * @param obj the cloned object
	 * @param <T> type of cloned object
	 * @return {@code (T) object} clone of cloned object
	 */
	private static <T> T cloneWithReflectionAPI(T obj) {
		try {
			T clone = (T) obj.getClass().newInstance();
			//TODO: Rewrite this code with Stream API and Lambdas
			for (Field field : obj.getClass().getDeclaredFields()) {
				field.setAccessible(true);

				if (field.get(obj) == null || Modifier.isFinal(field.getModifiers())) {
					continue;
				}

				if (field.getType().isPrimitive()
						|| field.getType().equals(String.class)
						|| field.getType().getSuperclass().equals(Number.class)
						|| field.getType().equals(Boolean.class)) {
					field.set(clone, field.get(obj));
				} else {
					Object childObj = field.get(obj);

					if (childObj == obj) {
						field.set(clone, clone);
					} else {
						field.set(clone, cloneWithReflectionAPI(field.get(obj)));
					}
				}
			}
			return clone;
		} catch (Exception e) {
			return null;
		}
	}
}
