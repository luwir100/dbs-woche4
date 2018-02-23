package de.hhu.cs.dbs.internship.project.gui;

import com.alexanderthelen.applicationkit.database.Data;

import java.io.IOException;
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
        //throw new SQLException(getClass().getName() + ".loginUser(Data) nicht implementiert.");
    }

    @Override
    public void registerUser(Data data) throws SQLException {
        throw new SQLException(getClass().getName() + ".registerUser(Data) nicht implementiert.");
    }
}
