package app.game.model.cards;

public interface Card extends java.io.Serializable {

    class Dimension {
        public final static double HEIGHT = 140;
        public final static double WIDTH = 100;
        public final static int FACEUPOFFSET = 20;
        public final static int FACEDOWNOFFSET = 8;
    }

    enum Color {
        CLUBS("C"), DIAMONDS("D"), HEARTS("H"), SPADES("S");

        private final String symbol;
        private final boolean color;

        Color (String s) {
            this.symbol = s;
            color = !s.equals("C") && !s.equals("S");
        }

        public boolean similarColorTo(Card.Color c) {
            return this.color == c.color;
        }

        @Override
        public String toString() {
            return this.symbol;
        }
    }

    Card.Color getColor();
    int getValue();
    int compareValue(Card c);
    boolean isTurnedFaceUp();
    boolean turnCard();
    boolean turnFaceUp();
    boolean turnFaceDown();
    boolean similarColorTo(Card c);
}
