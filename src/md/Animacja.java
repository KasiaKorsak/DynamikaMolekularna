package md;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Animacja {

    private static Pane drawingPane;
    private static Button btnStart;
    private static Button btnStop;
    private static Button btnChart;
    private static AnimationTimer atimer;
    private static Label label;
    private static Slider slider;
    private static TextField txtQuantity;
    private static Circle circles[];
    private static MD md;
    private static int quantity; //ilosc atomow
    private static double boxWidth = 100.0;

    private static boolean runnig = false;

    private static double[] newXY;
    private static double h = 0.01;
    private static List<Double> eKin, ePot, eElastic, eTotal;
    private static double R = 1;

    public static Scene getScene() {

        label = new Label("Number of atoms: ");
        // suwak
        // params: wartosc minimalna, wartosc maksymalna, parametr
        slider = new Slider(0, 1000, quantity);
        slider.setPrefSize(500, 10);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(100);
        slider.setMinorTickCount(1);

        txtQuantity = new TextField(Integer.toString(quantity));
        txtQuantity.setEditable(false);

        // pobieranie wartości z suwaka
        // tworzenie anonimowej klasy wewnetrznej (new ChangeListener)
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                txtQuantity.setText(Integer.toString(newValue.intValue()));
            }
        });

        HBox hBox = new HBox(slider, txtQuantity);
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);
        VBox vBox = new VBox();

        drawingPane = new Pane();

        drawingPane.setPrefSize(400, 400);
        //drawingPane.prefWidthProperty().bind(vBox.widthProperty());
        drawingPane.setStyle("-fx-background-color: white;");

        btnStart = new Button("Start");
        btnStart.setPrefWidth(100);
        btnStop = new Button("Stop");
        btnStop.setPrefWidth(100);
        btnStop.setDisable(true);
        btnChart = new Button("Charts");
        btnChart.setPrefWidth(100);

        eKin = new ArrayList<>();
        ePot = new ArrayList<>();
        eElastic = new ArrayList<>();
        eTotal = new ArrayList<>();

        btnStart.setOnAction(e -> {
            btnChart.setVisible(false);
            vBox.getChildren().add(drawingPane);

            if (!txtQuantity.equals("")) {
                quantity = Integer.parseInt(txtQuantity.getText());
                circles = new Circle[quantity];

                md = new MD(quantity, boxWidth);

                if (!runnig) { //to wykona się raz na początku animacji

                    for (int i = 0; i < quantity; i++) {
                        circles[i] = new Circle(R, Color.color(Math.random(), Math.random(), Math.random()));

                        newXY = Scale.scale(md.getX()[i], md.getY()[i], drawingPane, 100);
                       // if(newXY[0]<boxWidth && newXY[1]<boxWidth){
                        circles[i].relocate(newXY[0], newXY[1]);
                        drawingPane.getChildren().addAll(circles[i]);}


                    btnStart.setDisable(true);
                    btnStop.setDisable(false);
                    runnig = true;

                }
                atimer = new AnimationTimer() {
                    private long lastUpdate;

                    @Override
                    public void handle(long now) {

                        if (now - lastUpdate > 50_000_000) {

                            for (int i = 0; i < quantity; i++) {

                                newXY = Scale.scale(md.getX()[i], md.getY()[i], drawingPane, 100);
                                circles[i].relocate(newXY[0], newXY[1]);

                            }


                            lastUpdate = now;
                        } else {
                            md.Verlet(h);
                            eKin.add(md.geteKin());
                            ePot.add(md.getePot());
                            eElastic.add(md.getElasticEnergy());
                            eTotal.add(md.getTotalEnergy());
                        }
                    }
                };

                atimer.start();
            }

        });

        btnStop.setOnAction(e -> {
            btnChart.setVisible(true);
            atimer.stop();
            btnStart.setDisable(false);
            btnStop.setDisable(true);
            runnig = false;
            //drawingPane.getChildren().clear();
            circles = null;
            vBox.getChildren().remove(drawingPane);
            btnChart.setAlignment(Pos.BOTTOM_CENTER);
            vBox.getChildren().add(btnChart);

        });


        btnChart.setOnAction(e -> {
            drawCharts();
        });
        HBox hBox2 = new HBox(btnStart, btnStop);

        hBox2.setSpacing(50);
        hBox2.setAlignment(Pos.CENTER);

        vBox.getChildren().addAll(label, hBox, hBox2);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);

        Scene scene = new Scene(vBox, 500, 500);

        return scene;

    }

    private static void drawCharts() {

        double t = md.getStepCounter() * h;
        double th = 0;

        XYChart.Series eKinChart = new XYChart.Series();
        XYChart.Series ePotChart = new XYChart.Series();
        XYChart.Series eElasticChart = new XYChart.Series();
        XYChart.Series eTotalChart = new XYChart.Series();

        for (int i = 0; i < t; i++) {

            eKinChart.getData().add(new XYChart.Data<>(th / h, eKin.get(i)));

            ePotChart.getData().add(new XYChart.Data<>(th / h, ePot.get(i)));

            eElasticChart.getData().add(new XYChart.Data<>(th / h, eElastic.get(i)));

            eTotalChart.getData().add(new XYChart.Data<>(th / h, eTotal.get(i)));

            th += h;
        }


        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time [s]");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Energy [J]");

        eKinChart.setName("Kinetic en.");
        ePotChart.setName("Potencial en.");
        eElasticChart.setName("Elastic en.");
        eTotalChart.setName("Total en.");

        LineChart charts = new LineChart<Number, Number>(xAxis, yAxis);
        charts.getData().addAll(eKinChart, ePotChart, eElasticChart, eTotalChart);
        charts.setCreateSymbols(false);
        charts.setLegendVisible(true);
        charts.setLegendSide(Side.BOTTOM);

        Stage stage = new Stage();
        VBox layout = new VBox();
        layout.getChildren().add(charts);
        Scene sceneChart = new Scene(layout, 500, 500);
        stage.setScene(sceneChart);
        stage.show();
    }

    public static double getR() {
        return R;
    }
}