package app.game.model.cmd;

import app.Solitaire;
import app.game.Game;

public class WasteToTableau implements Commander.Command {
    private final Game game;
    private final int dst;
    private int old;

    public WasteToTableau(Game game, int dst) {
        this.game = game;
        this.dst = dst;
    }

    @Override
    public boolean execute() {
        old = game.getScore();
        if (Solitaire.Move.wasteToTableau(dst)) {
            game.setScore(old + 5);
            return true;
        } else return false;
    }

    @Override
    public boolean undo() {
        game.setScore(old);
        return Solitaire.Move.wasteToTableauUndo(dst);
    }
}
