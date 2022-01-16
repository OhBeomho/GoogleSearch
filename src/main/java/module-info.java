module com.example.googlesearch {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    requires org.jsoup;

    opens search to javafx.fxml;
    exports search;
}