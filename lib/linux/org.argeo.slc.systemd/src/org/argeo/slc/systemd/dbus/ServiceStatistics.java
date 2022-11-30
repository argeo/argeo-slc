package org.argeo.slc.systemd.dbus;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;
import static java.lang.System.Logger.Level.WARNING;

import java.io.IOException;
import java.io.Writer;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.math.BigInteger;
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

	private long begin;

	private Statistics previousStat = null;

	private StatisticsThread statisticsThread;

	private Path basePath;

	private BigInteger maxMemory = BigInteger.ZERO;
	private BigInteger maxTasks = BigInteger.ZERO;

	public void start() {
		begin = Instant.now().toEpochMilli();
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
			// standard systemd property
			String logsDirectory = System.getenv("LOGS_DIRECTORY");
			if (logsDirectory == null) {
				logsDirectory = System.getProperty("user.dir");
			}
			// MainPID,CPUUsageNSec,MemoryCurrent,IPIngressBytes,IPEgressBytes,IOReadBytes,IOWriteBytes,TasksCurrent
			basePath = Paths.get(logsDirectory);

			logger.log(DEBUG, () -> "Writing statistics for " + unitName + " to " + basePath);
			// start collecting
			statisticsThread = new StatisticsThread();
			statisticsThread.start();
		}
	}

	public void stop() {
		if (manager != null) {
			// write accounting
			Path accountingPath = basePath.resolve("accounting-" + unitName + ".csv");
			logger.log(INFO, () -> "Writing accounting for " + unitName + " to " + accountingPath);
			boolean writeHeader = !Files.exists(accountingPath);
			try (Writer writer = Files.newBufferedWriter(accountingPath, StandardCharsets.UTF_8,
					StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
				CsvWriter csvWriter = new CsvWriter(writer);

				if (writeHeader)// header
					csvWriter.writeLine("BeginTimeMillis", "EndTimeMillis", "CPUUsageNSec", "MaxMemory",
							"IPIngressBytes", "IPEgressBytes", "IOReadBytes", "IOWriteBytes", "MaxTasks");

				// TODO better synchronise with stop
				csvWriter.writeLine(begin, System.currentTimeMillis(), service.getCPUUsageNSec(), maxMemory,
						service.getIPIngressBytes(), service.getIPEgressBytes(), service.getIOReadBytes(),
						service.getIOWriteBytes(), maxTasks);
				writer.flush();
			} catch (IOException e) {
				logger.log(ERROR, "Cannot write accounting to " + accountingPath, e);
			}

			// disconnect
			synchronized (this) {
				Systemd.disconnect();
				manager = null;
				notifyAll();
				statisticsThread.interrupt();
			}
		}
	}

	protected void collectStatistics() {
		try {
			while (manager != null) {
				synchronized (this) {

					String dateSuffix = Instant.ofEpochMilli(begin).atOffset(ZoneOffset.UTC)
							.format(DateTimeFormatter.ISO_LOCAL_DATE) + "-" + begin;

					Path statisticsPath = basePath.resolve("statistics-" + unitName + "-" + dateSuffix + ".csv");
					boolean writeHeader = !Files.exists(statisticsPath);
					if (!writeHeader && previousStat == null)
						logger.log(ERROR,
								"File " + statisticsPath + " exists, but we don't have previous statistics in memory");

					try (Writer writer = Files.newBufferedWriter(statisticsPath, StandardCharsets.UTF_8,
							StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
						CsvWriter csvWriter = new CsvWriter(writer);

						if (writeHeader)// header
							csvWriter.writeLine("CurrentTimeMillis", "CPUUsageNSec", "MemoryCurrent", "IPIngressBytes",
									"IPEgressBytes", "IOReadBytes", "IOWriteBytes", "TasksCurrent");

						Statistics s = new Statistics(Instant.now().toEpochMilli(), service.getCPUUsageNSec(),
								service.getMemoryCurrent(), service.getIPIngressBytes(), service.getIPEgressBytes(),
								service.getIOReadBytes(), service.getIOWriteBytes(), service.getTasksCurrent());

						if (s.MemoryCurrent().compareTo(maxMemory) > 0)
							maxMemory = s.MemoryCurrent();
						if (s.TasksCurrent().compareTo(maxTasks) > 0)
							maxTasks = s.TasksCurrent();

						Statistics diff = Statistics.diff(s, previousStat);
						// TODO better synchronise with stop
						csvWriter.writeLine(diff.CurrentTimeMillis(), diff.CPUUsageNSec(), diff.MemoryCurrent(),
								diff.IPIngressBytes(), diff.IPEgressBytes(), diff.IOReadBytes(), diff.IOWriteBytes(),
								diff.TasksCurrent());
						previousStat = s;
					}
					try {
						this.wait(frequency);
					} catch (InterruptedException e) {
						logger.log(Level.TRACE, () -> "Statistics collection interrupted for " + unitName);
					}
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("Cannot collect statistics", e);
		}
	}

	class StatisticsThread extends Thread {

		public StatisticsThread() {
			super("Statistics for " + unitName);
		}

		@Override
		public void run() {
			collectStatistics();
		}

	}

	private record Statistics(long CurrentTimeMillis, BigInteger CPUUsageNSec, BigInteger MemoryCurrent,
			BigInteger IPIngressBytes, BigInteger IPEgressBytes, BigInteger IOReadBytes, BigInteger IOWriteBytes,
			BigInteger TasksCurrent) {

		final static Statistics NULL = new Statistics(0, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO,
				BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO);

		public static Statistics diff(Statistics now, Statistics previous) {
			if (previous == null)
				previous = NULL;
			return new Statistics(now.CurrentTimeMillis(), now.CPUUsageNSec().subtract(previous.CPUUsageNSec()),
					now.MemoryCurrent(), now.IPIngressBytes().subtract(previous.IPIngressBytes()),
					now.IPEgressBytes().subtract(previous.IPEgressBytes()),
					now.IOReadBytes().subtract(previous.IOReadBytes()),
					now.IOWriteBytes().subtract(previous.IOWriteBytes()), now.TasksCurrent());
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
