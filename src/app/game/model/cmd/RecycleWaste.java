package app.game.model.cmd;

import app.Solitaire;
import app.game.Game;

public class RecycleWaste implements Commander.Command {
    private final Game game;
    private int old;

    public RecycleWaste(Game game) {
        this.game = game;
    }

    @Override
    public boolean execute() {
        old = game.getScore();
        if (Solitaire.Move.recycleWaste()) {
            game.setScore(old - 100);
            return true;
        } else return false;
    }

    @Override
    public boolean undo() {
        game.setScore(old);
        return Solitaire.Move.recycleWasteUndo();
    }
}
