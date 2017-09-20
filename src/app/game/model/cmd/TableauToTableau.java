package app.game.model.cmd;

import app.Solitaire;
import app.game.model.cards.Card;

public class TableauToTableau implements Commander.Command {
    private final int c;
    private final int src;
    private final int dst;
    private Card card;

    public TableauToTableau(int c, int src, int dst) {
        this.c = c;
        this.src = src;
        this.dst = dst;
    }

    @Override
    public boolean execute() {
        card = Solitaire.Move.tableauToTableau(c, src, dst);
        return (card != null);
    }

    @Override
    public boolean undo() {
        return Solitaire.Move.tableauToTableauUndo(card, src, dst);
    }
}
