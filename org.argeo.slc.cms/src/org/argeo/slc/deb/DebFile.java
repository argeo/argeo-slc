package org.argeo.slc.deb;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class DebFile {
	private URL url;

	public DebFile(URL url) {
		this.url = url;
	}

	public void printDataArchive() {
		openDataArchive((tarIn) -> {
			try {
				ArchiveEntry entry = null;
				while ((entry = tarIn.getNextEntry()) != null) {
					System.out.println(entry.getName());
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}

	public void printControlArchive() {
		openControlArchive((tarIn) -> {
			try {
				ArchiveEntry entry = null;
				while ((entry = tarIn.getNextEntry()) != null) {
					System.out.println(entry.getName());
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}

	public void printControlFile() {
		openControlArchive((tarIn) -> {
			try {
				ArchiveEntry entry = null;
				while ((entry = tarIn.getNextEntry()) != null) {
					if ("./control".equals(entry.getName())) {
						String controlFileStr = new String(tarIn.readAllBytes(), StandardCharsets.UTF_8);
						System.out.println(controlFileStr);
					}
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		});
	}

	protected void openDataArchive(Consumer<ArchiveInputStream> process) {
		try (ArArchiveInputStream arIn = new ArArchiveInputStream(new BufferedInputStream(url.openStream()))) {
			// order is specified, so we don't verify entry names
			ArArchiveEntry debianBinaryEntry = arIn.getNextArEntry();
			assert "debian-binary".equals(debianBinaryEntry.getName());
			checkVersion(arIn);
			arIn.getNextArEntry();// control archive
			ArArchiveEntry dataArchiveEntry = arIn.getNextArEntry();
			processArArchiveEntry(dataArchiveEntry, arIn, process);
		} catch (IOException | CompressorException e) {
			throw new RuntimeException("Cannot open data archive of " + url, e);
		}
	}

	protected void openControlArchive(Consumer<ArchiveInputStream> process) {
		try (ArArchiveInputStream arIn = new ArArchiveInputStream(new BufferedInputStream(url.openStream()))) {
			// order is specified, so we don't verify entry names
			ArArchiveEntry debianBinaryEntry = arIn.getNextArEntry();
			assert "debian-binary".equals(debianBinaryEntry.getName());
			checkVersion(arIn);
			ArArchiveEntry controlArchiveEntry = arIn.getNextArEntry();
			processArArchiveEntry(controlArchiveEntry, arIn, process);
		} catch (IOException | CompressorException e) {
			throw new RuntimeException("Cannot open control archive of " + url, e);
		}
	}

	protected void processArArchiveEntry(ArchiveEntry archiveEntry, ArArchiveInputStream arIn,
			Consumer<ArchiveInputStream> process) throws IOException, CompressorException {
		String dataArchiveName = archiveEntry.getName();
		String compressionType = null;
		if (dataArchiveName.endsWith(".xz"))
			compressionType = CompressorStreamFactory.XZ;
		else if (dataArchiveName.endsWith(".gz"))
			compressionType = CompressorStreamFactory.GZIP;
		else if (dataArchiveName.endsWith(".bzip2"))
			compressionType = CompressorStreamFactory.BZIP2;
		else if (dataArchiveName.endsWith(".lzma"))
			compressionType = CompressorStreamFactory.LZMA;

		if (compressionType == null) {
			try (TarArchiveInputStream tarIn = new TarArchiveInputStream(arIn)) {
				process.accept(tarIn);
			}
		} else {
			try (TarArchiveInputStream tarIn = new TarArchiveInputStream(
					new CompressorStreamFactory().createCompressorInputStream(compressionType, arIn))) {
				process.accept(tarIn);
			}
		}
	}

	protected void checkVersion(ArArchiveInputStream arIn) throws IOException {
		String version = new String(arIn.readAllBytes(), StandardCharsets.US_ASCII).trim();
		if (!"2.0".equals(version))
			throw new IllegalStateException("Deb " + version + " is not supported");
	}

	public static void main(String[] args) throws Exception {
//		URL url = Paths
//				.get(System.getProperty("user.home")+"/qa/git/argeo-qa-unstable/build/repo/deb/platform/2.3/argeo-cms_2.3.18_all.deb")
//				.toUri().toURL();
		URL url = new URL("http://repo.netiket.eu/deb/pool/bullseye/platform/2.3/argeo-cms_2.3.18_all.deb");
		DebFile debFile = new DebFile(url);
		debFile.printControlArchive();
		debFile.printControlFile();
		debFile.printDataArchive();
	}

}
