package main;

@SuppressWarnings("serial")
public class GameThread extends Game implements Runnable {
	
	
	public Thread thread; //maak een extra thread zodat we een loop continu kunnen laten runnen zonder dat de mane frame vastloopt
	
	private boolean running;
	private long FPS = 144;
	private long frameTime = 1000/FPS;
	private long lastcheck;
	public static double elapsed;
	
	
	public GameThread() {
		if (thread == null) {
			thread = new Thread(this);//maak een nieuwe thread (als deze nog niet bestaat) en attach deze aan deze class.
			running = true;
			thread.start();
		}
	}


	public void run() {
		long start;
		long wait;
		
		while (running) {
			
			start = System.nanoTime();
			
			Game.draw();
			Game.drawToScreen();
			Game.update();
			
			elapsed = System.nanoTime() - start;
			if (lastcheck - System.currentTimeMillis() < 0 && Game.showFramerate) {
				System.out.println("Frametime: " + (double) Math.round(elapsed / 10000)/100 + "ms");
				if (FPS > Math.round(1000/(elapsed / 1000000))) {
					System.out.println("FPS: " + Math.round(1000/(elapsed / 1000000)));
				} else {
					System.out.println("FPS: " + FPS);
				}
				lastcheck = System.currentTimeMillis() + 200;
			}
			
			wait = frameTime - (long) elapsed / 1000000;
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
