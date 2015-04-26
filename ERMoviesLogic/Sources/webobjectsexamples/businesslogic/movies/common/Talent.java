/*
 * Talent.java [JavaBusinessLogic Project] © Copyright 2005 Apple Computer, Inc. All rights reserved. IMPORTANT: This Apple software is supplied to you by Apple Computer, Inc. ("Apple") in consideration of your agreement to the following terms, and your use, installation, modification or
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
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;

public class Talent extends _Talent
{
	private static final long	serialVersionUID	= -5942077710497738108L;

	public static final String	FirstNameKey		= "firstName";

	public static final String	LastNameKey			= "lastName";

	public static final String	MoviesDirectedKey	= "moviesDirected";

	public static final String	PhotoKey			= "photo";

	public static final String	RolesKey			= "roles";

	public Talent()
	{
		super();
	}

	@Override
	public String firstName()
	{
		return (String)(storedValueForKey(FirstNameKey));
	}

	@Override
	public String lastName()
	{
		return (String)(storedValueForKey(LastNameKey));
	}

	@Override
	public NSArray roles()
	{
		return (NSArray)(storedValueForKey(RolesKey));
	}

	public String fullName()
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append(firstName());
		buffer.append(' ');
		buffer.append(lastName());
		return buffer.toString();
	}

	public NSArray moviesStarredIn()
	{
		NSMutableArray<Object> moviesStarredIn = new NSMutableArray<Object>();
		NSArray movies = (NSArray)(roles().valueForKey("movie"));
		if (movies != null)
		{
			int count = movies.count();
			for (int i = 0; i < count; i++)
			{
				Object movie = movies.objectAtIndex(i);
				if ((movie != NSKeyValueCoding.NullValue) && (!(moviesStarredIn.containsObject(movie))))
				{
					moviesStarredIn.addObject(movie);
				}
			}
		}
		return moviesStarredIn;
	}

	// used by ERDivaLookExample
	@SuppressWarnings("unchecked")
	public boolean isDirector()
	{
		NSArray moviesDirected = (NSArray)valueForKey("moviesDirected");
		return (moviesDirected != null && moviesDirected.count() > 0);
	}

	public interface IFirstName
	{
		public ILastName firstName(String firstName);
	}

	// first and only mandatory attribute
	public interface ILastName
	{
		public IPhoto lastName(String lastName);
	}

	// mandatory to one relationship interfaces
	// only mandatory to-one-relationship
	public interface IPhoto
	{
		public Builder photo(webobjectsexamples.businesslogic.movies.common.TalentPhoto photo);

	}

	//mandatory to many relationship interface

	public static class Builder
			implements
			IFirstName,
			ILastName,
			IPhoto
	{

		EOEditingContext																editingContext;

		// attributes
		private String																	firstName;
		private String																	lastName;

		// To one relationships
		private webobjectsexamples.businesslogic.movies.common.TalentPhoto				photo;

		// to many retlationships
		private NSArray<webobjectsexamples.businesslogic.movies.common.Movie>			moviesDirected;
		private NSArray<webobjectsexamples.businesslogic.movies.common.Movie.Builder>	moviesDirectedBuilders;
		private NSArray<webobjectsexamples.businesslogic.movies.common.MovieRole>		roles;

		public Talent build(final EOEditingContext editingContext)
		{
			this.editingContext = editingContext;

			// build to-one-relationships
			if (photo == null)
			{

			}
			else
			{
				photo.localInstanceIn(editingContext);
			}

			Talent result = Talent.createTalent(editingContext,
					firstName,
					lastName
					);

			// set non mandatory to-one-relationships
			// set non mandatory attributes

			//build and add to-many-relationships
			if (moviesDirected == null)
			{
				if (moviesDirectedBuilders != null && !moviesDirectedBuilders.isEmpty())
				{
					NSMutableArray<webobjectsexamples.businesslogic.movies.common.Movie> builtRelationships = new NSMutableArray<webobjectsexamples.businesslogic.movies.common.Movie>();
					for (webobjectsexamples.businesslogic.movies.common.Movie.Builder builder : moviesDirectedBuilders)
					{
						result.addToMoviesDirectedRelationship(builder.build(editingContext));
					}
				}
			}
			else
			{
				for (webobjectsexamples.businesslogic.movies.common.Movie entity : moviesDirected)
				{
					result.addToMoviesDirectedRelationship(entity.localInstanceIn(editingContext));
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

		public Talent buildAndSave(final EOEditingContext editingContext)
		{
			Talent result = build(editingContext);
			editingContext.saveChanges();
			return result;
		}

		// mandatory attribute methods

		public ILastName firstName(String firstName)
		{
			this.firstName = firstName;
			return this;
		}

		// no mandatoryToOneRelationship return Builder
		public IPhoto lastName(String lastName)
		{
			this.lastName = lastName;
			return this;
		}

		// mandatory to one relationship method implementations
		// last mandatory relationship
		public Builder photo(webobjectsexamples.businesslogic.movies.common.TalentPhoto photo)
		{
			this.photo = photo;
			return this;
		}

		public Builder moviesDirectedFromEOArray(NSArray<webobjectsexamples.businesslogic.movies.common.Movie> moviesDirected)
		{
			this.moviesDirected = moviesDirected;
			return this;
		}

		public Builder moviesDirectedFromBuilderArray(NSArray<webobjectsexamples.businesslogic.movies.common.Movie.Builder> moviesDirectedBuilders)
		{
			this.moviesDirectedBuilders = moviesDirectedBuilders;
			return this;
		}

		public Builder rolesFromEOArray(NSArray<webobjectsexamples.businesslogic.movies.common.MovieRole> roles)
		{
			this.roles = roles;
			return this;
		}

		//Talent
		public static IFirstName create()
		{
			return new Builder();
		}

	}

}
