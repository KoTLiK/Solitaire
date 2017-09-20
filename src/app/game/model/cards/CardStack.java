package app.game.model.cards;

public interface CardStack extends CardDeck {
    CardStack pop(Card card);
    boolean put(CardStack stack);
    boolean _add(Card card);
    boolean _add(CardStack stack);
}
