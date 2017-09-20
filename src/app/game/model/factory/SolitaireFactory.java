package app.game.model.factory;

import app.game.model.cards.*;

public abstract class SolitaireFactory implements java.io.Serializable {
    public SolitaireFactory(){}
    public abstract Card createCard(Card.Color color, int value);
    public abstract CardDeck createCardDeck();
    public abstract CardDeck createCardDeck(int size);
    public abstract CardDeck createFoundation();
    public abstract CardStack createTableauPile();
    public abstract CardStack createTableauPile(int index, CardDeck deck);
}