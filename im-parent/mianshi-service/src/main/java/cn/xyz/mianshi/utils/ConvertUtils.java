//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.xyz.mianshi.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

public class ConvertUtils {
    private static Logger logger = LoggerFactory.getLogger(ConvertUtils.class);

    public ConvertUtils() {
    }

    public static <T> T sourceToTarget(Object source, Class<T> target) {
        return sourceToTarget(source, target);
    }

    public static <T> T sourceToTarget(Object source, Class<T> target, String... ignoreProperties) {
        if (source == null) {
            return null;
        } else {
            T targetObject = null;

            try {
                targetObject = target.newInstance();
                BeanUtils.copyProperties(source, targetObject, ignoreProperties);
            } catch (Exception var5) {
                logger.error("convert error ", var5);
            }

            return targetObject;
        }
    }

    public static <T> List<T> sourceToTarget(Collection<?> sourceList, Class<T> target) {
        return sourceToTarget(sourceList, target);
    }

    public static <T> List<T> sourceToTarget(Collection<?> sourceList, Class<T> target, String... ignoreProperties) {
        if (sourceList == null) {
            return null;
        } else {
            List targetList = new ArrayList(sourceList.size());

            try {
                Iterator var4 = sourceList.iterator();

                while(var4.hasNext()) {
                    Object source = var4.next();
                    T targetObject = target.newInstance();
                    BeanUtils.copyProperties(source, targetObject, ignoreProperties);
                    targetList.add(targetObject);
                }
            } catch (Exception var7) {
                logger.error("convert error ", var7);
            }

            return targetList;
        }
    }
}
