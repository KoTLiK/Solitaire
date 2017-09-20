package app.game.model.cmd;

import app.Solitaire;
import app.game.Game;

public class TableauToFoundation implements Commander.Command {
    private final Game game;
    private final int c;
    private final int src;
    private final int dst;
    private int old;

    public TableauToFoundation(Game game, int c, int src, int dst) {
        this.game = game;
        this.c = c;
        this.src = src;
        this.dst = dst;
    }

    @Override
    public boolean execute() {
        old = game.getScore();
        if (Solitaire.Move.tableauToFoundation(c, src, dst)) {
            game.setScore(old + 10);
            return true;
        } else return false;
    }

    @Override
    public boolean undo() {
        game.setScore(old);
        return Solitaire.Move.tableauToFoundationUndo(src, dst);
    }
}
