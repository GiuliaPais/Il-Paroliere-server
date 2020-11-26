package uninsubria.server.gui;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.prefs.BackingStoreException;

/**
 * Controller class for the credentials at server startup.
 * @author Giulia Pais
 * @version 0.9.0
 */
public class DbLoginController {
    /*---Fields---*/
    @FXML
    StackPane root;
    @FXML
    JFXTextField dbHost, dbName, dbAdmin, emailAddr;
    @FXML
    JFXPasswordField dbPw, emailPw;
    @FXML
    JFXButton confirmBtn, savePrefsBtn;


    /*---Constructors---*/
    public DbLoginController() {
    }

    /*---Methods---*/
    public void initialize() {
        setRequiredValidator();
        dbHost.setText(ServerLauncher.prefs.get("HOST", "localhost"));
        dbName.setText(ServerLauncher.prefs.get("DBNAME", "Il-Paroliere"));
        dbAdmin.setText(ServerLauncher.prefs.get("DBADMIN", "postgres"));
        dbPw.setText(ServerLauncher.prefs.get("DBPW", ""));
        emailAddr.setText(ServerLauncher.prefs.get("SERVER_EMAIL", "Il_Paroliere@outlook.it"));
        emailPw.setText(ServerLauncher.prefs.get("EMAIL_PW", ""));
    }

    @FXML
    void savePrefs() throws BackingStoreException {
        if (!validateAll()) {
            return;
        }
        ServerLauncher.prefs.put("HOST", dbHost.getText());
        ServerLauncher.prefs.put("DBNAME", dbName.getText());
        ServerLauncher.prefs.put("DBADMIN", dbAdmin.getText());
        ServerLauncher.prefs.put("DBPW", dbPw.getText());
        ServerLauncher.prefs.put("SERVER_EMAIL", emailAddr.getText());
        ServerLauncher.prefs.put("EMAIL_PW", emailPw.getText());
        ServerLauncher.prefs.flush();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        Label lbl = new Label("Preferences correctly saved");
        dialogLayout.setBody(lbl);
        JFXDialog dialog = new JFXDialog(root, dialogLayout, JFXDialog.DialogTransition.CENTER);
        JFXButton ok_button = new JFXButton("OK");ok_button.setOnAction((actionEvent -> {dialog.close();}));
        dialogLayout.setActions(ok_button);
        dialog.show();
    }

    @FXML void confirm() throws IOException {
        if (!validateAll()) {
            return;
        }
        ServerLauncher.masterServer.setDbHost(dbHost.getText());
        ServerLauncher.masterServer.setDbName(dbName.getText());
        ServerLauncher.masterServer.setDbAdmin(dbAdmin.getText());
        ServerLauncher.masterServer.setDbPw(dbPw.getText());
        ServerLauncher.masterServer.setServerEmail(emailAddr.getText());
        ServerLauncher.masterServer.setServerEmailPassword(emailPw.getText());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainServerGui.fxml"));
        Parent parent = loader.load();
        root.getScene().setRoot(parent);
    }

    private void setRequiredValidator() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Required");
        dbHost.setValidators(validator);
        dbName.setValidators(validator);
        dbAdmin.setValidators(validator);
        dbPw.setValidators(validator);
        emailAddr.setValidators(validator);
        emailPw.setValidators(validator);
    }

    private boolean validateAll() {
        boolean validHost = dbHost.validate();
        boolean validName = dbName.validate();
        boolean validAdmin = dbAdmin.validate();
        boolean validPw = dbPw.validate();
        boolean validEmail = emailAddr.validate();
        boolean validEPw = emailPw.validate();
        if (validHost & validName & validAdmin & validPw & validEmail & validEPw) {
            return true;
        }
        return false;
    }
}
