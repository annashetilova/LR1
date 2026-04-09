package reflector;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;

public class Main
{
    static void main(String[] args)
    {
        System.out.println("ЗАВДАННЯ 1");
        System.out.println(Class_inspector.buildClassReport("java.util.Scanner"));

        System.out.println("\nЗАВДАННЯ 2");
        Smartphone myPhone = new Smartphone("Samsung", 45);
        Class_inspector.inspectAndInteract(myPhone);

        System.out.println("\nЗАВДАННЯ 3");
        try {
            System.out.println("Викликаємо charge(25)...");
            Class_inspector.executeMethodDynamically(myPhone, "charge", 25);
            System.out.println("Нова батарея: " + myPhone.getBatteryLevel());

            // Тест виключення
            System.out.println("\nПробуємо викликати неіснуючий метод...");
            Class_inspector.executeMethodDynamically(myPhone, "fly");
        } catch (FunctionNotFoundException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\nЗАВДАННЯ 4");
        Object doubleArray = Class_inspector.generateArray(double.class, 2);
        Array.setDouble(doubleArray, 0, 1.5);
        Array.setDouble(doubleArray, 1, 3.14);
        System.out.println("Масив: " + Class_inspector.convertArrayToString(doubleArray));

        Object expandedArray = Class_inspector.expandArray(doubleArray, 4);
        System.out.println("Розширений: " + Class_inspector.convertArrayToString(expandedArray));

        Object stringMatrix = Class_inspector.generateMatrix(String.class, 2, 2);
        Array.set(Array.get(stringMatrix, 0), 0, "A");
        System.out.println("Матриця: " + Class_inspector.convertArrayToString(stringMatrix));

        Object expandedMatrix = Class_inspector.expandMatrix(stringMatrix, 3, 3);
        System.out.println("Розширена матриця: " + Class_inspector.convertArrayToString(expandedMatrix));

        System.out.println("\nЗАВДАННЯ 5");
        DataProcessor realServer = new HeavyServer();
        DataProcessor proxyServer = (DataProcessor) Proxy.newProxyInstance(
                HeavyServer.class.getClassLoader(),
                new Class<?>[]{DataProcessor.class},
                new TracingProfilerHandler(realServer)
        );
        proxyServer.processData("hello world");
    }
}

class Smartphone {
    private final String brand;
    public int batteryLevel;

    public Smartphone(String brand, int batteryLevel) {
        this.brand = brand;
        this.batteryLevel = batteryLevel;
    }

    public void checkStatus() {
        System.out.println("Телефон " + brand + " працює. Батарея: " + batteryLevel + "%");
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void charge(Integer amount) {
        this.batteryLevel += amount;
        System.out.println("Заряджено на " + amount + "%.");
    }
}

interface DataProcessor {
    String processData(String input);
}

class HeavyServer implements DataProcessor {
    @Override
    public String processData(String input) {
        try { Thread.sleep(600); } catch (InterruptedException e) {} // Імітація довгої роботи
        return "Processed: " + input.toUpperCase();
    }
}