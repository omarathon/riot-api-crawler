package com.omarathon.riotapicrawler.presets.matchformatters.lib;

import com.merakianalytics.orianna.types.core.match.Team;

import java.io.Serializable;

public class Teams implements Serializable {
    private Team red;
    private Team blue;

    public Teams(Team red, Team blue) {
        this.red = red;
        this.blue = blue;
    }

    public Team getRed() {
        return red;
    }

    public Team getBlue() {
        return blue;
    }
}