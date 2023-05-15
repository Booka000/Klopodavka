package gRPC.Shared;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;


public class Grid extends JPanel {

    private final int GRID_WIDTH = 12;
    private final int GRID_HEIGHT = 12;
    private final int POSITION_WIDTH = 40;
    private final int POSITION_HEIGHT = 40;
    private final int BASE = 3;
    private Point enemyBase;
    private Point base_loc;
    private int clickablePositionCounter;
    private final GridPosition[][] gridPositions;
    private PositionState ally;
    private final Image red;
    private PositionState deadAlly;
    private final Image blue;
    private PositionState enemy;
    private final Image dead_red;
    private PositionState deadEnemy;
    private final Image dead_blue;
    private ActionsListener actionsListener;
    private final Environment env;

    public Grid(Environment env) throws IOException {
        this.env = env;
        setSize(Environment.WIDTH, Environment.HEIGHT);
        setPreferredSize(new Dimension(Environment.WIDTH, Environment.HEIGHT));

        red = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/red.png")));
        blue = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/blue.png")));
        dead_red = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/dead_red.png")));
        dead_blue = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/dead_blue.png")));

         gridPositions = new GridPosition[GRID_WIDTH][GRID_HEIGHT];
         for(int i = 0; i < GRID_WIDTH;i++)
             for(int j = 0; j < GRID_WIDTH;j++) {
                 gridPositions[i][j] = new GridPosition();
                 if(i == GRID_WIDTH - BASE - 1 && j == BASE)
                     gridPositions[i][j].state = PositionState.RED;
                 else if(i == BASE && j == GRID_HEIGHT - BASE - 1)
                     gridPositions[i][j].state = PositionState.BLUE;
             }
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                mouseClickHandler(event);
            }
        });

    }

    public void setActionsListener(ActionsListener a){
        this.actionsListener = a;
    }

    private void mouseClickHandler(MouseEvent event) {
        Point position = new Point(event.getX()  / (POSITION_WIDTH + 1)
                ,event.getY()  / (POSITION_HEIGHT + 1));

        if(position.x < 0 || position.x > GRID_WIDTH - 1 ||
                position.y < 0 || position.y > GRID_HEIGHT - 1 ||
                !env.isItMyTurn() || position == enemyBase)
            return;
        if(gridPositions[position.x][position.y].isClickable) {
            if (gridPositions[position.x][position.y].state == PositionState.FREE)
                gridPositions[position.x][position.y].state = ally;
            else
                gridPositions[position.x][position.y].state = deadEnemy;
            gridPositions[position.x][position.y].isClickable = false;
            gridPositions[position.x][position.y].isExplored = false;
            checkAvaPoints(position);
            if(clickablePositionCounter == 0)
                if(this.actionsListener != null)
                    this.actionsListener.onGameIsLost();
            this.repaint();
            env.handleTurn();
            if(this.actionsListener != null)
                this.actionsListener.handleMove(position);
        }
    }

    private void draw(Graphics2D graphics2D){
        int x = 0;
        int y = 0;
        for(int i = 0; i < GRID_WIDTH; i++){
            for(int j = 0; j < GRID_HEIGHT; j++){
                Rectangle rect = new Rectangle(x, y, POSITION_WIDTH, POSITION_HEIGHT);
                graphics2D.draw(rect);
                x += POSITION_WIDTH + 1;
            }
            x = 0;
            y += POSITION_HEIGHT + 1;
        }
    }

    public void plotEnemyMove(Point position){
        if(gridPositions[position.x][position.y].state == PositionState.FREE)
            gridPositions[position.x][position.y].state = enemy;
        else if (gridPositions[position.x][position.y].state == ally)
            gridPositions[position.x][position.y].state = deadAlly;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g);

        for(int i = 0; i < GRID_WIDTH; i++)
            for(int j = 0; j < GRID_HEIGHT; j++){
                switch(gridPositions[i][j].state){
                    case RED -> g.drawImage(red, i * (POSITION_WIDTH + 1) + 1,
                            j * (POSITION_HEIGHT + 1) + 1, POSITION_WIDTH, POSITION_HEIGHT, null);
                    case BLUE -> g.drawImage(blue, i * (POSITION_WIDTH + 1) + 1,
                            j * (POSITION_HEIGHT + 1) + 1, POSITION_WIDTH, POSITION_HEIGHT, null);
                    case DEAD_RED -> g.drawImage(dead_red, i * (POSITION_WIDTH + 1) + 1,
                            j * (POSITION_HEIGHT + 1) + 1, POSITION_WIDTH, POSITION_HEIGHT, null);
                    case DEAD_BLUE -> g.drawImage(dead_blue, i * (POSITION_WIDTH + 1) + 1,
                            j * (POSITION_HEIGHT + 1) + 1, POSITION_WIDTH, POSITION_HEIGHT, null);
                }
            }
    }

    public void setColor(String color){
        if(Objects.equals(color, "red")) {
            ally = PositionState.RED;
            deadAlly = PositionState.DEAD_RED;
            enemy = PositionState.BLUE;
            deadEnemy = PositionState.DEAD_BLUE;
            base_loc = new Point(GRID_WIDTH - BASE - 1, BASE);
            enemyBase = new Point(BASE, GRID_HEIGHT - BASE - 1);
        } else if (Objects.equals(color, "blue")){
            ally = PositionState.BLUE;
            deadAlly = PositionState.DEAD_BLUE;
            enemy = PositionState.RED;
            deadEnemy = PositionState.DEAD_RED;
            base_loc = new Point(BASE,GRID_HEIGHT - BASE - 1);
            enemyBase = new Point(GRID_WIDTH - BASE - 1,BASE);
        }
    }

    private void uncheckGrid(){
        for (GridPosition[] row : gridPositions)
            for (GridPosition position : row)
                position.isExplored = false;
    }

    public  void changeTurn(boolean myTurn){
        if(myTurn){
            uncheckGrid();
            checkAvaPoints(base_loc);
            if (clickablePositionCounter == 0 )
                if(this.actionsListener != null)
                    this.actionsListener.onGameIsLost();
            this.repaint();
        } else {
            for (GridPosition[] row : gridPositions)
                for (GridPosition position : row)
                    position.isClickable = false;
            clickablePositionCounter = 0;
        }
    }

    public void checkAvaPoints(Point position) {
        if(position.x < 0 || position.x > GRID_WIDTH-1 ||
        position.y < 0 || position.y > GRID_HEIGHT-1 ||
                gridPositions[position.x][position.y].isExplored)
            return;
        if ((gridPositions[position.x][position.y].state == PositionState.FREE ||
                gridPositions[position.x][position.y].state == enemy) &&
                position != enemyBase) {
            gridPositions[position.x][position.y].isExplored = true;
            gridPositions[position.x][position.y].isClickable = true;
            clickablePositionCounter++;
        } else if (gridPositions[position.x][position.y].state == ally ||
                gridPositions[position.x][position.y].state == deadEnemy) {
            gridPositions[position.x][position.y].isExplored = true;

            checkAvaPoints(new Point(position.x-1, position.y-1));
            checkAvaPoints(new Point(position.x-1, position.y));
            checkAvaPoints(new Point(position.x-1, position.y+1));
            checkAvaPoints(new Point(position.x, position.y-1));
            checkAvaPoints(new Point(position.x, position.y+1));
            checkAvaPoints(new Point(position.x+1, position.y-1));
            checkAvaPoints(new Point(position.x+1, position.y));
            checkAvaPoints(new Point(position.x+1, position.y+1));
        }
    }
}
