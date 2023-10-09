

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;

public class GUI extends Application {

	public static final int size = 20; 
	public static final int scene_height = size * 20 + 100;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right,hero_left,hero_up,hero_down;

	public static Image fireDown, fireHorizontal, fireLeft, fireRight, fireUp, fireVertical, fireWallEast, fireWallNorth, fireWallSouth, fireWallWest;

	public static Player me;

	public static List<Player> players = new ArrayList<Player>();

	private Label[][] fields;
	private TextArea scoreList;

	private Socket clientSocket;
	private DataOutputStream outToServer;
	private BufferedReader inFromServer;
	private TextField nameField;
	private  String[] board = {    // 20x20
			"wwwwwwwwwwwwwwwwwwww",
			"w        ww        w",
			"w w  w  www w  w  ww",
			"w w  w   ww w  w  ww",
			"w  w               w",
			"w w w w w w w  w  ww",
			"w w     www w  w  ww",
			"w w     www w  w  ww",
			"w   w w  w  w  w   w",
			"w     w  w  w  w   w",
			"w ww ww        w  ww",
			"w  w w    w    w  ww",
			"w        ww w  w  ww",
			"w         w w  w  ww",
			"w        w     w  ww",
			"w  w              ww",
			"w  w www  w w  ww ww",
			"w w      ww w     ww",
			"w   w   ww  w      w",
			"wwwwwwwwwwwwwwwwwwww"
	};

	
	// -------------------------------------------
	// | Maze: (0,0)              | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1)          | scorelist    |
	// |                          | (1,1)        |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		try {
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(0, 10, 0, 10));

			Text mazeLabel = new Text("Maze:");
			mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	
			Text scoreLabel = new Text("Score:");
			scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

			scoreList = new TextArea();
			
			GridPane boardGrid = new GridPane();

			image_wall  = new Image(getClass().getResourceAsStream("Image/wall4.png"),size,size,false,false);
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"),size,size,false,false);

			hero_right  = new Image(getClass().getResourceAsStream("Image/heroRight.png"),size,size,false,false);
			hero_left   = new Image(getClass().getResourceAsStream("Image/heroLeft.png"),size,size,false,false);
			hero_up     = new Image(getClass().getResourceAsStream("Image/heroUp.png"),size,size,false,false);
			hero_down   = new Image(getClass().getResourceAsStream("Image/heroDown.png"),size,size,false,false);

			fireDown = new Image(getClass().getResourceAsStream("Image/fireDown.png"), size, size, false, false);
			fireHorizontal = new Image(getClass().getResourceAsStream("Image/fireHorizontal.png"), size, size, false, false);
			fireLeft = new Image(getClass().getResourceAsStream("Image/fireLeft.png"), size, size, false, false);
			fireRight = new Image(getClass().getResourceAsStream("Image/fireRight.png"), size, size, false, false);
			fireUp = new Image(getClass().getResourceAsStream("Image/fireUp.png"), size, size, false, false);
			fireVertical = new Image(getClass().getResourceAsStream("Image/fireVertical.png"), size, size, false, false);
			fireWallEast = new Image(getClass().getResourceAsStream("Image/fireWallEast.png"), size, size, false, false);
			fireWallNorth = new Image(getClass().getResourceAsStream("Image/fireWallNorth.png"), size, size, false, false);
			fireWallSouth = new Image(getClass().getResourceAsStream("Image/fireWallSouth.png"), size, size, false, false);
			fireWallWest = new Image(getClass().getResourceAsStream("Image/fireWallWest.png"), size, size, false, false);


			fields = new Label[20][20];
			for (int j=0; j<20; j++) {
				for (int i=0; i<20; i++) {
					switch (board[j].charAt(i)) {
					case 'w':
						fields[i][j] = new Label("", new ImageView(image_wall));
						break;
					case ' ':					
						fields[i][j] = new Label("", new ImageView(image_floor));
						break;
					default: throw new Exception("Illegal field value: "+board[j].charAt(i) );
					}
					boardGrid.add(fields[i][j], i, j);
				}
			}
			scoreList.setEditable(false);
			
			
			grid.add(mazeLabel,  0, 0); 
			grid.add(scoreLabel, 1, 0); 
			grid.add(boardGrid,  0, 1);
			grid.add(scoreList,  1, 1);
						
			Scene scene = new Scene(grid,scene_width,scene_height);
			primaryStage.setScene(scene);
			primaryStage.show();

			clientSocket = new Socket("localhost",6789);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				switch (event.getCode()) {
				case UP: sendBesked("UP"); break;
				case DOWN:  sendBesked("DOWN");  break;
				case LEFT:  sendBesked("LEFT");  break;
				case RIGHT: sendBesked("RIGHT"); break;
				case SPACE:
					sendSkud();
					break;
				default: break;
				}
			});

			class Listener extends Thread {
				private Socket socket;

				public Listener(Socket socket) {
					this.socket = socket;
				}
				@Override
				public void run() {
					try {
						BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						while (true) {
							String[] tekst = input.readLine().split(" ");
							System.out.println(Arrays.toString(tekst));
							if (tekst.length > 2) {
								addSpiller(tekst[0], tekst[1], tekst[2]);
							} else if (tekst.length > 1){
								moveFromServer(tekst[0], tekst[1]);
							} else {
								shootFromServer(tekst[0]);
							}
						}
					} catch (IOException | InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}

			Listener listener = new Listener(clientSocket);
			listener.start();
			
            // Setting up standard players
			indtastNavn();
			/**
			me = new Player("Orville",9,4,"up");
			players.add(me);
			fields[9][4].setGraphic(new ImageView(hero_up));


			Player harry = new Player("Harry",14,15,"up");
			players.add(harry);
			fields[14][15].setGraphic(new ImageView(hero_up));

			Player Silas = new Player("Silas",3,14, "up");
			players.add(Silas);
			fields[3][14].setGraphic(new ImageView(hero_up));
			*/

			scoreList.setText(getScoreList());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void shoot(Player player) throws InterruptedException {
		int x = player.getXpos(),y = player.getYpos();
		String direction = player.direction;

		if (direction.equals("up")) {
			char boardCord = board[y-1].charAt(x);
			int i = 1;
			while (boardCord != 'w') {
				char nextBoardCord = board[y-i-1].charAt(x);
				Player p = getPlayerAt(x,y-i);

				if (p != null) {
					die(p);
				}

				if (i == 1) {
					fields[x][y-i].setGraphic(new ImageView(fireUp));
				} else if (nextBoardCord != 'w') {
					fields[x][y-i].setGraphic(new ImageView(fireVertical));
				} else {
					fields[x][y-i].setGraphic(new ImageView(fireWallNorth));
					ExecutorService executor = Executors.newSingleThreadExecutor();
					executor.submit(() -> {
						try {
							Thread.sleep(1000); // Sleep for 1 second (1000 milliseconds)
						} catch (InterruptedException e) {
							e.printStackTrace(); // Handle the exception as needed
						}

						// Call a method to remove the floor image
						remove(x, y-1, direction);
					});
				}

				i++;
				boardCord = board[y-i].charAt(x);
			}
		} else if (direction.equals("down")) {
			char boardCord = board[y+1].charAt(x);
			int i = 1;
			while (boardCord != 'w') {
				char nextBoardCord = board[y + i + 1].charAt(x);
				Player p = getPlayerAt(x, y + i);

				if (p != null) {
					die(p);
				}

				if (i == 1) {
					fields[x][y + i].setGraphic(new ImageView(fireDown));
				} else if (nextBoardCord != 'w') {
					fields[x][y + i].setGraphic(new ImageView(fireVertical));
				} else {
					fields[x][y + i].setGraphic(new ImageView(fireWallSouth));
					ExecutorService executor = Executors.newSingleThreadExecutor();
					executor.submit(() -> {
						try {
							Thread.sleep(1000); // Sleep for 1 second (1000 milliseconds)
						} catch (InterruptedException e) {
							e.printStackTrace(); // Handle the exception as needed
						}

						// Call a method to remove the floor image
						remove(x, y+1, direction);
					});
				}

				i++;
				boardCord = board[y + i].charAt(x);
			}

		} else if (direction.equals("right")) {
			char boardCord = board[y].charAt(x+1);
			int i = 1;
			while (boardCord != 'w') {
				char nextBoardCord = board[y].charAt(x + i + 1);
				Player p = getPlayerAt(x + i, y);

				if (p != null) {
					die(p);
				}

				if (i == 1) {
					fields[x + i][y].setGraphic(new ImageView(fireRight));
				} else if (nextBoardCord != 'w') {
					fields[x + i][y].setGraphic(new ImageView(fireHorizontal));
				} else {
					fields[x + i][y].setGraphic(new ImageView(fireWallEast));
					ExecutorService executor = Executors.newSingleThreadExecutor();
					executor.submit(() -> {
						try {
							Thread.sleep(1000); // Sleep for 1 second (1000 milliseconds)
						} catch (InterruptedException e) {
							e.printStackTrace(); // Handle the exception as needed
						}

						// Call a method to remove the floor image
						remove( x+1, y, direction);
					});
				}

				i++;
				boardCord = board[y].charAt(x + i);
			}
		} else if (direction.equals("left")) {
			char boardCord = board[y].charAt(x-1);
			int i = 1;
			while (boardCord != 'w') {
				char nextBoardCord = board[y].charAt(x - i - 1);
				Player p = getPlayerAt(x - i, y);

				if (p != null) {
					die(p);
				}

				if (i == 1) {
					fields[x - i][y].setGraphic(new ImageView(fireLeft));
				} else if (nextBoardCord != 'w') {
					fields[x - i][y].setGraphic(new ImageView(fireHorizontal));
				} else {
					fields[x - i][y].setGraphic(new ImageView(fireWallWest));
					ExecutorService executor = Executors.newSingleThreadExecutor();
					executor.submit(() -> {
						try {
							Thread.sleep(1000); // Sleep for 1 second (1000 milliseconds)
						} catch (InterruptedException e) {
							e.printStackTrace(); // Handle the exception as needed
						}

						// Call a method to remove the floor image
						remove(x-1, y, direction);
					});
				}
				i++;
				boardCord = board[y].charAt(x - i);
			}
		}
	}

	public void remove(int x, int y, String direction) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				int xPos_r = x;
				int yPos_r = y;
				while (board[yPos_r].charAt(xPos_r) != 'w') {
					fields[xPos_r][yPos_r].setGraphic(new ImageView(image_floor));
					if (direction.equals("up")) {
						yPos_r--;

					} else if (direction.equals("down")) {
						yPos_r++;

					} else if (direction.equals("right")) {
						xPos_r++;

					} else if (direction.equals("left")) {
						xPos_r--;
					}
				}
			}
		});
	}

	public void die(Player player) {
		player.addPoints(-100);
		respawn(player);
	}

	public void respawn(Player player) {
		int x = player.getXpos(),y = player.getYpos();
		fields[x][y].setGraphic(new ImageView(image_floor));
		String[] coords = randomPosition().split(" ");
		player.setXpos(Integer.parseInt(coords[0]));
		player.setYpos(Integer.parseInt(coords[1]));
		player.setDirection("up");
		fields[Integer.parseInt(coords[0])][Integer.parseInt(coords[1])].setGraphic(new ImageView(hero_up));
	}

	public void playerMoved(int delta_x, int delta_y, String direction, Player player) {
		player.direction = direction;
		int x = player.getXpos(),y = player.getYpos();

		if (board[y+delta_y].charAt(x+delta_x)=='w') {
			player.addPoints(-1);
		} 
		else {
			Player p = getPlayerAt(x+delta_x,y+delta_y);
			if (p!=null) {
              player.addPoints(10);
              p.addPoints(-10);
			} else {
				player.addPoints(1);
			
				fields[x][y].setGraphic(new ImageView(image_floor));
				x+=delta_x;
				y+=delta_y;

				if (direction.equals("right")) {
					fields[x][y].setGraphic(new ImageView(hero_right));
				};
				if (direction.equals("left")) {
					fields[x][y].setGraphic(new ImageView(hero_left));
				};
				if (direction.equals("up")) {
					fields[x][y].setGraphic(new ImageView(hero_up));
				};
				if (direction.equals("down")) {
					fields[x][y].setGraphic(new ImageView(hero_down));
				};

				player.setXpos(x);
				player.setYpos(y);
			}
		}
		scoreList.setText(getScoreList());
	}

	public void indtastNavn(){
		Stage newStage = new Stage();
		VBox comp = new VBox();
		Label label = new Label("Vent med at trykke ok til alle spillere er connected");
		nameField = new TextField("Indtast navn");
		Button button = new Button("Ok");
		comp.getChildren().add(label);
		comp.getChildren().add(nameField);
		comp.getChildren().add(button);
		button.setOnAction(event -> {
			try {
				okAction(nameField.getText(), newStage);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		Scene stageScene = new Scene(comp, 200, 75);
		newStage.setScene(stageScene);
		newStage.show();
	}

	private void okAction(String name, Stage stage) throws IOException {
		String pos = randomPosition();
		outToServer.writeBytes(name + " " + pos + "\n");
		stage.hide();
	}

	private void addSpiller(String name, String x, String y) {
		if	(nameField.getText().equals(name)) {
			me = new Player(name, Integer.parseInt(x), Integer.parseInt(y), "up");
			players.add(me);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					fields[Integer.parseInt(x)][Integer.parseInt(y)].setGraphic(new ImageView(hero_up));
				}
			});
		} else {
			players.add(new Player(name,Integer.parseInt(x),Integer.parseInt(y), "up"));
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					fields[Integer.parseInt(x)][Integer.parseInt(y)].setGraphic(new ImageView(hero_up));
				}
			});
		}
	}

	public String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			b.append(p+"\r\n");
		}
		return b.toString();
	}

	public Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos()==x && p.getYpos()==y) {
				return p;
			}
		}
		return null;
	}

	public void sendBesked(String dir) {
		try {
			outToServer.writeBytes(dir + " " + me.name + '\n');
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendSkud() {
		try {
			outToServer.writeBytes(me.name + '\n');
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String randomPosition() {
		int x = (int) (Math.random()*18) + 1;
		int y = (int) (Math.random()*18) + 1;
		if (board[y].charAt(x) == 'w') {
			return randomPosition();
		} else {
			return x + " " + y;
		}
	}

	public void moveFromServer(String dir, String navn) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Player p = me;
				for (Player player : players) {
					if (player.name.equals(navn)) {
						p = player;
					}
				}
				switch (dir) {
					case "UP": playerMoved(0,-1, dir.toLowerCase(), p); break;
					case "DOWN": playerMoved(0,+1,dir.toLowerCase(),p); break;
					case "LEFT": playerMoved(-1,0,dir.toLowerCase(),p); break;
					case "RIGHT": playerMoved(+1,0,dir.toLowerCase(),p); break;
					default: break;
				}
			}
		});
	}

	public void shootFromServer(String navn) throws InterruptedException {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Player p = me;
				for (Player player : players) {
					if (player.name.equals(navn)) {
						p = player;
					}
				}
				try {
					shoot(p);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

}