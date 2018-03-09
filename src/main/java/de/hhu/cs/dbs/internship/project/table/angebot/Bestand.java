package de.hhu.cs.dbs.internship.project.table.angebot;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;
import de.hhu.cs.dbs.internship.project.Project;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Bestand extends Table {
    @Override
    public String getSelectQueryForTableWithFilter(String s) throws SQLException {
        if(s == null || s.isEmpty()){
            return "SELECT Angebot.ID, artikelID, bezeichnung, preis, anbieterBezeichnung AS Anbieter, bestand " +
                    "FROM Angebot JOIN Artikel ON Angebot.artikelID=Artikel.ID JOIN bietet ON angebotID=Angebot.ID";
        }
        return "SELECT Angebot.ID, artikelID, bezeichnung, preis, anbieterBezeichnung AS Anbieter, bestand " +
                "FROM Angebot JOIN Artikel ON Angebot.artikelID=Artikel.ID JOIN bietet ON angebotID=Angebot.ID " +
                "WHERE bezeichnung LIKE '%" + s + "%'";
    }

    @Override
    public String getSelectQueryForRowWithData(Data data) throws SQLException {
        return "SELECT anbieterBezeichnung as Anbieter, angebotID, bestand " +
                "FROM bietet " +
                "WHERE angebotID=" + data.get("Angebot.ID") + " AND anbieterBezeichnung='" + data.get("bietet.Anbieter") + "'";
    }

    @Override
    public void insertRowWithData(Data data) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            String s = "INSERT INTO bietet VALUES(?,?,?)";

            PreparedStatement insertBietet = Project.getInstance().getConnection().prepareStatement(s);
            insertBietet.setString(1, data.get("bietet.Anbieter").toString());
            insertBietet.setInt(2, Integer.parseInt(data.get("bietet.angebotID").toString()));
            insertBietet.setInt(3, Integer.parseInt(data.get("bietet.bestand").toString()));
            insertBietet.execute();
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }

    @Override
    public void updateRowWithData(Data oldData, Data newData) throws SQLException {
        // keine Entitäten sondern nur Beziehungen zwischen ihnen werden geändert. Also ist update so möglich
        deleteRowWithData(oldData);
        insertRowWithData(newData);
    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            String s = "DELETE FROM bietet WHERE anbieterBezeichnung=? AND angebotID=?";
            PreparedStatement deleteBietet = Project.getInstance().getConnection().prepareStatement(s);
            deleteBietet.setString(1, data.get("bietet.Anbieter").toString());
            deleteBietet.setInt(2, Integer.parseInt(data.get("bietet.angebotID").toString()));
            deleteBietet.execute();
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }
}
