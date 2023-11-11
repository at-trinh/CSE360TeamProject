module me.thfour.effortlogger {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires java.sql;
    requires MaterialFX;
    requires fr.brouillard.oss.cssfx;
    requires org.apache.commons.csv;
    requires com.h2database;

    //requires org.controlsfx.controls;
    //requires org.kordamp.bootstrapfx.core;

    opens me.thfour.effortlogger to javafx.fxml;
    exports me.thfour.effortlogger;
    exports me.thfour.effortlogger.controllers;
    exports me.thfour.effortlogger.models;
    opens me.thfour.effortlogger.controllers to javafx.fxml;
}