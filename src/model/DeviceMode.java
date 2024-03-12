package model;

public enum DeviceMode {

    /**
     * ODMAC++ with batteries that are sufficiently large.
     * Basically ODMACC++ with PG-SLB (see paper)
     */
    ODMACPP_SLB,

    /**
     * ODMAC++ supporting generic battery size, computes frequency
     * by simulating over the next energy cycle (ODMAC++ PG-GB)
     */
    ODMACPP_GB,

    CONSTANT_FREQUENCY,

    PROPORTIONAL_FREQUENCY;

    public boolean isODMACPP() {
        return this == ODMACPP_SLB || this == ODMACPP_GB;
    }
}
