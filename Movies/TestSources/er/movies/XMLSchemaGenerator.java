package er.movies;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.LowerCaseDataSet;
import org.dbunit.dataset.xml.FlatDtdWriter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

import er.extensions.ERXExtensions;
import er.movies.dbunit.MoviesDBTestCase;

/**
 * This application generates a xml schema for the the models setup by the migrations
 * IMPORTANT: For this to work you have to run this as a WOapplication
 */
public class XMLSchemaGenerator
{

	public static void main(String[] args) throws Exception
	{
		// Update the dtd used by the dataset files
		//@formatter:off
		XMLSchemaGenerator.createDtd(
				MoviesDBTestCase.dbUrl,
				MoviesDBTestCase.dbUsername,
				MoviesDBTestCase.dbPassword,
				"/Users/gdessena/workspace/movies-multiproject/Movies/TestResources/schemas/db_model.dtd"
		);
		//@formatter:on

	}

	public static void createDtd(String url, String username, String password, String dtdFilename) throws Exception
	{
		Class.forName(MoviesDBTestCase.dbDriverClass);
		Connection jdbcConnection = DriverManager.getConnection(url, username, password);
		IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

		TestApplication.setUseInMemoryDB(true);
		TestApplication.setUpdateModelSettings(true);
		TestApplication.setInitMemcache(false);

		FlatXmlDataSetBuilder builder = (new FlatXmlDataSetBuilder());
		builder.setDtdMetadata(false);
		//		TestApplication.setStartupDataset(builder.build(new File("TestResources/scenarios/setups/setup_world.xml")));
		ERXExtensions.initApp(TestApplication.class, new String[0]);

		IDataSet dataSet = connection.createDataSet();
		Writer out = new OutputStreamWriter(new FileOutputStream(dtdFilename));
		FlatDtdWriter datasetWriter = new FlatDtdWriter(out);
		datasetWriter.setContentModel(FlatDtdWriter.CHOICE);

		// You could also use the sequence model which is the default
		// datasetWriter.setContentModel(FlatDtdWriter.SEQUENCE);
		datasetWriter.write(new LowerCaseDataSet(dataSet));

		TestApplication.application().terminate();
	}

}
