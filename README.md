# Anleitung zur Programmierung des Projekts für das DBS Programmierpraktikum


## Vorbereitung

Wir empfehlen IntelliJ oder Eclipse als IDE mit der Java-Version 1.8 (genauer 1.8.0\_152). Um den gesamten Ablauf, Integrität, Deployment etc. einfach zu halten, werden wir das Build-Management-Automatisierungs-Tool [Gradle](https://gradle.org) in unserer IDE verwenden. Gradle selbst muss nicht installiert werden, da der Gradle Wrapper benutzt wird, der dem Projekt beiliegt.
Die Programmierung des Projekts basiert auf folgenden Frameworks bzw. Libraries:
*  `com.github.Phlegethon90:ApplicationKit:v1.1.0` (auch im [uni-internen GitLab](https://git.hhu.de/althe100/ApplicationKit) zu finden)
*  `org.xerial:sqlite-jdbc:3.16.1+` (bei [Bitbucket](https://bitbucket.org/xerial/sqlite-jdbc))

Es muss immer die aktuellste ApplicationKit-Version verwendet werden. Wenn eine bestimmte SQLite-JDBC-Version benutzt werden soll (mindestens 3.16.1), kann die Versionsnummer entsprechend der im MavenCentral [gelisteten Versionen](https://search.maven.org/\#search\%7Cgav\%7C1\%7Cg\%3A\%22org.xerial\%22\%20AND\%20a\%3A\%22sqlite-jdbc\%22) angepasst werden.

Das gesamte Projekt steht als [Git-Repository](https://git.hhu.de/cs/dbs/datenbanksysteme/programmierpraktikum/Project) zur Verfügung. In der IDE muss es als Gradle-Projekt importiert werden.


## Datenbank anlegen

Da persistente Datenhaltung erforderlich ist, müssen wir eine Datenbank in [SQLite](https://www.sqlite.org) erzeugen. Voraussetzung dafür ist, dass das [Konsolenprogramm für SQLite](https://www.sqlite.org/download.html) auf dem Rechner installiert ist und gestartet werden kann.

Die DDL- und ggf. DML-Befehle werden per Datei eingelesen. Dazu legen wir eine Datei `project.sql` Projektverzeichnis an, die alle Befehle zum Initialisieren der Datenbank enthält. Vor den eigentlichen Befehlen müssen die Anweisungen aus dem folgenden Codeblock eingefügt werden.
```sqlite3
PRAGMA auto_vacuum = 1;
PRAGMA automatic_index = 1;
PRAGMA case_sensitive_like = 0;
PRAGMA defer_foreign_keys = 0;
PRAGMA encoding = 'UTF-8';
PRAGMA foreign_keys = 1;
PRAGMA ignore_check_constraints = 0;
PRAGMA journal_mode = WAL;
PRAGMA query_only = 0;
PRAGMA recursive_triggers = 1;
PRAGMA reverse_unordered_selects = 0;
PRAGMA secure_delete = 0;
PRAGMA synchronous = NORMAL;
```

Diese `PRAGMA`-Anweisungen sind wichtig, um volle Funktionalität (z.B. Fremdschlüsselintegrität) zu gewährleisten. Ausführliche Informationen dazu bietet die [Dokumentation](https://www.sqlite.org/pragma.html) zu SQLite.

Es ist absolut notwendig, dass man sich beim Erstellen der Datenbank auf folgende von ApplicationKit unterstützte Datentypen beschränkt:
*  `INTEGER`
*  `BOOLEAN`
*  `REAL`
*  `NUMERIC`
*  `TEXT`
*  `VARCHAR`
*  `BLOB`

Gibt man einen anderen Datentyp an, so treten eventuell Fehler auf. Ein wichtiger Hinweis: Will man in bestimmten Spalten der Datenbanktabellen auch Zeilenumbrüche speichern, so ist dies nur durch den Datentyp `TEXT` möglich, da erst bei diesem `TextArea`-Instanzen in der Zeilenansicht (siehe unten) erstellt werden. `BLOB` ist für Bilder zuständig. Zulässige Dateien sind ausschließlich PNG-Bilder. Für Datumsangaben muss `VARCHAR`genutzt werden.

Nachdem nun diese Datei angelegt wurde, öffnen wir die Konsole, wechseln in das Projektverzeichnis und starten das SQLite-Konsolenprogramm (meistens mit dem Befehl `sqlite3`). Eine Eingabeaufforderung erscheint, die nicht nur SQL-Befehle sondern auch andere Anweisungen entgegennimmt. Mit `.help` erscheint die Hilfe. Durch die Anweisung `.open project.db` wird eine leere Datenbank erstellt, sofern die Datei `project.db` in dem Verzeichnis, in dem SQLite ausgeführt wird, nicht existiert; ansonsten wird versucht, die entsprechende Datei zu öffnen. Der Befehl `.read project.sql` liest die eben erstellten Befehle ein und gibt gegebenenfalls Fehlermeldungen aus. Danach beenden wir die Eingabeaufforderung mit `.exit`.

Sollten beim Einlesen Fehlermeldungen auftauchen, muss man die angelegte Datenbank `project.db` einschließlich der ggf. damit angelegten Dateien löschen und die oben erwähnten Schritte, nachdem die Datei `project.sql` auf Fehler überprüft wurde, wiederholen.


## Verbindung einrichten und verwalten

Damit das Programm weiß, welche Datenbank es verwalten soll, muss zuerst die Verbindung zur Datenbank gesetzt bzw. angepasst werden. Dies geschieht in der `start()`-Methode der Klasse `de.hhu.cs.dbs.internship.project.Project`: Mit dem Aufruf der Methode `setConnection(Connection)` am Anfang des Methodenrumpfes, kann man die Verbindung initialisieren. Der folgende Codeblock dient als Beispiel dazu und setzt voraus, dass die Datenbankdatei `project.db` im Projektordner bzw. im aktuellen Arbeitsverzeichnis, wenn das Programm ausgeführt wird, liegt.
```java
setConnection(new Connection("jdbc:sqlite:project.db"));
```

Durch `Project.getInstance().getConnection()` kann nun programmweit, sofern die Klasse `de.hhu.cs.dbs.project.Project` in der entsprechenden Java-Datei importiert wird, auf die Verbindung zugegriffen werden. Auf der Verbindung stellen die Methoden
*  `createStatement()` und
*  `prepareStatement(String)`

eine Möglichkeit bereit, SQL-Anweisungen zu erstellen, wobei der Parameter die Zeichenkette des SQL-Befehls ist. Der Rückgabewert dieser Methoden ist eine [`Statement`](https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html)- bzw. [`PreparedStatement`](https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html)-Instanz der entsprechenden Klasse in Java. Auf diesen Instanzen kann man dann die entsprechenden Befehle ausführen. Der folgende Codeblock dient als einfaches Beispiel.
```java
String sql = "SELECT * FROM Tabelle1";
Statement statement = Project.getInstance().getConnection().createStatement();
ResultSet resultSet = statement.executeQuery(sql);
while (resultSet.next()) {
    System.out.println(resultSet.getObject("SpalteX")); 
}
```

Für weitere Informationen und Beispiele (insbesondere für `PreparedStatement`-Instanzen) sei auf das Internet und die Dokumentation zu Java verwiesen.


## Authentifizierung ermöglichen

Sobald wir das Programm starten, soll eine Anmeldung erscheinen. Da das Programm aber nicht nur eine Anmeldung, sondern auch eine Registrierung ermöglichen muss, eignet sich am besten eine Unterklasse des `AuthenticationViewController` des ApplicationKits.
Diese Unterklasse besitzt den gleichen Namen und befindet sich im Package `de.hhu.cs.dbs.internship.project.gui`. Sie bettet schon eine `LoginViewController`-Instanz und eine `RegistrationViewController`-Instanz in sich ein.
Unsere Aufgabe ist es jetzt, die Methoden
*  `loginUser(Data)` und
*  `registerUser(Data)`

zu implementieren. Beide Methoden funktionieren nach folgendem Prinzip: In der jeweiligen Methode wird versucht, den Nutzer anzumelden bzw. zu registrieren. Der Parameter vom Datentyp `Data` stellt dabei die Informationen bereit, die in den Eingabefeldern eingegeben wurden. Der Zugriff auf eine Information funktioniert wie bei einer HashMap mit der Instanzmethode `get(key)`, wobei `key` der Schlüssel der entsprechenden Information ist. Ist dieser Schlüssel nicht vorhanden, so wird eine `SQLException`-Instanz geworfen.

Zum Beispiel gibt ein Aufruf von `data.get("password")` das eingegebene Passwort zurück. Ein einfacher Aufruf von `System.out.println(data)` gibt Klarheit über die sich in `data` befindenden Informationen. Sollte die Anmeldung bzw. Registrierung aus irgendeinem Grund fehlschlagen, muss eine `SQLException`-Instanz geworfen werden.

Der Methodenrumpf der Methode `registerUser(Data)` soll einen neuen Benutzer in den entsprechenden Tabellen anlegen. Sollte der Benutzer schon existieren, ist die Registrierung ungültig und es muss eine `SQLException`-Instanz geworfen werden.

Der Methodenrumpf der Methode `loginUser(Data)` soll überprüfen, ob die angegebene Benutzername-Passwort-Kombination in der Datenbank enthalten ist. Ist dies nicht der Fall, muss eine `SQLException`-Instanz geworfen werden. Ist die Anmeldung jedoch erfolgreich, dann bietet es sich an, den Benutzernamen und andere Informationen (wie Rechte(!), Name, E-Mail, ...) aus der Datenbank programmweit zu speichern. Beispielsweise kann durch Codeblock \ref{lst:Data} der zugehörige Inhalt des Schlüssels `email` verwaltet werden.
```java
Project.getInstance().getData().put("email", "max@mustermann.de");
Project.getInstance().getData().get("email");
```

Sollte man bei `Data`-Instanzen auf Schlüssel zugreifen wollen, die nicht existieren, so wird ein neues `SQLException`-Objekt geworfen, das dieses Problem mit "`Schlüssel x nicht vorhanden"` bestätigt, wobei x der entsprechende Schlüsselname ist.

## Tabellen erstellen, anzeigen und verwalten

Sobald sich ein Nutzer angemeldet hat, erscheint eine `MasterDetailViewController`-Instanz des ApplicationKits. Der View ist zweigeteilt: Links sieht man den auf das Programm angepassten `MasterViewController` und rechts den entsprechenden DetailViewController, welcher meistens eine `TableViewController`-Instanz ist.
Die Unterklasse des `MasterViewController` besitzt den gleichen Namen und befindet sich im Package `de.hhu.cs.dbs.internship.project.gui`. In dieser Klasse befindet sich auskommentierter Code, der einem verdeutlicht, wie `TableViewController`-Instanzen in die GUI eingebettet werden.
Klickt man links im `MasterViewController` auf einen Eintrag, so wird der DetailViewController entsprechend neu gesetzt. Auf unser Projekt bezogen heißt das Folgendes: Klickt man links auf einen Tabellentitel, wird rechts die entsprechende Tabelle in einer `TableViewController`-Instanz angezeigt. Eingerückte Tabellentitel sind als Beziehung, untergeordnete Entitäten oder spezielle Tabellen zu dem übergeordneten Tabellentitel zu interpretieren.

Zu jedem übergeordneten Tabellentitel sollte es zur besseren Übersicht in `de.hhu.cs.dbs.internship.project.table` ein entsprechendes Package, in dem alle zu implementierenden Tabellenklassen erstellt werden.

Jede Tabellenklasse in `de.hhu.cs.dbs.internship.project.table` erbt von der abstrakten `Table`-Klasse des ApplicationKits. Dadurch sind die Methoden
*  `getSelectQueryForTableWithFilter(String)`,
*  `getSelectQueryForRowWithData(Data)`,
*  `insertRowWithData(Data)`,
*  `updateRowWithData(Data, Data)` und
*  `deleteRowWithData(Data)`

zu implementieren. Wir gehen diese Methoden und deren Bedeutung für die Tabelle anhand einer fiktiven, möglicherweise im eigentlichen Projekt nicht relevanten Tabelle in den folgenden Abschnitten durch.


### Tabellenansicht aufbauen und füllen

Wenn wir auf einen Tabellentitel klicken, wollen wir rechts die entsprechende Tabelle anzeigen lassen. Welche Spalten und Zeilen sollen in dieser Tabelle angezeigt werden?

Die Methode `getSelectQueryForTableWithFilter(String)` bestimmt diese Aspekte, indem der Rückgabewert ein `SELECT`-Befehl ist. In dieser `SELECT`-Anweisung legt die Projektion die folgenden anzuzeigenden Spalten fest. Der Parameter beinhaltet die Eingabe, die man als letztes im Suchfeld über der Tabelle bestätigt hat. Will man in der Tabelle nach bestimmten Kriterien filtern, so muss dem `SELECT`-Befehl noch die entsprechende `WHERE`-Bedingung angehangen werden. Eine Beispielimplementierung sieht folgendermaßen aus:
```java
@Override
public String getSelectQueryForTableWithFilter(String filter) throws SQLException {
    String selectQuery = "SELECT Tabelle1.Spalte1 FROM Tabelle1 JOIN Tabelle2 WHERE Tabelle1.Spalte1 = Tabelle2.Spalte1";
    if (filter != null && !filter.isEmpty()) {
        selectQuery += " AND Tabelle2.Spalte3 LIKE '%" + filter + "%'";
    }
    return selectQuery;
}
```

Es können beliebige Tabellen der Datenbank miteinander in Verbund treten, sodass man beliebige Informationen in der Tabelle darstellen kann.


### Zeilenansicht aufbauen und füllen

Wenn wir auf eine Zeile der Tabelle doppelklicken, wollen wir eine Detailansicht dieser Zeile (kurz: Zeilenansicht) anzeigen lassen, die wir gegebenenfalls bearbeiten können. Manchmal ist es notwendig, in der Tabelle selbst weniger bzw. andere Spalten anzuzeigen als in der Zeilenansicht. Wie legen wir diesen Sachverhalt fest? Wie erreichen wir überhaupt, dass die richtige Zeile in der Zeilenansicht angezeigt wird?

Die Methode `getSelectQueryForRowWithData(Data)` entscheidet, welche Informationen in dieser Ansicht angezeigt werden. Dazu gibt die Methode eine `SELECT`-Anweisung zurück. In diesem Befehl legt die Projektion wie auch bei der vorherigen Methode die anzuzeigenden Spalten fest. Der Parameter ist vom Datentyp `Data`, der schon weiter oben eingeführt wurde. Dieser stellt nun die Informationen bereit, die in der markierten Zeile der Tabelle stehen. Diese müssen, sofern es nötig ist, entsprechend ihres Datentyps in die Anfrage eingebettet werden! Dabei bestehen die Schlüssel dieses Parameters aus dem Tabellennamen in der Datenbank, einem Punkt und dem Spaltennamen. Die Schlüssel werden also von dem Rückgabewert der Methode `getSelectQueryForTableWithFilter(String)` bestimmt. Die Methode lässt sich beispielsweise folgendermaßen implementieren:
```java
@Override
public String getSelectQueryForRowWithData(Data) throws SQLException {
    String selectQuery = "SELECT * FROM Tabelle1 NATURAL JOIN Tabelle2 WHERE Tabelle1.Spalte1 = '" + data.get("Tabelle1.Spalte1") + "'";
    return selectQuery;
}
```

Es ist absolut notwendig in der Tabellenansicht den Primärschlüssel in Spalten anzeigen zu lassen, da man in der Methode ansonsten nicht eindeutig auf die markierte Zeile zugreifen kann.

Die Zeilenansicht dient nicht nur der genaueren Betrachtung und Bearbeitung der in der Tabelle ausgewählten Zeile, sondern wird auch angezeigt, wenn man auf den `+`-Button klickt.


### Zeile hinzufügen

Wie erwähnt, wird die Zeilenansicht auch angezeigt, wenn man den `+`-Button betätigt. Sobald der Nutzer in der erscheinenden Zeilenansicht auf den `Speichern`-Button klickt, sollen die eingegebenen Informationen, sofern diese gültig sind, gespeichert werden. Wie erreichen wir das?

In der Methode `insertRowWithData(Data)` werden sämtliche SQL-Anweisungen ausgeführt, die zum Einfügen der in `data` stehenden Informationen in die Datenbank notwendig sind. Diese Informationen sind genau diejenigen, die in der Zeilenansicht in die entsprechenden Eingabefelder eingegeben wurden. Der Parameter beinhaltet diese Daten, die wieder per Schlüssel angesprochen werden. Diese bestehen wieder aus dem Tabellennamen in der Datenbank, einem Punkt und dem Spaltennamen. Allerdings werden die Schlüssel nun von dem `SELECT`-Befehl der Methode `getSelectQueryForRowWithData(Data)` bestimmt, da dieser ja erst den Aufbau der Zeilenansicht bestimmt. Bevor wir weitere Hinweise zu der Methode geben, geben wir einen Beispielcode an.
```java
public void insertRowWithData(Data data) throws SQLException {
    if ((Integer) Project.getInstance().getData().get("permission") <= 1) {
        throw new SQLException("Nicht die notwendigen Rechte.");
    }

    PreparedStatement preparedStatement = Project.getInstance().getConnection().prepareStatement("INSERT INTO Tabelle1(Spalte1, Spalte2, Spalte3) VALUES (?, ?, ?)");
    preparedStatement.setObject(1, data.get("Tabelle1.Spalte1"));
    preparedStatement.setObject(2, data.get("Tabelle1.Spalte2"));
    preparedStatement.setObject(3, data.get("Tabelle1.Spalte3"));
    preparedStatement.executeUpdate();

    int i = preparedStatement.getGeneratedKeys().getInt(1);

    try {
        PreparedStatement preparedStatement2 = Project.getInstance().getConnection().prepareStatement("INSERT INTO Tabelle2(Spalte1, Spalte2) VALUES (?, ?)");
        preparedStatement2.setObject(1, i);
        preparedStatement2.setObject(2, data.get("Tabelle2.Spalte2"));
        preparedStatement2.executeUpdate();
    } catch (SQLException e) {
        PreparedStatement preparedStatement3 = Project.getInstance().getConnection().prepareStatement("DELETE FROM Tabelle1 WHERE ID = ?");
        preparedStatement3.setObject(1, i);
        preparedStatement3.executeUpdate();
        throw e;
    }
}
```

Eine Besonderheit ist die `if`-Abfrage in Zeile 3 bis 5. Bei der Anmeldung haben wir nicht nur geprüft, ob die Kombination aus Benutzername und Passwort in der Datenbank vorhanden ist, sondern auch herausbekommen, welche Rechte dieser Nutzer hat, sodass wir diese programmweit zur Verfügung stellen konnten. Die `permission` 1 steht in unserem Beispiel für geringe Rechte. Sind diese (oder noch geringere Rechte) gegeben, so darf der angemeldete Nutzer keine Zeilen hinzufügen. Es muss eine `SQLException`-Instanz geworfen, sodass alle in der Methode darauf folgenden Anweisungen nicht mehr ausgeführt werden. Generell muss bei implementierungsspezifischen Fehlern eine `SQLException`-Instanz geworfen werden.


### Zeile bearbeiten

Lässt man sich eine Zeile der Tabelle in der Zeilenansicht genauer anzeigen (entweder mit Doppelklick auf diese Zeile oder per Klick auf den `\`-Button), so kann man jederzeit auf den `Speichern`-Button klicken, sodass unter Umständen die Informationen einer Zeile in der Datenbank aktualisiert werden.

Die Methode `updateRowWithData(Data, Data)` wird ausgeführt, sobald dieser Button betätigt wurde. Sie stellt zwei Parameter zur Verfügung:
*  Der erste Parameter beinhaltet die in den Eingabefeldern stehenden Informationen vor Bearbeitung und
*  der zweite Parameter enthält die in den Eingabefeldern stehenden Informationen nach Bearbeitung.

Der Zugriff auf die entsprechenden Informationen erfolgt wieder durch Schlüssel gemäß der Spaltenauflistung im `SELECT`-Befehl von `getSelectQueryForRowWithData(Data)`. Auch hier gilt: Bei implementierungsspezifischen Fehlern muss eine `SQLException`-Instanz geworfen werden (wie zum Beispiel bei den beiden `if`-Abfragen in folgendem Codeblock.

```java
@Override
public void updateRowWithData(Data oldData, Data newData) throws SQLException {
    if ((Integer) Project.getInstance().getData().get("permission") <= 1) {
        throw new SQLException("Nicht die notwendigen Rechte.");
    }
    
    PreparedStatement preparedStatement = Project.getInstance().getConnection().prepareStatement("UPDATE Tabelle1 SET Spalte2 = ?, Spalte3 = ? WHERE Spalte1 = ?");
    preparedStatement.setObject(1, newData.get("Tabelle1.Spalte2"));
    preparedStatement.setObject(2, newData.get("Produkt.Spalte3"));
    preparedStatement.setObject(3, newData.get("Tabelle1.Spalte1"));
    preparedStatement.executeUpdate();

    try {
        PreparedStatement preparedStatement2 = Project.getInstance().getConnection().prepareStatement("UPDATE Tabelle2 SET Spalte2 = ? WHERE Spalte1 = ?");
        preparedStatement2.setObject(1, newData.get("Tabelle2.Spalte2"));
        preparedStatement2.setObject(2, newData.get("Tabelle2.Spalte1"));
        preparedStatement2.executeUpdate();
    } catch (SQLException e) {
        PreparedStatement preparedStatement3 = Project.getInstance().getConnection().prepareStatement("UPDATE Tabelle1 SET Spalte2 = ?, Spalte3 = ? WHERE Spalte1 = ?");
        preparedStatement3.setObject(1, oldData.get("Tabelle1.Spalte2"));
        preparedStatement3.setObject(2, oldData.get("Tabelle1.Spalte3"));
        preparedStatement3.setObject(3, oldData.get("Tabelle1.Spalte1"));
        preparedStatement3.executeUpdate();
        throw e;
    }
}
```


### Zeile löschen

Sobald man in der Tabelle eine Zeile markiert, kann man durch Klicken auf den `-`-Button direkt diese Zeile löschen.

Die Methode `deleteRowWithData(Data)` wird ausgeführt, sobald dieser Button betätigt wurde. Da das Löschen anhand von Informationen aus der Tabelle geschieht, bauen sich die Schlüssel des Parameters `data` anhand des `SELECT`-Befehls der Methode `getSelectQueryForTableWithFilter(String)` auf. Ein abstraktes Beispiel ist der folgende Codeblock.
```java
@Override
public void deleteRowWithData(Data data) throws SQLException {
    if ((Integer) Project.getInstance().getData().get("permission") <= 1) {
        throw new SQLException("Nicht die notwendigen Rechte.");
    }
    
    PreparedStatement preparedStatement = Project.getInstance().getConnection().prepareStatement("DELETE FROM Tabelle1 WHERE Spalte1 = ?");
    preparedStatement.setObject(1, data.get("Tabelle1.Spalte1"));
    preparedStatement.executeUpdate();
}
```



## Ausführbare JAR-Datei erzeugen

Nachdem man alle Tabellen implementiert hat, muss man das fertige Programm als ausführbare JAR-Datei in einem zu komprimierenden Verzeichnis abgeben. Um das Deployment des Projektes zu vereinfachen, wurde das Plugin `com.github.johnrengelman.shadow` eingebettet. Führt man den Gradle-Task `shadowJar` aus, wird im Ordner `build/libs` des Projektverzeichnis die ausführbare JAR-Datei `project-<version>-all.jar` erzeugt. Bei der Abgabe ist es wichtig, dass die verwendete Datenbank und ggf. zusätzliche Dateien im zu komprimierenden Verzeichnis liegen, da sonst das Programm nicht richtig gestartet werden kann. Sofern diese entsprechend der Anleitung im Projektordner angelegt wurden, ist dies das Verzeichnis, in dem auch die JAR-Datei liegt. Anschließend wird aus dem Verzeichnis ein ZIP-Archiv erstellt und dieses dann hochgeladen.