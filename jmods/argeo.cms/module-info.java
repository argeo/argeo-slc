/** Dependencies of the core Argeo CMS, for use with jmod and jlink. */
module argeo.cms {
	requires java.naming;
	requires java.xml;
	requires java.xml.crypto;
	requires java.transaction.xa;
	requires java.sql;
	requires java.net.http;
	requires java.desktop;
	requires java.management;
	requires java.rmi;
	requires java.scripting;
	requires java.security.sasl;

	requires jdk.httpserver;
	requires jdk.jshell;
	requires jdk.security.auth;
	requires jdk.xml.dom;
	requires jdk.net;
	requires jdk.nio.mapmode;
	requires jdk.crypto.cryptoki;
	requires jdk.crypto.ec;
	requires jdk.unsupported;
	requires jdk.management;
	requires jdk.management.agent;
	
	requires org.eclipse.osgi;

	requires org.argeo.init;
}