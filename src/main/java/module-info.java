module me.th4.effortlogger {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.h2database;
    requires java.sql;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens me.th4.effortlogger to javafx.fxml;
    exports me.th4.effortlogger;
}