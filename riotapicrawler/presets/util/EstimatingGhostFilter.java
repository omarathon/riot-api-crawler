package com.omarathon.riotapicrawler.presets.util;

import com.merakianalytics.orianna.types.core.GhostObject;
import com.omarathon.riotapicrawler.presets.util.estimators.lib.MemorisingEstimator;
import com.omarathon.riotapicrawler.src.lib.filter.GhostFilter;

import java.util.Set;

public class EstimatingGhostFilter<T extends GhostObject, E> extends GhostFilter<T> {
    private Set<E> allowedEstimates;
    private MemorisingEstimator<T, E> estimator;

    private EstimatingGhostFilter() { }

    public EstimatingGhostFilter(Set<E> allowedEstimates, MemorisingEstimator<T, E> estimator) {
        this.allowedEstimates = allowedEstimates;
        this.estimator = estimator;
    }

    public boolean filter(T o) {
        E estimate = estimator.getEstimate(o);

        if (estimate == null) {
            return false;
        }

        return allowedEstimates.contains(estimate);
    }
}
