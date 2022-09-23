package cloud.huel.util;

import java.util.UUID;
import java.util.regex.Pattern;

public final class StringUtils {
	
	/**
	 * 必须是纯数字,否则返回false
	 * @param source 要检验的字符串
	 * @return
	 */
	public static boolean IsNumber(String source) {
		return Pattern.matches("[0-9]+", source);
	}
	
	/**
	 * 随机UUID,去掉特殊字符-
	 * @return
	 */
	public static String randomUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	
	
	
	public static void main(String[] args) {
		System.out.println(IsNumber("123"));
		System.out.println(randomUUID());
	}
	
	
	
	
	
	
}
