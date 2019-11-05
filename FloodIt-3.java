/*
 *
 *
 *  IMPORTANT!!!
 *  Normal game is first half of code
 * 
 *  Comment out top part and uncomment out bottom
 *  part to play 2 player FloodIt
 *  Tests run with board size of 2
 *  Play with board size of 20
 *
 *
 *
 *
 *
 */




import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;





//Represents a single square of the game area
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  // constructor for cell
  Cell(int x, int y, Color color, boolean flooded) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  //checks to see if given cell color equals color
  public boolean checkColor(Color c) {
    return this.color.equals(c);
  }

  //Contains function for posns
  public boolean contains(Posn pos) {
    boolean xContains = (pos.x <= (x * FloodItWorld.CELL_SIZE) + FloodItWorld.CELL_SIZE)
        && (pos.x >= (x * FloodItWorld.CELL_SIZE));
    boolean yContains = (pos.y <= (y * FloodItWorld.CELL_SIZE) + FloodItWorld.CELL_SIZE)
        && (pos.y >= (y * FloodItWorld.CELL_SIZE));

    return xContains && yContains;
  }

  //returns the flood score
  int flood(Color col, ArrayList<Boolean> visited, int flooded) {
    //this.color = col;
    this.flooded = true;
    int arrayPos = (this.y * FloodItWorld.BOARD_SIZE) + this.x;
    visited.set(arrayPos, true);
    flooded ++;

    if (this.bottom != null) {
      if ((this.bottom.checkColor(col))
          && !visited.get(arrayPos + FloodItWorld.BOARD_SIZE)) {
        flooded = this.bottom.flood(col, visited, flooded);
      }
    }

    if (this.right != null) {
      if ((this.right.checkColor(col))
          && !visited.get(arrayPos + 1)) {
        flooded = this.right.flood(col, visited, flooded);
      }
    }

    if (this.top != null) {
      if ((this.top.checkColor(col))
          && !visited.get(arrayPos -  FloodItWorld.BOARD_SIZE)) {
        flooded = this.top.flood(col, visited, flooded);
      }
    }

    if (this.left != null) {
      if ((this.left.checkColor(col))
          && !visited.get(arrayPos - 1)) {
        flooded = this.left.flood(col, visited, flooded);
      }
    }

    return flooded;
  }

}

//class for FloodItWorld
class FloodItWorld extends World {
  //All the cells of the game
  int clicks;
  ArrayList<Cell> queue;
  ArrayList<Cell> board;
  boolean flooding;
  static  final int CELL_SIZE = 20;
  int numColors;
  int flooded;
  Color floodColor;


  // for best experience set board size to 20
  // tests pass with a board size of 2
  static final int BOARD_SIZE = 2;
  static final ArrayList<Color> COLORZ =
      new ArrayList<Color>(Arrays.asList(Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE,
          Color.MAGENTA, Color.CYAN));
  int maxClicks;


  FloodItWorld(int numColors) {
    if (numColors > 6) {
      throw new IllegalArgumentException("too many colors");
    }
    else {
      this.numColors = numColors;
    }

    this.board = new ArrayList<Cell>();
    this.makeBoard();
    this.flooding = false;
    this.queue = new ArrayList<Cell>();
    this.clicks = 0;
    this.maxClicks = FloodItWorld.BOARD_SIZE * 2;
  }



  //On Key Event. if R is pressed board refreshes
  public void onKeyEvent(String ke) {
    if (flooding) {
      return;
    }
    super.onKeyEvent(ke);
    if (ke.equals("r")) {
      this.board = new ArrayList<Cell>();
      this.makeBoard();
    }
  }


  //On-Mouse Event
  public void onMouseClicked(Posn mouse) {
    if (flooding) {
      return;
    }
    if (mouse.x < FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE
        && mouse.y < FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE) {
      floodColor = new ArrayUtils().getCellColor(mouse, this.board);
      if (floodColor.equals(board.get(0).color)) {
        return;
      }

      ArrayList<Boolean> visited = new ArrayList<Boolean>();
      for (int i = 0; i < FloodItWorld.BOARD_SIZE *  FloodItWorld.BOARD_SIZE; i ++) {
        visited.add(false);
      }

      flooded = board.get(0).flood(board.get(0).color, visited, 0);

      queue.add(board.get(0));

      this.flooding = true;
      clicks++;
    }
  }

  // animates the flooding
  public void animateFlood() {
    int limit = queue.size();
    for (int i = 0; i < limit; i ++) {
      queue.get(i).color = floodColor;
      queue.get(i).flooded = false;

      if (queue.get(i).left != null && queue.get(i).left.flooded) {
        queue.add(queue.get(i).left);
      }
      if (queue.get(i).right != null && queue.get(i).right.flooded) {
        queue.add(queue.get(i).right);
      }
      if (queue.get(i).top != null && queue.get(i).top.flooded) {
        queue.add(queue.get(i).top);
      }
      if (queue.get(i).bottom != null && queue.get(i).bottom.flooded) {
        queue.add(queue.get(i).bottom);
      }
      queue.remove(i);
      limit--;

    }
  }


  //Make scene method, creates the scene
  public WorldScene makeScene() {
    if (flooding) {
      animateFlood();
    }
    if (queue.size() == 0) {
      ArrayList<Boolean> visited = new ArrayList<Boolean>();
      for (int i = 0; i < FloodItWorld.BOARD_SIZE *  FloodItWorld.BOARD_SIZE; i ++) {
        visited.add(false);
      }
      flooded = board.get(0).flood(floodColor, visited, 0);

      this.flooding = false;
    }

    int wh = FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE;
    WorldScene w = new WorldScene(wh + 150, wh + 150);
    w.placeImageXY(new TextImage(Integer.toString(this.flooded), 25, Color.red), 50, wh + 50);
    w.placeImageXY(new TextImage(Integer.toString(this.clicks) + "/"
        + Integer.toString(maxClicks), 25, Color.red), 150, wh + 50);
    return new ArrayUtils().drawCells(this.board, w);
  }

  //Creates the board for the game
  void makeBoard() {
    this.board = new ArrayList<Cell>();
    for (int y = 0; y < FloodItWorld.BOARD_SIZE; y++) {
      for (int x = 0; x < FloodItWorld.BOARD_SIZE; x++) {
        int temp = new Random().nextInt(this.numColors);

        this.board.add(new Cell(x, y, FloodItWorld.COLORZ.get(temp), false));
      }
    }

    for (int y = 0; y < FloodItWorld.BOARD_SIZE; y++) {
      int row = y * FloodItWorld.BOARD_SIZE;

      for (int x = 0; x < FloodItWorld.BOARD_SIZE; x++) {
        if (x > 0) {
          this.board.get(row + x).left = this.board.get(row + x - 1);
        }
        if (x < FloodItWorld.BOARD_SIZE - 1) {
          this.board.get(row + x).right = this.board.get(row + x + 1);
        }
        if (y > 0) {
          this.board.get(row + x).top = this.board.get(row + x - FloodItWorld.BOARD_SIZE);
        }
        if (y < FloodItWorld.BOARD_SIZE - 1) {
          this.board.get(row + x).bottom = this.board.get(row + x + FloodItWorld.BOARD_SIZE);
        }
      }
    }


    ArrayList<Boolean> visited = new ArrayList<Boolean>();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE *  FloodItWorld.BOARD_SIZE; i ++) {
      visited.add(false);
    }
    this.flooded = board.get(0).flood(this.board.get(0).color, visited, 0);
    floodColor = this.board.get(0).color;
    this.clicks = 0;
  }


  //creates the randomized board for the tester
  void makeBoard(int temp, int bz) {
    this.board = new ArrayList<Cell>();
    for (int y = 0; y < bz; y++) {
      for (int x = 0; x < bz; x++) {
        this.board.add(new Cell(x, y, FloodItWorld.COLORZ.get(temp), false));
      }
    }

    for (int y = 0; y < bz; y++) {
      int row = y * bz;

      for (int x = 0; x < bz; x++) {
        if (x > 0) {
          this.board.get(row + x).left = this.board.get(row + x - 1);
        }
        if (x < bz - 1) {
          this.board.get(row + x).right = this.board.get(row + x + 1);
        }
        if (y > 0) {
          this.board.get(row + x).top = this.board.get(row + x - bz);
        }
        if (y < bz - 1) {
          this.board.get(row + x).bottom = this.board.get(row + x + bz);
        }
      }
    }


    ArrayList<Boolean> visited = new ArrayList<Boolean>();
    for (int i = 0; i < bz *  bz; i ++) {
      visited.add(false);
    }
    this.flooded = board.get(0).flood(this.board.get(0).color, visited, 0);
    floodColor = this.board.get(0).color;
    this.clicks = 0;
  }

  //the world ends with either a winner a loser or a tie else continues game
  public WorldEnd worldEnds() {
    WorldScene empty = new WorldScene(FloodItWorld.BOARD_SIZE + 150, FloodItWorld.BOARD_SIZE + 150);
    if (this.flooded == FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE
        || this.clicks == this.maxClicks) {
      if (this.clicks == this.maxClicks) {
        empty.placeImageXY(new TextImage("You Lose!", 25, Color.RED),
            FloodItWorld.BOARD_SIZE + 50, FloodItWorld.BOARD_SIZE + 50);
      } else {
        empty.placeImageXY(new TextImage("You Win!", 25, Color.RED),
            FloodItWorld.BOARD_SIZE + 50, FloodItWorld.BOARD_SIZE + 50);
      }
      return new WorldEnd(true, empty);
    } else {
      return new WorldEnd(false, this.makeScene());
    }
  }

}

//ArrayUtils Class
class ArrayUtils {
  //draws the board of cells
  WorldScene drawCells(ArrayList<Cell> board, WorldScene w) {
    for (Cell i: board) {
      WorldImage temp =
          new RectangleImage(FloodItWorld.CELL_SIZE,
              FloodItWorld.CELL_SIZE,
              OutlineMode.SOLID, i.color);
      w.placeImageXY(temp, i.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2,
          i.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);
    }
    return w;
  }


  //Gets the color of the cell
  Color getCellColor(Posn mouse, ArrayList<Cell> board) {
    for (int i = 0; i < FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE; i++) {
      if (board.get(i).contains(mouse)) {
        return board.get(i).color;
      }
    }
    return Color.black;
  }
}




//Examples Class
class ExamplesWorld {
  FloodItWorld q = new FloodItWorld(5);
  FloodItWorld a = new FloodItWorld(1);
  ArrayList<Cell> testBoard;
  ArrayList<Cell> testBoard2;


  Color g = Color.green;

  Color red = Color.red;

  Posn posn1 = new Posn(10, 0);
  Posn posn2 = new Posn(150, 50);
  Posn posn3 = new Posn(8, 8);

  void init() {

    q = new FloodItWorld(5);
    int randomSeed = 2;
    q.makeBoard(randomSeed, 2);


    a = new FloodItWorld(1);
    a.makeBoard(randomSeed, 2);

    testBoard = new ArrayList<Cell>();
    testBoard2 = new ArrayList<Cell>();

    Cell cell1 = new Cell(0, 0, Color.green, true);
    Cell cell2 = new Cell(1, 0, Color.green, true);
    Cell cell3 = new Cell(0, 1, Color.green, true);
    Cell cell4 = new Cell(1, 1, Color.green, true);

    cell1.bottom = cell3;
    cell1.right = cell2;

    cell2.left = cell1;
    cell2.bottom = cell4;

    cell3.top = cell1;
    cell3.right = cell4;

    cell4.top = cell2;
    cell4.left = cell3;

    testBoard.add(cell1);
    testBoard.add(cell2);
    testBoard.add(cell3);
    testBoard.add(cell4);

    Cell cella = new Cell(2, 0, Color.red, true);
    Cell cellb = new Cell(10, 0, Color.blue, true);

    cella.right = cellb;
    cellb.left = cella;

    testBoard2.add(cella);
    testBoard2.add(cellb);
  }


  // test making the board
  void testMakeBoard(Tester t) {
    init();
    t.checkExpect(q.board, testBoard);
    // t.checkExpect(a.board, testBoard2);
  }


  // tests creating the scene
  void testMakeScene(Tester t) {
    init();
    int wh = FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE;
    WorldScene w = new WorldScene(wh + 150, wh + 150);
    RectangleImage greenRect = new RectangleImage(FloodItWorld.CELL_SIZE,
        FloodItWorld.CELL_SIZE, OutlineMode.SOLID, Color.green);

    w.placeImageXY(new TextImage(Integer.toString(4), 25, Color.red), 50, wh + 50);
    w.placeImageXY(new TextImage(Integer.toString(0) + "/"
        + Integer.toString(4), 25, Color.red), 150, wh + 50);

    w.placeImageXY(greenRect, 10, 10);
    w.placeImageXY(greenRect, 10, 30);
    w.placeImageXY(greenRect, 30, 10);
    w.placeImageXY(greenRect, 30, 30);

    t.checkExpect(q.makeScene(), w);

  }

  // tests on key event if r is pressed board resets
  void testOnKey(Tester t) {
    init();
    q.onKeyEvent("r");
    t.checkFail(q.board, testBoard);

    q.onKeyEvent("t");
    t.checkExpect(q.board, q.board);

    q.onKeyEvent(" ");
    t.checkExpect(q.board, q.board);

    q.onKeyEvent("m");
    t.checkExpect(q.board, q.board);

    q.onKeyEvent("q");
    t.checkExpect(q.board, q.board);

    q.onKeyEvent("mom");
    t.checkExpect(q.board, q.board);
  }

  // tests if color matches
  void testcheckColor(Tester t) {
    init();
    t.checkExpect(q.board.get(0).checkColor(red), false);
    t.checkExpect(q.board.get(0).checkColor(Color.GREEN), true);
  }


  // tests if posn is contained
  void testContains(Tester t) {
    init();
    t.checkExpect(q.board.get(0).contains(posn1), true);
    t.checkExpect(q.board.get(0).contains(posn3), true);
    t.checkExpect(q.board.get(0).contains(posn2), false);
  }

  // tests getting cell color
  void testGetCellColor(Tester t) {
    init();
    t.checkExpect(new ArrayUtils().getCellColor(posn1, q.board), Color.GREEN);
    t.checkExpect(new ArrayUtils().getCellColor(posn2, q.board), Color.BLACK);

  }


  //tests drawing cells
  void testDrawCells(Tester t) {
    init();
    int wh = FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE;
    WorldScene w = new WorldScene(wh + 150, wh + 150);
    RectangleImage greenRect = new RectangleImage(FloodItWorld.CELL_SIZE,
        FloodItWorld.CELL_SIZE, OutlineMode.SOLID, Color.green);

    w.placeImageXY(greenRect, 10, 10);
    w.placeImageXY(greenRect, 10, 30);
    w.placeImageXY(greenRect, 30, 10);
    w.placeImageXY(greenRect, 30, 30);

    t.checkExpect(new ArrayUtils().drawCells(q.board, new WorldScene(wh + 150, wh + 150)), w);
  }

  // tests the flooding method for a cell
  void testFlood(Tester t) {
    init();
    ArrayList<Boolean> visited = new ArrayList<Boolean>();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE; i ++) {
      visited.add(false);
    }
    t.checkExpect(q.board.get(0).flood(Color.RED, visited, 0), 1);

    visited = new ArrayList<Boolean>();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE; i ++) {
      visited.add(false);
    }
    t.checkExpect(q.board.get(0).flood(Color.GREEN, visited, 0), 4);
  }


  // tests the flooding event
  void testActionWithFlooding(Tester t) {
    init();
    q.flooding = true;
    FloodItWorld copy = q;
    q.onMouseClicked(posn1);
    t.checkExpect(q, copy);

  }


  // tests the queue function to animate the flooding
  void testAnimateFlood(Tester t) {
    init();
    q.queue.add(q.board.get(0));
    q.animateFlood();

    t.checkExpect(q.queue.size(), 2);
  }

  // test on mouse click
  void testOnMouseClick(Tester t) {
    init();
    FloodItWorld copy = q;
    q.onMouseClicked(posn2);
    t.checkExpect(q, copy);
    q.onMouseClicked(posn1);
    t.checkExpect(q, copy);
  }

  //tests ending world
  void testEndWorld(Tester t) {
    init();
    WorldScene empty = new WorldScene(FloodItWorld.BOARD_SIZE + 150, FloodItWorld.BOARD_SIZE + 150);

    empty.placeImageXY(new TextImage("You Win!", 25, Color.RED),
        FloodItWorld.BOARD_SIZE + 50, FloodItWorld.BOARD_SIZE + 50);
    WorldEnd end = new WorldEnd(true, empty);
    t.checkExpect(q.worldEnds(), end);
  }

  // tests the actual game
  void testBigBang(Tester t) {
    int worldWidth = FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE + 150;
    worldWidth = 500;
    int worldHeight = worldWidth;
    double tickRate = .1;
    q.bigBang(worldWidth, worldHeight, tickRate);

  }

}



/*


// Represents a single square of the game area
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded1;
  boolean flooded2;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  // constructor
  Cell(int x, int y, Color color, boolean flooded1, boolean flooded2) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded1 = flooded1;
    this.flooded2 = flooded2;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  //checks to make sure color equals given color
  public boolean checkColor(Color c) {
    return this.color.equals(c);
  }

  //contains function true if posn in in given constraints
  public boolean contains(Posn pos) {
    boolean xContains = (pos.x <= (x * FloodItWorld.CELL_SIZE) + FloodItWorld.CELL_SIZE)
        && (pos.x >= (x * FloodItWorld.CELL_SIZE));
    boolean yContains = (pos.y <= (y * FloodItWorld.CELL_SIZE) + FloodItWorld.CELL_SIZE)
        && (pos.y >= (y * FloodItWorld.CELL_SIZE));

    return xContains && yContains;
  }

  //contains function true if posn in in colorz sidebar
  public boolean colorzContains(Posn pos) {
    boolean xContains = (pos.x <= (x + FloodItWorld.CELL_SIZE / 2))
        && (pos.x >= (x - FloodItWorld.CELL_SIZE / 2));
    boolean yContains = (pos.y <= (y + FloodItWorld.CELL_SIZE / 2))
        && (pos.y >= (y - FloodItWorld.CELL_SIZE / 2));

    return xContains && yContains;
  }

  //get flooded method returns flooded 1 or 2 for player
  public boolean getFlooded(int player) {
    if (player == 1) {
      return flooded1;
    }
    return  flooded2;
  }

  //set flooded method for player
  public void setFlooded(int player, boolean f) {
    if (player == 1) {
      flooded1 = f;
    } else {
      flooded2 = f;
    }
  }

  //int flood represents floodIt score
  int flood(Color col, ArrayList<Boolean> visited, int flooded, int player) {
    //this.color = col;
    this.setFlooded(player, true);
    int arrayPos = (this.y * FloodItWorld.BOARD_SIZE) + this.x;
    visited.set(arrayPos, true);
    flooded ++;

    if (this.top != null) {
      if ((this.top.checkColor(col) || this.top.getFlooded(player))
          && !visited.get(arrayPos -  FloodItWorld.BOARD_SIZE)) {
        flooded = this.top.flood(col, visited, flooded, player);
      }
    }

    if (this.bottom != null) {
      if ((this.bottom.checkColor(col) || this.bottom.getFlooded(player))
          && !visited.get(arrayPos + FloodItWorld.BOARD_SIZE)) {
        flooded = this.bottom.flood(col, visited, flooded, player);
      }
    }

    if (this.right != null) {
      if ((this.right.checkColor(col) || this.right.getFlooded(player))
          && !visited.get(arrayPos + 1)) {
        flooded = this.right.flood(col, visited, flooded, player);
      }
    }

    if (this.left != null) {
      if ((this.left.checkColor(col) || this.left.getFlooded(player))
          && !visited.get(arrayPos - 1)) {
        flooded = this.left.flood(col, visited, flooded, player);
      }
    }

    return flooded;
  }

}

//class for FloodItWorld
class FloodItWorld extends World {

  //All the cells of the game
  ArrayList<Cell> board;
  ArrayList<Cell> queue;
  Color floodColor;
  boolean flooding;
  static  final int CELL_SIZE = 20;
  int numColors;
  int flooded1;
  int flooded2;
  int playermove;



  // for best experience set board size to 20
  // tests pass with a board size of 2
  static final int BOARD_SIZE = 2;




  static final ArrayList<Color> COLORZ =
      new ArrayList<Color>(Arrays.asList(Color.BLUE, Color.RED, Color.GREEN,
          Color.ORANGE, Color.MAGENTA, Color.CYAN));
  static final Cell NULLCELL = new Cell(0, 0, null, false, false);

  ArrayList<Cell> colorz;


  //checks if too many colors are added then throws exception if over 6
  FloodItWorld(int numColors) {
    if (numColors > 6) {
      throw new IllegalArgumentException("too many colors");
    }
    else {
      this.numColors = numColors;
    }

    this.board = new ArrayList<Cell>();
    this.queue = new ArrayList<Cell>();
    this.makeBoard();
    this.flooding = false;
    this.playermove = 0;
    this.colorz = new ArrayList<Cell>();

    for (int i = 0; i < numColors; i ++) {
      colorz.add(new Cell(450, 200 + i * FloodItWorld.CELL_SIZE, COLORZ.get(i), false, false));
    }


  }

  //on key event if r is pressed board resets
  public void onKeyEvent(String ke) {
    super.onKeyEvent(ke);
    if (ke.equals("r")) {
      this.board = new ArrayList<Cell>();
      this.makeBoard();
    }
  }

  //onMouseClicked event increases player move after click
  public void onMouseClicked(Posn mouse) {

    floodColor = new ArrayUtils().getCellColor(mouse, this.board, this.colorz);

    if (floodColor.equals(Color.black)) {
      return;
    }

    if (floodColor.equals(board.get(0).color) || (floodColor.equals(
        board.get(FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE - 1).color))) {
      return;
    }

    ArrayList<Boolean> visited = new ArrayList<Boolean>();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE *  FloodItWorld.BOARD_SIZE; i ++) {
      visited.add(false);
    }
    ArrayList<Cell> copy = new ArrayList<Cell>();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE *  FloodItWorld.BOARD_SIZE; i ++) {
      copy.add(board.get(i));
    }
    this.flooding = true;
    if (playermove % 2 == 0) {
      flooded1 = board.get(0).flood(board.get(0).color, visited, 0, 1);
      queue.add(board.get(0));

    } else {
      Cell botcell = board.get(FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE - 1);
      flooded2 = botcell.flood(botcell.color, visited, 0, 2);
      queue.add(botcell);

    }
    playermove++;

  }

  //animates the flooding
  public void animateFlood(int player) {
    int limit = queue.size();
    for (int i = 0; i < limit; i ++) {
      queue.get(i).color = floodColor;
      queue.get(i).setFlooded(player, false);

      if (queue.get(i).left != null && queue.get(i).left.getFlooded(player)) {
        queue.add(queue.get(i).left);
      }
      if (queue.get(i).right != null && queue.get(i).right.getFlooded(player)) {
        queue.add(queue.get(i).right);
      }
      if (queue.get(i).top != null && queue.get(i).top.getFlooded(player)) {
        queue.add(queue.get(i).top);
      }
      if (queue.get(i).bottom != null && queue.get(i).bottom.getFlooded(player)) {
        queue.add(queue.get(i).bottom);
      }
      queue.remove(i);
      limit--;

    }
  }

  //MakeScene method for the world
  public WorldScene makeScene() {
    if (flooding) {
      animateFlood(playermove % 2);
    }
    if (queue.size() == 0) {
      this.flooding = false;

      ArrayList<Boolean> visited = new ArrayList<Boolean>();
      for (int i = 0; i < FloodItWorld.BOARD_SIZE *  FloodItWorld.BOARD_SIZE; i ++) {
        visited.add(false);
      }
      if (playermove % 2 == 1) {
        flooded1 = board.get(0).flood(board.get(0).color, visited, 0, 1);

      } else {
        Cell botcell = board.get(FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE - 1);
        flooded2 = botcell.flood(botcell.color, visited, 0, 2);
      }

    }

    int wh = FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE;
    WorldScene w = new WorldScene(wh + 150, wh + 150);

    for (Cell i: colorz) {
      w.placeImageXY(new RectangleImage(FloodItWorld.CELL_SIZE,
          FloodItWorld.CELL_SIZE, OutlineMode.SOLID, i.color),
          i.x, i.y);
    }

    w.placeImageXY(new TextImage("P1 " + Integer.toString(flooded1), 25, Color.red), 100, 450);
    w.placeImageXY(new TextImage("P2: " + Integer.toString(flooded2), 25, Color.red), 350, 450);
    w.placeImageXY(new TextImage("P" + Integer.toString(playermove %  2 + 1),
        25, Color.red), 450, 100);
    return new ArrayUtils().drawCells(this.board, w);
  }




  //makes the board for the game with columns and rows
  void makeBoard() {
    this.board = new ArrayList<Cell>();
    for (int y = 0; y < FloodItWorld.BOARD_SIZE; y++) {
      for (int x = 0; x < FloodItWorld.BOARD_SIZE; x++) {
        int temp = new Random().nextInt(this.numColors);

        this.board.add(new Cell(x, y, FloodItWorld.COLORZ.get(temp), false, false));
      }
    }

    for (int y = 0; y < FloodItWorld.BOARD_SIZE; y++) {
      int row = y * FloodItWorld.BOARD_SIZE;

      for (int x = 0; x < FloodItWorld.BOARD_SIZE; x++) {
        if (x > 0) {
          this.board.get(row + x).left = this.board.get(row + x - 1);
        }
        if (x < FloodItWorld.BOARD_SIZE - 1) {
          this.board.get(row + x).right = this.board.get(row + x + 1);
        }
        if (y > 0) {
          this.board.get(row + x).top = this.board.get(row + x - FloodItWorld.BOARD_SIZE);
        }
        if (y < FloodItWorld.BOARD_SIZE - 1) {
          this.board.get(row + x).bottom = this.board.get(row + x + FloodItWorld.BOARD_SIZE);
        }
      }
    }

    ArrayList<Boolean> visited = new ArrayList<Boolean>();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE *  FloodItWorld.BOARD_SIZE; i ++) {
      visited.add(false);
    }
    flooded1 = board.get(0).flood(this.board.get(0).color, visited, 0, 1);

    Cell botright = board.get(FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE - 1);
    visited = new ArrayList<Boolean>();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE *  FloodItWorld.BOARD_SIZE; i ++) {
      visited.add(false);
    }
    flooded2 = botright.flood(botright.color, visited, 0, 2);
  }

  //MakeBoard method creates for tests
  void makeBoard(int temp) {
    this.board = new ArrayList<Cell>();
    for (int y = 0; y < FloodItWorld.BOARD_SIZE; y++) {
      for (int x = 0; x < FloodItWorld.BOARD_SIZE; x++) {
        this.board.add(new Cell(x, y, Color.GREEN, false, false));
      }
    }

    for (int y = 0; y < FloodItWorld.BOARD_SIZE; y++) {
      int row = y * FloodItWorld.BOARD_SIZE;

      for (int x = 0; x < FloodItWorld.BOARD_SIZE; x++) {
        if (x > 0) {
          this.board.get(row + x).left = this.board.get(row + x - 1);
        }
        if (x < FloodItWorld.BOARD_SIZE - 1) {
          this.board.get(row + x).right = this.board.get(row + x + 1);
        }
        if (y > 0) {
          this.board.get(row + x).top = this.board.get(row + x - FloodItWorld.BOARD_SIZE);
        }
        if (y < FloodItWorld.BOARD_SIZE - 1) {
          this.board.get(row + x).bottom = this.board.get(row + x + FloodItWorld.BOARD_SIZE);
        }
      }
    }

    this.board.get(3).color = Color.blue;

    ArrayList<Boolean> visited = new ArrayList<Boolean>();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE *  FloodItWorld.BOARD_SIZE; i ++) {
      visited.add(false);
    }
    flooded1 = board.get(0).flood(Color.green, visited, 0, 1);

    Cell botright = board.get(FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE - 1);
    visited = new ArrayList<Boolean>();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE *  FloodItWorld.BOARD_SIZE; i ++) {
      visited.add(false);
    }
    flooded2 = botright.flood(Color.blue, visited, 0, 2);
  }

  //End World method. Game ends if turns are reached or board is flooded
  public WorldEnd worldEnds() {
    WorldScene empty = new WorldScene(FloodItWorld.BOARD_SIZE + 150, FloodItWorld.BOARD_SIZE + 150);
    if (this.flooded1 + this.flooded2 == FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE) {
      if (this.flooded1 > this.flooded2) {
        empty.placeImageXY(new TextImage("Player 1 Wins!", 25, Color.RED),
            FloodItWorld.BOARD_SIZE + 150, FloodItWorld.BOARD_SIZE + 50);
      } else if (this.flooded2 > this.flooded1) {
        empty.placeImageXY(new TextImage("Player 2 Wins!", 25, Color.RED),
            FloodItWorld.BOARD_SIZE + 150, FloodItWorld.BOARD_SIZE + 50);
      } else {
        empty.placeImageXY(new TextImage("It's a tie!", 25, Color.RED),
            FloodItWorld.BOARD_SIZE + 150, FloodItWorld.BOARD_SIZE + 50);
      }
      return new WorldEnd(true, empty);
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }
}

//ArrayUtils Class
class ArrayUtils {
  //draws the board of cells
  WorldScene drawCells(ArrayList<Cell> board, WorldScene w) {
    for (Cell i: board) {
      WorldImage temp =
          new RectangleImage(FloodItWorld.CELL_SIZE,
              FloodItWorld.CELL_SIZE,
              OutlineMode.SOLID, i.color);
      w.placeImageXY(temp, i.x * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2,
          i.y * FloodItWorld.CELL_SIZE + FloodItWorld.CELL_SIZE / 2);
    }
    return w;
  }


  //Gets the color of the cell if cell is not on board color is black
  Color getCellColor(Posn mouse, ArrayList<Cell> board, ArrayList<Cell> colorz) {
    for (int i = 0; i < FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE; i++) {
      if (board.get(i).contains(mouse)) {
        return board.get(i).color;
      }
    }

    for (Cell c: colorz) {
      if (c.colorzContains(mouse)) {
        return c.color;
      }
    }

    return Color.black;
  }
}



//Examples Class
class ExamplesWorld {
  FloodItWorld q = new FloodItWorld(5);
  FloodItWorld a = new FloodItWorld(1);
  ArrayList<Cell> testBoard;


  Color g = Color.green;

  Color red = Color.red;

  Posn posn1 = new Posn(10, 0);
  Posn posn2 = new Posn(150, 50);
  Posn posn3 = new Posn(8, 8);

  void init() {

    q = new FloodItWorld(5);
    q.makeBoard(2);

    testBoard = new ArrayList<Cell>();

    Cell cell1 = new Cell(0, 0, Color.green, true, false);
    Cell cell2 = new Cell(1, 0, Color.green, true, false);
    Cell cell3 = new Cell(0, 1, Color.green, true, false);
    Cell cell4 = new Cell(1, 1, Color.blue, false, true);

    cell1.bottom = cell3;
    cell1.right = cell2;

    cell2.left = cell1;
    cell2.bottom = cell4;

    cell3.top = cell1;
    cell3.right = cell4;

    cell4.top = cell2;
    cell4.left = cell3;

    testBoard.add(cell1);
    testBoard.add(cell2);
    testBoard.add(cell3);
    testBoard.add(cell4);

  }


  // test making the board
  void testMakeBoard(Tester t) {
    init();
    t.checkExpect(q.board, testBoard);
    // t.checkExpect(a.board, testBoard2);
  }


  // tests creating the scene
  void testMakeScene(Tester t) {
    init();
    int wh = FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE;
    WorldScene w = new WorldScene(wh + 150, wh + 150);
    RectangleImage greenRect = new RectangleImage(FloodItWorld.CELL_SIZE,
        FloodItWorld.CELL_SIZE, OutlineMode.SOLID, Color.green);

    RectangleImage blueRect = new RectangleImage(FloodItWorld.CELL_SIZE,
        FloodItWorld.CELL_SIZE, OutlineMode.SOLID, Color.blue);

    for (Cell i: q.colorz) {
      w.placeImageXY(new RectangleImage(FloodItWorld.CELL_SIZE,
          FloodItWorld.CELL_SIZE, OutlineMode.SOLID, i.color),
          i.x, i.y);
    }

    w.placeImageXY(new TextImage("P1 " + Integer.toString(q.flooded1), 25, Color.red), 100, 450);
    w.placeImageXY(new TextImage("P2: " + Integer.toString(q.flooded2), 25, Color.red), 350, 450);
    w.placeImageXY(new TextImage("P" + Integer.toString(q.playermove %  2 + 1),
        25, Color.red), 450, 100);

    w.placeImageXY(greenRect, 10, 10);
    w.placeImageXY(greenRect, 10, 30);
    w.placeImageXY(greenRect, 30, 10);
    w.placeImageXY(blueRect, 30, 30);

    t.checkExpect(q.makeScene(), w);

  }

  // tests on key event if r is pressed board resets
  void testOnKey(Tester t) {
    init();
    q.onKeyEvent("r");
    t.checkFail(q.board, testBoard);

    q.onKeyEvent("t");
    t.checkExpect(q.board, q.board);

    q.onKeyEvent(" ");
    t.checkExpect(q.board, q.board);

    q.onKeyEvent("m");
    t.checkExpect(q.board, q.board);

    q.onKeyEvent("q");
    t.checkExpect(q.board, q.board);

    q.onKeyEvent("mom");
    t.checkExpect(q.board, q.board);
  }

  // tests if color matches
  void testcheckColor(Tester t) {
    init();
    t.checkExpect(q.board.get(0).checkColor(red), false);
    t.checkExpect(q.board.get(0).checkColor(Color.GREEN), true);
  }


  // tests if posn is contained
  void testContains(Tester t) {
    init();
    t.checkExpect(q.board.get(0).contains(posn1), true);
    t.checkExpect(q.board.get(0).contains(posn3), true);
    t.checkExpect(q.board.get(0).contains(posn2), false);
  }

  // tests getting cell color
  void testGetCellColor(Tester t) {
    init();
    t.checkExpect(new ArrayUtils().getCellColor(posn1, q.board, q.colorz), Color.GREEN);
    t.checkExpect(new ArrayUtils().getCellColor(posn2, q.board, q.colorz), Color.BLACK);

  }


  //tests drawing cells
  void testDrawCells(Tester t) {
    init();
    int wh = FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE;
    WorldScene w = new WorldScene(wh + 150, wh + 150);
    RectangleImage greenRect = new RectangleImage(FloodItWorld.CELL_SIZE,
        FloodItWorld.CELL_SIZE, OutlineMode.SOLID, Color.green);
    RectangleImage blueRect = new RectangleImage(FloodItWorld.CELL_SIZE,
        FloodItWorld.CELL_SIZE, OutlineMode.SOLID, Color.blue);

    w.placeImageXY(greenRect, 10, 10);
    w.placeImageXY(greenRect, 10, 30);
    w.placeImageXY(greenRect, 30, 10);
    w.placeImageXY(blueRect, 30, 30);

    t.checkExpect(new ArrayUtils().drawCells(q.board, new WorldScene(wh + 150, wh + 150)), w);
  }

  // tests the flooding method for a cell
  void testFlood(Tester t) {
    init();
    ArrayList<Boolean> visited = new ArrayList<Boolean>();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE; i ++) {
      visited.add(false);
    }
    t.checkExpect(q.board.get(0).flood(Color.RED, visited, 0, 1), 3);

    visited = new ArrayList<Boolean>();
    for (int i = 0; i < FloodItWorld.BOARD_SIZE * FloodItWorld.BOARD_SIZE; i ++) {
      visited.add(false);
    }
    t.checkExpect(q.board.get(0).flood(Color.GREEN, visited, 0, 1), 3);
  }


  // tests the flooding event
  void testActionWithFlooding(Tester t) {
    init();
    q.flooding = true;
    FloodItWorld copy = q;
    q.onMouseClicked(posn1);
    t.checkExpect(q, copy);

  }


  // tests the queue function to animate the flooding
  void testAnimateFlood(Tester t) {
    init();
    q.queue.add(q.board.get(0));
    q.animateFlood(1);

    t.checkExpect(q.queue.size(), 2);
  }

  // test on mouse click
  void testOnMouseClick(Tester t) {
    init();
    FloodItWorld copy = q;
    q.onMouseClicked(posn2);
    t.checkExpect(q, copy);
    q.onMouseClicked(posn1);
    t.checkExpect(q, copy);
  }

  //tests ending world
  void testEndWorld(Tester t) {
    init();
    WorldScene empty = new WorldScene(FloodItWorld.BOARD_SIZE + 150,
        FloodItWorld.BOARD_SIZE + 150);

    empty.placeImageXY(new TextImage("Player 1 Wins!", 25, Color.RED),
        FloodItWorld.BOARD_SIZE + 150, FloodItWorld.BOARD_SIZE + 50);
    WorldEnd end = new WorldEnd(true, empty);
    t.checkExpect(q.worldEnds(), end);
  }

  // tests colorz contains function
  void testColorContains(Tester t) {
    init();
    t.checkExpect(q.board.get(0).colorzContains(posn1), true);
    t.checkExpect(q.board.get(0).colorzContains(posn2), false);

  }

  // tests get and set flooded methods for cell
  void testGetSetFlooded(Tester t) {
    init();
    t.checkExpect(q.board.get(0).getFlooded(1), true);
    t.checkExpect(q.board.get(0).getFlooded(2), false);
    q.board.get(0).setFlooded(2, true);
    q.board.get(0).setFlooded(1, false);
    t.checkExpect(q.board.get(0).getFlooded(1), false);
    t.checkExpect(q.board.get(0).getFlooded(2), true);



  }

  // tests the actual game
  void testBigBang(Tester t) {
    q.makeBoard();
    int worldWidth = FloodItWorld.BOARD_SIZE * FloodItWorld.CELL_SIZE + 150;
    worldWidth = 500;
    int worldHeight = worldWidth;
    double tickRate = .1;
    q.bigBang(worldWidth, worldHeight, tickRate);

  }

}


 */
