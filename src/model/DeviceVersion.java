package model;

public enum DeviceVersion {

    /**
     * ODMAC++ with batteries that are sufficiently large.
     * Basically ODMACC++ with PG-SLB (see paper)
     */
    ODMACPP_V1,

    /**
     * ODMAC++ supporting generic battery size, computes frequency
     * by simulating over the next energy cycle (ODMAC++ PG-GB)
     */
    ODMACPP_V2;
}
