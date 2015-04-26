package webobjectsexamples.businesslogic.movies.common;

import org.apache.log4j.Logger;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

public class Movie extends _Movie
{
	private static Logger	log	= Logger.getLogger(Movie.class);

	@SuppressWarnings("all")
	public String content()
	{
		if (log.isDebugEnabled())
		{
			log.debug("Movie.searchableContent: " + title());
		}
		StringBuilder buffer = new StringBuilder();

		buffer.append(title());
		buffer.append(' ');

		String studioName = (String)valueForKeyPath(Movie.STUDIO.dot(Studio.NameKey).toString());
		if (studioName != null)
		{
			buffer.append(studioName);
			buffer.append(' ');
		}

		NSArray directorNames = (NSArray)valueForKeyPath(Movie.DIRECTORS.dot("fullName").toString());
		if (directorNames != null && directorNames.count() > 0)
		{
			buffer.append(directorNames.componentsJoinedByString(" "));
			buffer.append(' ');
		}

		NSArray talentNames = (NSArray)valueForKeyPath(Movie.ROLES.dot(MovieRole.TALENT).dot("fullName").toString());
		if (talentNames != null && talentNames.count() > 0)
		{
			String talentNamesString = talentNames.componentsJoinedByString(" ");
			NSLog.out.appendln("Movie.searchableContent: talent names: " + talentNamesString);
			buffer.append(talentNamesString);
			buffer.append(' ');
		}
		if (log.isDebugEnabled())
		{
			log.debug("Movie.content: " + buffer);
		}
		return buffer.toString();
	}

	// first and only mandatory attribute
	public interface ITitle
	{
		public IPlotSummary title(String title);
	}

	// mandatory to one relationship interfaces
	public interface IPlotSummary
	{
		public IVoting plotSummary(webobjectsexamples.businesslogic.movies.common.PlotSummary plotSummary);

	}

	// only mandatory to-one-relationship
	public interface IVoting
	{
		public Builder voting(webobjectsexamples.businesslogic.movies.common.Voting voting);

	}

	//mandatory to many relationship interface

	public static class Builder
			implements
			ITitle,
			IPlotSummary
			,
			IVoting
	{

		EOEditingContext																editingContext;

		// attributes
		private String																	category;
		private NSTimestamp																dateReleased;
		private String																	posterName;
		private String																	rated;
		private java.math.BigDecimal													revenue;
		private String																	title;
		private String																	trailerName;

		// To one relationships
		private webobjectsexamples.businesslogic.movies.common.PlotSummary				plotSummary;
		private er.attachment.model.ERAttachment										poster;
		private webobjectsexamples.businesslogic.movies.server.Studio					studio;
		private webobjectsexamples.businesslogic.movies.common.Voting					voting;

		// to many retlationships
		private NSArray<webobjectsexamples.businesslogic.movies.common.Talent>			directors;
		private NSArray<webobjectsexamples.businesslogic.movies.common.Talent.Builder>	directorsBuilders;
		private NSArray<webobjectsexamples.businesslogic.movies.common.Review>			reviews;
		private NSArray<webobjectsexamples.businesslogic.movies.common.MovieRole>		roles;

		public Movie build(final EOEditingContext editingContext)
		{
			this.editingContext = editingContext;

			// build to-one-relationships
			if (plotSummary == null)
			{

			}
			else
			{
				plotSummary.localInstanceIn(editingContext);
			}
			if (poster == null)
			{

			}
			else
			{
				poster.localInstanceIn(editingContext);
			}
			if (studio == null)
			{

			}
			else
			{
				studio.localInstanceIn(editingContext);
			}
			if (voting == null)
			{

			}
			else
			{
				voting.localInstanceIn(editingContext);
			}

			Movie result = Movie.createMovie(editingContext,
					title
					);

			// set non mandatory to-one-relationships
			if (poster != null)
			{
				result.setPosterRelationship(poster);
			}
			if (studio != null)
			{
				result.setStudioRelationship(studio);
			}
			// set non mandatory attributes
			if (category != null)
			{
				result.setCategory(category);
			}
			if (dateReleased != null)
			{
				result.setDateReleased(dateReleased);
			}
			if (posterName != null)
			{
				result.setPosterName(posterName);
			}
			if (rated != null)
			{
				result.setRated(rated);
			}
			if (revenue != null)
			{
				result.setRevenue(revenue);
			}
			if (trailerName != null)
			{
				result.setTrailerName(trailerName);
			}

			//build and add to-many-relationships
			if (directors == null)
			{
				if (directorsBuilders != null && !directorsBuilders.isEmpty())
				{
					NSMutableArray<webobjectsexamples.businesslogic.movies.common.Talent> builtRelationships = new NSMutableArray<webobjectsexamples.businesslogic.movies.common.Talent>();
					for (webobjectsexamples.businesslogic.movies.common.Talent.Builder builder : directorsBuilders)
					{
						result.addToDirectorsRelationship(builder.build(editingContext));
					}
				}
			}
			else
			{
				for (webobjectsexamples.businesslogic.movies.common.Talent entity : directors)
				{
					result.addToDirectorsRelationship(entity.localInstanceIn(editingContext));
				}
			}
			if (reviews == null)
			{

			}
			else
			{
				for (webobjectsexamples.businesslogic.movies.common.Review entity : reviews)
				{
					result.addToReviewsRelationship(entity.localInstanceIn(editingContext));
				}
			}
			if (roles == null)
			{

			}
			else
			{
				for (webobjectsexamples.businesslogic.movies.common.MovieRole entity : roles)
				{
					result.addToRolesRelationship(entity.localInstanceIn(editingContext));
				}
			}

			return result;
		}

		public Movie buildAndSave(final EOEditingContext editingContext)
		{
			Movie result = build(editingContext);
			editingContext.saveChanges();
			return result;
		}

		// mandatory attribute methods

		// no mandatoryToOneRelationship return Builder
		public IPlotSummary title(String title)
		{
			this.title = title;
			return this;
		}

		// mandatory to one relationship method implementations

		public IVoting plotSummary(webobjectsexamples.businesslogic.movies.common.PlotSummary plotSummary)
		{
			this.plotSummary = plotSummary;
			return this;
		}

		// last mandatory relationship
		public Builder voting(webobjectsexamples.businesslogic.movies.common.Voting voting)
		{
			this.voting = voting;
			return this;
		}

		//mandatory to many relationship method implementations

		// non mandatory attribute methods

		public Builder category(String category)
		{
			this.category = category;
			return this;
		}

		public Builder dateReleased(NSTimestamp dateReleased)
		{
			this.dateReleased = dateReleased;
			return this;
		}

		public Builder posterName(String posterName)
		{
			this.posterName = posterName;
			return this;
		}

		public Builder rated(String rated)
		{
			this.rated = rated;
			return this;
		}

		public Builder revenue(java.math.BigDecimal revenue)
		{
			this.revenue = revenue;
			return this;
		}

		public Builder trailerName(String trailerName)
		{
			this.trailerName = trailerName;
			return this;
		}

		// non mandatory to one relationship methods

		// --------------- poster methods ---------------

		public Builder poster(er.attachment.model.ERAttachment poster)
		{
			this.poster = poster;
			return this;
		}

		// --------------- studio methods ---------------

		public Builder studio(webobjectsexamples.businesslogic.movies.server.Studio studio)
		{
			this.studio = studio;
			return this;
		}

		public Builder directorsFromEOArray(NSArray<webobjectsexamples.businesslogic.movies.common.Talent> directors)
		{
			this.directors = directors;
			return this;
		}

		public Builder directorsFromBuilderArray(NSArray<webobjectsexamples.businesslogic.movies.common.Talent.Builder> directorsBuilders)
		{
			this.directorsBuilders = directorsBuilders;
			return this;
		}

		public Builder reviewsFromEOArray(NSArray<webobjectsexamples.businesslogic.movies.common.Review> reviews)
		{
			this.reviews = reviews;
			return this;
		}

		public Builder rolesFromEOArray(NSArray<webobjectsexamples.businesslogic.movies.common.MovieRole> roles)
		{
			this.roles = roles;
			return this;
		}

		//Movie
		public static ITitle create()
		{
			return new Builder();
		}

	}
}
