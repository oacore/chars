/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.common.model.legacy;

/* Harvest Levels *****************************************************************************/
public enum HarvestLevel {

    LEVEL_0(0),
    LEVEL_1(1),
    LEVEL_2(2);
    private final Integer level;

    HarvestLevel(Integer level) {
        this.level = level;
    }

    public double getValue() {
        return level;
    }

    public int getLevel() {
        return (int) Math.round(Math.floor(level));
    }
    
    public boolean isAboveHarvestLevel(Integer levelCounter){
        return level<levelCounter;
    }

    /**
     * Get HarvestLevel value from numeric
     *
     * @param val double
     * @return HarvestLevel
     * @throws RuntimeException
     */
    public static HarvestLevel fromInt(Integer val) throws RuntimeException {

        for (HarvestLevel level : HarvestLevel.values()) {
            if (level.getValue() == val) {
                return level;
            }
        }
        throw new RuntimeException("Unknown value of harvest level:" + val);
    }
}
