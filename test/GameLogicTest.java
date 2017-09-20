import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import app.game.model.cards.Card;
import app.game.model.cards.CardDeck;
import app.game.model.cards.CardStack;
import app.game.model.factory.KlondikeFactory;
import app.game.model.factory.SolitaireFactory;

import java.util.HashSet;
import java.util.Set;

public class GameLogicTest {

    protected static SolitaireFactory factory;

    @Before
    public void setUp() {
        factory = new KlondikeFactory();
    }

    @After
    public void tearDown() {}

    @Test
    public void testCard() {
        Card c1 = factory.createCard(Card.Color.CLUBS, 0);
        Card c2 = factory.createCard(Card.Color.DIAMONDS, 11);
        Card c3 = factory.createCard(Card.Color.SPADES, 5);
        Card c4 = factory.createCard(Card.Color.CLUBS, 6);
        Card c5 = factory.createCard(Card.Color.CLUBS, 6);

        Assert.assertNull("Card with value 0", c1);
        Assert.assertFalse("Card c2 can not be faced up.", c2.isTurnedFaceUp());
        Assert.assertTrue("Turning card c2.", c2.turnFaceUp());
        Assert.assertTrue("Card c2 must be faced up.", c2.isTurnedFaceUp());
        Assert.assertFalse("Turning card c2.", c2.turnFaceUp());
        Assert.assertTrue("Card colors (not symbol) are the same.", c3.similarColorTo(c4));
        Assert.assertFalse("Card colors (not symbol) are the same.", c3.similarColorTo(c2));
        Assert.assertEquals("The objects are equal.", c4, c5);
        Assert.assertTrue("Difference of two cards.", c2.compareValue(c3) == 6);
    }

    @Test
    public void testCardDeck() {
        CardDeck deck = factory.createCardDeck();

        Assert.assertEquals("Number of cards is 52", 52, deck.size());

        Set<Card> testCards = new HashSet<>();
        for (int i = 1; i <= 13; i++) { testCards.add(factory.createCard(Card.Color.CLUBS,i)); }
        for (int i = 1; i <= 13; i++) { testCards.add(factory.createCard(Card.Color.DIAMONDS,i)); }
        for (int i = 1; i <= 13; i++) { testCards.add(factory.createCard(Card.Color.HEARTS,i)); }
        for (int i = 1; i <= 13; i++) { testCards.add(factory.createCard(Card.Color.SPADES,i)); }

        for (int i = 0; i < 52; i++) {
            Card c = deck.pop();
            Assert.assertTrue("Correct card " + c.toString(), testCards.remove(c));
        }

        Assert.assertTrue("Testing set must be empty.", testCards.isEmpty());
    }

    @Test
    public void testFoundation() {

        CardDeck pack = factory.createFoundation();

        Card c1 = factory.createCard(Card.Color.DIAMONDS, 11);
        Card c2 = factory.createCard(Card.Color.DIAMONDS, 1);
        Card c3 = factory.createCard(Card.Color.CLUBS, 11);
        Card c4 = factory.createCard(Card.Color.CLUBS, 1);
        Card c5 = factory.createCard(Card.Color.CLUBS, 2);

        Assert.assertTrue("Number of cards in pack is 0.", pack.isEmpty());
        Assert.assertTrue("Card can be inserted.", pack.put(c2));
        Assert.assertFalse("Card cannot be inserted.", pack.put(c1));
        Assert.assertEquals("Number of cards in pack is 1.", 1, pack.size());
        Assert.assertEquals("Card removed.", c2, pack.pop());
        Assert.assertTrue("Number of cards in pack is 0.", pack.isEmpty());
        Assert.assertFalse("Card cannot be inserted.", pack.put(c3));
        Assert.assertEquals("Number of cards in pack is 0.", 0, pack.size());
        Assert.assertTrue("Card can be inserted.", pack.put(c4));
        Assert.assertEquals("Number of cards in pack is 1.", 1, pack.size());
        Assert.assertFalse("Card cannot be inserted.", pack.put(c3));
        Assert.assertEquals("Number of cards in pack is 1.", 1, pack.size());
        Assert.assertTrue("Card can be inserted.", pack.put(c5));
        Assert.assertEquals("Number of cards in pack is 2.", 2, pack.size());

        Card c55 = factory.createCard(Card.Color.CLUBS, 2);
        Card c44 = factory.createCard(Card.Color.CLUBS, 1);

        Assert.assertEquals("Top card is c55", c55, pack.get());
        Assert.assertEquals("Number of cards in pack is 2.", 2, pack.size());
        Assert.assertEquals("Remove card from top.", c55, pack.pop());
        Assert.assertEquals("Number of cards in pack is 1.", 1, pack.size());
        Assert.assertEquals("Top card is c44", c44, pack.get());
        Assert.assertEquals("Number of cards in pack is 1.", 1, pack.size());
    }

    @Test
    public void testTableauPile() {

        CardStack pack = factory.createTableauPile();

        Card c1 = factory.createCard(Card.Color.DIAMONDS, 11);
        Card c2 = factory.createCard(Card.Color.DIAMONDS, 13);
        Card c3 = factory.createCard(Card.Color.HEARTS, 12);
        Card c4 = factory.createCard(Card.Color.CLUBS, 12);
        Card c5 = factory.createCard(Card.Color.SPADES, 11);
        Card c6 = factory.createCard(Card.Color.HEARTS, 11);

        Assert.assertEquals("Tableau pile is empty.", 0, pack.size());
        Assert.assertFalse("Only king is allowed to insert into empty tableau pile.", pack.put(c1));
        Assert.assertTrue("Inserting king into empty tableau pile.", pack.put(c2));
        Assert.assertFalse("At red king is allowed only black queen.", pack.put(c3));
        Assert.assertEquals("Tableau pile contains 1 card.", 1, pack.size());

        pack.get().turnCard();
        Assert.assertTrue("Inserting black queen at red king.", pack.put(c4));
        Assert.assertEquals("Tableau pile contains 2 cards.", 2, pack.size());

        pack.get().turnCard();
        Assert.assertFalse("At black queen is allowed only red jack.", pack.put(c5));
        Assert.assertEquals("Tableau pile contains 2 cards.", 2, pack.size());
        Assert.assertTrue("Inserting red jack at black queen.", pack.put(c6));
        Assert.assertEquals("Tableau pile contains 3 cards.", 3, pack.size());

        CardStack s = pack.pop(factory.createCard(Card.Color.CLUBS, 12));
        Assert.assertEquals("Tableau pile contains 1 card.", 1, pack.size());
        Assert.assertEquals("Number of removed cards is 2.", 2, s.size());

        Assert.assertEquals("At top is H(11).", factory.createCard(Card.Color.HEARTS, 11), s.pop());
        Assert.assertEquals("At top is C(12).", factory.createCard(Card.Color.CLUBS, 12), s.pop());
        Assert.assertEquals("Pile is empty.", 0, s.size());
    }

    @Test
    public void testTableauPile2() {

        Card c1 = factory.createCard(Card.Color.DIAMONDS, 13);
        Card c2 = factory.createCard(Card.Color.CLUBS, 12);
        Card c3 = factory.createCard(Card.Color.HEARTS, 11);
        Card c4 = factory.createCard(Card.Color.HEARTS, 13);

        c1.turnCard();
        c2.turnCard();
        c3.turnCard();
        c4.turnCard();

        CardStack pack1 = factory.createTableauPile();
        CardStack pack2 = factory.createTableauPile();

        pack1.put(c1);
        pack1.put(c2);
        pack1.put(c3);

        CardStack s = pack1.pop(factory.createCard(Card.Color.CLUBS, 12));

        Assert.assertFalse("Cannot insert removed card group (Tableau pile is empty).", pack2.put(s));
        Assert.assertTrue("Inserting red king into empty tableau pile.", pack2.put(c4));
        Assert.assertTrue("Inserting removed card group.", pack2.put(s));
        Assert.assertEquals("Tableau pile n.2 contains 3 cards.", 3, pack2.size());
    }
}
