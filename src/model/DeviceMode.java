package model;

public enum DeviceMode {

    /**
     * ODMAC++ with batteries that are sufficiently large.
     * Basically ODMACC++ with PG-SLB (see paper)
     */
    ODMACPP_SLB("PE-SLB"),

    /**
     * ODMAC++ supporting generic battery size, computes frequency
     * by simulating over the next energy cycle (ODMAC++ PG-GB)
     */
    ODMACPP_GB("PE-GB"),

    CONSTANT_FREQUENCY("Constant"),

    PROPORTIONAL_FREQUENCY("Proportional");

    public final String name;

    DeviceMode(String name) {
        this.name = name;
    }

    public boolean isODMACPP() {
        return this == ODMACPP_SLB || this == ODMACPP_GB;
    }
}
