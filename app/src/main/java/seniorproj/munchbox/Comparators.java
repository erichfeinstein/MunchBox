package seniorproj.munchbox;

import java.util.Comparator;

/**
 * Created by J Wilk on 3/24/2018.
 */

/*
A class containing comparators for JournalEntry sorting purposes
 */

public class Comparators {
    private Comparator<JournalEntry> rateComparator;
    private Comparator<JournalEntry> frequencyComparator;
    private Comparator<JournalEntry> dateComparator;
    private Comparator<JournalEntry> distanceComparator;

    public Comparators(){
        rateComparator = new Comparator<JournalEntry>(){
            public int compare(JournalEntry j1, JournalEntry j2){
                return j1.getRating() - j2.getRating();
            }
        };

        frequencyComparator = new Comparator<JournalEntry>(){
            public int compare(JournalEntry j1, JournalEntry j2){
                return j1.getFrequency() - j2.getFrequency();
            }
        };

        dateComparator = new Comparator<JournalEntry>(){
            public int compare(JournalEntry j1, JournalEntry j2){
                return j1.getEntryDate().compareTo(j2.getEntryDate());
            }
        };

        distanceComparator = new Comparator<JournalEntry>(){
            public int compare(JournalEntry j1, JournalEntry j2){
                //return j1.getDistance() - j2.getDistance();
                return 0; //For now

            }
        };
    }

    public Comparator<JournalEntry> getRateComparator(){
        return rateComparator;
    }

    public Comparator<JournalEntry> getFrequencyComparator(){
        return frequencyComparator;
    }

    public Comparator<JournalEntry> getDateComparator(){
        return dateComparator;
    }
}
