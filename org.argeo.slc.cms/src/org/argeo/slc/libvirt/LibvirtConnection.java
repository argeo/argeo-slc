package org.argeo.slc.libvirt;

import java.util.UUID;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;

public class LibvirtConnection {
	public static void main(String[] args) throws LibvirtException {
		Connect conn = new Connect("qemu:///system", true);
		System.out.println("## Active domains");
		for (int id : conn.listDomains()) {
			Domain domain = conn.domainLookupByID(id);
			UUID uuid = UUID.fromString(domain.getUUIDString());
			System.out.println(uuid + " - " + domain.getName());
		}
		System.out.println("## Inactive domains");
		for (String name : conn.listDefinedDomains()) {
			Domain domain = conn.domainLookupByName(name);
			UUID uuid = UUID.fromString(domain.getUUIDString());
			System.out.println(uuid + " - " + domain.getName());
		}
	}
}