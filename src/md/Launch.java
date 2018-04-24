package md;

import javafx.application.Application;
import javafx.stage.Stage;

public class Launch extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(Animacja.getScene());
        primaryStage.show();
    }
}
