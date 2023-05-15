package RMI.Shared;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
    private final GridPositions[][] gridPositions;
    private final Map<PositionState,Image> imageMap;
    private PositionState ally;
    private PositionState deadAlly;
    private PositionState enemy;
    private PositionState deadEnemy;
    private ActionsListener actionsListener;
    private final Environment env;

    public Grid(Environment env) throws IOException {
        this.env = env;
        setSize(Environment.WIDTH, Environment.HEIGHT);
        setPreferredSize(new Dimension(Environment.WIDTH, Environment.HEIGHT));

        Image red = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/red.png")));
        Image blue = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/blue.png")));
        Image dead_red = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/dead_red.png")));
        Image dead_blue = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/dead_blue.png")));

         gridPositions = new GridPositions[GRID_WIDTH][GRID_HEIGHT];
         for(int i = 0; i < GRID_WIDTH;i++)
             for(int j = 0; j < GRID_WIDTH;j++)
                 gridPositions[i][j] = new GridPositions();

         gridPositions[GRID_WIDTH - BASE - 1][BASE].state = PositionState.RED;
         gridPositions[BASE][GRID_HEIGHT - BASE - 1].state = PositionState.BLUE;

         imageMap = new HashMap<>();
         imageMap.put(PositionState.BLUE, blue);
         imageMap.put(PositionState.DEAD_BLUE, dead_blue);
         imageMap.put(PositionState.RED, red);
         imageMap.put(PositionState.DEAD_RED, dead_red);

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
            gridPositions[position.x][position.y].isChecked = false;
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
        for(int i =0; i <= GRID_WIDTH; i++){
            Line2D line2D = new Line2D.Float(i * (POSITION_WIDTH + 1), 0,
                    i * (POSITION_WIDTH + 1), GRID_HEIGHT * (POSITION_HEIGHT + 1));
            graphics2D.draw(line2D);
        }
        for(int i = 0; i < GRID_WIDTH; i++){
            Line2D lin = new Line2D.Float(0, i * (POSITION_HEIGHT +1),
                    GRID_WIDTH * (POSITION_HEIGHT + 1), i * (POSITION_HEIGHT +1));
            graphics2D.draw(lin);
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
        Graphics2D graphics2D = (Graphics2D)  g;
        draw(graphics2D);

        for(int i = 0; i < GRID_WIDTH; i++)
            for(int j = 0; j < GRID_HEIGHT; j++){
                g.drawImage(imageMap.get(gridPositions[i][j].state), i * (POSITION_WIDTH + 1) + 1,
                        j * (POSITION_HEIGHT + 1) + 1, POSITION_WIDTH, POSITION_HEIGHT, null);
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
        for(int i = 0; i < GRID_WIDTH; i++)
            for(int j = 0; j < GRID_HEIGHT; j++)
                gridPositions[i][j].isChecked = false;
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
            for(int i = 0; i < GRID_WIDTH; i++)
                for(int j = 0; j < GRID_HEIGHT; j++)
                    gridPositions[i][j].isClickable = false;
            clickablePositionCounter = 0;
        }
    }

    public void checkAvaPoints(Point position) {
        if(position.x < 0 || position.x > GRID_WIDTH-1 ||
        position.y < 0 || position.y > GRID_HEIGHT-1 ||
                gridPositions[position.x][position.y].isChecked)
            return;
        if ((gridPositions[position.x][position.y].state == PositionState.FREE ||
                gridPositions[position.x][position.y].state == enemy) &&
                position != enemyBase) {
            gridPositions[position.x][position.y].isChecked = true;
            gridPositions[position.x][position.y].isClickable = true;
            clickablePositionCounter++;
        } else if (gridPositions[position.x][position.y].state == ally ||
                gridPositions[position.x][position.y].state == deadEnemy) {
            gridPositions[position.x][position.y].isChecked = true;

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
