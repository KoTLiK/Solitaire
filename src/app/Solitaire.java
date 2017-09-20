package app;

import app.game.Delta;
import app.game.Game;
import app.game.gui.GameController;
import app.game.model.cards.Card;
import app.game.model.cards.CardDeck;
import app.game.model.cards.CardStack;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Solitaire extends Application {

    public static final String SAVE_PATH = "./files/";

    @Override
    public void start(Stage primaryStage) throws Exception {
        rootFrame(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void rootFrame(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Solitaire.class.getResource("game/gui/RootFrame.fxml"));
        primaryStage.getIcons().add((new Image("/solitaire.png"))); // TODO .icon ?
        primaryStage.setTitle("Solitaire");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(root, 220, 260));
        primaryStage.getScene().getStylesheets().add("/root.css");
        primaryStage.show();
    }

    public static void gameStart(Stage primaryStage, Game game) throws Exception {
        if (game == null) return;

        FXMLLoader loader = new FXMLLoader(Solitaire.class.getResource("game/gui/GameFrame.fxml"));
        primaryStage.getIcons().add((new Image("/solitaire.png"))); // TODO .icon ?
        primaryStage.setTitle("Solitaire");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(loader.load(), 900, 680));

        GameController controller = loader.getController();
        controller.setGame(game);

        primaryStage.getScene().getStylesheets().add("/game.css");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void minimizeWindow(AnchorPane contentPane, boolean flag) {
        ((Stage)contentPane.getScene().getWindow()).setIconified(flag);
    }

    public static void closeWindow(AnchorPane contentPane) {
        ((Stage)contentPane.getScene().getWindow()).close();
    }

    public static void windowMousePressed(MouseEvent mouseEvent, AnchorPane rootPane, Delta delta) {
        final Stage rootStage = (Stage) rootPane.getScene().getWindow();
        delta.x = rootStage.getX() - mouseEvent.getScreenX();
        delta.y = rootStage.getY() - mouseEvent.getScreenY();
    }

    public static void windowMouseDragged(MouseEvent mouseEvent, AnchorPane rootPane, Delta delta) {
        final Stage rootStage = (Stage) rootPane.getScene().getWindow();
        rootStage.setX(mouseEvent.getScreenX() + delta.x);
        rootStage.setY(mouseEvent.getScreenY() + delta.y);
    }

    public static class Move {

        private static CardDeck[] waste;
        private static CardDeck[] foundation;
        private static CardStack[] tableau;

        public static void setWaste(CardDeck[] waste) {
            Move.waste = waste;
        }

        public static void setFoundation(CardDeck[] foundation) {
            Move.foundation = foundation;
        }

        public static void setTableau(CardStack[] tableau) {
            Move.tableau = tableau;
        }

        /**          NEXT WASTE           */

        public static boolean nextWaste() {
            if (waste[0].isEmpty())
                return false;
            else {
                Card c = waste[0].pop();
                c.turnFaceUp();
                return waste[1].put(c);
            }
        }

        public static boolean nextWaste(boolean test) {
            if (!test) return nextWaste();
            else return !waste[0].isEmpty();
        }

        public static boolean nextWasteUndo() {
            if (waste[1].isEmpty())
                return false;
            else {
                Card c = waste[1].pop();
                c.turnFaceDown();
                return waste[0].put(c);
            }
        }

        /**          RECYCLE WASTE           */

        public static boolean recycleWaste() {
            if (waste[1].isEmpty()) return true;
            else {
                while (!waste[1].isEmpty()) {
                    Card c = waste[1].pop();
                    c.turnFaceDown();
                    if (!waste[0].put(c))
                        return false;
                }
            }
            return true;
        }

        public static boolean recycleWaste(boolean test) {
            if (!test) return recycleWaste();
            else return !waste[1].isEmpty();
        }

        public static boolean recycleWasteUndo() {
            if (waste[0].isEmpty()) return true;
            else {
                while (!waste[0].isEmpty()) {
                    Card c = waste[0].pop();
                    c.turnFaceUp();
                    if (!waste[1].put(c))
                        return false;
                }
            }
            return true;
        }

        /**          WASTE TO TABLEAU           */

        public static boolean wasteToTableau(int dest) {
            Card c = waste[1].pop();
            if (c == null) return false;
            if (tableau[dest].put(c))
                return true;
            else {
                waste[1].put(c);
                return false;
            }
        }

        public static boolean wasteToTableau(int dest, boolean test) {
            if (!test) return wasteToTableau(dest);
            else {
                Card t = tableau[dest].get();
                Card w = waste[1].get();
                if (t == null)
                    return (w.getValue() == 13);
                else return t.isTurnedFaceUp()
                        && (t.compareValue(w) == 1)
                        && !t.similarColorTo(w);
            }
        }

        public static boolean wasteToTableauUndo(int dest) {
            Card c = tableau[dest].pop();
            if (c == null) return false;
            if (waste[1].put(c))
                return true;
            else {
                tableau[dest]._add(c);
                return false;
            }
        }

        /**          WASTE TO FOUNDATION           */

        public static boolean wasteToFoundation(int dest) {
            Card c = waste[1].pop();
            if (c == null) return false;
            if (foundation[dest].put(c))
                return true;
            else {
                waste[1].put(c);
                return false;
            }
        }

        public static boolean wasteToFoundation(int dest, boolean test) {
            if (!test) return wasteToFoundation(dest);
            else {
                Card w = waste[1].get();
                if (w == null) return false;
                if (foundation[dest].isEmpty()) return (w.getValue() == 1);
                else return (w.getValue() - 1 == foundation[dest].size())
                        && (w.getColor() == foundation[dest].get().getColor());
            }
        }

        public static boolean wasteToFoundationUndo(int dest) {
            Card c = foundation[dest].pop();
            if (c == null) return false;
            if (waste[1].put(c))
                return true;
            else {
                foundation[dest].put(c);
                return false;
            }
        }

        /**          CARD TURN OVER           */

        public static boolean tableauTurnOver(int dest) {
            return tableau[dest].get().turnFaceUp();
        }

        public static boolean tableauTurnOver(int dest, boolean test) {
            if (!test) return tableauTurnOver(dest);
            else return !tableau[dest].get().isTurnedFaceUp();
        }

        public static boolean tableauTurnOverUndo(int dest) {
            return tableau[dest].get().turnFaceDown();
        }

        /**          TABLEAU TO FOUNDATION           */

        public static boolean tableauToFoundation(int c, int src, int dest) {
            CardStack s = tableau[src].pop(tableau[src].get(c));
            if (s == null) return false;
            if (s.size() == 1) {
                Card card = s.get();
                if (foundation[dest].put(card))
                    return true;
                else {
                    tableau[src]._add(card);
                    return false;
                }
            } else {
                tableau[src]._add(s);
                return false;
            }
        }

        public static boolean tableauToFoundation(int c, int src, int dest, boolean test) {
            if (!test) return tableauToFoundation(c, src, dest);
            else {
                Card t = tableau[src].get(c);
                if (t == tableau[src].get()) {
                    if (t == null) return false;
                    if (foundation[dest].isEmpty()) return (t.getValue() == 1);
                    else return (t.getValue() - 1 == foundation[dest].size())
                            && (t.getColor() == foundation[dest].get().getColor());
                } else return false;
            }
        }

        public static boolean tableauToFoundationUndo(int src, int dest) {
            Card c = foundation[dest].pop();
            if (c == null) return false;
            if (tableau[src]._add(c))
                return true;
            else {
                foundation[dest].put(c);
                return false;
            }
        }

        /**          TABLEAU TO TABLEAU           */

        public static Card tableauToTableau(int c, int src, int dest) {
            if (src == dest) return null;
            Card card = tableau[src].get(c);
            CardStack s = tableau[src].pop(card);
            if (s == null) return null;
            if (tableau[dest].put(s))
                return card;
            else {
                tableau[src]._add(s);
                return null;
            }
        }

        public static boolean tableauToTableau(int c, int src, int dest, boolean test) {
            if (!test) return (tableauToTableau(c, src, dest) != null);
            else {
                Card t_src = tableau[src].get(c);
                Card t_dst = tableau[dest].get();
                if (t_dst == null)
                    return (t_src.getValue() == 13);
                else return t_dst.isTurnedFaceUp()
                        && (t_dst.compareValue(t_src) == 1)
                        && !t_dst.similarColorTo(t_src);
            }
        }

        public static boolean tableauToTableauUndo(Card c, int src, int dest) {
            if (src == dest) return false;
            CardStack s = tableau[dest].pop(c);
            if (s == null) return false;
            if (tableau[src]._add(s))
                return true;
            else {
                tableau[dest]._add(s);
                return false;
            }
        }
    }
}
