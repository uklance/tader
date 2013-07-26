package org.grater;

import org.grater.model.GraterModel;
import org.grater.model.Schema;

public interface GraterModelSource {
	GraterModel getGraterModel(Schema schema);
}
