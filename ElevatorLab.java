import fxclasses.FxBuilding;
import fxclasses.FxPerson;
import fxclasses.search.SearchEngine;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import netclasses.NetFxBuilding;

public class ElevatorLab extends Application {

    private final static int default_floorcount = 9;  // количество этажей по умолчанию
    private final static int default_passcount = 200; // количество пассажиров по умолчанию
    private final static int default_elevcount = 6;   // количество лифтов по умолчанию
    private final static int default_capacity = 20;   // вместимость лифтов по умолчанию
    private final static int default_source = 0;      // random value
    private final static int default_destination = 0; // random value

    private final static int height = 800;            // размеры окна
    private final static int width = 1400;

    private SearchEngine searchEngine;                // поисковик по именам и фамилиям
    private AnchorPane passengerPane;
    private AnchorPane statisticsPane;

    public static void main(String[] args) {
        launch(args);
    }

    private void CreateInterface(Stage primaryStage) {
        Group root = new Group();
        Canvas canvas = new Canvas(width, height+20);
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, width, height+20);

        // панель с закладками
        TabPane tabpane = new TabPane();
        tabpane.setMinWidth(width);
        tabpane.setMinHeight(height+20);

        Tab tab1 = new Tab();
        tab1.setText("Визуализация");
        tab1.setClosable(false);

        Tab tab2 = new Tab();
        tab2.setText("Список пассажиров");
        tab2.setClosable(false);

        Tab tab3 = new Tab();
        tab3.setText("Статистика");
        tab3.setClosable(false);
        tabpane.getTabs().addAll(tab1, tab2, tab3);
        tabpane.setMinHeight(height+20);
        tabpane.setMinWidth(width);

        passengerPane = new AnchorPane();
        tab1.setContent(passengerPane);
        AnchorPane anchor2 = new AnchorPane();
        tab2.setContent(anchor2);
        statisticsPane = new AnchorPane();
        tab3.setContent(statisticsPane);

        primaryStage.setTitle("Лифты в здании");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.getChildren().addAll(tabpane);

        // таблица с именами и фамилиями перевезенных пассажиров
        TableView table = new TableView();
        table.setEditable(true);
        table.setMinHeight(height - 88);

        // описание столбцов таблички
        TableColumn idCol = new TableColumn<FxPerson, Long>("ID");
        idCol.setMinWidth(width*0.05);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn firstNameCol = new TableColumn<FxPerson, String>("Имя");
        firstNameCol.setMinWidth(width*0.15);
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        TableColumn lastNameCol = new TableColumn<FxPerson, String>("Фамилия");
        lastNameCol.setMinWidth(width*0.19);
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("secondname"));
        TableColumn fromCol = new TableColumn<FxPerson, Integer>("С какого этажа");
        fromCol.setMinWidth(width*0.09);
        fromCol.setCellValueFactory(new PropertyValueFactory<>("source"));
        TableColumn toCol = new TableColumn<FxPerson, Integer>("На какой этаж");
        toCol.setMinWidth(width*0.1);
        toCol.setCellValueFactory(new PropertyValueFactory<>("destination"));
        TableColumn timeCreateCol = new TableColumn<FxPerson, Double>("Время появления");
        timeCreateCol.setMinWidth(width*0.1);
        timeCreateCol.setCellValueFactory(new PropertyValueFactory<>("generateTime"));
        TableColumn timeStartCol = new TableColumn<FxPerson, Double>("Время входа в лифт");
        timeStartCol.setMinWidth(width*0.1);
        timeStartCol.setCellValueFactory(new PropertyValueFactory<>("inTime"));
        TableColumn timeEndCol = new TableColumn<FxPerson, Double>("Время выхода из лифта");
        timeEndCol.setMinWidth(width*0.1);
        timeEndCol.setCellValueFactory(new PropertyValueFactory<>("outTime"));
        TableColumn timeOverCol = new TableColumn<FxPerson, Double>("Время поездки");
        timeOverCol.setMinWidth(width*0.1);
        timeOverCol.setCellValueFactory(new PropertyValueFactory<>("inoutTime"));

        table.getColumns().addAll(idCol, firstNameCol, lastNameCol, timeCreateCol, fromCol, toCol, timeStartCol, timeEndCol, timeOverCol);

        searchEngine = new SearchEngine(table);

        HBox hbox = new HBox();
        Label s_label = new Label("Поиск:");
        s_label.setFont(new Font("Arial", 16));
        TextField s_text = new TextField();
        s_text.setMinWidth(width / 2);
        s_text.setPromptText("Введите данные для поиска...");
        s_text.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                searchEngine.setSearchValue(s_text.getText());
            }
        });
        hbox.getChildren().addAll(s_label, s_text);

        Label ab_label = new Label("Список пассажиров");
        ab_label.setFont(new Font("Arial", 20));
        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.setMinWidth(width);
        vbox.setMinHeight(height+20);
        vbox.getChildren().addAll(ab_label, hbox, table);
        anchor2.getChildren().add(vbox);

    }

    private FxBuilding home;

    @Override
    public void start(Stage primaryStage) {

        int passcount;
        int elevcount;
        int floorcount;
        int capacity;
        int source;
        int destination;

        // все gui компоненты - в отдельную функцию
        CreateInterface(primaryStage);

        // параметры командной строки
        if(getParameters().getNamed().get("passcount")==null)
            passcount = default_passcount;
        else
            passcount = Integer.valueOf(getParameters().getNamed().get("passcount"));
        if(getParameters().getNamed().get("elevcount")==null)
            elevcount = default_elevcount;
        else
            elevcount = Integer.valueOf(getParameters().getNamed().get("elevcount"));
        if(getParameters().getNamed().get("elevcount")==null)
            floorcount = default_floorcount;
        else
            floorcount = Integer.valueOf(getParameters().getNamed().get("floorcount"));
        if(getParameters().getNamed().get("capacity")==null)
            capacity = default_capacity;
        else
            capacity = Integer.valueOf(getParameters().getNamed().get("capacity"));
        if(getParameters().getNamed().get("source")==null)
            source = default_source;
        else
            source = Integer.valueOf(getParameters().getNamed().get("source"));
        if(getParameters().getNamed().get("destination")==null)
            destination = default_destination;
        else
            destination = Integer.valueOf(getParameters().getNamed().get("destination"));
        if((source<0) || (source>floorcount)) {
            System.out.println("Значение source не может превышать floorcount или быть меньше 0!");
            source = 0;
        }
        if((destination<0) || (destination>floorcount)) {
            System.out.println("Значение destination не может превышать floorcount или быть меньше 0!");
            source = 0;
        }

        home = new NetFxBuilding(floorcount, passcount, elevcount, capacity, source, destination, width, height,
                passengerPane, statisticsPane, searchEngine);
        home.start();

    }

    @Override
    public void stop() {
        home.killService();
    }
}
