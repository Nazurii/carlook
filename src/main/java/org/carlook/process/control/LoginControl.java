package org.carlook.process.control;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import org.carlook.model.objects.entities.User;
import org.carlook.services.exceptions.DatabaseException;
import org.carlook.services.exceptions.NoSuchUserOrPassword;
import org.carlook.services.db.JDBCConnection;
import org.carlook.services.util.Konstanten;
import org.carlook.services.util.Roles;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginControl {

    private LoginControl(){}

    public static void checkAuthentication(String email, String pw) throws DatabaseException, SQLException, NoSuchUserOrPassword {
        User user = new User();
        String sql = "SELECT * FROM carlook.user WHERE carlook.user.email = ? AND carlook.user.passwort = ?";
        PreparedStatement statement = JDBCConnection.getInstance().getPreparedStatement(sql);
        statement.setString(1, email);
        statement.setString(2, pw);

        try(ResultSet set = statement.executeQuery()){
            if(set.next()){
                user.setVorname(set.getString(1));
                user.setNachname(set.getString(2));
                user.setEmail(set.getString(3));
                user.setPw(set.getString(4));
                user.setRole(set.getString(5));
                user.setId(set.getInt(6));
                if(user.getRole().equals(Roles.KUNDE)) user.setKundeId(set.getInt(7));
                if(user.getRole().equals(Roles.VERTRIEBLER)) user.setVerId(set.getInt(8));
            }else{
                throw new NoSuchUserOrPassword();
            }
        }catch(SQLException ex){
            Logger.getLogger(LoginControl.class.getName()).log(Level.SEVERE, null, ex);
        }

        VaadinSession.getCurrent().setAttribute(Roles.CURRENT, user);
        if(user.getRole().equals(Roles.KUNDE)) UI.getCurrent().getNavigator().navigateTo(Konstanten.SUCHE);
        if(user.getRole().equals(Roles.VERTRIEBLER)) UI.getCurrent().getNavigator().navigateTo(Konstanten.VER_MAIN);
    }

    public static void logoutUser() {
        UI.getCurrent().getSession().close();
        UI.getCurrent().getPage().setLocation("/carlook");
    }
}
