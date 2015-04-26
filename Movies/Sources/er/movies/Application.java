package er.movies;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import webobjectsexamples.businesslogic.movies.common.Movie;

import com.webobjects.foundation.development.NSBundleFactory;
import com.webobjects.foundation.development.NSStandardProjectBundle;

import er.extensions.appserver.ERXApplication;
import er.movies.rest.MoviesController;
import er.rest.routes.ERXRoute;
import er.rest.routes.ERXRouteRequestHandler;

public class Application extends ERXApplication
{
	private static final String	FATAL_LOAD_SETTINGS	= "Could not initialize application - TERMINATING!";

	public static void main(String[] argv)
	{

		NSBundleFactory.registerBundleFactory(new NSStandardProjectBundle.Factory());
		if (isDevelopmentModeSafe())
		{
			reorderClasspath(Application.class);
		}

		ERXApplication.main(argv, Application.class);
	}

	public Application()
	{
		ERXApplication.log.info("Application " + name() + " initializes now.");
		try
		{
			updateModels();
			initMemcache();
		}
		catch (Exception e)
		{
			String terminatingMessage = e.getMessage() != null ? FATAL_LOAD_SETTINGS + " " + e.getMessage() : FATAL_LOAD_SETTINGS;
			log.fatal(terminatingMessage, e);
			this.terminate();
			return;
		}

		ERXApplication.log.info("Welcome to " + name() + " !");
		//		ERTaggableEntity.registerTaggable(Movie.ENTITY_NAME);

		ERXRouteRequestHandler restReqHandler = new ERXRouteRequestHandler();

		restReqHandler.addDefaultRoutes(Movie.ENTITY_NAME);
		restReqHandler.insertRoute(new ERXRoute(Movie.ENTITY_NAME, "/movies", ERXRoute.Method.Options, MoviesController.class, "options"));

		ERXRouteRequestHandler.register(restReqHandler);

	}

	protected void updateModels() throws Exception
	{
		// do some model initializations
	}

	protected void initMemcache()
	{
		// connect to memcache
	}

	public static void reorderClasspath(Class<?> anApplicationClass)
	{
		URL anApplicationSourceURL = anApplicationClass.getProtectionDomain().getCodeSource().getLocation();
		String anApplicationClasspathEntry = anApplicationSourceURL.getProtocol().equals("file") ? anApplicationSourceURL.getPath() : anApplicationSourceURL.toExternalForm();

		final String[] aClasspathEntryArray = System.getProperty("java.class.path").split(File.pathSeparator);
		final List<String> aNewClasspath = new ArrayList<String>();
		final List<String> aTestClassesClasspath = new ArrayList<String>();

		aNewClasspath.add(anApplicationClasspathEntry);
		for (String aClasspathEntry : aClasspathEntryArray)
		{
			if (aClasspathEntry.endsWith(File.separator + "target" + File.separator + "test-classes"))
			{
				aTestClassesClasspath.add(aClasspathEntry);
			}
			else
			{
				aNewClasspath.add(aClasspathEntry);
			}
		}
		aNewClasspath.addAll(aTestClassesClasspath);

		final StringBuilder aNewClasspathJoined = new StringBuilder();
		for (String aNewClasspathEntry : aNewClasspath)
		{
			if (aNewClasspathJoined.length() > 0)
			{
				aNewClasspathJoined.append(File.pathSeparator);
			}
			aNewClasspathJoined.append(aNewClasspathEntry);
		}
		System.setProperty("java.class.path", aNewClasspathJoined.toString());
	}

}
