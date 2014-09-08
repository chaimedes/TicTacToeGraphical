import java.util.Random;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.EOFException;
import java.nio.channels.FileChannel;
import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.plaf.ButtonUI;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Font;

/** Tic Tac Toe -- Java Swing Graphical **/
public class TTTG extends JApplet implements ActionListener {

// Init vars, etc
public JFrame frame;
private JPanel top;
private JButton sq1;
private JButton sq2;
private JButton sq3;
private JButton sq4;
private JButton sq5;
private JButton sq6;
private JButton sq7;
private JButton sq8;
private JButton sq9;
//private JLabel scoreLabel;
private JMenuBar mainMenuBar;
private JLabel topScoreLabel;

public JButton [] map = {sq1,sq2,sq3,sq4,sq5,sq6,sq7,sq8,sq9}; // Holds references to JButtons in "order."
private int [] moveList = {10, 10, 10, 10, 10, 10, 10, 10, 10};
private int moveMarker = 0;
private int [] stakeArray = { -1 }; // To revert back to "off" number
private int [] curPri = { -1 }; // "off" number for setting curPri
private static final char NEUTRAL_CHAR = ' '; // The constant neutral character that exists before X/O
private final String aboutInfo = "Tic Tac Toe in Java -- by Martin Berlove\n\nwww.mberlove.com";
private static final int CPU_PAUSE_TIME = 500;
private final String BASE_TITLE = "Tic Tac Toe";

/** No-arg constructor, thank you very much. **/
public TTTG() {

//writeMoveLists(false); // Write out blanks if no file exists.

// Write a blank score to data (if a score doesn't exist)
File f = new File("scores.bin");
try {
if (!f.exists()) {
f.createNewFile();
writeScore(0,0);
}
}
catch (IOException ie) {
ie.printStackTrace();
}

// Init swing panel, etc
frame = new JFrame();
Font font = new Font("Arial",Font.PLAIN,120);
top = new JPanel(new GridLayout(3,3)); // Main panel

for (int i = 0; i < map.length; i++) { // Buttons!
map[i] = new JButton(Character.toString(NEUTRAL_CHAR));
map[i].setFont(font);
/*
MetalButtonUI ui = new MetalButtonUI();
ui.installUI(map[i]);
map[i].setUI(ui);
*/
map[i].setUI((ButtonUI)MetalButtonUI.createUI(map[i]));
map[i].addActionListener(this); // One actionListener to rule them all
top.add(map[i]);
}
// Keep in case of later changes.
/*
scoreLabel = new JLabel("");
scoreLabel.setFont(new Font("Arial",Font.PLAIN,16));
top.add(scoreLabel);
*/
// Menu
mainMenuBar = new JMenuBar();
JMenu file = new JMenu("File");
file.setMnemonic(KeyEvent.VK_F);
JMenuItem qItem = new JMenuItem("Exit");
qItem.setMnemonic(KeyEvent.VK_C);
qItem.setToolTipText("Quit Tic Tac Toe (and end game)");
qItem.addActionListener(new ActionListener() {
public void actionPerformed(ActionEvent event) {
System.exit(0);
}
});
file.add(qItem);
JMenuItem aItem = new JMenuItem("App Info");
aItem.setMnemonic(KeyEvent.VK_A);
aItem.setToolTipText("Get information about the app and author.");
aItem.addActionListener(new ActionListener() {
public void actionPerformed(ActionEvent event) {
JOptionPane.showMessageDialog(top, aboutInfo, "Application Information", JOptionPane.INFORMATION_MESSAGE);
}
});
file.add(aItem);
mainMenuBar.add(file);
// Score label
topScoreLabel = new JLabel("");
topScoreLabel.setText("Score: " + 0 + " wins, " + 0 + " losses");
topScoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
topScoreLabel.setLocation(topScoreLabel.getX()+100,topScoreLabel.getY());
mainMenuBar.add(topScoreLabel);
setJMenuBar(mainMenuBar);
frame.add(mainMenuBar);
frame.add(top);
// Frame properties
frame.setTitle(BASE_TITLE);
frame.setSize(700,500);
//setLocationRelativeTo(null);
//setDefaultCloseOperation(EXIT_ON_CLOSE);
//setResizable(false);
try {
UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
}
catch (UnsupportedLookAndFeelException e) {
   e.printStackTrace();
}
catch (ClassNotFoundException e) {
   e.printStackTrace();
}
catch (InstantiationException e) {
   e.printStackTrace();
}
catch (IllegalAccessException e) {
   e.printStackTrace();
}
frame.setLocationByPlatform(true);
frame.setVisible(true);
frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
reportScores();
} // End of TTTG constructor

/** Change buttons to show winning message. **/
private void drawResult(int which) {
try {
Thread.sleep(1000);
}
catch (Exception e) {
e.printStackTrace();
}
if (which == 0) {
map[0].setText("Y");
map[1].setText("O");
map[2].setText("U");
map[3].setText("W");
map[4].setText("I");
map[5].setText("N");
map[6].setText("!");
map[7].setText("!");
map[8].setText("!");
}
else if (which == 1 ) {
map[0].setText("C");
map[1].setText("P");
map[2].setText("U");
map[3].setText("W");
map[4].setText("O");
map[5].setText("N");
map[6].setText("!");
map[7].setText("!");
map[8].setText("!");
}
else {
map[0].setText("N");
map[1].setText("O");
map[2].setText(" ");
map[3].setText("W");
map[4].setText("I");
map[5].setText("N");
map[6].setText("!");
map[7].setText("!");
map[8].setText("!");
}
top.paintImmediately(0,0,(int)top.getSize().getWidth(),(int)top.getSize().getHeight());
try {
Thread.sleep(1000);
}
catch (Exception e) {
e.printStackTrace();
}
}


/** Reset for a new game. **/
private void resetGame() {
int rego = JOptionPane.showConfirmDialog(top,"Would you like to start a new game?","Replay!",2);
if (rego == 0) {
moveMarker = 0;
for (int i = 0; i < moveList.length; i++) {
moveList[i] = 10;
}
for (int a = 0; a < map.length; a++) {
replace(a,NEUTRAL_CHAR);
}
curPri = stakeArray;
}
else {
System.exit(0);
}
}

/** Read in the score from the data file into a two-var int array. **/
public int [] readScore() throws FileNotFoundException, IOException {
int [] score = new int[2];
DataInputStream dis = new DataInputStream(new FileInputStream("scores.bin"));
score[0] = dis.readInt(); // wins
dis.readChar();
score[1] = dis.readInt(); // losses
dis.close();
return score;
}

/** Write the score out to the file, tab separated (wins is first.) **/
public void writeScore(int wins, int losses) throws FileNotFoundException, IOException {
DataOutputStream dos = new DataOutputStream(new FileOutputStream("scores.bin"));
dos.writeInt(wins);
dos.writeChar('\t');
dos.writeInt(losses);
dos.close();
}

public void writeMoveLists(boolean playerWin) {
System.out.println("Writing.");
File f3 = new File("movelist.bin");
if (!f3.exists()) {
try {
f3.createNewFile();
}
catch(IOException ie) {
ie.printStackTrace();
}
doWrite(true);
}
else {
try {
ArrayList<int[]> moves = readMoveLists();
boolean equiv = true;
outerLoop:
for (int i = 0; i < moves.size(); i++) { // -1 is so not as to compare score
for (int j = 0; j < moves.get(i).length-2; j++) { // -1 is so not as to compare score
if (moves.get(i)[j] != moveList[j] && moves.get(i)[j] != 10) {
equiv = false;
break;
} // End of if current move is equivalent
} // End of for each int member of list (except score)
System.out.println(equiv);
if (equiv) {
if (playerWin) {
moves.get(i)[9] += 1;
}
else {
moves.get(i)[10] += 1;
}
doWriteChange(moves);
} // End of if new list is equal to an old one
} // End of for each moves list
if (!equiv) {
if (playerWin) {
doWrite(true);
}
else {
doWrite(false);
}
}
// Read in current movelists, look for copies to change ratio of, write out old score
} catch (FileNotFoundException fe) {
fe.printStackTrace();
}
catch (IOException ie) {
ie.printStackTrace();
}
}
}
 
/** Append new data to the end of the move list file. **/
private void doWrite(boolean playerWin) {
try {
DataOutputStream dos = new DataOutputStream(new FileOutputStream("movelist.bin",true));
for (int i = 0; i < moveList.length; i++) { // For each member of the move list
dos.writeInt(moveList[i]); // Write out the number
dos.writeChar(' '); // Leave space to differentiate
}
if (playerWin) {
dos.writeInt(1);
}
else {
dos.writeInt(0); // Write a zero for score
}
dos.writeChar(' ');
if (!playerWin) {
dos.writeInt(1);
}
else {
dos.writeInt(0);
}
dos.writeChar(' ');
dos.writeChar('\t'); // Tab separate each main entry (not technically req'd)
dos.close();
}
catch (FileNotFoundException fe) {
fe.printStackTrace();
}
catch (IOException ie) {
ie.printStackTrace();
}
}


/** Delete move list file, create new file, rewrite with revised data. **/
private void doWriteChange(ArrayList<int[]> moves) {
File f = new File("movelist.bin");;
try {
//clearMoveLists();
DataOutputStream dos = new DataOutputStream(new FileOutputStream("movelist.bin",false));
for (int i = 0; i < moves.size(); i++) {
for (int j = 0; j < moves.get(i).length; j++) {
dos.writeInt(moves.get(i)[j]);
dos.writeChar(' ');
}
dos.writeChar('\t');
}
}
catch (FileNotFoundException fe) {
fe.printStackTrace();
}
catch (IOException ie) {
ie.printStackTrace();
}
}

public ArrayList<int[]> readMoveLists() throws FileNotFoundException, IOException {
System.out.println("Reading");
// Assume, for now, that file will exist and be previously written to.
// Format is nine space-separated #s followed by a 10th # indicating player wins, and each move list tab-separated.
ArrayList<int[]> moves = new ArrayList<int[]>();
DataInputStream dis = new DataInputStream(new FileInputStream("movelist.bin"));
for (int i = 0; ; i++) {
moves.add(i,new int[11]);
for (int j = 0; j < 11; j++) {
try {
moves.get(i)[j] = dis.readInt();
}
catch (EOFException ee) {
dis.close();
return moves;
}
dis.readChar(); // Skip space
}
dis.readChar(); // Skip tab
}
}

/**
public void clearMoveLists() throws IOException { 
FileChannel fc = new FileOutputStream("movelist.bin",false).getChannel();
fc.truncate(0);
fc.close();
}

/** Print movement lists and scores in a human-readable format to console. **/
public void printMoveLists() {
try {
ArrayList<int[]> moves = readMoveLists();
for (int k = 0; k < moves.size(); k++) {
System.out.print("Moves: ");
for (int l = 0; l < moves.get(k).length-1; l++) {
System.out.print(moves.get(k)[l] + " ");
}
System.out.print("\tWins : " + moves.get(k)[9]);
System.out.print("\tLosses : " + moves.get(k)[10]);
System.out.println("");
}
} 
catch (FileNotFoundException fe) {
fe.printStackTrace();
}
catch (IOException ie) {
ie.printStackTrace();
}
}

/** Increment the number of wins, and write it. **/
public void addWin() {
try {
int [] score = readScore();
score[0] += 1;
writeScore(score[0],score[1]);
}
catch (FileNotFoundException fe) {
fe.printStackTrace();
}
catch (IOException ie) {
ie.printStackTrace();
}
}

/** Increment the number of losses, and write it. **/
public void addLoss() {
try {
int [] score = readScore();
score[1] += 1;
writeScore(score[0],score[1]);
}
catch (FileNotFoundException fe) {
fe.printStackTrace();
}
catch (IOException ie) {
ie.printStackTrace();
}
}

/** Set a space to the new character (int spot) **/
private void replace(int which, char curChar) {
map[which].setText(Character.toString(curChar));
}

/** Set a space to the new character (map spot) **/
private void replace(JButton which, char curChar) {
which.setText(Character.toString(curChar));
}

/** Checks current player locations. If there is a trend in the movement, the remaining space in the triplet gets returned, as an int. **/
private int getPlayerDirectionByInt() {
for (int i = 0; i < 3; i+=3) {
if (map[i].getText().charAt(0) == 'X') {
if (map[i+1].getText().charAt(0) == 'X') {
if (map[i+2].getText().charAt(0) == NEUTRAL_CHAR) {
return i+2;
}
}
else if (map[i+2].getText().charAt(0) == 'X') {
if (map[i+1].getText().charAt(0) == NEUTRAL_CHAR) {
return i+1;
}
}
} // End of if this spot has X
else if (map[i+1].getText().charAt(0) == 'X' && map[i+2].getText().charAt(0) == 'X') {
if (map[i].getText().charAt(0) == NEUTRAL_CHAR) {
return i;
}
}
} // End of for
for (int i = 0; i < 3; i++) {
if (map[i].getText().charAt(0) == 'X') {
if (map[i+3].getText().charAt(0) == 'X') {
if (map[i+6].getText().charAt(0) == NEUTRAL_CHAR) {
return i+6;
}
}
else if (map[i+6].getText().charAt(0) == 'X') {
if (map[i+3].getText().charAt(0) == NEUTRAL_CHAR) {
return i+3;
}
}
} // End of if this spot has X
else if (map[i+3].getText().charAt(0) == 'X' && map[i+6].getText().charAt(0) == 'X') {
if (map[i].getText().charAt(0) == NEUTRAL_CHAR) {
return i;
}
}
} // End of for
if (map[0].getText().charAt(0) == 'X' && map[4].getText().charAt(0) == 'X') {
if (map[8].getText().charAt(0) == NEUTRAL_CHAR) {
return 8;
}
}
else if (map[0].getText().charAt(0) == 'X' && map[8].getText().charAt(0) == 'X') {
if (map[4].getText().charAt(0) == NEUTRAL_CHAR) {
return 4;
}
}
else if (map[4].getText().charAt(0) == 'X' && map[8].getText().charAt(0) == 'X') {
if (map[0].getText().charAt(0) == NEUTRAL_CHAR) {
return 0;
}
}
else if (map[2].getText().charAt(0) == 'X' && map[4].getText().charAt(0) == 'X') {
if (map[6].getText().charAt(0) == NEUTRAL_CHAR) {
return 6;
}
}
else if (map[2].getText().charAt(0) == 'X' && map[6].getText().charAt(0) == 'X') {
if (map[4].getText().charAt(0) == NEUTRAL_CHAR) {
return 4;
}
}
else if (map[4].getText().charAt(0) == 'X' && map[6].getText().charAt(0) == 'X') {
if (map[2].getText().charAt(0) == NEUTRAL_CHAR) {
return 2;
}
}
for (int i = 0; i < map.length; i++) {
if (map[i].getText().charAt(0) == NEUTRAL_CHAR) {
return i; 
}
}
return 0; // This should never happen
}

/** Checks current player locations. If there is a trend in the movement, the remaining space in the triplet gets returned. **/
private JButton getPlayerDirection() {
return map[getPlayerDirectionByInt()];
}

/** Call for CPU to pick a spot for its move. **/
public void cpuAction(int start) {
int curSpot = getPlayerDirectionByInt(); // Get spot to move
JButton b = map[curSpot];
if (b.getText().charAt(0) == NEUTRAL_CHAR) { // If space is untaken
replace(b,'O');
moveList[moveMarker++] = curSpot; // Record the move in the current movelist.
top.paintImmediately(0,0,(int)top.getSize().getWidth(),(int)top.getSize().getHeight()); // Redraw
}
// If the chosen spot is taken, run again and find a new one.
// There should not be a danger of infinite recursion since checkFull() is always run before this.
else {
cpuAction(0);
}
}

/** Read scores and display them to the user. **/
private void reportScores() {
try {
int [] scores = readScore();
// Keep the following in case of later changes.
/*
scoreLabel.setText("Score: " + scores[0] + " wins, " + scores[1] + " losses");
topScoreLabel.setText("Score: " + scores[0] + " wins, " + scores[1] + " losses");
*/
frame.setTitle(BASE_TITLE + " | Score: " + scores[0] + " to " + scores[1]);
}
catch (FileNotFoundException fe) {
fe.printStackTrace();
}
catch (IOException ie) {
ie.printStackTrace();
}
}

/** Call cpuAction() standardly. **/
public void cpuAction() {
cpuAction(0);
}

/** Run the computer's actions on computer's turn. **/
private void compGo() {
// First check if player's previous move should end the game.
String win = checkWin();
if (!"".equals(win)) {
if ("You".equals(win)) {
addWin();
reportScores();
writeMoveLists(true);
drawResult(0);
printMoveLists();
resetGame();
}
else {
addLoss();
reportScores();
writeMoveLists(false);
drawResult(1);
printMoveLists();
resetGame();
}
}
if (checkFull() == true) {
addWin();
addLoss();
reportScores();
drawResult(2);
printMoveLists();
resetGame();
}
// If not, pause a moment (for visual effect)
else {
top.paintImmediately(0,0,(int)top.getSize().getWidth(),(int)top.getSize().getHeight());
try {
Thread.sleep(CPU_PAUSE_TIME);
}
catch (Exception e) {
e.printStackTrace();
}
// Make CPU move
cpuAction();
// Check if CPU move should end the game.
win = checkWin();
if (!"".equals(win)) {
if ("You".equals(win)) {
addWin();
reportScores();
writeMoveLists(true);
drawResult(0);
printMoveLists();
resetGame();
}
else {
addLoss();
reportScores();
writeMoveLists(false);
drawResult(1);
printMoveLists();
resetGame();
} // End of if computer wins
} // End of if someone wins (computer turn)
if (checkFull() == true) {
addWin();
addLoss();
reportScores();
drawResult(2);
resetGame();
}
} // End of if not a draw
} // End of compGo()

/** Determine if board is full; return true or false. **/
public boolean checkFull() {
boolean full = true;
for (int a = 0; a < 9; a++) {
if (map[a].getText().charAt(0) == NEUTRAL_CHAR) {
full = false;
}
}
return full;
}

/** Check if either player has won; return corresponding string. **/
public String checkWin() {
String won = "";
char [] go = {'X','O'};
for (int i = 0; i < 2; i++) {
char curCheck = go[i];
if (map[0].getText().charAt(0) == curCheck && map[1].getText().charAt(0) == curCheck && map[2].getText().charAt(0) == curCheck) {
won = (curCheck=='X')?"You":"Computer";
}
else if (map[3].getText().charAt(0) == curCheck && map[4].getText().charAt(0) == curCheck && map[5].getText().charAt(0) == curCheck) {
won = (curCheck=='X')?"You":"Computer";
}
else if (map[6].getText().charAt(0) == curCheck && map[7].getText().charAt(0) == curCheck && map[8].getText().charAt(0) == curCheck) {
won = (curCheck=='X')?"You":"Computer";
}
else if (map[0].getText().charAt(0) == curCheck && map[3].getText().charAt(0) == curCheck && map[6].getText().charAt(0) == curCheck) {
won = (curCheck=='X')?"You":"Computer";
}
else if (map[1].getText().charAt(0) == curCheck && map[4].getText().charAt(0) == curCheck && map[7].getText().charAt(0) == curCheck) {
won = (curCheck=='X')?"You":"Computer";
}
else if (map[2].getText().charAt(0) == curCheck && map[5].getText().charAt(0) == curCheck && map[8].getText().charAt(0) == curCheck) {
won = (curCheck=='X')?"You":"Computer";
}
else if (map[0].getText().charAt(0) == curCheck && map[4].getText().charAt(0) == curCheck && map[8].getText().charAt(0) == curCheck) {
won = (curCheck=='X')?"You":"Computer";
}
else if (map[2].getText().charAt(0) == curCheck && map[4].getText().charAt(0) == curCheck && map[6].getText().charAt(0) == curCheck) {
won = (curCheck=='X')?"You":"Computer";
}
}
return won;
}

public void init() {
  try {
    SwingUtilities.invokeAndWait(new Runnable() {
      public void run() {
        TTTG tg = new TTTG();
        tg.setVisible(true);
      }
    });
  }
  catch (Exception e) {
    System.err.println("Could not complete operation.");
  }
}

/** React to user action. **/
public void actionPerformed(ActionEvent e) {
JButton temp = (JButton)e.getSource();
temp.setText(Character.toString('X'));
compGo();
}

public static void main(String [] args) {
SwingUtilities.invokeLater(new Runnable() {
public void run() {
TTTG tg = new TTTG();
}
});
}

}