package de.hhu.cs.dbs.internship.project.table.angebot;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;
import de.hhu.cs.dbs.internship.project.Project;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Angebot extends Table {
    @Override
    public String getSelectQueryForTableWithFilter(String s) throws SQLException {
        if(s == null || s.isEmpty())
            return  "SELECT Angebot.ID,Angebot.artikelID,preis,bezeichnung,beschreibung,bild " +
                    "FROM Angebot JOIN Artikel ON Angebot.artikelID=Artikel.ID LEFT JOIN Bild ON Artikel.ID=Bild.artikelID";
        else return "SELECT Angebot.ID,Angebot.artikelID,preis,bezeichnung,beschreibung,bild " +
                    "FROM Angebot JOIN Artikel ON Angebot.artikelID=Artikel.ID LEFT JOIN Bild ON Artikel.ID=Bild.artikelID " +
                    "WHERE bezeichnung LIKE '%" + s + "%'";
    }

    @Override
    public String getSelectQueryForRowWithData(Data data) throws SQLException {
        return "SELECT * FROM Angebot WHERE ID=" + data.get("Angebot.ID");
    }

    @Override
    public void insertRowWithData(Data data) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            int newID = Project.getInstance().getConnection().executeQuery("SELECT MAX(ID) FROM Angebot").getInt(1) +1;

            String s = "INSERT INTO Angebot VALUES(" + newID + ",?,?)";
            PreparedStatement insertAngebot = Project.getInstance().getConnection().prepareStatement(s);
            insertAngebot.setString(1, data.get("Angebot.artikelID").toString());
            insertAngebot.setString(2, data.get("Angebot.preis").toString());
            insertAngebot.execute();
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }

    @Override
    public void updateRowWithData(Data oldData, Data newData) throws SQLException {
        deleteRowWithData(oldData);
        insertRowWithData(newData);
    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            String s ="DELETE FROM Angebot WHERE ID=" + data.get("Angebot.ID");
            PreparedStatement deleteAngebot = Project.getInstance().getConnection().prepareStatement(s);
            deleteAngebot.execute();
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }
    private boolean artikleHasBild(int artikelID) throws SQLException {
        String s = "SELECT COUNT(*) FROM Bild WHERE artikelID=" + artikelID;

        return Project.getInstance().getConnection().executeQuery(s).getInt(1) > 0;
    }
}
