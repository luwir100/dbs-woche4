package de.hhu.cs.dbs.internship.project.table.newsletter;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;
import de.hhu.cs.dbs.internship.project.Project;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Newsletter extends Table{
    @Override
    public String getSelectQueryForTableWithFilter(String s) throws SQLException {
        if(s != null && !s.isEmpty()) return "SELECT ID,betreff,datum FROM Newsletter WHERE betreff LIKE '%" + s + "%'";
        return "SELECT ID,betreff,text,datum FROM Newsletter";
    }

    @Override
    public String getSelectQueryForRowWithData(Data data) throws SQLException {
        if(data.get("Newsletter.ID")==null)return "SELECT NULL AS betreff, NULL AS text";
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            return "SELECT ID,betreff,text,datum,angestellterKundeEmail as Verfasser FROM Newsletter WHERE Newsletter.ID=" + data.get("Newsletter.ID").toString();
        }
        return "SELECT ID,betreff,text,datum FROM Newsletter WHERE Newsletter.ID=" + data.get("Newsletter.ID").toString();
    }

    @Override
    public void insertRowWithData(Data data) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            int newsID = Project.getInstance().getConnection().executeQuery("SELECT COUNT(*) FROM Newsletter").getInt(1) +1;
            System.out.println(data);

            String s = "INSERT INTO Newsletter VALUES(?,?,?,?,date('now'))";
            PreparedStatement insertNewsletter = Project.getInstance().getConnection().prepareStatement(s);
            insertNewsletter.setInt(1, newsID);
            insertNewsletter.setString(2, Project.getInstance().getData().get("email").toString());
            insertNewsletter.setString(3, data.get(".betreff").toString());
            insertNewsletter.setString(4, data.get(".text").toString());

            insertNewsletter.execute();
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }

    @Override
    public void updateRowWithData(Data oldData, Data newData) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            String s =  "UPDATE Newsletter SET angestellterKundeEmail = ?, betreff = ?, text = ? " +
                        "WHERE ID=" + oldData.get("Newsletter.ID");

            PreparedStatement updateNewsletter = Project.getInstance().getConnection().prepareStatement(s);
            updateNewsletter.setString(1, Project.getInstance().getData().get("email").toString());
            updateNewsletter.setString(2, newData.get("Newsletter.betreff").toString());
            updateNewsletter.setString(3, newData.get("Newsletter.text").toString());
            updateNewsletter.execute();
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            String s = "DELETE FROM Newsletter WHERE ID=" + data.get("Newsletter.ID");

            PreparedStatement deleteNewsletter = Project.getInstance().getConnection().prepareStatement(s);
            deleteNewsletter.execute();
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }
}
