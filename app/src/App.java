import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;

public class App extends JFrame implements ActionListener {
    
    private JTextField urlField;
    private JEditorPane htmlPane;
    private JButton backButton;
    private JButton forwardButton;
    private String currentUrl;
    private java.util.Stack<String> backStack;
    private java.util.Stack<String> forwardStack;
    
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

        backButton = new JButton("Back");
        backButton.addActionListener(this);
        backButton.setEnabled(false);

        forwardButton = new JButton("Forward");
        forwardButton.addActionListener(this);
        forwardButton.setEnabled(false);

        urlPanel.add(backButton, BorderLayout.WEST);
        urlPanel.add(forwardButton, BorderLayout.SOUTH);
        
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

        currentUrl = urlField.getText();
        backStack = new java.util.Stack<String>();
        forwardStack = new java.util.Stack<String>();
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == urlField || e.getSource() instanceof JButton) {
            if (e.getSource() == backButton) {
                if (!backStack.isEmpty()) {
                    forwardStack.push(currentUrl);
                    String backUrl = backStack.pop();
                    loadURL(backUrl);
                    urlField.setText(backUrl);
                    currentUrl = backUrl;
                    forwardButton.setEnabled(true);
                    if (backStack.isEmpty()) {
                        backButton.setEnabled(false);
                    }
                }
            } else if (e.getSource() == forwardButton) {
                if (!forwardStack.isEmpty()) {
                    backStack.push(currentUrl);
                    String forwardUrl = forwardStack.pop();
                    loadURL(forwardUrl);
                    urlField.setText(forwardUrl);
                    currentUrl = forwardUrl;
                    backButton.setEnabled(true);
                    if (forwardStack.isEmpty()) {
                        forwardButton.setEnabled(false);
                    }
                }
            } else {
                backStack.push(currentUrl);
                forwardStack.clear();
                String url = urlField.getText();
                loadURL(url);
                currentUrl = url;
                backButton.setEnabled(true);
                forwardButton.setEnabled(false);
            }
        }
    }
    
    private void loadURL(String urlString) {
        try {
            htmlPane.setPage(urlString);
            currentUrl = urlString;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new App();
    }
    
}
