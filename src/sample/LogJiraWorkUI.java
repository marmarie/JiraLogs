package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

/**
 * Created by marie on 05.12.16.
 */
public class LogJiraWorkUI extends Application {
    Stage primaryStage ;
private static final String iconImageLoc =
        "http://icons.iconarchive.com/icons/scafer31000/bubble-circle-3/16/GameCenter-icon.png";



    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Locale.setDefault(Locale.ENGLISH);

        // instructs the javafx system not to exit implicitly when the last application window is shut.
        Platform.setImplicitExit(false);

        // sets up the tray icon (using awt code run on the swing thread).
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);

        primaryStage.setTitle("Tabs");
        Group root = new Group();
        Scene scene = new Scene(root, 450, 250, Color.WHITE);

        TabPane tabPane = new TabPane();

        BorderPane borderPane = new BorderPane();

        Tab tabD = getTabDays("Log days");
        Tab tabS = getTabDays("Log Task");
        Tab logAuto = getTabDays("Auto Log");

        HBox hbox = new HBox();

        hbox.setAlignment(Pos.CENTER);
        tabD.setContent(new LogMonthContent().getContent());
        tabS.setContent(new LogDaysContent().getContent());
        logAuto.setContent(new LogTodayAuto().getContent());
        tabPane.getTabs().addAll(tabS,tabD,logAuto);

        tabPane.setSide(Side.LEFT);
        tabPane.setTabMinWidth(30);
        tabPane.setTabMinHeight(70);
        // bind to take available space
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        //  borderPane.setPrefWidth(text1.getScaleX());
        borderPane.setLeft(tabPane);
        root.getChildren().add(borderPane);
        primaryStage.setScene(scene);

        primaryStage.show();

    }

    private void addAppToTray() {
        try {
            // ensure awt toolkit is initialized.
            java.awt.Toolkit.getDefaultToolkit();

            // app requires system tray support, just exit if there is no support.
            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            // set up a system tray icon.
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            URL imageLoc = new URL(iconImageLoc);
            java.awt.Image image = ImageIO.read(imageLoc);
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);

            // if the user double-clicks on the tray icon, show the main app stage.
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            // if the user selects the default menu item (which includes the app name),
            // show the main app stage.
            java.awt.MenuItem logDaysTray = new java.awt.MenuItem("Log Days");
            logDaysTray.addActionListener(event -> Platform.runLater(this::showStage));

            // the convention for tray icons seems to be to set the default icon for opening
            // the application stage in a bold font.
            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            logDaysTray.setFont(boldFont);

            // to really exit the application, the user must go to the system tray icon
            // and select the exit option, this will shutdown JavaFX and remove the
            // tray icon (removing the tray icon will also shut down AWT).
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
                Platform.exit();
                tray.remove(trayIcon);
            });

            // setup the popup menu for the application.
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(logDaysTray);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);


            // add the application tray icon to the system tray.
            tray.add(trayIcon);
        } catch (java.awt.AWTException | IOException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }




    private Tab getTabDays(String name){
        Tab tabD = new Tab();
        tabD.setClosable(false);
        tabD.setGraphic(new Label(name));
        return tabD;
    }



    /**
     * Shows the application stage and ensures that it is brought ot the front of all stages.
     */
    private void showStage() {
        if (this.primaryStage != null) {
            this.primaryStage.show();
            this.primaryStage.toFront();
        }
    }



    public static void main(String... args) {
        launch(args);
    }
}
