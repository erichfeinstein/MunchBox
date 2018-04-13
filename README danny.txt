Danny Miles

Week of 4/8

4/12: Performed testing on overall app function. Experienced and cataloged bug occurring on multiple entries being edited in one session, but so far is unreproduceable on team members'
devices. Implemented skeleton of share funcationality; performed code cleanup. 

4/10: Implemented cancel functionality on the EditEntry screen.

Week of 4/1

4/4: Worked with Eric to finalize Vision implementation. Basic functionality achieved; now working on optimization and tag filtering.

4/3: Continued work on Vision implementation. Labels are finally being succesfully retrieved; now working on passing information back from asynchrous thread that retrieves it.

4/1: Attended group meeting to discuss progress on project. Identified source of errors regarding Vision implementation, began taking steps to repair it. 

Week of 3/25

3/30: Continued work on Vision API. Implemented Vision
using a different (but still Google) library, but still having issues with requests being made to Google servers, as well as with the debugger not firing on breakpoints. Will work with the group in order to solve these
issues over the coming days.

3/29: Continued modifying Google Vision code. Switched to using client services for API contact, but am currently running into issues; however, have modified framework in such a way that
further work will be simpler to change and fix. Also made slight fixes to the permissions system.

3/27: Began refactoring code to accomodate new design for journal entry addition. Began modification of Google Vision code; currently having issues with HTTP requests.

Week of 3/18

3/23: Began implementation of Google Vision functionality. Code is in place and theoretically functional, but will require significant testing and tweaks
for production quality.

3/22: Rehauled + simplified permissions system. Fixed bug where dialog window would pop up regardless of whether user agreed to permissions. App no longer
allows users to proceed without accepting all permissions.

Week of 3/4

3/6: Worked on permissions implementation. Application will no longer run if user refuses permissions. Not functioning quite as intended, but the
framework is in place.

3/4: Met with Eric and designed custom camera funcationality upon realization that Android default camera would not satisfy needs.

Week of 2/25

3/1: Implemented camera functionality. User can now take picture and the picture and its thumbnail will be associated with the journal entry.
Pending UI implementation, the journal entries should work as intended now.

2/27: Set up image framework and thumbnail generation using Android's camera. Updated manifest with various permissions.

2/25: Generated API key for team use in utilizing relevant Google APIs. Read about implementation of Google Vision, and updated the manifeset.



Week of 2/18

2/22: JournalEntry.java:
	Created skeleton of JournalEntry class, including fields and base methods, as per the design document


2/20: Refreshed myself on basics of Android Studio development, researched the utilization of Android system features
for app use. Set up Android Studio development environment, coordinated version control protocols among team members.