package de.hhu.cs.dbs.internship.project.table.account;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;
import de.hhu.cs.dbs.internship.project.Project;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Lieferabos extends Table{
    @Override
    public String getSelectQueryForTableWithFilter(String s) throws SQLException {
        if (Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            if(s == null || s.isEmpty()){
                return  "SELECT warenkorbID as ID, kundeEmail as Besitzer, beginn, ende, lieferintervall, bestellstatus " +
                        "FROM Lieferabo JOIN Warenkorb ON warenkorbID = ID " +
                        "WHERE kundeEmail NOT IN (SELECT kundeEmail FROM Angestellter " +
                        "WHERE kundeEmail <>'" + Project.getInstance().getData().get("email").toString() +"')";
            }
            else return "SELECT warenkorbID as ID, kundeEmail as Besitzer, beginn, ende, lieferintervall, bestellstatus " +
                        "FROM Lieferabo JOIN Warenkorb ON warenkorbID = ID " +
                        "WHERE kundeEmail LIKE '%" + s + "%' AND kundeEmail NOT IN " +
                        "(SELECT kundeEmail FROM Angestellter " +
                        "WHERE kundeEmail <>'" + Project.getInstance().getData().get("email").toString() +"')";
        }
        return "SELECT warenkorbID as ID, kundeEmail as Besitzer, beginn, ende, lieferintervall, bestellstatus " +
                "FROM Lieferabo JOIN Warenkorb ON warenkorbID = ID " +
                "WHERE kundeEmail='" + Project.getInstance().getData().get("email").toString() + "'";
    }

    @Override
    public String getSelectQueryForRowWithData(Data data) throws SQLException {
        return  "SELECT warenkorbID as ID, beginn, ende, lieferintervall FROM Lieferabo " +
                "WHERE warenkorbID = " + data.get("Lieferabo.ID");
    }

    @Override
    public void insertRowWithData(Data data) throws SQLException {
        if(isPremiumkunde()){
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(false);

            int newID = Project.getInstance().getConnection().executeQuery("SELECT MAX(ID) FROM Warenkorb").getInt(1) +1;

            String s =  "INSERT INTO Warenkorb VALUES(" + newID + ",'" + Project.getInstance().getData().get("email").toString() +
                        "',date('now'),'in Bearbeitung')";

            PreparedStatement insertWarenkorb = Project.getInstance().getConnection().prepareStatement(s);
            insertWarenkorb.execute();

            s = "INSERT INTO Lieferabo VALUES("+ newID + ",?,?,?)";

            PreparedStatement insertLieferabo = Project.getInstance().getConnection().prepareStatement(s);
            insertLieferabo.setString(1, data.get("Lieferabo.beginn").toString());
            insertLieferabo.setString(2, data.get("Lieferabo.ende").toString());
            insertLieferabo.setInt(3, Integer.parseInt(data.get("Lieferabo.lieferintervall").toString()));

            insertLieferabo.execute();

            Project.getInstance().getConnection().getRawConnection().commit();
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(true);
        }
        else throw new SQLException(getClass().getName() + ": Premiumstatus benötigt");
    }

    @Override
    public void updateRowWithData(Data oldData, Data newData) throws SQLException {
        String s = "UPDATE Lieferabo SET ende=?,lieferintervall=? WHERE warenkorbID=?";

        PreparedStatement updateLieferabo = Project.getInstance().getConnection().prepareStatement(s);
        updateLieferabo.setString(1, newData.get("Lieferabo.ende").toString());
        updateLieferabo.setInt(2, Integer.parseInt  (newData.get("Lieferabo.lieferintervall").toString()));
        updateLieferabo.setString(3, oldData.get("Lieferabo.ID").toString());
        updateLieferabo.execute();
    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {
        String s = "DELETE FROM Lieferabo WHERE warenkorbID=?";

        PreparedStatement deleteLieferabo = Project.getInstance().getConnection().prepareStatement(s);
        deleteLieferabo.setString(1, data.get("Lieferabo.ID").toString());
        deleteLieferabo.execute();
    }
    private boolean isPremiumkunde() throws SQLException {
        String s =  "SELECT COUNT(*) FROM Premiumkunde WHERE " +
                    "kundeEmail ='" + Project.getInstance().getData().get("email").toString() + "'";

        return Project.getInstance().getConnection().executeQuery(s).getInt(1) > 0;
    }
}
