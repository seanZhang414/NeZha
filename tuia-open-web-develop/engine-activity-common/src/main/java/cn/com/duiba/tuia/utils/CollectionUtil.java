package cn.com.duiba.tuia.utils;

import org.apache.commons.collections.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author xuyenan
 * @createTime 2017/2/22
 */
public class CollectionUtil {

    private static Logger log = LoggerFactory.getLogger(CollectionUtil.class);

    private CollectionUtil(){
    }

    /**
     * getFieldList:(获取列表中某一属性的列表,例如ID). <br/>
     *
     * @author ZFZ
     * @param list
     * @param fieldName
     * @return
     * @since JDK 1.6
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List<Long> getFieldList(List list, String fieldName) {
        if (list == null || list.isEmpty()) {
            return ListUtils.EMPTY_LIST;
        }
        try {
            List<Long> filedList = new ArrayList();
            StringBuilder sb = new StringBuilder(fieldName);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            String field = sb.toString();
            for (Object obj : list) {
                Class clazz = obj.getClass();
                Method method = clazz.getMethod("get" + field);
                Object value = method.invoke(obj);
                filedList.add((Long) value);
            }
            return filedList;
        } catch (Exception e) {
            log.error(" CollectionUtil getFieldList has error , the msg = [{}]", e);
            return Collections.emptyList();
        }
    }
}
