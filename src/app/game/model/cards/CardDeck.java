package app.game.model.cards;

public interface CardDeck extends java.io.Serializable {
    Card get();
    Card get(int index);
    Card pop();
    boolean put(Card card);
    boolean isEmpty();
    int size();
    void shuffle();
    int find(Card card);
}
