package com.martinscastanho.marco.smarteam.helpers;

import java.util.ArrayList;

public class CoefficientsFactory {
    private final ArrayList<ArrayList<Double>> coefficients = new ArrayList<>();
    private final Double KEY_PREF_K;

    public CoefficientsFactory() {
        this.KEY_PREF_K = 1.1;
    }

    private void setCoefficients(Integer numMatches){
        double denominator = 0.0;
        for(int nMatches = 0; nMatches < numMatches; ++nMatches){
            denominator += Math.pow(KEY_PREF_K, nMatches);

            if(nMatches < coefficients.size())
                continue;

            coefficients.add(new ArrayList<Double>());
            for(int iMatch = 0; iMatch <= nMatches; ++iMatch){
                coefficients.get(nMatches).add(Math.pow(KEY_PREF_K, iMatch) / denominator);
            }
        }
    }

    public ArrayList<Double> get(Integer matchNum){
        if(matchNum >= coefficients.size())
            setCoefficients(matchNum + 1);
        return coefficients.get(matchNum);
    }
}
