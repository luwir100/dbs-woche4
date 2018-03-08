package de.hhu.cs.dbs.internship.project.table.angebot;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;
import de.hhu.cs.dbs.internship.project.Project;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Empfehlungen extends Table {
    @Override
    public String getSelectQueryForTableWithFilter(String s) throws SQLException {
        if(s == null || s.isEmpty()) {
            return "SELECT artikel1ID, a1.bezeichnung AS Artikel, artikel2ID, a2.bezeichnung AS Empfehlung " +
                    "FROM empfiehlt JOIN Artikel a1 ON a1.ID=artikel1ID JOIN Artikel a2 ON a2.ID=artikel2ID";
        }
        else return "SELECT artikel1ID, a1.bezeichnung AS Artikel, artikel2ID, a2.bezeichnung AS Empfehlung " +
                    "FROM empfiehlt JOIN Artikel a1 ON a1.ID=artikel1ID JOIN Artikel a2 ON a2.ID=artikel2ID " +
                    "WHERE a1.bezeichnung LIKE '%" + s + "%'";
    }

    @Override
    public String getSelectQueryForRowWithData(Data data) throws SQLException {
        System.out.println(data);
        return  "SELECT * FROM empfiehlt WHERE artikel1ID=" + data.get("empfiehlt.artikel1ID") +
                " AND artikel2ID=" + data.get("empfiehlt.artikel2ID");
    }

    @Override
    public void insertRowWithData(Data data) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            String s = "INSERT INTO empfiehlt VALUES(?,?)";

            PreparedStatement insertEmpfiehlt = Project.getInstance().getConnection().prepareStatement(s);
            insertEmpfiehlt.setInt(1, Integer.parseInt(data.get("empfiehlt.artikel1ID").toString()));
            insertEmpfiehlt.setInt(2, Integer.parseInt(data.get("empfiehlt.artikel2ID").toString()));
            insertEmpfiehlt.execute();
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }

    @Override
    public void updateRowWithData(Data oldData, Data newData) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            String s = "UPDATE empfiehlt SET artikel1ID=?, artikel2ID=? WHERE artikel1ID=? AND artikel2ID=?";

            PreparedStatement updateEmpfiehlt = Project.getInstance().getConnection().prepareStatement(s);
            updateEmpfiehlt.setInt(1, Integer.parseInt(newData.get("empfiehlt.artikel1ID").toString()));
            updateEmpfiehlt.setInt(2, Integer.parseInt(newData.get("empfiehlt.artikel2ID").toString()));
            updateEmpfiehlt.setInt(3, Integer.parseInt(oldData.get("empfiehlt.artikel1ID").toString()));
            updateEmpfiehlt.setInt(4, Integer.parseInt(oldData.get("empfiehlt.artikel2ID").toString()));
            updateEmpfiehlt.execute();
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {
        if(Project.getInstance().getData().get("rights").toString().equals("angestellter")){
            String s = "DELETE FROM empfiehlt WHERE artikel1ID=? AND artikel2ID=?";

            PreparedStatement deleteEmpfiehlt = Project.getInstance().getConnection().prepareStatement(s);
            deleteEmpfiehlt.setInt(1, Integer.parseInt(data.get("empfiehlt.artikel1ID").toString()));
            deleteEmpfiehlt.setInt(2, Integer.parseInt(data.get("empfiehlt.artikel2ID").toString()));
            deleteEmpfiehlt.execute();
        }
        else throw new SQLException(getClass().getName() + ": insuffiziente Rechte");
    }
}
