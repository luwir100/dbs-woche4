package de.hhu.cs.dbs.internship.project.table.angebot;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;

import java.sql.SQLException;

public class Schlagwortsuche extends Table{
    @Override
    public String getSelectQueryForTableWithFilter(String s) throws SQLException {
        if(s == null || s.isEmpty()){
            return  "SELECT Artikel.ID,bezeichnung,beschreibung,bild " +
                    "FROM Artikel LEFT JOIN Bild ON Artikel.ID=Bild.artikelID LEFT JOIN besitzt ON Artikel.ID=besitzt.artikelID";
        }
        String schlagworte[] = s.split(",");
        String match ="";
        for(String wort : schlagworte){
            match = match.concat(" AND Artikel.ID IN (SELECT artikelID FROM besitzt WHERE schlagwortWort LIKE '%" + wort + "%')");
        }
        return  "SELECT DISTINCT Artikel.ID,bezeichnung,beschreibung,bild " +
                "FROM Artikel LEFT JOIN Bild ON Artikel.ID=Bild.artikelID LEFT JOIN besitzt ON Artikel.ID=besitzt.artikelID " +
                "WHERE 1=1" + match;
    }

    @Override
    public String getSelectQueryForRowWithData(Data data) throws SQLException {
        return  "SELECT Artikel.ID,bezeichnung,beschreibung,bild " +
                "FROM Artikel LEFT JOIN Bild ON Artikel.ID=Bild.artikelID LEFT JOIN besitzt ON Artikel.ID=besitzt.artikelID";
    }

    @Override
    public void insertRowWithData(Data data) throws SQLException {
        throw new SQLException(getClass().getName() + ": Hier ist nur Suche vorgesehen");
    }

    @Override
    public void updateRowWithData(Data data, Data data1) throws SQLException {
        throw new SQLException(getClass().getName() + ": Hier ist nur Suche vorgesehen");
    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {
        throw new SQLException(getClass().getName() + ": Hier ist nur Suche vorgesehen");
    }
}
