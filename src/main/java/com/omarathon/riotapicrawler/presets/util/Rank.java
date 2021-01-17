/*
    A class that represents ranks in League of Legends,
    which consist of a tier (e.g PLATINUM) and a division (e.g III)

    Author: Omar Tanner (omarathon)
    Copyright © 2019 omarathon
 */

package com.omarathon.riotapicrawler.presets.util;

import com.merakianalytics.orianna.types.common.Division;
import com.merakianalytics.orianna.types.common.Tier;

public class Rank {
    // store tier and division
    private Tier tier;
    private Division division;

    // Constructor sets the division and tier
    public Rank(Tier tier, Division division) {
        this.tier = tier;
        this.division = division;
    }

    // getter for tier
    public Tier getTier() {
        return tier;
    }

    // getter for division
    public Division getDivision() {
        return division;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rank)) return false;

        Rank r = (Rank) o;
        return (r.getTier().equals(tier) && r.getDivision().equals(division));
    }

    @Override
    public int hashCode() {
        return tier.ordinal() * Division.values().length + division.ordinal();
    }

    public String toString() {
        return tier.toString() + " " + division.toString();
    }
}
