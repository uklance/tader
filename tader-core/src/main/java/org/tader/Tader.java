package org.tader;

import java.util.List;

public interface Tader {
	Entity insert(PartialEntity entity);
	List<Entity> insert(PartialEntity entity, int count);
	void delete(Entity entity);
}
