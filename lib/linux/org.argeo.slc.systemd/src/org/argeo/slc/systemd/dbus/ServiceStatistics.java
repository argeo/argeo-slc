package org.argeo.slc.systemd.dbus;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.WARNING;

import java.io.IOException;
import java.io.Writer;
import java.lang.System.Logger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.argeo.cms.util.CsvWriter;
import org.freedesktop.dbus.exceptions.DBusException;

import de.thjom.java.systemd.Manager;
import de.thjom.java.systemd.Service;
import de.thjom.java.systemd.Systemd;
import de.thjom.java.systemd.types.UnitType;

/** Gathers statistics about the runnign process, if it is a systemd unit. */
public class ServiceStatistics {
	private final static Logger logger = System.getLogger(ServiceStatistics.class.getName());

	private Manager manager;

	private String unitName;
	private Service service;

	private long frequency = 60 * 1000;

	public void start() {
		final long pid = ProcessHandle.current().pid();
		try {
			manager = Systemd.get().getManager();
			// find own service
			for (UnitType unitType : manager.listUnits()) {
				if (unitType.isService()) {
					Service s = manager.getService(unitType.getUnitName());
					if (s.getMainPID() == pid) {
						service = s;
						unitName = unitType.getUnitName();
						logger.log(INFO, "Systemd service found for pid " + pid + ": " + unitName);
					}
				}
			}

		} catch (DBusException e) {
			logger.log(WARNING, "Cannot connect to systemd", e);
		}

		if (service == null) {
			logger.log(DEBUG, () -> "No systemd service found for pid " + pid + ", disconnecting from DBus...");
			manager = null;
			Systemd.disconnect();
		} else {
			// start collecting
			collectStatistics();
		}
	}

	public void stop() {
		if (manager != null) {
			Systemd.disconnect();
			manager = null;
			notifyAll();
		}
	}

	protected void collectStatistics() {
		// standard systemd property
		String logsDirectory = System.getenv("LOGS_DIRECTORY");
		if (logsDirectory == null) {
			logsDirectory = System.getProperty("user.dir");
		}
		// MainPID,CPUUsageNSec,MemoryCurrent,IPIngressBytes,IPEgressBytes,IOReadBytes,IOWriteBytes,TasksCurrent
		Path basePath = Paths.get(logsDirectory);

		logger.log(DEBUG, () -> "Writing statistics for " + unitName + " to " + basePath);
		try {
			while (manager != null) {
				String dateSuffix = Instant.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE);

				Path csvPath = basePath.resolve("statistics-" + unitName + "-" + dateSuffix + ".csv");
				boolean writeHeader = !Files.exists(csvPath);
				try (Writer writer = Files.newBufferedWriter(csvPath, StandardCharsets.UTF_8,
						StandardOpenOption.APPEND)) {
					CsvWriter csvWriter = new CsvWriter(writer);

					if (writeHeader)// header
						csvWriter.writeLine("CurrentTimeMillis", "CPUUsageNSec", "MemoryCurrent", "IPIngressBytes",
								"IPEgressBytes", "IOReadBytes", "IOWriteBytes", "TasksCurrent");

					// TODO better synchronise with stop
					csvWriter.writeLine(System.currentTimeMillis(), service.getCPUUsageNSec(),
							service.getMemoryCurrent(), service.getIPIngressBytes(), service.getIPEgressBytes(),
							service.getIOReadBytes(), service.getIOWriteBytes(), service.getTasksCurrent());
				}
				Thread.sleep(frequency);
			}
		} catch (IOException e) {
			throw new IllegalStateException("Cannot collect statistics", e);
		} catch (InterruptedException e) {
			logger.log(WARNING, "Statistics collection interrupted for " + unitName);
		}

	}

	public static void main(String[] args) throws Exception {
		try {
			Systemd systemd = Systemd.get();
			Service service = systemd.getManager().getService("ipsec.service");
			System.out.println(service.getCPUUsageNSec());

			for (UnitType unitType : systemd.getManager().listUnits()) {
				if (unitType.isService()) {
					System.out.println(unitType.getUnitName());
				}
			}

		} finally {
			Systemd.disconnect();
		}
	}

}
