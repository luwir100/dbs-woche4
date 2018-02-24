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
        //  Eigene SQL-Exception werfen, falls Kunde nicht in DB vorhanden? momentan wird SQLException: ResultSet closed geworfen.
    }

    @Override
    public void registerUser(Data data) throws SQLException {
        // Schema: Kunde(email,adresseID,vorname,nachname,passwort)
        String insertKunde = "INSERT INTO Kunde VALUES(?,(SELECT COUNT(*) FROM Adresse),?,?,?)";
        // Schema: Adresse(ID,plz,ort,straße,hausnummer)
        String insertAdresse = "INSERT INTO Adresse(ID,plz,ort,straße,hausnummer) VALUES((SELECT COUNT(*) FROM Adresse)+1,?,?,?,?)";

        PreparedStatement insertKundeStatement = Project.getInstance().getConnection().prepareStatement(insertKunde);
        PreparedStatement insertAdresseStatement = Project.getInstance().getConnection().prepareStatement(insertAdresse);

        if(!data.get("password1").toString().equals(data.get("password2").toString())){
            throw new SQLException(getClass().getName() + "Passwörter stimmen nicht überein");
        }
        // disable auto commit so both inserts are treated as one single transaction
        Project.getInstance().getConnection().getRawConnection().setAutoCommit(false);

        insertAdresseStatement.setString(1, data.get("zipCode").toString());
        insertAdresseStatement.setString(2, data.get("city").toString());
        insertAdresseStatement.setString(3, data.get("street").toString());
        insertAdresseStatement.setInt(4, Integer.parseInt(data.get("houseNumber").toString()));

        insertKundeStatement.setString(1, data.get("eMail").toString());
        insertKundeStatement.setString(2, data.get("firstName").toString());
        insertKundeStatement.setString(3, data.get("lastName").toString());
        insertKundeStatement.setString(4, data.get("password1").toString());

        insertAdresseStatement.execute();
        insertKundeStatement.execute();

        Project.getInstance().getConnection().getRawConnection().commit();
        Project.getInstance().getConnection().getRawConnection().setAutoCommit(true);
    }
}
