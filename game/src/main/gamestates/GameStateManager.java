package main.gamestates;

import java.util.ArrayList;

public class GameStateManager {
	private static ArrayList<GameState> gameStates = new ArrayList<GameState>();
	public static ArrayList<Integer> keysDown = new ArrayList<>();
	
	public static final int MENUSTATE = 0;
	public static final int CLIENTMENUSTATE = 1;
	public static final int CLIENTSTATE = 2;
	public static final int SERVERSTATE = 3;
	
	private static int currentState;
	
	public GameStateManager() {
		gameStates.add(new MenuState(this));
		gameStates.add(new ClientMenuState(this));
		gameStates.add(new ServerMenuState(this));
		gameStates.add(new ClientState(this));
		setState(MENUSTATE);
	}
	
	public void setState(int state) {
		currentState = state;
		gameStates.get(currentState).init();
	}
	
	public void update() {
		gameStates.get(currentState).update();
	}
	
	public void draw(java.awt.Graphics2D g) {
		gameStates.get(currentState).draw(g);
	}
	
	
	public static void input(int keyCode, boolean pressed) {
		if (pressed) {
			gameStates.get(currentState).keyPressed(keyCode);
		} else {
			gameStates.get(currentState).keyReleased(keyCode);
		}
		if (pressed && !keysDown.contains(keyCode)) {
			keysDown.add(keyCode);
		} else if (!pressed && keysDown.contains(keyCode)) {
			keysDown.remove(keysDown.indexOf(keyCode));
		}
	}
	
	public static void mouseMove(int x, int y) {
		gameStates.get(currentState).mouseMove(x,y);
	}
	
	public static void mouseClicked(int x, int y) {
		gameStates.get(currentState).mouseClicked(x,y);
	}
}