package de.hhu.cs.dbs.internship.project.table.angebot;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;
import de.hhu.cs.dbs.internship.project.Project;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Artikel extends Table {
    @Override
    public String getSelectQueryForTableWithFilter(String s) throws SQLException {
        if(s == null || s.isEmpty()){
            return  "SELECT Artikel.ID,bezeichnung,beschreibung,bild " +
                    "FROM Artikel LEFT JOIN Bild ON Artikel.ID=Bild.artikelID";
        }
        return "SELECT Artikel.ID,bezeichnung,beschreibung,bild " +
                "FROM Artikel LEFT JOIN Bild ON Artikel.ID=Bild.artikelID " +
                "WHERE bezeichnung LIKE '%" + s + "%'";
    }

    @Override
    public String getSelectQueryForRowWithData(Data data) throws SQLException {
        return  "SELECT Artikel.ID,bezeichnung,beschreibung,bild,name AS Bildname " +
                "FROM Artikel LEFT JOIN Bild ON Artikel.ID=Bild.artikelID " +
                "WHERE Artikel.ID=" + data.get("Artikel.ID");
    }

    @Override
    public void insertRowWithData(Data data) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(false);
            int newArtikelID = Project.getInstance().getConnection().executeQuery("SELECT MAX(ID) FROM Artikel").getInt(1) + 1;

            String s = "INSERT INTO Artikel VALUES(" + newArtikelID + ",?,?)";
            PreparedStatement insertArtikel = Project.getInstance().getConnection().prepareStatement(s);
            insertArtikel.setString(1, data.get("Artikel.bezeichnung").toString());
            insertArtikel.setString(2, data.get("Artikel.beschreibung").toString());
            insertArtikel.execute();

            if(data.get("Bild.bild") != null && data.get("Bild.Bildname") != null &&
                    !data.get("Bild.Bildname").toString().isEmpty()){
                int newBildID = Project.getInstance().getConnection().executeQuery("SELECT MAX(ID) FROM Bild").getInt(1) + 1;
                s = "INSERT INTO Bild VALUES(" + newBildID + "," + newArtikelID + ",?,?)";

                PreparedStatement insertBild = Project.getInstance().getConnection().prepareStatement(s);
                insertBild.setString(1, data.get("Bild.Bildname").toString());
                insertBild.setBytes(2, (byte[]) data.get("Bild.bild"));
                insertBild.execute();
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
            if(newData.get("Bild.bild") != null && newData.get("Bild.Bildname") != null &&
                    !newData.get("Bild.Bildname").toString().isEmpty()){
                if(oldData.get("Bild.bild") == null){
                    int newID =  Project.getInstance().getConnection().executeQuery("SELECT MAX(ID) FROM Bild").getInt(1) +1;

                    s = "INSERT INTO Bild VALUES(" + newID + "," + oldData.get("Artikel.ID") + ",?,?)";
                    PreparedStatement insertBild = Project.getInstance().getConnection().prepareStatement(s);
                    insertBild.setString(1, newData.get("Bild.Bildname").toString());
                    insertBild.setBytes(2, (byte[]) newData.get("Bild.bild"));
                    insertBild.execute();
                }
                else{
                    s = "UPDATE Bild SET name=?,bild=? WHERE artikelID=" + oldData.get("Artikel.ID");

                    PreparedStatement updateBild = Project.getInstance().getConnection().prepareStatement(s);
                    updateBild.setString(1, newData.get("Bild.Bildname").toString());
                    updateBild.setBytes(2, (byte[]) newData.get("Bild.bild"));

                    updateBild.execute();
                }
            }
            s = "UPDATE Artikel SET bezeichnung=?,beschreibung=? WHERE ID=" + oldData.get("Artikel.ID");
            PreparedStatement updateArtikel = Project.getInstance().getConnection().prepareStatement(s);
            updateArtikel.setString(1, newData.get("Artikel.bezeichnung").toString());
            updateArtikel.setString(2, newData.get("Artikel.beschreibung").toString());
            updateArtikel.execute();

            Project.getInstance().getConnection().getRawConnection().commit();
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(true);
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            String s  = "DELETE FROM Bild WHERE artikelID=" + data.get("Artikel.ID");
            PreparedStatement deleteBild = Project.getInstance().getConnection().prepareStatement(s);
            deleteBild.execute();

            s = "DELETE FROM Artikel WHERE ID=" + data.get("Artikel.ID");

            PreparedStatement deleteArtikel = Project.getInstance().getConnection().prepareStatement(s);
            deleteArtikel.execute();
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }
}
