package de.hhu.cs.dbs.internship.project.table.angebot;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;
import de.hhu.cs.dbs.internship.project.Project;

import java.sql.Blob;
import java.sql.PreparedStatement;
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
        /*
        try{
            int id = Integer.parseInt(data.get("Artikel.ID").toString());
            String s = "SELECT COUNT(*) FROM Bild WHERE Bild.artikelID =" + id;
            PreparedStatement countBild = Project.getInstance().getConnection().prepareStatement(s);
            countBild.execute();
            if (countBild.getResultSet().getInt(1) > 0){
                return "SELECT bezeichnung,preis,beschreibung,bild,anbieterBezeichnung as Anbieter,bestand " +
                        "FROM Artikel JOIN Angebot ON Artikel.ID=Angebot.artikelID " +
                        "JOIN Bild ON Artikel.ID=Bild.artikelID JOIN bietet ON Angebot.ID=bietet.angebotID " +
                        "WHERE Artikel.ID =" + id;
            }
            else{
                return "SELECT bezeichnung,preis,beschreibung,anbieterBezeichnung as Anbieter,bestand " +
                        "FROM Artikel JOIN Angebot ON Artikel.ID=Angebot.artikelID JOIN bietet ON Angebot.ID=bietet.angebotID " +
                        "WHERE Artikel.ID =" + id;
            }
        }
        /*catch (NullPointerException e){
            return "SELECT NULL AS bezeichnung, NULL AS preis, NULL AS beschreibung, NULL as bild, NULL AS Anbieter, NULL as bestand " +
                    "FROM Artikel JOIN Angebot ON Artikel.ID=Angebot.artikelID " +
                    "JOIN Bild ON Artikel.ID=Bild.artikelID JOIN bietet ON Angebot.ID=bietet.angebotID";
        }*/
        return "SELECT bezeichnung,preis,beschreibung,bild,anbieterBezeichnung as Anbieter,bestand " +
                "FROM Artikel JOIN Angebot ON Artikel.ID=Angebot.artikelID " +
                "JOIN Bild ON Artikel.ID=Bild.artikelID JOIN bietet ON Angebot.ID=bietet.angebotID " +
                "WHERE Artikel.ID =" + data.get("Artikel.ID");
        }

    @Override
    public void insertRowWithData(Data data) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(false);
            int bildID = Project.getInstance().getConnection().executeQuery("SELECT COUNT(*) FROM Bild").getInt(1) +1;
            int angebotID = Project.getInstance().getConnection().executeQuery("SELECT COUNT(*) FROM Angebot").getInt(1)+1;
            int artikelID = Project.getInstance().getConnection().executeQuery("SELECT COUNT(*) FROM Artikel").getInt(1)+1;

            String s = "INSERT INTO  VALUES(" + artikelID + ",?,?)";
            PreparedStatement insertArtikel = Project.getInstance().getConnection().prepareStatement(s);
            insertArtikel.setString(1, data.get("Artikel.bezeichnung").toString());
            insertArtikel.setString(2, data.get("Artikel.beschreibung").toString());
            insertArtikel.execute();

            if(!(data.get("Bild.bild") == null)){
                s = "INSERT INTO Bild VALUES(" + bildID + "," + artikelID + ",?,?)";
                PreparedStatement insertBild = Project.getInstance().getConnection().prepareStatement(s);
                insertArtikel.setString(1, data.get("Artikel.bezeichnung").toString());
                insertArtikel.setBlob(2, (Blob) data.get("Bild.bild"));
                insertArtikel.execute();
            }
            s = "INSERT INTO Angebot VALUES (" + angebotID +"," + artikelID + ",?)";
            PreparedStatement insertAngebot = Project.getInstance().getConnection().prepareStatement(s);
            insertAngebot.setInt(1, Integer.parseInt(data.get("Angebot.preis").toString()));
            insertAngebot.execute();

            Project.getInstance().getConnection().getRawConnection().commit();
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(true);
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }

    @Override
    public void updateRowWithData(Data data, Data data1) throws SQLException {

    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {

    }
}
