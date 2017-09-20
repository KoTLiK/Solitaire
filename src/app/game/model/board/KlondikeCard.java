package app.game.model.board;

import app.game.model.cards.Card;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class KlondikeCard extends Label implements Card {

    private final Card.Color color;
    private final int value;
    private boolean faceUp;

    private transient ImageView back;
    private transient ImageView front;

    public KlondikeCard(Card.Color color, int value) {
        this.color = color;
        this.value = value;
        this.faceUp = false;

        // GUI part
        this.prefHeight(Card.Dimension.HEIGHT);
        this.prefWidth(Card.Dimension.WIDTH);
        this.back = new ImageView("/cards/B.png");
        this.front = new ImageView("/cards/"+color.toString()+"/"+value+".png");
        initialize();
    }

    private void initialize() {
        this.back.setPreserveRatio(true);
        this.back.setSmooth(true);
        this.back.setFitHeight(Card.Dimension.HEIGHT);
        this.back.setFitWidth(Card.Dimension.WIDTH);

        this.front.setPreserveRatio(true);
        this.front.setSmooth(true);
        this.front.setFitHeight(Card.Dimension.HEIGHT);
        this.front.setFitWidth(Card.Dimension.WIDTH);

        this.setGraphic(this.back);
    }

    public void updateFace() {
        this.setGraphic(this.faceUp ? this.front : this.back);
    }

    public void reloadGraphics() {
        this.back = new ImageView("/cards/B.png");
        this.back.setPreserveRatio(true);
        this.back.setSmooth(true);
        this.back.setFitHeight(Card.Dimension.HEIGHT);
        this.back.setFitWidth(Card.Dimension.WIDTH);

        this.front = new ImageView("/cards/"+color.toString()+"/"+value+".png");
        this.front.setPreserveRatio(true);
        this.front.setSmooth(true);
        this.front.setFitHeight(Card.Dimension.HEIGHT);
        this.front.setFitWidth(Card.Dimension.WIDTH);

        updateFace();
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public int compareValue(Card c) {
        return this.value - ((KlondikeCard) c).value;
    }

    @Override
    public boolean isTurnedFaceUp() {
        return this.faceUp;
    }

    @Override
    public boolean turnCard() {
        if ((this.faceUp = !this.faceUp)) {
            updateFace();
            return true;
        } else {
            updateFace();
            return false;
        }

        //return (this.faceUp = !this.faceUp);
    }

    @Override
    public boolean turnFaceUp() {
        if ((!this.faceUp) && (this.faceUp = true)) {
            updateFace();
            return true;
        } else return false;

        //return (!this.faceUp) && (this.faceUp = true);
        //return (this.faceUp = true);
    }

    @Override
    public boolean turnFaceDown() {
        if (this.faceUp && !(this.faceUp = false)) {
            updateFace();
            return true;
        } else return false;

        //return this.faceUp && !(this.faceUp = false);
        //return !(this.faceUp = false);
    }

    @Override
    public boolean similarColorTo(Card c) {
        return this.color.similarColorTo(((KlondikeCard) c).color);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof KlondikeCard) {
            KlondikeCard c = (KlondikeCard) obj;
            if (this.value == c.value && this.color == c.color)
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.value * this.color.hashCode();
    }

    @Override
    public String toString() {
        switch (this.value) {
            case  1: return "A("+ this.color.toString() +")"+ (this.faceUp?"T":"F");
            case 11: return "J("+ this.color.toString() +")"+ (this.faceUp?"T":"F");
            case 12: return "Q("+ this.color.toString() +")"+ (this.faceUp?"T":"F");
            case 13: return "K("+ this.color.toString() +")"+ (this.faceUp?"T":"F");
            default: return this.value +"("+ this.color.toString() +")"+ (this.faceUp?"T":"F");
        }
    }
}
