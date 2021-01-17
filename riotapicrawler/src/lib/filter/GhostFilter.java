package com.omarathon.riotapicrawler.src.lib.filter;

import com.merakianalytics.orianna.types.core.GhostObject;

public abstract class GhostFilter<F extends GhostObject> extends MemorisingFilter<F>{
    @Override
    public boolean apply(F o) {
        if (!o.exists()) {
            return false;
        }
        return super.apply(o);
    }
}
