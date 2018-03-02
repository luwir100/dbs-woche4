package de.hhu.cs.dbs.internship.project.table.newsletter;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;
import de.hhu.cs.dbs.internship.project.Project;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Anmeldungen extends Table {
    @Override
    public String getSelectQueryForTableWithFilter(String s) throws SQLException {
        return  "SELECT ID,betreff,text FROM Newsletter JOIN angemeldet ON ID=newsletterID " +
                "WHERE kundeEmail='" + Project.getInstance().getData().get("email").toString() + "'";
    }

    @Override
    public String getSelectQueryForRowWithData(Data data) throws SQLException {
        if(data.get("Newsletter.ID")==null) return "SELECT NULL AS anmelden_für";
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            return "SELECT ID,betreff,text,datum,angestellterKundeEmail as Verfasser FROM Newsletter WHERE Newsletter.ID=" + data.get("Newsletter.ID").toString();
        }
        return "SELECT ID,betreff,text,datum FROM Newsletter WHERE Newsletter.ID=" + data.get("Newsletter.ID").toString();
    }

    @Override
    public void insertRowWithData(Data data) throws SQLException {
        String s = "INSERT INTO angemeldet VALUES(?,?)";

        PreparedStatement insertAnmeldung = Project.getInstance().getConnection().prepareStatement(s);
        insertAnmeldung.setString(1, Project.getInstance().getData().get("email").toString());
        insertAnmeldung.setInt(2, Integer.parseInt(data.get(".anmelden_für").toString()));

        insertAnmeldung.execute();
    }

    @Override
    public void updateRowWithData(Data oldData, Data newData) throws SQLException {
        String s = "UPDATE angemeldet SET newsletterID=? WHERE kundeEmail=? AND newsletterID=?";

        PreparedStatement updateAnmeldung = Project.getInstance().getConnection().prepareStatement(s);
        updateAnmeldung.setInt(1, (int) newData.get("angemeldet.newsletterID"));
        updateAnmeldung.setString(2, Project.getInstance().getData().get("email").toString());
        updateAnmeldung.setInt(3, (int) oldData.get("angemeldet.newsletterID"));

        updateAnmeldung.execute();
    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {
        String s = "DELETE FROM angemeldet WHERE kundeEmail=? AND newsletterID=?";

        PreparedStatement deleteAnmeldung = Project.getInstance().getConnection().prepareStatement(s);
        deleteAnmeldung.setString(1, Project.getInstance().getData().get("email").toString());
        deleteAnmeldung.setInt(2, (int) data.get("Newsletter.ID"));

        deleteAnmeldung.execute();
    }
}
