package app.game.model.cmd;

import app.Solitaire;

public class NextWaste implements Commander.Command {
    @Override
    public boolean execute() {
        return Solitaire.Move.nextWaste();
    }

    @Override
    public boolean undo() {
        return Solitaire.Move.nextWasteUndo();
    }
}
