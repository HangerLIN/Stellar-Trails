package utils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class FastJson2Util {

    /**
     * 构建类型
     *
     * @param types
     * @return
     */
    public static Type buildType(Type... types) {
        ParameterizedTypeImpl beforeType = null;
        if (types != null && types.length > 0) {
            if (types.length == 1) {
                return new ParameterizedTypeImpl(new Type[]{null}, null, types[0]);
            }
            for (int i = types.length - 1; i > 0; i--) {
                beforeType = new ParameterizedTypeImpl(new Type[]{beforeType == null ? types[i] : beforeType}, null, types[i - 1]);
            }
        }
        return beforeType;
    }

    public static class ParameterizedTypeImpl implements Type {
        private Type[] actualTypeArguments;
        private Type ownerType;
        private Type rawType;

        public ParameterizedTypeImpl(Type[] actualTypeArguments, Type ownerType, Type rawType) {
            this.actualTypeArguments = actualTypeArguments;
            this.ownerType = ownerType;
            this.rawType = rawType;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (rawType != null) {
                sb.append(rawType.getTypeName());
            }
            if (actualTypeArguments != null && actualTypeArguments.length > 0) {
                sb.append("<");
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(actualTypeArguments[i].getTypeName());
                }
                sb.append(">");
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        Type type = FastJson2Util.buildType(Map.class, String.class, List.class, Integer.class);
        System.out.println(type);
    }
}