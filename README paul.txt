Paul Palumbo

Week 1:
I added all the getter and setter methods for the journal entry. I also created the journal itself to test with.

Week 2:
Tried to get pictures to work and be attached to specific entries. Changed "photo" to "photoID" with the idea that imageView can all these photos when necessary instead of storing them in the entry.
Also made it so the default title for an entry is the first tag, if one exists.
All datapoints in an entry now work. The dummy list has tags, rating, description, etc. The log shows all this data, UI is not connected yet.

Week 3:
Created a basic search method. Now we can search through the list of journal entries to find entries whose keywords match the searched word.
The search is not case-sensitive and works even when the keyword is comprised of multiple words (Will find "Otani Noodle" from searching "otani")
Search method returns all entries with matching keywords in a new list. Also added a method that sorts this search to display entries from descending rating order
I also made it so the dummyList has random ratings to help ensure sorting by rating is successful
Not optimized and not all sorting returns are in place, but with a basic search method we can integrate this feature with the others, specifically UI

Week 4:
Started work on saving journal data between sessions. Methods in place, do not work but also don't crash.
Reloading ListUI is now a separate method. Can be called at any time to reload the ListUI with new parameters.
Results of searches now appear in the List UI. Giving the ListUI a new list of entries(the return value of a search) when it reloads will cause only those entries to be shown
Can now search a specific date to find an entry made on that day. Will add further options and settings to this search later. Getters and setters for dates for each entry.
Reformatted the generated list of entries we use for testing. Now more variety in terms on entries, more organized in case we want to add more. Each entry now has a date.

Week 5:
Deleted listItem. It was way past irrelevant and deleted it removes the possibility of anybody getting confused.
Created sortAlphabetically, and all the addons it demanded
Created sortBy Distance. I'm not sure how the distance with Google Maps is gonna work, so it's basic.

Week 6:
Finished sortByDistance, allowing users to find the entries that are nearest to them. Not fully implemented, since Google Maps must be integrated for it to work as intended
JournalEntries now have random coordinates for testing purposes.
JournalEntries now have TravelTime for testing purposes.

Week 7:
Created sortByFrequency, which shows users what dishes they've had the most often.
Added a "favorite" identifier to JournalEntry, with the appropriate methods.

Week 8:
Created method to filter out entries that are not favorited
Testing and administrative work, such as the poster. The project is approaching it's end and we aren't adding many more features

Week 9:
Added more exempt tags, to ensure unwanted tags are not found
More testing, work on presentation

Week 10:
Testing, work on presentation and final writeup.
If there was more for me to add I would've but we're basically done.