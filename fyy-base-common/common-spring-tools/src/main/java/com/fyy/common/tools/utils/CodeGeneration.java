package com.fyy.common.tools.utils;

import com.fyy.common.tools.global.Result;
import org.springframework.cloud.openfeign.FeignClient;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * [代码生成器]
 *
 * @author fuyouyi
 * @since 2020/12/15
 */
public class CodeGeneration {

    /**
     * 【代码生成器】 feignClientFallback
     *
     * @throws ClassNotFoundException
     */
    public static void fallbackCodeGeneration(Class<?> controllerClass, Class<?> fallBackClass, boolean skipExist) {
        String moduleName = controllerClass.getAnnotation(FeignClient.class).name();

        // 拼接类头
        System.out.println("@Component\npublic class " + controllerClass.getSimpleName() + "Fallback implements " + controllerClass.getSimpleName() + " {\n");
        for (Method method : controllerClass.getMethods()) {

            if (skipExist) {
                try {
                    fallBackClass.getMethod(method.getName(), method.getParameterTypes());
                    continue;
                } catch (Exception e) {
                }
            }

            // 拼接方法头
            System.out.println("\t@Override");
            System.out.print("\tpublic Result " + method.getName() + "(");

            Class<?>[] typeParameters = method.getParameterTypes();
            for (int i = 0; i < typeParameters.length; i++) {
                Class<?> paramClass = typeParameters[i];
                String requestParam = paramClass.getSimpleName();
                if (paramClass.getClassLoader() != null) {
                    System.out.print(requestParam + " " + requestParam.substring(0, 1).toLowerCase() + requestParam.substring(1));
                } else {
                    System.out.print(requestParam + " param" + i);
                }
                if (i != typeParameters.length - 1) {
                    System.out.print(",");
                }
            }
            System.out.println(") {");
            // 拼接方法体
            System.out.println("\t\treturn new Result().fallback(\"" + moduleName + "模块, " + method.getName() + "接口降级\");\n\t}\n\n");
        }
        System.out.println("}");
    }


    /**
     * 【代码生成器】 feignClientHelper
     */
    public static void feignHelperCodeGeneration(Class<?> controllerClass, Class<?> helperClass, boolean skipExist) {
        String moduleName = controllerClass.getAnnotation(FeignClient.class).name();

        // 拼接类头
        System.out.println("@Slf4j\n@Component\npublic class " + controllerClass.getSimpleName() + "Helper extends AutowireInitializingStaticUtil{\n");
        System.out.println("\t@Autowired\n\tpublic static " + controllerClass.getSimpleName() + " client;\n");
        // 拼接方法
        for (Method method : controllerClass.getMethods()) {

            if (skipExist) {
                try {
                    helperClass.getMethod(method.getName(), method.getParameterTypes());
                    continue;
                } catch (Exception e) {
                }
            }

            String resultDataClassName = getResultGenClassName(method.getGenericReturnType(), Result.class);
            // 拼接方法头
            System.out.print("\tpublic static " + resultDataClassName + " " + method.getName() + "(");
            Class<?>[] typeParameters = method.getParameterTypes();
            for (int i = 0; i < typeParameters.length; i++) {
                Class<?> paramClass = typeParameters[i];
                String requestParam = paramClass.getSimpleName();
                System.out.print(requestParam + " param" + i);
                if (i != typeParameters.length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println(") {");
            // 拼接方法体
            String resultClass = "Result" + ("void".equals(resultDataClassName) ? "" : "<" + resultDataClassName + ">");
            System.out.print("\t\t" + resultClass + " rs = client." + method.getName() + "(");
            for (int i = 0; i < typeParameters.length; i++) {
                System.out.print(" param" + i);
                if (i != typeParameters.length - 1) {
                    System.out.print(",");
                }
            }
            System.out.println(");");
            System.out.println(
                    "\t\tif(rs!=null && rs.fallback()) {\n" +
                            "\t\t\tthrow new HystrixException(rs.getMsg());\n" +
                            "\t\t}\n" +
                            "\t\tif(rs == null || !rs.success()) {\n" +
                            "\t\t\tlog.error(\"" + moduleName + "模块, " + method.getName() + "执行失败, 失败原因={}\", rs == null ? \"\" : rs.getMsg());\n" +
                            "\t\t\tthrow new RenException(buildErrorMsg(\"" + moduleName + "模块, " + method.getName() + "\", rs));\n" +
                            "\t\t}");
            if (!"void".equals(resultDataClassName)) {
                System.out.println("\t\treturn rs.getData();");
            }
            // 拼接方法结束
            System.out.println("\t}\n");
        }
        System.out.println(
                "\tprivate static String buildErrorMsg(String param, Result rs) {\n" +
                        "\t\treturn param + (rs == null ? \"\" : \": \" + rs.getMsg());\n" +
                        "\t}");
        System.out.println("}");
    }

    /**
     * 【Result专用】获取泛型类里面的对象
     */
    private static String getResultGenClassName(Type type, Class<?> resultClass) {
        String name = getTypeGenClassName(type);
        if (resultClass.getSimpleName().equals(name)) {
            return "void";
        } else {
            return name.substring(resultClass.getSimpleName().length() + 1, name.length() - 1);
        }
    }

    /**
     * 【通用】获取泛型类
     */
    private static String getTypeGenClassName(Type type) {
        return getTypeGenClassName(type, 0);
    }

    private static String getTypeGenClassName(Type type, int deep) {
        // 1. 转化ParameterizedType，如果转化失败，说明到头了
        // 2. 如果成功,说明一定长这样 rawType<ActualTypeArgument...>
        if (deep == 5) {
            // 你tm写这么多泛型干吗
            return getTypeClassName(type);
        }
        StringBuilder typeClass = new StringBuilder();
        try {
            ParameterizedType pType1 = (ParameterizedType) type;
            typeClass.append(getTypeClassName(pType1.getRawType())).append("<");
            // 递归
            Type[] typeList = pType1.getActualTypeArguments();
            for (int i = 0; i < typeList.length; i++) {
                typeClass.append(getTypeGenClassName(typeList[i], ++deep));
                if (i != typeList.length - 1) {
                    typeClass.append(", ");
                }
            }
            typeClass.append(">");
            return typeClass.toString();
        } catch (Exception exception) {
            return getTypeClassName(type);
        }
    }

    private static String getTypeClassName(Type type) {
        String[] typeName = type.getTypeName().split("\\.");
        return typeName[typeName.length - 1];
    }
}
