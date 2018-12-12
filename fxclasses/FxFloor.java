package fxclasses;

import classes.Floor;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class FxFloor extends Floor {
    private AnchorPane group;
    private int height;
    private int width;
    private int floorcount;
    private int elevcount;
    private List<ImageView> images = new ArrayList<>();
    private List<ImageView> outimages = new ArrayList<>();
    private Label labelleft = null;
    private Label labelright = null;

    private final Font font24;
    private final Font font14;

    private Double floorheight;

    public FxFloor(int number, FxBuilding building, int width, int height, AnchorPane group, int floorcount, int elevcount) {
        super(number, building);
        this.height = height;
        this.width = width;
        this.group = group;
        this.floorcount = floorcount;
        this.elevcount = elevcount;

        floorheight = (height - 20)/floorcount*1.0;

        font24 = Font.loadFont(getClass().getResourceAsStream("/fonts/BlackOpsOne.ttf"), 24);
        font14 = Font.loadFont(getClass().getResourceAsStream("/fonts/BlackOpsOne.ttf"), 14);

        Line leftLine = new Line(10, 10 + Math.round(number * floorheight), 700 - (elevcount*64)/2, 10 + Math.round(number * floorheight));
        leftLine.setFill(Color.GREEN);
        leftLine.setStroke(Color.BLUE);
        leftLine.setStrokeWidth(5);
        Line rightLine = new Line(700 + (elevcount*64)/2, 10 + Math.round(number * floorheight), width, 10 + Math.round(number * floorheight));
        rightLine.setFill(Color.GREEN);
        rightLine.setStroke(Color.BLUE);
        rightLine.setStrokeWidth(5);

        Platform.runLater(() -> {
            group.getChildren().add(leftLine);
            group.getChildren().add(rightLine);
        });

        if(number>0) {
            Label label = new Label();
            label.setText(String.valueOf(floorcount - number + 1));
            label.setFont(font24);
            label.setTranslateX(10);
            label.setTranslateY(Math.round(number * floorheight) - floorheight/2);
            group.getChildren().add(label);
        }

        labelleft = new Label();
        labelleft.setFont(font14);
        labelleft.setLayoutX(maxvisiblepass * 32);
        labelleft.setLayoutY(20 + floorheight * (floorcount - getNumber()));
        Platform.runLater(() -> group.getChildren().add(labelleft));

        labelright = new Label();
        labelright.setFont(font14);
        labelright.setLayoutX(width - maxvisiblepass * 32);
        labelright.setLayoutY(20 + floorheight * (floorcount - getNumber()));
        Platform.runLater(() -> group.getChildren().add(labelright));
    }

    @Override
    public void drawFloorInPassenger(final int passcount) {
        super.drawFloorInPassenger(passcount);
        int viscount = passcount;
        if (viscount > maxvisiblepass)
            viscount = maxvisiblepass;
        for(int i=viscount;i<images.size();i++) {
            ImageView iv = images.get(images.size() - 1);
            iv.setImage(null);
            images.remove(iv);
            Platform.runLater(() -> group.getChildren().remove(iv));
        }
        for(int i=images.size();i<viscount;i++) {
            ImageView iv = new ImageView();
            Image image = new Image("/images/human.png");
            iv.setImage(image);
            iv.setScaleY(floorheight / 64);
            iv.setX(10 + (images.size()) * 16);
            iv.setY(20 + floorheight * (floorcount - getNumber()));
            images.add(iv);
            Platform.runLater(() -> group.getChildren().add(iv));
        }
        Platform.runLater(() -> labelleft.setText(String.valueOf(passcount)));
    }

    @Override
    public void drawFloorOutPassenger(final int passcount) {
        super.drawFloorOutPassenger(passcount);
        if ((passcount <= maxvisiblepass) && (passcount > 0)) {
            ImageView iv = new ImageView();
            Image image = new Image("/images/human.png");
            iv.setImage(image);
            iv.setScaleY(floorheight / 64);
            iv.setX(width - (outimages.size() * 16) - 64);
            iv.setY(20 + floorheight * (floorcount - getNumber()));
            outimages.add(iv);
            Platform.runLater(() -> group.getChildren().add(iv));
        }
        Platform.runLater(() -> labelright.setText(String.valueOf(passcount)));
    }

}
