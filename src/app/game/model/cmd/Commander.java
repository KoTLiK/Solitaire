package app.game.model.cmd;

import java.util.ArrayList;
import java.util.List;

public class Commander {

    public interface Command extends java.io.Serializable {
        boolean execute();
        boolean undo();
    }

    public static class Invoker implements java.io.Serializable {
        private final List<Command> commands = new ArrayList<>();

        public void execute(Command cmd) {
            if (cmd.execute())
                commands.add(0, cmd);
        }

        public void undo() {
            if (commands.size() > 0)
                commands.remove(0).undo();
        }

        public boolean add(Command cmd) {
            if (cmd.execute()) {
                commands.add(0, cmd);
                return true;
            } else return false;
        }

        public int size() {
            return commands.size();
        }
    }
}
