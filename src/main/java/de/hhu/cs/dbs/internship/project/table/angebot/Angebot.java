package de.hhu.cs.dbs.internship.project.table.angebot;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;

import java.sql.SQLException;

public class Angebot extends Table {
    @Override
    public String getSelectQueryForTableWithFilter(String s) throws SQLException {
        if(s == null || s.isEmpty()){
            return "SELECT Artikel.ID,bezeichnung,preis FROM Angebot JOIN Artikel ON Angebot.artikelID=Artikel.ID";
        }
        return "SELECT Artikel.ID,bezeichnung,preis FROM Angebot JOIN Artikel ON Angebot.artikelID=Artikel.ID WHERE bezeichnung LIKE '%" + s + "%'";
    }

    @Override
    public String getSelectQueryForRowWithData(Data data) throws SQLException {
        int id = Integer.parseInt(data.get("Artikel.ID").toString());
        return "SELECT bezeichnung,preis,beschreibung,bild " +
                "FROM Artikel JOIN Angebot ON Artikel.ID=Angebot.artikelID JOIN Bild ON Artikel.ID=Bild.artikelID " +
                "WHERE Artikel.ID =" + id;
    }

    @Override
    public void insertRowWithData(Data data) throws SQLException {

    }

    @Override
    public void updateRowWithData(Data data, Data data1) throws SQLException {

    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {

    }
}
