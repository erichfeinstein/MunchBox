package seniorproj.munchbox;

import java.util.Comparator;

/**
 * Created by J Wilk on 3/24/2018.
 */

/*
 * A class containing comparators for JournalEntry sorting purposes
 */

public class Comparators {

    /*Returns comparator based on rating. These are sorted from highest to lowest.
    @return A comparator that sorts based on rating, highest to lowest
     */
    static Comparator<JournalEntry> getRateComparator(){
        return new Comparator<JournalEntry>(){
            public int compare(JournalEntry j1, JournalEntry j2){
                if(j2.getRating() == j1.getRating()){
                    return j1.getDistanceMeters() - j2.getDistanceMeters();
                }
                return j2.getRating() - j1.getRating();
            }
        };
    }

    /*Comparator based on date.
    @return A comparator that sorts based on entry date, most recent first
     */
    static Comparator<JournalEntry> getDateComparator(){
        return new Comparator<JournalEntry>(){
            public int compare(JournalEntry j1, JournalEntry j2){
                return j2.getEntryDate().compareTo(j1.getEntryDate());
            }
        };
    }

    /* Comparator based on distance.
    @return A comparator that sorts based on distance from the user. Closest first
     */
    static Comparator<JournalEntry> getDistanceComparator(){
        return new Comparator<JournalEntry>(){
            public int compare(JournalEntry j1, JournalEntry j2){
                return j1.getDistanceMeters() - j2.getDistanceMeters();
            }
        };
    }

    /* Comparator based on names of entries.
    @return A comparator that sorts based on dish name. Ordered from A to Z
     */
    static Comparator<JournalEntry> getDishNameComparator(){
        return new Comparator<JournalEntry>(){
            public int compare(JournalEntry j1, JournalEntry j2){
                if(j1.getNameOfDish().compareTo(j2.getNameOfDish()) == 0){
                    return j1.getDistanceMeters() - j2.getDistanceMeters();
                }
                else{
                    return j1.getNameOfDish().compareTo(j2.getNameOfDish());
                }
            }
        };
    }
}
