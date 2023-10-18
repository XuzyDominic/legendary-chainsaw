package com.xzydominic.xzymq.utils;

import java.util.Objects;
import java.util.Optional;

public class ParamsUtils {

    public static Object getParamByMethod(Object[] args, Class<?> clazz) {
        Optional.ofNullable(args).orElseThrow(() -> new RuntimeException("method args is null"));
        Optional.ofNullable(clazz).orElseThrow(() -> new RuntimeException("class tag is null"));
        Object realObject = null;
        for (int var1 = args.length - 1; var1 >= 0; var1--) {
            if (clazz.isInstance(args[var1])) {
                realObject = args[var1];
                break;
            }
        }
        if (realObject == null) {
            throw new RuntimeException("Param not found");
        }
        return realObject;
    }

}
