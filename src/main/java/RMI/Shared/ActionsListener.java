package RMI.Shared;

import java.awt.*;

public interface ActionsListener {
    void handleMove(Point position);
    void onGameIsLost();
}
