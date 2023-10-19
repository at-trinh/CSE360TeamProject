module me.thfour.effortlogger {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.h2database;
    requires java.sql;

    //requires org.controlsfx.controls;
    //requires org.kordamp.bootstrapfx.core;

    opens me.thfour.effortlogger to javafx.fxml;
    exports me.thfour.effortlogger;
    exports me.thfour.effortlogger.controllers;
    opens me.thfour.effortlogger.controllers to javafx.fxml;
}