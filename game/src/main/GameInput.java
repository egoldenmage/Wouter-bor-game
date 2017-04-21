package main;

import java.awt.event.*;
import java.util.ArrayList;

import main.gamestates.GameStateManager;

@SuppressWarnings("serial")
public class GameInput extends Game implements KeyListener {
	public static ArrayList<Integer> keysDown = new ArrayList<>();

	
	public GameInput() {
		Game.gamePane.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) {
				GameStateManager.mouseClicked(e.getX(),e.getY(), e.getButton());
	        } 
			public void mouseReleased(MouseEvent e) {
				GameStateManager.mouseReleased(e.getX(),e.getY(), e.getButton());
			}
	    }); 
		Game.gamePane.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				GameStateManager.mouseScrolled(e.getPreciseWheelRotation());
			}
		});
		Game.gamePane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			public void mouseMoved(java.awt.event.MouseEvent evt) {
				GameStateManager.mouseMove(evt.getX(), evt.getY());
			}
			public void mouseDragged(java.awt.event.MouseEvent evt) {
				GameStateManager.mouseMove(evt.getX(), evt.getY());
			}
		});
	}

    public void keyPressed(KeyEvent e) {
    	//System.out.println(e.getKeyCode());
        GameStateManager.input(e.getKeyCode(), true);
    }
    public void keyReleased(KeyEvent e) {
    	GameStateManager.input(e.getKeyCode(), false);
    }

	public void keyTyped(KeyEvent e) {
	}

}
