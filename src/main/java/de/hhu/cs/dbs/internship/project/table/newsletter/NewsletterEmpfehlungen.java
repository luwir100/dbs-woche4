package de.hhu.cs.dbs.internship.project.table.newsletter;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;
import de.hhu.cs.dbs.internship.project.Project;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewsletterEmpfehlungen extends Table{
    @Override
    public String getSelectQueryForTableWithFilter(String s) throws SQLException {
        if(s == null || s.isEmpty()){
            return  "SELECT newsletterID as Newsletter, betreff, artikelID, bezeichnung as Artikel " +
                    "FROM enthaelt JOIN Artikel ON enthaelt.artikelID=Artikel.ID JOIN Newsletter ON Newsletter.ID=newsletterID";
        }
        else return "SELECT newsletterID as Newsletter, betreff, artikelID, bezeichnung as Artikel " +
                    "FROM enthaelt JOIN Artikel ON enthaelt.artikelID=Artikel.ID JOIN Newsletter ON Newsletter.ID=newsletterID " +
                    "WHERE betreff LIKE '%" + s + "%'";
    }

    @Override
    public String getSelectQueryForRowWithData(Data data) throws SQLException {
        return  "SELECT newsletterID, artikelID FROM enthaelt WHERE newsletterID=" + data.get("enthaelt.Newsletter") +
                " AND artikelID=" + data.get("enthaelt.artikelID");
    }

    @Override
    public void insertRowWithData(Data data) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            String s ="INSERT INTO enthaelt VALUES(?,?)";

            PreparedStatement insertEnthaelt = Project.getInstance().getConnection().prepareStatement(s);
            insertEnthaelt.setInt(1, Integer.parseInt(data.get("enthaelt.newsletterID").toString()));
            insertEnthaelt.setInt(2, Integer.parseInt(data.get("enthaelt.artikelID").toString()));
            insertEnthaelt.execute();
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }

    @Override
    public void updateRowWithData(Data oldData, Data newData) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            String s = "UPDATE enthaelt SET newsletterID=?, artikelID=? WHERE newsletterID=? AND artikelID=?";

            PreparedStatement updateEnthaelt = Project.getInstance().getConnection().prepareStatement(s);
            updateEnthaelt.setInt(1, Integer.parseInt(newData.get("enthaelt.newsletterID").toString()));
            updateEnthaelt.setInt(2, Integer.parseInt(newData.get("enthaelt.artikelID").toString()));
            updateEnthaelt.setInt(3, Integer.parseInt(oldData.get("enthaelt.newsletterID").toString()));
            updateEnthaelt.setInt(4, Integer.parseInt(oldData.get("enthaelt.artikelID").toString()));
            updateEnthaelt.execute();
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            String s = "DELETE FROM enthaelt WHERE newsletterID=? AND artikelID=?";

            PreparedStatement deleteEnthaelt = Project.getInstance().getConnection().prepareStatement(s);
            deleteEnthaelt.setInt(1, Integer.parseInt(data.get("enthaelt.Newsletter").toString()));
            deleteEnthaelt.setInt(2, Integer.parseInt(data.get("enthaelt.artikelID").toString()));
            deleteEnthaelt.execute();
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }
}
