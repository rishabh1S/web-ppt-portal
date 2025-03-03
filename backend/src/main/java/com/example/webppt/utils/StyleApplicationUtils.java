package com.example.webppt.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Color;
import org.springframework.stereotype.Component;

@Component
public class StyleApplicationUtils {

    public static void applyStyles(Object target, Map<String, Object> styles) {
        // Create a filtered copy of the styles, excluding keys like "text" that are
        // content
        Map<String, Object> filteredStyles = new HashMap<>(styles);
        filteredStyles.remove("text");
        filteredStyles.remove("shapeType");

        Class<?> targetClass = target.getClass();
        for (Map.Entry<String, Object> entry : filteredStyles.entrySet()) {
            String styleKey = entry.getKey();
            Object value = entry.getValue();
            String setterName = "set" + Character.toUpperCase(styleKey.charAt(0)) + styleKey.substring(1);
            try {
                boolean methodFound = false;
                for (Method method : targetClass.getMethods()) {
                    if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                        Class<?> paramType = method.getParameterTypes()[0];
                        value = updateValueForColorCheck(paramType, value);
                        method.invoke(target, value);
                        methodFound = true;
                        break;
                    }
                }
                if (!methodFound) {
                    System.err.println("No setter found for style key: " + styleKey);
                }
            } catch (Exception e) {
                System.err.println("Failed to apply style '" + styleKey + "': " + e.getMessage());
            }
        }
    }

    private static Object updateValueForColorCheck(Class<?> paramType, Object value) throws Exception {
        if (paramType == java.awt.Color.class && value instanceof String) {
            return java.awt.Color.decode((String) value);
        }
        if (paramType.getSimpleName().equals("PaintStyle") && value instanceof String) {
            Method m = paramType.getMethod("createSolidPaint", Color.class);
            return m.invoke(null, java.awt.Color.decode((String) value));
        }
        return value;
    }

}
