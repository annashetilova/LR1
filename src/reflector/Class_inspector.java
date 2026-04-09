package reflector;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Class_inspector {

    // Завдання 1
    public static String buildClassReport(String typeName) {
        try {
            Class<?> targetClass = switch (typeName) {
                case "int" -> int.class; case "double" -> double.class;
                case "float" -> float.class; case "long" -> long.class;
                case "short" -> short.class; case "byte" -> byte.class;
                case "boolean" -> boolean.class; case "char" -> char.class;
                case "void" -> void.class;
                default -> Class.forName(typeName);
            };
            return buildClassReport(targetClass);
        } catch (ClassNotFoundException e) {
            return "Помилка: Клас або тип '" + typeName + "' не знайдено.";
        }
    }

    public static String buildClassReport(Class<?> cls) {
        StringBuilder report = new StringBuilder();

        if (cls.getPackage() != null) {
            report.append("Пакет: ").append(cls.getPackage().getName()).append("\n");
        }
        report.append("Модифікатори: ").append(Modifier.toString(cls.getModifiers())).append("\n");
        report.append("Тип: ").append(cls.getSimpleName()).append("\n");

        if (cls.getSuperclass() != null && cls.getSuperclass() != Object.class) {
            report.append("Базовий клас: ").append(cls.getSuperclass().getName()).append("\n");
        }

        Class<?>[] interfaces = cls.getInterfaces();
        if (interfaces.length > 0) {
            report.append("Реалізовані інтерфейси:\n");
            for (Class<?> i : interfaces) {
                report.append(" - ").append(i.getName()).append("\n");
            }
        }

        report.append("\nПоля: \n");
        for (Field field : cls.getDeclaredFields()) {
            report.append(Modifier.toString(field.getModifiers())).append(" ")
                    .append(field.getType().getSimpleName()).append(" ")
                    .append(field.getName()).append("\n");
        }

        report.append("\nКонструктори: \n");
        for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
            report.append(Modifier.toString(constructor.getModifiers())).append(" ")
                    .append(constructor.getName()).append("(");
            Parameter[] params = constructor.getParameters();
            for (int i = 0; i < params.length; i++) {
                report.append(params[i].getType().getSimpleName());
                if (i < params.length - 1) report.append(", ");
            }
            report.append(")\n");
        }

        report.append("\nМетоди: \n");
        for (Method method : cls.getDeclaredMethods()) {
            report.append(Modifier.toString(method.getModifiers())).append(" ")
                    .append(method.getReturnType().getSimpleName()).append(" ")
                    .append(method.getName()).append("(");
            Parameter[] params = method.getParameters();
            for (int i = 0; i < params.length; i++) {
                report.append(params[i].getType().getSimpleName());
                if (i < params.length - 1) report.append(", ");
            }
            report.append(")\n");
        }

        return report.toString();
    }

    // Завдання 2
    public static void inspectAndInteract(Object targetObject) {
        if (targetObject == null) {
            System.out.println("Об'єкт порожній (null).");
            return;
        }

        Class<?> objClass = targetObject.getClass();
        System.out.println("Реальний тип об'єкта: " + objClass.getName());
        System.out.println("\nПоточний стан (поля та значення):");

        for (Field field : objClass.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                System.out.println(" * " + field.getName() + " = " + field.get(targetObject));
            } catch (IllegalAccessException e) {
                System.out.println(" * " + field.getName() + " = [Доступ закрито]");
            }
        }

        System.out.println("\nВідкриті методи без параметрів:");
        List<Method> availableMethods = new ArrayList<>();
        int counter = 1;

        for (Method method : objClass.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) && method.getParameterCount() == 0) {
                availableMethods.add(method);
                System.out.println("[" + counter + "] " + method.getName() + "()");
                counter++;
            }
        }

        if (availableMethods.isEmpty()) return;

        Scanner in = new Scanner(System.in);
        System.out.print("\nВведіть номер методу для виклику (0 - скасувати): ");
        if (in.hasNextInt()) {
            int choice = in.nextInt();
            if (choice > 0 && choice <= availableMethods.size()) {
                Method selected = availableMethods.get(choice - 1);
                try {
                    System.out.println("Виконується метод: " + selected.getName());
                    Object res = selected.invoke(targetObject);
                    if (selected.getReturnType() != void.class) {
                        System.out.println("Результат: " + res);
                    }
                } catch (Exception e) {
                    System.out.println("Помилка виконання: " + e.getCause());
                }
            }
        }
    }

    // Завдання 3
    public static Object executeMethodDynamically(Object obj, String methodName, Object... arguments) throws FunctionNotFoundException {
        Class<?> clazz = obj.getClass();
        Class<?>[] argTypes = new Class<?>[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            argTypes[i] = arguments[i].getClass();
        }

        try {
            Method method = clazz.getMethod(methodName, argTypes);
            return method.invoke(obj, arguments);
        } catch (NoSuchMethodException e) {
            throw new FunctionNotFoundException("Метод '" + methodName + "' не знайдено для заданих параметрів!");
        } catch (Exception e) {
            throw new RuntimeException("Внутрішня помилка виклику: " + e.getMessage());
        }
    }

    // Завдання 4
    public static Object generateArray(Class<?> type, int length) {
        return Array.newInstance(type, length);
    }

    public static Object generateMatrix(Class<?> type, int rows, int cols) {
        return Array.newInstance(type, rows, cols);
    }

    public static Object expandArray(Object originalArray, int newLength) {
        Class<?> type = originalArray.getClass().getComponentType();
        Object newArray = Array.newInstance(type, newLength);
        int elementsToCopy = Math.min(Array.getLength(originalArray), newLength);
        System.arraycopy(originalArray, 0, newArray, 0, elementsToCopy);
        return newArray;
    }

    public static Object expandMatrix(Object originalMatrix, int newRows, int newCols) {
        Class<?> type = originalMatrix.getClass().getComponentType().getComponentType();
        Object newMatrix = Array.newInstance(type, newRows, newCols);
        int rowsToCopy = Math.min(Array.getLength(originalMatrix), newRows);
        for (int i = 0; i < rowsToCopy; i++) {
            Object oldRow = Array.get(originalMatrix, i);
            Object newRow = Array.get(newMatrix, i);
            int colsToCopy = Math.min(Array.getLength(oldRow), newCols);
            System.arraycopy(oldRow, 0, newRow, 0, colsToCopy);
        }
        return newMatrix;
    }

    public static String convertArrayToString(Object arrayObj) {
        if (arrayObj == null) return "null";
        if (!arrayObj.getClass().isArray()) return arrayObj.toString();

        StringBuilder builder = new StringBuilder("{");
        int length = Array.getLength(arrayObj);
        for (int i = 0; i < length; i++) {
            Object item = Array.get(arrayObj, i);
            if (item != null && item.getClass().isArray()) {
                builder.append(convertArrayToString(item));
            } else {
                builder.append(item);
            }
            if (i < length - 1) builder.append(", ");
        }
        builder.append("}");
        return builder.toString();
    }
}