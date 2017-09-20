package app.game.model.board;

import app.game.model.cards.Card;
import app.game.model.cards.CardDeck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KlondikeCardDeck implements CardDeck {

    protected int deck_size;
    protected List<Card> deck;

    public KlondikeCardDeck(int size) {
        this.deck_size = size;
        this.deck = new ArrayList<>(size);
    }

    public static CardDeck standardKlondikeCardDeck() {
        KlondikeCardDeck cards = new KlondikeCardDeck(52);
        for (int i = 1; i <= 13; i++) {
            cards.deck.add(new KlondikeCard(Card.Color.CLUBS, i));
            cards.deck.add(new KlondikeCard(Card.Color.DIAMONDS, i));
            cards.deck.add(new KlondikeCard(Card.Color.HEARTS, i));
            cards.deck.add(new KlondikeCard(Card.Color.SPADES, i));
        }
        return cards;
    }

    @Override
    public Card get() {
        if (this.isEmpty()) return null;
        else return this.deck.get(this.size()-1);
    }

    @Override
    public Card get(int index) {
        if (index < this.size() && index >= 0)
            return this.deck.get(index);
        else return null;
    }

    @Override
    public Card pop() {
        if (this.isEmpty()) return null;
        else return this.deck.remove(--this.deck_size);
    }

    @Override
    public boolean put(Card card) {
        ((KlondikeCard) card).updateFace();
        return this.deck.add(card) && (++this.deck_size != 0);
    }

    @Override
    public boolean isEmpty() {
        return this.deck_size == 0;
    }

    @Override
    public int size() {
        return this.deck_size;
    }

    @Override
    public void shuffle() {
        Collections.shuffle(this.deck);
    }

    @Override
    public int find(Card card) {
        return deck.indexOf(card);
    }
}