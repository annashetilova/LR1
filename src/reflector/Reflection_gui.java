package reflector;

import javax.swing.*;
import java.awt.*;

public class Reflection_gui {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Дослідник Класів (Reflection API)");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new FlowLayout());
        JTextField classInput = new JTextField(30);
        JButton analyzeBtn = new JButton("Аналізувати");
        topPanel.add(new JLabel("Введіть повне ім'я класу (напр. java.lang.String або int):"));
        topPanel.add(classInput);
        topPanel.add(analyzeBtn);

        JTextArea resultArea = new JTextArea();
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        analyzeBtn.addActionListener(e -> {
            String target = classInput.getText().trim();
            if (!target.isEmpty()) {
                String report = Class_inspector.buildClassReport(target);
                resultArea.setText(report);
            }
        });

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}