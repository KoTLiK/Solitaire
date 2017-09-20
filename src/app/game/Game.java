package app.game;

import app.Solitaire;
import app.game.gui.dialog.DialogFX;
import app.game.model.board.KlondikeCard;
import app.game.model.cards.Card;
import app.game.model.cards.CardDeck;
import app.game.model.cards.CardStack;
import app.game.model.cmd.*;
import app.game.model.factory.KlondikeFactory;
import app.game.model.factory.SolitaireFactory;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.Collections;

public class Game implements java.io.Serializable {

    private final CardDeck[] waste = new CardDeck[2];
    private final CardDeck[] foundation = new CardDeck[4];
    private final CardStack[] tableau = new CardStack[7];
    private final Commander.Invoker invoker = new Commander.Invoker();
    private int score;

    private transient Label[] wL;
    private transient Label[] fL;
    private transient Label[] tL;
    private transient Label msg_box;

    public Game() {
        score = 0;
        SolitaireFactory factory = new KlondikeFactory();

        waste[0] = factory.createCardDeck();
        waste[0].shuffle();
        waste[1] = factory.createCardDeck(0);

        for (int i = 0; i < 4; i++)
            foundation[i] = factory.createFoundation();

        for (int i = 0; i < 7; i++)
            tableau[i] = factory.createTableauPile(i, waste[0]);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = (score < 0 ? 0 : score);
    }

    public void undo() {
        invoker.undo();
        updateAll();
    }

    public int getUndoSize() {
        return invoker.size();
    }

    private CardDeck[] getWaste() {
        return waste;
    }

    private CardDeck[] getFoundation() {
        return foundation;
    }

    private CardStack[] getTableau() {
        return tableau;
    }

    public void initializeMoves() {
        Solitaire.Move.setWaste(this.getWaste());
        Solitaire.Move.setFoundation(this.getFoundation());
        Solitaire.Move.setTableau(this.getTableau());
    }

    public void setBoard(Pane pane, Label[] wL, Label[] fL, Label[] tL, Label msg_box) {
        this.wL = wL;
        this.fL = fL;
        this.tL = tL;
        this.msg_box = msg_box;
        final Delta delta = new Delta();

        wL[0].setOnMousePressed(mouseEvent -> {
            invoker.execute(new RecycleWaste(this));
            updateWaste();
        });
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < waste[i].size(); j++) {
                KlondikeCard card = (KlondikeCard) waste[i].get(j);
                card.reloadGraphics();
                card.setLayoutX(wL[i].getLayoutX());
                card.setLayoutY(wL[i].getLayoutY());
                pane.getChildren().add(card);

                card.setOnMousePressed(mouseEvent -> cardMousePressed(mouseEvent, delta));
                card.setOnMouseDragged(mouseEvent -> cardMouseDragged(mouseEvent, delta));
                card.setOnMouseReleased(mouseEvent -> cardMouseReleased(mouseEvent, delta));
            }
        }

        for (int i = 0; i < 4; i++) {
            if (foundation[i].size() > 0) {
                KlondikeCard card = (KlondikeCard) foundation[i].get();
                card.reloadGraphics();
                card.setLayoutX(fL[i].getLayoutX());
                card.setLayoutY(fL[i].getLayoutY());
                pane.getChildren().add(card);

                card.setOnMousePressed(mouseEvent -> cardMousePressed(mouseEvent, delta));
                card.setOnMouseDragged(mouseEvent -> cardMouseDragged(mouseEvent, delta));
                card.setOnMouseReleased(mouseEvent -> cardMouseReleased(mouseEvent, delta));
            }
        }

        for (int k = 0, i = 0; i < 7; i++, k = 0) {
            for (int j = 0; j < tableau[i].size(); j++) {
                KlondikeCard card = (KlondikeCard) tableau[i].get(j);
                card.reloadGraphics();
                card.setLayoutX(tL[i].getLayoutX());
                card.setLayoutY(tL[i].getLayoutY() + k);
                pane.getChildren().add(card);
                k += card.isTurnedFaceUp()? Card.Dimension.FACEUPOFFSET : Card.Dimension.FACEDOWNOFFSET;

                card.setOnMousePressed(mouseEvent -> cardMousePressed(mouseEvent, delta));
                card.setOnMouseDragged(mouseEvent -> cardMouseDragged(mouseEvent, delta));
                card.setOnMouseReleased(mouseEvent -> cardMouseReleased(mouseEvent, delta));
            }
        }
    }

    private void cardMousePressed(MouseEvent event, Delta delta) {
        delta.old_x = ((KlondikeCard)event.getTarget()).getLayoutX();
        delta.old_y = ((KlondikeCard)event.getTarget()).getLayoutY();
        delta.x = delta.old_x - event.getSceneX();
        delta.y = delta.old_y - event.getSceneY();

        int pos;
        if ((pos = findIn(event, wL, 2)) != -1) { // Waste Pile
            if (pos == 0) {
                invoker.execute(new NextWaste());
                updateWaste();
                delta.target = 0;
            } else {
                delta.src = 1;
                if (event.getClickCount() == 2) { // Double Click
                    delta.src = -1;
                    delta.target = 0;
                    for (int i = 0; i < 4; i++) {
                        if (invoker.add(new WasteToFoundation(this, i))) {
                            updateFoundation(i);
                            checkForEnd();
                            break;
                        }
                    }
                }
            }

        } else if (findIn(event, fL, 4) != -1) { // Foundation Pile
            delta.target = 0;

        } else if ((pos = findInTableau(event, true)) != -1) { // Tableau Pile
            if (!((KlondikeCard) event.getTarget()).isTurnedFaceUp()){
                delta.target = 0;
                delta.src = pos + 10;
            } else {
                delta.src = pos + 10;
                if (event.getClickCount() == 2) { // Double Click
                    delta.src = -1;
                    delta.target = 0;
                    for (int i = 0; i < 4; i++) {
                        if (invoker.add(new TableauToFoundation(this,
                                tableau[pos].find((KlondikeCard)event.getTarget()),
                                pos, i))) {
                            updateTableau(pos);
                            updateFoundation(i);
                            checkForEnd();
                            break;
                        }
                    }
                }
            }

        } else updateAll();
    }

    private void cardMouseDragged(MouseEvent event, Delta delta) {
        if (delta.target == 0) return;

        ((KlondikeCard)event.getTarget()).setLayoutX(event.getSceneX() + delta.x);
        ((KlondikeCard)event.getTarget()).setLayoutY(event.getSceneY() + delta.y);
        ((KlondikeCard)event.getTarget()).toFront();

        if (delta.src >= 10) {
            int k, g = 0;
            if ((k = tableau[delta.src - 10].find((KlondikeCard) event.getTarget())) >= 0) {
                for (int i = k; i < tableau[delta.src - 10].size(); i++) {
                    KlondikeCard card = ((KlondikeCard)tableau[delta.src - 10].get(i));
                    card.setLayoutX(event.getSceneX() + delta.x);
                    card.setLayoutY(event.getSceneY() + delta.y + g);
                    card.toFront();
                    g += Card.Dimension.FACEUPOFFSET;
                }
            }
        }
    }

    private void cardMouseReleased(MouseEvent event, Delta delta) {
        if (delta.target != 0) {
            ((KlondikeCard) event.getTarget()).setLayoutX(delta.old_x);
            ((KlondikeCard) event.getTarget()).setLayoutY(delta.old_y);
        }

        int pos;
        if (findIn(event, wL, 2) != -1) { // Waste Pile
            if (delta.src >= 10)
                updateAll();

        } else if ((pos = findIn(event, fL, 4)) != -1) { // Foundation Pile
            if (delta.src < 10) {
                invoker.execute(new WasteToFoundation(this, pos));
                updateFoundation(pos);
                checkForEnd();
            } else {
                invoker.execute(new TableauToFoundation(this,
                        tableau[delta.src - 10].find((KlondikeCard) event.getTarget()),
                        delta.src - 10, pos));
                updateTableau(delta.src - 10);
                updateFoundation(pos);
                checkForEnd();
            }

        } else if ((pos = findInTableau(event, false)) != -1) { // Tableau Pile
            if (delta.src < 10) {
                if (delta.target != 0) { // Possible bug fix, not sure yet
                    invoker.execute(new WasteToTableau(this, pos));
                    updateTableau(pos);
                }
            } else {
                if ((delta.src - 10) == pos) {
                    invoker.execute(new TableauTurnOver(this, pos));
                    updateTableau(pos);
                } else {
                    invoker.execute(new TableauToTableau(
                            tableau[delta.src - 10].find((KlondikeCard) event.getTarget()),
                            delta.src - 10, pos));
                    updateTableau(delta.src - 10);
                    updateTableau(pos);
                }
            }
        } else updateAll();

        delta.clear();
    }

    private int findIn(MouseEvent event, Label[] pile, int count) {
        for (int i = 0; i < count; i++) {
            double x = event.getSceneX() - pile[i].getLayoutX();
            double y = event.getSceneY() - pile[i].getLayoutY() -30; // 30 offset (Whole window -> Navigation Bar Height)
            if (x >= 0 && x < Card.Dimension.WIDTH && y >= 0 && y < Card.Dimension.HEIGHT) return i;
        }

        return -1;
    }

    private int findInTableau(MouseEvent event, boolean mousePressed) {
        if (mousePressed) {
            for (int i = 0; i < 7; i++)
                if (tableau[i].find((KlondikeCard) event.getTarget()) >= 0) return i;
        } else {
            for (int i = 0; i < 7; i++) {
                double x = event.getSceneX() - tL[i].getLayoutX();
                if (x >= 0 && x < Card.Dimension.WIDTH && event.getSceneY() >= 180) return i;
            }
        }

        return -1;
    }

    private void updateWaste() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < waste[i].size(); j++) {
                KlondikeCard card = (KlondikeCard) waste[i].get(j);
                card.setLayoutX(wL[i].getLayoutX());
                card.setLayoutY(wL[i].getLayoutY());
                card.toFront();
            }
        }
        updateScoreBoard();
    }

    private void updateFoundation(int i) {
        if (foundation[i].size() > 0) {
            KlondikeCard card = (KlondikeCard) foundation[i].get();
            card.setLayoutX(fL[i].getLayoutX());
            card.setLayoutY(fL[i].getLayoutY());
            card.toFront();
        }
        updateScoreBoard();
    }

    private void updateTableau(int i) {
        int k = 0;
        for (int j = 0; j < tableau[i].size(); j++) {
            KlondikeCard card = (KlondikeCard) tableau[i].get(j);
            card.setLayoutX(tL[i].getLayoutX());
            card.setLayoutY(tL[i].getLayoutY() + k);
            card.toFront();
            k += card.isTurnedFaceUp() ? Card.Dimension.FACEUPOFFSET : Card.Dimension.FACEDOWNOFFSET;
        }
        updateScoreBoard();
    }

    private void updateAll() {
            updateWaste();
        for (int i = 0; i < 4; i++)
            updateFoundation(i);
        for (int i = 0; i < 7; i++)
            updateTableau(i);
    }
    
    private void checkForEnd() {
        for (CardDeck deck : foundation) {
            Card card = deck.get();
            if (card == null) return;
            if (card.getValue() != 13) return;
        }

        // TODO little more user-friendly
        msg_box.setText("You won with " + score + " score!");
        new DialogFX(DialogFX.Type.CUSTOM)
            .addButtons(Collections.singletonList("Ok"))
            .setTitleText("Victory !")
            .setMessage("You won the game with "+ score +" score!")
            .setIcon("/W.png")
            .addStyleSheetFile("/dialogFX.css")
            .showDialog();
    }

    private void updateScoreBoard() {
        msg_box.setText("Score: " + score);
    }
}
