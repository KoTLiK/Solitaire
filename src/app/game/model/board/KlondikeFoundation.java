package app.game.model.board;

import app.game.model.cards.Card;

public class KlondikeFoundation extends KlondikeCardDeck {

    private Card.Color color;

    public KlondikeFoundation() {
        super(0);
        this.color = null;
    }

    @Override
    public boolean put(Card card) {
        if (this.isEmpty()) {
            if (card.getValue() != 1) return false;
            this.color = card.getColor();
            return super.put(card);
        } else
            return (card.getValue() - 1 == this.deck_size)
                    && (card.getColor() == this.color)
                    && super.put(card);
    }
}
