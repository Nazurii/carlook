package org.carlook.gui.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.carlook.process.control.LoginControl;
import org.carlook.process.control.exceptions.DatabaseException;
import org.carlook.process.control.exceptions.NoSuchUserOrPassword;
import org.carlook.services.util.Konstanten;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartseiteView extends VerticalLayout implements View {

    public void enter(ViewChangeListener.ViewChangeEvent event){
        this.setUp();}

    public void setUp(){
        this.setSizeFull();
        Label spacer = new Label("&nbsp", ContentMode.HTML);

        VerticalLayout login = new VerticalLayout();
        Panel panel = new Panel("SignIn");
        panel.setSizeUndefined();

        final TextField email = new TextField();
        email.setCaption("Email-Adresse: ");
        email.setDescription("Geben sie ihre Email-Adresse ein.");

        final PasswordField pw = new PasswordField();
        pw.setCaption("Passwort: ");
        pw.setDescription("Geben sie das von Ihnen gewählte Passwort ein.");

        Button signin = new Button("Login");
        signin.addClickListener(e->{
            String emailIn = email.getValue();
            String pwIn = pw.getValue();

            try {
                LoginControl.checkAuthentication(emailIn, pwIn);
            }catch(NoSuchUserOrPassword nsup){
                Notification.show("Benutzerfehler", "Email-Adresse oder Passwort falsch!", Notification.Type.ERROR_MESSAGE);
                email.setValue("");
                pw.setValue("");
            }catch(DatabaseException exception){
                Notification.show("DB-Fehler", Notification.Type.ERROR_MESSAGE);
            }catch(SQLException sqlException){
                Logger.getLogger(StartseiteView.class.getName()).log(Level.SEVERE, null, sqlException);            }
        });


        HorizontalLayout register = new HorizontalLayout();
        Label regis = new Label("Noch kein Konto? Registrieren Sie sich");
        Button signUp = new Button("hier", e->{
            UI.getCurrent().getNavigator().navigateTo(Konstanten.REGISTER);
        });
        signUp.addStyleName(ValoTheme.BUTTON_LINK);
        signUp.addStyleName("here-button");
        signUp.setWidthUndefined();
        register.addComponents(regis, signUp);
        register.setComponentAlignment(regis, Alignment.MIDDLE_CENTER);
        register.setComponentAlignment(signUp, Alignment.MIDDLE_CENTER);


        login.addComponents(email, pw, register, signin);
        login.setComponentAlignment(signin, Alignment.MIDDLE_CENTER);
        login.setComponentAlignment(email, Alignment.MIDDLE_CENTER);
        login.setComponentAlignment(pw, Alignment.MIDDLE_CENTER);
        panel.setContent(login);

        this.addComponent(panel);
        this.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
    }


}