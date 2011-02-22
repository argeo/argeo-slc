package org.argeo.slc.jts;

import java.util.List;

import org.argeo.slc.geotools.map.OverlayLocationReceiver;
import org.argeo.slc.gis.model.FieldPosition;

public class PositionUpdater implements Runnable {
	private PositionProvider positionProvider;
	private List<OverlayLocationReceiver> positionReceivers;
	/** in s */
	private Integer positionRefreshPeriod = 1;

	private Thread thread;
	private Boolean running = false;

	private FieldPosition currentPosition = null;

	public void run() {
		while (running) {
			FieldPosition position = positionProvider.currentPosition();

			for (OverlayLocationReceiver receiver : positionReceivers) {
				if (position != null) {
					currentPosition = position;
					receiver.receiveOverlayLocation(
							currentPosition.getLocation(), false);
				} else {
					receiver.receiveOverlayLocation(
							currentPosition.getLocation(), true);
				}
			}
			
			try {
				Thread.sleep(positionRefreshPeriod * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		running = true;
		thread = new Thread(this, "Position Updater");
		thread.start();
	}

	public void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setPositionProvider(PositionProvider positionProvider) {
		this.positionProvider = positionProvider;
	}

	public void setPositionRefreshPeriod(Integer positionRefreshPeriod) {
		this.positionRefreshPeriod = positionRefreshPeriod;
	}

}