package gRPC.Shared;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

public class Environment extends JFrame {

    public static final int WIDTH = 492;
    public static final int HEIGHT = 492;

    protected Grid grid;

    protected int moves = 4;

    protected JLabel movesRemainLabel;

    protected boolean isItMyTurn;

    protected JLabel turnLabel;

    protected int movesRemain = 4;

    public Environment() throws IOException {
        setTitle("gRPC New Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setFocusable(true);
        setSize(WIDTH,HEIGHT);
        setResizable(false);

        grid = new Grid(this);
        add(grid);

        pack();
    }

    protected void initialize(ActionsListener actionsListener){
        grid.setActionsListener(actionsListener);
        JMenuBar menu = new JMenuBar();

         turnLabel = new JLabel("0");
         turnLabel.setBorder(new EmptyBorder(0,10,0,100));
         menu.add(turnLabel);

         movesRemainLabel = new JLabel("Moves: " + movesRemain);
         menu.add(movesRemainLabel);
         setJMenuBar(menu);
    }


    public boolean isItMyTurn() { return isItMyTurn;}

    protected void changeTurnLabel(){
        if(isItMyTurn){
            turnLabel.setText("My turn");
        } else {
            turnLabel.setText("opponent's turn");
        }
    }

    public void handleTurn() {
        movesRemain--;
        boolean shouldChangeTurn = movesRemain == 0;

        if (shouldChangeTurn) {
            isItMyTurn = !isItMyTurn;
            grid.changeTurn(isItMyTurn);
            movesRemain = moves;
            changeTurnLabel();
        }

        String movesRemainText = String.format("Moves: %d", movesRemain);
        movesRemainLabel.setText(movesRemainText);
    }

    public void plotEnemyMove(Point point){
        grid.plotEnemyMove(point);
        this.handleTurn();
    }

}
