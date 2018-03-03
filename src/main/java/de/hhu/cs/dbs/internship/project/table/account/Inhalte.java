package de.hhu.cs.dbs.internship.project.table.account;

import com.alexanderthelen.applicationkit.database.Data;
import com.alexanderthelen.applicationkit.database.Table;
import de.hhu.cs.dbs.internship.project.Project;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Inhalte extends Table {
    @Override
    public String getSelectQueryForTableWithFilter(String s) throws SQLException {
        return "SELECT warenkorbID, inw.angebotID as AngebotID, Artikel.bezeichnung as Artikel, inw.anbieterBezeichnung as Anbieter, anzahl, (preis*anzahl) as Preis " +
                "FROM Warenkorb JOIN inw ON Warenkorb.ID= inw.warenkorbID JOIN Angebot ON Angebot.ID = inw.angebotID " +
                "JOIN Artikel ON Angebot.artikelID = Artikel.ID JOIN bietet ON Angebot.ID = bietet.angebotID AND inw.anbieterBezeichnung = bietet.anbieterBezeichnung " +
                "WHERE Warenkorb.kundeEmail ='" + Project.getInstance().getData().get("email").toString() + "'";
    }

    @Override
    public String getSelectQueryForRowWithData(Data data) throws SQLException {
        return "SELECT warenkorbID, inw.angebotID as AngebotID, inw.anbieterBezeichnung as Anbieter, anzahl " +
                "FROM inw " +
                "WHERE inw.warenkorbID =" + data.get("inw.warenkorbID") + " AND inw.angebotID =" + data.get("inw.AngebotID") +
                " AND inw.anbieterBezeichnung ='" + data.get("inw.Anbieter") + "'";
    }

    @Override
    public void insertRowWithData(Data data) throws SQLException {
        // check if still being processed
        if(!hasStatus(data,"in Bearbeitung")){
            throw new SQLException(getClass().getName() + ": Warenkorb bereits versandfertig/versendet.");
        }
        //  Check if current offer exists and is available in demanded amount
        int available = getAvailable(data);
        if (Integer.parseInt(data.get("inw.anzahl").toString()) > available || available == 0) {
            throw new SQLException(getClass().getName() + ": Angebot nicht ausreichend vorhanden");
        }
        // deny changes if not owned by current user
        if (isNotOwner(data)) {
            throw new SQLException(getClass().getName() + ": fremder Warenkorb: Zugriff verweigert.");
        }

        else {
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(false);

            String s = "INSERT INTO inw VALUES(?,?,?,?)";

            PreparedStatement insertINW = Project.getInstance().getConnection().prepareStatement(s);
            insertINW.setInt(1, Integer.parseInt(data.get("inw.warenkorbID").toString()));
            insertINW.setInt(2, Integer.parseInt(data.get("inw.AngebotID").toString()));
            insertINW.setString(3, data.get("inw.Anbieter").toString());
            insertINW.setInt(4, Integer.parseInt(data.get("inw.anzahl").toString()));

            insertINW.execute();

            s = "UPDATE bietet SET bestand =? WHERE bietet.anbieterBezeichnung = ? AND bietet.angebotID =?";
            PreparedStatement updateAnzahl = Project.getInstance().getConnection().prepareStatement(s);
            updateAnzahl.setInt(1, available - Integer.parseInt(data.get("inw.anzahl").toString()));
            updateAnzahl.setString(2, data.get("inw.Anbieter").toString());
            updateAnzahl.setInt(3, Integer.parseInt(data.get("inw.AngebotID").toString()));

            updateAnzahl.execute();

            Project.getInstance().getConnection().getRawConnection().commit();
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(true);
        }
    }

    @Override
    public void updateRowWithData(Data oldData, Data newData) throws SQLException {
        // check if still being processed
        if(!hasStatus(oldData,"in Bearbeitung")){
            throw new SQLException(getClass().getName() + ": Warenkorb bereits versandfertig/versendet.");
        }
        // check offer availability/existence
        int available = getAvailable(newData);
        if(Integer.parseInt(newData.get("inw.anzahl").toString()) > available || available == 0){
            throw new SQLException(getClass().getName() + ": Angebot nicht ausreichend verfügbar.");
        }
        // check if user is owner of new inw warenkorb
        if (isNotOwner(newData)){
            throw new SQLException(getClass().getName() + ": fremder Warenkorb: Zugriff verweigert.");
        }
        else{
            deleteRowWithData(oldData);
            insertRowWithData(newData);

            /*
            String s =  "UPDATE inw SET warenkorbID=?, angebotID=?, anbieterBezeichnung=?, anzahl=? " +
                        "WHERE warenkorbID=? AND angebotID=? AND anbieterBezeichnung=?";

            PreparedStatement updateINW = Project.getInstance().getConnection().prepareStatement(s);
            updateINW.setInt(1, Integer.parseInt(newData.get("inw.warenkorbID").toString()));
            updateINW.setInt(2, Integer.parseInt(newData.get("inw.AngebotID").toString()));
            updateINW.setString(3, newData.get("inw.Anbieter").toString());
            updateINW.setInt(4, Integer.parseInt(newData.get("inw.anzahl").toString()));
            updateINW.setInt(5, Integer.parseInt(oldData.get("inw.warenkorbID").toString()));
            updateINW.setInt(6, Integer.parseInt(oldData.get("inw.AngebotID").toString()));
            updateINW.setString(7, oldData.get("inw.Anbieter").toString());

            updateINW.execute();*/
        }
    }

    @Override
    public void deleteRowWithData(Data data) throws SQLException {
        //  check if Warenkorb is still to be processed
        if(!hasStatus(data,"in Bearbeitung")){
            throw new SQLException(getClass().getName() + ": Warenkorb bereits versendet.");
        }
        else{
            //  Only very own inw is visible and therefore reachable since bound to Data object. Check redundant
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(false);

            String s = "DELETE FROM inw WHERE inw.angebotID =? AND inw.anbieterBezeichnung=? AND inw.warenkorbID=?";

            PreparedStatement deleteINW = Project.getInstance().getConnection().prepareStatement(s);
            deleteINW.setInt(1, Integer.parseInt(data.get("inw.AngebotID").toString()));
            deleteINW.setInt(3, Integer.parseInt(data.get("inw.warenkorbID").toString()));
            deleteINW.setString(2, data.get("inw.Anbieter").toString());

            deleteINW.execute();

            s = "UPDATE bietet SET bestand = (bestand + ?) WHERE anbieterBezeichnung=? AND angebotID=?";

            PreparedStatement updateAnzahl = Project.getInstance().getConnection().prepareStatement(s);
            updateAnzahl.setInt(1, Integer.parseInt(data.get("inw.anzahl").toString()));
            updateAnzahl.setString(2, data.get("inw.Anbieter").toString());
            updateAnzahl.setInt(3, Integer.parseInt(data.get("inw.AngebotID").toString()));

            updateAnzahl.execute();

            Project.getInstance().getConnection().getRawConnection().commit();
            Project.getInstance().getConnection().getRawConnection().setAutoCommit(true);
        }
    }
    private boolean isNotOwner(Data data) throws SQLException {
        String s = "SELECT kundeEmail FROM Warenkorb WHERE ID=?";
        PreparedStatement getOwner = Project.getInstance().getConnection().prepareStatement(s);

        getOwner.setInt(1, Integer.parseInt(data.get("inw.warenkorbID").toString()));
        getOwner.execute();

        return !getOwner.getResultSet().getString(1).equals(Project.getInstance().getData().get("email").toString());
    }
    private int getAvailable(Data data) throws SQLException {
        String s = "SELECT bestand FROM bietet WHERE anbieterBezeichnung=? AND angebotID=?";
        PreparedStatement checkBestand = Project.getInstance().getConnection().prepareStatement(s);
        checkBestand.setString(1, data.get("inw.Anbieter").toString());
        checkBestand.setInt(2, Integer.parseInt(data.get("inw.AngebotID").toString()));
        checkBestand.execute();

        ResultSet resultSet = checkBestand.getResultSet();
        if(resultSet.isClosed()) return 0;
        return resultSet.getInt(1);
    }
    private boolean hasStatus(Data data, String status) throws SQLException {
        String s =  "SELECT bestellstatus FROM Warenkorb WHERE ID=?";
        PreparedStatement checkStatus = Project.getInstance().getConnection().prepareStatement(s);

        checkStatus.setInt(1, Integer.parseInt(data.get("inw.warenkorbID").toString()));
        checkStatus.execute();

        return checkStatus.getResultSet().getString(1).equals(status);
    }
}