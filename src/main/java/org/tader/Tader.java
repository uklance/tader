package org.tader;

import java.util.Collection;
import java.util.List;

public interface Tader {
	/**
	 * Inserts the partial entity into the persistent store via {@link EntityPersistence}. Any required values will be
	 * generated via the {@link AutoGenerateSource} 
	 *
	 * @see EntityPersistence
	 * @see AutoGenerateSource
	 * @see AutoGenerateStrategy
	 * @see AutoGenerateSourceContribution
	 * 
	 * @param entity A partial entity to be inserted into the persistent store
	 * @return A persisted entity
	 */
	Entity insert(PartialEntity entity);
	
	/**
	 * Bulk insert operation to use a single template to insert multiple records
	 * 
	 * @see EntityPersistence
	 * @see AutoGenerateSource
	 * @see AutoGenerateStrategy
	 * @see AutoGenerateSourceContribution
	 * 
	 * @param entity A partial entity template to be inserted into the persistent store
	 * @param count Number of insert operations to perform
	 * @return The persisted entities
	 */
	List<Entity> insert(PartialEntity entity, int count);
	
	/**
	 * Deletes an entity from the persistent store via {@link EntityPersistence}
	 * 
	 * @param entity
	 */
	void delete(Entity entity);

	/**
	 * Bulk delete operation to delete entities from the persistent store via {@link EntityPersistence}
	 * 
	 * @param entity
	 */
	void delete(Collection<Entity> entities);
}
