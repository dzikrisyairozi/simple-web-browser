module simple_web_browser {
    requires javafx.controls;
    requires javafx.fxml;

    opens simple_web_browser to javafx.fxml;
    exports simple_web_browser;
}
