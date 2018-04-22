package seniorproj.munchbox;

import java.util.Comparator;

/**
 * Created by J Wilk on 3/24/2018.
 */

/*
 * A class containing comparators for JournalEntry sorting purposes
 */

public class Comparators {
    static Comparator<JournalEntry> getRateComparator(){ //Highest to lowest
        return new Comparator<JournalEntry>(){
            public int compare(JournalEntry j1, JournalEntry j2){
                return j2.getRating() - j1.getRating();
            }
        };
    }

    static Comparator<JournalEntry> getDateComparator(){ //Most recent first
        return new Comparator<JournalEntry>(){
            public int compare(JournalEntry j1, JournalEntry j2){
                return j2.getEntryDate().compareTo(j1.getEntryDate());
            }
        };
    }

    static Comparator<JournalEntry> getDistanceComparator(){ //Closest to furthest
        return new Comparator<JournalEntry>(){
            public int compare(JournalEntry j1, JournalEntry j2){
                //return j1.getDistance() - j2.getDistance();
                return 0; //For now

            }
        };
    }

    static Comparator<JournalEntry> getDishNameComparator(){ //A to Z
        return new Comparator<JournalEntry>(){
            public int compare(JournalEntry j1, JournalEntry j2){
                return j1.getNameOfDish().compareTo(j2.getNameOfDish());
            }
        };
    }
}
