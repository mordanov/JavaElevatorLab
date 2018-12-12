package fxclasses;

import classes.Building;
import classes.Elevator;
import classes.Passenger;
import fxclasses.search.SearchEngine;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class FxBuilding extends Building {

    protected AnchorPane passPane;
    protected AnchorPane statPane;
    protected SearchEngine search;
    protected int height;
    protected int width;

    public FxBuilding(int floorcount, int passcount, int elevcount, int capacity, int source, int destination,
                      int width, int height, AnchorPane passPane, AnchorPane statPane, SearchEngine search) {
        super(floorcount, passcount, elevcount, capacity, source, destination);
        this.height = height;
        this.width = width;
        this.search = search;
        this.passPane = passPane;
        this.statPane = statPane;
    }

    @Override
    public FxElevator elevatorGenerator(int number) {
        return new FxElevator(number, floors.get(0), ++elevid, capacity,this, this.width, this.height, this.passPane, this.floorcount, this.elevcount, search);
    }

    @Override
    public FxFloor floorGenerator(int number) {
        return new FxFloor(number, this, this.width, this.height, this.passPane, this.floorcount, this.elevcount);
    }

    @Override
    public void drawPassenger(Passenger p) {
        search.addPerson(new FxPerson(p.getId(), p.getFirstname(), p.getSecondname(), p.getSource().getNumber(),
                p.getDestination().getNumber(), p.getCreateTime(), 0, 0, 0));
    }

    // отрисовать статистику на pane
    @Override
    public void stopService() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> barChart = new BarChart<String,Number>(xAxis,yAxis);
        Double totalInOutTime = .0;
        int maxLoad = 0;

        super.stopService();
        XYChart.Series series1 = new XYChart.Series(), series2 = new XYChart.Series();
        series1.setName("С какого этажа");
        series2.setName("На какой этаж");
        for(int i=0;i<floorcount;i++)
        {
            series1.getData().add(new XYChart.Data("Этаж " + String.valueOf(i + 1), floors.get(i).getSource_pass_count()));
            series2.getData().add(new XYChart.Data("Этаж " + String.valueOf(i + 1), floors.get(i).getDestination_pass_count()));
        }
        barChart.setMinWidth(width/2);
        barChart.setMinHeight(Math.round((height-20)/2.2));
        barChart.getData().addAll(series1, series2);
        barChart.setTitle("Перевезенные пассажиры");

        final NumberAxis xAxis1 = new NumberAxis();
        final NumberAxis yAxis1 = new NumberAxis();
        xAxis1.setLabel("Пассажир лифта");
        yAxis1.setLabel("Время ожидания/поездки");
        final LineChart<Number,Number> lineChart = new LineChart<>(xAxis1,yAxis1);
        XYChart.Series series3 = new XYChart.Series();
        XYChart.Series series4 = new XYChart.Series();
        for(FxPerson fp: search.getSourcelist().values()) {
            series3.getData().add(new XYChart.Data(fp.getId(), fp.getInTime() - fp.getGenerateTime()));
            series4.getData().add(new XYChart.Data(fp.getId(), fp.getInoutTime()));
            totalInOutTime += fp.getInTime() - fp.getGenerateTime();
        }
        totalInOutTime /= search.getSourcelist().size();
        for(Elevator e: elevators) {
            if(e.getMaxPassCount()>maxLoad)
                maxLoad = e.getMaxPassCount();
        }
        xAxis1.setForceZeroInRange(false);
        lineChart.setMinWidth(width/2);
        lineChart.setMinHeight((height-20)/2);
        lineChart.getData().addAll(series3, series4);
        lineChart.setTitle("Время пассажиров");

        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.getChildren().addAll(barChart, lineChart);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for(int i=0;i<elevcount;i++) {
            pieChartData.add(new PieChart.Data("Лифт " + String.valueOf(i+1), elevators.get(i).getMovedPassCount()));
        }
        final PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Работа лифтов");

        Label avgWaitTime = new Label();
        avgWaitTime.setFont(new Font("Arial", 20));
        avgWaitTime.setText(String.format("Среднее время ожидания лифта: %.2f с", totalInOutTime));
        Label maxLoadElevator = new Label();
        maxLoadElevator.setFont(new Font("Arial", 20));
        maxLoadElevator.setText(String.format("Максимальная загрузка лифта (%d): %d", capacity, maxLoad));

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.getChildren().addAll(avgWaitTime, maxLoadElevator, hbox, pieChart);

        Platform.runLater(() -> {
            statPane.getChildren().add(vbox);
        });
    }
}
