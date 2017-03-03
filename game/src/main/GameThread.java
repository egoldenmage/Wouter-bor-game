package main;

@SuppressWarnings("serial")
public class GameThread extends Game implements Runnable {
	
	
	public Thread thread; //maak een extra thread zodat we een loop continu kunnen laten runnen zonder dat de mane frame vastloopt
	
	private boolean running;
	private long FPS = 144;
	private long frameTime = 1000/FPS;
	
	
	public GameThread() {
		if (thread == null) {
			thread = new Thread(this);//maak een nieuwe thread (als deze nog niet bestaat) en attach deze aan deze class.
			running = true;
			thread.start();
		}
	}


	public void run() {
		long start;
		long elapsed;
		long wait;
		
		while (running) {
			
			start = System.nanoTime();
			
			Game.draw();
			Game.drawToScreen();
			Game.update();
			
			elapsed = System.nanoTime() - start;
			wait = frameTime - elapsed / 1000000;
			if (wait >= 0) {
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			}

			
		}
		
	}


	
}
