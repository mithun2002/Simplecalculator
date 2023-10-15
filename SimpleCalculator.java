import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimpleCalculator extends JFrame implements ActionListener {

    // Components
    private JPanel panel;
    private JTextField textField;
    private JButton[] buttons;
    private String[] buttonLabels = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+"
    };

    // Constructor
    public SimpleCalculator() {
        panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4));
        textField = new JTextField();
        buttons = new JButton[16];

        // Adding components
        for (int i = 0; i < 16; i++) {
            buttons[i] = new JButton(buttonLabels[i]);
            panel.add(buttons[i]);
            buttons[i].addActionListener(this);
        }

        // Setting layout
        add(textField, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        setTitle("Simple Calculator");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Action listener for the buttons
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("=")) {
            // Evaluate the expression
            String expression = textField.getText();
            try {
                textField.setText(String.valueOf(eval(expression)));
            } catch (Exception exception) {
                textField.setText("Error");
            }
        } else {
            // Append the command to the text field
            textField.setText(textField.getText() + command);
        }
    }

    // Function to evaluate the expression
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean isDigit(int charToCheck) {
                return charToCheck >= '0' && charToCheck <= '9';
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if (isDigit(ch)) { // numbers
                    while (isDigit(ch)) nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }
        }.parse();
    }

    // Main method to run the application
    public static void main(String[] args) {
        new SimpleCalculator();
    }
}
