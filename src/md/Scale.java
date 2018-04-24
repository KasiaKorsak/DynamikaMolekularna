package md;

import javafx.scene.layout.Pane;
import javafx.stage.Screen;

public class Scale {

    private static double width;
    private static double height;

    public static double[] scale(double oldX, double oldY, Pane pane, double boxWidth){

        double[] newXY = new double[2];
        width = pane.getWidth();
        height = pane.getHeight();
        newXY[0] = (oldX*width)/boxWidth; //wspolrzedna x, 100 - szerokosc pudla
        newXY[1] = (oldY*(height/2))/(boxWidth/2); //wspolrzedna y, 50 - polowa wysokosci pudla
        return newXY;
    }


}
