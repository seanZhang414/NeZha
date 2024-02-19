package cn.com.duiba.nezha.compute.common.util;

import java.util.Collection;
import java.util.Map;

/**
 * Created by pc on 2016/12/5.
 */
public class AssertUtil {

    private AssertUtil() {
    }


    /**
     *
     * @param keys
     * @return
     */
    public static boolean isAllEmpty(final Object... keys) {
        boolean ret = true;
        for (Object key : keys) {
            if(isNotEmpty(key)){
                ret = false;
            }
        }
        return ret;
    }

    /**
     *
     * @param keys
     * @return
     */
    public static boolean isAllNotEmpty(final Object... keys) {
        boolean ret = true;
        for (Object key : keys) {
            if(isEmpty(key)){
                ret = false;
            }
        }
        return ret;
    }

    /**
     *
     * @param keys
     * @return
     */
    public static boolean isAnyEmpty(final Object... keys) {
        boolean ret = false;
        for (Object key : keys) {
            if(isEmpty(key)){
                ret = true;
            }
        }
        return ret;
    }

    /**
     * @param collection collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {
        return isNull(collection) || collection.isEmpty();
    }

    /**
     * @param map map
     * @return
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.size() < 1;
    }

    /**
     * @param object object
     * @return
     */
    public static boolean isEmpty(Object object) {
        if (object instanceof Collection) {
            return isEmpty((Collection<?>) object);
        } else if (object instanceof Map) {
            return isEmpty((Map<?, ?>) object);
        }
        return isNull(object) || "".equals(object);
    }

    /**
     * @param object object
     * @return
     */
    public static boolean isEmpty(Object[] object) {
        return isNull(object) || object.length < 1;
    }

    /**
     * @param text text
     * @return
     */
    public static boolean isEmpty(String text) {
        return isNull(text) || text.trim().length() < 1;
    }

    /**
     * @param collection collection
     * @return
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * @param map map
     * @return
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * @param object object
     * @return
     */
    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    /**
     * @param object object
     * @return
     */
    public static boolean isNotEmpty(Object[] object) {
        return !isEmpty(object);
    }

    /**
     * @param text text
     * @return
     */
    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }

    /**
     * @param object object
     * @return
     */
    private static boolean isNull(Object object) {
        return object == null;
    }

    /**
     * @param object object
     * @return
     */
    private static boolean isNotNull(Object object) {
        return object != null;
    }

    /**
     * @param text text
     * @return
     */
    public static boolean hasLength(String text) {
        return text != null && text.length() > 0;
    }

    /**
     * @param text text
     * @return
     */
    public static boolean hasText(String text) {
        if (!hasLength(text)) {
            return false;
        }
        int strLen = text.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param textToSearch textToSearch
     * @param substring substring
     * @return
     */
    public static boolean isContain(String textToSearch, String substring) {
        return hasLength(textToSearch) && hasLength(substring) && textToSearch.contains(substring);
    }

    /**
     * @param superType superType
     * @param subType subType
     * @return
     */
    public static boolean isAssignable(Class<?> superType, Class<?> subType) {
        return isNotNull(superType) && isNotNull(subType) && superType.isAssignableFrom(subType);
    }

    /**
     * @param type type
     * @param object object
     * @return
     */
    public static boolean isInstanceOf(Class<?> type, Object object) {
        return isNotNull(type) && type.isInstance(object);
    }
}
