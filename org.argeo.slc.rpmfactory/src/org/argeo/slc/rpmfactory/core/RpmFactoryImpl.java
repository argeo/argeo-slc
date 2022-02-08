package org.argeo.slc.rpmfactory.core;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.FileUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcConstants;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.NodeIndexerVisitor;
import org.argeo.slc.rpmfactory.RpmFactory;
import org.argeo.slc.rpmfactory.RpmRepository;
import org.argeo.slc.runtime.tasks.SystemCall;

/**
 * Defines a build environment. This information is typically used by other
 * components performing the various actions related to RPM build.
 */
public class RpmFactoryImpl implements RpmFactory {
	private CmsLog log = CmsLog.getLog(RpmFactoryImpl.class);

	private Repository rpmRepository;
	private Repository distRepository;

	private String id;
	private List<RpmRepository> repositories = new ArrayList<RpmRepository>();
	private List<String> archs = new ArrayList<String>();

	private String rpmBase = "/mnt/slc/repos/rpm";
	private String distBase = "/mnt/slc/repos/dist";
	private String mockVar = "/var/lib/mock";
	private String mockEtc = "/etc/mock";

	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");

	private String gitWorkspace = "git";

	private String localUrlBase = "http://localhost:7070/";
	/** If not null or empty, this is a developer instance. */
	private String gitDevBaseUrl = null;

	private Boolean withTestingRepository = false;

	private String yumConfigMainSection = "cachedir=/var/cache/yum\n"
			+ "debuglevel=1\n" + "reposdir=/dev/null\n"
			+ "logfile=/var/log/yum.log\n" + "retries=20\n" + "obsoletes=1\n"
			+ "gpgcheck=0\n" + "assumeyes=1\n" + "syslog_ident=mock\n"
			+ "syslog_device=\n" + "http_caching=none\n";

	private String defaultMacroFiles = "/usr/lib/rpm/macros:"
			+ "/usr/lib/rpm/ia32e-linux/macros:"
			+ "/usr/lib/rpm/redhat/macros:" + "/etc/rpm/macros.*:"
			+ "/etc/rpm/macros:" + "/etc/rpm/ia32e-linux/macros:"
			+ "~/.rpmmacros";
	private Map<String, String> rpmmacros = new HashMap<String, String>();

	// set by init
	private String proxiedReposBase;
	private String managedReposBase;

	private String stagingWorkspace;
	private String testingWorkspace;
	private String stableWorkspace;

	private File rpmFactoryBaseDir;
	private File mockConfDir;
	private File yumConfDir;

	public void init() {
		// local URL bases
		proxiedReposBase = localUrlBase + "repo/rpm/";
		managedReposBase = localUrlBase + "data/public/rpm/";

		// local directories
		rpmFactoryBaseDir.mkdirs();
		mockConfDir = new File(rpmFactoryBaseDir.getPath() + "/conf/mock");
		mockConfDir.mkdirs();
		yumConfDir = new File(rpmFactoryBaseDir.getPath() + "/conf/yum");
		yumConfDir.mkdirs();

		// managed repositories
		stagingWorkspace = id + "-staging";
		if (withTestingRepository)
			testingWorkspace = id + "-testing";
		stableWorkspace = id;

		initDistWorkspace(stableWorkspace);
		initGitWorkspace();
		initRpmWorkspace(stagingWorkspace);
		if (withTestingRepository)
			initRpmWorkspace(testingWorkspace);
		initRpmWorkspace(stableWorkspace);
	}

	protected void initRpmWorkspace(String workspace) {
		Session session = null;
		try {
			session = JcrUtils.loginOrCreateWorkspace(rpmRepository, workspace);
			JcrUtils.addPrivilege(session, "/", "anonymous", "jcr:read");
			JcrUtils.addPrivilege(session, "/", SlcConstants.ROLE_SLC,
					"jcr:all");

			for (String arch : archs) {
				Node archFolder = JcrUtils.mkfolders(session, "/" + arch);
				session.save();
				File workspaceDir = getWorkspaceDir(workspace);
				try {
					if (!archFolder.hasNode("repodata")) {
						// touch a file in order to make sure this is properly
						// mounted.
						File touch = new File(workspaceDir, ".touch");
						touch.createNewFile();
						touch.delete();

						SystemCall createrepo = new SystemCall();
						createrepo.arg("createrepo");
						createrepo.arg("-q");
						File archDir = new File(workspaceDir, arch);
						createrepo.arg(archDir.getAbsolutePath());
						createrepo.run();
					}
				} catch (IOException e) {
					log.error(workspaceDir + " not properly mounted.", e);
				}
			}
		} catch (Exception e) {
			throw new SlcException("Cannot initialize workspace " + workspace,
					e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}

	/** Caller must logout the underlying session. */
	public Node newDistribution(String distributionId) {
		Session session = null;
		try {
			session = JcrUtils.loginOrCreateWorkspace(rpmRepository,
					distributionId);
			JcrUtils.addPrivilege(session, "/", "anonymous", "jcr:read");
			JcrUtils.addPrivilege(session, "/", SlcConstants.ROLE_SLC,
					"jcr:all");

			Calendar now = new GregorianCalendar();
			String folderName = dateFormat.format(now.getTime());
			return JcrUtils.mkfolders(session, "/" + folderName);
		} catch (Exception e) {
			JcrUtils.logoutQuietly(session);
			throw new SlcException("Cannot initialize distribution workspace "
					+ distributionId, e);
		}
	}

	protected void initGitWorkspace() {
		Session session = null;
		try {
			session = JcrUtils.loginOrCreateWorkspace(rpmRepository,
					gitWorkspace);
			JcrUtils.addPrivilege(session, "/", "anonymous", "jcr:read");
			JcrUtils.addPrivilege(session, "/", SlcConstants.ROLE_SLC,
					"jcr:all");
		} catch (Exception e) {
			throw new SlcException("Cannot initialize workspace "
					+ gitWorkspace, e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}

	protected void initDistWorkspace(String workspace) {
		Session session = null;
		try {
			session = JcrUtils
					.loginOrCreateWorkspace(distRepository, workspace);
			JcrUtils.addPrivilege(session, "/", "anonymous", "jcr:read");
		} catch (RepositoryException e) {
			throw new SlcException("Cannot initialize workspace " + workspace,
					e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}

	public void destroy() {

	}

	public String generateMockConfigFile(String arch, String branch) {
		StringBuffer buf = new StringBuffer();

		buf.append("config_opts['root'] = '" + getIdWithArch(arch) + "'\n");
		buf.append("config_opts['target_arch'] = '" + arch + "'\n");
		buf.append("config_opts['legal_host_arches'] = ('" + arch + "',)\n");
		buf.append("config_opts['chroot_setup_cmd'] = 'groupinstall buildsys-build'\n");
		// buf.append("config_opts['dist'] = 'el6'\n");
		buf.append("config_opts['plugin_conf']['yum_cache_enable'] = False\n");

		buf.append("config_opts['scm'] = False\n");
		buf.append("config_opts['scm_opts']['method'] = 'git'\n");
		buf.append("config_opts['scm_opts']['spec'] = 'SCM_PKG.spec'\n");
		buf.append("config_opts['scm_opts']['ext_src_dir'] = '"
				+ getSourcesDir().getAbsolutePath() + "'\n");
		buf.append("config_opts['scm_opts']['git_timestamps'] = True\n");

		// development
		if (gitDevBaseUrl != null && !gitDevBaseUrl.trim().equals(""))
			buf.append("config_opts['scm_opts']['git_get'] = 'git clone "
					+ (branch != null ? "-b " + branch : "") + " "
					+ gitDevBaseUrl + "/SCM_PKG SCM_PKG'\n");
		else
			buf.append("config_opts['scm_opts']['git_get'] = 'git clone "
					+ (branch != null ? "-b " + branch : "") + " "
					+ getGitBaseUrl() + "/SCM_PKG.git SCM_PKG'\n");

		buf.append("\nconfig_opts['yum.conf'] = \"\"\"\n");
		buf.append(generateYumConfigFile(arch)).append('\n');
		buf.append("\"\"\"\n");
		return buf.toString();
	}

	public String generateYumConfigFile(String arch) {
		StringBuffer buf = new StringBuffer();
		buf.append("[main]\n");
		buf.append(yumConfigMainSection).append('\n');

		for (RpmRepository repository : repositories) {
			buf.append('[').append(repository.getId()).append("]\n");
			buf.append("name=").append(repository.getId()).append('\n');
			if (repository instanceof ThirdPartyRpmRepository) {
				buf.append("#baseurl=").append(repository.getUrl())
						.append(arch).append('/').append("\n");
				buf.append("baseurl=").append(proxiedReposBase)
						.append(repository.getId()).append('/').append(arch)
						.append('/').append("\n");
				if (((ThirdPartyRpmRepository) repository).getYumConf() != null)
					buf.append(
							((ThirdPartyRpmRepository) repository).getYumConf()
									.trim()).append('\n');
			}
		}

		// managed repos
		addManagedRepository(buf, stagingWorkspace, arch);
		if (withTestingRepository)
			addManagedRepository(buf, testingWorkspace, arch);
		addManagedRepository(buf, stableWorkspace, arch);
		return buf.toString();
	}

	protected void addManagedRepository(StringBuffer buf, String workspace,
			String arch) {
		buf.append('[').append(workspace).append("]\n");
		buf.append("baseurl=").append(managedReposBase).append(workspace)
				.append('/').append(arch).append('/').append("\n");
		buf.append("gpgcheck=0").append("\n");
	}

	/** Creates a mock config file. */
	public File getMockConfigFile(String arch, String branch) {
		File mockSiteDefaultsFile = new File(mockConfDir, "site-defaults.cfg");
		File mockLoggingFile = new File(mockConfDir, "logging.ini");
		File mockConfigFile = new File(mockConfDir, getIdWithArch(arch)
				+ ".cfg");
		try {
			if (!mockSiteDefaultsFile.exists())
				mockSiteDefaultsFile.createNewFile();
			if (!mockLoggingFile.exists())
				FileUtils.copyFile(new File(mockEtc + "/logging.ini"),
						mockLoggingFile);

			FileUtils.writeStringToFile(mockConfigFile,
					generateMockConfigFile(arch, branch));
			return mockConfigFile;
		} catch (IOException e) {
			throw new SlcException("Cannot write mock config file to "
					+ mockConfigFile, e);
		}
	}

	/** Creates a yum config file. */
	public File getYumRepoFile(String arch) {
		File yumConfigFile = new File(yumConfDir, getIdWithArch(arch) + ".repo");
		try {
			FileUtils.writeStringToFile(yumConfigFile,
					generateYumConfigFile(arch));
			return yumConfigFile;
		} catch (IOException e) {
			throw new SlcException("Cannot write yum config file to "
					+ yumConfigFile, e);
		}
	}

	public File getResultDir(String arch) {
		return new File(mockVar + "/" + getIdWithArch(arch) + "/result");
	}

	public File getWorkspaceDir(String workspace) {
		return new File(rpmBase + "/" + workspace);
	}

	public File getSourcesDir() {
		return new File(distBase + "/" + stableWorkspace);
	}

	public String getMockConfig(String arch) {
		return getIdWithArch(arch);
	}

	public String getIdWithArch(String arch) {
		return id + "-" + arch;
	}

	public String getGitBaseUrl() {
		return managedReposBase + gitWorkspace;
	}

	public void indexWorkspace(String workspace) {
		Session session = null;
		try {
			session = rpmRepository.login(workspace);
			session.getRootNode().accept(
					new NodeIndexerVisitor(new RpmIndexer()));
			if (log.isDebugEnabled())
				log.debug("Indexed workspace " + workspace);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot index workspace " + workspace, e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
	}

	public Boolean isDeveloperInstance() {
		return gitDevBaseUrl != null;
	}

	/** Write (topdir)/rpmmacros and (topdir)/rpmrc */
	public void writeRpmbuildConfigFiles(File topdir) {
		writeRpmbuildConfigFiles(topdir, new File(topdir, "rpmmacros"),
				new File(topdir, "rpmrc"));
	}

	public void writeRpmbuildConfigFiles(File topdir, File rpmmacroFile,
			File rpmrcFile) {
		try {
			List<String> macroLines = new ArrayList<String>();
			macroLines.add("%_topdir " + topdir.getCanonicalPath());
			for (String macroKey : rpmmacros.keySet()) {
				macroLines.add(macroKey + " " + rpmmacros.get(macroKey));
			}
			FileUtils.writeLines(rpmmacroFile, macroLines);

			List<String> rpmrcLines = new ArrayList<String>();
			rpmrcLines.add("include: /usr/lib/rpm/rpmrc");
			rpmrcLines.add("macrofiles: " + defaultMacroFiles + ":"
					+ rpmmacroFile.getCanonicalPath());
			FileUtils.writeLines(rpmrcFile, rpmrcLines);
		} catch (IOException e) {
			throw new SlcException("Cannot write rpmbuild config files", e);
		}

	}

	public Map<String, String> getRpmmacros() {
		return rpmmacros;
	}

	public void setRpmmacros(Map<String, String> rpmmacros) {
		this.rpmmacros = rpmmacros;
	}

	public String getDefaultMacroFiles() {
		return defaultMacroFiles;
	}

	public void setDefaultMacroFiles(String defaultMacroFiles) {
		this.defaultMacroFiles = defaultMacroFiles;
	}

	public void setArchs(List<String> archs) {
		this.archs = archs;
	}

	public List<String> getArchs() {
		return archs;
	}

	public void setRpmBase(String stagingBase) {
		this.rpmBase = stagingBase;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setMockVar(String mockVar) {
		this.mockVar = mockVar;
	}

	public void setRpmRepository(Repository rpmRepository) {
		this.rpmRepository = rpmRepository;
	}

	public void setDistRepository(Repository distRepository) {
		this.distRepository = distRepository;
	}

	public void setLocalUrlBase(String localUrlBase) {
		this.localUrlBase = localUrlBase;
	}

	public void setYumConfigMainSection(String yumConfigMainSection) {
		this.yumConfigMainSection = yumConfigMainSection;
	}

	public void setRepositories(List<RpmRepository> repositories) {
		this.repositories = repositories;
	}

	public void setRpmFactoryBaseDir(File rpmFactoryBaseDir) {
		this.rpmFactoryBaseDir = rpmFactoryBaseDir;
	}

	public String getStagingWorkspace() {
		return stagingWorkspace;
	}

	public String getTestingWorkspace() {
		return testingWorkspace;
	}

	public String getStableWorkspace() {
		return stableWorkspace;
	}

	public void setWithTestingRepository(Boolean withTestingRepository) {
		this.withTestingRepository = withTestingRepository;
	}

	public void setGitDevBaseUrl(String gitBaseUrl) {
		this.gitDevBaseUrl = gitBaseUrl;
	}
}
