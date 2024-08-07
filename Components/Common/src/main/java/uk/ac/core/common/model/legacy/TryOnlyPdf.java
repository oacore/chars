/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.common.model.legacy;

/* Harvest Levels *****************************************************************************/
public enum TryOnlyPdf {

    LINK_ENDS_WITH_PDF(1),
    TRY_ALL_LINKS(0),
    LINK_CONTAIN_PDF(2);
    private final Integer level;

    TryOnlyPdf(Integer level) {
        this.level = level;
    }

    public int getValue() {
        return level;
    }

    /**
     * Get HarvestLevel value from numeric
     *
     * @param val double
     * @return HarvestLevel
     * @throws RuntimeException
     */
    public static TryOnlyPdf fromInt(Integer val) throws RuntimeException {

        for (TryOnlyPdf level : TryOnlyPdf.values()) {
            if (level.getValue() == val) {
                return level;
            }
        }
        throw new RuntimeException("Unknown value of harvest level:" + val);
    }
}
