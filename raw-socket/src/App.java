import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.util.Base64;

public class App extends JFrame implements ActionListener {
    
    private JTextField urlField;
    private JEditorPane htmlPane;
    private JButton backButton;
    private JButton forwardButton;
    private String currentUrl;
    private java.util.Stack<String> backStack;
    private java.util.Stack<String> forwardStack;
    private JList<String> historyList;
    private DefaultListModel<String> historyListModel;
    private JProgressBar progressBar;
    
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

        backButton = new JButton("<");
        backButton.addActionListener(this);
        backButton.setEnabled(false);

        forwardButton = new JButton(">");
        forwardButton.addActionListener(this);
        forwardButton.setEnabled(false);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadURL(currentUrl);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(backButton);
        buttonPanel.add(forwardButton);
        buttonPanel.add(refreshButton);
        urlPanel.add(buttonPanel, BorderLayout.WEST);
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true); 
        getContentPane().add(progressBar, BorderLayout.SOUTH);
        
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

        historyListModel = new DefaultListModel<String>();
        historyList = new JList<String>(historyListModel);
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String url = historyList.getSelectedValue();
                    loadURL(url);
                    urlField.setText(url);
                }
            }
        });
        JScrollPane historyScrollPane = new JScrollPane(historyList);

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.add(new JLabel("History:"), BorderLayout.NORTH);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);
        getContentPane().add(historyPanel, BorderLayout.WEST);
        
        setSize(1080, 768);
        setVisible(true);
        
        loadURL(urlField.getText());

        currentUrl = urlField.getText();
        backStack = new java.util.Stack<String>();
        forwardStack = new java.util.Stack<String>();
        historyListModel.addElement(currentUrl);
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
                    progressBar.setValue(0);
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
            URL url = new URL(urlString);
            String host = url.getHost();
            int port = url.getPort() == -1 ? 80 : url.getPort();
            String path = url.getPath().isEmpty() ? "/" : url.getPath();
            
            // Open socket connection to the server
            Socket socket = new Socket(host, port);
            
            // Send HTTP request to the server
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("GET " + path + " HTTP/1.1");
            writer.println("Host: " + host);
            writer.println("Connection: close");
            writer.println();
            
            // Read the HTTP response from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line).append("\n");
            }
            
            // Extract the HTML content from the response
            String response = responseBuilder.toString();
            int htmlStartIndex = response.indexOf("<html");
            if (htmlStartIndex != -1) {
                String htmlContent = response.substring(htmlStartIndex);
                
                // Display the HTML content
                htmlPane.setText(htmlContent);
                currentUrl = urlString;
                urlField.setText(urlString);
                if (!historyListModel.contains(urlString)) {
                    historyListModel.addElement(urlString);
                }
            }
            
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new App();
    }
    
}