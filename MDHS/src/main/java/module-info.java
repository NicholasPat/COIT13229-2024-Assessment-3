module com.mycompany.mdhs {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.mdhs to javafx.fxml;
    exports com.mycompany.mdhs;
}
