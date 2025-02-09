module com.example.loborems {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.hibernate.orm.core;
    requires java.persistence;
    requires java.sql;
    requires mysql.connector.java;
    requires java.naming;
    requires jbcrypt;
    requires org.slf4j;
    requires com.jfoenix;
    requires java.logging;
    requires java.desktop;
    requires org.apache.commons.compress;
    requires org.simplejavamail;
    requires org.simplejavamail.core;




    // Export and open necessary packages
    opens com.example.loborems to javafx.fxml;
    exports com.example.loborems;
    exports com.example.loborems.controllers;
    opens com.example.loborems.controllers to javafx.fxml;
    exports com.example.loborems.interfaces;
    opens com.example.loborems.interfaces to javafx.fxml;
    opens com.example.loborems.models to javafx.fxml, org.hibernate.orm.core;
    exports com.example.loborems.models;
    opens com.example.loborems.util to javafx.fxml;
    exports com.example.loborems.util;
    exports com.example.loborems.services;
    opens com.example.loborems.services to javafx.fxml, org.hibernate.orm.core;
}