package app.game.model.factory;

import app.game.model.board.*;
import app.game.model.cards.*;

public class KlondikeFactory extends SolitaireFactory {

    public KlondikeFactory(){}

    @Override
    public Card createCard(Card.Color color, int value) {
        if (value > 0 && value < 14)
            return new KlondikeCard(color, value);
        else return null;
    }

    @Override
    public CardDeck createCardDeck() {
        return KlondikeCardDeck.standardKlondikeCardDeck();
    }

    @Override
    public CardDeck createCardDeck(int size) {
        return new KlondikeCardDeck(size);
    }

    @Override
    public CardDeck createFoundation() {
        return new KlondikeFoundation();
    }

    @Override
    public CardStack createTableauPile() {
        return new KlondikeTableauPile();
    }

    @Override
    public CardStack createTableauPile(int index, CardDeck deck) {
        return new KlondikeTableauPile(index, deck);
    }
}
