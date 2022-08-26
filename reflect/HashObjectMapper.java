package cloud.redis.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author 张晓华
 * @date 2022-8-26
 */
public final class HashObjectMapper<T> {


	/**
	 *
	 * @param obj 实体类对象
	 * @return Map<String, Object> 返回一个属性名:属性值的Map
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public Map<String, Object> mappingObjectToMap(T obj) throws InvocationTargetException, IllegalAccessException {
		assert Objects.nonNull(obj):"参数不能为空";
		Class cla = obj.getClass();
		HashMap<String, Object> objectHashMap = new HashMap<>();
		Field[] fields = cla.getDeclaredFields();
		Method[] methods = cla.getMethods();
		ArrayList<Method> list = new ArrayList<>();
		// 将所有的get,set方法存入到这个集合中
		for (Method method : methods) {
			String name = method.getName();
			if (name.contains("get")){
				list.add(method);
			}
		}
		for (Field field : fields) {
			String methodName = "get" +  firstLetterToUpper(field.getName());
			method:for (Method method : list) {
				// 如果方法名相同,则调用其方法获取属性值
				if (method.getName().equals(methodName)){
					method.setAccessible(true);
					Object o = method.invoke(obj);
					if (Objects.nonNull(o)) {
						if (o.getClass().getSimpleName().equals("String")){
							String value = (String) o;
							// 字符串如果没有值就直接结束掉本次循环.
							if (!hasText(value)) {
								break method;
							}
						}
						objectHashMap.put(field.getName(),o);
					}
				}
			}
		}
		return objectHashMap;
	}

	/**
	 * @param objectMap 传入一个Map,泛型必须是Map<String, Object>
	 * @param cla 目标类型的实体类的Class
	 * @return 实体类对象
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public T mappingToObject(Map<Object, Object> objectMap, Class cla) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		assert (Objects.nonNull(objectMap) && Objects.nonNull(cla)):"参数不能为空";
		T obj = (T) cla.newInstance();
		Field[] fields = cla.getDeclaredFields();
		Method[] methods = cla.getDeclaredMethods();
		ArrayList<Method> list = new ArrayList<>();
		// 获取所有设置属性值的方法
		for (Method method : methods) {
			if (method.getName().contains("set")) {
				list.add(method);
			}
		}
		for (Map.Entry<Object, Object> entry : objectMap.entrySet()) {
			String key = entry.getKey().toString();
			Object value = entry.getValue();
			for (Method method : list) {
				if (method.getName().equals("set" + firstLetterToUpper(key))) {
					Class<?>[] types = method.getParameterTypes();
					if ( Objects.nonNull(value) && types[0].getSimpleName().equals(value.getClass().getSimpleName())) {
						method.setAccessible(true);
						method.invoke(obj, value);
					}

				}
			}
		}
		return obj;
	}



	private String firstLetterToUpper(String str) {
		String s = (str.charAt(0)+"").toUpperCase();
		return s+ str.substring(1);
	}

	private boolean hasText(String str) {
		if (Objects.isNull(str) || str.isEmpty()) {
			return false;
		}
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (Character.isWhitespace(chars[i])) {
				return false;
			}
		}
		return true;
	}





}
