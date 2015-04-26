/*
 * MovieRole.java [JavaBusinessLogic Project] © Copyright 2005 Apple Computer, Inc. All rights reserved. IMPORTANT: This Apple software is supplied to you by Apple Computer, Inc. ("Apple") in consideration of your agreement to the following terms, and your use, installation, modification or
 * redistribution of this Apple software constitutes acceptance of these terms. If you do not agree with these terms, please do not use, install, modify or redistribute this Apple software. In consideration of your agreement to abide by the following terms, and subject to these terms, Apple grants
 * you a personal, non-exclusive license, under Apple's copyrights in this original Apple software (the "Apple Software"), to use, reproduce, modify and redistribute the Apple Software, with or without modifications, in source and/or binary forms; provided that if you redistribute the Apple Software
 * in its entirety and without modifications, you must retain this notice and the following text and disclaimers in all such redistributions of the Apple Software. Neither the name, trademarks, service marks or logos of Apple Computer, Inc. may be used to endorse or promote products derived from the
 * Apple Software without specific prior written permission from Apple. Except as expressly stated in this notice, no other rights or licenses, express or implied, are granted by Apple herein, including but not limited to any patent rights that may be infringed by your derivative works or by other
 * works in which the Apple Software may be incorporated. The Apple Software is provided by Apple on an "AS IS" basis. APPLE MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE,
 * REGARDING THE APPLE SOFTWARE OR ITS USE AND OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS. IN NO EVENT SHALL APPLE BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION, MODIFICATION AND/OR DISTRIBUTION OF THE APPLE SOFTWARE, HOWEVER CAUSED AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE), STRICT LIABILITY OR OTHERWISE, EVEN IF APPLE HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package webobjectsexamples.businesslogic.movies.common;

import com.webobjects.eocontrol.EOEditingContext;

public class MovieRole extends _MovieRole
{
	private static final long	serialVersionUID	= -6163887530246217226L;

	public static final String	MovieKey			= "movie";

	public static final String	RoleNameKey			= "roleName";

	public static final String	TalentKey			= "talent";

	public MovieRole()
	{
		super();
	}

	@Override
	public void awakeFromInsertion(EOEditingContext editingContext)
	{
		super.awakeFromInsertion(editingContext);
		if (roleName() == null)
		{
			setRoleName("New Role");
		}
	}

	@Override
	public String roleName()
	{
		return (String)(storedValueForKey(RoleNameKey));
	}

	@Override
	public void setRoleName(String value)
	{
		takeStoredValueForKey(value, RoleNameKey);
	}

	// mandatory to one relationship interfaces
	public interface IMovie
	{
		public ITalent movie(webobjectsexamples.businesslogic.movies.common.Movie movie);

		public ITalent movie(webobjectsexamples.businesslogic.movies.common.Movie.Builder movieBuilder);
	}

	// only mandatory to-one-relationship
	public interface ITalent
	{
		public Builder talent(webobjectsexamples.businesslogic.movies.common.Talent talent);

		public Builder talent(webobjectsexamples.businesslogic.movies.common.Talent.Builder talentBuilder);
	}

	//mandatory to many relationship interface

	public static class Builder
			implements
			IMovie
			,
			ITalent
	{

		EOEditingContext														editingContext;

		// attributes
		private String															roleName;

		// To one relationships
		private webobjectsexamples.businesslogic.movies.common.Movie			movie;
		private webobjectsexamples.businesslogic.movies.common.Movie.Builder	movieBuilder;
		private webobjectsexamples.businesslogic.movies.common.Talent			talent;
		private webobjectsexamples.businesslogic.movies.common.Talent.Builder	talentBuilder;

		// to many retlationships

		public MovieRole build(final EOEditingContext editingContext)
		{
			this.editingContext = editingContext;

			// build to-one-relationships
			if (movie == null)
			{
				if (movieBuilder != null)
				{
					movie = movieBuilder.build(editingContext);
				}
			}
			else
			{
				movie.localInstanceIn(editingContext);
			}
			if (talent == null)
			{
				if (talentBuilder != null)
				{
					talent = talentBuilder.build(editingContext);
				}
			}
			else
			{
				talent.localInstanceIn(editingContext);
			}

			MovieRole result = MovieRole.createMovieRole(editingContext,
					movie,
					talent
					);

			// set non mandatory to-one-relationships
			// set non mandatory attributes
			if (roleName != null)
			{
				result.setRoleName(roleName);
			}

			//build and add to-many-relationships

			return result;
		}

		public MovieRole buildAndSave(final EOEditingContext editingContext)
		{
			MovieRole result = build(editingContext);
			editingContext.saveChanges();
			return result;
		}

		// mandatory attribute methods

		// mandatory to one relationship method implementations

		public ITalent movie(webobjectsexamples.businesslogic.movies.common.Movie movie)
		{
			this.movie = movie;
			return this;
		}

		public ITalent movie(webobjectsexamples.businesslogic.movies.common.Movie.Builder movieBuilder)
		{
			this.movieBuilder = movieBuilder;
			return this;
		}

		// last mandatory relationship
		public Builder talent(webobjectsexamples.businesslogic.movies.common.Talent talent)
		{
			this.talent = talent;
			return this;
		}

		public Builder talent(webobjectsexamples.businesslogic.movies.common.Talent.Builder talentBuilder)
		{
			this.talentBuilder = talentBuilder;
			return this;
		}

		//mandatory to many relationship method implementations
		// non mandatory attribute methods

		public Builder roleName(String roleName)
		{
			this.roleName = roleName;
			return this;
		}

		// non mandatory to one relationship methods

		// non mandatory to many relationship methods

		public static IMovie create()
		{
			return new Builder();
		}

	}

}
