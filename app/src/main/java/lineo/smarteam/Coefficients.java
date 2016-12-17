package lineo.smarteam;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by marco on 17/12/2016.
 * Coefficients for Score calculation
 */

public class Coefficients {
    private static final String TAG = "Coefficients";
    private Double weight;
    private ArrayList<ArrayList<Double>> coefficients = new ArrayList<>();

    public Coefficients(Double weight) {
        this.weight = weight;
    }

    private void set(Integer numMatches){
        Log.d(TAG, "set() coefficients for matchday "+numMatches);
        Double power = 0.0;
        for(int i=0; i<numMatches; ++i){
            if(i==0){
                power=1.0;
            }
            else{
                power += Math.pow(weight, i);
            }

            if(i<coefficients.size())
                continue;

            coefficients.add(new ArrayList<Double>());

            for(int j=0; j<=i; ++j){
                coefficients.get(i).add(Math.pow(weight, j) / power);
            }
        }
    }

    public ArrayList<Double> get(Integer matchId){
        if(matchId >= coefficients.size())
            set(matchId+1);
        return coefficients.get(matchId);
    }
}
