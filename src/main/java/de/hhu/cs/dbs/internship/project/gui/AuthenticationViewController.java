package de.hhu.cs.dbs.internship.project.gui;

import com.alexanderthelen.applicationkit.database.Data;
import de.hhu.cs.dbs.internship.project.Project;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationViewController extends com.alexanderthelen.applicationkit.gui.AuthenticationViewController {
    protected AuthenticationViewController(String name) {
        super(name);
    }

    public static AuthenticationViewController createWithName(String name) throws IOException {
        AuthenticationViewController viewController = new AuthenticationViewController(name);
        viewController.loadView();
        return viewController;
    }

    @Override
    public void loginUser(Data data) throws SQLException {
        String query = "SELECT passwort FROM Kunde WHERE Kunde.email=?";
        PreparedStatement statement = Project.getInstance().getConnection().prepareStatement(query);
        statement.setString(1, data.get("email").toString());
        ResultSet resultSet = statement.executeQuery();
        if(resultSet.getString(1).equals(data.get("password").toString())){
            Project.getInstance().getData().put("email", data.get("email").toString());
        }
        else throw new SQLException(getClass().getName() + "Falsche Eingabe");
    }

    @Override
    public void registerUser(Data data) throws SQLException {
        throw new SQLException(getClass().getName() + ".registerUser(Data) nicht implementiert.");
    }
}
