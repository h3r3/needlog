package org.postronic.h3.needlog;

import org.postronic.h3.needlog.ui.MainWindow;

public class IndexingFeedbackRunnable implements Runnable {
	
	private Needlog needlog;
	private MainWindow mainWindow;
	
	public IndexingFeedbackRunnable(Needlog needlog, MainWindow mainWindow) {
		this.needlog = needlog;
		this.mainWindow = mainWindow;
	}

	public void run() {
		float percentIndexing;
		int refreshEveryLoops = 1;
		int loopsSinceRefresh = 0;
		while ((percentIndexing = needlog.getPercentIndexing()) < 100.0f) {
			try {
				mainWindow.setPercentIndexing(percentIndexing);
				Thread.sleep(150);
				loopsSinceRefresh++;
				if (loopsSinceRefresh >= refreshEveryLoops) {
					mainWindow.queryRefresh();
					loopsSinceRefresh = 0;
					refreshEveryLoops++;
					if (refreshEveryLoops > 5000 / 150) {
						refreshEveryLoops = 5000 / 150;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mainWindow.setPercentIndexing(100.0f);
		mainWindow.queryRefresh();
	}

}
