package de.hhu.cs.dbs.internship.project.table.account;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;
import de.hhu.cs.dbs.internship.project.Project;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Bestellung extends Table{

    @Override
    public String getSelectQueryForTableWithFilter(String s) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            if(s == null || s.isEmpty()){
                return "SELECT Warenkorb.ID,kundeEmail as Besitzer, bestelldatum, bestellstatus " +
                        "FROM Warenkorb " +
                        //"WHERE bestellstatus <> 'in Bearbeitung' AND kundeEmail NOT IN (SELECT kundeEmail FROM Angestellter " +
                        "WHERE kundeEmail NOT IN (SELECT kundeEmail FROM Angestellter " +
                        "WHERE kundeEmail <>'" + Project.getInstance().getData().get("email").toString() +"')";
            }
            else{
                return "SELECT Warenkorb.ID,kundeEmail as Besitzer, bestelldatum, bestellstatus " +
                        "FROM Warenkorb " +
                        //"WHERE bestellstatus <> 'in Bearbeitung' AND kundeEmail LIKE '%" + s + "%' AND kundeEmail NOT IN " +
                        "WHERE kundeEmail LIKE '%" + s + "%' AND kundeEmail NOT IN " +
                        "(SELECT kundeEmail FROM Angestellter " +
                        "WHERE kundeEmail <>'" + Project.getInstance().getData().get("email").toString() +"')";
            }
        }
        else return "SELECT Warenkorb.ID,kundeEmail as Besitzer, bestelldatum, bestellstatus " +
                    "FROM Warenkorb " +
                    "WHERE kundeEmail='" + Project.getInstance().getData().get("email").toString() + "'";
    }

    @Override
    public String getSelectQueryForRowWithData(Data data) throws SQLException {
        return  "SELECT Warenkorb.ID,kundeEmail as Besitzer, bestelldatum, bestellstatus " +
                "FROM Warenkorb " +
                "WHERE Warenkorb.ID=" + data.get("Warenkorb.ID");
    }

    @Override
    public void insertRowWithData(Data data) throws SQLException {
        int newID = Project.getInstance().getConnection().executeQuery("SELECT MAX(ID) FROM Warenkorb").getInt(1) +1;

        String s = "INSERT INTO Warenkorb VALUES(" + newID + ",'" + Project.getInstance().getData().get("email").toString() +"',date('now'),'in Bearbeitung')";
        PreparedStatement insertWarenkorb = Project.getInstance().getConnection().prepareStatement(s);

        insertWarenkorb.execute();
    }

    @Override
    public void updateRowWithData(Data data, Data data1) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            String s = "UPDATE Warenkorb SET bestellstatus = ? WHERE Warenkorb.ID ='" + data.get("Warenkorb.ID") +"'";

            PreparedStatement updateWarekorb = Project.getInstance().getConnection().prepareStatement(s);
            updateWarekorb.setString(1, data1.get("Warenkorb.bestellstatus").toString());
            updateWarekorb.execute();
        }
        else{
            if (data.get("Warenkorb.bestellstatus").toString().equals("in Bearbeitung")
                    && data1.get("Warenkorb.bestellstatus").toString().equals("versandfertig")){
                String s = "UPDATE Warenkorb SET bestellstatus = ? WHERE Warenkorb.ID ='" + data.get("Warenkorb.ID") +"'";

                PreparedStatement updateWarekorb = Project.getInstance().getConnection().prepareStatement(s);
                updateWarekorb.setString(1, data1.get("Warenkorb.bestellstatus").toString());
                updateWarekorb.execute();
            }
            else throw new SQLException(getClass().getName() + ": Insuffiziente Rechte");
        }
    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {
        Project.getInstance().getConnection().getRawConnection().setAutoCommit(false);
        if (isLieferabo(data)){
            String  s = "DELETE FROM Lieferabo WHERE warenkorbID=" + data.get("Warenkorb.ID").toString();

            PreparedStatement deleteLieferabo = Project.getInstance().getConnection().prepareStatement(s);
            deleteLieferabo.execute();
        }
        String s = "DELETE FROM Warenkorb WHERE Warenkorb.ID =" + Integer.parseInt(data.get("Warenkorb.ID").toString());

        PreparedStatement deleteWarenkorb = Project.getInstance().getConnection().prepareStatement(s);
        deleteWarenkorb.execute();

        Project.getInstance().getConnection().getRawConnection().commit();
        Project.getInstance().getConnection().getRawConnection().setAutoCommit(true);
    }
    private boolean isLieferabo(Data data) throws SQLException {
        String s = "SELECT COUNT(*) FROM Lieferabo WHERE warenkorbID=" + data.get("Warenkorb.ID").toString();

        return Project.getInstance().getConnection().executeQuery(s).getInt(1) > 0;
    }
}
