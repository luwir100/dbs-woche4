package de.hhu.cs.dbs.internship.project.table.account;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;
import de.hhu.cs.dbs.internship.project.Project;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Account extends Table {
    @Override
    public String getSelectQueryForTableWithFilter(String filter) throws SQLException {
        String user = Project.getInstance().getData().get("email").toString();
        switch (Project.getInstance().getData().get("rights").toString()) {
            case "kunde":
                return "SELECT vorname,nachname,email,plz,ort,straße,hausnummer " +
                        "FROM Kunde JOIN Adresse ON Kunde.adresseID=Adresse.ID " +
                        "WHERE Kunde.email ='" + user + "'";

            //  Angestellte sehen alle Kunden und ihre eigenen Daten, allerdings nicht die von ihren Kollegen
            case "angestellter":
                if(filter==null || filter.isEmpty()) {
                    return "SELECT vorname,nachname,email,plz,ort,straße,hausnummer " +
                            "FROM Kunde JOIN Adresse ON Kunde.adresseID=Adresse.ID " +
                            "WHERE Kunde.email NOT IN (SELECT kundeEmail FROM Angestellter WHERE kundeEmail<>'" + user + "')";
                }
                return "SELECT vorname,nachname,email,plz,ort,straße,hausnummer " +
                        "FROM Kunde JOIN Adresse ON Kunde.adresseID=Adresse.ID " +
                        "WHERE Kunde.email NOT IN (SELECT kundeEmail FROM Angestellter WHERE kundeEmail<>'" + user + "') " +
                        "AND Kunde.email LIKE '%" + filter + "%'";
            default:
                throw new SQLException(getClass().getName() + ": insuffiziente Rechte.");
        }
    }

    @Override
    public String getSelectQueryForRowWithData(Data data) throws SQLException {
        //  für Angestellte Gehalt/Jobbezeichnung anzeigen lassen?
        return "SELECT vorname,nachname,passwort,email,plz,ort,straße,hausnummer " +
                "FROM Kunde JOIN Adresse ON Kunde.adresseID=Adresse.ID " +
                "WHERE Kunde.email ='" + data.get("Kunde.email") + "'";
    }

    @Override
    public void insertRowWithData(Data data) throws SQLException {
        if (Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(false);

            String s = "SELECT COUNT(*) FROM Adresse";
            PreparedStatement countAdress = Project.getInstance().getConnection().prepareStatement(s);
            countAdress.execute();
            int adresseID = countAdress.getResultSet().getInt(1) + 1;

            s = "INSERT INTO Adresse VALUES(" + adresseID + ",?,?,?,?)";

            PreparedStatement insertAdress = Project.getInstance().getConnection().prepareStatement(s);
            insertAdress.setString(1,data.get("Adresse.plz").toString());
            insertAdress.setString(2,data.get("Adresse.ort").toString());
            insertAdress.setString(3,data.get("Adresse.straße").toString());
            insertAdress.setInt(4,Integer.parseInt(data.get("Adresse.hausnummer").toString()));
            insertAdress.execute();

            s = "INSERT INTO Kunde VALUES(?," + adresseID + ",?,?,?)";

            PreparedStatement insertKunde = Project.getInstance().getConnection().prepareStatement(s);
            insertKunde.setString(1,data.get("Kunde.email").toString());
            insertKunde.setString(2,data.get("Kunde.vorname").toString());
            insertKunde.setString(3,data.get("Kunde.nachname").toString());
            insertKunde.setString(4,data.get("Kunde.passwort").toString());
            insertKunde.execute();

            Project.getInstance().getConnection().getRawConnection().commit();
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(true);
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte.");
    }

    @Override
    public void updateRowWithData(Data oldData, Data newData) throws SQLException {
        int adresseID = getAdresseID(oldData.get("Kunde.email").toString());

        Project.getInstance().getConnection().getRawConnection().setAutoCommit(false);

        // no new records needed
        if(oldData.get("Kunde.email").toString().equals(newData.get("Kunde.email").toString())){
            updateAdress(newData, adresseID);

            String s = "UPDATE Kunde SET vorname = ?, nachname = ?, passwort= ?" +
                        "WHERE Kunde.email ='" + oldData.get("Kunde.email") + "'";

            PreparedStatement updateKunde = Project.getInstance().getConnection().prepareStatement(s);

            updateKunde.setString(1, newData.get("Kunde.vorname").toString());
            updateKunde.setString(2, newData.get("Kunde.nachname").toString());
            updateKunde.setString(3, newData.get("Kunde.passwort").toString());

            updateKunde.execute();
        }
        // new record needed since email(PK) changed
        else{
            // update Data
            Project.getInstance().getData().put("email",newData.get("Kunde.email").toString());
            Project.getInstance().getData().put("passwort",newData.get("Kunde.passwort").toString());

            String s = "INSERT INTO Kunde VALUES(?," + adresseID + ",?,?,?)";

            PreparedStatement insertKunde = Project.getInstance().getConnection().prepareStatement(s);

            insertKunde.setString(1, newData.get("Kunde.email").toString());
            insertKunde.setString(2, newData.get("Kunde.vorname").toString());
            insertKunde.setString(3, newData.get("Kunde.nachname").toString());
            insertKunde.setString(4, newData.get("Kunde.passwort").toString());

            insertKunde.execute();

            updateAdress(newData, adresseID);


            s = "DELETE FROM Kunde WHERE Kunde.email='" + oldData.get("Kunde.email").toString() + "'";

            PreparedStatement deleteKunde = Project.getInstance().getConnection().prepareStatement(s);

            deleteKunde.execute();
        }
        Project.getInstance().getConnection().getRawConnection().commit();
        Project.getInstance().getConnection().getRawConnection().setAutoCommit(true);
    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {
        /* Check für Recht hier nicht notwendig da:
        *   - Kunden sehen nur ihr eigenes Account
        *   - Angestellte sehen alle Accounts, die nicht ihren Kollegen gehören
        *   - Somit kann jeder alles löschen, was er selbst sieht
        */
        String s = "DELETE FROM Kunde WHERE Kunde.email='" + data.get("Kunde.email").toString() + "'";

        PreparedStatement deleteKunde = Project.getInstance().getConnection().prepareStatement(s);
        deleteKunde.execute();
    }
    private int getAdresseID(String user) throws SQLException{
        String s = "SELECT adresseID FROM Kunde WHERE email='" + user + "'";

        PreparedStatement preparedStatement = Project.getInstance().getConnection().prepareStatement(s);
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();

        return resultSet.getInt(1);
    }
    private void updateAdress(Data newData, int adresseID) throws SQLException{
        String s =  "UPDATE Adresse SET plz = ?, ort = ?, straße = ?, hausnummer= ? " +
                "WHERE Adresse.ID =" + adresseID;

        PreparedStatement updateAdresse = Project.getInstance().getConnection().prepareStatement(s);

        updateAdresse.setString(1, newData.get("Adresse.plz").toString());
        updateAdresse.setString(2, newData.get("Adresse.ort").toString());
        updateAdresse.setString(3, newData.get("Adresse.straße").toString());
        updateAdresse.setInt(4, Integer.parseInt(newData.get("Adresse.hausnummer").toString()));

        updateAdresse.execute();
    }
}
