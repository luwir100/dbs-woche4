package de.hhu.cs.dbs.internship.project.table.account;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;
import de.hhu.cs.dbs.internship.project.Project;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class Premiumstatus extends Table{
    @Override
    public String getSelectQueryForTableWithFilter(String s) throws SQLException {
        if (Project.getInstance().getData().get("rights").toString().equals("angestellter")) {
            if (s == null || s.isEmpty()) {
                return "SELECT * FROM Premiumkunde";
            } else {
                return "SELECT * FROM Premiumkunde WHERE kundeEmail LIKE '%" + s + "%'";
            }
        }
        else return "SELECT * FROM Premiumkunde WHERE kundeEmail='" + Project.getInstance().getData().get("email").toString() + "'";
    }

    @Override
    public String getSelectQueryForRowWithData(Data data) throws SQLException {
        return "SELECT * FROM Premiumkunde WHERE kundeEmail ='" + data.get("Premiumkunde.kundeEmail") + "'";
    }

    @Override
    public void insertRowWithData(Data data) throws SQLException {
        //check if one entry is already present
        if (hasEntry()) throw new SQLException(getClass().getName() + ": bereits Premiumkunde");
        else {
            String s = "INSERT INTO Premiumkunde VALUES(?,?,?,date('now'),'in Bearbeitung')";
            System.out.println(data.get("Premiumkunde.studentenausweis"));
            System.out.println(data.get("Premiumkunde.studentenausweis").toString());

            PreparedStatement insertPremiumkunde = Project.getInstance().getConnection().prepareStatement(s);
            insertPremiumkunde.setString(1, Project.getInstance().getData().get("email").toString());
            insertPremiumkunde.setBytes(2, (byte[]) data.get("Premiumkunde.studentenausweis"));
            insertPremiumkunde.setInt(3, Integer.parseInt(data.get("Premiumkunde.gebuehr").toString()));

            insertPremiumkunde.execute();
        }
    }

    @Override
    public void updateRowWithData(Data oldData, Data newData) throws SQLException {
        if (Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            String s = "UPDATE Premiumkunde SET studentenausweis=?,gebuehr=?,ablaufdatum=?,status=? WHERE kundeEmail=?";

            String updatedTime;
            if(newData.get("Premiumkunde.ablaufdatum") == null || newData.get("Premiumkunde.ablaufdatum").toString().isEmpty()){
                updatedTime = LocalDate.now().plusMonths(6).toString();
                System.out.println(updatedTime);
            }
            else updatedTime = newData.get("Premiumkunde.ablaufdatum").toString();
            PreparedStatement updatePremiumkunde = Project.getInstance().getConnection().prepareStatement(s);
            updatePremiumkunde.setBytes(1, (byte[]) newData.get("Premiumkunde.studentenausweis"));
            updatePremiumkunde.setInt(2, Integer.parseInt(newData.get("Premiumkunde.gebuehr").toString()));
            updatePremiumkunde.setString(3, updatedTime);
            updatePremiumkunde.setString(4, newData.get("Premiumkunde.status").toString());
            updatePremiumkunde.setString(5, oldData.get("Premiumkunde.kundeEmail").toString());

            updatePremiumkunde.execute();
        }
        else{
            if(oldData.get("Premiumkunde.status").toString().equals("in Bearbeitung")){
                String s = "UPDATE Premiumkunde SET studentenausweis=?, gebuehr=?";

                PreparedStatement updatePremiumkunde = Project.getInstance().getConnection().prepareStatement(s);
                updatePremiumkunde.setBytes(1, (byte[]) newData.get("Premiumkunde.studentenausweis"));
                updatePremiumkunde.setInt(2, Integer.parseInt(newData.get("Premiumkunde.gebuehr").toString()));

                updatePremiumkunde.execute();
            }
            else throw new SQLException(getClass().getName() + ": Premiumstatus bereits aktiv");
        }
    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {
        String s = "DELETE FROM Premiumkunde WHERE kundeEmail=?";

        PreparedStatement deletePremiumkunde = Project.getInstance().getConnection().prepareStatement(s);
        deletePremiumkunde.setString(1, data.get("Premiumkunde.kundeEmail").toString());

        deletePremiumkunde.execute();
    }
    private boolean hasEntry() throws SQLException {
        String s = "SELECT COUNT(*) FROM Premiumkunde WHERE kundeEmail=?";

        PreparedStatement countEntry = Project.getInstance().getConnection().prepareStatement(s);
        countEntry.setString(1, Project.getInstance().getData().get("email").toString());
        countEntry.execute();

        return countEntry.getResultSet().getInt(1) > 0;
    }
}
