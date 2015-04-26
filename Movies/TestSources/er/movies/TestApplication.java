package er.movies;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;

import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.foundation.ERXProperties;
import er.movies.dbunit.MoviesDBTestCase;

public class TestApplication extends Application
{

	public static final boolean		UPDATE_MODEL_SETTINGS_DEFAULT	= false;
	public static final boolean		USE_IN_MEMORY_DB_DEFAULT		= false;

	public static final boolean		INIT_MEMCACHE_DEFAULT			= true;

	private static final IDataSet	STARTUP_DATASET_DEFAULT			= null;

	// Initialization settings
	private static boolean			updateModelSettings				= UPDATE_MODEL_SETTINGS_DEFAULT;
	private static boolean			useInMemoryDB					= USE_IN_MEMORY_DB_DEFAULT;

	private static IDataSet			startupDataset					= STARTUP_DATASET_DEFAULT;

	private static boolean			initMemcache					= INIT_MEMCACHE_DEFAULT;

	// Prototypes key
	private static final String		EOPrototypesEntityKey			= "dbEOPrototypesEntityGLOBAL";
	// migrations table
	private static String			migrationsTableName				= "db_migrations";

	// backup for old prototype
	private String					dbEOPrototypesEntityGLOBALBackup;

	public TestApplication()
	{
		super();

	}

	@Override
	protected void initMemcache()
	{
		if (initMemcache)
		{
			super.initMemcache();
		}
	}

	@Override
	protected void updateModels() throws Exception
	{
		if (updateModelSettings)
		{
			if (useInMemoryDB)
			{
				log.info("Setting model adaptors to H2");

				NSMutableDictionary<String, Object> connectionDictionary = new NSMutableDictionary<String, Object>();

				connectionDictionary.put("URL", MoviesDBTestCase.dbUrl);
				connectionDictionary.put("username", MoviesDBTestCase.dbUsername);
				connectionDictionary.put("password", MoviesDBTestCase.dbPassword);
				connectionDictionary.put("plugin", MoviesDBTestCase.erprototype);

				dbEOPrototypesEntityGLOBALBackup = ERXProperties.stringForKey(EOPrototypesEntityKey);
				ERXProperties.setStringForKey("EOJDBCH2Prototypes", EOPrototypesEntityKey);

				for (EOModel model : EOModelGroup.defaultGroup().models())
				{
					if (!model.name().equals("erprototypes"))
					{
						model.setConnectionDictionary(connectionDictionary);
					}
				}

				setUpDBModel();

				if (startupDataset != null)
				{
					IDatabaseTester databaseTester = new JdbcDatabaseTester(
							MoviesDBTestCase.dbDriverClass,
							MoviesDBTestCase.dbUrl,
							MoviesDBTestCase.dbUsername,
							MoviesDBTestCase.dbPassword);

					databaseTester.setDataSet(startupDataset);
					databaseTester.onSetup();
				}

			}
			else
			{
				super.updateModels();
			}
		}
		else
		{
			log.info("Skipped model updates");
		}
	}

	private void setUpDBModel() throws ClassNotFoundException, SQLException
	{
		//create _dbupdater table
		// Load the database driver
		Class.forName(MoviesDBTestCase.dbDriverClass);

		ERXProperties.setStringForKey(migrationsTableName, "er.migration.JDBC.dbUpdaterTableName");

		// Get a connection to the database
		Connection connection = DriverManager.getConnection(
				MoviesDBTestCase.dbUrl,
				MoviesDBTestCase.dbUsername,
				MoviesDBTestCase.dbPassword);
		Statement statement = connection.createStatement();

		// create _dbupdater table
		System.out.println("CREATE TABLE " + migrationsTableName + "(" + " lockowner VARCHAR(100)," + " modelname VARCHAR(100) NOT NULL," + " updatelock INT NOT NULL," + " version INT NOT NULL" + ");");

		int result = statement.executeUpdate(
				"CREATE TABLE " + migrationsTableName +
						"(" +
						" lockowner VARCHAR(100)," +
						" modelname VARCHAR(100) NOT NULL," +
						" updatelock INT NOT NULL," +
						" version INT NOT NULL" +
						")"
				);
		//@formatter:on

		if (result == 0)
		{
			log.info("created _dbupdater");
		}
		else
		{
			log.error("could not create _dbupdater");
		}

	}

	public static boolean isUpdateModelSettings()
	{
		return updateModelSettings;
	}

	public static void setUpdateModelSettings(boolean updateModelSettings)
	{
		TestApplication.updateModelSettings = updateModelSettings;
	}

	public static boolean isUseInMemoryDB()
	{
		return useInMemoryDB;
	}

	public static void setUseInMemoryDB(boolean useInMemoryDB)
	{
		TestApplication.useInMemoryDB = useInMemoryDB;
	}

	public static IDataSet getStartupDataset()
	{
		return startupDataset;
	}

	public static void setStartupDataset(IDataSet startupDataset)
	{
		TestApplication.startupDataset = startupDataset;
	}

	public static boolean isInitMemcache()
	{
		return initMemcache;
	}

	public static void setInitMemcache(boolean initMemcache)
	{
		TestApplication.initMemcache = initMemcache;
	}

	public static String getMigrationsTableName()
	{
		return migrationsTableName;
	}

	public static void setMigrationsTableName(String migrationsTableName)
	{
		TestApplication.migrationsTableName = migrationsTableName;
	}

	@Override
	public void terminate()
	{
		super.terminate();

		//reset defaults
		updateModelSettings = UPDATE_MODEL_SETTINGS_DEFAULT;
		initMemcache = INIT_MEMCACHE_DEFAULT;
		useInMemoryDB = USE_IN_MEMORY_DB_DEFAULT;
		startupDataset = STARTUP_DATASET_DEFAULT;

		// reset the EOPropertyEntity to it's previous value
		if (dbEOPrototypesEntityGLOBALBackup == null)
		{
			ERXProperties.removeKey(EOPrototypesEntityKey);
		}
		else
		{
			ERXProperties.setStringForKey(dbEOPrototypesEntityGLOBALBackup, EOPrototypesEntityKey);
		}
	}
}
