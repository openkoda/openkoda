package com.openkoda.service.upgrade;

import com.openkoda.model.DbVersion;
import com.openkoda.repository.DbVersionRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A service that during Openkoda startup performs upgrades of the database mode and existing data. It's up to developer to update given upgrade script, this service is just a runner.
 * Each model version change is executed from the oldest to the latest version that has not been yet applied. Once it's succesfully applied it should be permanently skipped.
 * 
 * The upgrade script file should be developed incrementally
 *
 * @author borowa
 * @since 28-05-2024
 */
@Service
public class DbVersionService {

    // default script and versioning queries
    static final String MIGRATION_UPGRADE_SQL = "/migration/core_upgrade.sql";
    static final String FIND_CURRENT = """
            SELECT v.major, v.minor, v.build, v.revision, v.done, v.note, (v.major * 10000000 + v.minor * 100000 + v.build * 100 + v.revision) AS "version_numeric"
            FROM db_version v 
            WHERE v.done = true 
            ORDER BY version_numeric DESC
            LIMIT 1
            """;
    static final String FIND_OLDER_INSTALLED = """
            SELECT v.major, v.minor, v.build, v.revision, v.done, v.note, (v.major * 10000000 + v.minor * 100000 + v.build * 100 + v.revision) AS "version_numeric"
            FROM db_version v 
            WHERE v.done = true AND (v.major * 10000000 + v.minor * 100000 + v.build * 100 + v.revision) < $1
            ORDER BY version_numeric ASC
            """;
    
    static final String VERSION_INSERT = """
            INSERT INTO public.db_version
                (id, created_by, created_by_id, created_on, modified_by, modified_by_id, updated_on,  major, minor, build, revision, done,note)
            VALUES(nextval('seq_global_id'), '', 0, CURRENT_TIMESTAMP, '', 0, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?);
            """;
    
    static final String DV_VERSION_DDL = """
                CREATE TABLE IF NOT EXISTS public.db_version (
                    id int8 NOT NULL,
                    created_by varchar(255) NULL,
                    created_by_id int8 NULL,
                    created_on timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
                    modified_by varchar(255) NULL,
                    modified_by_id int8 NULL,
                    updated_on timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
                    build int4 NOT NULL,
                    done bool NULL,
                    major int4 NOT NULL,
                    minor int4 NOT NULL,
                    note varchar(255) NULL,
                    revision int4 NOT NULL,
                    CONSTRAINT db_version_pkey PRIMARY KEY (id)
                );
            """;

    private static final String APP_VERSION_REGEXP = "(\\d+)\\.(\\d+)\\.(\\d+).*";
    private static final String VERSION_REGEXP = "^--\\s*@version\\s*:\\s*(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+).*";
    private static final String RUN_ON_INIT_REGEXP = "^--\\s*@init.*";
    private static final Pattern APP_VERSION_PATTERN = Pattern.compile(APP_VERSION_REGEXP);
    private static final Pattern VERSION_PATTERN = Pattern.compile(VERSION_REGEXP);
    private static final Pattern RUN_ON_INIT_PATTERN = Pattern.compile(RUN_ON_INIT_REGEXP);
    
    private DbVersionRepository repository;

    @Value("${upgrade.db.file:" + MIGRATION_UPGRADE_SQL +"}")
    private String upgradeScript = MIGRATION_UPGRADE_SQL;
    
    @Value("${upgrade.db.current:" + FIND_CURRENT + "}")
    private String currentVersionQuery = FIND_CURRENT;

    @Value("${upgrade.db.installed:" + FIND_OLDER_INSTALLED + "}")
    private String allInstalledQuery = FIND_OLDER_INSTALLED;
    
    @Value("${upgrade.db.insert:" + VERSION_INSERT + "}")
    private String dbVersionInsertQuery = VERSION_INSERT;
    
    private boolean isForce = false;
    
    public DbVersionService(String upgradeScript, String currentVersionQuery,
            String dbVersionInsertQuery, boolean isForce) {
        this.upgradeScript = StringUtils.defaultIfBlank(upgradeScript, MIGRATION_UPGRADE_SQL);
        this.currentVersionQuery = StringUtils.defaultIfBlank(currentVersionQuery, FIND_CURRENT);
        this.dbVersionInsertQuery = StringUtils.defaultIfBlank(dbVersionInsertQuery, VERSION_INSERT);
        this.isForce = isForce;
    }

    @Autowired
    public DbVersionService() {
    }

    public DbVersion getCurrentVersion() {
        return repository.findCurrentDbVersion();
    }
    
    /**
     * Tries to find the latest DB model version that has been succesfully applied so far
     * @param con
     * @return
     * @throws SQLException
     */
    public DbVersion getCurrentVersion(Connection con) throws SQLException {
        try(Statement stmt = con.createStatement()) {
            try (ResultSet resultSet = stmt.executeQuery(currentVersionQuery)) {
                // use resultSet here
                if(resultSet.next()) {
                    DbVersion current = new DbVersion();
                    current.setMajor(resultSet.getInt("major"));
                    current.setMinor(resultSet.getInt("minor"));
                    current.setBuild(resultSet.getInt("build"));
                    current.setRevision(resultSet.getInt("revision"));
                    current.setDone(resultSet.getBoolean("done"));
                    current.setNote(resultSet.getString("note"));
                    return current;
                }
                
                return null;
            } catch (SQLException sqle) {
                System.out.println(sqle.getMessage());
                return null;
            }
        }
    }
    
    /**
     * Tries to find the latest DB model version that has been succesfully applied so far
     * @param con
     * @return
     * @throws SQLException
     */
    public List<DbVersion> findAllInstalled(Connection con, DbVersion appVersion) throws SQLException {
        List<DbVersion> installedVersion = new ArrayList<>();
        try(Statement stmt = con.createStatement()) {
            try (ResultSet resultSet = stmt.executeQuery(getFindInstalledQuery(appVersion))) {
                // use resultSet here
                while(resultSet.next()) {
                    DbVersion current = new DbVersion();
                    current.setMajor(resultSet.getInt("major"));
                    current.setMinor(resultSet.getInt("minor"));
                    current.setBuild(resultSet.getInt("build"));
                    current.setRevision(resultSet.getInt("revision"));
                    current.setDone(resultSet.getBoolean("done"));
                    current.setNote(resultSet.getString("note"));
                    installedVersion.add(current);
                }
            } catch (SQLException sqle) {
                System.out.println(sqle.getMessage());
            }
        }
        
        return installedVersion;
    }

    /**
     * @param appVersion
     * @return
     */
    public String getFindInstalledQuery(DbVersion appVersion) {
        return allInstalledQuery.replace("$1", Integer.toString(appVersion.hashCode()));
    }
    
    /**
     * Performs and upgrade. Only higher versions then the current one are processed and not higher than actual app version
     * 
     * @param currentVersion
     * @param upgradeScriptsMap
     * @param con
     * @return
     * @throws SQLException
     */
    public boolean runUpgrade(DbVersion appVersion, DbVersion currentVersion, List<DbVersion> allInstalled,
            Map<DbVersion, String> upgradeScriptsMap, Connection con) throws SQLException {
        con.setAutoCommit(false);
        List<Map.Entry<DbVersion, String>> versionsToRun = upgradeScriptsMap.entrySet().stream()
                .filter( ev -> ev.getKey() != null && (currentVersion == null 
                                    || currentVersion != null 
                                        && allInstalled.stream().noneMatch( ai -> ai.hashCode() == ev.getKey().hashCode())))
                .filter( ev -> ev.getKey() != null && appVersion != null && ev.getKey().compareTo(appVersion) <= 0)
                .sorted( (v1, v2) -> v1.getKey().compareTo(v2.getKey())) .toList();
        for (Entry<DbVersion, String> ev : versionsToRun) {
            if(currentVersion != null || currentVersion == null && ev.getKey().isRunOnInit()) {
                if(currentVersion != null && currentVersion.compareTo(ev.getKey()) > 0) {
                    System.out.printf("%s Upgrading to %s as it was probably not yet executed %n", Character.toString(0x1F5C0), ev.getKey());
                } else {
                    System.out.printf("%s Upgrading to %s%n", Character.toString(0x1F5C0), ev.getKey());
                }
                try(Statement stmt = con.createStatement()) {
                    ev.getKey().setDone(true);
                    stmt.execute(ev.getValue().trim());
                } catch (SQLException e) {
                    System.out.printf("%s Upgrade failed due to : %s%n", Character.toString(0x1F5C0), e.getMessage());
                    ev.getKey().setDone(false);
                    ev.getKey().setNote(e.getMessage());
                    try {
                        con.rollback();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                System.out.printf("%s Skipping due to initialization to %s%n", Character.toString(0x1F5C0), ev.getKey());
                ev.getKey().setDone(true);
            }
            
            prepareInsert(ev.getKey(), con);
            if(Boolean.FALSE.equals(ev.getKey().getDone())) {
                proceedOnError(ev.getKey());
                return false;
            }
        }

        return true;
    }
    
    private void proceedOnError(DbVersion dbVersion) {
        System.out.println("*********************************************************************");
        System.out.printf("%s Could not upgrade to %s and further versions %n", Character.toString(0x1F5C0), dbVersion);
        System.out.printf("%s Proceed with startup? (y/n) ", Character.toString(0x1F5C0));
        if(isForce) {
            System.out.println(" Force mode, assuming yes");
        } else {                    
            int c;
            try {
                c = System.in.read();
                if (c != 'y') {
                    System.out.println(Character.toString(0x1F5C0) + " Unfinished upgrade, stopping");
                    System.exit(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("*********************************************************************");
    }
    
    private void prepareInsert(DbVersion ver, Connection con) {
        try(PreparedStatement stmt = con.prepareStatement(dbVersionInsertQuery)) {
            stmt.setInt(1, ver.getMajor());
            stmt.setInt(2, ver.getMinor());
            stmt.setInt(3, ver.getBuild());
            stmt.setInt(4, ver.getRevision());
            stmt.setBoolean(5, ver.getDone());
            stmt.setString(6, ver.getNote());
            stmt.execute();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads an upgrade script. Each model version/change should be marked in a following way :
     * -- @version: <major>.<minor>.<build>.<revision>
     * 
     * @return
     * @throws IOException
     */
    public Map<DbVersion, String> loadUpgradeSteps() throws IOException {
        Resource upgradeSql = new ClassPathResource(upgradeScript);
        Map<DbVersion, String> versionScripts = new HashMap<>();
        if(upgradeSql.exists()) {
            InputStreamReader streamReader = new InputStreamReader(upgradeSql.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            Matcher m = null;
            DbVersion version = null;
            StringBuilder currentVersionScript = new StringBuilder();
            for (String line; (line = reader.readLine()) != null;) {
                if(line.startsWith("--") && (m = VERSION_PATTERN.matcher(line)).matches()) {
                    // finish current version and prepre lines for a next one
                    if (version != null) {
                        versionScripts.put(version, currentVersionScript.toString());
                        currentVersionScript = new StringBuilder();
                    }
                    
                    Integer major = Integer.valueOf(m.group(1));
                    Integer minor = Integer.valueOf(m.group(2));
                    Integer build = Integer.valueOf(m.group(3));
                    Integer revision = Integer.valueOf(m.group(4));
                    version = new DbVersion(major, minor, build, revision);
                } else if(line.startsWith("--") && (m = RUN_ON_INIT_PATTERN.matcher(line)).matches()) {
                    version.setRunOnInit(true);
                } else if(!line.startsWith("--") && StringUtils.isNotBlank(line)){
                    currentVersionScript.append(line).append("\n");
                }
            }
            
            if (version != null) {
                versionScripts.put(version, currentVersionScript.toString());
            }
        }
        
        return versionScripts;
    }
    
    DbVersion getAppVersion() throws IOException {
        String appVersionString = "0.0.0";
        DbVersion appVersion =  new DbVersion(0, 0, 0, 99);
        Enumeration<URL> resources = getClass().getClassLoader()
                .getResources("META-INF/MANIFEST.MF");
              while (resources.hasMoreElements()) {
                  try {
                    Manifest manifest = new Manifest(resources.nextElement().openStream());
                    if("openkoda".equals(manifest.getMainAttributes().getValue("Implementation-Title"))){
                        appVersionString = manifest.getMainAttributes().getValue("Implementation-Version");
                        Matcher m = APP_VERSION_PATTERN.matcher(appVersionString);
                        if(m.matches()) {
                            appVersion.setMajor(Integer.parseInt(m.group(1)));
                            appVersion.setMinor(Integer.parseInt(m.group(2)));
                            appVersion.setBuild(Integer.parseInt(m.group(3)));
                        }
                        
                        break;
                    }
                  } catch (IOException E) {
                    // handle
                      E.printStackTrace();
                  }
              }
          return appVersion;
    }

    /**
     * checks for the latest version that has been applied, loads the upgrade script and then executes each step
     * 
     * @param con
     * @throws SQLException
     * @throws IOException
     */
    public void tryUpgade(Connection con) throws SQLException, IOException {
        // revision is ignored when comparing app version
        DbVersion appVersion = getAppVersion();
        
        List<DbVersion> allInstalled = findAllInstalled(con, appVersion);
        DbVersion currentVersion = getCurrentVersion(con);
        System.out.println(Character.toString(0x1F5C0) + " Current app version      : " + appVersion);
        System.out.println(Character.toString(0x1F5C0) + " Current DB model version : " + currentVersion);        
        Map<DbVersion, String> upgradeScriptsMap = loadUpgradeSteps();
        if(currentVersion == null) {
            System.out.println(Character.toString(0x1F5C0) + " Initializing db model versions");
            try(Statement stmt = con.createStatement()) {
                stmt.execute(DV_VERSION_DDL);
            }
        }

        runUpgrade(appVersion, currentVersion, allInstalled, upgradeScriptsMap, con);
    }
}
