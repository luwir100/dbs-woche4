package de.hhu.cs.dbs.internship.project.gui;

import com.alexanderthelen.applicationkit.database.Table;
import com.alexanderthelen.applicationkit.gui.TableViewController;
import com.alexanderthelen.applicationkit.gui.ViewController;
import de.hhu.cs.dbs.internship.project.table.account.*;
import de.hhu.cs.dbs.internship.project.table.angebot.*;
import de.hhu.cs.dbs.internship.project.table.newsletter.Anmeldungen;
import de.hhu.cs.dbs.internship.project.table.newsletter.Newsletter;
import de.hhu.cs.dbs.internship.project.table.newsletter.NewsletterEmpfehlungen;
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

        // Account.Lieferabos
        table = new Lieferabos();
        table.setTitle("Lieferabos");
        try{
            tableViewController = TableViewController.createWithNameAndTable("lieferabos", table);
            tableViewController.setTitle("Lieferabos");
        }catch (IOException e){
            tableViewController = null;
        }
        subTreeItem = new TreeItem<>(tableViewController);
        treeItem.getChildren().add(subTreeItem);

        // Account.Inhalte
        table = new Inhalte();
        table.setTitle("Inhalte");
        try{
            tableViewController = TableViewController.createWithNameAndTable("inhalte", table);
            tableViewController.setTitle("Inhalte");
        }catch (IOException e){
            tableViewController = null;
        }
        subTreeItem = new TreeItem<>(tableViewController);
        treeItem.getChildren().add(subTreeItem);

        // Account.Premiumstatus
        table = new Premiumstatus();
        table.setTitle("Premiumstatus");
        try{
            tableViewController = TableViewController.createWithNameAndTable("premiumstatus", table);
            tableViewController.setTitle("Premiumstatus");
        }catch (IOException e){
            tableViewController = null;
        }
        subTreeItem = new TreeItem<>(tableViewController);
        treeItem.getChildren().add(subTreeItem);

        // Bestand
        table = new Bestand();
        table.setTitle("Bestand");
        try{
            tableViewController = TableViewController.createWithNameAndTable("bestand", table);
            tableViewController.setTitle("Bestand");
        }catch (IOException e){
            tableViewController = null;
        }
        treeItem = new TreeItem<>(tableViewController);
        treeItem.setExpanded(true);
        treeItems.add(treeItem);

        // Bestand.Angebot
        table = new Angebot();
        table.setTitle("Angebote");
        try{
            tableViewController = TableViewController.createWithNameAndTable("angebote", table);
            tableViewController.setTitle("Angebote");
        }catch (IOException e){
            tableViewController = null;
        }
        subTreeItem = new TreeItem<>(tableViewController);
        treeItem.getChildren().add(subTreeItem);

        // Bestand.Artikel
        table = new Artikel();
        table.setTitle("Artikel");
        try{
            tableViewController = TableViewController.createWithNameAndTable("artikel", table);
            tableViewController.setTitle("Artikel");
        }catch (IOException e){
            tableViewController = null;
        }
        subTreeItem = new TreeItem<>(tableViewController);
        treeItem.getChildren().add(subTreeItem);

        // Bestand.Schlagwortsuche
        table = new Schlagwortsuche();
        table.setTitle("Schlagwortsuche");
        try{
            tableViewController = TableViewController.createWithNameAndTable("schlagwortsuche", table);
            tableViewController.setTitle("Schlagwortsuche");
        }catch (IOException e){
            tableViewController = null;
        }
        subTreeItem = new TreeItem<>(tableViewController);
        treeItem.getChildren().add(subTreeItem);

        // Bestand.Schlagwort
        table = new Schlagwort();
        table.setTitle("Schlagwort");
        try{
            tableViewController = TableViewController.createWithNameAndTable("schlagwort", table);
            tableViewController.setTitle("Schlagwort");
        }catch (IOException e){
            tableViewController = null;
        }
        subTreeItem = new TreeItem<>(tableViewController);
        treeItem.getChildren().add(subTreeItem);

        // Bestand.Empfehlung
        table = new Empfehlungen();
        table.setTitle("Empfehlungen");
        try {
            tableViewController = TableViewController.createWithNameAndTable("empfehlungen", table);
            tableViewController.setTitle("Empfehlungen");
        } catch (IOException e) {
            tableViewController = null;
        }
        subTreeItem = new TreeItem<>(tableViewController);
        treeItem.getChildren().add(subTreeItem);

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

        // Newsletter.Empfehlungen
        table = new NewsletterEmpfehlungen();
        table.setTitle("Empfehlungen");
        try{
            tableViewController = TableViewController.createWithNameAndTable("empfehlungen", table);
            tableViewController.setTitle("Empfehlungen");
        }catch (IOException e){
            tableViewController = null;
        }
        subTreeItem = new TreeItem<>(tableViewController);
        treeItem.getChildren().add(subTreeItem);

        return treeItems;
    }
}
