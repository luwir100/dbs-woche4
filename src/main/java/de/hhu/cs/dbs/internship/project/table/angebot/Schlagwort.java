package de.hhu.cs.dbs.internship.project.table.angebot;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;
import de.hhu.cs.dbs.internship.project.Project;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Schlagwort extends Table{
    @Override
    public String getSelectQueryForTableWithFilter(String s) throws SQLException {
        if(s == null || s.isEmpty()){
            return  "SELECT Schlagwort.wort AS Schlagwort, besitzt.artikelID,bezeichnung AS Artikel " +
                    "FROM Schlagwort LEFT JOIN besitzt ON besitzt.schlagwortWort=Schlagwort.wort " +
                    "LEFT JOIN Artikel ON besitzt.artikelID=Artikel.ID";
        }
        return "SELECT Schlagwort.wort AS Schlagwort, besitzt.artikelID,bezeichnung AS Artikel " +
                "FROM Schlagwort LEFT JOIN besitzt ON besitzt.schlagwortWort=Schlagwort.wort " +
                "LEFT JOIN Artikel ON besitzt.artikelID=Artikel.ID " +
                "WHERE Schlagwort.wort LIKE '%" + s + "%'";
    }

    @Override
    public String getSelectQueryForRowWithData(Data data) throws SQLException {
        return  "SELECT Schlagwort.wort AS Schlagwort, besitzt.artikelID, bezeichnung AS Artikel " +
                "FROM Schlagwort LEFT JOIN besitzt ON besitzt.schlagwortWort=Schlagwort.wort " +
                "LEFT JOIN Artikel ON besitzt.artikelID=Artikel.ID " +
                "WHERE Schlagwort.wort='" + data.get("Schlagwort.Schlagwort") + "'";
    }

    @Override
    public void insertRowWithData(Data data) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(false);
            String s;
            if(!schlagwortExists(data)){
                s = "INSERT INTO Schlagwort VALUES('" + data.get("Schlagwort.Schlagwort") + "')";
                PreparedStatement insertSchlagwort = Project.getInstance().getConnection().prepareStatement(s);
                insertSchlagwort.execute();
            }
            if(data.get("besitzt.artikelID") != null){
                s = "INSERT INTO besitzt VALUES(?,'" + data.get("Schlagwort.Schlagwort") +"')";

                PreparedStatement insertBesitzt = Project.getInstance().getConnection().prepareStatement(s);
                insertBesitzt.setInt(1, Integer.parseInt(data.get("besitzt.artikelID").toString()));
                insertBesitzt.execute();
            }
            Project.getInstance().getConnection().getRawConnection().commit();
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(true);
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }

    @Override
    public void updateRowWithData(Data oldData, Data newData) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(false);

            String s;
            if(oldData.get("besitzt.artikelID") != null && newData.get("besitzt.artikelID") != null){
                s = "UPDATE besitzt SET artikelID=?,schlagwortWort=? WHERE schlagwortWort=? AND artikelID=?";
                PreparedStatement updateBesitzt = Project.getInstance().getConnection().prepareStatement(s);
                updateBesitzt.setInt(1, Integer.parseInt(newData.get("besitzt.artikelID").toString()));
                updateBesitzt.setString(2, newData.get("Schlagwort.Schlagwort").toString());
                updateBesitzt.setString(3, oldData.get("Schlagwort.Schlagwort").toString());
                updateBesitzt.setInt(4, Integer.parseInt(oldData.get("besitzt.artikelID").toString()));
                updateBesitzt.execute();
            }
            Project.getInstance().getConnection().getRawConnection().commit();
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(true);
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            String s;
            if(schlagwortDeletable(data)){
                s = "DELETE FROM Schlagwort WHERE wort ='" + data.get("Schlagwort.Schlagwort").toString() +"'";

                PreparedStatement deleteSchlagwort = Project.getInstance().getConnection().prepareStatement(s);
                deleteSchlagwort.execute();
            }
            else{
                s = "DELETE FROM besitzt WHERE artikelID=? AND schlagwortWort=?";

                PreparedStatement deleteBesitzt = Project.getInstance().getConnection().prepareStatement(s);
                deleteBesitzt.setInt(1, Integer.parseInt(data.get("besitzt.artikelID").toString()));
                deleteBesitzt.setString(2, data.get("Schlagwort.Schlagwort").toString());
                deleteBesitzt.execute();
            }
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }
    private boolean schlagwortExists(Data data) throws SQLException {
        int i = Project.getInstance().getConnection().executeQuery("SELECT COUNT(*) FROM Schlagwort " +
                "WHERE wort='" + data.get("Schlagwort.Schlagwort") + "'").getInt(1);
        return i > 0;
    }
    private boolean schlagwortDeletable(Data data) throws SQLException {
        int i = Project.getInstance().getConnection().executeQuery("SELECT COUNT(*) FROM Schlagwort JOIN besitzt ON " +
                "Schlagwort.wort=besitzt.schlagwortWort WHERE Schlagwort.wort='" + data.get("Schlagwort.Schlagwort").toString()
                + "'").getInt(1);

        return i == 0;
    }
}
