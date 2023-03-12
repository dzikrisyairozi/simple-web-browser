import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;

public class App extends JFrame implements ActionListener {
    private JTextField urlField;
    private JTextArea pageArea;
    private JButton goButton;

    public App() {
        super("Simple Browser");

        urlField = new JTextField("https://www.google.com");
        urlField.addActionListener(this);

        goButton = new JButton("Go");
        goButton.addActionListener(this);

        pageArea = new JTextArea();
        pageArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(pageArea);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(urlField, BorderLayout.CENTER);
        panel.add(goButton, BorderLayout.EAST);

        getContentPane().add(panel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setSize(800, 600);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == urlField || e.getSource() == goButton) {
            try {
                URL url = new URL(urlField.getText());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                reader.close();
                pageArea.setText(sb.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        App browser = new App();
        browser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
