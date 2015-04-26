# Integration testing with DBUnit and WO

This is a multi project gradle port of the Movies and ERMoviesLogic demo projects, that demonstarates how you can get WO to work with dbunit.

## Important quirks

### Reorder Classpath for gradle to be able to execute tests
```groovy
test.doFirst {
	sourceSets.test.runtimeClasspath = files(
		project.configurations.compile.find { it.name.startsWith("ERExtensions") }.path) 
		+ sourceSets.test.runtimeClasspath
}
```

### Initialize NSBundle manually in your DBUnit test case

```java
public abstract class YourDBTestCase extends DBTestCase
{

	public MoviesDBTestCase(String name) throws Exception
	{
		super(name);

		// Classpath fixes
		// NSBundle needs to initialized before being able to be run and by doing so it messes up the classpath
		NSBundleFactory.registerBundleFactory(new NSStandardProjectBundle.Factory());

		// reorder the classpath 
		// - put test classes back at the end of the class path
		// - put the application class back to the top
		Application.reorderClasspath(Application.class);
	}
}

### Initialize NSBundle manually in your Application class

```java
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
}
```


 