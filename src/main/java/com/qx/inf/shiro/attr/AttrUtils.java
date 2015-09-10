package com.qx.inf.shiro.attr;

import com.qx.inf.shiro.utils.SerializeUtils;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年9月2日 下午5:32:08
 */
final class AttrUtils {
    static Object deserializeAttribute(AttrObject attr) {
        if (attr.isReturnHolder()) {
            return attr.holder();
        }
        Object obj = null;
        switch (attr.getType()) {
            case SIMPLE:
                obj = SerializeUtils.deserialize(attr.getValue(), attr.getParametrizedType());
                break;
            case MAP:
                obj = SerializeUtils.deserialize(attr.getValue(), SerializeUtils.getType(attr.getParametrizedType(), attr.getKeyType(), attr.getValueType()));
                break;
            case COLLECTION:
                obj = SerializeUtils.deserialize(attr.getValue(), SerializeUtils.getType(attr.getParametrizedType(), attr.getElementType()));
                break;
            case ARRAY:
                obj = SerializeUtils.deserialize(attr.getValue(), SerializeUtils.getType(attr.getParametrizedType(), attr.getElementType()));
                break;
            default:
                obj = SerializeUtils.deserialize(attr.getValue(), attr.getParametrizedType());
                break;
        }
        attr.holder(obj);
        return obj;
    }
}
