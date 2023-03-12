import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;

public class App extends JFrame implements ActionListener {
    
    private JTextField urlField;
    private JEditorPane htmlPane;
    
    public App() {
        super("Simple Web Browser");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel urlPanel = new JPanel(new BorderLayout());
        urlField = new JTextField("https://www.google.com");
        urlField.addActionListener(this);
        urlPanel.add(urlField, BorderLayout.CENTER);
        
        JButton goButton = new JButton("Go");
        goButton.addActionListener(this);
        urlPanel.add(goButton, BorderLayout.EAST);
        
        getContentPane().add(urlPanel, BorderLayout.NORTH);

        htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
        htmlPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    loadURL(e.getURL().toString());
                }
            }
        });
        getContentPane().add(new JScrollPane(htmlPane), BorderLayout.CENTER);
        
        setSize(640, 480);
        setVisible(true);
        
        loadURL(urlField.getText());
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == urlField || e.getSource() instanceof JButton) {
            loadURL(urlField.getText());
        }
    }
    
    private void loadURL(String urlString) {
        try {
            htmlPane.setPage(urlString);
            urlField.setText(urlString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new App();
    }
    
}
