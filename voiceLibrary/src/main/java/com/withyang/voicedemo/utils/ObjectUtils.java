package com.withyang.voicedemo.utils;

import java.text.DecimalFormat;
import java.util.UUID;

public class ObjectUtils {

	public static boolean isNull(Object object) {
		try {
			if (null == object) {
				return true;
			}

			if (object instanceof String) {
				if (object.equals("")) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;

	}

	public static boolean isOneNull(Object... o) {
		Object[] objects = o;
		if (isNull(objects)) {
			return true;
		}

		for (Object object : objects) {
			 
			if (isNull(object)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isOneNotNull(Object... o) {
		Object[] objects = o;
		if (isNull(objects)) {
			return false;
		}

		for (Object object : objects) {
			if (!isNull(object)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isAllNotNull(Object... o) {
		Object[] objects = o;
		if (isNull(objects)) {
			return false;
		}

		for (Object object : objects) {
			if (isNull(object)) {
				return false;
			}
		}

		return true;
	}

	public static int string2Int(String str) {

		if (isNull(str)) {
			return 0;
		}

		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return 0;
		}
	}

	public static long string2Long(String str) {

		if (isNull(str)) {
			return 0L;
		}

		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			return 0L;
		}
	}

	public static float string2Float(String str) {

		if (isNull(str)) {
			return 0f;
		}

		try {
			return Float.parseFloat(str);
		} catch (Exception e) {
			return 0f;
		}
	}

	public static double string2Double(String str) {

		if (isNull(str)) {
			return 0;
		}

		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
			return 0;
		}
	}

	public static boolean string2Boolean(String str) {

		if (isNull(str)) {
			return false;
		}
		try {
			return Boolean.parseBoolean(str);
		} catch (Exception e) {
			return false;
		}

	}

	public static byte string2Byte(String str) {

		if (isNull(str)) {
			return 0;
		}
		try {
			return Byte.parseByte(str);
		} catch (Exception e) {
			return 0;
		}

	}

	public static String object2String(Object object) {
		return object == null ? "" : object.toString();
	}

	/**
	 * @param d
	 * @param length
	 *            精确小数位数
	 * @return
	 */

	public static double decimal(double d, int length) {
		StringBuffer sb = new StringBuffer();
		sb.append("0.");
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				sb.append("0");
			}
			DecimalFormat decimalFormat = new DecimalFormat(sb.toString());
			return string2Double(decimalFormat.format(d));
		}
		return 0;
	}

	private static final String[] digits = new String[] { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
	private static final String[] radices = new String[] { "", "十", "百", "千" };
	private static final String[] bigRadices = new String[] { "", "万", "亿", "万" };

	public static String digitConvert(int digit) {

		String currencyDigits = String.valueOf(digit);

		String integral = null;
		String outputCharacters = null;

		String d = null;
		int zeroCount = 0, p = 0, quotient = 0, modulus = 0;

		currencyDigits = currencyDigits.replace("/,/g", "");
		currencyDigits = currencyDigits.replace("/^0+/", "");
		String[] parts = currencyDigits.split("\\.");
		if (parts.length > 1) {
			integral = parts[0];

		} else {
			integral = parts[0];
		}

		outputCharacters = "";
		if (Double.parseDouble(integral) > 0) {

			zeroCount = 0;

			for (int i = 0; i < integral.length(); i++) {

				p = integral.length() - i - 1;
				d = integral.substring(i, i + 1);

				quotient = p / 4;
				modulus = p % 4;
				if (d.equals("0")) {
					zeroCount++;
				} else {
					if (zeroCount > 0) {
						outputCharacters += digits[0];
					}
					zeroCount = 0;
					outputCharacters += digits[Integer.parseInt(d)] + radices[modulus];
				}
				if (modulus == 0 && zeroCount < 4) {
					outputCharacters += bigRadices[quotient];
				}
			}
		}
		if (outputCharacters.length() > 1 && outputCharacters.startsWith("һ") && outputCharacters.indexOf("ʮ") == 1) {
			outputCharacters = outputCharacters.substring(1, outputCharacters.length());
		}

		return outputCharacters;
	}

	public static String formatString(String s) {
		if (isNull(s) || "null".equals(s)) {
			return "";
		}
		return s;
	}

	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

}
