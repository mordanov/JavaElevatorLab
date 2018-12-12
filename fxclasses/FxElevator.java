package fxclasses;

import classes.Building;
import classes.Elevator;
import classes.Floor;
import classes.Passenger;
import classes.elements.eDirection;
import fxclasses.search.SearchEngine;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class FxElevator extends Elevator {

    private final static int elevimagesize = 64;

    private final Double floorheight;
    private ImageView imageview;
    private ImageView ivd;
    private Text inCountText;
    private SearchEngine search;

    public FxElevator(int number, Floor position, int id, int capacity, Building b, int width, int height, AnchorPane group, int floorcount, int elevcount, SearchEngine search) {
        super(number, position, id, capacity, b);
        final int fontsize = 20;
        this.search = search;
        floorheight = (height - 20)/floorcount*1.0;

        ImageView iv = new ImageView();
        Image image = new Image("/images/elevator.png");
        iv.setImage(image);
        iv.setScaleY(floorheight / elevimagesize);
        iv.setX(700 - (elevcount*elevimagesize)/2 + ((elevcount - number) * elevimagesize));
        iv.setY((floorcount - position.getNumber()) * floorheight + 20);
        this.imageview = iv;

        Text incount = new Text();
        incount.setText("00");
        incount.setFont(new Font("Arial", fontsize));
        incount.setX(700 - (elevcount*elevimagesize)/2 + ((elevcount - number) * elevimagesize) + (elevimagesize - fontsize)/2);
        incount.setY((floorcount - position.getNumber()) * floorheight + 20 + (elevimagesize - fontsize)/2);
        this.inCountText = incount;

        ImageView ivd = new ImageView();
        ivd.setScaleY(floorheight / elevimagesize);
        ivd.setX(700 - (elevcount*elevimagesize)/2 + ((elevcount - number) * elevimagesize));
        ivd.setY((floorcount - position.getNumber() - 1) * floorheight  + elevimagesize/2 + 20);
        this.ivd = ivd;

        group.getChildren().addAll(iv, ivd, incount);

        setDirection(direction);

    }

    // сколько пассажиров внутри
    private void setInText(int n) {
        if(n<10) {
            inCountText.setText("0"+String.valueOf(n));
        }
        else {
            inCountText.setText(String.valueOf(n));
        }
    }

    @Override
    public void setDirection(eDirection direction) {
        super.setDirection(direction);
        if(ivd==null)
            return;;

        Image dir_img;
        switch (direction) {
            case GET_PASSENGERS:
                dir_img = new Image("/images/get_passengers.png");
                break;
            case PUT_PASSENGERS:
                dir_img = new Image("/images/put_passengers.png");
                break;
            case OFFLINE:
            default:
                dir_img = new Image("/images/no_passengers.png");
                break;
        }
        Platform.runLater(() -> {
            ivd.setImage(dir_img);
        });
    }

    // нарисовать перемещение лифта
    @Override
    public void moveInfo(int source, int destination) {
        moveImage2NewLocation(source, destination, imageview);
        moveImage2NewLocation(source, destination, ivd);
        Platform.runLater(() -> {
            setInText(getPassengers().size());

            TranslateTransition tt = new TranslateTransition();
            tt.setNode(inCountText);
            tt.setByY((source - destination) * floorheight);
            tt.setCycleCount(1);
            tt.play();
        });
    }

    // рисовалка стрелки над лифтом
    private void moveImage2NewLocation(int source, int destination, ImageView ivd) {
        Platform.runLater(() -> {
            TranslateTransition tt = new TranslateTransition();
            tt.setNode(ivd);
            tt.setByY((source - destination) * floorheight);
            tt.setCycleCount(1);
            tt.play();
        });
    }

    // остановить лифт и запрозрачить его :)
    @Override
    public void finishInfo(Floor position) {
        FadeTransition ft = new FadeTransition();
        ft.setNode(imageview);
        ft.setDuration(new Duration(2000));
        ft.setFromValue(1.0);
        ft.setToValue(0.1);
        ft.setCycleCount(1);
        ft.setAutoReverse(true);
        Platform.runLater(() -> {
            ft.play();
            setInText(0);
            setDirection(eDirection.OFFLINE);
        });
    }

    @Override
    public void drawPassengerOutTime(Passenger p) {
        FxPerson fxp = search.getSourcelist().get(p.getId());
        fxp.setOutTime((p.getOutTime()));
        fxp.setInoutTime((p.getInOutTime()));
    }

    @Override
    protected void drawPassengerInTime(Passenger p) {
        if(search!=null)
            synchronized (this) {
                search.getSourcelist().get(p.getId()).setInTime((p.getInTime()));
            }
    }

}
