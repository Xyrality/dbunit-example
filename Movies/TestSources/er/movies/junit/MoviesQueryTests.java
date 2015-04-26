package er.movies.junit;

import org.junit.Test;

import webobjectsexamples.businesslogic.movies.common.Movie;
import webobjectsexamples.businesslogic.movies.common.MovieRole;
import webobjectsexamples.businesslogic.movies.common.Talent;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

import er.extensions.eof.ERXEC;
import er.movies.dbunit.MoviesDBTestCase;

public class MoviesQueryTests extends MoviesDBTestCase
{

	public MoviesQueryTests(String name) throws Exception
	{
		super(name);
	}

	@Test
	public void testQueryFetchAllMoviesWithTalent()
	{
		EOEditingContext editingContext = ERXEC.newEditingContext();

		// fetch data loaded with the dataset
		Talent bruceWillis = Talent.fetchTalent(editingContext, Talent.FIRST_NAME.eq("Bruce").and(Talent.LAST_NAME.eq("Willis")));
		Movie dieHard3 = Movie.fetchMovie(editingContext, Movie.TITLE.eq("Die Hard 3"));

		// create a new role to add to the database
		MovieRole johnMcClane = MovieRole.Builder.create()
				.movie(dieHard3)
				.talent(bruceWillis)
				.roleName("John McClane")
				.build(editingContext);

		editingContext.saveChanges();

		dieHard3.addToRoles(johnMcClane);

		editingContext.saveChanges();

		NSArray<Movie> movies = Movie.fetchMovies(
				editingContext,
				Movie.ROLES.containsAnyObjectSatisfying(MovieRole.TALENT.eq(bruceWillis)),
				Movie.TITLE.ascs());

		// check that we have two Bruce willis movies
		assertEquals("There should be 2 Bruce Willis movies in here!!!", 2, movies.size());

	}
}
