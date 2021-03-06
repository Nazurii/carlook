package org.carlook.gui.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import org.carlook.gui.components.TopPanel;
import org.carlook.gui.windows.ConfirmationWindow;
import org.carlook.model.objects.entities.Auto;
import org.carlook.model.objects.dao.AutoDAO;
import org.carlook.model.objects.entities.User;
import org.carlook.services.util.GridBuild;
import org.carlook.services.util.Konstanten;
import org.carlook.services.util.Roles;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SucheView extends VerticalLayout implements View {

    User user;
    VerticalLayout content = new VerticalLayout();
    HorizontalLayout suchFelder;
    TopPanel toppanel;
    Label spacer = new Label("&nbsp", ContentMode.HTML);

    public void enter(ViewChangeListener.ViewChangeEvent event) {
        user = (User) VaadinSession.getCurrent().getAttribute(Roles.CURRENT);
        if(user == null || !user.getRole().equals(Roles.KUNDE)) UI.getCurrent().getNavigator().navigateTo(Konstanten.START);
        else this.setUp();
    }

    public void setUp() {
        toppanel = new TopPanel();

        TextField markeFeld = new TextField("Marke:");
        markeFeld.setDescription("Geben sie eine Automarke ein.");
        markeFeld.setValue("");
        markeFeld.setId("markeField");

        TextField baujahrFeld = new TextField("Baujahr:");
        baujahrFeld.setDescription("Geben sie das Baujahr des Autos ein.");
        baujahrFeld.setValue("");

        markeFeld.addValueChangeListener(e-> onTheFly(markeFeld.getValue(), baujahrFeld.getValue()));
        baujahrFeld.addValueChangeListener(e-> onTheFly(markeFeld.getValue(), baujahrFeld.getValue()));

        suchFelder = new HorizontalLayout();
        suchFelder.addComponents(markeFeld, baujahrFeld);

        //Erstmal alle Autos anzeigen lassen
        onTheFly("", "");
        this.addComponent(content);
        this.setComponentAlignment(content, Alignment.MIDDLE_CENTER);
    }

    public void onTheFly(String marke, String baujahr){
        //Erstellung Tabelle mit Jobangeboten
        content.removeAllComponents();
        content.addComponents(toppanel, new Label("&nbsp", ContentMode.HTML), suchFelder);

        List<Auto> autos = null;

        try {
            autos = AutoDAO.getInstance().searchAutos(marke, baujahr);
        } catch (SQLException ex) {
            Logger.getLogger(SucheView.class.getName()).log(Level.SEVERE, null, ex);
        }

        Grid<Auto> autoGrid = GridBuild.basicGrid(autos);
        autoGrid.setCaption(" <span style='color:#EAECEC; font-size:25px; text-shadow: 1px 1px 1px black; font-family: Roboto, sans-serif;'> " + (marke.equals("") ? "Alle Autos:" : "Ergebnisse für: " + marke) + " </span>");

        ButtonRenderer<Auto> reservieren = new ButtonRenderer<>(clickEvent ->{
            ConfirmationWindow window = new ConfirmationWindow("Wollen sie dieses Auto reservieren?", clickEvent.getItem().getAutoid());
            UI.getCurrent().addWindow(window);
        });

        autoGrid.addColumn(Auto -> "Reservieren", reservieren).setWidth(150);

        content.addComponents(spacer, autoGrid);
    }
}
