package model;

import java.util.Collection;

abstract class Cluster implements Simulable {

    abstract Collection<Simulable> getSimulables();

}
