/**
 * Copyright (c) 2011-2017, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jfinal.kit;

import java.util.UUID;

/**
 * StrKit.
 */
public class StrKit {
	
	/**
	 * 首字母变小写
	 */
	public static String firstCharToLowerCase(String str) {
		char firstChar = str.charAt(0);
		if (firstChar >= 'A' && firstChar <= 'Z') {
			char[] arr = str.toCharArray();
			arr[0] += ('a' - 'A');
			return new String(arr);
		}
		return str;
	}
	
	/**
	 * 首字母变大写
	 */
	public static String firstCharToUpperCase(String str) {
		char firstChar = str.charAt(0);
		if (firstChar >= 'a' && firstChar <= 'z') {
			char[] arr = str.toCharArray();
			arr[0] -= ('a' - 'A');
			return new String(arr);
		}
		return str;
	}
	
	/**
	 * 字符串为 null 或者内部字符全部为 ' ' '\t' '\n' '\r' 这四类字符时返回 true
	 */
	public static boolean isBlank(String str) {
		if (str == null) {
			return true;
		}
		int len = str.length();
		if (len == 0) {
			return true;
		}
		for (int i = 0; i < len; i++) {
			switch (str.charAt(i)) {
			case ' ':
			case '\t':
			case '\n':
			case '\r':
			// case '\b':
			// case '\f':
				break;
			default:
				return false;
			}
		}
		return true;
	}
	
	public static boolean notBlank(String str) {
		return !isBlank(str);
	}
	
	public static boolean notBlank(String... strings) {
		if (strings == null) {
			return false;
		}
		for (String str : strings) {
			if (isBlank(str)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean notNull(Object... paras) {
		if (paras == null) {
			return false;
		}
		for (Object obj : paras) {
			if (obj == null) {
				return false;
			}
		}
		return true;
	}
	
	public static String toCamelCase(String stringWithUnderline) {
		if (stringWithUnderline.indexOf('_') == -1) {
			return stringWithUnderline;
		}
		
		stringWithUnderline = stringWithUnderline.toLowerCase();
		char[] fromArray = stringWithUnderline.toCharArray();
		char[] toArray = new char[fromArray.length];
		int j = 0;
		for (int i=0; i<fromArray.length; i++) {
			if (fromArray[i] == '_') {
				// 当前字符为下划线时，将指针后移一位，将紧随下划线后面一个字符转成大写并存放
				i++;
				if (i < fromArray.length) {
					toArray[j++] = Character.toUpperCase(fromArray[i]);
				}
			}
			else {
				toArray[j++] = fromArray[i];
			}
		}
		return new String(toArray, 0, j);
	}
	
	public static String join(String[] stringArray) {
		StringBuilder sb = new StringBuilder();
		for (String s : stringArray) {
			sb.append(s);
		}
		return sb.toString();
	}
	
	public static String join(String[] stringArray, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<stringArray.length; i++) {
			if (i > 0) {
				sb.append(separator);
			}
			sb.append(stringArray[i]);
		}
		return sb.toString();
	}
	
	public static boolean slowEquals(String a, String b) {
		byte[] aBytes = (a != null ? a.getBytes() : null);
		byte[] bBytes = (b != null ? b.getBytes() : null);
		return HashKit.slowEquals(aBytes, bBytes);
	}
	
	public static String getRandomUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

    /**
     * 对象到字符串
     *
     * @param obj
     * @return 字符串，默认为""
     */
    public static String toStr(Object obj) {
        return toStr(obj, "");
    }

    /**
     * 对象到字符串
     *
     * @param obj
     * @param defaultVal
     * @return 字符串，默认为 defaultVal 的值
     */
    public static String toStr(Object obj, String defaultVal) {
        if (obj == null) {
            return defaultVal;
        } else {
            return obj.toString();
        }
    }
}




