package RMI.Shared;

public class GridPositions {

    public PositionState state;
    public boolean isChecked;
    public boolean isClickable;

    public GridPositions() {
        state = PositionState.FREE;
        isChecked = false;
        isClickable = false;
    }
}
