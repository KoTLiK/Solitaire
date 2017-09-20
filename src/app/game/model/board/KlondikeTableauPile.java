package app.game.model.board;

import app.game.model.cards.Card;
import app.game.model.cards.CardDeck;
import app.game.model.cards.CardStack;

public class KlondikeTableauPile extends KlondikeCardDeck implements CardStack {

    public KlondikeTableauPile() {
        super(0);
    }

    public KlondikeTableauPile(int index, CardDeck deck) {
        super(index + 1);

        for (int i = 0; i <= index; i++)
            this.deck.add(deck.pop());

        //this.get().turnFaceUp();
        //if (!this.get().isTurnedFaceUp()) this.get().turnCard();
        this.get().turnCard();
        ((KlondikeCard)this.get()).updateFace();
    }

    @Override
    public CardStack pop(Card card) {
        KlondikeTableauPile pile = new KlondikeTableauPile();
        int index = this.deck.indexOf(card);

        if (index == -1 || !this.deck.get(index).isTurnedFaceUp())
            return null;

        while (!this.isEmpty() && index < this.size()) {
            pile.deck.add(this.deck.remove(index));
            pile.deck_size++;
            this.deck_size--;
        }

        return pile;
    }

    @Override
    public boolean put(Card card) {
        Card c = this.get();

        if (c == null) // empty pile
            return (card.getValue() == 13) && super.put(card);
        else return c.isTurnedFaceUp()
                && (c.compareValue(card) == 1)
                && !c.similarColorTo(card)
                && super.put(card);
    }

    @Override
    public boolean put(CardStack stack) {
        if (this.isEmpty()) {
            if (stack.get(0).getValue() != 13) return false;
        } else if (this.get().compareValue(stack.get(0)) != 1 || this.get(this.size()-1).similarColorTo(stack.get(0)))
            return false;

        return this._add(stack);
    }

    @Override
    public boolean _add(Card card) {
        return super.put(card);
    }

    @Override
    public boolean _add(CardStack stack) {
        KlondikeTableauPile s = (KlondikeTableauPile) stack;
        while (!s.isEmpty()) {
            super.put(s.deck.remove(0));
            s.deck_size--;
        }
        return true;
    }
}
