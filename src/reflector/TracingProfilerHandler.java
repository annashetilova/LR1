package reflector;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TracingProfilerHandler implements InvocationHandler {
    private final Object realObject;

    public TracingProfilerHandler(Object realObject) {
        this.realObject = realObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // (вивід інфи про метод)
        System.out.println("\n[TRACE] Виклик методу: " + method.getName());
        System.out.println("[TRACE] Параметри: " + (args == null ? "немає" : Arrays.toString(args)));

        // (замір часу)
        long startTime = System.currentTimeMillis();

        Object methodResult = method.invoke(realObject, args);

        long endTime = System.currentTimeMillis();

        System.out.println("[TRACE] Обчислене значення: " + methodResult);
        System.out.println("[PROFILE] Час виконання: " + (endTime - startTime) + " мілісекунд.");

        return methodResult;
    }
}