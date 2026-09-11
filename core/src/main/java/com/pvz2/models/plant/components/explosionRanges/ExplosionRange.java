package com.pvz2.models.plant.components.explosionRanges;

import com.pvz2.models.plant.Plant;
import com.pvz2.models.world.Cell;

import java.util.List;

public interface ExplosionRange {
    List<Cell> getCells(Plant owner);
}
