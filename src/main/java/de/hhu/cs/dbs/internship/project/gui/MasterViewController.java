package de.hhu.cs.dbs.internship.project.gui;

import com.alexanderthelen.applicationkit.database.Table;
import com.alexanderthelen.applicationkit.gui.TableViewController;
import com.alexanderthelen.applicationkit.gui.ViewController;
import de.hhu.cs.dbs.internship.project.table.account.Account;
import de.hhu.cs.dbs.internship.project.table.account.Bestellung;
import de.hhu.cs.dbs.internship.project.table.angebot.Angebot;
import de.hhu.cs.dbs.internship.project.table.newsletter.Anmeldungen;
import de.hhu.cs.dbs.internship.project.table.newsletter.Newsletter;
import javafx.scene.control.TreeItem;

import java.io.IOException;
import java.util.ArrayList;

public class MasterViewController extends com.alexanderthelen.applicationkit.gui.MasterViewController {
    protected MasterViewController(String name) {
        super(name);
    }

    public static MasterViewController createWithName(String name) throws IOException {
        MasterViewController controller = new MasterViewController(name);
        controller.loadView();
        return controller;
    }

    @Override
    protected ArrayList<TreeItem<ViewController>> getTreeItems() {
        ArrayList<TreeItem<ViewController>> treeItems = new ArrayList<>();
        TreeItem<ViewController> treeItem;
        TreeItem<ViewController> subTreeItem;
        TableViewController tableViewController;
        Table table;

        // Account
        table = new Account();
        table.setTitle("Account");
        try {
            tableViewController = TableViewController.createWithNameAndTable("account", table);
            tableViewController.setTitle("Account");
        } catch (IOException e) {
            tableViewController = null;
        }
        treeItem = new TreeItem<>(tableViewController);
        treeItem.setExpanded(true);
        treeItems.add(treeItem);

        // Account.Warenkörbe
        table = new Bestellung();
        table.setTitle("Bestellungen");
        try{
            tableViewController = TableViewController.createWithNameAndTable("bestellungen", table);
            tableViewController.setTitle("Bestellungen");
        }catch (IOException e){
            tableViewController = null;
        }
        subTreeItem = new TreeItem<>(tableViewController);
        treeItem.getChildren().add(subTreeItem);

        // Angebote
        table = new Angebot();
        table.setTitle("Angebote");
        try{
            tableViewController = TableViewController.createWithNameAndTable("angebot", table);
            tableViewController.setTitle("Angebote");
        }catch (IOException e){
            tableViewController = null;
        }
        treeItem = new TreeItem<>(tableViewController);
        treeItem.setExpanded(true);
        treeItems.add(treeItem);

        // Newsletter
        table = new Newsletter();
        table.setTitle("Newsletter");
        try{
            tableViewController = TableViewController.createWithNameAndTable("newsletter", table);
            tableViewController.setTitle("Newsletter");
        }catch (IOException e){
            tableViewController = null;
        }
        treeItem = new TreeItem<>(tableViewController);
        treeItem.setExpanded(true);
        treeItems.add(treeItem);

        // Newsletter.Anmeldungen
        table = new Anmeldungen();
        table.setTitle("Anmeldungen");
        try{
            tableViewController = TableViewController.createWithNameAndTable("anmeldungen", table);
            tableViewController.setTitle("Anmeldungen");
        }catch (IOException e){
            tableViewController = null;
        }
        subTreeItem = new TreeItem<>(tableViewController);
        treeItem.getChildren().add(subTreeItem);
        /*
        table = new Favorites();
        table.setTitle("Favoriten");
        try {
            tableViewController = TableViewController.createWithNameAndTable("favorites", table);
            tableViewController.setTitle("Favoriten");
        } catch (IOException e) {
            tableViewController = null;
        }
        subTreeItem = new TreeItem<>(tableViewController);
        treeItem.getChildren().add(subTreeItem);*/

        return treeItems;
    }
}
