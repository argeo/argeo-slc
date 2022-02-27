package org.argeo.slc.rpmfactory.core;

import static org.redline_rpm.header.Header.HeaderTag.HEADERIMMUTABLE;
import static org.redline_rpm.header.Signature.SignatureTag.SIGNATURES;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.io.FilenameUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.repo.NodeIndexer;
import org.redline_rpm.ChannelWrapper.Key;
import org.redline_rpm.ReadableChannelWrapper;
import org.redline_rpm.header.AbstractHeader;
import org.redline_rpm.header.Format;
import org.redline_rpm.header.Header;

/** Indexes an RPM file. */
public class RpmIndexer implements NodeIndexer, SlcNames {
	private Boolean force = false;

	@Override
	public Boolean support(String path) {
		return FilenameUtils.getExtension(path).equals("rpm");
	}

	@Override
	public void index(Node node) {
		try {
			if (!support(node.getPath()))
				return;

			// Already indexed
			if (!force && node.isNodeType(SlcTypes.SLC_RPM))
				return;

			if (!node.isNodeType(NodeType.NT_FILE))
				return;

			InputStream in = node.getNode(Node.JCR_CONTENT)
					.getProperty(Property.JCR_DATA).getBinary().getStream();
			ReadableChannelWrapper channel = new ReadableChannelWrapper(
					Channels.newChannel(in));
			Format format = readRpmInfo(channel);

			node.addMixin(SlcTypes.SLC_RPM);
			node.setProperty(SLC_NAME, readTag(format, Header.HeaderTag.NAME));
			String rpmVersion = readTag(format, Header.HeaderTag.VERSION);
			String rpmRelease = readTag(format, Header.HeaderTag.RELEASE);
			node.setProperty(SLC_RPM_VERSION, rpmVersion);
			node.setProperty(SLC_RPM_RELEASE, rpmRelease);
			node.setProperty(SLC_VERSION, rpmVersion + "-" + rpmRelease);

			String arch = readTag(format, Header.HeaderTag.ARCH);
			if (arch != null)
				node.setProperty(SLC_RPM_ARCH, arch);

			String archiveSize = readTag(format, Header.HeaderTag.ARCHIVESIZE);
			if (archiveSize != null)
				node.setProperty(SLC_RPM_ARCHIVE_SIZE,
						Long.parseLong(archiveSize));

			node.getSession().save();
		} catch (Exception e) {
			throw new SlcException("Cannot index " + node, e);
		}

	}

	@SuppressWarnings("unused")
	public Format readRpmInfo(ReadableChannelWrapper channel) throws Exception {
		Format format = new Format();

		Key<Integer> lead = channel.start();
		format.getLead().read(channel);
		// System.out.println( "Lead ended at '" + in.finish( lead) + "'.");

		Key<Integer> signature = channel.start();
		int count = format.getSignature().read(channel);
		int expected = ByteBuffer
				.wrap((byte[]) format.getSignature().getEntry(SIGNATURES)
						.getValues(), 8, 4).getInt()
				/ -16;
		// System.out.println( "Signature ended at '" + in.finish( signature) +
		// "' and contained '" + count + "' headers (expected '" + expected +
		// "').");

		Key<Integer> header = channel.start();
		count = format.getHeader().read(channel);
		expected = ByteBuffer.wrap(
				(byte[]) format.getHeader().getEntry(HEADERIMMUTABLE)
						.getValues(), 8, 4).getInt()
				/ -16;
		// System.out.println( "Header ended at '" + in.finish( header) +
		// " and contained '" + count + "' headers (expected '" + expected +
		// "').");

		return format;
	}

	private String readTag(Format format, Header.HeaderTag tag) {
		AbstractHeader.Entry<?> entry = format.getHeader().getEntry(tag);
		if (entry == null)
			return null;
		if (entry.getValues() == null)
			return null;
		Object[] values = (Object[]) entry.getValues();
		return values[0].toString().trim();
	}
}
